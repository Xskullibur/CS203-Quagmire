'use client';

import HeroSection from "../components/main/HeroSection";
import HeroCards from "../components/main/HeroCards";
import { HeroBrands } from "../components/main/HeroBrands";
import PreFooter from "../components/main/PreFooter";
import TournamentCardViewerCarousel from "../components/tournaments/TournamentCardViewerCarousel";

export default function Home() {
  return (
    <div className="min-h-screen text-foreground footer-shadow">
      <main className="pt-20 p-8 flex flex-col items-center align-center">
        <HeroSection />
        <HeroBrands />
        <HeroCards />
        <TournamentCardViewerCarousel />
      </main>
      <PreFooter />
    </div>
  );
}