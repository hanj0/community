import type { ChannelData, TrendingItem } from '../types';

export const COLORS = {
  red:    '#E24B4A',
  amber:  '#EF9F27',
  green:  '#639922',
  blue:   '#378ADD',
  purple: '#7F77DD',
  gray:   '#888780',
} as const;

export const CHANNELS: ChannelData[] = [
  { id: 'dev',   name: '개발',      color: COLORS.red    },
  { id: 'job',   name: '취업/이직', color: COLORS.amber  },
  { id: 'ai',    name: 'AI/ML',     color: COLORS.blue   },
  { id: 'daily', name: '일상',      color: COLORS.green  },
  { id: 'anon',  name: '익명',      color: COLORS.purple },
];

export const NOTICES_TEXT: string[] = [
  '커뮤니티 운영 정책 안내 (2025.04 개정)',
  '어뷰징 신고 기준 및 포인트 차감 안내',
  '채널 개설 신청 방법 변경 안내',
];

export const TRENDING: TrendingItem[] = [
  { w: '번아웃',       d: '↑2'  },
  { w: '연봉협상',     d: '신규' },
  { w: '타입스크립트', d: '↑1'  },
  { w: 'Claude API',  d: '↑5'  },
  { w: '면접 후기',   d: '↓1'  },
];
