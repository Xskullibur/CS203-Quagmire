// frontend/app/admin/dashboard/page.tsx
"use client";

import React, { useEffect, useState } from "react";
import { useAuth } from "@/hooks/useAuth";
import withAuth from "@/hooks/withAuth";
import { UserRole } from "@/models/user-role";
import { User } from "@/models/user";
import UserTable from "./user-table";
import axiosInstance from "@/lib/axios";

const AdminDashboard: React.FC = () => {
  const { user } = useAuth();
  const [username, setUsername] = useState("Guest");
  const [showModal, setShowModal] = useState(false);
  const [loading, setLoading] = useState(true);
  const [users, setUsers] = useState<User[]>([]);
  const [selectedUser, setSelectedUser] = useState<User | null>(null);

  const API_URL = `${process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL}`;

  useEffect(() => {
    const storedUsername = user?.username ?? "Guest";
    setUsername(storedUsername);
  }, [user]);

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        setLoading(true);
        const response = await axiosInstance.post(
          new URL("/admin/get-users", API_URL).toString()
        );

        if (response.status !== 200) {
          throw new Error("Failed to fetch users");
        }

        const data = await response.data;
        setUsers(data);
      } catch (error) {
        alert("Failed to retieve");
      } finally {
        setLoading(false);
      }
    };

    fetchUsers();
  }, []);

  const handleEditUser = (user: User) => {
    alert(`Editing ${user.username}`);
    // setSelectedUser(user);
    // setShowModal(true);
  };

  const handleSaveUser = (user: User) => {
    if (selectedUser) {
      setUsers(users.map((u) => (u.userId === selectedUser.userId ? user : u)));
    }
    setShowModal(false);
    setSelectedUser(null);
  };

  const handleDeleteUser = (user: User) => {
    alert(`Deleting ${user.username}`);
    // setUsers(users.filter((u) => u.userId !== user.userId));
  };

  return (
    <div className="flex flex-col items-center justify-center min-h-screen">
      <h2 className="text-2xl font-bold mb-4">Admin Dashboard</h2>
      <p>Welcome, {username}!</p>
      <UserTable
        users={users}
        onEdit={handleEditUser}
        onDelete={handleDeleteUser}
      />
      {/* Add pagination controls here */}
    </div>
  );
};

export default withAuth(AdminDashboard, UserRole.ADMIN);
