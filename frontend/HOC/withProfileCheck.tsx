import { ComponentType } from "react";
import { useProfileCheck } from "../hooks/useProfileCheck";

export function withProfileCheck<P extends object>(
  WrappedComponent: ComponentType<P>
) {
  return function WithProfileCheckComponent(props: P) {
    const { isChecking, hasProfile } = useProfileCheck();

    if (isChecking) {
      return <div>Loading...</div>;
    }

    if (!hasProfile) {
      return null;
    }

    return <WrappedComponent {...props} />;
  };
}
