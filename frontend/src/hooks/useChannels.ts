import { useChannelContext } from '../context/ChannelContext';
import type { ChannelData } from '../types';

export function useChannels(): ChannelData[] {
  return useChannelContext();
}
