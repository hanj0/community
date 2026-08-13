export interface PageMeta {
  page: number;
  size: number;
  total: number;
  totalPages: number;
}

export interface PagedResponse<T> {
  data: T[];
  meta: PageMeta;
}

export interface ChannelData {
  id: string;
  name: string;
  color: string;
  postCount?: number;
}

export interface PostSummary {
  id: number;
  title: string;
  authorInfo: { id: number; username: string } | null;
  createdAt: string;
  channelId: string;
  channelName: string;
  likeCount: number;
  commentCount: number;
  viewCount: number;
  hasImage: boolean;
  isNotice: boolean;
  isPinned: boolean;
  score?: number;
}

export interface CommentData {
  id: number;
  authorInfo: { id: number; username: string } | null;
  content: string;
  createdAt: string;
  likeCount: number;
  dislikeCount: number;
  reactionType: ReactionType | null;
  replyCount: number;
}

export type ReactionType = 'LIKE' | 'DISLIKE';

export interface CursorResponse<T> {
  data: T[];
  nextCursor: string | null;
  hasNext: boolean;
}

export type NotificationType = 'REACTION' | 'COMMENT';
export type NotificationTargetType = 'POST' | 'COMMENT';

export interface NotificationItem {
  id: number;
  type: NotificationType;
  targetType: NotificationTargetType;
  targetId: number;
  rootPostId: number;
  lastActorName: string;
  actorCount: number;
  isRead: boolean;
  updatedAt: string;
  /** 게시글/댓글 미리보기. 서버 추가 예정이라 옵셔널. */
  targetPreview?: string;
}

export type ReportTargetType = 'POST' | 'COMMENT';
export type ReportReason =
  | 'SPAM'
  | 'ABUSE'
  | 'SEXUAL'
  | 'VIOLENCE'
  | 'DISGUSTING'
  | 'PRIVACY'
  | 'ILLEGAL'
  | 'COPYRIGHT'
  | 'OTHER';

export interface ReportRequest {
  targetType: ReportTargetType;
  targetId: number;
  reason: ReportReason;
  reasonDetail: string;
}

/** 신고 처리 단계. 목록 탭은 이 둘로만 나뉜다. */
export type ReportStatus = 'PENDING' | 'RESOLVED';

/** RESOLVED 안에서의 처리 결과. PENDING인 신고에는 값이 없다. */
export type ReportResolution = 'REJECTED' | 'CONTENT_DELETED';

export type ReportSortType = 'count' | 'latest';

/** 같은 대상에 대한 신고를 한 줄로 묶은 관리자 목록 행. */
export interface ReportSummary {
  reportCount: number;
  targetType: ReportTargetType;
  targetId: number;
  targetPreview: string | null;
  reportedUserId: number;
  reportedUsername: string;
  mainReason: ReportReason;
  lastReportedAt: string;
  status: ReportStatus;
  /** 서버 추가 예정이라 옵셔널. 없으면 상태 배지는 '처리완료'로만 표시. */
  resolution?: ReportResolution | null;
}

export interface ReportReasonCount {
  reason: ReportReason;
  reasonCount: number;
}

export interface ReportedUserState {
  label: string;
  until: string | null;
}

/**
 * PENDING 신고 상세 드로어가 필요로 하는 데이터.
 * `GET /api/admin/reports/{targetType}/{targetId}/pending` 응답(PENDING 전용) + 목록 행(summary)에서
 * 보충한 `reportedUsername`으로 채워진다.
 * 서버가 아직 피신고자 제재 이력(`userSanctionInfo`)을 내려주지 않아 관련 필드는 optional이다.
 */
