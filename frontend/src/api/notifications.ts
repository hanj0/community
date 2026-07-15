import type { CursorResponse, NotificationItem } from '../types';

const BASE = '/api/notifications';

function onUnauthorized(status: number) {
  if (status === 401) window.dispatchEvent(new CustomEvent('auth:unauthorized'));
}

/** 안읽은 알림 개수. GET /unread-count → SuccessResponse 래핑. */
export async function fetchUnreadCount(): Promise<number> {
  const res = await fetch(`${BASE}/unread-count`, { credentials: 'include' });
  onUnauthorized(res.status);
  if (!res.ok) throw new Error('알림 개수를 불러올 수 없습니다.');
  const body = await res.json();
  return body?.data?.unreadCount ?? 0;
}

/** 알림 목록(커서 페이징). GET /notifications → CursorResponse 직접 반환. */
export async function fetchNotifications(cursor?: string): Promise<CursorResponse<NotificationItem>> {
  const url = new URL(BASE, window.location.origin);
  if (cursor) url.searchParams.set('cursor', cursor);
  const res = await fetch(url.toString(), { credentials: 'include' });
  onUnauthorized(res.status);
  if (!res.ok) throw new Error('알림을 불러올 수 없습니다.');
  return res.json() as Promise<CursorResponse<NotificationItem>>;
}

/** 개별 읽음 처리. PATCH /{id} → 200, 바디 없음. */
export async function markNotificationRead(id: number): Promise<void> {
  const res = await fetch(`${BASE}/${id}`, { method: 'PATCH', credentials: 'include' });
  onUnauthorized(res.status);
  if (!res.ok) throw new Error('알림을 읽음 처리할 수 없습니다.');
}

/** 일괄 읽음 처리. PATCH /notifications → 200, 바디 없음. */
export async function markAllNotificationsRead(): Promise<void> {
  const res = await fetch(BASE, { method: 'PATCH', credentials: 'include' });
  onUnauthorized(res.status);
  if (!res.ok) throw new Error('알림을 읽음 처리할 수 없습니다.');
}
