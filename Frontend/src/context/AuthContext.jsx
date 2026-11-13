import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { hospitalApi } from '../api/hospitalApi.js';
import { authStorage } from '../api/client.js';

const AuthContext = createContext(null);

function readStoredUser() {
  try {
    return JSON.parse(localStorage.getItem(authStorage.USER_KEY) || 'null');
  } catch {
    localStorage.removeItem(authStorage.USER_KEY);
    localStorage.removeItem(authStorage.TOKEN_KEY);
    return null;
  }
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => (
    localStorage.getItem(authStorage.TOKEN_KEY) ? readStoredUser() : null
  ));

  const logout = useCallback(() => {
    localStorage.removeItem(authStorage.TOKEN_KEY);
    localStorage.removeItem(authStorage.USER_KEY);
    setUser(null);
  }, []);

  useEffect(() => {
    window.addEventListener('hms:unauthorized', logout);
    return () => window.removeEventListener('hms:unauthorized', logout);
  }, [logout]);

  const login = useCallback(async (credentials) => {
    const { data } = await hospitalApi.login(credentials);
    if (data.role !== 'PATIENT') {
      throw new Error('This portal is for patients. Please sign in with a patient account.');
    }
    localStorage.setItem(authStorage.TOKEN_KEY, data.token);
    const patientUser = {
      id: data.id,
      name: data.name,
      email: data.email,
      phone: data.phone,
      role: data.role,
    };
    localStorage.setItem(authStorage.USER_KEY, JSON.stringify(patientUser));
    setUser(patientUser);
    return patientUser;
  }, []);

  const value = useMemo(() => ({
    user,
    isAuthenticated: Boolean(user && localStorage.getItem(authStorage.TOKEN_KEY)),
    login,
    logout,
  }), [user, login, logout]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used within AuthProvider');
  return context;
}
