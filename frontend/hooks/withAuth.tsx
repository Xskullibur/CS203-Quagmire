import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/hooks/useAuth";
import { UserRole } from "@/models/user-role";

const withAuth = (WrappedComponent: React.FC, requiredRole?: UserRole) => {
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

    if (isLoading || !isAuthenticated || (requiredRole && user?.role !== requiredRole)) {
      return <div>Loading...</div>;
    }

    return <WrappedComponent {...props} />;
  };

  WithAuthComponent.displayName = `WithAuth(${WrappedComponent.displayName ?? WrappedComponent.name ?? "Component"})`;

  return WithAuthComponent;
};

export default withAuth;