// components/MenuBar.jsx
import { useAuth } from '@/hooks/useAuth';
import Link from 'next/link';

export default function MenuBar() {
  const { user, logout, isAuthenticated } = useAuth();

  return (
    <nav className="fixed top-0 left-0 w-full bg-background/10 backdrop-blur-sm z-50 py-4">
      <div className="container mx-auto flex justify-between items-center">
        <Link href="/" className="text-2xl font-bold font-mono text-primary">HH</Link>
        <div className="flex space-x-6">
          <Link href="/" className="text-primary hover:text-muted-foreground transition">Home</Link>
          {user?.role === 'ADMIN' && (
            <Link href="/admin/dashboard" className="text-primary hover:text-muted-foreground transition">Dashboard</Link>
          )}
          <Link href="/about" className="text-primary hover:text-muted-foreground transition">About</Link>
          <Link href="/schedule" className="text-primary hover:text-muted-foreground transition">Schedule</Link>
          <Link href="/tournaments" className="text-primary hover:text-muted-foreground transition">Tournaments</Link>
          <Link href="/profile" className="text-primary hover:text-muted-foreground transition">Profile</Link>
          {!isAuthenticated() && (
            <>
              <Link href="/auth/login" className="text-primary hover:text-muted-foreground transition">Login</Link>
              <Link href="/auth/register" className="text-primary hover:text-muted-foreground transition">Register</Link>
            </>
          )}
          {isAuthenticated() && (
            <button onClick={logout} className="text-primary hover:text-muted-foreground transition">Logout</button>
          )}
        </div>
      </div>
    </nav>
  );
}