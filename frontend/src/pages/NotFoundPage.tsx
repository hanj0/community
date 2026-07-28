import { useNavigate } from 'react-router-dom';

export default function NotFoundPage() {
  const navigate = useNavigate();

  return (
    <div className="nf-page">
      <div className="nf-code">404</div>
      <div className="nf-title">페이지를 찾을 수 없습니다</div>
      <p className="nf-desc">주소가 잘못되었거나 삭제된 페이지입니다.</p>
      <button className="nf-btn" onClick={() => navigate('/')}>홈으로</button>
    </div>
  );
}
