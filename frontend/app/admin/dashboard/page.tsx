"use client";
import React, { useEffect, useState, useCallback } from "react";
import withAuth from "@/hooks/withAuth";
import { UserRole } from "@/models/user-role";
import { User } from "@/models/user";
import UserTable from "./user-table";
import axiosInstance from "@/lib/axios";
import { Button } from "@/components/ui/button";
import { PlusIcon } from "lucide-react";
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Alert, AlertDescription } from "@/components/ui/alert";

const API_URL = `${process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL}`;

const AdminDashboard: React.FC = () => {

  const [loading, setLoading] = useState(true);
  const [users, setUsers] = useState<User[]>([]);
  const [selectedUser, setSelectedUser] = useState<User | null>(null);
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    confirmPassword: ''
  });
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const fetchUsers = useCallback(async () => {
    try {

      setLoading(true);
      const response = await axiosInstance.post(
        new URL("/admin/get-users", API_URL).toString()
      );
      if (response.status !== 200) {
        throw new Error("Failed to fetch users");
      }
      setUsers(response.data);
    } catch (error) {

      console.error("Failed to retrieve users:", error);
      alert("Failed to retrieve users. Please try again.");
    } finally {

      setLoading(false);
    }
  }, []);

  useEffect(() => {

    if (users.length === 0) {
      fetchUsers();
    }

  }, [fetchUsers]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleAddUser = async (e: React.FormEvent) => {

    setError(null);
    setSuccess(null);

    e.preventDefault();

    try {
      const response = await axiosInstance.post(new URL('/admin/register-admin', API_URL).toString(), formData);
      if (response.status === 201) {
        setSuccess('Successfully Registered: ' + response.data.username);
      }
    } catch (error: any) {
      if (error?.response?.data) {
        setError(error.response.data.description);
      } else {
        setError('Registration failed. Please try again.');
      }
    }
  };

  const handleEditUser = (user: User) => {
    alert(`Editing ${user.username}`);
    // Uncomment these lines when ready to implement editing
    // setSelectedUser(user);
    // setShowModal(true);
  };

  const handleSaveUser = (user: User) => {
    if (selectedUser) {
      setUsers(users.map((u) => (u.userId === selectedUser.userId ? user : u)));
    }

    setSelectedUser(null);
  };

  const handleDeleteUser = (user: User) => {
    alert(`Deleting ${user.username}`);
    // Uncomment this line when ready to implement deletion
    // setUsers(users.filter((u) => u.userId !== user.userId));
  };

  return (
    <div className="flex flex-col items-center min-h-screen pt-20">
      <h2 className="text-2xl font-bold mb-4">Admin Dashboard</h2>
      {loading ? (
        <p>Loading users...</p>
      ) : (
        <UserTable
          users={users}
          onEdit={handleEditUser}
          onDelete={handleDeleteUser}
        />
      )}

      <Dialog onOpenChange={() => {
        setError(null);
        setSuccess(null);
      }}>
        <DialogTrigger asChild>
          <Button variant={"fab"} size={"icon"}>
            <PlusIcon />
          </Button>
        </DialogTrigger>
        <DialogContent className="sm:max-w-[425px]">
          <form onSubmit={handleAddUser}>

            <DialogHeader>
              <DialogTitle>Add Admin</DialogTitle>
              <DialogDescription>
                Add a new admin user by filling out the form below. Click save when you're done.
              </DialogDescription>
            </DialogHeader>

            {error && (
              <Alert variant="destructive" className="mt-4">
                <AlertDescription>{error}</AlertDescription>
              </Alert>
            )}

            {success && (
              <Alert variant="successful" className="mt-4">
                <AlertDescription>{success}</AlertDescription>
              </Alert>
            )}

            <div className="grid gap-4 py-4">
              <div className="grid items-center gap-4">
                <Input
                  id="username"
                  type="text"
                  name="username"
                  placeholder="Username"
                  value={formData.username}
                  onChange={handleChange}
                  className="bg-transparent col-span-3 border-b border-zinc-600 text-white placeholder-zinc-500 transition duration-300"
                />
              </div>

              <div className="grid items-center gap-4">
                <Input
                  id="email"
                  type="text"
                  name="email"
                  placeholder="Email"
                  value={formData.email}
                  onChange={handleChange}
                  className="bg-transparent col-span-3 border-b border-zinc-600 text-white placeholder-zinc-500 transition duration-300"
                />
              </div>
            </div>

            <DialogFooter>
              <Button type="submit" className="bg-primary hover:bg-accent text-black hover:text-white transition duration-300">
                Register
              </Button>
            </DialogFooter>

          </form>
        </DialogContent>
      </Dialog>
    </div >
  );
};

export default withAuth(AdminDashboard, UserRole.ADMIN);