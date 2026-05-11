import type { PageType, User } from '../../types';

interface GNBProps {
  page: PageType;
  user: User | null;
  onNavigate: (page: PageType) => void;
}

const NAV_ITEMS: [PageType, string][] = [
  ['home',    '홈'],
  ['all',     '전체글'],
  ['channel', '채널 탐색'],
];

export default function GNB({ page, user, onNavigate }: GNBProps) {
  const isHomeGroup = page === 'home' || page === 'hot' || page === 'detail';

  return (
    <nav className="gnb">
      <div className="gnb-inner">
        <div className="gnb-l">
          <span className="logo" onClick={() => onNavigate('home')}>커뮤니티</span>
          <div className="gnb-tabs">
            {NAV_ITEMS.map(([id, label]) => (
              <button
                key={id}
                className={'gtab' + ((id === 'home' ? isHomeGroup : page === id) ? ' active' : '')}
                onClick={() => onNavigate(id)}
              >
                {label}
              </button>
            ))}
          </div>
        </div>
        <div className="gnb-r">
          {user ? (
            <>
              <div className="nw">알림<span className="ndot" /></div>
              <div
                className="gnb-user"
                onClick={() => onNavigate('mypage')}
              >
                <div className="av">{user.username.charAt(0).toUpperCase()}</div>
                <span className="gnb-username">{user.username}</span>
              </div>
            </>
          ) : (
            <>
              <button className="gnb-login-btn" onClick={() => onNavigate('login')}>로그인</button>
              <button className="gnb-signup-btn" onClick={() => onNavigate('signup')}>회원가입</button>
            </>
          )}
        </div>
      </div>
    </nav>
  );
}
