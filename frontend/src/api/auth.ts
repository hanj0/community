import type { LoginRequest, SignupRequest, User } from '../types';

const BASE = '/api/auth';

async function handleResponse<T>(res: Response): Promise<T> {
  const body = await res.json().catch(() => null);
  if (!res.ok) {
    throw new Error(body?.error?.message ?? '서버 오류가 발생했습니다.');
  }
  return body.data as T;
}

export async function signup(data: SignupRequest): Promise<User> {
  const res = await fetch(`${BASE}/signup`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include',
    body: JSON.stringify(data),
  });
  return handleResponse<User>(res);
}

export async function login(data: LoginRequest): Promise<User> {
  const res = await fetch(`${BASE}/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include',
    body: JSON.stringify(data),
  });
  return handleResponse<User>(res);
}

export async function logout(): Promise<void> {
  await fetch(`${BASE}/logout`, {
    method: 'POST',
    credentials: 'include',
  });
}

export async function getMe(): Promise<User | null> {
  const res = await fetch(`${BASE}/me`, {
    credentials: 'include',
  });
  if (res.status === 401) return null;
  return handleResponse<User>(res);
}

export async function changePassword(data: {
  currentPassword: string;
  newPassword: string;
}): Promise<void> {
  const res = await fetch(`${BASE}/password`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include',
    body: JSON.stringify(data),
  });
  if (!res.ok) {
    const body = await res.json().catch(() => null);
    throw new Error(body?.error?.message ?? '비밀번호 변경에 실패했습니다.');
  }
}
