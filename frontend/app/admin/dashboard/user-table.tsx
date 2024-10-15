import React from "react";
import { Table, TableHeader, TableRow, TableHead, TableBody, TableCell } from "@/components/ui/table";
import { Button } from "@/components/ui/button";
import { User } from "@/models/user";
import { ChevronLeftIcon, ChevronRightIcon } from "lucide-react";
import { formatDistance } from "date-fns";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Label } from "@/components/ui/label";

interface UserTableProps {
  users: User[];
  onEdit: (user: User) => void;
  onDelete: (user: User) => void;
  currentPage: number;
  totalPages: number;
  pageSize: number;
  onPageChange: (page: number) => void;
  onPageSizeChange: (size: number) => void;
  sortBy: string;
  sortOrder: string;
  onSort: (key: string) => void;
}

const UserTable: React.FC<UserTableProps> = ({
  users,
  onEdit,
  onDelete,
  currentPage,
  totalPages,
  pageSize,
  onPageChange,
  onPageSizeChange,
  sortBy,
  sortOrder: sortOrder,
  onSort,
}) => {
  return (
    <div className="w-full max-w-[80%] border rounded-lg overflow-hidden">
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead className="w-2/12 cursor-pointer" onClick={() => onSort("username")}>
              Username {sortBy === "username" && (sortOrder === "asc" ? "\u2191" : "\u2193")}
            </TableHead>
            <TableHead className="w-3/12 cursor-pointer" onClick={() => onSort("email")}>
              Email {sortBy === "email" && (sortOrder === "asc" ? "\u2191" : "\u2193")}
            </TableHead>
            <TableHead className="w-2/12 cursor-pointer" onClick={() => onSort("role")}>
              Role {sortBy === "role" && (sortOrder === "asc" ? "\u2191" : "\u2193")}
            </TableHead>
            <TableHead className="w-2/12 cursor-pointer" onClick={() => onSort("createdAt")}>
              Created At {sortBy === "createdAt" && (sortOrder === "asc" ? "\u2191" : "\u2193")}
            </TableHead>
            <TableHead className="w-2/12 cursor-pointer" onClick={() => onSort("updatedAt")}>
              Updated At {sortBy === "updatedAt" && (sortOrder === "asc" ? "\u2191" : "\u2193")}
            </TableHead>
            {/* <TableHead className="w-1/12">Actions</TableHead> */}
          </TableRow>
        </TableHeader>
        <TableBody>
          {users.map((user) => (
            <TableRow key={user.userId}>
              <TableCell className="truncate">{user.username}</TableCell>
              <TableCell className="truncate">{user.email}</TableCell>
              <TableCell className="truncate">{user.role}</TableCell>
              <TableCell className="truncate">
                {formatDistance(new Date(user.createdAt), new Date(), { addSuffix: true })}
              </TableCell>
              <TableCell className="truncate">
                {formatDistance(new Date(user.updatedAt), new Date(), { addSuffix: true })}
              </TableCell>
              {/* <TableCell className="2xl:space-x-4 sm:space-y-2">
                <Button variant="outline" size="icon" onClick={() => onEdit(user)}>
                  <FilePenIcon className="h-4 w-4" />
                </Button>
                <Button variant="destructive" size="icon" onClick={() => onDelete(user)}>
                  <LockKeyholeIcon className="h-4 w-4" />
                </Button>
              </TableCell> */}
            </TableRow>
          ))}
        </TableBody>
      </Table>

      <div className="flex items-center justify-between p-4">
        <div className="flex items-center gap-2">
          <Label htmlFor="page-size">Show</Label>
          <Select
            value={pageSize.toString()}
            onValueChange={(value) => onPageSizeChange(Number(value))}
            name="page-size"
          >
            <SelectTrigger>
              <SelectValue placeholder="10" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value={"10"}>10</SelectItem>
              <SelectItem value={"20"}>20</SelectItem>
              <SelectItem value={"50"}>50</SelectItem>
            </SelectContent>
          </Select>

          <Label htmlFor="page-size">entries</Label>
        </div>

        <div>
          <Button
            variant="outline"
            size="sm"
            onClick={() => onPageChange(currentPage - 1)}
            disabled={currentPage === 0}
          >
            <ChevronLeftIcon className="h-4 w-4" />
          </Button>
          <span className="mx-2">
            Page {currentPage + 1} of {totalPages}
          </span>
          <Button
            variant="outline"
            size="sm"
            onClick={() => onPageChange(currentPage + 1)}
            disabled={currentPage === totalPages - 1}
          >
            <ChevronRightIcon className="h-4 w-4" />
          </Button>
        </div>
      </div>
    </div>
  );
};

export default UserTable;