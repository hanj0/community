import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import NotificationBell from './NotificationBell';

const NAV_ITEMS = [
  { path: '/',        label: '홈' },
  { path: '/all',     label: '전체글' },
  { path: '/channels',label: '채널 탐색' },
];

export default function GNB() {
  const navigate = useNavigate();
  const location = useLocation();
  const { user } = useAuth();

  const isHomeGroup = ['/', '/hot', '/posts'].some(p =>
    p === '/' ? location.pathname === '/' : location.pathname.startsWith(p)
  );

  function isActive(path: string) {
    if (path === '/') return isHomeGroup;
    return location.pathname.startsWith(path);
  }

  return (
    <nav className="gnb">
      <div className="gnb-inner">
        <div className="gnb-l">
          <span className="logo" onClick={() => navigate('/')}>커뮤니티</span>
          <div className="gnb-tabs">
            {NAV_ITEMS.map(({ path, label }) => (
              <button
                key={path}
                className={'gtab' + (isActive(path) ? ' active' : '')}
                onClick={() => navigate(path)}
              >
                {label}
              </button>
            ))}
          </div>
        </div>
        <div className="gnb-r">
          {user ? (
            <>
              <NotificationBell />
              <div className="gnb-user" onClick={() => navigate('/me')}>
                <div className="av">{user.username.charAt(0).toUpperCase()}</div>
                <span className="gnb-username">{user.username}</span>
              </div>
            </>
          ) : (
            <>
              <button className="gnb-login-btn" onClick={() => navigate('/login')}>로그인</button>
              <button className="gnb-signup-btn" onClick={() => navigate('/signup')}>회원가입</button>
            </>
          )}
        </div>
      </div>
    </nav>
  );
}
