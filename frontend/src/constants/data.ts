import type { Channel, Post, FeedPost, Comment, TrendingItem } from '../types';

export const COLORS = {
  red:    '#E24B4A',
  amber:  '#EF9F27',
  green:  '#639922',
  blue:   '#378ADD',
  purple: '#7F77DD',
  gray:   '#888780',
} as const;

export const CHANNELS: Channel[] = [
  { id: 'dev',   name: '개발',     color: COLORS.red    },
  { id: 'job',   name: '취업/이직', color: COLORS.amber  },
  { id: 'ai',    name: 'AI/ML',    color: COLORS.blue   },
  { id: 'daily', name: '일상',     color: COLORS.green  },
  { id: 'anon',  name: '익명',     color: COLORS.purple },
];

export const ALL_POSTS: Post[] = [
  { id:1,  title:'스타트업 3년차 개발자가 느끼는 번아웃에 대해서',               channel:'개발',     channelColor:COLORS.red,    likes:847, comments:213, views:12402, score:2847, isPin:true,  author:'철수킴',       time:'2시간 전',  hasImg:false },
  { id:2,  title:'대기업 vs 스타트업 경력 3년차 연봉 비교 정리해봤습니다',       channel:'취업/이직', channelColor:COLORS.amber,  likes:621, comments:158, views:9811,  score:1934, isPin:false, author:'dev_runner',   time:'4시간 전',  hasImg:true  },
  { id:3,  title:'오늘 면접에서 떨어졌는데 위로받고 싶어서요',                   channel:'일상',     channelColor:COLORS.green,  likes:512, comments:301, views:7244,  score:1701, isPin:false, author:'익명',         time:'5시간 전',  hasImg:false },
  { id:4,  title:'Claude API 실무 사용 후기 — 비용이랑 퀄리티 솔직하게 씁니다', channel:'AI/ML',    channelColor:COLORS.blue,   likes:398, comments:87,  views:5120,  score:1231, isPin:false, author:'aidev_kr',     time:'7시간 전',  hasImg:false },
  { id:5,  title:'타입스크립트 도입하고 나서 달라진 점들 — 실무 6개월 후기',     channel:'개발',     channelColor:COLORS.red,    likes:311, comments:65,  views:4033,  score:987,  isPin:false, author:'ts_believer',  time:'9시간 전',  hasImg:false },
  { id:6,  title:'React 18 Concurrent Mode 실무에서 써본 솔직 후기',             channel:'개발',     channelColor:COLORS.red,    likes:289, comments:54,  views:3820,  score:871,  isPin:false, author:'react_pro',    time:'11시간 전', hasImg:true  },
  { id:7,  title:'이직 준비 중인데 포트폴리오 피드백 부탁드려요',                channel:'취업/이직', channelColor:COLORS.amber,  likes:201, comments:143, views:3100,  score:824,  isPin:false, author:'job_seeker22', time:'12시간 전', hasImg:false },
  { id:8,  title:'GPT-4o vs Claude 3.5 코딩 성능 비교 직접 해봤습니다',          channel:'AI/ML',    channelColor:COLORS.blue,   likes:256, comments:91,  views:4210,  score:812,  isPin:false, author:'llm_tester',   time:'13시간 전', hasImg:true  },
  { id:9,  title:'사수 없이 신입 6개월 버티는 법',                                channel:'일상',     channelColor:COLORS.green,  likes:198, comments:177, views:2890,  score:780,  isPin:false, author:'alone_dev',    time:'15시간 전', hasImg:false },
  { id:10, title:'Rust 배우는데 왜 이렇게 어렵죠? 다들 어떻게 넘어갔어요',      channel:'개발',     channelColor:COLORS.red,    likes:155, comments:88,  views:2100,  score:601,  isPin:false, author:'rust_noob',    time:'17시간 전', hasImg:false },
  { id:11, title:'스택오버플로우 죽어가는 거 다들 느끼시나요',                    channel:'개발',     channelColor:COLORS.red,    likes:134, comments:72,  views:1980,  score:521,  isPin:false, author:'old_dev',      time:'19시간 전', hasImg:false },
  { id:12, title:'연봉 협상 팁 공유합니다 — 7000만원 달성 경험담',               channel:'취업/이직', channelColor:COLORS.amber,  likes:410, comments:92,  views:6200,  score:1490, isPin:false, author:'salary_king',  time:'20시간 전', hasImg:false },
  { id:13, title:'쿠버네티스 처음 배울때 알았으면 좋았을 것들',                   channel:'개발',     channelColor:COLORS.red,    likes:178, comments:41,  views:2430,  score:598,  isPin:false, author:'k8s_veteran',  time:'22시간 전', hasImg:false },
  { id:14, title:'백엔드 개발자 커피챗 후기 — 생각보다 훨씬 좋았어요',           channel:'일상',     channelColor:COLORS.green,  likes:112, comments:38,  views:1720,  score:421,  isPin:false, author:'coffee_dev',   time:'23시간 전', hasImg:false },
  { id:15, title:'Supabase 실무 도입 후기 — Firebase랑 비교해봤습니다',           channel:'개발',     channelColor:COLORS.red,    likes:203, comments:56,  views:3010,  score:692,  isPin:false, author:'db_hopper',    time:'1일 전',    hasImg:true  },
];

