import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { signup } from '../api/auth';
import { useAuth } from '../context/AuthContext';

export default function SignupPage() {
  const navigate = useNavigate();
  const { setUser } = useAuth();
  const [form, setForm] = useState({ username: '', email: '', password: '', confirm: '' });
  const [errors, setErrors] = useState<Partial<typeof form>>({});
  const [serverError, setServerError] = useState('');
  const [loading, setLoading] = useState(false);

  function set(field: keyof typeof form) {
    return (e: React.ChangeEvent<HTMLInputElement>) => {
      setForm(prev => ({ ...prev, [field]: e.target.value }));
      setErrors(prev => ({ ...prev, [field]: '' }));
    };
  }

  function validate(): boolean {
    const next: Partial<typeof form> = {};
    if (!form.username.trim()) next.username = '닉네임을 입력해주세요.';
    else if (form.username.trim().length < 2) next.username = '닉네임은 2자 이상이어야 합니다.';
    if (!form.email.trim()) next.email = '이메일을 입력해주세요.';
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) next.email = '올바른 이메일 형식이 아닙니다.';
    if (!form.password) next.password = '비밀번호를 입력해주세요.';
    else if (form.password.length < 8) next.password = '비밀번호는 8자 이상이어야 합니다.';
    if (!form.confirm) next.confirm = '비밀번호 확인을 입력해주세요.';
    else if (form.password !== form.confirm) next.confirm = '비밀번호가 일치하지 않습니다.';
    setErrors(next);
    return Object.keys(next).length === 0;
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setServerError('');
    if (!validate()) return;

    setLoading(true);
    try {
      const user = await signup({
        username: form.username.trim(),
        email: form.email.trim(),
        password: form.password,
      });
      setUser(user);
      navigate('/', { replace: true });
    } catch (err) {
      setServerError(err instanceof Error ? err.message : '회원가입에 실패했습니다.');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="auth-wrap">
      <div className="auth-card">
        <div className="auth-title">회원가입</div>
        <div className="auth-sub">커뮤니티 계정을 만들어보세요</div>

        {serverError && <div className="auth-err">{serverError}</div>}

        <form onSubmit={handleSubmit}>
          <div className="fgroup">
            <div>
              <label className="flabel" htmlFor="username">닉네임</label>
              <input
                id="username"
                type="text"
                className={`finput${errors.username ? ' err' : ''}`}
                placeholder="사용할 닉네임을 입력하세요"
                value={form.username}
                onChange={set('username')}
                autoComplete="nickname"
              />
              {errors.username && <div className="ferr">{errors.username}</div>}
            </div>
            <div>
              <label className="flabel" htmlFor="su-email">이메일</label>
              <input
                id="su-email"
                type="email"
                className={`finput${errors.email ? ' err' : ''}`}
                placeholder="example@email.com"
                value={form.email}
                onChange={set('email')}
                autoComplete="email"
              />
              {errors.email && <div className="ferr">{errors.email}</div>}
            </div>
            <div>
              <label className="flabel" htmlFor="su-password">비밀번호</label>
              <input
                id="su-password"
                type="password"
                className={`finput${errors.password ? ' err' : ''}`}
                placeholder="8자 이상 입력하세요"
                value={form.password}
                onChange={set('password')}
                autoComplete="new-password"
              />
              {errors.password && <div className="ferr">{errors.password}</div>}
            </div>
            <div>
              <label className="flabel" htmlFor="confirm">비밀번호 확인</label>
              <input
                id="confirm"
                type="password"
                className={`finput${errors.confirm ? ' err' : ''}`}
                placeholder="비밀번호를 다시 입력하세요"
                value={form.confirm}
                onChange={set('confirm')}
                autoComplete="new-password"
              />
              {errors.confirm && <div className="ferr">{errors.confirm}</div>}
            </div>
          </div>

          <button type="submit" className="auth-btn" disabled={loading}>
            {loading ? '처리 중...' : '회원가입'}
          </button>
        </form>

        <div className="auth-link">
          이미 계정이 있으신가요?{' '}
          <button onClick={() => navigate('/login')}>로그인</button>
        </div>
      </div>
    </div>
  );
}
