import { ITEMS_PER_PAGE } from '../../constants/data';

interface PaginationProps {
  current: number;
  total: number;
  onChange: (page: number) => void;
}

export default function Pagination({ current, total, onChange }: PaginationProps) {
  const pages = Math.ceil(total / ITEMS_PER_PAGE);
  if (pages <= 1) return null;

  const range = Array.from({ length: Math.min(pages, 7) }, (_, i) => {
    if (pages <= 7) return i + 1;
    if (current <= 4) return i + 1;
    if (current >= pages - 3) return pages - 6 + i;
    return current - 3 + i;
  });

  return (
    <div className="pgn">
      <button className="pgb" onClick={() => onChange(1)} disabled={current === 1}>«</button>
      <button className="pgb" onClick={() => onChange(current - 1)} disabled={current === 1}>‹</button>
      {range.map(p => (
        <button key={p} className={'pgb' + (p === current ? ' active' : '')} onClick={() => onChange(p)}>
          {p}
        </button>
      ))}
      <button className="pgb" onClick={() => onChange(current + 1)} disabled={current === pages}>›</button>
      <button className="pgb" onClick={() => onChange(pages)} disabled={current === pages}>»</button>
    </div>
  );
}
