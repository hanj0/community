import type { ReportRequest } from '../types';

/**
 * 신고 등록. POST /api/reports → SuccessResponse<{ reportId }> 래핑.
 * 로그인 필수(401 시 로그인 프롬프트), 중복 신고는 409로 안내.
 */
export async function createReport(data: ReportRequest): Promise<number> {
  const res = await fetch('/api/reports', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include',
    body: JSON.stringify(data),
  });

  if (!res.ok) {
    if (res.status === 401) {
      window.dispatchEvent(new CustomEvent('auth:unauthorized'));
      throw new Error('로그인이 필요합니다.');
    }
    if (res.status === 409) {
      throw new Error('이미 신고한 대상입니다.');
    }
    const body = await res.json().catch(() => null);
    throw new Error(body?.error?.message ?? '신고를 접수하지 못했습니다.');
  }

  const body = await res.json();
  return body?.data?.reportId as number;
}
