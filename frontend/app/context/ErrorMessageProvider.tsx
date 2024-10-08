import React, {
  createContext,
  useContext,
  ReactNode,
  useMemo,
  useCallback,
} from "react";
import { ToastProvider, ToastViewport } from "@/components/ui/toast";
import { useToast } from "@/hooks/use-toast";

interface ErrorHandlerContextType {
  showErrorToast: (title: string, message: string) => void;
}

const ErrorHandlerContext = createContext<ErrorHandlerContextType | undefined>(
  undefined
);

export const useErrorHandler = (): ErrorHandlerContextType => {
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

  const contextValue = useMemo(() => ({ showErrorToast }), [showErrorToast]);

  return (
    <ErrorHandlerContext.Provider value={contextValue}>
      <ToastProvider>
        {children}
        <ToastViewport />
      </ToastProvider>
    </ErrorHandlerContext.Provider>
  );
};
