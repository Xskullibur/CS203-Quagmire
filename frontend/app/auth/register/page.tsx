"use client";

import React, { useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import axiosInstance from "@/lib/axios";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { toast } from "@/hooks/use-toast";
import { AxiosError } from "axios";
import { RegisterUser } from "@/types/register-user";
import { ErrorHandler } from "@/utils/errorHandler";
import { useAuth } from "@/hooks/useAuth";

const API_URL = `${process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL}`;

interface FormState {
  formData: RegisterUser;
  error: string | null;
}

const Register: React.FC = () => {
  const router = useRouter();
  const { login } = useAuth();

  const [formState, setFormState] = useState<FormState>({
    formData: {
      username: "",
      email: "",
      password: "",
      confirmPassword: "",
    },
    error: null,
  });

  // Form handlers
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormState((prev) => ({
      ...prev,
      formData: { ...prev.formData, [name]: value },
    }));
  };

  const handleConfirmPassword = (e: React.ChangeEvent<HTMLInputElement>) => {
    const confirmPassword = e.target.value;
    setFormState((prev) => ({
      formData: { ...prev.formData, confirmPassword },
      error:
        prev.formData.password !== confirmPassword
          ? "Passwords do not match"
          : null,
    }));
  };

  // Registration handler
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (formState.formData.password !== formState.formData.confirmPassword) {
      setFormState((prev) => ({ ...prev, error: "Passwords do not match" }));
      return;
    }

    try {
      const registerResponse = await axiosInstance.post(
        new URL("/authentication/register", API_URL).toString(),
        formState.formData
      );

      if (registerResponse.status === 201) {
        const loginResponse = await axiosInstance.post(
          new URL("/authentication/login", API_URL).toString(),
          {
            username: formState.formData.username,
            password: formState.formData.password,
          }
        );

        if (loginResponse.status === 200) {
          await login(formState.formData.username, formState.formData.password);

          toast({
            variant: "success",
            title: "Success",
            description: "Account registered successfully.",
          });

          router.push("/profile/edit?new=true");
        }
      }
    } catch (error) {
      if (error instanceof AxiosError) {
        const { message } = ErrorHandler.handleError(error);
        setFormState((prev) => ({ ...prev, error: message }));
      }
    }
  };

  // Render form fields
  const renderFormField = (
    name: keyof RegisterUser,
    type: string,
    placeholder: string,
    handler = handleChange
  ) => (
    <div className="mb-4">
      <Input
        type={type}
        name={name}
        placeholder={placeholder}
        value={formState.formData[name]}
        onChange={handler}
        className="bg-transparent border-b border-zinc-600 text-white placeholder-zinc-500 transition duration-300"
      />
    </div>
  );

  return (
    <div className="flex items-center justify-center min-h-screen bg-primary-foreground">
      <div
        className="w-96 p-6 bg-primary-foreground rounded-lg shadow-md relative 
        overflow-hidden backdrop-blur-sm hover:backdrop-blur-md transition 
        duration-300 z-10 border border-zinc-700 hover:border-zinc-400 hover:bg-zinc-800/50 shadow-zinc-800"
      >
        <div className="absolute inset-0 bg-gradient-radial from-zinc-700/30 to-transparent opacity-50 pointer-events-none" />
        <h2 className="text-2xl font-bold mb-6 text-white">Register</h2>

        {formState.error && (
          <Alert variant="destructive" className="mb-4">
            <AlertDescription>{formState.error}</AlertDescription>
          </Alert>
        )}

        <form onSubmit={handleSubmit}>
          {renderFormField("username", "text", "Username")}
          {renderFormField("email", "email", "Email")}
          {renderFormField("password", "password", "Password")}
          {renderFormField(
            "confirmPassword",
            "password",
            "Confirm Password",
            handleConfirmPassword
          )}

          <Button
            type="submit"
            className="w-full bg-primary hover:bg-accent text-black hover:text-white transition duration-300"
          >
            Register
          </Button>

          <div className="mt-4 text-center">
            <Link href="/auth/login">
              <span className="text-primary hover:text-zinc-400 transition duration-300 font-semibold text-sm">
                Already have an account? Login here.
              </span>
            </Link>
          </div>
        </form>
      </div>
    </div>
  );
};

export default Register;
