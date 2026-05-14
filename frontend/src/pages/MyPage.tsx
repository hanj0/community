import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

function formatDate(iso: string) {
  return new Date(iso).toLocaleDateString('ko-KR', { year: 'numeric', month: 'long', day: 'numeric' });
}

export default function MyPage() {
  const navigate = useNavigate();
  const { user, logout } = useAuth();

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
        <div style={{ fontSize: 12, color: 'var(--t3)', marginBottom: 16 }}>
          가입일 {formatDate(user.createdAt)}
        </div>
        <div className="mypg-stats">
          <div className="mypg-stat">
            <div className="mypg-stat-v">0</div>
            <div className="mypg-stat-l">작성글</div>
          </div>
          <div className="mypg-stat">
            <div className="mypg-stat-v">0</div>
            <div className="mypg-stat-l">받은 좋아요</div>
          </div>
          <div className="mypg-stat">
            <div className="mypg-stat-v">0</div>
            <div className="mypg-stat-l">댓글</div>
          </div>
        </div>
      </div>

      <div className="mypg-sec">
        <div className="mypg-sch">활동</div>
        <div className="mypg-act" onClick={() => navigate('/')}>
          <span className="mypg-act-ic">📝</span>
          <span className="mypg-act-lbl">내가 쓴 글</span>
          <span className="mypg-act-val">0개 &rsaquo;</span>
        </div>
        <div className="mypg-act" onClick={() => navigate('/')}>
          <span className="mypg-act-ic">💬</span>
          <span className="mypg-act-lbl">내 댓글</span>
          <span className="mypg-act-val">0개 &rsaquo;</span>
        </div>
        <div className="mypg-act" onClick={() => navigate('/')}>
          <span className="mypg-act-ic">🔖</span>
          <span className="mypg-act-lbl">북마크</span>
          <span className="mypg-act-val">0개 &rsaquo;</span>
        </div>
      </div>

      <div className="mypg-sec">
        <div className="mypg-sch">설정</div>
        <div className="mypg-act">
          <span className="mypg-act-ic">🔔</span>
          <span className="mypg-act-lbl">알림 설정</span>
          <span className="mypg-act-val">&rsaquo;</span>
        </div>
        <div className="mypg-act">
          <span className="mypg-act-ic">🔐</span>
          <span className="mypg-act-lbl">비밀번호 변경</span>
          <span className="mypg-act-val">&rsaquo;</span>
        </div>
      </div>

      <button className="mypg-logout" onClick={handleLogout}>로그아웃</button>
    </div>
  );
}
