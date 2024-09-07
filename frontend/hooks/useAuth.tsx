import { useState, useEffect, useContext, createContext, useMemo, useCallback } from "react";
import { useRouter } from "next/navigation";
import axios, { AxiosResponse } from "axios";
import Cookies from "js-cookie";
import { User } from "@/models/user";

interface AuthContextType {
  user: User | null;
  loading: boolean;
  login: (email: string, password: string) => Promise<AxiosResponse<any, any>>;
  logout: () => void;
  isAuthenticated: () => boolean;
}

const AUTH_TOKEN = "authToken";
const AuthContext = createContext<AuthContextType | null>(null);
const API_URL = `${process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL}`;

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const router = useRouter();

  useEffect(() => {
    // Check if there's a token in cookies
    const token = Cookies.get(AUTH_TOKEN);
    if (token) {
      // Validate token with the backend
      validateToken(token);
    } else {
      setLoading(false);
    }
  }, []);

  const validateToken = async (token: string) => {
    try {
      const response = await axios.post(
        new URL("/authentication/validate-token", API_URL).toString(),
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
          }
        }
      );
      setUser(response.data);
    } catch (error) {
      console.error("Token validation failed:", error);
      Cookies.remove(AUTH_TOKEN);
    } finally {
      setLoading(false);
    }
  };

  const login = async (username: string, password: string) => {
    try {
      const response = await axios.post(
        new URL("/authentication/login", API_URL).toString(),
        {
          username,
          password,
        }
      );
      const { token, user } = response.data;
      Cookies.set(AUTH_TOKEN, token, {
        secure: true,
        sameSite: "Lax",
        expires: 10,
      });
      setUser(user);
      return response;
    } catch (error) {
      console.error("Login failed:", error);
      throw error;
    }
  };

  const logout = useCallback(() => {
    Cookies.remove(AUTH_TOKEN);
    setUser(null);
    router.push("/login");
  }, [setUser, router]);

  const isAuthenticated = useCallback(() => {
    return !!user;
  }, [user]);

  const providerValue = useMemo(() => ({
    user,
    loading,
    login,
    logout,
    isAuthenticated,
  }), [isAuthenticated, loading, logout, user]);

  return (
    <AuthContext.Provider
      value={providerValue}
    >
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
