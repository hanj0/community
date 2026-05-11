import { COLORS } from '../constants/data';

export function rankColor(rank: number): string {
  if (rank === 1) return COLORS.red;
  if (rank === 2) return COLORS.amber;
  if (rank === 3) return COLORS.green;
  return '#B4B2A9';
}
