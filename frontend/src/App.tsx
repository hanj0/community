import { Routes, Route, Navigate } from 'react-router-dom';
import './styles/community.css';
import GNB from './components/layout/GNB';
import HomePage from './pages/HomePage';
import HotPage from './pages/HotPage';
import AllPage from './pages/AllPage';
import PostDetailPage from './pages/PostDetailPage';
import LoginPage from './pages/LoginPage';
import SignupPage from './pages/SignupPage';
import MyPage from './pages/MyPage';
import WritePostPage from './pages/WritePostPage';
import { useAuth } from './context/AuthContext';

function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const { user } = useAuth();
  return user ? <>{children}</> : <Navigate to="/login" replace />;
}

export default function App() {
  const { user } = useAuth();

  return (
    <>
      <GNB />
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/hot" element={<HotPage />} />
        <Route path="/all" element={<AllPage />} />
        <Route path="/posts/:id" element={<PostDetailPage />} />
        <Route path="/login" element={user ? <Navigate to="/" replace /> : <LoginPage />} />
        <Route path="/signup" element={user ? <Navigate to="/" replace /> : <SignupPage />} />
        <Route path="/write" element={<ProtectedRoute><WritePostPage /></ProtectedRoute>} />
        <Route path="/me" element={<ProtectedRoute><MyPage /></ProtectedRoute>} />
        <Route path="/channels" element={
          <div style={{ textAlign: 'center', padding: '60px 20px', color: 'var(--t3)', fontSize: 14 }}>
            채널 탐색 페이지 — 준비 중
          </div>
        } />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </>
  );
}
