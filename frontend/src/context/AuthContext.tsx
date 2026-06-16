import { createContext, useContext, useState, useEffect, type ReactNode } from 'react';
import type { User } from '../types';
import { getMe, logout as apiLogout } from '../api/auth';

interface AuthContextValue {
  user: User | null;
  loading: boolean;
  setUser: (user: User | null) => void;
  logout: () => void;
  showLoginPrompt: boolean;
  dismissLoginPrompt: () => void;
}

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const [showLoginPrompt, setShowLoginPrompt] = useState(false);

  useEffect(() => {
    getMe().then(setUser).finally(() => setLoading(false));
  }, []);

  useEffect(() => {
    const handler = () => setShowLoginPrompt(true);
    window.addEventListener('auth:unauthorized', handler);
    return () => window.removeEventListener('auth:unauthorized', handler);
  }, []);

  function logout() {
    apiLogout().finally(() => setUser(null));
  }

  function dismissLoginPrompt() {
    setShowLoginPrompt(false);
  }

  return (
    <AuthContext.Provider value={{ user, loading, setUser, logout, showLoginPrompt, dismissLoginPrompt }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used inside AuthProvider');
  return ctx;
}
