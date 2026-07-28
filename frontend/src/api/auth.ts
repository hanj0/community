import type { LoginRequest, SignupRequest, User, UserRole } from '../types';

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
  const user = await handleResponse<User>(res);

  // 로그인 응답에는 role이 없다. 관리자 판별이 새로고침 전까지 밀리지 않게 me로 한 번 더 확인한다.
  const me = await getMe().catch(() => null);
  return me ? { ...user, role: me.role } : user;
}

export async function logout(): Promise<void> {
  await fetch(`${BASE}/logout`, {
    method: 'POST',
    credentials: 'include',
  });
}

/** 서버는 권한을 'ROLE_ADMIN' 형태로 내려준다. 접두사를 떼고 아는 값만 받는다. */
function normalizeRole(raw: string | undefined): UserRole | undefined {
  const role = raw?.replace(/^ROLE_/, '');
  return role === 'ADMIN' || role === 'USER' ? role : undefined;
}

export async function getMe(): Promise<User | null> {
  const res = await fetch(`${BASE}/me`, {
    credentials: 'include',
  });
  if (res.status === 401) return null;
  const user = await handleResponse<User & { role?: string }>(res);
  return { ...user, role: normalizeRole(user.role) };
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
