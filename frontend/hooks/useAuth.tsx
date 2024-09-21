import { useState, useEffect, useContext, createContext, useMemo, useCallback, useRef } from "react";
import { useRouter } from "next/navigation";
import { AxiosResponse } from "axios";
import Cookies from "js-cookie";
import { User } from "@/models/user";
import axiosInstance from "@/lib/axios";

interface AuthContextType {
  user: User | null;
  loading: boolean;
  login: (username: string, password: string) => Promise<{ user: User; response: AxiosResponse<any, any>; }>;
  logout: () => void;
  isAuthenticated: boolean;
}

const AUTH_TOKEN = "authToken";
const AuthContext = createContext<AuthContextType | null>(null);
const API_URL = process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL || '';

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const router = useRouter();
  const tokenValidationAttempted = useRef(false);

  useEffect(() => {

    const validateToken = async (token: string) => {

      if (tokenValidationAttempted.current) return;
      tokenValidationAttempted.current = true;

      try {
        const response = await axiosInstance.post(`${API_URL}/authentication/validate-token`, { token });
        setUser(response.data);
        setIsAuthenticated(true);
      } catch (error) {
        console.error("Token validation failed:", error);
        Cookies.remove(AUTH_TOKEN);
      } finally {
        setLoading(false);
      }
    };

    const token = Cookies.get(AUTH_TOKEN);
    if (token) {
      validateToken(token);
    } else {
      setLoading(false);
    }
  }, []);

  const login = async (username: string, password: string) => {
    try {
      const response = await axiosInstance.post(`${API_URL}/authentication/login`, { username, password });
      const { token, user } = response.data;

      Cookies.set(AUTH_TOKEN, token, {
        expires: 1, // 1 day
        path: '/',
        sameSite: 'Lax'
      });

      setUser(user);
      setIsAuthenticated(true);
      setLoading(false);
      return { user, response };
    } catch (error) {
      console.error("Login failed:", error);
      throw error;
    }
  };

  const logout = useCallback(() => {
    Cookies.remove(AUTH_TOKEN, { path: '/' });
    setUser(null);
    setIsAuthenticated(false);
    router.push("/auth/login");
  }, [router]);

  const providerValue = useMemo(() => ({
    user,
    loading,
    login,
    logout,
    isAuthenticated,
  }), [user, loading, logout]);

  return (
    <AuthContext.Provider value={providerValue}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
};