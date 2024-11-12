"use client";

import React from "react";
import {
  Table,
  TableHeader,
  TableRow,
  TableHead,
  TableBody,
  TableCell,
} from "@/components/ui/table";
import { Button } from "@/components/ui/button";
import { User } from "@/types/user";
import {
  ChevronLeftIcon,
  ChevronRightIcon,
  LockKeyholeIcon,
  PenIcon,
  RotateCcw,
  UnlockKeyholeIcon,
} from "lucide-react";
import { formatDistance } from "date-fns";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Label } from "@/components/ui/label";
import { Tournament } from "@/types/tournament";
import { useRouter } from "next/navigation";
import axios, { AxiosError } from "axios";
import { useGlobalErrorHandler } from "@/app/context/ErrorMessageProvider";
import { client } from "stompjs";
import Link from "next/link";
import axiosInstance from "@/lib/axios";
import { toast } from "@/hooks/use-toast";

const API_URL = `${process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL}/tournament`;
const WEB_URL = `${process.env.NEXT_PUBLIC_WEB_URL}`;

// Add the new prop for resetting passwords
interface TournamentTableProps {
  tournaments: Tournament[];
  currentPage: number;
  totalPages: number;
  pageSize: number;
  onPageChange: (page: number) => void;
  onPageSizeChange: (size: number) => void;
}

const TournamentTable: React.FC<TournamentTableProps> = ({
  tournaments,
  currentPage,
  totalPages,
  pageSize,
  onPageChange,
  onPageSizeChange,
}) => {
  const { handleError } = useGlobalErrorHandler();
  const [error, setError] = React.useState<string | null>(null);
  const router = useRouter();

  const handleStartOrUpdate = async (tournament: Tournament) => {
    try {
      // If tournament is to be started, change tournament status to in progress
      if (tournament.status === "SCHEDULED") {
        const response = await axiosInstance.put(
          `${API_URL}/${tournament.id}/start`
        );

        toast({
          variant: "success",
          title: "Success",
          description: `${tournament.name} has started`,
        });
      }

      // Navigate to tournament configurations
      router.push(`/tournaments/${tournament.id}/brackets`);
    } catch (error) {
      if (axios.isAxiosError(error)) {
        handleError(error);
      }

      setError("Failed to start tournament.");
    }
  };

  return (
    <div className="w-full max-w-[80%] border rounded-lg overflow-hidden mb-4">
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead
              className="w-2/12 cursor-pointer"
            >
              Name{" "}
            </TableHead>
            <TableHead
              className="w-2/12 cursor-pointer"
            >
              Status{" "}
            </TableHead>
            <TableHead
              className="w-2/12 cursor-pointer"
            >
              Start Date{" "}
            </TableHead>
            <TableHead
              className="w-2/12 cursor-pointer"
            >
              End Date{" "}
            </TableHead>
          </TableRow>
        </TableHeader>

        <TableBody>
          {tournaments.map((tournament) => (
            <TableRow key={tournament.id}
              className="cursor-pointer"
              onClick={() => router.push(`/tournaments/${tournament.id}`)}
            >
              <TableCell className="truncate">{tournament.name}</TableCell>
              <TableCell className="truncate">{tournament.status}</TableCell>
              <TableCell className="truncate">{new Date(tournament.startDate).toLocaleDateString('en-GB', { day: '2-digit', month: 'long', year: 'numeric' })}</TableCell>
              <TableCell className="truncate">{new Date(tournament.endDate).toLocaleDateString('en-GB', { day: '2-digit', month: 'long', year: 'numeric' })}</TableCell>

              <TableCell className="flex justify-center items-center space-x-2">
                <Button
                  className="w-24 h-10 flex items-center justify-center p-0 cursor-pointer"
                  onClick={(e) => {
                    e.stopPropagation();
                    handleStartOrUpdate(tournament);
                  }}
                >
                  {tournament.status === "SCHEDULED" ? "Start" : "Update"}
                </Button>
                <Link href={`/tournaments/${tournament.id}/update`}>
                  <Button
                    variant="outline"
                    className="w-10 h-10 flex items-center justify-center p-0"
                    onClick={(e) => e.stopPropagation()}
                  >
                    <PenIcon />
                  </Button>
                </Link>

              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>

      {/* Table footer */}
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

export default TournamentTable;
