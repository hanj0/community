import { useState, useEffect } from 'react';
import type { ChannelData } from '../types';
import { fetchChannels } from '../api/channels';
import { CHANNELS } from '../constants/data';

export function useChannels(): ChannelData[] {
  const [channels, setChannels] = useState<ChannelData[]>(CHANNELS);
  useEffect(() => {
    fetchChannels().then(setChannels).catch(() => {});
  }, []);
  return channels;
}
