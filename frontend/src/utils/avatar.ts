const PALETTE = [
  { ab: '#B5D4F4', ac: '#0C447C' },
  { ab: '#C0DD97', ac: '#27500A' },
  { ab: '#EEEDFE', ac: '#3C3489' },
  { ab: '#FAEEDA', ac: '#633806' },
  { ab: '#FCEBEB', ac: '#A32D2D' },
  { ab: '#E6F1FB', ac: '#1A5276' },
];

export function getAvatarStyle(author: string): { av: string; ab: string; ac: string } {
  if (!author) return { av: '?', ab: PALETTE[0].ab, ac: PALETTE[0].ac };
  const hash = author.split('').reduce((acc, c) => acc + c.charCodeAt(0), 0);
  const { ab, ac } = PALETTE[hash % PALETTE.length];
  return { av: author.charAt(0), ab, ac };
}
