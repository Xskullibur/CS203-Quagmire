import { Skeleton } from "@/components/ui/skeleton"

const ProfileCardSkeleton = () => {
  return (
    <div className="bg-[#212121] text-white min-h-screen flex flex-col items-center justify-center">
      <div className="bg-[#2C2C2C] rounded-lg p-8 w-full max-w-3xl flex flex-col md:flex-row gap-8">
        {/* Left side - Profile Picture */}
        <div className="flex flex-col items-center gap-4">
          <Skeleton className="h-48 w-48 rounded-full" />
          <Skeleton className="h-6 w-32" />
          <Skeleton className="h-5 w-24" />
        </div>

        {/* Right side - Profile Info */}
        <div className="flex-1 space-y-6">
          {/* Stats Section */}
          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Skeleton className="h-4 w-24" />
              <Skeleton className="h-6 w-16" />
            </div>
            <div className="space-y-2">
              <Skeleton className="h-4 w-24" />
              <Skeleton className="h-6 w-16" />
            </div>
            <div className="space-y-2">
              <Skeleton className="h-4 w-24" />
              <Skeleton className="h-6 w-16" />
            </div>
            <div className="space-y-2">
              <Skeleton className="h-4 w-24" />
              <Skeleton className="h-6 w-16" />
            </div>
          </div>

          {/* Bio Section */}
          <div className="space-y-2">
            <Skeleton className="h-4 w-16" />
            <Skeleton className="h-24 w-full" />
          </div>

          {/* Social Links */}
          <div className="flex gap-4">
            <Skeleton className="h-8 w-8 rounded-full" />
            <Skeleton className="h-8 w-8 rounded-full" />
            <Skeleton className="h-8 w-8 rounded-full" />
          </div>
        </div>
      </div>
    </div>
  )
}

export default ProfileCardSkeleton;