import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { UserProfile } from '../types';
import { authApi } from '../api/services';
import { getAccessToken, setTokens, clearTokens } from '../api/client';

interface AuthContextType {
  user: UserProfile | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (usernameOrEmail: string, password?: string) => Promise<void>;
  logout: () => Promise<void>;
  hasPermission: (permission: string) => boolean;
  hasRole: (role: string) => boolean;
  refreshUser: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<UserProfile | null>(() => {
    const saved = localStorage.getItem('abaxial_user');
    return saved ? JSON.parse(saved) : null;
  });
  const [isLoading, setIsLoading] = useState<boolean>(true);

  const refreshUser = useCallback(async () => {
    try {
      const profile = await authApi.getMe();
      setUser(profile);
      localStorage.setItem('abaxial_user', JSON.stringify(profile));
    } catch (err) {
      console.warn('Error refrescando perfil del usuario:', err);
      setUser(null);
      clearTokens();
    }
  }, []);

  useEffect(() => {
    const token = getAccessToken();
    if (token) {
      refreshUser().finally(() => setIsLoading(false));
    } else {
      setIsLoading(false);
    }
  }, [refreshUser]);

  const login = async (usernameOrEmail: string | { usernameOrEmail: string; password?: string }, password?: string) => {
    setIsLoading(true);
    try {
      let reqBody: { usernameOrEmail: string; password: string };
      if (typeof usernameOrEmail === 'object' && usernameOrEmail !== null) {
        reqBody = {
          usernameOrEmail: usernameOrEmail.usernameOrEmail,
          password: usernameOrEmail.password || '',
        };
      } else {
        reqBody = {
          usernameOrEmail,
          password: password || '',
        };
      }

      const res = await authApi.login(reqBody);
      setTokens(res.accessToken, res.refreshToken);
      setUser(res.user);
      localStorage.setItem('abaxial_user', JSON.stringify(res.user));
    } finally {
      setIsLoading(false);
    }
  };

  const logout = async () => {
    try {
      await authApi.logout();
    } catch (e) {
      // Ignorar error al cerrar sesión
    } finally {
      clearTokens();
      setUser(null);
    }
  };

  const hasRole = useCallback(
    (role: string): boolean => {
      if (!user) return false;
      return user.roles.includes(role) || user.roles.includes('SUPER_ADMIN');
    },
    [user]
  );

  const hasPermission = useCallback(
    (permission: string): boolean => {
      if (!user) return false;
      if (user.roles.includes('SUPER_ADMIN')) return true;
      return user.permisos.includes(permission);
    },
    [user]
  );

  return (
    <AuthContext.Provider
      value={{
        user,
        isAuthenticated: !!user,
        isLoading,
        login: login as any,
        logout,
        hasPermission,
        hasRole,
        refreshUser,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth debe usarse dentro de un AuthProvider');
  }
  return context;
};
