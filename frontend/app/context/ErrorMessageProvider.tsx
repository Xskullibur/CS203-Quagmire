import React, {
  createContext,
  useContext,
  ReactNode,
  useMemo,
  useCallback,
} from "react";
import { ToastProvider, ToastViewport } from "@/components/ui/toast";
import { useToast } from "@/hooks/use-toast";
import { ErrorHandler } from "@/utils/errorHandler";
import { AxiosError } from "axios";

interface ErrorHandlerContextType {
  showErrorToast: (title: string, message: string) => void;
  handleError: (error: AxiosError) => void;
}

const ErrorHandlerContext = createContext<ErrorHandlerContextType | undefined>(
  undefined
);

export const useGlobalErrorHandler = (): ErrorHandlerContextType => {
  const context = useContext(ErrorHandlerContext);
  if (!context) {
    throw new Error(
      "useErrorHandler must be used within an ErrorHandlerProvider"
    );
  }
  return context;
};

interface ErrorHandlerProviderProps {
  children: ReactNode;
}

export const ErrorHandlerProvider: React.FC<ErrorHandlerProviderProps> = ({
  children,
}) => {
  const { toast } = useToast();

  const showErrorToast = useCallback(
    (title: string, message: string) => {
      toast({
        variant: "destructive",
        title,
        description: message,
      });
    },
    [toast]
  );

  const handleError = useCallback(
    (error: AxiosError) => {
      const { title, message } = ErrorHandler.handleError(error);
      showErrorToast(title, message);
    },
    [showErrorToast]
  );

  const contextValue = useMemo(
    () => ({ showErrorToast, handleError }),
    [showErrorToast, handleError]
  );

  return (
    <ErrorHandlerContext.Provider value={contextValue}>
      <ToastProvider>
        {children}
        <ToastViewport />
      </ToastProvider>
    </ErrorHandlerContext.Provider>
  );
};
