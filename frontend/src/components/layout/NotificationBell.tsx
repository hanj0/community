import { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useNotifications } from '../../hooks/useNotifications';
import { formatRelativeTime } from '../../utils/time';
import type { NotificationItem } from '../../types';

function actorText(n: NotificationItem): string {
  return n.actorCount > 1 ? `${n.lastActorName}님 외 ${n.actorCount - 1}명` : `${n.lastActorName}님`;
}

function bodyText(n: NotificationItem): string {
  if (n.type === 'REACTION') {
    return n.targetType === 'COMMENT' ? '회원님의 댓글을 좋아합니다' : '회원님의 글을 좋아합니다';
  }
  return '회원님의 글에 댓글을 남겼습니다';
}

export default function NotificationBell() {
  const navigate = useNavigate();
  const [open, setOpen] = useState(false);
  const wrapRef = useRef<HTMLDivElement>(null);

  const { unreadCount, items, hasNext, loading, error, ensureLoaded, loadMore, markRead, markAllRead } =
    useNotifications();

  function toggle() {
    setOpen((prev) => {
      if (!prev) ensureLoaded();
      return !prev;
    });
  }

  function handleItemClick(n: NotificationItem) {
    markRead(n.id);
    setOpen(false);
    navigate(`/posts/${n.rootPostId}`);
  }

  useEffect(() => {
    if (!open) return;
    function onClickOutside(e: MouseEvent) {
      if (wrapRef.current && !wrapRef.current.contains(e.target as Node)) setOpen(false);
    }
    document.addEventListener('mousedown', onClickOutside);
    return () => document.removeEventListener('mousedown', onClickOutside);
  }, [open]);

  return (
    <div className="noti" ref={wrapRef}>
      <button className="noti-btn" onClick={toggle}>
        알림
        {unreadCount > 0 && <span className="noti-badge">{unreadCount > 99 ? '99+' : unreadCount}</span>}
      </button>

      {open && (
        <div className="noti-panel">
          <div className="noti-head">
            <span className="noti-title">알림</span>
            <button className="noti-readall" onClick={markAllRead} disabled={unreadCount === 0}>
              모두 읽음
            </button>
          </div>

          <div className="noti-list">
            {items.length === 0 && !loading ? (
              <div className="noti-empty">{error ?? '새로운 알림이 없습니다'}</div>
            ) : (
              items.map((n) => (
                <div
                  key={n.id}
                  className={'noti-item' + (n.isRead ? '' : ' unread')}
                  onClick={() => handleItemClick(n)}
                >
                  <span className={'noti-icon ' + n.type.toLowerCase()}>
                    {n.type === 'REACTION' ? '♥' : '💬'}
                  </span>
                  <div className="noti-body">
                    <p className="noti-text">
                      <strong>{actorText(n)}</strong>이 {bodyText(n)}
                    </p>
                    {n.targetPreview && <p className="noti-sub">{n.targetPreview}</p>}
                    <span className="noti-time">{formatRelativeTime(n.updatedAt)}</span>
                  </div>
                </div>
              ))
            )}
          </div>

          {loading && <div className="noti-status">불러오는 중…</div>}
          {error && items.length > 0 && <div className="noti-status err">{error}</div>}
          {hasNext && !loading && (
            <button className="noti-more" onClick={loadMore}>
              더 보기
            </button>
          )}
        </div>
      )}
    </div>
  );
}
