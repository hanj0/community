import type { CreatePostRequest, PostDetail, PagedResponse, PostSummary, CommentData } from '../types';

async function handleResponse<T>(res: Response): Promise<T> {
  const body = await res.json().catch(() => null);
  if (!res.ok) {
    throw new Error(body?.error?.message ?? '서버 오류가 발생했습니다.');
  }
  return body.data as T;
}

function buildUrl(path: string, params: Record<string, string | number | undefined>): string {
  const url = new URL(path, window.location.origin);
  for (const [k, v] of Object.entries(params)) {
    if (v !== undefined && v !== '') url.searchParams.set(k, String(v));
  }
  return url.toString();
}

export async function fetchPosts(params: {
  page?: number;
  size?: number;
  channelId?: string;
  sort?: string;
  search?: string;
}): Promise<PagedResponse<PostSummary>> {
  const res = await fetch(
    buildUrl('/api/posts', params as Record<string, string | number | undefined>),
    { credentials: 'include' },
  );
  if (!res.ok) throw new Error('게시글 목록을 불러올 수 없습니다.');
  return res.json() as Promise<PagedResponse<PostSummary>>;
}

export async function fetchHotPosts(params: {
  page?: number;
  size?: number;
  channelId?: string;
  period?: string;
}): Promise<PagedResponse<PostSummary>> {
  const res = await fetch(
    buildUrl('/api/posts/hot', params as Record<string, string | number | undefined>),
    { credentials: 'include' },
  );
  if (!res.ok) throw new Error('인기글 목록을 불러올 수 없습니다.');
  return res.json() as Promise<PagedResponse<PostSummary>>;
}

interface RawPostDetail {
  id: number;
  title: string;
  content: string;
  userInfo: { id: number; username: string } | null;
  channelInfo: { id: number; name: string; description: string } | null;
  viewCount: number;
  likeCount: number;
  commentCount: number;
  createdAt: string;
  isPinned?: boolean;
}

export async function fetchPostDetail(id: number): Promise<PostDetail> {
  const res = await fetch(`/api/posts/${id}`, { credentials: 'include' });
  const raw = await handleResponse<RawPostDetail>(res);
  return {
    ...raw,
    authorId: raw.userInfo?.id ?? 0,
    authorName: raw.userInfo?.username ?? '',
    channelId: String(raw.channelInfo?.id ?? ''),
    channelName: raw.channelInfo?.name ?? '',
  };
}

export async function updatePost(id: number, data: { title: string; content: string }): Promise<void> {
  const res = await fetch(`/api/posts/${id}`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include',
    body: JSON.stringify(data),
  });
  await handleResponse<unknown>(res);
}

export async function updateComment(id: number, content: string): Promise<void> {
  const res = await fetch(`/api/comments/${id}`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include',
    body: JSON.stringify({ content }),
  });
  await handleResponse<unknown>(res);
}

export async function fetchComments(
  postId: number,
  params: { page?: number; size?: number },
): Promise<PagedResponse<CommentData>> {
  const res = await fetch(
    buildUrl(`/api/posts/${postId}/comments`, params as Record<string, number | undefined>),
    { credentials: 'include' },
  );
  if (!res.ok) throw new Error('댓글을 불러올 수 없습니다.');
  return res.json() as Promise<PagedResponse<CommentData>>;
}

export async function createComment(postId: number, content: string): Promise<CommentData> {
  const res = await fetch(`/api/posts/${postId}/comments`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include',
    body: JSON.stringify({ content }),
  });
  return handleResponse<CommentData>(res);
}

export async function fetchReplies(commentId: number): Promise<CommentData[]> {
  const res = await fetch(
    buildUrl(`/api/comments/${commentId}/replies`, { size: 100 }),
    { credentials: 'include' },
  );
  if (!res.ok) throw new Error('답글을 불러올 수 없습니다.');
  const body = await res.json();
  return body.data as CommentData[];
}

export async function createPost(data: CreatePostRequest): Promise<PostDetail> {
  const res = await fetch('/api/posts', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include',
    body: JSON.stringify(data),
  });
  return handleResponse<PostDetail>(res);
}