export const FEED_POSTS: FeedPost[] = [
  { id:201, title:'React Query v5로 마이그레이션 삽질 기록',              channel:'개발',     channelColor:COLORS.red,    likes:12,  comments:4,   views:320,  time:'3분 전',   author:'철수킴',       hasImg:true,  isNotice:false },
  { id:202, title:'팀장이 너무 힘드네요 솔직히',                            channel:'익명',     channelColor:COLORS.purple, likes:34,  comments:21,  views:890,  time:'11분 전',  author:'익명',         hasImg:false, isNotice:false },
  { id:203, title:'이 연봉 제안 어떻게 생각하세요? (서울 SI 경력 2년)',    channel:'취업/이직', channelColor:COLORS.amber,  likes:8,   comments:16,  views:540,  time:'28분 전',  author:'dev_newbie',   hasImg:false, isNotice:false },
  { id:204, title:'Next.js 14 App Router 정착 후기',                        channel:'개발',     channelColor:COLORS.red,    likes:19,  comments:7,   views:410,  time:'42분 전',  author:'next_fan',     hasImg:false, isNotice:false },
  { id:205, title:'오늘 처음 사이드프로젝트 배포했어요!',                    channel:'일상',     channelColor:COLORS.green,  likes:44,  comments:12,  views:670,  time:'1시간 전', author:'rookie_ship',  hasImg:true,  isNotice:false },
  { id:206, title:'커뮤니티 운영 정책 안내 (2025.04 개정)',                  channel:'공지',     channelColor:COLORS.gray,   likes:0,   comments:0,   views:1200, time:'2시간 전', author:'운영자',       hasImg:false, isNotice:true  },
  { id:207, title:'Supabase 실무 도입 후기 — Firebase랑 비교해봤습니다',   channel:'개발',     channelColor:COLORS.red,    likes:203, comments:56,  views:3010, time:'3시간 전', author:'db_hopper',    hasImg:true,  isNotice:false },
  { id:208, title:'GPT-4o vs Claude 3.5 코딩 성능 비교 직접 해봤습니다',   channel:'AI/ML',    channelColor:COLORS.blue,   likes:256, comments:91,  views:4210, time:'4시간 전', author:'llm_tester',   hasImg:true,  isNotice:false },
  { id:209, title:'연봉 협상 팁 공유합니다 — 7000만원 달성 경험담',          channel:'취업/이직', channelColor:COLORS.amber,  likes:410, comments:92,  views:6200, time:'5시간 전', author:'salary_king',  hasImg:false, isNotice:false },
  { id:210, title:'사수 없이 신입 6개월 버티는 법',                          channel:'일상',     channelColor:COLORS.green,  likes:198, comments:177, views:2890, time:'6시간 전', author:'alone_dev',    hasImg:false, isNotice:false },
  { id:211, title:'쿠버네티스 처음 배울때 알았으면 좋았을 것들',              channel:'개발',     channelColor:COLORS.red,    likes:178, comments:41,  views:2430, time:'7시간 전', author:'k8s_veteran',  hasImg:false, isNotice:false },
  { id:212, title:'스타트업 3년차 개발자가 느끼는 번아웃에 대해서',           channel:'개발',     channelColor:COLORS.red,    likes:847, comments:213, views:12402,time:'8시간 전', author:'철수킴',       hasImg:false, isNotice:false },
  { id:213, title:'대기업 vs 스타트업 경력 3년차 연봉 비교 정리',             channel:'취업/이직', channelColor:COLORS.amber,  likes:621, comments:158, views:9811, time:'9시간 전', author:'dev_runner',   hasImg:true,  isNotice:false },
  { id:214, title:'오늘 면접에서 떨어졌는데 위로받고 싶어서요',               channel:'일상',     channelColor:COLORS.green,  likes:512, comments:301, views:7244, time:'10시간 전',author:'익명',         hasImg:false, isNotice:false },
  { id:215, title:'Claude API 실무 사용 후기 — 비용이랑 퀄리티 솔직하게',   channel:'AI/ML',    channelColor:COLORS.blue,   likes:398, comments:87,  views:5120, time:'11시간 전',author:'aidev_kr',     hasImg:false, isNotice:false },
  { id:216, title:'타입스크립트 도입하고 나서 달라진 점들 — 실무 6개월',     channel:'개발',     channelColor:COLORS.red,    likes:311, comments:65,  views:4033, time:'12시간 전',author:'ts_believer',  hasImg:false, isNotice:false },
  { id:217, title:'React 18 Concurrent Mode 실무에서 써본 솔직 후기',        channel:'개발',     channelColor:COLORS.red,    likes:289, comments:54,  views:3820, time:'13시간 전',author:'react_pro',    hasImg:true,  isNotice:false },
  { id:218, title:'이직 준비 중인데 포트폴리오 피드백 부탁드려요',             channel:'취업/이직', channelColor:COLORS.amber,  likes:201, comments:143, views:3100, time:'14시간 전',author:'job_seeker22', hasImg:false, isNotice:false },
  { id:219, title:'Rust 배우는데 왜 이렇게 어렵죠?',                          channel:'개발',     channelColor:COLORS.red,    likes:155, comments:88,  views:2100, time:'16시간 전',author:'rust_noob',    hasImg:false, isNotice:false },
  { id:220, title:'스택오버플로우 죽어가는 거 다들 느끼시나요',                channel:'개발',     channelColor:COLORS.red,    likes:134, comments:72,  views:1980, time:'18시간 전',author:'old_dev',      hasImg:false, isNotice:false },
  { id:221, title:'백엔드 개발자 커피챗 후기 — 생각보다 훨씬 좋았어요',       channel:'일상',     channelColor:COLORS.green,  likes:112, comments:38,  views:1720, time:'20시간 전',author:'coffee_dev',   hasImg:false, isNotice:false },
  { id:222, title:'LLM 파인튜닝 입문 — 로컬에서 직접 돌려봤습니다',           channel:'AI/ML',    channelColor:COLORS.blue,   likes:88,  comments:29,  views:1340, time:'22시간 전',author:'ml_rookie',    hasImg:true,  isNotice:false },
  { id:223, title:'개발자 번아웃 극복 후기 — 6개월 휴직 결정 이야기',         channel:'일상',     channelColor:COLORS.green,  likes:321, comments:88,  views:4800, time:'1일 전',   author:'comeback_dev', hasImg:false, isNotice:false },
  { id:224, title:'신입 프론트엔드 취업 준비 로드맵 공유합니다',               channel:'취업/이직', channelColor:COLORS.amber,  likes:267, comments:112, views:5100, time:'1일 전',   author:'fe_mentor',    hasImg:false, isNotice:false },
  { id:225, title:'PostgreSQL vs MySQL 실무 선택 기준 정리',                  channel:'개발',     channelColor:COLORS.red,    likes:143, comments:47,  views:2200, time:'1일 전',   author:'db_geek',      hasImg:false, isNotice:false },
];

