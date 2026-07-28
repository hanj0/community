import type { PagedResponse, ReportSortType, ReportStatus, ReportSummary } from '../types';

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
