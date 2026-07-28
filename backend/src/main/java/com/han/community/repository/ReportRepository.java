package com.han.community.repository;

import com.han.community.dto.report.ReportSummaryView;
import com.han.community.entity.Report;
import com.han.community.entity.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query(value = """
SELECT
    summary.report_count AS reportCount,
    summary.target_type AS targetType,
    summary.target_id AS targetId,
    COALESCE(p.content, c.content) AS targetPreview,
    summary.reported_user_id AS reportedUserId,
    u.username AS reportedUsername,
    summary.main_reason AS mainReason,
    summary.last_reported_at AS lastReportedAt,
    summary.status AS status
FROM
    (SELECT
        COUNT(*) AS report_count,
        r.target_type,
        r.target_id,
        r.reported_user_id,
        r.status,
        MAX(r.created_at) AS last_reported_at,
        MODE() within GROUP (ORDER BY reason) AS main_reason
    FROM report r
    GROUP BY r.target_type, r.target_id, r.reported_user_id, r.status) AS summary
INNER JOIN users u ON u.id = summary.reported_user_id
LEFT JOIN post p ON summary.target_type = 'POST' AND p.id = summary.target_id
LEFT JOIN comment c ON summary.target_type = 'COMMENT' AND c.id = summary.target_id
WHERE summary.status = :status
""",
countQuery = """
SELECT COUNT(*)
FROM ( SELECT r.status
       FROM report r
       GROUP BY r.target_type, r.target_id, r.reported_user_id, r.status
     ) AS summary
WHERE summary.status = :status
""", nativeQuery = true)
    Page<ReportSummaryView> findReportPageImpl(@Param("status")String status, Pageable pageable);

    @Query(value = """
SELECT
    summary.report_count AS reportCount,
    summary.target_type AS targetType,
    summary.target_id AS targetId,
    COALESCE(p.content, c.content) AS targetPreview,
    summary.reported_user_id AS reportedUserId,
    u.username AS reportedUsername,
    summary.main_reason AS mainReason,
    summary.last_reported_at AS lastReportedAt,
    summary.status AS status
FROM
    (SELECT
        COUNT(*) AS report_count,
        r.target_type,
        r.target_id,
        r.reported_user_id,
        r.status,
        MAX(r.created_at) AS last_reported_at,
        MODE() within GROUP (ORDER BY reason) AS main_reason
    FROM report r
    GROUP BY r.target_type, r.target_id, r.reported_user_id, r.status) AS summary
INNER JOIN users u ON u.id = summary.reported_user_id
LEFT JOIN post p ON summary.target_type = 'POST' AND p.id = summary.target_id
LEFT JOIN comment c ON summary.target_type = 'COMMENT' AND c.id = summary.target_id
""",
countQuery = """
SELECT COUNT(*)
FROM ( SELECT r.status
       FROM report r
       GROUP BY r.target_type, r.target_id, r.reported_user_id, r.status
     ) AS summary
""", nativeQuery = true)
    Page<ReportSummaryView> findReportPageImpl(Pageable pageable);
}
