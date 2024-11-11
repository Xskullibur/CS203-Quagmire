"use client";

import React, { useState } from "react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Alert, AlertDescription } from "@/components/ui/alert";
import axiosInstance from "@/lib/axios";
import { useRouter } from "next/navigation";
import { useGlobalErrorHandler } from "@/app/context/ErrorMessageProvider";
import axios from "axios";
import { toast } from "@/hooks/use-toast";

const UpdatePasswordForm: React.FC = () => {
  const router = useRouter();
  const [formData, setFormData] = useState({
    currentPassword: "",
    newPassword: "",
    confirmPassword: "",
  });
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [passwordsMatch, setPasswordsMatch] = useState(true);
  const { handleError } = useGlobalErrorHandler();

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });

    // Check if newPassword and confirmPassword match whenever they change
    if (name === "newPassword" || name === "confirmPassword") {
      setPasswordsMatch(
        formData.newPassword === value || formData.confirmPassword === value
      );
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);

    if (!passwordsMatch) {
      setError("Passwords do not match");
      return;
    }

    try {
      const response = await axiosInstance.put(
        `${process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL}/users/update-password`,
        {
          currentPassword: formData.currentPassword,
          newPassword: formData.newPassword,
        }
      );

      if (response.status === 200) {
        toast({
          variant: "success",
          title: "Success",
          description: "Password updated successfully.",
        });
        router.push("/users");
      }
    } catch (error: any) {
      if (axios.isAxiosError(error)) {
        handleError(error);
      }
      setError(
        "Failed to update password. Please check your credentials and try again."
      );
    }
  };

  return (
    <div className="flex items-center justify-center min-h-screen bg-primary-foreground">
      <div className="w-80 p-6 bg-primary-foreground rounded-lg shadow-md">
        <h2 className="text-2xl font-bold mb-6 text-white">Update Password</h2>
        <form onSubmit={handleSubmit}>
          {error && (
            <Alert variant="destructive" className="mb-4">
              <AlertDescription>{error}</AlertDescription>
            </Alert>
          )}
          {success && (
            <Alert variant="successful" className="mb-4">
              <AlertDescription>{success}</AlertDescription>
            </Alert>
          )}
          <div className="mb-4">
            <Input
              type="password"
              name="currentPassword"
              placeholder="Current Password"
              value={formData.currentPassword}
              onChange={handleChange}
              required
            />
          </div>
          <div className="mb-4">
            <Input
              type="password"
              name="newPassword"
              placeholder="New Password"
              value={formData.newPassword}
              onChange={handleChange}
              required
            />
          </div>
          <div className="mb-4">
            <Input
              type="password"
              name="confirmPassword"
              placeholder="Confirm New Password"
              value={formData.confirmPassword}
              onChange={handleChange}
              required
            />
          </div>
          {!passwordsMatch && (
            <p className="text-red-500 text-sm mb-4">Passwords do not match</p>
          )}
          <Button
            type="submit"
            className="w-full bg-primary hover:bg-accent text-black hover:text-white transition duration-300"
          >
            Update Password
          </Button>
        </form>
      </div>
    </div>
  );
};

export default UpdatePasswordForm;
