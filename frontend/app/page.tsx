'use client';

import { useState, useEffect } from 'react';
import HeroSection from "../components/main/HeroSection";
import HeroCards from "../components/main/HeroCards";
import { HeroBrands } from "../components/main/HeroBrands";
import PreFooter from "../components/main/PreFooter";
import HeroVideos from "../components/main/HeroVideos";
import TournamentCardViewerCarousel from "../components/tournaments/TournamentCardViewerCarousel";
import { Skeleton } from "@/components/ui/skeleton";

export default function Home() {
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    // Simulate a delay to ensure all components are ready
    const timer = setTimeout(() => {
      setIsLoading(false);
    }, 100); // Adjust this delay as needed

    return () => clearTimeout(timer);
  }, []);

  if (isLoading) {
    return (
      <div className="min-h-screen p-8 space-y-4 m-16">
        <Skeleton className="h-[300px] w-full rounded-lg" />
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <Skeleton className="h-[200px] w-full rounded-lg" />
          <Skeleton className="h-[200px] w-full rounded-lg" />
          <Skeleton className="h-[200px] w-full rounded-lg" />
        </div>
        <Skeleton className="h-[400px] w-full rounded-lg" />
      </div>
    );
  }

  return (
    <div className="min-h-screen text-foreground">
      <main className="pt-20 p-8 flex flex-col items-center align-center">
        <HeroSection />
        <HeroBrands />
        <TournamentCardViewerCarousel />
        <HeroCards />
        <HeroVideos />
      </main>
      <PreFooter />
    </div>
  );
}