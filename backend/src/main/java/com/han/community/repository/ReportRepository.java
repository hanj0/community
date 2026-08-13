package com.han.community.repository;

import com.han.community.dto.report.*;
import com.han.community.entity.report.Report;
import com.han.community.entity.report.ReportResolution;
import com.han.community.entity.report.ReportStatus;
import com.han.community.entity.report.ReportTargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query(value = """
SELECT
    s.report_count AS reportCount,
    s.target_type AS targetType,
    s.target_id AS targetId,
    COALESCE(p.content, c.content) AS targetPreview,
    s.reported_user_id AS reportedUserId,
    u.username AS reportedUsername,
    s.main_reason AS mainReason,
    s.last_reported_at AS lastReportedAt,
    s.status AS status,
    s.handled_at AS handledAt
FROM
    (SELECT
        COUNT(*) AS report_count,
        r.target_type,
        r.target_id,
        MIN(r.reported_user_id) AS reported_user_id,
        CASE WHEN r.handled_at IS NULL THEN 'PENDING' ELSE 'RESOLVED' END AS status,
        MAX(r.created_at) AS last_reported_at,
        MODE() within GROUP (ORDER BY reason) AS main_reason,
        r.handled_at
    FROM Report r
    WHERE (r.handled_at IS NOT NULL) = (:status = 'RESOLVED')
    GROUP BY r.target_type, r.target_id, r.handled_at) AS s
LEFT JOIN Users u ON u.id = s.reported_user_id
LEFT JOIN Post p ON s.target_type = 'POST' AND p.id = s.target_id
LEFT JOIN Comment c ON s.target_type = 'COMMENT' AND c.id = s.target_id
ORDER BY handledAt DESC, reportCount DESC, targetId DESC
""", countQuery = """
SELECT COUNT(*)
FROM (
    SELECT 1
    FROM report r
    WHERE (r.handled_at IS NOT NULL) = (:status = 'RESOLVED')
    GROUP BY r.target_type, r.target_id, r.handled_at
) AS s
""", nativeQuery = true)
    Page<ReportSummaryResponseProjection> findReportPageInternal(@Param("status")String status, Pageable pageable);

    @Query(value = """
SELECT
    COALESCE(p.updated_at, c.updated_at) content_updated_at,
    COALESCE(p.content, c.content) content,
    s.reportIds reportIds,
    s.report_count reportCount,
    s.target_type targetType,
    s.target_id targetId,
    s.reported_user_id reportedUserId,
    s.status status,
    s.first_reported_at firstReportedAt,
    s.last_reported_at lastReportedAt,
    s.main_reason mainReason,
    s.snapshot snapshot,
    s.handled_at handledAt
FROM (
	SELECT
	    STRING_AGG(CAST(r.id AS text), ',' ORDER BY r.created_at) AS reportIds,
		COUNT(r.id) report_count,
		r.target_type,
		r.target_id,
		MIN(r.status) status,
		MIN(r.reported_user_id) reported_user_id,
		MIN(r.created_at) first_reported_at,
		MAX(r.created_at) last_reported_at,
		MODE() WITHIN GROUP (ORDER BY reason) main_reason,
		(ARRAY_AGG(r.target_content_snapshot ORDER BY r.created_at ASC))[1] AS snapshot,
	    r.handled_at
	FROM Report r
	WHERE r.target_type = :targetType
	    AND r.target_id = :targetId
	    AND r.handled_at IS NULL
	GROUP BY r.target_type, r.target_id, r.handled_at) AS s
LEFT JOIN Post p ON s.target_type = 'POST' AND p.id = s.target_id
LEFT JOIN Comment c ON s.target_type = 'COMMENT' AND c.id = s.target_id
""", nativeQuery = true)
    Optional<ReportDetailProjection> findReportDetailInternal(@Param("targetType")String targetType, @Param("targetId")Long targetId, @Param("handled")boolean handled);

    @Query(value = """
SELECT
    h.target_type targetType,
    h.target_id targetId,
    h.handled_at handledAt,
    h.resolution resolution,
    h.reportCount reportCount,
    h.mainReason mainReason,
    h.firstReportedAt firstReportedAt,
    h.lastReportedAt lastReportedAt,
    u.username handledBy,
    h.handledMemo handledMemo
FROM (
	SELECT
	    r.target_type,
		r.target_id,
		r.handled_at,
		MIN(r.resolution) resolution,
		COUNT(r.id) reportCount,
		(MODE() within GROUP (ORDER BY r.reason)) mainReason,
		MIN(r.created_at) firstReportedAt,
		MAX(r.created_at) lastReportedAt,
		MIN(r.handled_by) handledBy,
		MIN(r.handled_memo) handledMemo
	FROM Report r
	WHERE r.target_type = :targetType
		AND r.target_id = :targetId
		AND r.handled_at IS NOT NULL
	GROUP BY r.target_type, r.target_id, r.handled_at
) h
LEFT JOIN Users u ON u.id = h.handledBy
ORDER BY h.handled_at
""", nativeQuery = true)
    List<ReportHistoryResponseProjection> findReportHistoryListInternal(@Param("targetType")String targetType, @Param("targetId")Long targetId);

    @Query("""
SELECT
    r.reason,
    COUNT(r) reasonCount
FROM Report r
WHERE r.targetType = :targetType
    AND r.targetId = :targetId
    AND r.status = 'PENDING'
GROUP BY r.reason
ORDER BY COUNT(r) DESC
""")
    List<ReportReasonCount> findReportReasonCount(@Param("targetType")ReportTargetType targetType, @Param("targetId")Long targetId);

    @Modifying
    @Query("""
UPDATE Report r
SET r.status = 'RESOLVED',
    r.resolution = :resolution,
    r.handledAt = CURRENT_TIMESTAMP,
    r.handledBy = :adminId,
    r.handledMemo = :memo
WHERE r.id IN :ids
    AND r.status = 'PENDING'
""")
    int updateStatusAndResolutionByIds(@Param("ids")List<Long> ids, @Param("resolution")ReportResolution resolution, @Param("adminId")Long adminId, @Param("memo")String memo);


    default Page<ReportSummaryResponseProjection> findReportPage(ReportStatus status, Pageable pageable) {
        return findReportPageInternal(status == null ? null : status.name(), pageable);
    }

    default Optional<ReportDetailProjection> findReportDetail(ReportTargetType targetType, Long targetId, boolean handled) {
        return findReportDetailInternal(targetType == null ? null : targetType.name(), targetId, handled);
    }

    default List<ReportHistoryResponseProjection> findReportHistoryList(ReportTargetType targetType, Long targetId) {
        return findReportHistoryListInternal(targetType == null ? null : targetType.name(), targetId);
    }
}
