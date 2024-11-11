import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/hooks/useAuth";
import { UserRole } from "@/types/user-role";

type AuthConfig = {
    requireAuth?: boolean;
    role?: UserRole;
};

// Function overloads to support both patterns
function withAuth(WrappedComponent: React.FC<any>): React.FC<any>;
function withAuth(
    WrappedComponent: React.FC<any>,
    role: UserRole
): React.FC<any>;
function withAuth(
    WrappedComponent: React.FC<any>,
    config: AuthConfig
): React.FC<any>;
function withAuth(
    WrappedComponent: React.FC<any>,
    configOrRole?: UserRole | AuthConfig
) {
    const WithAuthComponent = (props: any) => {
        const { isLoading, isAuthenticated, user } = useAuth();
        const router = useRouter();

        // Determine auth requirements based on parameters
        let requireAuth = true;
        let requiredRole: UserRole | undefined;

        if (typeof configOrRole === "object") {
            // Config object pattern
            requireAuth = configOrRole.requireAuth ?? true;
            requiredRole = configOrRole.role;
        } else {
            // Original pattern
            requiredRole = configOrRole;
        }

        useEffect(() => {
            if (!isLoading) {
                if (requireAuth) {
                    // Protected routes
                    if (!isAuthenticated) {
                        router.push("/auth/login");
                    } else if (requiredRole && user?.role !== requiredRole) {
                        router.push("/not-found");
                    }
                }
                // Public routes don't redirect
            }
        }, [isLoading, isAuthenticated, user, router, requireAuth, requiredRole]);

        // Show loading state while authentication is being checked
        if (isLoading) {
            return <div>Loading...</div>;
        }

        // For protected routes, don't render if not authenticated or wrong role
        if (
            requireAuth &&
            (!isAuthenticated || (requiredRole && user?.role !== requiredRole))
        ) {
            return null;
        }

        // For both public and protected routes, render the component if we reach here
        return <WrappedComponent {...props} />;
    };

    WithAuthComponent.displayName = `WithAuth(${WrappedComponent.displayName ?? WrappedComponent.name ?? "Component"
        })`;

    return WithAuthComponent;
}

export default withAuth;
