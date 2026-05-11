import { useState, useEffect } from 'react';
import type { AnyPost, PageType, User } from './types';
import './styles/community.css';
import GNB from './components/layout/GNB';
import HomePage from './pages/HomePage';
import HotPage from './pages/HotPage';
import AllPage from './pages/AllPage';
import PostDetailPage from './pages/PostDetailPage';
import LoginPage from './pages/LoginPage';
import SignupPage from './pages/SignupPage';
import MyPage from './pages/MyPage';
import { getMe } from './api/auth';

const TOKEN_KEY = 'auth_token';

export default function App() {
  const [page, setPage] = useState<PageType>('home');
  const [selectedPost, setSelectedPost] = useState<AnyPost | null>(null);
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);

  useEffect(() => {
    const saved = localStorage.getItem(TOKEN_KEY);
    if (!saved) return;
    getMe(saved)
      .then(u => { setUser(u); setToken(saved); })
      .catch(() => localStorage.removeItem(TOKEN_KEY));
  }, []);

  function handleLogin(u: User, t: string) {
    setUser(u);
    setToken(t);
    localStorage.setItem(TOKEN_KEY, t);
    setPage('home');
  }

  function handleLogout() {
    setUser(null);
    setToken(null);
    localStorage.removeItem(TOKEN_KEY);
    setPage('home');
  }

  const goDetail = (post: AnyPost) => {
    setSelectedPost(post);
    setPage('detail');
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  return (
    <>
      <GNB
        page={page}
        user={user}
        onNavigate={setPage}
      />

      {page === 'login' ? (
        <LoginPage onLogin={handleLogin} onGoSignup={() => setPage('signup')} />
      ) : page === 'signup' ? (
        <SignupPage onLogin={handleLogin} onGoLogin={() => setPage('login')} />
      ) : page === 'mypage' && user && token ? (
        <MyPage user={user} onLogout={handleLogout} onNavigate={setPage} />
      ) : page === 'detail' && selectedPost ? (
        <PostDetailPage post={selectedPost} onBack={() => setPage('home')} onPostClick={goDetail} />
      ) : page === 'hot' ? (
        <HotPage onBack={() => setPage('home')} onPostClick={goDetail} user={user} onNavigate={setPage} />
      ) : page === 'home' ? (
        <HomePage onGoHot={() => setPage('hot')} onPostClick={goDetail} user={user} onNavigate={setPage} />
      ) : page === 'all' ? (
        <AllPage onPostClick={goDetail} user={user} onNavigate={setPage} />
      ) : (
        <div style={{ textAlign: 'center', padding: '60px 20px', color: 'var(--t3)', fontSize: 14 }}>
          채널 탐색 페이지 — 준비 중
        </div>
      )}
    </>
  );
}
