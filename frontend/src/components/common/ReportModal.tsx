import { useState } from 'react';
import type { ReportReason, ReportTargetType } from '../../types';
import { createReport } from '../../api/reports';

interface ReportModalProps {
  targetType: ReportTargetType;
  targetId: number;
  onClose: () => void;
}

const REASONS: { value: ReportReason; label: string; desc: string }[] = [
  { value: 'SPAM', label: '스팸/광고', desc: '홍보성 도배, 반복 게시물' },
  { value: 'ABUSE', label: '욕설/비방', desc: '모욕, 인신공격, 혐오 표현' },
  { value: 'SEXUAL', label: '음란물/선정성', desc: '노골적이거나 부적절한 성적 내용' },
  { value: 'ILLEGAL', label: '불법 정보', desc: '불법 거래, 저작권 침해 등' },
  { value: 'ETC', label: '기타', desc: '위 항목에 해당하지 않는 사유' },
];

const DETAIL_MAX = 300;

export default function ReportModal({ targetType, targetId, onClose }: ReportModalProps) {
  const [reason, setReason] = useState<ReportReason | null>(null);
  const [detail, setDetail] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [done, setDone] = useState(false);

  const targetLabel = targetType === 'POST' ? '게시글' : '댓글';
  const detailRequired = reason === 'ETC';

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setError('');

    if (!reason) {
      setError('신고 사유를 선택해주세요.');
      return;
    }
    if (detailRequired && !detail.trim()) {
      setError('기타 사유는 상세 내용을 입력해주세요.');
      return;
    }

    setLoading(true);
    try {
      await createReport({ targetType, targetId, reason, reasonDetail: detail.trim() });
      setDone(true);
    } catch (err) {
      setError(err instanceof Error ? err.message : '신고를 접수하지 못했습니다.');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-card" onClick={e => e.stopPropagation()}>
        <div className="modal-title">{targetLabel} 신고</div>

        {done ? (
          <>
            <div className="modal-done">
              신고가 접수되었습니다.
              <br />
              검토 후 조치될 예정입니다.
            </div>
            <button className="auth-btn" onClick={onClose}>확인</button>
          </>
        ) : (
          <form onSubmit={handleSubmit}>
            {error && <div className="auth-err">{error}</div>}

            <div className="report-reasons">
              {REASONS.map(r => (
                <label
                  key={r.value}
                  className={'report-reason' + (reason === r.value ? ' sel' : '')}
                >
                  <input
                    type="radio"
                    name="report-reason"
                    value={r.value}
                    checked={reason === r.value}
                    onChange={() => { setReason(r.value); setError(''); }}
                  />
                  <div className="report-reason-text">
                    <span className="report-reason-label">{r.label}</span>
                    <span className="report-reason-desc">{r.desc}</span>
                  </div>
                </label>
              ))}
            </div>

            <div className="report-detail">
              <label className="flabel" htmlFor="report-detail">
                상세 내용{detailRequired ? '' : ' (선택)'}
              </label>
              <textarea
                id="report-detail"
                className="finput report-detail-input"
                placeholder="신고 사유를 자세히 적어주세요."
                value={detail}
                onChange={e => setDetail(e.target.value.slice(0, DETAIL_MAX))}
                rows={3}
              />
              <div className="report-detail-count">{detail.length}/{DETAIL_MAX}</div>
            </div>

            <div className="modal-actions">
              <button type="button" className="modal-btn-cancel" onClick={onClose} disabled={loading}>
                취소
              </button>
              <button type="submit" className="auth-btn" disabled={loading || !reason}>
                {loading ? '접수 중...' : '신고하기'}
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  );
}
