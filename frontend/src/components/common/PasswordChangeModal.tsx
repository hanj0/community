import { useState } from 'react';
import { changePassword } from '../../api/auth';

interface PasswordChangeModalProps {
  onClose: () => void;
}

export default function PasswordChangeModal({ onClose }: PasswordChangeModalProps) {
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [done, setDone] = useState(false);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setError('');

    if (!currentPassword || !newPassword || !confirmPassword) {
      setError('모든 항목을 입력해주세요.');
      return;
    }
    if (newPassword !== confirmPassword) {
      setError('새 비밀번호가 일치하지 않습니다.');
      return;
    }
    if (newPassword === currentPassword) {
      setError('새 비밀번호는 현재 비밀번호와 달라야 합니다.');
      return;
    }

    setLoading(true);
    try {
      await changePassword({ currentPassword, newPassword });
      setDone(true);
    } catch (err) {
      setError(err instanceof Error ? err.message : '비밀번호 변경에 실패했습니다.');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-card" onClick={e => e.stopPropagation()}>
        <div className="modal-title">비밀번호 변경</div>

        {done ? (
          <>
            <div className="modal-done">비밀번호가 변경되었습니다.</div>
            <button className="auth-btn" onClick={onClose}>확인</button>
          </>
        ) : (
          <form onSubmit={handleSubmit}>
            {error && <div className="auth-err">{error}</div>}

            <div className="fgroup">
              <div>
                <label className="flabel" htmlFor="current-password">현재 비밀번호</label>
                <input
                  id="current-password"
                  type="password"
                  className={`finput${error ? ' err' : ''}`}
                  placeholder="현재 비밀번호"
                  value={currentPassword}
                  onChange={e => setCurrentPassword(e.target.value)}
                  autoComplete="current-password"
                />
              </div>
              <div>
                <label className="flabel" htmlFor="new-password">새 비밀번호</label>
                <input
                  id="new-password"
                  type="password"
                  className={`finput${error ? ' err' : ''}`}
                  placeholder="새 비밀번호"
                  value={newPassword}
                  onChange={e => setNewPassword(e.target.value)}
                  autoComplete="new-password"
                />
              </div>
              <div>
                <label className="flabel" htmlFor="confirm-password">새 비밀번호 확인</label>
                <input
                  id="confirm-password"
                  type="password"
                  className={`finput${error ? ' err' : ''}`}
                  placeholder="새 비밀번호 확인"
                  value={confirmPassword}
                  onChange={e => setConfirmPassword(e.target.value)}
                  autoComplete="new-password"
                />
              </div>
            </div>

            <div className="modal-actions">
              <button type="button" className="modal-btn-cancel" onClick={onClose} disabled={loading}>
                취소
              </button>
              <button type="submit" className="auth-btn" disabled={loading}>
                {loading ? '변경 중...' : '변경'}
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  );
}
