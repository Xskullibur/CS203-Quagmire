// Components/ProfileCard/TriggerButton.tsx
import { SheetTrigger } from "@/components/ui/sheet";

interface TriggerButtonProps {
  isOpen: boolean;
}

export const TriggerButton = ({ isOpen }: TriggerButtonProps) => (
  <div
    className={`fixed top-[calc(50vh+2rem)] right-[-4rem] z-50 transition-opacity duration-300 ${
      isOpen ? "opacity-0 pointer-events-none" : "opacity-100"
    }`}
  >
    <SheetTrigger
      className={`
        flex items-center justify-center 
        bg-secondary-blue hover:bg-primary/90 
        text-primary-foreground 
        h-12 w-40 
        rounded-lg shadow-lg 
        -rotate-90
        transition-colors
      `}
    >
      Open Stats
    </SheetTrigger>
  </div>
);