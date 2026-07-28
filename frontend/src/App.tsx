import { Routes, Route, Navigate, useNavigate, useLocation } from 'react-router-dom';
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
import AdminReportsPage from './pages/AdminReportsPage';
import NotFoundPage from './pages/NotFoundPage';
import { useAuth } from './context/AuthContext';

function RouteLoading() {
  return (
    <div style={{ textAlign: 'center', padding: '60px 20px', color: 'var(--t3)', fontSize: 14 }}>
      <div className="spin" style={{ margin: '0 auto 12px' }} />
      불러오는 중...
    </div>
  );
}

function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const { user, loading } = useAuth();
  const location = useLocation();
  if (loading) return <RouteLoading />;
  return user ? <>{children}</> : <Navigate to="/login" state={{ from: location.pathname + location.search }} replace />;
}

/**
 * 관리자 전용 경로. 권한이 없으면 로그인으로 보내지 않고 404를 띄운다 —
 * "권한이 없습니다"는 그 경로가 실재한다는 걸 알려주는 셈이라서.
 * 실제 방어선은 백엔드의 hasRole('ADMIN')이고 이 가드는 UX까지다.
 */
function AdminRoute({ children }: { children: React.ReactNode }) {
  const { user, loading } = useAuth();
  if (loading) return <RouteLoading />;
  return user?.role === 'ADMIN' ? <>{children}</> : <NotFoundPage />;
}

function LoginRoute() {
  const { user } = useAuth();
  const location = useLocation();
  const from = (location.state as { from?: string } | null)?.from;
  if (user) return <Navigate to={from ?? '/'} replace />;
  return <LoginPage />;
}

function LoginPromptModal() {
  const { dismissLoginPrompt } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleConfirm = () => {
    dismissLoginPrompt();
    navigate('/login', { state: { from: location.pathname + location.search } });
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
        <Route path="/login" element={<LoginRoute />} />
        <Route path="/signup" element={user ? <Navigate to="/" replace /> : <SignupPage />} />
        <Route path="/write" element={<ProtectedRoute><WritePostPage /></ProtectedRoute>} />
        <Route path="/me" element={<ProtectedRoute><MyPage /></ProtectedRoute>} />
        <Route path="/admin/reports" element={<AdminRoute><AdminReportsPage /></AdminRoute>} />
        <Route path="/channels" element={
          <div style={{ textAlign: 'center', padding: '60px 20px', color: 'var(--t3)', fontSize: 14 }}>
            채널 탐색 페이지 — 준비 중
          </div>
        } />
        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </>
  );
}
