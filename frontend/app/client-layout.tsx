"use client";

import MenuBar from "@/components/layout/MenuBar";
import { AuthProvider } from "@/hooks/useAuth";

export default function ClientLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <AuthProvider>
      <MenuBar />
      {children}
    </AuthProvider>
  );
}