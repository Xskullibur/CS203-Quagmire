import { useAuth } from '@/hooks/useAuth';
import Link from 'next/link';
import { useState, useEffect } from 'react';

export default function MenuBar() {
  const [isScrolled, setIsScrolled] = useState(false);
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const { user, logout, isAuthenticated, isLoading } = useAuth();

  useEffect(() => {
    const handleScroll = () => {
      setIsScrolled(window.scrollY > 10);
      document.getElementsByClassName('logo')[0].classList.toggle('text-accent', window.scrollY > 10);
    };
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  const renderLinks = () => {
    if (isLoading) {
      return null;
    }

    if (!isAuthenticated) {
      return (
        <>
          <Link href="/auth/login" className="text-sm text-white hover:text-gray-600 transition">Log in</Link>
          <Link href="/auth/register" className="text-sm bg-zinc-500 text-white px-4 py-2 rounded-full hover:bg-gray-700 transition">Sign up</Link>
        </>
      );
    }

    return (
      <>
        <Link href="/profile" className="text-sm text-white hover:text-gray-600 transition">Profile</Link>
        <button onClick={logout} className="text-sm text-white hover:text-gray-600 transition">Logout</button>
        <Link href="/match" className="text-sm bg-zinc-500 text-white px-4 py-2 rounded-full hover:bg-gray-700 transition">Queue</Link>
      </>
    );
  };

  const renderMobileLinks = () => {
    if (isLoading) {
      return null;
    }

    if (!isAuthenticated) {
      return (
        <>
          <Link href="/auth/login" className="text-xl text-white hover:text-gray-400 transition" onClick={() => setIsMenuOpen(false)}>Log in</Link>
          <Link href="/auth/register" className="text-xl bg-zinc-500 text-white px-6 py-2 rounded-full hover:bg-gray-700 transition" onClick={() => setIsMenuOpen(false)}>Sign up</Link>
        </>
      );
    }

    return (
      <>
        <Link href="/profile" className="text-xl text-white hover:text-gray-400 transition" onClick={() => setIsMenuOpen(false)}>Profile</Link>
        <Link href="/match" className="text-xl text-white hover:text-gray-400 transition" onClick={() => setIsMenuOpen(false)}>Queue</Link>
        <button onClick={() => { logout(); setIsMenuOpen(false); }} className="text-xl text-white hover:text-gray-400 transition">Logout</button>
      </>
    );
  };

  return (
    <>
      <div className="fixed top-4 left-0 right-0 mx-auto w-11/12 max-w-6xl z-50">
        <nav className={`
          px-6 py-2 rounded-full transition-all duration-200
          ${isScrolled ? 'bg-zinc-900/70 backdrop-blur-xl border border-zinc-800/60' : 'bg-transparent'}
        `}>
          <div className="flex items-center justify-between">
            <Link href="/" className="logo text-md font-bold font-mono text-white hover:text-gray-600 transition">Quagmire</Link>

            <div className="hidden md:flex absolute left-1/2 top-1/2 transform -translate-x-1/2 -translate-y-1/2">
              <div className="flex items-center space-x-6">
                <Link href="/tournaments" className="text-sm text-white hover:text-gray-600 transition">Tournaments</Link>
                <Link href="/leaderboard" className="text-sm text-white hover:text-gray-600 transition">Leaderboard</Link>
                {user?.role === 'ADMIN' && (
                  <Link href="/admin/dashboard" className="text-sm text-white hover:text-gray-600 transition">Dashboard</Link>
                )}
              </div>
            </div>

            {/* Hamburger menu for mobile */}
            <button
              className="md:hidden text-white text-2xl"
              onClick={() => setIsMenuOpen(true)}
            >
              ☰
            </button>

            <div className="hidden md:flex items-center space-x-6">
              {renderLinks()}
            </div>
          </div>
        </nav>
      </div>

      {/* Mobile menu overlay */}
      <div
        className={`fixed inset-0 bg-black z-50 transition-opacity duration-300 ease-in-out ${isMenuOpen ? 'opacity-90' : 'opacity-0 pointer-events-none'
          }`}
      >
        <div
          className={`flex flex-col items-center justify-center h-full transition-all duration-300 ease-in-out ${isMenuOpen ? 'opacity-100 scale-100' : 'opacity-0 scale-95'
            }`}
        >
          <button
            className="absolute top-8 right-10 text-white text-3xl"
            onClick={() => setIsMenuOpen(false)}
          >
            &times;
          </button>
          <div className="flex flex-col items-center space-y-8">
            <Link href="/tournaments" className="text-xl text-white hover:text-gray-400 transition" onClick={() => setIsMenuOpen(false)}>Tournaments</Link>
            {isAuthenticated && (
              <>
                <Link href="/profile" className="text-xl text-white hover:text-gray-400 transition" onClick={() => setIsMenuOpen(false)}>Profile</Link>
                <Link href="/match" className="text-xl text-white hover:text-gray-400 transition" onClick={() => setIsMenuOpen(false)}>Queue</Link>
              </>
            )}
            {user?.role === 'ADMIN' && (
              <Link href="/admin/dashboard" className="text-xl text-white hover:text-gray-400 transition" onClick={() => setIsMenuOpen(false)}>Dashboard</Link>
            )}
            {renderMobileLinks()}
          </div>
        </div>
      </div>
    </>
  );
}