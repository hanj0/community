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
export type ReportReason = 'SPAM' | 'ABUSE' | 'SEXUAL' | 'ILLEGAL' | 'ETC';

export interface ReportRequest {
  targetType: ReportTargetType;
  targetId: number;
  reason: ReportReason;
  reasonDetail: string;
}

/** 신고 처리 단계. 목록 탭은 이 둘로만 나뉜다. */
export type ReportStatus = 'PENDING' | 'RESOLVED';

/** RESOLVED 안에서의 처리 결과. PENDING인 신고에는 값이 없다. */
export type ReportResolution = 'REJECTED' | 'CONTENT_DELETED' | 'TARGET_ALREADY_DELETED';

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
