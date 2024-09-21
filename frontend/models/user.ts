// frontend/models/User.ts

import { UserRole } from "./user-role";

export interface User {
  userId: string;
  username: string;
  email: string;
  role: UserRole;
  createdAt: string;
  updatedAt: string;
}