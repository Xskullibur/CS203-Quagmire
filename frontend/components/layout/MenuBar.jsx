// components/MenuBar.jsx
'use client';

import Link from 'next/link';
import { useState, useEffect } from 'react';

export default function MenuBar() {
  const [isScrolled, setIsScrolled] = useState(false);

  useEffect(() => {
    const handleScroll = () => {
      setIsScrolled(window.scrollY > 10);
    };

    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  return (
    <div className="fixed top-4 left-1/2 transform -translate-x-1/2 w-11/12 max-w-6xl z-50">
      <nav className={`
        px-6 py-2 rounded-full transition-all duration-300
        ${isScrolled ? 'bg-zinc-900/90 backdrop-blur-sm border border-zinc-700' : 'bg-transparent'}
      `}>
        <div className="flex items-center justify-between">
          <Link href="/" className="text-xl font-bold text-white">HH</Link>

          <div className="flex items-center space-x-6">
            <Link href="/" className="text-sm text-white hover:text-gray-600 transition">Home</Link>
            <Link href="/about" className="text-sm text-white hover:text-gray-600 transition">About</Link>
            <Link href="/schedule" className="text-sm text-white hover:text-gray-600 transition">Schedule</Link>
            <Link href="/tournaments" className="text-sm text-white hover:text-gray-600 transition">Tournaments</Link>
            <Link href="/profile" className="text-sm text-white hover:text-gray-600 transition">Profile</Link>
            <Link href="/auth/login" className="text-sm text-white hover:text-gray-600 transition">Log in</Link>
            <Link href="/auth/register" className="text-sm bg-zinc-500 text-white px-4 py-2 rounded-full hover:bg-gray-700 transition">Sign up</Link>
          </div>
        </div>
      </nav>
    </div>
  );
}