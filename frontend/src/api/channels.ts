import type { ChannelData } from '../types';

export async function fetchChannels(): Promise<ChannelData[]> {
  const res = await fetch('/api/channels', { credentials: 'include' });
  if (!res.ok) throw new Error('채널 목록을 불러올 수 없습니다.');
  const body = await res.json();
  return body.data as ChannelData[];
}
