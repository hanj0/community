import { useState, useEffect, useRef, useCallback } from 'react';
import type { AnyPost, PageType, User } from '../types';
import { ALL_POSTS, CHANNELS, PAGE_SIZE } from '../constants/data';
import HotPostCard from '../components/post/HotPostCard';
import Sidebar from '../components/layout/Sidebar';

interface HotPageProps {
  onBack: () => void;
  onPostClick: (post: AnyPost) => void;
  user: User | null;
  onNavigate: (page: PageType) => void;
}

export default function HotPage({ onBack, onPostClick, user, onNavigate }: HotPageProps) {
  const [period, setPeriod] = useState('24h');
  const [channel, setChannel] = useState('all');
  const [displayCount, setDisplayCount] = useState(PAGE_SIZE);
  const [loading, setLoading] = useState(false);

  const filtered = ALL_POSTS.filter(p =>
    channel === 'all' ? true : p.channel === CHANNELS.find(c => c.id === channel)?.name
  );
  const pinned = filtered.filter(p => p.isPin);
  const normal = filtered.filter(p => !p.isPin);
  const visible = normal.slice(0, displayCount);
  const hasMore = displayCount < normal.length;

  const loadMore = useCallback(() => {
    setLoading(true);
    setTimeout(() => { setDisplayCount(n => n + PAGE_SIZE); setLoading(false); }, 600);
  }, []);

  const sentinelRef = useRef<HTMLDivElement>(null);
  const observerRef = useRef<IntersectionObserver | null>(null);

  useEffect(() => {
    if (!sentinelRef.current) return;
    observerRef.current = new IntersectionObserver(entries => {
      if (entries[0].isIntersecting && hasMore && !loading) loadMore();
    }, { threshold: 0.1 });
    observerRef.current.observe(sentinelRef.current);
    return () => observerRef.current?.disconnect();
  }, [hasMore, loading, loadMore]);

  let rank = 1;

  return (
    <div className="hotpg">
      <div className="hotmn">
        <div className="pghd">
          <button className="bbtn" onClick={onBack}>← 홈으로</button>
          <span style={{ color: 'var(--t3)', fontSize: 13 }}>/</span>
          <span className="pgtit">인기글</span>
          <span className="bhot" style={{ fontSize: 11, padding: '3px 9px' }}>HOT</span>
        </div>
        <div className="fbar">
          <span className="flbl">기간</span>
          <div className="chips">
            {([['24h', '24시간'], ['7d', '7일'], ['30d', '30일']] as const).map(([v, l]) => (
              <button key={v} className={'chip' + (period === v ? ' active' : '')}
                onClick={() => { setPeriod(v); setDisplayCount(PAGE_SIZE); }}>
                {l}
              </button>
            ))}
          </div>
          <span className="flbl" style={{ marginLeft: 8 }}>채널</span>
          <div className="chips">
            <button className={'chip' + (channel === 'all' ? ' active' : '')}
              onClick={() => { setChannel('all'); setDisplayCount(PAGE_SIZE); }}>
              전체
            </button>
            {CHANNELS.map(ch => (
              <button key={ch.id} className={'chip' + (channel === ch.id ? ' active' : '')}
                onClick={() => { setChannel(ch.id); setDisplayCount(PAGE_SIZE); }}>
                {ch.name}
              </button>
            ))}
          </div>
        </div>

        {pinned.length > 0 && (
          <>
            <div style={{ fontSize: 11, color: 'var(--t3)', marginBottom: 6 }}>고정글</div>
            {pinned.map(p => <HotPostCard key={p.id} post={p} rank={rank++} onClick={() => onPostClick(p)} />)}
            <div className="dvlbl"><span>인기순</span></div>
          </>
        )}
        {normal.length === 0 && <div className="empty">해당 조건의 인기글이 없습니다.</div>}
        {visible.map(p => <HotPostCard key={p.id} post={p} rank={rank++} onClick={() => onPostClick(p)} />)}
        {loading && <div className="lrow"><div className="spin" /><span>불러오는 중...</span></div>}
        {hasMore && !loading && <div ref={sentinelRef} style={{ height: 1 }} />}
        {!hasMore && normal.length > 0 && (
          <div style={{ textAlign: 'center', padding: '20px 0', fontSize: 12, color: 'var(--t3)' }}>
            마지막 게시글입니다
          </div>
        )}
      </div>
      <Sidebar user={user} onNavigate={onNavigate} />
    </div>
  );
}