export const RECENT_POSTS = FEED_POSTS.filter(p => !p.isNotice).slice(0, 5);

export const NOTICES_TEXT: string[] = [
  '커뮤니티 운영 정책 안내 (2025.04 개정)',
  '어뷰징 신고 기준 및 포인트 차감 안내',
  '채널 개설 신청 방법 변경 안내',
];

export const TRENDING: TrendingItem[] = [
  { w: '번아웃',     d: '↑2'  },
  { w: '연봉협상',   d: '신규' },
  { w: '타입스크립트', d: '↑1' },
  { w: 'Claude API', d: '↑5'  },
  { w: '면접 후기',  d: '↓1'  },
];

export const SAMPLE_COMMENTS: Comment[] = [
  {
    id: 1, author: '하늘색개발자', av: '하', ab: '#B5D4F4', ac: '#0C447C', time: '1시간 전',
    text: '정말 공감되는 글이에요. 저도 비슷한 상황에서 많이 고민했는데, 이렇게 정리해주시니 도움이 많이 됐습니다.',
    likes: 24, dislikes: 1,
    replies: [
      { id:11, author:'철수킴',     av:'철', ab:'#C0DD97', ac:'#27500A', time:'50분 전', text:'저도 같은 생각이에요. 특히 세 번째 단락이 인상적이었습니다.',                              likes:8, dislikes:0, isMention:false },
      { id:12, author:'dev_runner', av:'d',  ab:'#EEEDFE', ac:'#3C3489', time:'35분 전', text:'@철수킴 맞아요, 그 부분이 핵심인 것 같더라고요. 저도 적용해볼 생각입니다.',               likes:4, dislikes:0, isMention:true  },
    ],
  },
  {
    id: 2, author: '익명', av: '익', ab: '#EEEDFE', ac: '#3C3489', time: '45분 전',
    text: '솔직히 이 내용은 좀 아닌 것 같아요. 케이스마다 다를 수 있겠지만요.',
    likes: 3, dislikes: 11, replies: [],
  },
  {
    id: 3, author: 'ts_believer', av: 't', ab: '#FAEEDA', ac: '#633806', time: '30분 전',
    text: '좋은 글 감사합니다. 혹시 참고하신 자료나 레퍼런스 있으면 공유해주실 수 있나요?',
    likes: 17, dislikes: 0,
    replies: [
      { id:31, author:'작성자', av:'작', ab:'#FCEBEB', ac:'#A32D2D', time:'20분 전', text:'댓글 감사해요! 레퍼런스는 주로 개인 경험 기반이라 별도 링크는 없고요, 관련 글 나중에 정리해서 올릴게요.', likes:6, dislikes:0, isMention:false },
    ],
  },
  {
    id: 4, author: 'aidev_kr', av: 'a', ab: '#E6F1FB', ac: '#0C447C', time: '15분 전',
    text: '북마크 해뒀습니다. 나중에 팀원들한테도 공유해야겠어요.',
    likes: 12, dislikes: 0, replies: [],
  },
];

export const POST_BODY: string[] = [
  '안녕하세요, 오늘은 개인적인 경험을 공유하고자 글을 씁니다. 이 주제에 대해 평소 생각이 많았는데 커뮤니티 분들의 의견도 궁금해서요.',
  '처음엔 별거 아닌 줄 알았는데, 막상 직접 겪어보니 꽤 복잡한 문제더라고요. 특히 실무에서 마주하는 상황들은 인터넷에서 찾아볼 수 있는 정보들과 꽤 달랐습니다.',
  '제가 경험한 것들을 최대한 솔직하게 정리해봤습니다. 틀린 부분이나 다른 시각이 있으시면 댓글로 편하게 남겨주세요. 같이 논의하면 좋겠습니다.',
  '긴 글 읽어주셔서 감사합니다. 비슷한 경험 있으신 분들 계시면 공유해주세요 :)',
];

export const PAGE_SIZE = 10;
export const ITEMS_PER_PAGE = 10;
