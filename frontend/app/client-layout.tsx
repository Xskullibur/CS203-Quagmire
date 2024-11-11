"use client";

import Footer from "@/components/layout/Footer";
import AmbientLight from "@/components/layout/AmbientLight";
import MenuBar from "@/components/layout/MenuBar";
import { AuthProvider } from "@/hooks/useAuth";
import { ErrorHandlerProvider } from "./context/ErrorMessageProvider";
import { Toaster } from "@/components/ui/toaster";
import { AlertDialogProvider } from "./context/AlertDialogContext";
import { GlobalAlertDialog } from "@/components/GlobalAlertDialog";
import { Suspense } from "react";

export default function ClientLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <Suspense>
      <AlertDialogProvider>
        <GlobalAlertDialog />
        <ErrorHandlerProvider>
          <AuthProvider>
            <Toaster />
            <AmbientLight />
            <MenuBar />
            {children}
            <Footer />
          </AuthProvider>
        </ErrorHandlerProvider>
      </AlertDialogProvider>
    </Suspense>
  );
}
