import { createContext, useContext, useState, useEffect, type ReactNode } from 'react';
import type { ChannelData } from '../types';
import { fetchChannels } from '../api/channels';

const ChannelContext = createContext<ChannelData[]>([]);

export function ChannelProvider({ children }: { children: ReactNode }) {
  const [channels, setChannels] = useState<ChannelData[]>([]);

  useEffect(() => {
    fetchChannels().then(setChannels).catch(() => {});
  }, []);

  return <ChannelContext.Provider value={channels}>{children}</ChannelContext.Provider>;
}

export function useChannelContext(): ChannelData[] {
  return useContext(ChannelContext);
}
