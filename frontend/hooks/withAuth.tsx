import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/hooks/useAuth";
import { UserRole } from "@/models/user-role";

const withAuth = (WrappedComponent: React.FC, requiredRole?: UserRole) => {
  const WithAuthComponent = (props: any) => {
    const { loading, isAuthenticated, user } = useAuth();
    const router = useRouter();

    useEffect(() => {
      if (!loading) {
        if (!isAuthenticated) {
          router.push("/auth/login");
        } else if (requiredRole && user?.role !== requiredRole) {
          router.push("/not-found");
        }
      }
    }, [loading, isAuthenticated, user, router]);

    if (loading || !isAuthenticated || (requiredRole && user?.role !== requiredRole)) {
      return <div>Loading...</div>;
    }

    return <WrappedComponent {...props} />;
  };

  WithAuthComponent.displayName = `WithAuth(${WrappedComponent.displayName ?? WrappedComponent.name ?? "Component"})`;

  return WithAuthComponent;
};

export default withAuth;