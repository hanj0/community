import { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import type {
  PageMeta,
  ReportDetail,
  ReportHistoryEntry,
  ReportSortType,
  ReportStatus,
  ReportSummary,
} from '../types';
import { fetchAdminReportDetail, fetchAdminReportHistory, fetchAdminReports } from '../api/adminReports';
import Pagination from '../components/common/Pagination';
import ReportDrawer from '../components/admin/ReportDrawer';
import ReportHistoryDrawer from '../components/admin/ReportHistoryDrawer';
import { REASON_LABEL, RESOLUTION_LABEL, TARGET_LABEL } from '../utils/reportLabels';

const PAGE_SIZE = 20;

const TABS: { value: ReportStatus; label: string }[] = [
  { value: 'PENDING', label: '대기' },
  { value: 'RESOLVED', label: '처리완료' },
];

function reportKey(r: Pick<ReportSummary, 'targetType' | 'targetId'>): string {
  return `${r.targetType}-${r.targetId}`;
}

function isTab(value: string | null): value is ReportStatus {
  return TABS.some(t => t.value === value);
}

function formatReportedAt(iso: string): string {
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) return iso;
  const pad = (n: number) => String(n).padStart(2, '0');
  return `${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
}

function StatusBadge({ report }: { report: ReportSummary }) {
  if (report.status === 'PENDING') {
    return <span className="rpt-status pending">대기</span>;
  }
  const resolution = report.resolution;
  if (!resolution) {
    return <span className="rpt-status done">처리완료</span>;
  }
  return (
    <span className={'rpt-status resolved' + (resolution === 'REJECTED' ? ' muted' : '')}>
      {RESOLUTION_LABEL[resolution]}
    </span>
  );
}

export default function AdminReportsPage() {
  const [searchParams, setSearchParams] = useSearchParams();

  const statusParam = searchParams.get('status');
  const status: ReportStatus = isTab(statusParam) ? statusParam : 'PENDING';
  const sort: ReportSortType = searchParams.get('sort') === 'latest' ? 'latest' : 'count';
  const currentPage = parseInt(searchParams.get('page') ?? '1', 10);

  const [reports, setReports] = useState<ReportSummary[]>([]);
  const [meta, setMeta] = useState<PageMeta>({ page: 0, size: PAGE_SIZE, total: 0, totalPages: 0 });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [activeReport, setActiveReport] = useState<ReportSummary | null>(null);
  const [leavingKey, setLeavingKey] = useState<string | null>(null);

  const [detail, setDetail] = useState<ReportDetail | null>(null);
  const [history, setHistory] = useState<ReportHistoryEntry[] | null>(null);
  const [detailLoading, setDetailLoading] = useState(false);
  const [detailError, setDetailError] = useState<string | null>(null);

  useEffect(() => {
    let active = true;
    async function load() {
      setLoading(true);
      setError(null);
      try {
        const res = await fetchAdminReports({ status, sort, page: currentPage - 1, size: PAGE_SIZE });
        if (active) { setReports(res.data); setMeta(res.meta); }
      } catch (e: unknown) {
        if (active) {
          setReports([]);
          setError(e instanceof Error ? e.message : '오류가 발생했습니다.');
        }
      } finally {
        if (active) setLoading(false);
      }
    }
    void load();
    return () => { active = false; };
  }, [status, sort, currentPage]);

  useEffect(() => {
    if (!activeReport) {
      setDetail(null);
      setHistory(null);
      setDetailError(null);
      return;
    }
    let active = true;
    setDetail(null);
    setHistory(null);
    setDetailError(null);
    setDetailLoading(true);

    const request = activeReport.status === 'PENDING'
      ? fetchAdminReportDetail(activeReport).then(d => { if (active) setDetail(d); })
      : fetchAdminReportHistory(activeReport.targetType, activeReport.targetId)
        .then(h => { if (active) setHistory(h); });

    request
      .catch((e: unknown) => {
        if (active) setDetailError(e instanceof Error ? e.message : '신고 상세를 불러올 수 없습니다.');
      })
      .finally(() => { if (active) setDetailLoading(false); });
    return () => { active = false; };
  }, [activeReport]);

  const updateParams = (patch: Record<string, string | null>) => {
    setSearchParams(prev => {
      const next = new URLSearchParams(prev);
      for (const [k, v] of Object.entries(patch)) {
        if (v === null) next.delete(k);
        else next.set(k, v);
      }
      return next;
    });
  };

  const handleTabChange = (value: ReportStatus) => updateParams({ status: value, page: null });
  const handleSortChange = (value: ReportSortType) => updateParams({ sort: value, page: null });

  const handlePageChange = (p: number) => {
    updateParams({ page: String(p) });
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const activeKey = activeReport ? reportKey(activeReport) : null;
  const hasNextPending = status === 'PENDING' && reports.some(r => reportKey(r) !== activeKey);
  const nextPendingCount = status === 'PENDING' ? reports.filter(r => reportKey(r) !== activeKey).length : 0;

  const handleResolve = () => {
    if (!activeReport) return;
    const key = reportKey(activeReport);
    setLeavingKey(key);
    // TODO: 처리 API가 준비되면 여기서 실제 요청을 보낸다. 지금은 목록 상태만 갱신한다.
    window.setTimeout(() => {
      setReports(prev => prev.filter(r => reportKey(r) !== key));
      setMeta(prev => ({ ...prev, total: Math.max(0, prev.total - 1) }));
      setLeavingKey(prev => (prev === key ? null : prev));
    }, 240);
  };

  const handleNextPending = () => {
    const next = reports.find(r => reportKey(r) !== activeKey);
    setActiveReport(next ?? null);
  };

  return (
    <div className="rpt-page">
      <div className="rpt-head">
        <h1 className="rpt-title">신고 관리</h1>
        <p className="rpt-sub">같은 대상에 대한 신고는 한 줄로 묶어 보여줍니다.</p>
      </div>

      <div className="rpt-tabs" role="tablist" aria-label="신고 상태 필터">
        {TABS.map(tab => (
          <button
            key={tab.value}
            role="tab"
            aria-selected={status === tab.value}
            className={'rpt-tab' + (status === tab.value ? ' active' : '')}
            onClick={() => handleTabChange(tab.value)}
          >
            {tab.label}
            {status === tab.value && !loading && !error && (
              <span className="rpt-tab-count">{meta.total}</span>
            )}
          </button>
        ))}
      </div>

      <div className="rpt-table-wrap">
        <table className="rpt-table">
          <thead>
            <tr>
              <th className="rpt-col-count">
                <button
                  className={'rpt-sort' + (sort === 'count' ? ' active' : '')}
                  onClick={() => handleSortChange('count')}
                >
                  신고 건수{sort === 'count' && <span className="rpt-caret">▾</span>}
                </button>
              </th>
              <th>유형</th>
              <th>신고 대상</th>
              <th>주요 사유</th>
              <th>피신고자</th>
              <th>
                <button
                  className={'rpt-sort' + (sort === 'latest' ? ' active' : '')}
                  onClick={() => handleSortChange('latest')}
                >
                  최근 신고{sort === 'latest' && <span className="rpt-caret">▾</span>}
                </button>
              </th>
              <th>상태</th>
            </tr>
          </thead>
          <tbody>
            {reports.map(r => (
              <tr
                key={`${r.targetType}-${r.targetId}-${r.status}`}
                className={leavingKey === reportKey(r) ? 'rpt-row-leave' : undefined}
                onClick={() => setActiveReport(r)}
              >
                <td className="rpt-col-count">
                  {r.reportCount}<span className="rpt-unit">건</span>
                </td>
                <td><span className="rpt-kind">{TARGET_LABEL[r.targetType] ?? r.targetType}</span></td>
                <td className="rpt-col-target">
                  {r.targetPreview
                    ? <span className="rpt-preview" title={r.targetPreview}>{r.targetPreview}</span>
                    : <span className="rpt-preview gone">삭제된 콘텐츠</span>}
                </td>
                <td>{REASON_LABEL[r.mainReason] ?? r.mainReason}</td>
                <td>{r.reportedUsername}</td>
                <td className="rpt-mono" title={r.lastReportedAt}>{formatReportedAt(r.lastReportedAt)}</td>
                <td><StatusBadge report={r} /></td>
              </tr>
            ))}
          </tbody>
        </table>

        {loading && <div className="lrow"><div className="spin" /><span>불러오는 중...</span></div>}
        {!loading && error && <div className="empty" style={{ color: 'var(--red)' }}>{error}</div>}
        {!loading && !error && reports.length === 0 && (
          <div className="empty">
            {status === 'PENDING' ? '처리할 신고가 없습니다.' : '처리한 신고가 없습니다.'}
          </div>
        )}
      </div>

      <Pagination current={currentPage} totalPages={meta.totalPages} onChange={handlePageChange} />

      {activeReport && !detail && !history && (
        <>
          <div className="rpt-drawer-scrim" onClick={() => setActiveReport(null)} />
          <aside className="rpt-drawer" aria-label="신고 처리" role="dialog" aria-modal="true">
            {detailLoading && <div className="lrow"><div className="spin" /><span>불러오는 중...</span></div>}
            {!detailLoading && detailError && (
              <div className="empty" style={{ color: 'var(--red)' }}>{detailError}</div>
            )}
          </aside>
        </>
      )}

      {activeReport && detail && (
        <ReportDrawer
          key={reportKey(activeReport)}
          detail={detail}
          hasNextPending={hasNextPending}
          nextPendingCount={nextPendingCount}
          onClose={() => setActiveReport(null)}
          onResolve={handleResolve}
          onNextPending={handleNextPending}
        />
      )}

      {activeReport && history && (
        <ReportHistoryDrawer
          key={reportKey(activeReport)}
          targetType={activeReport.targetType}
          targetId={activeReport.targetId}
          entries={history}
          onClose={() => setActiveReport(null)}
        />
      )}
    </div>
  );
}
