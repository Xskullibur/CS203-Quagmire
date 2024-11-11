import { useAlertDialog } from "@/app/context/AlertDialogContext";
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog";

export function GlobalAlertDialog() {
  const { isOpen, hideAlert, options } = useAlertDialog();

  if (!options) return null;

  return (
    <AlertDialog open={isOpen} onOpenChange={hideAlert}>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>{options.title || "Alert"}</AlertDialogTitle>
          <AlertDialogDescription>{options.description}</AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          {options.variant === 'confirm' && (
            <AlertDialogCancel onClick={options.onCancel}>
              {options.cancelText || "Cancel"}
            </AlertDialogCancel>
          )}
          <AlertDialogAction
            onClick={() => {
              options.onConfirm?.();
              hideAlert();
            }}
          >
            {options.confirmText || "OK"}
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
}