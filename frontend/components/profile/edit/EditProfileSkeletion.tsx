import { Skeleton } from "@/components/ui/skeleton";
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
} from "@/components/ui/card";

const EditProfileSkeleton = () => {
  return (
    <div className="flex flex-col items-center min-h-screen mt-24">
      <Card className="w-full flex flex-col max-w-2xl">
        <CardHeader>
          <Skeleton className="h-8 w-48 mx-auto" />
        </CardHeader>

        <CardContent className="space-y-8">
          {/* Profile Picture Skeleton */}
          <div className="flex flex-col items-center space-y-4">
            <Skeleton className="h-32 w-32 rounded-full" />
            <div className="flex gap-2">
              <Skeleton className="h-10 w-28" />
              <Skeleton className="h-10 w-28" />
            </div>
          </div>

          {/* Form Fields Skeleton */}
          <div className="space-y-6">
            <div className="space-y-2">
              <Skeleton className="h-4 w-24" />
              <Skeleton className="h-10 w-full" />
            </div>
            <div className="space-y-2">
              <Skeleton className="h-4 w-24" />
              <Skeleton className="h-10 w-full" />
            </div>
            <div className="space-y-2">
              <Skeleton className="h-4 w-24" />
              <Skeleton className="h-32 w-full" />
            </div>
          </div>
        </CardContent>

        <CardFooter>
          <Skeleton className="h-10 w-28" />
        </CardFooter>
      </Card>
    </div>
  );
};

export default EditProfileSkeleton;
