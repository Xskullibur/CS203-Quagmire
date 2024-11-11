// Components/ProfileCard/TriggerButton.tsx
import { SheetTrigger } from "@/components/ui/sheet";

interface TriggerButtonProps {
  isOpen: boolean;
  label: String;
  onClick: () => void;
}

export const TriggerButton = ({ isOpen, label, onClick }: TriggerButtonProps) => (
  <div
    className={`transition-opacity duration-300 ${
      isOpen ? "opacity-0 pointer-events-none" : "opacity-100"
    }`}
  >
    <SheetTrigger
      onClick={onClick}
      className={`
        flex items-center justify-center 
        bg-primary hover:bg-primary/90 
        text-secondary 
        h-12 w-40 
        rounded-lg shadow-lg 
        -rotate-90
        transition-colors
      `}
    >
      {label}
    </SheetTrigger>
  </div>
);