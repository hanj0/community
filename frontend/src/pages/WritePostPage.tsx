import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { createPost } from '../api/posts';
import { useChannels } from '../hooks/useChannels';

export default function WritePostPage() {
  const navigate = useNavigate();
  const channels = useChannels();
  const [form, setForm] = useState({ title: '', channelId: '', content: '' });
  const [errors, setErrors] = useState<Partial<typeof form>>({});
  const [serverError, setServerError] = useState('');
  const [loading, setLoading] = useState(false);

  function set(field: keyof typeof form) {
    return (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
      setForm(prev => ({ ...prev, [field]: e.target.value }));
      setErrors(prev => ({ ...prev, [field]: '' }));
    };
  }

  function validate(): boolean {
    const next: Partial<typeof form> = {};
    if (!form.title.trim()) next.title = '제목을 입력해주세요.';
    else if (form.title.trim().length < 2) next.title = '제목은 2자 이상이어야 합니다.';
    if (!form.channelId) next.channelId = '채널을 선택해주세요.';
    if (!form.content.trim()) next.content = '내용을 입력해주세요.';
    setErrors(next);
    return Object.keys(next).length === 0;
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setServerError('');
    if (!validate()) return;

    setLoading(true);
    try {
      await createPost({
        title: form.title.trim(),
        channelId: Number(form.channelId),
        content: form.content.trim(),
      });
      navigate('/all');
    } catch (err) {
      setServerError(err instanceof Error ? err.message : '게시글 등록에 실패했습니다.');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="auth-wrap">
      <div className="auth-card" style={{ maxWidth: 640 }}>
        <div className="auth-title">글쓰기</div>

        {serverError && <div className="auth-err">{serverError}</div>}

        <form onSubmit={handleSubmit}>
          <div className="fgroup">
            <div>
              <label className="flabel" htmlFor="post-channel">채널</label>
              <select
                id="post-channel"
                className={`finput${errors.channelId ? ' err' : ''}`}
                value={form.channelId}
                onChange={set('channelId')}
              >
                <option value="">채널을 선택하세요</option>
                {channels.map(ch => (
                  <option key={ch.id} value={ch.id}>{ch.name}</option>
                ))}
              </select>
              {errors.channelId && <div className="ferr">{errors.channelId}</div>}
            </div>
            <div>
              <label className="flabel" htmlFor="post-title">제목</label>
              <input
                id="post-title"
                type="text"
                className={`finput${errors.title ? ' err' : ''}`}
                placeholder="제목을 입력하세요"
                value={form.title}
                onChange={set('title')}
              />
              {errors.title && <div className="ferr">{errors.title}</div>}
            </div>
            <div>
              <label className="flabel" htmlFor="post-content">내용</label>
              <textarea
                id="post-content"
                className={`finput${errors.content ? ' err' : ''}`}
                placeholder="내용을 입력하세요"
                value={form.content}
                onChange={set('content')}
                rows={10}
                style={{ resize: 'vertical' }}
              />
              {errors.content && <div className="ferr">{errors.content}</div>}
            </div>
          </div>

          <div style={{ display: 'flex', gap: 8 }}>
            <button
              type="button"
              className="auth-btn"
              style={{ background: 'var(--bg)', color: 'var(--t2)', border: '.5px solid var(--b)' }}
              onClick={() => navigate('/all')}
            >
              취소
            </button>
            <button type="submit" className="auth-btn" disabled={loading}>
              {loading ? '등록 중...' : '등록하기'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
