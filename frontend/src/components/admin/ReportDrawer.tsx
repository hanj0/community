import { Fragment, useEffect, useRef, useState } from 'react';
import type {
  ReportContentAction,
  ReportDetail,
  ReportProcessResult,
  ReportReason,
  ReportUserAction,
  ReportUserActionReason,
} from '../../types';
import {
  CONTENT_ACTION_LABEL,
  REASON_LABEL,
  RESOLUTION_LABEL,
  TARGET_LABEL,
  USER_ACTION_LABEL,
  USER_ACTION_REASON_LABEL,
} from '../../utils/reportLabels';

interface ReportDrawerProps {
  detail: ReportDetail;
  hasNextPending: boolean;
  nextPendingCount: number;
  onClose: () => void;
  onResolve: (result: ReportProcessResult) => void;
  onNextPending: () => void;
}

const CONTENT_ACTION_OPTIONS: { value: ReportContentAction; label: string; danger?: boolean }[] = [
  { value: 'REJECTED', label: CONTENT_ACTION_LABEL.REJECTED },
  { value: 'CONTENT_DELETED', label: CONTENT_ACTION_LABEL.CONTENT_DELETED, danger: true },
  { value: 'TARGET_ALREADY_DELETED', label: CONTENT_ACTION_LABEL.TARGET_ALREADY_DELETED },
];

const CONTENT_REASON_OPTIONS: ReportReason[] = ['SEXUAL', 'SPAM', 'ABUSE', 'ILLEGAL', 'ETC'];

const USER_ACTION_OPTIONS: { value: ReportUserAction; label: string; danger?: boolean }[] = [
  { value: 'NONE', label: USER_ACTION_LABEL.NONE },
  { value: 'WARNING', label: USER_ACTION_LABEL.WARNING },
  { value: 'SUSPEND_7D', label: USER_ACTION_LABEL.SUSPEND_7D, danger: true },
  { value: 'PERMANENT_BAN', label: USER_ACTION_LABEL.PERMANENT_BAN, danger: true },
];

const USER_REASON_OPTIONS: ReportUserActionReason[] = [
  'REPEATED',
  'SEVERE_ONCE',
  'MULTIPLE_CONTENT',
  'COMBINED',
  'ETC',
];

function Seg<T extends string>({
  options,
  value,
  onChange,
  disabled,
}: {
  options: { value: T; label: string; danger?: boolean }[];
  value: T;
  onChange: (v: T) => void;
  disabled?: boolean;
}) {
  return (
    <div className="rpt-drawer-seg">
      {options.map(opt => (
        <button
          key={opt.value}
          type="button"
          className={'rpt-drawer-seg-btn' + (opt.danger ? ' danger' : '')}
          aria-pressed={value === opt.value}
          disabled={disabled}
          onClick={() => onChange(opt.value)}
        >
          {opt.label}
        </button>
      ))}
    </div>
  );
}

