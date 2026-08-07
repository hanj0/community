import { useEffect } from 'react';
import type { ReportHistoryEntry, ReportTargetType } from '../../types';
import { TARGET_LABEL } from '../../utils/reportLabels';
import ReportResolutionHistory from './ReportResolutionHistory';

interface ReportHistoryDrawerProps {
  targetType: ReportTargetType;
  targetId: number;
  entries: ReportHistoryEntry[];
  onClose: () => void;
}

export default function ReportHistoryDrawer({ targetType, targetId, entries, onClose }: ReportHistoryDrawerProps) {
  useEffect(() => {
    const onKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape') onClose();
    };
    document.addEventListener('keydown', onKeyDown);
    return () => document.removeEventListener('keydown', onKeyDown);
  }, [onClose]);

  return (
    <>
      <div className="rpt-drawer-scrim" onClick={onClose} />
      <aside className="rpt-drawer" aria-label="신고 처리 이력" role="dialog" aria-modal="true">
        <div className="rpt-drawer-head">
          <span className="rpt-drawer-id">
            {TARGET_LABEL[targetType]} <b>#{targetId}</b>
          </span>
          <button className="rpt-drawer-close" type="button" aria-label="닫기" onClick={onClose}>✕</button>
        </div>

        <div className="rpt-drawer-body">
          <ReportResolutionHistory entries={entries} />
        </div>
      </aside>
    </>
  );
}
