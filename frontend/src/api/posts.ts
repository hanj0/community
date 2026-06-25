import type { CreatePostRequest, PostDetail, PagedResponse, PostSummary, CommentData, MyComment, ReactionType } from '../types';

async function handleResponse<T>(res: Response): Promise<T> {
  const body = await res.json().catch(() => null);
  if (!res.ok) {
    if (res.status === 401) {
      window.dispatchEvent(new CustomEvent('auth:unauthorized'));
    }
    throw new Error(body?.error?.message ?? '서버 오류가 발생했습니다.');
  }
  return body?.data as T;
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
  authorInfo: { id: number; username: string } | null;
  channelInfo: { id: number; name: string; description: string } | null;
  viewCount: number;
  likeCount: number;
  dislikeCount: number;
  reactionType: ReactionType | null;
  commentCount: number;
  createdAt: string;
  isPinned?: boolean;
  bookmarked?: boolean;
}

export async function fetchPostDetail(id: number): Promise<PostDetail> {
  const res = await fetch(`/api/posts/${id}`, { credentials: 'include' });
  const raw = await handleResponse<RawPostDetail>(res);
  return {
    ...raw,
    authorId: raw.authorInfo?.id ?? 0,
    authorName: raw.authorInfo?.username ?? '',
    channelId: String(raw.channelInfo?.id ?? ''),
    channelName: raw.channelInfo?.name ?? '',
  };
}

interface PostReactionResponse {
  postId: number;
  type: ReactionType;
  likeCount: number;
  dislikeCount: number;
}

export async function setPostReaction(postId: number, type: ReactionType): Promise<PostReactionResponse> {
  const res = await fetch(`/api/posts/${postId}/reaction`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include',
    body: JSON.stringify({ type }),
  });
  return handleResponse<PostReactionResponse>(res);
}

export async function deletePostReaction(postId: number): Promise<void> {
  const res = await fetch(`/api/posts/${postId}/reaction`, {
    method: 'DELETE',
    credentials: 'include',
  });
  if (!res.ok) {
    if (res.status === 401) window.dispatchEvent(new CustomEvent('auth:unauthorized'));
    throw new Error('반응을 취소할 수 없습니다.');
  }
}

interface CommentReactionResponse {
  commentId: number;
  type: ReactionType;
  likeCount: number;
  dislikeCount: number;
}

export async function setCommentReaction(commentId: number, type: ReactionType): Promise<CommentReactionResponse> {
  const res = await fetch(`/api/comments/${commentId}/reaction`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include',
    body: JSON.stringify({ type }),
  });
  return handleResponse<CommentReactionResponse>(res);
}

export async function deleteCommentReaction(commentId: number): Promise<void> {
  const res = await fetch(`/api/comments/${commentId}/reaction`, {
    method: 'DELETE',
    credentials: 'include',
  });
  if (!res.ok) {
    if (res.status === 401) window.dispatchEvent(new CustomEvent('auth:unauthorized'));
    throw new Error('반응을 취소할 수 없습니다.');
  }
}

export async function toggleBookmark(postId: number, add: boolean): Promise<boolean> {
  const res = await fetch(`/api/posts/${postId}/bookmark`, {
    method: add ? 'PUT' : 'DELETE',
    credentials: 'include',
  });
  if (res.status === 401) window.dispatchEvent(new CustomEvent('auth:unauthorized'));
  return res.status === 204;
}

export async function fetchMyStats(): Promise<{ postCount: number; commentCount: number; createdAt: string }> {
  const res = await fetch('/api/users/me/stats', { credentials: 'include' });
  return handleResponse<{ postCount: number; commentCount: number; createdAt: string }>(res);
}

export async function fetchMyPosts(params: { page?: number; size?: number }): Promise<PagedResponse<PostSummary>> {
  const res = await fetch(
    buildUrl('/api/users/me/posts', params as Record<string, string | number | undefined>),
    { credentials: 'include' },
  );
  if (!res.ok) throw new Error('내 게시글을 불러올 수 없습니다.');
  return res.json() as Promise<PagedResponse<PostSummary>>;
}

export async function fetchMyComments(params: { page?: number; size?: number }): Promise<PagedResponse<MyComment>> {
  const res = await fetch(
    buildUrl('/api/users/me/comments', params as Record<string, string | number | undefined>),
    { credentials: 'include' },
  );
  if (!res.ok) throw new Error('내 댓글을 불러올 수 없습니다.');
  return res.json() as Promise<PagedResponse<MyComment>>;
}

export async function fetchMyBookmarks(params: { page?: number; size?: number }): Promise<PagedResponse<PostSummary>> {
  const res = await fetch(
    buildUrl('/api/users/me/bookmarks', params as Record<string, string | number | undefined>),
    { credentials: 'include' },
  );
  if (!res.ok) throw new Error('북마크를 불러올 수 없습니다.');
  return res.json() as Promise<PagedResponse<PostSummary>>;
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

export async function deletePost(id: number): Promise<void> {
  const res = await fetch(`/api/posts/${id}`, {
    method: 'DELETE',
    credentials: 'include',
  });
  await handleResponse<unknown>(res);
}

export async function deleteComment(id: number): Promise<void> {
  const res = await fetch(`/api/comments/${id}`, {
    method: 'DELETE',
    credentials: 'include',
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

export async function createComment(postId: number, content: string, parentId?: number): Promise<CommentData> {
  const res = await fetch(`/api/posts/${postId}/comments`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include',
    body: JSON.stringify(parentId !== undefined ? { content, parentId } : { content }),
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
