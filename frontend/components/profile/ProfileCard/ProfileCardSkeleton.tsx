import { Skeleton } from "@/components/ui/skeleton";

const ProfileCardSkeleton = () => {
  return (
    <div className="relative w-full h-full rounded-lg overflow-hidden flex items-center justify-center mt-16">
      {/* Background Skeleton */}
      <Skeleton className="absolute inset-0" />
      <div className="absolute inset-0 bg-primary-foreground/80" />

      <div className="relative w-full min-h-[calc(100vh-8rem)] z-20 flex flex-col justify-center p-10 text-primary">
        {/* Trigger Button Skeleton */}
        <div className="absolute right-10 top-10">
          <Skeleton className="h-10 w-10 rounded-full" />
        </div>

        {/* Content Section */}
        <div className="relative flex flex-col items-center transition-all duration-300 w-full">
          {/* Player Info Skeleton */}
          <div className="flex flex-col items-center space-y-6 w-full max-w-lg">
            {/* Profile Picture */}
            <Skeleton className="h-40 w-40 rounded-full" />

            {/* Username */}
            <Skeleton className="h-8 w-48" />

            {/* Bio */}
            <Skeleton className="h-20 w-full max-w-md" />

            {/* Social Links */}
            <div className="flex space-x-4">
              <Skeleton className="h-8 w-8 rounded-full" />
              <Skeleton className="h-8 w-8 rounded-full" />
              <Skeleton className="h-8 w-8 rounded-full" />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProfileCardSkeleton;
