import React from 'react';
import { Skeleton } from "@/components/ui/skeleton";

const TournamentDetailsSkeleton = () => {
  return (
    <div className="flex flex-col items-center min-h-screen pt-20">
      {/* Header Skeleton */}
      <header className="bg-background/10 w-full py-4 text-center">
        <Skeleton className="h-10 w-64 mx-auto" />
      </header>

      {/* Tournament Image Skeleton */}
      <Skeleton className="w-full max-w-md h-64 mt-6 mb-4 rounded-lg" />

      {/* Date Range Skeleton */}
      <Skeleton className="h-6 w-72 mt-2" />

      {/* Main Content Container */}
      <div className="mt-8 max-w-xl w-full px-4">
        {/* Location Section */}
        <Skeleton className="h-6 w-24 mb-2" /> {/* "Location:" label */}
        <Skeleton className="h-5 w-full max-w-md mb-4" />

        {/* Description Section */}
        <Skeleton className="h-6 w-32 mb-2" /> {/* "Description:" label */}
        <div className="space-y-2">
          <Skeleton className="h-5 w-full" />
          <Skeleton className="h-5 w-full" />
          <Skeleton className="h-5 w-3/4" />
        </div>

        {/* Registration Deadline Section */}
        <Skeleton className="h-6 w-48 mt-4 mb-2" /> {/* "Registration Deadline:" label */}
        <Skeleton className="h-5 w-48" />

        {/* Action Button Skeleton */}
        <div className="flex justify-center mt-6">
          <Skeleton className="h-10 w-32" />
        </div>
      </div>

      {/* Divider */}
      <div className="w-full max-w-xl mx-auto">
        <hr className="my-8 border-t border-gray-300" />
      </div>

      {/* Tournament Bracket Skeleton */}
      <div className="w-full max-w-screen-xl px-4">
        <div className="flex gap-8 overflow-x-auto py-4">
          {[1, 2, 3, 4].map((round) => (
            <div key={round} className="flex flex-col gap-4 min-w-[250px]">
              {/* Round Header */}
              <Skeleton className="h-6 w-32 mx-auto mb-4" />
              
              {/* Match Cards */}
              <div className="space-y-6">
                {[1, 2, 3].map((match) => (
                  <Skeleton key={match} className="h-32 w-full" />
                ))}
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default TournamentDetailsSkeleton;