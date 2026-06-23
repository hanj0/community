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

export type SortType = 'latest' | 'likes' | 'comments' | 'views';
export type ViewType = 'card' | 'compact';
export type PeriodType = '24h' | '7d' | '30d';

export interface User {
  id: number;
  username: string;
  email: string;
  createdAt: string;
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
  isBookmarked?: boolean;
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
