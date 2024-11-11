"use client";

import Footer from "@/components/layout/Footer";
import AmbientLight from "@/components/layout/AmbientLight";
import MenuBar from "@/components/layout/MenuBar";
import { AuthProvider } from "@/hooks/useAuth";
import { ErrorHandlerProvider } from "./context/ErrorMessageProvider";
import { Toaster } from "@/components/ui/toaster";
import { Suspense } from "react";

export default function ClientLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <Suspense>
      <ErrorHandlerProvider>
        <AuthProvider>
          <Toaster />
          <AmbientLight />
          <MenuBar />
          {children}
          <Footer />
        </AuthProvider>
      </ErrorHandlerProvider>
    </Suspense>
  );
}
