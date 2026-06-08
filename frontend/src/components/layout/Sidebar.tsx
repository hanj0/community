import { useNavigate, useLocation, useSearchParams } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { useChannels } from '../../hooks/useChannels';

export default function Sidebar() {
  const navigate = useNavigate();
  const { pathname } = useLocation();
  const [searchParams] = useSearchParams();
  const { user } = useAuth();
  const channels = useChannels();

  const activeChannelId = pathname === '/all' ? (searchParams.get('channel') ?? 'all') : null;

  return (
    <div className="sidebar">
      <div className="card">
        <div className="ubx">
          {user ? (
            <>
              <div className="urw">
                <div className="av">{user.username.charAt(0).toUpperCase()}</div>
                <div>
                  <div className="unm">{user.username}</div>
                  <div className="usc">활동점수 0</div>
                </div>
              </div>
              <button className="wbtn" onClick={() => navigate('/write')}>글쓰기</button>
              <div className="abr">
                <button className="abtn">북마크</button>
                <button className="abtn">알림 ●</button>
              </div>
            </>
          ) : (
            <>
              <div style={{ fontSize: 13, color: 'var(--t2)', marginBottom: 8 }}>
                로그인하고 더 많은 기능을 이용해보세요
              </div>
              <button className="wbtn" onClick={() => navigate('/login')}>로그인</button>
              <div className="abr">
                <button className="abtn" onClick={() => navigate('/signup')}>회원가입</button>
              </div>
            </>
          )}
        </div>
      </div>

      <div className="card">
        <div className="chd"><span className="ct">채널</span></div>
        <div className="sbb">
          {channels.map(ch => (
            <div
              className={'chitem clickable' + (activeChannelId === ch.id ? ' active' : '')}
              key={ch.id}
              onClick={() => navigate(`/all?channel=${ch.id}&sort=latest`)}
            >
              <div className="chdot" style={{ background: ch.color }} />
              <span className="chnm">{ch.name}</span>
              {ch.postCount !== undefined && (
                <span className="chcnt">글 {ch.postCount.toLocaleString()}</span>
              )}
            </div>
          ))}
          {channels.length === 0 && (
            <div style={{ fontSize: 12, color: 'var(--t3)', padding: '8px 0' }}>채널 로딩 중...</div>
          )}
        </div>
      </div>
    </div>
  );
}
