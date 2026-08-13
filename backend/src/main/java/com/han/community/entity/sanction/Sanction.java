package com.han.community.entity.sanction;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sanction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    @Enumerated(value = EnumType.STRING)
    private SanctionType type;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    @Enumerated(value = EnumType.STRING)
    private SanctionReason reason;
    private String reasonDetail;
    @Enumerated(value = EnumType.STRING)
    private SanctionSource sourceType;
    private Long sourceId;
    private Long createdBy;
    private LocalDateTime revokedAt;
    private Long revokedBy;
    private String revokedReason;

    public static Sanction create(Long userId,
                                  SanctionType sanctionType,
                                  SanctionReason reason,
                                  String reasonDetail,
                                  SanctionSource sourceType,
                                  Long sourceId,
                                  Long createdBy) {
        Sanction s = new Sanction();
        s.userId = userId;
        s.type = sanctionType;
        s.reason = reason;
        s.reasonDetail = reasonDetail;
        s.sourceType = sourceType;
        s.sourceId = sourceId;
        s.createdBy = createdBy;
        return s;
    }
}
