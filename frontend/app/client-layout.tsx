"use client";

import Footer from "@/components/layout/Footer";
import AmbientLight from "@/components/layout/AmbientLight";
import MenuBar from "@/components/layout/MenuBar";
import { AuthProvider } from "@/hooks/useAuth";
import { ErrorHandlerProvider } from "./context/ErrorMessageProvider";
import { Toaster } from "@/components/ui/toaster";

export default function ClientLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <AuthProvider>
      <ErrorHandlerProvider>
      <Toaster />
      <AmbientLight />
      <MenuBar />
      {children}
      <Footer />
      </ErrorHandlerProvider>
    </AuthProvider>
  );
}