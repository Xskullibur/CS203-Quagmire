import {
  useState,
  useEffect,
  useContext,
  createContext,
  useMemo,
  useCallback,
  useRef,
} from "react";
import { useRouter } from "next/navigation";
import axios, { AxiosResponse } from "axios";
import Cookies from "js-cookie";
import { User } from "@/models/user";
import axiosInstance from "@/lib/axios";
import { useErrorHandler } from "@/app/context/ErrorMessageProvider";
import { ErrorCodes } from "@/types/error-codes";

interface AuthContextType {
  user: User | null;
  isLoading: boolean;
  login: (
    username: string,
    password: string
  ) => Promise<{ user: User; response: AxiosResponse<any, any> }>;
  logout: () => void;
  isAuthenticated: boolean;
}

const AUTH_TOKEN = "authToken";
const AuthContext = createContext<AuthContextType | null>(null);
const API_URL = process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL || "";

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const router = useRouter();
  const tokenValidationAttempted = useRef(false);
  const { showErrorToast } = useErrorHandler();

  useEffect(() => {
    const validateToken = async (token: string) => {
      if (tokenValidationAttempted.current) return;
      tokenValidationAttempted.current = true;

      try {
        const response = await axiosInstance.post(
          `${API_URL}/authentication/validate-token`,
          { token }
        );
        setUser(response.data);
        setIsAuthenticated(true);
      } catch (error) {
        
        if (axios.isAxiosError(error)) {

          if (error.status === 401) {

            switch (error.response?.data.errorCode) {
              case ErrorCodes.TOKEN_EXPIRED:
                showErrorToast(
                  "Error",
                  "Your session has expired, please login to continue."
                );
                break;

              default:
                showErrorToast("Error", "Please login to continue");
                break;
            }

          }
          else if (error.status === 403) {
            showErrorToast("Error", "You are not authorized to view this page. If you believe this is a message please contact an administrator.")
          }
        }

        console.error("Token validation failed:", error);
        Cookies.remove(AUTH_TOKEN);
      } finally {
        setIsLoading(false);
      }
    };

    const token = Cookies.get(AUTH_TOKEN);
    if (token) {
      validateToken(token);
    } else {
      setIsLoading(false);
    }
  }, [showErrorToast]);

  const login = async (username: string, password: string) => {
    try {
      const response = await axiosInstance.post(
        `${API_URL}/authentication/login`,
        { username, password }
      );
      const { token, user } = response.data;

      Cookies.set(AUTH_TOKEN, token, {
        expires: 1, // 1 day
        path: "/",
        sameSite: "Lax",
      });

      setUser(user);
      setIsAuthenticated(true);
      setIsLoading(false);
      return { user, response };
    } catch (error) {
      console.error("Login failed:", error);
      throw error;
    }
  };

  const logout = useCallback(async () => {
    try {
      await axiosInstance.get(`${API_URL}/logout`);
      Cookies.remove(AUTH_TOKEN, { path: "/" });
      setUser(null);
      setIsAuthenticated(false);
      router.push("/auth/login");
    } catch (error) {
      console.error("Logout failed:", error);
      throw error;
    }
  }, [router]);

  const providerValue = useMemo(
    () => ({
      user,
      isLoading,
      login,
      logout,
      isAuthenticated,
    }),
    [user, isLoading, logout, isAuthenticated]
  );

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
