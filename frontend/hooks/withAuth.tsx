import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/hooks/useAuth";
import { UserRole } from "@/types/user-role";

// Add a new interface to define components that can use withAuth
interface WithLoadingComponent extends React.FC {
  Skeleton?: React.FC; // Optional skeleton component
}

const withAuth = (
  WrappedComponent: WithLoadingComponent,
  requiredRole?: UserRole
) => {
  const WithAuthComponent = (props: any) => {
    const { isLoading, isAuthenticated, user } = useAuth();
    const router = useRouter();

    useEffect(() => {
      if (!isLoading) {
        if (!isAuthenticated) {
          router.push("/auth/login");
        } else if (requiredRole && user?.role !== requiredRole) {
          router.push("/not-found");
        }
      }
    }, [isLoading, isAuthenticated, user, router]);

    if (
      isLoading ||
      !isAuthenticated ||
      (requiredRole && user?.role !== requiredRole)
    ) {
      // Use the component's Skeleton if provided, otherwise return null
      return WrappedComponent.Skeleton ? (
        <WrappedComponent.Skeleton />
      ) : (
        <div>Loading...</div>
      );
    }

    return <WrappedComponent {...props} />;
  };

  WithAuthComponent.displayName = `WithAuth(${WrappedComponent.displayName ?? WrappedComponent.name ?? "Component"})`;

  return WithAuthComponent;
};

export default withAuth;
