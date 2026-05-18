import type { ChannelData } from '../types';

interface ChannelApiResponse {
  id: number;
  name: string;
  description: string;
}

const CHANNEL_COLORS = [
  '#E24B4A',
  '#EF9F27',
  '#378ADD',
  '#639922',
  '#7F77DD',
  '#888780',
];

export async function fetchChannels(): Promise<ChannelData[]> {
  const res = await fetch('/api/channels', { credentials: 'include' });
  if (!res.ok) throw new Error('채널 목록을 불러올 수 없습니다.');
  const body = await res.json();
  const raw: ChannelApiResponse[] = body.data;
  return raw.map((ch, i) => ({
    id: String(ch.id),
    name: ch.name,
    color: CHANNEL_COLORS[i % CHANNEL_COLORS.length],
  }));
}
