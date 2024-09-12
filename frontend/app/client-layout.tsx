"use client";

import Footer from "@/components/layout/Footer";
import AmbientLight from "@/components/layout/AmbientLight";
import MenuBar from "@/components/layout/MenuBar";
import { AuthProvider } from "@/hooks/useAuth";

export default function ClientLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <AuthProvider>
      <AmbientLight />
      <MenuBar />
      {children}
      <Footer />
    </AuthProvider>
  );
}