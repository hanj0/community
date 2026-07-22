package com.han.community.service;

import com.han.community.common.IntegrationTestSupport;
import com.han.community.dto.NotificationDto;
import com.han.community.dto.common.CursorResponse;
import com.han.community.entity.Notification;
import com.han.community.entity.NotificationType;
import com.han.community.entity.TargetType;
import com.han.community.event.NotificationEvent;
import com.han.community.repository.NotificationActorRepository;
import com.han.community.repository.NotificationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class NotificationServiceTest extends IntegrationTestSupport {

    @Autowired NotificationService notificationService;
    @Autowired NotificationRepository notificationRepository;
    @Autowired NotificationActorRepository actorRepository;
    @Autowired JdbcTemplate jdbcTemplate;

    static final Long RECIPIENT = 1L;
    static final Long ACTOR_A = 2L;
    static final Long ACTOR_B = 3L;
    static final Long POST_ID = 100L;
    static final String CONTENT_PREVIEW = "preview";

    @AfterEach
    void tearDown() {
        actorRepository.deleteAllInBatch();
        notificationRepository.deleteAllInBatch();
    }

    @Nested
    class Upsert {

        @Test
        void 활성_그룹이_없으면_새_알림을_생성한다() {

            notificationService.upsert(eventOf(ACTOR_A));

            List<Notification> all = notificationRepository.findAll();
            assertThat(all).hasSize(1);

            Notification n = all.getFirst();
            assertThat(n.getRecipientId()).isEqualTo(RECIPIENT);
            assertThat(n.getActorCount()).isEqualTo(1);
            assertThat(n.getLastActorId()).isEqualTo(ACTOR_A);
            assertThat(n.isRead()).isFalse();
            assertThat(actorRepository.findAll()).hasSize(1);
        }

        @Test
        void 활성_그룹이_있으면_actor를_추가하고_카운트를_증가시킨다() {

            notificationService.upsert(eventOf(ACTOR_A));
            notificationService.upsert(eventOf(ACTOR_B));

            List<Notification> all = notificationRepository.findAll();
            assertThat(all).hasSize(1);

            Notification n = all.getFirst();
            assertThat(n.getActorCount()).isEqualTo(2);
            assertThat(n.getLastActorId()).isEqualTo(ACTOR_B);
            assertThat(actorRepository.findAll()).hasSize(2);
        }

        @Test
        void 같은_actor가_다시_반응해도_카운트가_증가하지_않는다() {

            notificationService.upsert(eventOf(ACTOR_A));
            notificationService.upsert(eventOf(ACTOR_A));

            Notification n = notificationRepository.findAll().getFirst();
            assertThat(n.getActorCount()).isEqualTo(1);
            assertThat(actorRepository.findAll()).hasSize(1);
        }

        @Test
        void 읽은_그룹에는_합류하지_않고_새_알림을_생성한다() {

            notificationService.upsert(eventOf(ACTOR_A));
            notificationService.markAllRead(RECIPIENT);

            notificationService.upsert(eventOf(ACTOR_B));

            List<Notification> all = notificationRepository.findAll();
            assertThat(all).hasSize(2);

            Notification read = findBy(all, true);
            Notification unread = findBy(all, false);
            assertThat(read.getActorCount()).isEqualTo(1);
            assertThat(read.getLastActorId()).isEqualTo(ACTOR_A);
            assertThat(unread.getActorCount()).isEqualTo(1);
            assertThat(unread.getLastActorId()).isEqualTo(ACTOR_B);
        }

        @Test
        void 다른_게시글의_반응은_별도_알림으로_생성된다() {

            notificationService.upsert(eventOf(ACTOR_A, POST_ID));
            notificationService.upsert(eventOf(ACTOR_A, 101L));


            assertThat(notificationRepository.findAll()).hasSize(2);
        }
    }

    @Nested
    class GetList {

        @Test
        void updatedAt이_같아도_중복_누락이_없다() {

            for(long i = 101L; i <= 150L; i++) {
                notificationService.upsert(eventOf(i, i));
            }

            List<Notification> all = notificationRepository.findAll(Sort.by("id"));
            LocalDateTime base = LocalDateTime.now();
            for(int i = 0; i < 50; i++) {
                if(i % 10 == 0) base = base.minusHours(1);
                jdbcTemplate.update("UPDATE Notification SET updated_at = ? WHERE id = ?", base, all.get(i).getId());
            }

            assertThat(notificationRepository.findAll()).hasSize(50);

            List<Long> notificationIds = new ArrayList<>();
            String cursor = null;
            do {
                CursorResponse<NotificationDto.Response> page =
                        notificationService.getNotifications(RECIPIENT, cursor, 6);
                page.data().forEach(n -> {notificationIds.add(n.id()); System.out.println(n.id() + " 알림 갱신시간 : " + n.updatedAt());});
                cursor = page.nextCursor();
            } while(cursor != null);

            assertThat(notificationIds).hasSize(50);
            assertThat(notificationIds).doesNotHaveDuplicates();
        }

        @Test
        void 조회_도중에_새_알림이_생겨도_중복_누락이_없다() {

            for(long i = 101L; i <= 150L; i++) {
                notificationService.upsert(eventOf(i, i));
            }
            assertThat(notificationRepository.findAll()).hasSize(50);

            List<Long> ids = new ArrayList<>();
            String cursor = null;
            long newId = 151L;
            do {
                CursorResponse<NotificationDto.Response> page =
                        notificationService.getNotifications(RECIPIENT, cursor, 10);
                page.data().forEach(n -> ids.add(n.id()));
                cursor = page.nextCursor();

                notificationService.upsert(eventOf(newId, newId++));
            } while(cursor != null);

            assertThat(ids).hasSize(50);
            assertThat(ids).doesNotHaveDuplicates();

        }
    }


    private NotificationEvent eventOf(Long actorId) {
        return eventOf(actorId, POST_ID);
    }

    private NotificationEvent eventOf(Long actorId, Long postId) {
        return eventOf(actorId, postId, CONTENT_PREVIEW);
    }

    private NotificationEvent eventOf(Long actorId, Long postId, String preview) {
        return new NotificationEvent(actorId, TargetType.POST, postId, postId, RECIPIENT, NotificationType.REACTION ,preview);
    }

    private Notification findBy(List<Notification> all, boolean isRead) {
        return all.stream().filter(n -> n.isRead() == isRead).findFirst().orElseThrow();
    }

}
