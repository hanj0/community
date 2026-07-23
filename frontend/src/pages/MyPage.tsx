import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { fetchMyPosts, fetchMyComments, fetchMyBookmarks, fetchMyStats } from '../api/posts';
import type { PostSummary, MyComment } from '../types';
import { formatRelativeTime } from '../utils/time';
import Pagination from '../components/common/Pagination';
import PasswordChangeModal from '../components/common/PasswordChangeModal';

type Section = 'activity' | 'settings';
type Tab = 'posts' | 'comments' | 'bookmarks';

function formatDate(iso: string) {
  return new Date(iso).toLocaleDateString('ko-KR', { year: 'numeric', month: 'long', day: 'numeric' });
}

const PAGE_SIZE = 10;

export default function MyPage() {
  const navigate = useNavigate();
  const { user, logout } = useAuth();

  const [stats, setStats] = useState({ postCount: 0, commentCount: 0, createdAt: '' });
  const [section, setSection] = useState<Section>('settings');
  const [tab, setTab] = useState<Tab>('posts');
  const [showPasswordModal, setShowPasswordModal] = useState(false);

  const [posts, setPosts] = useState<PostSummary[]>([]);
  const [postsPage, setPostsPage] = useState(1);
  const [postsTotalPages, setPostsTotalPages] = useState(0);
  const [postsLoading, setPostsLoading] = useState(false);

  const [comments, setComments] = useState<MyComment[]>([]);
  const [commentsPage, setCommentsPage] = useState(1);
  const [commentsTotalPages, setCommentsTotalPages] = useState(0);
  const [commentsLoading, setCommentsLoading] = useState(false);

  const [bookmarks, setBookmarks] = useState<PostSummary[]>([]);
  const [bookmarksPage, setBookmarksPage] = useState(1);
  const [bookmarksTotalPages, setBookmarksTotalPages] = useState(0);
  const [bookmarksLoading, setBookmarksLoading] = useState(false);

  useEffect(() => {
    fetchMyStats().then(setStats).catch(() => {});
  }, []);

  useEffect(() => {
    setPostsLoading(true);
    fetchMyPosts({ page: postsPage - 1, size: PAGE_SIZE })
      .then(res => { setPosts(res.data); setPostsTotalPages(res.meta.totalPages); })
      .catch(() => {})
      .finally(() => setPostsLoading(false));
  }, [postsPage]);

  useEffect(() => {
    setCommentsLoading(true);
    fetchMyComments({ page: commentsPage - 1, size: PAGE_SIZE })
      .then(res => { setComments(res.data); setCommentsTotalPages(res.meta.totalPages); })
      .catch(() => {})
      .finally(() => setCommentsLoading(false));
  }, [commentsPage]);

  useEffect(() => {
    setBookmarksLoading(true);
    fetchMyBookmarks({ page: bookmarksPage - 1, size: PAGE_SIZE })
      .then(res => { setBookmarks(res.data); setBookmarksTotalPages(res.meta.totalPages); })
      .catch(() => {})
      .finally(() => setBookmarksLoading(false));
  }, [bookmarksPage]);

  if (!user) return null;

  function handleLogout() {
    logout();
    navigate('/', { replace: true });
  }

  return (
    <div className="mypg">
      <div className="mypg-hd">
        <div className="mypg-av">{user.username.charAt(0).toUpperCase()}</div>
        <div className="mypg-nm">{user.username}</div>
        <div className="mypg-em">{user.email}</div>
        {stats.createdAt && (
          <div style={{ fontSize: 12, color: 'var(--t3)', marginBottom: 16 }}>
            가입일 {formatDate(stats.createdAt)}
          </div>
        )}
        <div className="mypg-stats">
          <div className="mypg-stat">
            <div className="mypg-stat-v">{stats.postCount}</div>
            <div className="mypg-stat-l">작성글</div>
          </div>
          <div className="mypg-stat">
            <div className="mypg-stat-v">{stats.commentCount}</div>
            <div className="mypg-stat-l">댓글</div>
          </div>
        </div>
      </div>

      <div className="mypg-nav">
        <button className={`mypg-nav-tab${section === 'activity' ? ' active' : ''}`} onClick={() => setSection('activity')}>
          내 활동
        </button>
        <button className={`mypg-nav-tab${section === 'settings' ? ' active' : ''}`} onClick={() => setSection('settings')}>
          설정
        </button>
      </div>

      {section === 'activity' && (
      <div className="mypg-sec">
        <div className="mypg-tabs">
          <button className={`mypg-tab${tab === 'posts' ? ' active' : ''}`} onClick={() => setTab('posts')}>
            내가 쓴 글
          </button>
          <button className={`mypg-tab${tab === 'comments' ? ' active' : ''}`} onClick={() => setTab('comments')}>
            내 댓글
          </button>
          <button className={`mypg-tab${tab === 'bookmarks' ? ' active' : ''}`} onClick={() => setTab('bookmarks')}>
            북마크
          </button>
        </div>

        <div className="mypg-tab-body">
          {tab === 'posts' && (
            postsLoading
              ? <div className="mypg-empty"><div className="spin" style={{ margin: '0 auto 8px' }} />불러오는 중...</div>
              : posts.length === 0
                ? <div className="mypg-empty">작성한 글이 없습니다.</div>
                : <>
                    {posts.map(p => (
                      <div key={p.id} className="mypg-row" onClick={() => navigate(`/posts/${p.id}`)}>
                        <div className="mypg-row-t">{p.title}</div>
                        <div className="mypg-row-m">
                          <span>{p.channelName}</span>
                          <span>·</span>
                          <span>{formatRelativeTime(p.createdAt)}</span>
                          <span>♥ {p.likeCount}</span>
                          <span>댓글 {p.commentCount}</span>
                        </div>
                      </div>
                    ))}
                    <div style={{ padding: '8px 0' }}>
                      <Pagination current={postsPage} totalPages={postsTotalPages} onChange={setPostsPage} />
                    </div>
                  </>
          )}

          {tab === 'comments' && (
            commentsLoading
              ? <div className="mypg-empty"><div className="spin" style={{ margin: '0 auto 8px' }} />불러오는 중...</div>
              : comments.length === 0
                ? <div className="mypg-empty">작성한 댓글이 없습니다.</div>
                : <>
                    {comments.map(c => (
                      <div key={c.id} className="mypg-row" onClick={() => navigate(`/posts/${c.postId}`)}>
                        <div className="mypg-row-t">{c.postTitle}</div>
                        <div className="mypg-row-sub">{c.content}</div>
                        <div className="mypg-row-m" style={{ marginTop: 4 }}>
                          <span>{formatRelativeTime(c.createdAt)}</span>
                        </div>
                      </div>
                    ))}
                    <div style={{ padding: '8px 0' }}>
                      <Pagination current={commentsPage} totalPages={commentsTotalPages} onChange={setCommentsPage} />
                    </div>
                  </>
          )}

          {tab === 'bookmarks' && (
            bookmarksLoading
              ? <div className="mypg-empty"><div className="spin" style={{ margin: '0 auto 8px' }} />불러오는 중...</div>
              : bookmarks.length === 0
                ? <div className="mypg-empty">북마크한 글이 없습니다.</div>
                : <>
                    {bookmarks.map(p => (
                      <div key={p.id} className="mypg-row" onClick={() => navigate(`/posts/${p.id}`)}>
                        <div className="mypg-row-t">{p.title}</div>
                        <div className="mypg-row-m">
                          <span>{p.channelName}</span>
                          <span>·</span>
                          <span>{formatRelativeTime(p.createdAt)}</span>
                          <span>♥ {p.likeCount}</span>
                          <span>댓글 {p.commentCount}</span>
                        </div>
                      </div>
                    ))}
                    <div style={{ padding: '8px 0' }}>
                      <Pagination current={bookmarksPage} totalPages={bookmarksTotalPages} onChange={setBookmarksPage} />
                    </div>
                  </>
          )}
        </div>
      </div>
      )}

      {section === 'settings' && (
      <>
        <div className="mypg-sec">
          <div className="mypg-sch">설정</div>
          <div className="mypg-act">
            <span className="mypg-act-ic">🔔</span>
            <span className="mypg-act-lbl">알림 설정</span>
            <span className="mypg-act-val">&rsaquo;</span>
          </div>
          <div className="mypg-act" onClick={() => setShowPasswordModal(true)}>
            <span className="mypg-act-ic">🔐</span>
            <span className="mypg-act-lbl">비밀번호 변경</span>
            <span className="mypg-act-val">&rsaquo;</span>
          </div>
        </div>

        <button className="mypg-logout" onClick={handleLogout}>로그아웃</button>
      </>
      )}

      {showPasswordModal && <PasswordChangeModal onClose={() => setShowPasswordModal(false)} />}
    </div>
  );
}
