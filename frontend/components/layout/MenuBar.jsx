// components/MenuBar.jsx
import Link from 'next/link';

export default function MenuBar() {
  return (
    <nav className="fixed top-0 left-0 w-full bg-background/10 backdrop-blur-sm z-50 py-4">
      <div className="container mx-auto flex justify-between items-center">
        <Link href="/" className="text-2xl font-bold font-mono text-primary">HH</Link>
        <div className="flex space-x-6">
          <Link href="/" className="text-primary hover:text-muted-foreground transition">Home</Link>
          <Link href="/about" className="text-primary hover:text-muted-foreground transition">About</Link>
          <Link href="/schedule" className="text-primary hover:text-muted-foreground transition">Schedule</Link>
          <Link href="/tournament" className="text-primary hover:text-muted-foreground transition">Tournaments</Link>
          <Link href="/profile" className="text-primary hover:text-muted-foreground transition">Profile</Link>
          <Link href="/auth/login" className="text-primary hover:text-muted-foreground transition">Login</Link>
          <Link href="/auth/register" className="text-primary hover:text-muted-foreground transition">Register</Link>
        </div>
      </div>
    </nav>
  );
}