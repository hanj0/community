package com.han.community.debug;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Profile("local")
@RestController
@RequestMapping("/debug/session")
@RequiredArgsConstructor
public class SessionDebugController {

    private static final String PREFIX = "spring:session:sessions:";

    private final StringRedisTemplate redisTemplate;
    private final SessionRepository<? extends Session> sessionRepository;

    @GetMapping
    public List<SessionInfo> getList() {

        List<SessionInfo> response = new ArrayList<>();

        ScanOptions options = ScanOptions.scanOptions()
                .match(PREFIX + "*")
                .count(100)
                .build();

        try(Cursor<String> cursor = redisTemplate.scan(options)) {
            while(cursor.hasNext()) {

                String key = cursor.next();
                String id = key.substring(PREFIX.length());
                if(id.startsWith("expires:")) continue;

                Session session = sessionRepository.findById(id);
                if(session == null) continue;

                Long ttl = redisTemplate.getExpire(key);
                response.add(SessionInfo.from(session, ttl));
            }
        }

        response.sort(Comparator.comparing((SessionInfo info) -> info.lastAccessedTime()).reversed());
        return response;
    }

    @GetMapping(value = "/table", produces = "text/plain; charset=UTF-8")
    public String table() {
        DateTimeFormatter time = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.of("Asia/Seoul"));
        String fmt = "%-10s %-12s %-19s  %-19s %8s  %s%n";

        StringBuilder sb = new StringBuilder();
        sb.append(String.format(fmt, "ID", "USER", "CREATED", "LAST ACCESS", "TTL", "ATTRIBUTES"));
        sb.append("-".repeat(100)).append('\n');

        for (SessionInfo s : getList()) {
            String attrs = s.attributeNames().stream()
                    .map(name -> name.substring(name.lastIndexOf('.') + 1)) // 긴 클래스 경로 제거
                    .collect(Collectors.joining(", "));

            sb.append(String.format(fmt,
                    s.id().substring(0, 8),
                    s.username() != null ? s.username() : "-",
                    time.format(s.creationTime()),
                    time.format(s.lastAccessedTime()),
                    s.ttlSeconds() + "s",
                    attrs));
        }
        return sb.toString();
    }
}
