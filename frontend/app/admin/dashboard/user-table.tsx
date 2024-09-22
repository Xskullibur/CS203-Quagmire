import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { format, formatDistance } from 'date-fns';
import {
  Select,
  SelectTrigger,
  SelectValue,
  SelectContent,
  SelectItem,
} from "@/components/ui/select";
import {
  Table,
  TableHeader,
  TableRow,
  TableHead,
  TableBody,
  TableCell,
} from "@/components/ui/table";
import { Button } from "@/components/ui/button";
import { User } from "@/models/user";
import { useMemo, useState } from "react";
import { ChevronLeftIcon, ChevronRightIcon, FilePenIcon, SearchIcon, TrashIcon } from "lucide-react";

interface UserTableProps {
  users: User[];
  onEdit: (user: User) => void;
  onDelete: (user: User) => void;
}

const UserTable: React.FC<UserTableProps> = ({ users, onEdit, onDelete }) => {
  const [search, setSearch] = useState("");
  const [pageSize, setPageSize] = useState("10");
  const [page, setPage] = useState(1);
  const [sort, setSort] = useState({ key: "username", order: "asc" });

  const filteredUsers = useMemo(() => {
    return users.filter(
      (user) =>
        user.username.toLowerCase().includes(search.toLowerCase()) ||
        user.email.toLowerCase().includes(search.toLowerCase()) ||
        user.role.toLowerCase().includes(search.toLowerCase())
    );
  }, [users, search]);

  const sortedUsers = useMemo(() => {
    const direction = sort.order === "asc" ? 1 : -1;

    return [...filteredUsers].sort((a, b) => {
      const aValue = a[sort.key as keyof User];
      const bValue = b[sort.key as keyof User];

      if (typeof aValue === "string" && typeof bValue === "string") {
        return direction * aValue.localeCompare(bValue);
      }

      if (typeof aValue === "number" && typeof bValue === "number") {
        return direction * (aValue - bValue);
      }

      return 0;
    });
  }, [filteredUsers, sort]);

  const totalPages = Math.ceil(sortedUsers.length / Number(pageSize));

  const paginatedUsers = useMemo(() => {
    const startIndex = (page - 1) * Number(pageSize);
    const endIndex = startIndex + Number(pageSize);
    return sortedUsers.slice(startIndex, endIndex);
  }, [sortedUsers, page, pageSize]);

  const handleSort = (key: keyof User) => {
    setSort((prevSort) => ({
      key,
      order: prevSort.key === key && prevSort.order === "asc" ? "desc" : "asc",
    }));
  };

  const handlePageSizeChange = (newPageSize: string) => {
    setPageSize(newPageSize);
    setPage(1);
  };

  return (
    <div className="flex flex-col items-center w-full pb-8 gap-6">
      <div className="w-full max-w-[80%] border rounded-lg overflow-hidden">
        <div className="bg-muted px-4 py-3 flex items-center justify-between">
          <div className="relative">
            <SearchIcon className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground" />
            <Input
              type="search"
              placeholder="Search users..."
              className="pl-10 w-[300px]"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
          </div>
          <div className="flex items-center gap-2">
            <Label htmlFor="page-size">Show</Label>
            <Select
              value={pageSize}
              onValueChange={handlePageSizeChange}
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
        </div>
        <div className="overflow-x-auto">
          <Table className="w-full table-fixed">
            <TableHeader>
              <TableRow>
                <TableHead
                  className="w-2/12 cursor-pointer"
                  onClick={() => handleSort("username")}
                >
                  Username{" "}
                  {sort.key === "username" && (
                    <span className="ml-1">
                      {sort.order === "asc" ? "\u2191" : "\u2193"}
                    </span>
                  )}
                </TableHead>
                <TableHead
                  className="w-2/12 cursor-pointer"
                  onClick={() => handleSort("email")}
                >
                  Email{" "}
                  {sort.key === "email" && (
                    <span className="ml-1">
                      {sort.order === "asc" ? "\u2191" : "\u2193"}
                    </span>
                  )}
                </TableHead>
                <TableHead
                  className="w-2/12 cursor-pointer"
                  onClick={() => handleSort("role")}
                >
                  Role{" "}
                  {sort.key === "role" && (
                    <span className="ml-1">
                      {sort.order === "asc" ? "\u2191" : "\u2193"}
                    </span>
                  )}
                </TableHead>
                <TableHead
                  className="cursor-pointer"
                  onClick={() => handleSort("createdAt")}
                >
                  Created At{" "}
                  {sort.key === "createdAt" && (
                    <span className="ml-1">
                      {sort.order === "asc" ? "\u2191" : "\u2193"}
                    </span>
                  )}
                </TableHead>
                <TableHead
                  className="cursor-pointer"
                  onClick={() => handleSort("updatedAt")}
                >
                  Updated At{" "}
                  {sort.key === "updatedAt" && (
                    <span className="ml-1">
                      {sort.order === "asc" ? "\u2191" : "\u2193"}
                    </span>
                  )}
                </TableHead>
                <TableHead className="w-32">Actions</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {paginatedUsers.map((user) => (
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
                  <TableCell>
                    <div className="flex items-center gap-2">
                      <Button
                        variant="outline"
                        size="icon"
                        onClick={() => onEdit(user)}
                      >
                        <FilePenIcon className="h-4 w-4" />
                      </Button>
                      <Button
                        variant="destructive"
                        size="icon"
                        onClick={() => onDelete(user)}
                      >
                        <TrashIcon className="h-4 w-4" />
                      </Button>
                    </div>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
          <div className="bg-muted px-4 py-3 flex items-center justify-between">
            <div className="text-sm text-muted-foreground">
              Showing {(page - 1) * Number(pageSize) + 1} to{" "}
              {Math.min(page * Number(pageSize), sortedUsers.length)} of{" "}
              {sortedUsers.length} entries
            </div>
            <div className="flex items-center gap-2">
              <Button
                variant="outline"
                size="icon"
                disabled={page === 1}
                onClick={() => setPage(page - 1)}
              >
                <ChevronLeftIcon className="h-4 w-4" />
              </Button>
              <div className="text-sm text-muted-foreground">
                Page {page} of {totalPages}
              </div>
              <Button
                variant="outline"
                size="icon"
                disabled={page >= totalPages}
                onClick={() => setPage(page + 1)}
              >
                <ChevronRightIcon className="h-4 w-4" />
              </Button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default UserTable;