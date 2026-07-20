import { useCallback, useEffect, useRef, useState } from 'react';
import {
  fetchNotifications,
  fetchUnreadCount,
  markAllNotificationsRead,
  markNotificationRead,
} from '../api/notifications';
import type { NotificationItem } from '../types';

const POLL_INTERVAL = 60_000;

/**
 * 알림 상태 관리 훅. (로그인 사용자에게만 마운트되는 컴포넌트에서 사용)
 * - unreadCount: 마운트 시 + 60초 주기로 폴링 (탭이 숨겨지면 중지, 복귀 시 즉시 갱신 후 재개)
 * - items: 커서 페이징으로 append
 * - markRead / markAllRead: 낙관적 선반영 후 서버 요청, 실패 시 카운트만 재동기화
 */
export function useNotifications() {
  const [unreadCount, setUnreadCount] = useState(0);
  const [items, setItems] = useState<NotificationItem[]>([]);
  const [cursor, setCursor] = useState<string | null>(null);
  const [hasNext, setHasNext] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const loadedRef = useRef(false);

  const refreshCount = useCallback(async () => {
    try {
      setUnreadCount(await fetchUnreadCount());
    } catch {
      /* 뱃지 갱신 실패는 조용히 무시 */
    }
  }, []);

  useEffect(() => {
    let timer: ReturnType<typeof setInterval> | null = null;

    const start = () => {
      if (timer != null) return;
      timer = setInterval(refreshCount, POLL_INTERVAL);
    };
    const stop = () => {
      if (timer == null) return;
      clearInterval(timer);
      timer = null;
    };

    const onVisibilityChange = () => {
      if (document.hidden) {
        stop(); // 탭이 안 보이면 폴링 중지
      } else {
        refreshCount(); // 복귀 시 다음 주기를 기다리지 않고 즉시 갱신
        start();
      }
    };

    // 초기: 보이는 상태면 즉시 갱신 + 폴링 시작
    if (!document.hidden) {
      refreshCount();
      start();
    }
    document.addEventListener('visibilitychange', onVisibilityChange);

    return () => {
      document.removeEventListener('visibilitychange', onVisibilityChange);
      stop();
    };
  }, [refreshCount]);

  /** 패널 최초 오픈 시 1페이지 로드 (이미 로드했으면 스킵) */
  const ensureLoaded = useCallback(async () => {
    if (loadedRef.current || loading) return;
    setLoading(true);
    setError(null);
    try {
      const res = await fetchNotifications();
      setItems(res.data);
      setCursor(res.nextCursor);
      setHasNext(res.hasNext);
      loadedRef.current = true;
    } catch {
      setError('알림을 불러오지 못했습니다.');
    } finally {
      setLoading(false);
    }
  }, [loading]);

  const loadMore = useCallback(async () => {
    if (loading || !hasNext || cursor == null) return;
    setLoading(true);
    setError(null);
    try {
      const res = await fetchNotifications(cursor);
      setItems((prev) => [...prev, ...res.data]);
      setCursor(res.nextCursor);
      setHasNext(res.hasNext);
    } catch {
      setError('알림을 더 불러오지 못했습니다.');
    } finally {
      setLoading(false);
    }
  }, [loading, hasNext, cursor]);

  const markRead = useCallback(
    async (id: number) => {
      const target = items.find((n) => n.id === id);
      if (!target || target.isRead) return;

      // 낙관적 선반영
      setItems((prev) => prev.map((n) => (n.id === id ? { ...n, isRead: true } : n)));
      setUnreadCount((c) => Math.max(0, c - 1));
      try {
        await markNotificationRead(id);
      } catch {
        refreshCount(); // 실패 시 서버 값으로 카운트 재동기화
      }
    },
    [items, refreshCount],
  );

  const markAllRead = useCallback(async () => {
    if (unreadCount === 0) return;
    setItems((prev) => prev.map((n) => (n.isRead ? n : { ...n, isRead: true })));
    setUnreadCount(0);
    try {
      await markAllNotificationsRead();
    } catch {
      refreshCount();
    }
  }, [unreadCount, refreshCount]);

  return { unreadCount, items, hasNext, loading, error, ensureLoaded, loadMore, markRead, markAllRead };
}
