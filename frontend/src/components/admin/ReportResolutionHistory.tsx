import type { ReportHistoryEntry } from '../../types';
import { REASON_LABEL, RESOLUTION_LABEL } from '../../utils/reportLabels';

interface ReportResolutionHistoryProps {
  entries: ReportHistoryEntry[];
}

export default function ReportResolutionHistory({ entries }: ReportResolutionHistoryProps) {
  return (
    <section className="rpt-history">
      <div className="rpt-history-head">
        <h2>처리 이력</h2>
        <span>{entries.length}건</span>
      </div>

      <div className="rpt-history-list">
        {entries.map(entry => (
          <article className="rpt-drawer-card rpt-history-round" key={entry.handledAt}>
            <div className="rpt-history-round-head">
              <span className={'rpt-status resolved' + (entry.resolution === 'REJECTED' ? ' muted' : '')}>
                {RESOLUTION_LABEL[entry.resolution]}
              </span>
              <span className="rpt-history-round-count">신고 {entry.reportCount}건</span>
              <span className="rpt-history-round-meta">{entry.handledAt}</span>
            </div>

            <dl className="rpt-drawer-result">
              <dt>주요 사유</dt>
              <dd>{REASON_LABEL[entry.mainReason]}</dd>
              <dt>신고 기간</dt>
              <dd>{entry.firstReportedAt} ~ {entry.lastReportedAt}</dd>
              <dt>처리자</dt>
              <dd>{entry.handledBy}</dd>
              <dt>메모</dt>
              <dd>{entry.handledMemo || '—'}</dd>
            </dl>
          </article>
        ))}
      </div>
    </section>
  );
}
