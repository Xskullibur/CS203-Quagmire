import React, { createContext, useContext, ReactNode } from 'react';
import { Toast, ToastProvider, ToastViewport } from "@/components/ui/toast"
import { useToast } from '@/hooks/use-toast';

interface ErrorHandlerContextType {
  showErrorToast: (title: string, message: string) => void;
}

const ErrorHandlerContext = createContext<ErrorHandlerContextType | undefined>(undefined);

export const useErrorHandler = (): ErrorHandlerContextType => {
  const context = useContext(ErrorHandlerContext);
  if (!context) {
    throw new Error('useErrorHandler must be used within an ErrorHandlerProvider');
  }
  return context;
};

interface ErrorHandlerProviderProps {
  children: ReactNode;
}

export const ErrorHandlerProvider: React.FC<ErrorHandlerProviderProps> = ({ children }) => {
  const { toast } = useToast();

  const showErrorToast = (title: string, message: string) => {
    toast({
      variant: "destructive",
      title,
      description: message,
    });
  };

  return (
    <ErrorHandlerContext.Provider value={{ showErrorToast }}>
      <ToastProvider>
        {children}
        <ToastViewport />
      </ToastProvider>
    </ErrorHandlerContext.Provider>
  );
};