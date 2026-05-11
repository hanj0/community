import type { PageType, User } from '../../types';
import { CHANNELS, TRENDING } from '../../constants/data';

interface SidebarProps {
  user: User | null;
  onNavigate: (page: PageType) => void;
}

const CHANNEL_COUNTS = [4231, 2891, 1540, 3012, 5821];

export default function Sidebar({ user, onNavigate }: SidebarProps) {
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
              <button className="wbtn">글쓰기</button>
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
              <button className="wbtn" onClick={() => onNavigate('login')}>로그인</button>
              <div className="abr">
                <button className="abtn" onClick={() => onNavigate('signup')}>회원가입</button>
              </div>
            </>
          )}
        </div>
      </div>

      <div className="card">
        <div className="chd"><span className="ct">채널</span></div>
        <div className="sbb">
          {CHANNELS.map((ch, i) => (
            <div className="chitem" key={ch.id}>
              <div className="chdot" style={{ background: ch.color }} />
              <span className="chnm">{ch.name}</span>
              <span className="chcnt">글 {CHANNEL_COUNTS[i].toLocaleString()}</span>
            </div>
          ))}
        </div>
      </div>

      <div className="card">
        <div className="chd"><span className="ct">실시간 키워드</span></div>
        <div className="sbb">
          {TRENDING.map((t, i) => (
            <div className="trow" key={i}>
              <span className="tnum">{i + 1}</span>
              <span className="twrd">{t.w}</span>
              <span className="tdlt">{t.d}</span>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
