import { useState, useEffect, useMemo, useRef, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import type { PostSummary, PageMeta, SortType, ViewType } from '../types';
import { fetchPosts } from '../api/posts';
import { useChannels } from '../hooks/useChannels';
import FeedCard from '../components/post/FeedCard';
import FeedCompact from '../components/post/FeedCompact';
import Pagination from '../components/common/Pagination';
import Sidebar from '../components/layout/Sidebar';

const PAGE_SIZE = 20;

export default function AllPage() {
  const navigate = useNavigate();
  const channels = useChannels();
  const [channelId, setChannelId] = useState('all');
  const [sort, setSort] = useState<SortType>('latest');
  const [view, setView] = useState<ViewType>('card');
  const [search, setSearch] = useState('');
  const [searchInput, setSearchInput] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [posts, setPosts] = useState<PostSummary[]>([]);
  const [meta, setMeta] = useState<PageMeta>({ page: 0, size: PAGE_SIZE, total: 0, totalPages: 0 });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const tabsRef = useRef<HTMLDivElement>(null);
  const [showTabArrow, setShowTabArrow] = useState(true);

  const checkTabScroll = useCallback(() => {
    const el = tabsRef.current;
    if (!el) return;
    setShowTabArrow(el.scrollLeft + el.clientWidth < el.scrollWidth - 4);
  }, []);

  useEffect(() => {
    const el = tabsRef.current;
    if (!el) return;
    checkTabScroll();
    el.addEventListener('scroll', checkTabScroll);
    return () => el.removeEventListener('scroll', checkTabScroll);
  }, [checkTabScroll, channels]);

  const colorMap = useMemo(
    () => Object.fromEntries(channels.map(c => [c.id, c.color])),
    [channels],
  );

  useEffect(() => {
    let active = true;
    async function load() {
      setLoading(true);
      setError(null);
      try {
        const res = await fetchPosts({
          page: currentPage - 1,
          size: PAGE_SIZE,
          channelId: channelId === 'all' ? undefined : channelId,
          sort,
          search: search || undefined,
        });
        if (active) { setPosts(res.data); setMeta(res.meta); }
      } catch (e: unknown) {
        if (active) setError(e instanceof Error ? e.message : '오류가 발생했습니다.');
      } finally {
        if (active) setLoading(false);
      }
    }
    void load();
    return () => { active = false; };
  }, [channelId, sort, search, currentPage]);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    setSearch(searchInput);
    setCurrentPage(1);
  };

  const handleChannelChange = (id: string) => {
    setChannelId(id);
    setCurrentPage(1);
  };

  const handleSortChange = (s: SortType) => {
    setSort(s);
    setCurrentPage(1);
  };

  const handlePageChange = (p: number) => {
    setCurrentPage(p);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const allChannelNav = [{ id: 'all', name: '전체', color: '' }, ...channels];

  return (
    <div className="allpg">
      <div className="allmn">
        <div className="allhd">
          <span className="allt">전체글</span>
          <div className="allc">
            <form onSubmit={handleSearch} className="sbox">
              <span style={{ fontSize: 13, color: 'var(--t3)' }}>🔍</span>
              <input
                className="sinp"
                placeholder="제목, 작성자 검색"
                value={searchInput}
                onChange={e => setSearchInput(e.target.value)}
              />
            </form>
            <select
              className="ssel"
              value={sort}
              onChange={e => handleSortChange(e.target.value as SortType)}
            >
              <option value="latest">최신순</option>
              <option value="likes">좋아요순</option>
              <option value="comments">댓글순</option>
              <option value="views">조회순</option>
            </select>
            <div className="vtgl">
              <button className={'vbtn' + (view === 'card' ? ' active' : '')} onClick={() => setView('card')}>카드</button>
              <button className={'vbtn' + (view === 'compact' ? ' active' : '')} onClick={() => setView('compact')}>목록</button>
            </div>
          </div>
        </div>

        <div className="chtabs-wrap">
        <div className="chtabs" ref={tabsRef}>
          {allChannelNav.map(ch => (
            <button
              key={ch.id}
              className={'chnt' + (channelId === ch.id ? ' active' : '')}
              onClick={() => handleChannelChange(ch.id)}
            >
              {ch.name}
            </button>
          ))}
        </div>
          {showTabArrow && <div className="chtabs-arrow">›</div>}
        </div>

        {search && (
          <div className="rcnt">
            "{search}" 검색 결과 {meta.total}건
            <button
              onClick={() => { setSearch(''); setSearchInput(''); setCurrentPage(1); }}
              style={{ marginLeft: 8, fontSize: 11, color: 'var(--t3)', background: 'none', border: 'none', cursor: 'pointer', textDecoration: 'underline' }}
            >
              초기화
            </button>
          </div>
        )}

        {loading && <div className="lrow"><div className="spin" /><span>불러오는 중...</span></div>}
        {error && <div className="empty" style={{ color: 'var(--danger, #E24B4A)' }}>{error}</div>}

        {!loading && !error && posts.length === 0 && (
          <div className="empty">{search ? `"${search}"에 해당하는 글이 없습니다.` : '게시글이 없습니다.'}</div>
        )}

        {!loading && !error && (
          view === 'card'
            ? posts.map(p => (
              <FeedCard
                key={p.id}
                post={p}
                channelColor={colorMap[p.channelId] ?? '#888'}
                onClick={() => navigate(`/posts/${p.id}`)}
              />
            ))
            : (
              <div className="cmpc">
                {posts.map(p => (
                  <FeedCompact
                    key={p.id}
                    post={p}
                    channelColor={colorMap[p.channelId] ?? '#888'}
                    onClick={() => navigate(`/posts/${p.id}`)}
                  />
                ))}
              </div>
            )
        )}

        <Pagination current={currentPage} totalPages={meta.totalPages} onChange={handlePageChange} />
      </div>
      <Sidebar selectedChannelId={channelId} onChannelClick={handleChannelChange} />
    </div>
  );
}
