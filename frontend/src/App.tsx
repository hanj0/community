import { Routes, Route, Navigate, useNavigate } from 'react-router-dom';
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

function LoginPromptModal() {
  const { dismissLoginPrompt } = useAuth();
  const navigate = useNavigate();

  const handleConfirm = () => {
    dismissLoginPrompt();
    navigate('/login');
  };

  return (
    <div className="modal-overlay" onClick={dismissLoginPrompt}>
      <div className="modal" onClick={e => e.stopPropagation()}>
        <div className="modal-title">로그인이 필요합니다</div>
        <div className="modal-body">로그인이 필요한 기능입니다.<br />로그인 페이지로 이동하시겠습니까?</div>
        <div className="modal-actions">
          <button className="modal-btn" onClick={dismissLoginPrompt}>취소</button>
          <button className="modal-btn primary" onClick={handleConfirm}>로그인하기</button>
        </div>
      </div>
    </div>
  );
}

export default function App() {
  const { user, showLoginPrompt } = useAuth();

  return (
    <>
      <GNB />
      {showLoginPrompt && <LoginPromptModal />}
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
