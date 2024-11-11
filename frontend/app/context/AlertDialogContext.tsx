import React, { createContext, useContext, useState } from 'react';

type AlertDialogVariant = 'alert' | 'confirm';

interface BaseAlertDialogOptions {
  title?: string;
  description: string;
  variant: AlertDialogVariant;
}

interface AlertOptions extends BaseAlertDialogOptions {
  variant: 'alert';
  confirmText?: string;
  onConfirm?: () => void;
}

interface ConfirmOptions extends BaseAlertDialogOptions {
  variant: 'confirm';
  confirmText?: string;
  cancelText?: string;
  onConfirm?: () => void;
  onCancel?: () => void;
}

type AlertDialogOptions = AlertOptions | ConfirmOptions;

interface AlertDialogContextType {
  showAlert: (options: AlertOptions) => void;
  showConfirm: (options: ConfirmOptions) => void;
  hideAlert: () => void;
  isOpen: boolean;
  options: AlertDialogOptions | null;
}

const AlertDialogContext = createContext<AlertDialogContextType | undefined>(undefined);

export function AlertDialogProvider({ children }: { children: React.ReactNode }) {
  const [isOpen, setIsOpen] = useState(false);
  const [options, setOptions] = useState<AlertDialogOptions | null>(null);

  const showAlert = (options: AlertOptions) => {
    setOptions(options);
    setIsOpen(true);
  };

  const showConfirm = (options: ConfirmOptions) => {
    setOptions(options);
    setIsOpen(true);
  };

  const hideAlert = () => {
    setIsOpen(false);
    setOptions(null);
  };

  return (
    <AlertDialogContext.Provider value={{ showAlert, showConfirm, hideAlert, isOpen, options }}>
      {children}
    </AlertDialogContext.Provider>
  );
}

export const useAlertDialog = () => {
  const context = useContext(AlertDialogContext);
  if (!context) {
    throw new Error('useAlertDialog must be used within an AlertDialogProvider');
  }
  return context;
};
