'use client';

import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from "@/components/ui/card";
import HeroSection from "../components/main/HeroSection";
import HeroCards from "../components/main/HeroCards";
import { HeroBrands } from "../components/main/HeroBrands";
import PreFooter from "../components/main/PreFooter";

export default function Home() {
  return (
    <div className="min-h-screen text-foreground footer-shadow">
      <main className="pt-20 p-8 flex flex-col items-center align-center">
        <HeroSection />
        <HeroBrands />
        <HeroCards />

        <PreFooter />
      </main>
    </div>
  );
}