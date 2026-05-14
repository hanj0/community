import { useState, useEffect, useRef, useCallback, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import type { PostSummary, PeriodType } from '../types';
import { fetchHotPosts } from '../api/posts';
import { useChannels } from '../hooks/useChannels';
import HotPostCard from '../components/post/HotPostCard';
import Sidebar from '../components/layout/Sidebar';

const PAGE_SIZE = 20;

export default function HotPage() {
  const navigate = useNavigate();
  const channels = useChannels();
  const [period, setPeriod] = useState<PeriodType>('24h');
  const [channelId, setChannelId] = useState('all');
  const [posts, setPosts] = useState<PostSummary[]>([]);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [loading, setLoading] = useState(false);

  const colorMap = useMemo(
    () => Object.fromEntries(channels.map(c => [c.id, c.color])),
    [channels],
  );

  const loadMore = useCallback(async (nextPage: number, reset: boolean) => {
    if (loading) return;
    setLoading(true);
    try {
      const res = await fetchHotPosts({
        page: nextPage,
        size: PAGE_SIZE,
        period,
        channelId: channelId === 'all' ? undefined : channelId,
      });
      setPosts(prev => reset ? res.data : [...prev, ...res.data]);
      setHasMore(nextPage + 1 < res.meta.totalPages);
      setPage(nextPage + 1);
    } catch {
      // ignore
    } finally {
      setLoading(false);
    }
  }, [loading, period, channelId]);

  useEffect(() => {
    setPosts([]);
    setPage(0);
    setHasMore(true);
    loadMore(0, true);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [period, channelId]);

  const sentinelRef = useRef<HTMLDivElement>(null);
  const observerRef = useRef<IntersectionObserver | null>(null);

  useEffect(() => {
    if (!sentinelRef.current) return;
    observerRef.current = new IntersectionObserver(entries => {
      if (entries[0].isIntersecting && hasMore && !loading) {
        loadMore(page, false);
      }
    }, { threshold: 0.1 });
    observerRef.current.observe(sentinelRef.current);
    return () => observerRef.current?.disconnect();
  }, [hasMore, loading, page, loadMore]);

  const pinned = posts.filter(p => p.isPinned);
  const normal = posts.filter(p => !p.isPinned);
  let rank = 1;

  return (
    <div className="hotpg">
      <div className="hotmn">
        <div className="pghd">
          <button className="bbtn" onClick={() => navigate('/')}>← 홈으로</button>
          <span style={{ color: 'var(--t3)', fontSize: 13 }}>/</span>
          <span className="pgtit">인기글</span>
          <span className="bhot" style={{ fontSize: 11, padding: '3px 9px' }}>HOT</span>
        </div>
        <div className="fbar">
          <span className="flbl">기간</span>
          <div className="chips">
            {([['24h', '24시간'], ['7d', '7일'], ['30d', '30일']] as const).map(([v, l]) => (
              <button
                key={v}
                className={'chip' + (period === v ? ' active' : '')}
                onClick={() => setPeriod(v)}
              >
                {l}
              </button>
            ))}
          </div>
          <span className="flbl" style={{ marginLeft: 8 }}>채널</span>
          <div className="chips">
            <button
              className={'chip' + (channelId === 'all' ? ' active' : '')}
              onClick={() => setChannelId('all')}
            >
              전체
            </button>
            {channels.map(ch => (
              <button
                key={ch.id}
                className={'chip' + (channelId === ch.id ? ' active' : '')}
                onClick={() => setChannelId(ch.id)}
              >
                {ch.name}
              </button>
            ))}
          </div>
        </div>

        {pinned.length > 0 && (
          <>
            <div style={{ fontSize: 11, color: 'var(--t3)', marginBottom: 6 }}>고정글</div>
            {pinned.map(p => (
              <HotPostCard
                key={p.id}
                post={p}
                channelColor={colorMap[p.channelId] ?? '#888'}
                rank={rank++}
                onClick={() => navigate(`/posts/${p.id}`)}
              />
            ))}
            <div className="dvlbl"><span>인기순</span></div>
          </>
        )}

        {!loading && posts.length === 0 && (
          <div className="empty">해당 조건의 인기글이 없습니다.</div>
        )}

        {normal.map(p => (
          <HotPostCard
            key={p.id}
            post={p}
            channelColor={colorMap[p.channelId] ?? '#888'}
            rank={rank++}
            onClick={() => navigate(`/posts/${p.id}`)}
          />
        ))}

        {loading && <div className="lrow"><div className="spin" /><span>불러오는 중...</span></div>}
        {hasMore && !loading && <div ref={sentinelRef} style={{ height: 1 }} />}
        {!hasMore && normal.length > 0 && (
          <div style={{ textAlign: 'center', padding: '20px 0', fontSize: 12, color: 'var(--t3)' }}>
            마지막 게시글입니다
          </div>
        )}
      </div>
      <Sidebar />
    </div>
  );
}
