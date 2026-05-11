export interface Channel {
  id: string;
  name: string;
  color: string;
}

export interface BasePost {
  id: number;
  title: string;
  channel: string;
  channelColor: string;
  likes: number;
  comments: number;
  views: number;
  time: string;
  author: string;
  hasImg: boolean;
}

export interface Post extends BasePost {
  score: number;
  isPin: boolean;
}

export interface FeedPost extends BasePost {
  isNotice: boolean;
}

export type AnyPost = Post | FeedPost;

export interface Reply {
  id: number;
  author: string;
  av: string;
  ab: string;
  ac: string;
  time: string;
  text: string;
  likes: number;
  dislikes: number;
  isMention: boolean;
}

export interface Comment {
  id: number;
  author: string;
  av: string;
  ab: string;
  ac: string;
  time: string;
  text: string;
  likes: number;
  dislikes: number;
  isMention?: boolean;
  replies: Reply[];
}

export interface TrendingItem {
  w: string;
  d: string;
}

export type PageType = 'home' | 'hot' | 'all' | 'channel' | 'detail' | 'login' | 'signup' | 'mypage';
export type SortType = 'latest' | 'likes' | 'comments' | 'views';
export type ViewType = 'card' | 'compact';
export type PeriodType = '24h' | '7d' | '30d';

export interface User {
  id: number;
  username: string;
  email: string;
  createdAt: string;
}

export interface AuthResponse {
  token: string;
  user: User;
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
