interface PaginationProps {
  current: number;
  totalPages: number;
  onChange: (page: number) => void;
}

export default function Pagination({ current, totalPages, onChange }: PaginationProps) {
  if (totalPages <= 1) return null;

  const range = Array.from({ length: Math.min(totalPages, 7) }, (_, i) => {
    if (totalPages <= 7) return i + 1;
    if (current <= 4) return i + 1;
    if (current >= totalPages - 3) return totalPages - 6 + i;
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
      <button className="pgb" onClick={() => onChange(current + 1)} disabled={current === totalPages}>›</button>
      <button className="pgb" onClick={() => onChange(totalPages)} disabled={current === totalPages}>»</button>
    </div>
  );
}
