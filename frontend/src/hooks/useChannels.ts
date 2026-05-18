import { useState, useEffect } from 'react';
import type { ChannelData } from '../types';
import { fetchChannels } from '../api/channels';

export function useChannels(): ChannelData[] {
  const [channels, setChannels] = useState<ChannelData[]>([]);
  useEffect(() => {
    fetchChannels().then(setChannels).catch(() => {});
  }, []);
  return channels;
}
