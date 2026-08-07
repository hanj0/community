import type {
  PagedResponse,
  ReportDetail,
  ReportHistoryEntry,
  ReportReason,
  ReportReasonCount,
  ReportSortType,
  ReportStatus,
  ReportSummary,
  ReportTargetType,
} from '../types';

/**
 * 관리자 신고 목록. GET /api/admin/reports → PageResponse 그대로.
 * 상태(reportStatus)는 서버에서 필수 파라미터라 항상 실어 보낸다.
 */
export async function fetchAdminReports(params: {
  status: ReportStatus;
  sort?: ReportSortType;
  page?: number;
  size?: number;
}): Promise<PagedResponse<ReportSummary>> {
  const url = new URL('/api/admin/reports', window.location.origin);
  url.searchParams.set('reportStatus', params.status);
  if (params.sort) url.searchParams.set('sort', params.sort);
  if (params.page !== undefined) url.searchParams.set('page', String(params.page));
  if (params.size !== undefined) url.searchParams.set('size', String(params.size));

  const res = await fetch(url.toString(), { credentials: 'include' });

  if (!res.ok) {
    if (res.status === 401) {
      window.dispatchEvent(new CustomEvent('auth:unauthorized'));
      throw new Error('로그인이 필요합니다.');
    }
    if (res.status === 403) {
      throw new Error('관리자 권한이 필요합니다.');
    }
    const body = await res.json().catch(() => null);
    throw new Error(body?.error?.message ?? '신고 목록을 불러올 수 없습니다.');
  }

  return res.json() as Promise<PagedResponse<ReportSummary>>;
}

interface RawReportDetail {
  targetType: ReportTargetType;
  targetId: number;
  reportCount: number;
  mainReason: ReportReason;
  firstReportedAt: string;
  lastReportedAt: string;
  updated: boolean;
  snapshot: string;
  reasonCounts: ReportReasonCount[];
  reportedUserId: number;
}

/**
 * PENDING 신고 상세. GET /api/admin/reports/content/{targetType}/{targetId} → SuccessResponse 래핑.
 * 이 엔드포인트는 PENDING 전용이라 쿼리파라미터가 없다. 처리완료는 fetchAdminReportHistory를 쓴다.
 * 서버가 아직 피신고자 닉네임을 안 내려줘서, 목록 행(summary)에서 보충한다.
 */
export async function fetchAdminReportDetail(summary: ReportSummary): Promise<ReportDetail> {
  const { targetType, targetId } = summary;
  const res = await fetch(`/api/admin/reports/content/${targetType}/${targetId}`, {
    credentials: 'include',
  });

  if (!res.ok) {
    if (res.status === 401) {
      window.dispatchEvent(new CustomEvent('auth:unauthorized'));
      throw new Error('로그인이 필요합니다.');
    }
    if (res.status === 403) {
      throw new Error('관리자 권한이 필요합니다.');
    }
    const body = await res.json().catch(() => null);
    throw new Error(body?.error?.message ?? '신고 상세를 불러올 수 없습니다.');
  }

  const body = await res.json();
  const raw = body.data as RawReportDetail;

  return {
    targetType: raw.targetType,
    targetId: raw.targetId,
    reportCount: raw.reportCount,
    mainReason: raw.mainReason,
    firstReportedAt: raw.firstReportedAt,
    lastReportedAt: raw.lastReportedAt,
    edited: raw.updated,
    contentSnapshot: raw.snapshot,
    reasonDistribution: raw.reasonCounts,
    reportedUserId: raw.reportedUserId,
    reportedUsername: summary.reportedUsername,
    reportedUserState: null,
    reportedUserPriorSanctions: null,
    suggestion: null,
  };
}

/**
 * 처리완료 대상의 처리 이력. GET /api/admin/reports/history/{targetType}/{targetId} → SuccessResponse 래핑.
 */
export async function fetchAdminReportHistory(
  targetType: ReportTargetType,
  targetId: number,
): Promise<ReportHistoryEntry[]> {
  const res = await fetch(`/api/admin/reports/history/${targetType}/${targetId}`, {
    credentials: 'include',
  });

  if (!res.ok) {
    if (res.status === 401) {
      window.dispatchEvent(new CustomEvent('auth:unauthorized'));
      throw new Error('로그인이 필요합니다.');
    }
    if (res.status === 403) {
      throw new Error('관리자 권한이 필요합니다.');
    }
    const body = await res.json().catch(() => null);
    throw new Error(body?.error?.message ?? '처리 이력을 불러올 수 없습니다.');
  }

  const body = await res.json();
  const raw = body.data as { histories: ReportHistoryEntry[]; totalCount: number };
  return raw.histories;
}
