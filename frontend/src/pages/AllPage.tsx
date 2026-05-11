import { useState } from 'react';
import type { AnyPost, PageType, SortType, User, ViewType } from '../types';
import { FEED_POSTS, CHANNELS, ITEMS_PER_PAGE } from '../constants/data';
import FeedCard from '../components/post/FeedCard';
import FeedCompact from '../components/post/FeedCompact';
import Pagination from '../components/common/Pagination';
import Sidebar from '../components/layout/Sidebar';

interface AllPageProps {
  onPostClick: (post: AnyPost) => void;
  user: User | null;
  onNavigate: (page: PageType) => void;
}

const ALL_CH_NAV = [{ id: 'all', name: '전체' }, ...CHANNELS];

export default function AllPage({ onPostClick, user, onNavigate }: AllPageProps) {
  const [channel, setChannel] = useState('all');
  const [sort, setSort] = useState<SortType>('latest');
  const [view, setView] = useState<ViewType>('card');
  const [search, setSearch] = useState('');
  const [searchInput, setSearchInput] = useState('');
  const [currentPage, setCurrentPage] = useState(1);

  const notices = FEED_POSTS.filter(p => p.isNotice);
  const nonNotice = FEED_POSTS.filter(p => !p.isNotice);

  const filtered = nonNotice.filter(p => {
    const matchCh = channel === 'all' || p.channel === CHANNELS.find(c => c.id === channel)?.name;
    const matchQ = search === '' || p.title.includes(search) || p.author.includes(search);
    return matchCh && matchQ;
  });

  const sorted = [...filtered].sort((a, b) => {
    if (sort === 'latest')   return b.id - a.id;
    if (sort === 'likes')    return b.likes - a.likes;
    if (sort === 'comments') return b.comments - a.comments;
    if (sort === 'views')    return b.views - a.views;
    return 0;
  });

  const totalCount = sorted.length;
  const paginated = sorted.slice((currentPage - 1) * ITEMS_PER_PAGE, currentPage * ITEMS_PER_PAGE);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    setSearch(searchInput);
    setCurrentPage(1);
  };

  const handlePageChange = (p: number) => {
    setCurrentPage(p);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

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
            <select className="ssel" value={sort} onChange={e => { setSort(e.target.value as SortType); setCurrentPage(1); }}>
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

        <div className="chtabs">
          {ALL_CH_NAV.map(ch => (
            <button key={ch.id} className={'chnt' + (channel === ch.id ? ' active' : '')}
              onClick={() => { setChannel(ch.id); setCurrentPage(1); }}>
              {ch.name}
            </button>
          ))}
        </div>

        {channel === 'all' && notices.length > 0 && (
          <div className="cmpc" style={{ marginBottom: 10 }}>
            {notices.map(p => <FeedCompact key={p.id} post={p} onClick={() => onPostClick(p)} />)}
          </div>
        )}

        {search && (
          <div className="rcnt">
            "{search}" 검색 결과 {totalCount}건
            <button
              onClick={() => { setSearch(''); setSearchInput(''); setCurrentPage(1); }}
              style={{ marginLeft: 8, fontSize: 11, color: 'var(--t3)', background: 'none', border: 'none', cursor: 'pointer', textDecoration: 'underline' }}
            >
              초기화
            </button>
          </div>
        )}

        {paginated.length === 0 && (
          <div className="empty">{search ? `"${search}"에 해당하는 글이 없습니다.` : '게시글이 없습니다.'}</div>
        )}

        {view === 'card'
          ? paginated.map(p => <FeedCard key={p.id} post={p} onClick={() => onPostClick(p)} />)
          : <div className="cmpc">{paginated.map(p => <FeedCompact key={p.id} post={p} onClick={() => onPostClick(p)} />)}</div>
        }

        <Pagination current={currentPage} total={totalCount} onChange={handlePageChange} />
      </div>
      <Sidebar user={user} onNavigate={onNavigate} />
    </div>
  );
}
