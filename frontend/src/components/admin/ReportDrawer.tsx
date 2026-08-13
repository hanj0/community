import { Fragment, useEffect, useRef, useState } from 'react';
import type {
  ReportDetail,
  ReportResolution,
  ReportResolveRequest,
  SanctionReason,
  SanctionType,
} from '../../types';
import {
  REASON_LABEL,
  RESOLUTION_LABEL,
  SANCTION_REASON_GROUPS,
  SANCTION_REASON_LABEL,
  SANCTION_TYPE_LABEL,
  TARGET_LABEL,
} from '../../utils/reportLabels';

interface ReportDrawerProps {
  detail: ReportDetail;
  hasNextPending: boolean;
  nextPendingCount: number;
  onClose: () => void;
  onResolve: (result: ReportResolveRequest) => Promise<void>;
  onNextPending: () => void;
}

type SanctionChoice = SanctionType | 'NONE';

const CONTENT_ACTION_OPTIONS: { value: ReportResolution; label: string; danger?: boolean }[] = [
  { value: 'REJECTED', label: RESOLUTION_LABEL.REJECTED },
  { value: 'CONTENT_DELETED', label: RESOLUTION_LABEL.CONTENT_DELETED, danger: true },
];

const SANCTION_TYPE_OPTIONS: { value: SanctionChoice; label: string; danger?: boolean }[] = [
  { value: 'NONE', label: '제재 없음' },
  { value: 'WARNING', label: SANCTION_TYPE_LABEL.WARNING },
  { value: 'WRITE_BLOCK', label: SANCTION_TYPE_LABEL.WRITE_BLOCK, danger: true },
  { value: 'BAN', label: SANCTION_TYPE_LABEL.BAN, danger: true },
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
  const [contentAction, setContentAction] = useState<ReportResolution>('CONTENT_DELETED');
  const [reasonGroup, setReasonGroup] = useState('');
  const [reason, setReason] = useState<SanctionReason | ''>('');
  const [reasonDetail, setReasonDetail] = useState('');
  const [sanctionType, setSanctionType] = useState<SanctionChoice>('NONE');
  const [durationDays, setDurationDays] = useState('');
  const [sanctionMemo, setSanctionMemo] = useState('');
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [submitError, setSubmitError] = useState<string | null>(null);
  const [result, setResult] = useState<ReportResolveRequest | null>(null);

  const nextBtnRef = useRef<HTMLButtonElement>(null);
  const closeBtnRef = useRef<HTMLButtonElement>(null);

  const showResultView = result !== null;
  const reasonDisabled = contentAction === 'REJECTED';
  const activeGroup = SANCTION_REASON_GROUPS.find(g => g.label === reasonGroup) ?? null;
  const sanctionActive = sanctionType !== 'NONE';
  const durationRequired = sanctionType === 'WRITE_BLOCK';

  const handleReasonGroupChange = (label: string) => {
    setReasonGroup(label);
    setReason('');
  };

  const canSubmit =
    (reasonDisabled || reason !== '') &&
    reasonDetail.trim().length > 0 &&
    (!sanctionActive || (sanctionMemo.trim().length > 0 && (!durationRequired || Number(durationDays) > 0)));

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

  const buildRequest = (): ReportResolveRequest => ({
    reportIds: detail.reportIds,
    contentActionType: contentAction,
    reason: reasonDisabled ? null : (reason as SanctionReason),
    reasonDetail: reasonDetail.trim(),
    sanction: sanctionActive
      ? {
          sanctionType,
          durationDays: durationRequired ? Number(durationDays) : null,
          memo: sanctionMemo.trim(),
        }
      : null,
  });

  const handleConfirm = async () => {
    const built = buildRequest();
    setSubmitting(true);
    setSubmitError(null);
    try {
      await onResolve(built);
      setResult(built);
      setConfirmOpen(false);
    } catch (e) {
      setSubmitError(e instanceof Error ? e.message : '처리에 실패했습니다.');
    } finally {
      setSubmitting(false);
    }
  };

  const commitLabel = [
    RESOLUTION_LABEL[contentAction],
    sanctionActive ? SANCTION_TYPE_LABEL[sanctionType] : null,
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
          <span className="rpt-drawer-tag crit">{REASON_LABEL[detail.mainReason]}</span>
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

          {!showResultView && (
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
                  <div className={'rpt-drawer-field' + (reasonDisabled ? ' is-off' : '')}>
                    <span className="rpt-drawer-flabel">
                      처리 사유
                      {reasonDisabled && (
                        <span className="rpt-drawer-off-why"> — 반려는 위반이 아니라는 판단이라 사유를 고르지 않습니다</span>
                      )}
                    </span>
                    <span className="rpt-drawer-seg-group-label">대분류</span>
                    <Seg
                      options={SANCTION_REASON_GROUPS.map(g => ({ value: g.label, label: g.label }))}
                      value={reasonGroup}
                      onChange={handleReasonGroupChange}
                      disabled={reasonDisabled}
                    />
                    {activeGroup && (
                      <>
                        <span className="rpt-drawer-seg-group-label">세부 사유</span>
                        <Seg
                          options={activeGroup.options.map(opt => ({ value: opt, label: SANCTION_REASON_LABEL[opt] }))}
                          value={reason}
                          onChange={setReason}
                          disabled={reasonDisabled}
                        />
                      </>
                    )}
                  </div>
                  <div className="rpt-drawer-field">
                    <label className="rpt-drawer-flabel" htmlFor="rpt-reason-detail">처리 사유 상세</label>
                    <textarea
                      id="rpt-reason-detail"
                      className="rpt-drawer-textarea"
                      placeholder="판단 근거 — 이용자 항의 시 유일한 근거가 됩니다"
                      value={reasonDetail}
                      onChange={e => setReasonDetail(e.target.value)}
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
                    <Seg options={SANCTION_TYPE_OPTIONS} value={sanctionType} onChange={setSanctionType} />
                  </div>
                  {durationRequired && (
                    <div className="rpt-drawer-field">
                      <label className="rpt-drawer-flabel" htmlFor="rpt-duration">제한 기간(일)</label>
                      <input
                        id="rpt-duration"
                        type="number"
                        min={1}
                        className="rpt-drawer-select"
                        value={durationDays}
                        onChange={e => setDurationDays(e.target.value)}
                      />
                    </div>
                  )}
                  <div className={'rpt-drawer-field' + (sanctionActive ? '' : ' is-off')}>
                    <label className="rpt-drawer-flabel" htmlFor="rpt-sanction-memo">
                      제재 메모
                      {!sanctionActive && (
                        <span className="rpt-drawer-off-why"> — 제재 없음을 골라 메모가 필요하지 않습니다</span>
                      )}
                    </label>
                    <textarea
                      id="rpt-sanction-memo"
                      className="rpt-drawer-textarea"
                      placeholder="수위 판단 근거 — 다음 제재 때 이력으로 남습니다"
                      value={sanctionMemo}
                      disabled={!sanctionActive}
                      onChange={e => setSanctionMemo(e.target.value)}
                    />
                  </div>
                </div>
              </section>
            </>
          )}

          {showResultView && result && (
            <section className="rpt-drawer-card">
              <span className="rpt-drawer-card-label">처리 결과</span>
              <dl className="rpt-drawer-result">
                <dt>콘텐츠</dt>
                <dd className="strong">{RESOLUTION_LABEL[result.contentActionType]}</dd>
                <dt>처리 사유</dt>
                <dd>{result.reason ? SANCTION_REASON_LABEL[result.reason] : '—'}</dd>
                <dt>사유 상세</dt>
                <dd>{result.reasonDetail || '—'}</dd>
                <dt>유저 제재</dt>
                <dd className="strong">
                  {result.sanction
                    ? SANCTION_TYPE_LABEL[result.sanction.sanctionType] +
                      (result.sanction.durationDays ? ` (${result.sanction.durationDays}일)` : '')
                    : '제재 없음'}
                </dd>
                <dt>제재 메모</dt>
                <dd>{result.sanction?.memo || '—'}</dd>
              </dl>
            </section>
          )}
        </div>

        <div className="rpt-drawer-foot">
          {!showResultView && (
            <button
              className="rpt-drawer-commit"
              type="button"
              disabled={!canSubmit}
              onClick={() => setConfirmOpen(true)}
            >
              {commitLabel}
            </button>
          )}
          {showResultView && (
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
          onClick={e => { if (e.target === e.currentTarget && !submitting) setConfirmOpen(false); }}
        >
          <div className="modal" role="dialog" aria-modal="true" aria-labelledby="rpt-confirm-title">
            <h2 id="rpt-confirm-title" className="modal-title">이대로 확정할까요</h2>
            <p className="modal-body">
              신고 {detail.reportCount}건이 함께 종결됩니다. 확정 후에는 되돌릴 수 없습니다.
            </p>
            <dl className="rpt-drawer-modal-summary">
              <dt>콘텐츠</dt>
              <dd>{RESOLUTION_LABEL[contentAction]}</dd>
              <dt>처리 사유</dt>
              <dd>{reasonDisabled ? '—' : reason ? SANCTION_REASON_LABEL[reason] : '—'}</dd>
              <dt>유저 제재</dt>
              <dd>
                {sanctionActive
                  ? SANCTION_TYPE_LABEL[sanctionType] + (durationRequired && durationDays ? ` (${durationDays}일)` : '')
                  : '제재 없음'}
              </dd>
            </dl>
            {submitError && <p className="auth-err">{submitError}</p>}
            <div className="modal-actions">
              <button className="modal-btn" type="button" disabled={submitting} onClick={() => setConfirmOpen(false)}>
                취소
              </button>
              <button className="modal-btn primary" type="button" disabled={submitting} onClick={handleConfirm}>
                {submitting ? '처리 중...' : '확정'}
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
}
