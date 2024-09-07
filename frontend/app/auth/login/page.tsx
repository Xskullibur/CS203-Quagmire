// app/auth/login/page.tsx
"use client";

import React, { useState } from "react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { useAuth } from "@/hooks/useAuth";

const API_URL = `${process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL}`;

const Login: React.FC = () => {
  const [formData, setFormData] = useState({ username: "", password: "" });
  const router = useRouter();
  const { login } = useAuth();

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const response = await login(formData.username, formData.password);
      if (response.status === 200) {
        router.push("/profile");
      } else {
        alert("Invalid credentials");
      }
    } catch (error: any) {
      alert("Login failed");
    }
  };

  return (
    <div className="flex items-center justify-center min-h-screen bg-zinc-900">
      <div
        className="w-80 p-6 bg-zinc-900 rounded-lg shadow-md relative 
                overflow-hidden backdrop-blur-sm hover:backdrop-blur-md transition 
                duration-300 z-10 border border-zinc-700 hover:border-zinc-400 hover:bg-zinc-800/50 shadow-zinc-800"
      >
        <div className="absolute inset-0 bg-gradient-radial from-zinc-700/30 to-transparent opacity-50 pointer-events-none" />
        <h2 className="text-2xl font-bold mb-6 text-white">Login</h2>
        <form onSubmit={handleSubmit}>
          <div className="mb-4">
            <Input
              type="text"
              name="username"
              placeholder="Username"
              value={formData.username}
              onChange={handleChange}
              className="bg-transparent border-b border-zinc-600 text-white placeholder-zinc-500 transition duration-300"
            />
          </div>
          <div className="mb-6">
            <Input
              type="password"
              name="password"
              placeholder="Password"
              value={formData.password}
              onChange={handleChange}
              className="bg-transparent border-b border-zinc-600 text-white placeholder-zinc-500 transition duration-300"
            />
          </div>
          <Button
            type="submit"
            className="w-full bg-primary hover:bg-accent text-black hover:text-white transition duration-300"
          >
            Login
          </Button>
          <div className="mt-4 text-center">
            <Link href="/auth/register">
              <span className="text-primary hover:text-zinc-400 transition duration-300 font-semibold text-sm">
                Don&apos;t have an account? Register here.
              </span>
            </Link>
          </div>
        </form>
      </div>
    </div>
  );
};

export default Login;