export default function ReportDrawer({
  detail,
  hasNextPending,
  nextPendingCount,
  onClose,
  onResolve,
  onNextPending,
}: ReportDrawerProps) {
  const [contentAction, setContentAction] = useState<ReportContentAction>('CONTENT_DELETED');
  const [contentReason, setContentReason] = useState<ReportReason>(detail.mainReason);
  const [userAction, setUserAction] = useState<ReportUserAction>('NONE');
  const [userReason, setUserReason] = useState<ReportUserActionReason>('REPEATED');
  const [memo, setMemo] = useState('');
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [result, setResult] = useState<ReportProcessResult | null>(null);

  const nextBtnRef = useRef<HTMLButtonElement>(null);
  const closeBtnRef = useRef<HTMLButtonElement>(null);

  const isHistory = detail.status === 'RESOLVED';
  const showResultView = result !== null;
  const contentReasonDisabled = contentAction === 'REJECTED';
  const userReasonDisabled = userAction === 'NONE';

  useEffect(() => {
    const onKeyDown = (e: KeyboardEvent) => {
      if (e.key !== 'Escape') return;
      if (confirmOpen) setConfirmOpen(false);
      else onClose();
    };
    document.addEventListener('keydown', onKeyDown);
    return () => document.removeEventListener('keydown', onKeyDown);
  }, [confirmOpen, onClose]);

  useEffect(() => {
    if (!result) return;
    (hasNextPending ? nextBtnRef.current : closeBtnRef.current)?.focus();
  }, [result, hasNextPending]);

  const handleConfirm = () => {
    const built: ReportProcessResult = {
      contentAction,
      contentActionReason: contentReasonDisabled ? null : contentReason,
      userAction,
      userActionReason: userReasonDisabled ? null : userReason,
      memo: memo.trim(),
    };
    setResult(built);
    setConfirmOpen(false);
    onResolve(built);
  };

  const commitLabel = [
    CONTENT_ACTION_LABEL[contentAction],
    userAction !== 'NONE' ? USER_ACTION_LABEL[userAction] : null,
    `신고 ${detail.reportCount}건 종결`,
  ]
    .filter(Boolean)
    .join(' · ');

  const maxDist = detail.reasonDistribution[0]?.reasonCount ?? 1;

  return (
    <>
      <div className="rpt-drawer-scrim" onClick={() => { if (!confirmOpen) onClose(); }} />
      <aside className="rpt-drawer" aria-label="신고 처리" role="dialog" aria-modal="true">
        <div className="rpt-drawer-head">
          <span className="rpt-drawer-tag crit">{detail.mainReason}</span>
          <span className="rpt-drawer-id">
            {TARGET_LABEL[detail.targetType]} <b>#{detail.targetId}</b>
          </span>
          {detail.edited && <span className="rpt-drawer-tag warn">신고 후 수정됨</span>}
          <button className="rpt-drawer-close" type="button" aria-label="닫기" onClick={onClose}>✕</button>
        </div>

        <div className="rpt-drawer-body">
          {showResultView && (
            <div className="rpt-drawer-success" role="status" aria-live="polite">
              <span className="rpt-drawer-success-check" aria-hidden="true">✓</span>
              <div className="rpt-drawer-success-text">
                <strong>처리 완료</strong>
                <span>신고 {detail.reportCount}건 함께 종결됐습니다</span>
              </div>
            </div>
          )}

          <div className="rpt-drawer-count">
            <span className="rpt-drawer-count-n">
              {detail.reportCount}
              <span>건</span>
            </span>
            <span className="rpt-drawer-count-meta">
              <span>최초 신고 <b>{detail.firstReportedAt}</b></span>
              <span>최근 신고 <b>{detail.lastReportedAt}</b></span>
            </span>
          </div>

          <section className="rpt-drawer-card">
            <span className="rpt-drawer-card-label">신고 시점 스냅샷</span>
            <blockquote className="rpt-drawer-snap">{detail.contentSnapshot}</blockquote>
          </section>

          <section className="rpt-drawer-card">
            <span className="rpt-drawer-card-label">신고 사유 분포</span>
            <div className="rpt-drawer-dist">
              {detail.reasonDistribution.map((d, i) => (
                <Fragment key={d.reason}>
                  <span className="rpt-drawer-dist-k">{REASON_LABEL[d.reason]}</span>
                  <span className={'rpt-drawer-dist-bar' + (i === 0 ? ' top' : '')}>
                    <i style={{ width: `${Math.round((d.reasonCount / maxDist) * 100)}%` }} />
                  </span>
                  <span className="rpt-drawer-dist-n">{d.reasonCount}</span>
                </Fragment>
              ))}
            </div>
          </section>

          {!showResultView && !isHistory && (
            <>
              <section className="rpt-drawer-card">
                <div className="rpt-drawer-act-head">
                  <h2>콘텐츠 조치</h2>
                  <span className="rpt-drawer-act-hint">
                    이 {TARGET_LABEL[detail.targetType]} 하나에만 적용됩니다
                  </span>
                </div>
                <div className="rpt-drawer-act-body">
                  <div className="rpt-drawer-field">
                    <span className="rpt-drawer-flabel">조치</span>
                    <Seg options={CONTENT_ACTION_OPTIONS} value={contentAction} onChange={setContentAction} />
                  </div>
                  <div className={'rpt-drawer-field' + (contentReasonDisabled ? ' is-off' : '')}>
                    <span className="rpt-drawer-flabel">
                      콘텐츠 조치 사유
                      {contentReasonDisabled && (
                        <span className="rpt-drawer-off-why"> — 반려는 위반이 아니라는 판단이라 사유를 고르지 않습니다</span>
                      )}
                    </span>
                    <Seg
                      options={CONTENT_REASON_OPTIONS.map(r => ({ value: r, label: REASON_LABEL[r] }))}
                      value={contentReason}
                      onChange={setContentReason}
                      disabled={contentReasonDisabled}
                    />
                  </div>
                  <div className="rpt-drawer-field">
                    <label className="rpt-drawer-flabel" htmlFor="rpt-content-memo">메모</label>
                    <textarea
                      id="rpt-content-memo"
                      className="rpt-drawer-textarea"
                      placeholder="판단 근거 — 이용자 항의 시 유일한 근거가 됩니다"
                      value={memo}
                      onChange={e => setMemo(e.target.value)}
                    />
                  </div>
                </div>
              </section>

              <section className="rpt-drawer-card">
                <span className="rpt-drawer-card-label">피신고자</span>
                <div className="rpt-drawer-who">
                  <span className="rpt-drawer-avatar" aria-hidden="true">
                    {detail.reportedUsername.charAt(0).toUpperCase()}
                  </span>
                  <div className="rpt-drawer-who-main">
                    <div className="rpt-drawer-who-name">{detail.reportedUsername}</div>
                    <div className="rpt-drawer-who-state">
                      <span className="k">현재 상태</span>
                      <span className={'v' + ((detail.reportedUserPriorSanctions ?? 0) > 0 ? ' warn' : '')}>
                        {detail.reportedUserState?.label ?? '정보 없음'}
                      </span>
                    </div>
                  </div>
                </div>
              </section>

              <section className="rpt-drawer-card">
                <div className="rpt-drawer-act-head">
                  <h2>유저 제재</h2>
                </div>
                {detail.suggestion && <p className="rpt-drawer-suggest">{detail.suggestion}</p>}
                <div className="rpt-drawer-act-body">
                  <div className="rpt-drawer-field">
                    <span className="rpt-drawer-flabel">제재 수위</span>
                    <Seg options={USER_ACTION_OPTIONS} value={userAction} onChange={setUserAction} />
                  </div>
                  <div className={'rpt-drawer-field' + (userReasonDisabled ? ' is-off' : '')}>
                    <span className="rpt-drawer-flabel">
                      유저 제재 사유
                      {userReasonDisabled && (
                        <span className="rpt-drawer-off-why"> — 제재 없음을 골라 사유가 필요하지 않습니다</span>
                      )}
                    </span>
                    <Seg
                      options={USER_REASON_OPTIONS.map(r => ({ value: r, label: USER_ACTION_REASON_LABEL[r] }))}
                      value={userReason}
                      onChange={setUserReason}
                      disabled={userReasonDisabled}
                    />
                  </div>
                  <div className="rpt-drawer-field">
                    <label className="rpt-drawer-flabel" htmlFor="rpt-user-memo">메모</label>
                    <textarea
                      id="rpt-user-memo"
                      className="rpt-drawer-textarea"
                      placeholder="수위 판단 근거 — 다음 제재 때 이력으로 남습니다"
                      value={memo}
                      onChange={e => setMemo(e.target.value)}
                    />
                  </div>
                </div>
              </section>
            </>
          )}

          {isHistory && !showResultView && (
            <section className="rpt-drawer-card rpt-drawer-history-note">
              <span className="rpt-drawer-card-label">처리 결과</span>
              <p>
                {detail.resolution ? RESOLUTION_LABEL[detail.resolution] : '처리완료'}로 종결된 신고입니다.
                세부 처리 이력(조치 사유, 제재, 메모)은 상세 API가 준비되는 대로 표시됩니다.
              </p>
            </section>
          )}

          {showResultView && result && (
            <section className="rpt-drawer-card">
              <span className="rpt-drawer-card-label">처리 결과</span>
              <dl className="rpt-drawer-result">
                <dt>콘텐츠</dt>
                <dd className="strong">{CONTENT_ACTION_LABEL[result.contentAction]}</dd>
                <dt>조치 사유</dt>
                <dd>{result.contentActionReason ? REASON_LABEL[result.contentActionReason] : '—'}</dd>
                <dt>유저 제재</dt>
                <dd className="strong">{USER_ACTION_LABEL[result.userAction]}</dd>
                <dt>제재 사유</dt>
                <dd>{result.userActionReason ? USER_ACTION_REASON_LABEL[result.userActionReason] : '—'}</dd>
                <dt>메모</dt>
                <dd>{result.memo || '—'}</dd>
              </dl>
            </section>
          )}
        </div>

        <div className="rpt-drawer-foot">
          {!showResultView && !isHistory && (
            <button className="rpt-drawer-commit" type="button" onClick={() => setConfirmOpen(true)}>
              {commitLabel}
            </button>
          )}
          {(showResultView || isHistory) && (
            <div className="rpt-drawer-result-foot">
              {!hasNextPending && <span className="rpt-drawer-foot-note">대기 중인 신고가 없습니다</span>}
              <button ref={closeBtnRef} className="modal-btn" type="button" onClick={onClose}>닫기</button>
              {hasNextPending && (
                <button ref={nextBtnRef} className="modal-btn primary" type="button" onClick={onNextPending}>
                  다음 대기 신고 처리 ({nextPendingCount}건 남음)
                </button>
              )}
            </div>
          )}
        </div>
      </aside>

      {confirmOpen && (
        <div
          className="modal-overlay"
          onClick={e => { if (e.target === e.currentTarget) setConfirmOpen(false); }}
        >
          <div className="modal" role="dialog" aria-modal="true" aria-labelledby="rpt-confirm-title">
            <h2 id="rpt-confirm-title" className="modal-title">이대로 확정할까요</h2>
            <p className="modal-body">
              신고 {detail.reportCount}건이 함께 종결됩니다. 확정 후에는 되돌릴 수 없습니다.
            </p>
            <dl className="rpt-drawer-modal-summary">
              <dt>콘텐츠</dt>
              <dd>{CONTENT_ACTION_LABEL[contentAction]}</dd>
              <dt>조치 사유</dt>
              <dd>{contentReasonDisabled ? '—' : REASON_LABEL[contentReason]}</dd>
              <dt>유저 제재</dt>
              <dd>{USER_ACTION_LABEL[userAction]}</dd>
              <dt>제재 사유</dt>
              <dd>{userReasonDisabled ? '—' : USER_ACTION_REASON_LABEL[userReason]}</dd>
            </dl>
            <div className="modal-actions">
              <button className="modal-btn" type="button" onClick={() => setConfirmOpen(false)}>취소</button>
              <button className="modal-btn primary" type="button" onClick={handleConfirm}>확정</button>
            </div>
          </div>
        </div>
      )}
    </>
  );
}