export interface ReportDetail {
  targetType: ReportTargetType;
  targetId: number;
  /** 이 대상에 걸린 신고 id 목록. 처리 API(POST resolutions) 호출 시 그대로 실어 보낸다. */
  reportIds: number[];
  reportCount: number;
  mainReason: ReportReason;
  firstReportedAt: string;
  lastReportedAt: string;
  edited: boolean;
  contentSnapshot: string;
  reasonDistribution: ReportReasonCount[];
  reportedUserId: number;
  reportedUsername: string;
  /** 서버 제재 이력 API 준비 전이라 null이면 "정보 없음"으로 표시. */
  reportedUserState: ReportedUserState | null;
  reportedUserPriorSanctions: number | null;
  /** 제재 수위 제안 문구. 근거 데이터가 없으면 null. */
  suggestion: string | null;
}

/**
 * 처리완료 대상의 처리 차수 하나.
 * `GET /api/admin/reports/{targetType}/{targetId}/resolutions` 응답의 `histories` 원소.
 */
export interface ReportHistoryEntry {
  targetType: ReportTargetType;
  targetId: number;
  handledAt: string;
  resolution: ReportResolution;
  reportCount: number;
  mainReason: ReportReason;
  firstReportedAt: string;
  lastReportedAt: string;
  handledBy: string;
  handledMemo: string;
}

/** 관리자가 신고 처리 시 고르는 제재 사유. 신고 등록 시의 {@link ReportReason}과는 다른 카테고리다. */
export type SanctionReason =
  | 'SPAM_ADVERTISING'
  | 'SPAM_FLOODING'
  | 'SPAM_BOT'
  | 'ABUSE_INSULT'
  | 'ABUSE_HARASSMENT'
  | 'ABUSE_HATE'
  | 'ABUSE_THREAT'
  | 'CONTENT_SEXUAL'
  | 'CONTENT_VIOLENCE'
  | 'CONTENT_DISGUSTING'
  | 'CONTENT_ILLEGAL'
  | 'CONTENT_FALSE_INFO'
  | 'PRIVACY_EXPOSURE'
  | 'COPYRIGHT_VIOLATION'
  | 'IMPERSONATION'
  | 'MULTI_ACCOUNT'
  | 'BAN_EVASION'
  | 'REPORT_ABUSE'
  | 'RULE_VIOLATION';

export type SanctionType = 'WARNING' | 'WRITE_BLOCK' | 'BAN';

/**
 * 신고 처리 요청. `POST /api/admin/reports/{targetType}/{targetId}/resolutions` 바디.
 * 콘텐츠 조치(`contentActionType`)와 유저 제재(`sanction`)는 `reason` 하나를 공유한다.
 * 반려(`REJECTED`)는 위반 판단이 아니므로 `reason`이 없다.
 */
export interface ReportResolveRequest {
  reportIds: number[];
  contentActionType: ReportResolution;
  reason: SanctionReason | null;
  reasonDetail: string;
  /** 유저 제재를 안 할 경우 null. */
  sanction: {
    sanctionType: SanctionType;
    durationDays: number | null;
    memo: string;
  } | null;
}

export type SortType = 'latest' | 'likes' | 'comments' | 'views';
export type ViewType = 'card' | 'compact';
export type PeriodType = '24h' | '7d' | '30d';

export type UserRole = 'USER' | 'ADMIN';

export interface User {
  id: number;
  username: string;
  email: string;
  createdAt: string;
  /** /api/auth/me만 내려준다. 로그인·회원가입 응답에는 없어서 옵셔널. */
  role?: UserRole;
}

export interface ApiErrorDetail {
  field: string;
  reason: string;
}

export interface CreatePostRequest {
  title: string;
  channelId: number;
  content: string;
}

export interface PostDetail {
  id: number;
  title: string;
  content: string;
  channelId: string;
  channelName: string;
  authorId: number;
  authorName: string;
  likeCount: number;
  dislikeCount: number;
  reactionType: ReactionType | null;
  commentCount: number;
  viewCount: number;
  createdAt: string;
  isPinned?: boolean;
  bookmarked?: boolean;
}

export interface MyComment {
  id: number;
  content: string;
  createdAt: string;
  postId: number;
  postTitle: string;
}

export interface SignupRequest {
  username: string;
  email: string;
  password: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}
