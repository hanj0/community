import { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import type {
  PageMeta,
  ReportReason,
  ReportResolution,
  ReportSortType,
  ReportStatus,
  ReportSummary,
  ReportTargetType,
} from '../types';
import { fetchAdminReports } from '../api/adminReports';
import Pagination from '../components/common/Pagination';

const PAGE_SIZE = 20;

const TABS: { value: ReportStatus; label: string }[] = [
  { value: 'PENDING', label: '대기' },
  { value: 'RESOLVED', label: '처리완료' },
];

const TARGET_LABEL: Record<ReportTargetType, string> = {
  POST: '게시글',
  COMMENT: '댓글',
};

const REASON_LABEL: Record<ReportReason, string> = {
  SPAM: '스팸/광고',
  ABUSE: '욕설/비방',
  SEXUAL: '음란물/선정성',
  ILLEGAL: '불법 정보',
  ETC: '기타',
};

/** 처리완료 안에서의 세부 결과. 서버가 resolution을 안 내려주면 '처리완료'로만 표시한다. */
const RESOLUTION_LABEL: Record<ReportResolution, string> = {
  REJECTED: '반려',
  CONTENT_DELETED: '삭제',
  TARGET_ALREADY_DELETED: '대상 삭제됨',
};

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
  const label = resolution ? RESOLUTION_LABEL[resolution] : '처리완료';
  return <span className={'rpt-status resolved' + (resolution === 'REJECTED' ? ' muted' : '')}>{label}</span>;
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
              <tr key={`${r.targetType}-${r.targetId}-${r.status}`}>
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
    </div>
  );
}
