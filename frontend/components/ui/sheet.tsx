"use client";

import * as React from "react";
import * as SheetPrimitive from "@radix-ui/react-dialog";
import { cva, type VariantProps } from "class-variance-authority";
import { X } from "lucide-react";
import { cn } from "@/lib/utils";

const Sheet = SheetPrimitive.Root;

const SheetTrigger = SheetPrimitive.Trigger;

const SheetClose = SheetPrimitive.Close;

const SheetPortal = SheetPrimitive.Portal;

const sheetVariants = cva(
  "absolute z-10 gap-4 bg-background shadow-lg transition-transform ease-in-out duration-500",
  {
    variants: {
      side: {
        left: "top-0 left-0 h-[calc(100vh-8rem)] w-1/2 border-r",
        right: "top-0 right-0 h-[calc(100vh-8rem)] w-1/2 border-l",
      },
    },
    defaultVariants: {
      side: "right",
    },
  }
);

interface SheetContentProps
  extends React.ComponentPropsWithoutRef<typeof SheetPrimitive.Content>,
    VariantProps<typeof sheetVariants> {
  containerRef?: React.RefObject<HTMLElement>;
}

const SheetContent = React.forwardRef<
  React.ElementRef<typeof SheetPrimitive.Content>,
  SheetContentProps
>(({ side = "right", className, children, containerRef, ...props }, ref) => {
  const [isOpen, setIsOpen] = React.useState(false);

  React.useEffect(() => {
    const container = containerRef?.current;
    const parent = container?.parentElement;

    if (container && parent) {
      // Apply initial transition styles
      container.style.transition = "all 0.5s ease-in-out";
      container.style.width = "100%";
      container.style.marginLeft = "auto";
      container.style.marginRight = "auto";
      parent.style.transition = "padding 0.5s ease-in-out";

      if (isOpen) {
        // Animate to open state
        requestAnimationFrame(() => {
          container.style.width = "50%";
          parent.style.paddingRight = "50%";
        });
      } else {
        // Animate to closed state
        requestAnimationFrame(() => {
          container.style.width = "100%";
          parent.style.paddingRight = "0";
        });
      }
    }
  }, [isOpen, containerRef]);

  return (
    <SheetPrimitive.Content
      ref={ref}
      onOpenAutoFocus={(e) => {
        e.preventDefault();
        setIsOpen(true);
      }}
      onCloseAutoFocus={(e) => {
        e.preventDefault();
        setIsOpen(false);
      }}
      className={cn(
        sheetVariants({ side }),
        isOpen
          ? "translate-x-0"
          : side === "right"
            ? "translate-x-full"
            : "-translate-x-full",
        "will-change-transform",
        className
      )}
      {...props}
    >
      <div className="h-full flex flex-col pt-4">{children}</div>
      <SheetClose className="absolute right-4 top-4 rounded-sm opacity-70 ring-offset-background transition-opacity hover:opacity-100 focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 disabled:pointer-events-none data-[state=open]:bg-secondary">
        <X className="h-4 w-4" />
        <span className="sr-only">Close</span>
      </SheetClose>
    </SheetPrimitive.Content>
  );
});
SheetContent.displayName = SheetPrimitive.Content.displayName;

const SheetHeader = ({
  className,
  ...props
}: React.HTMLAttributes<HTMLDivElement>) => (
  <div
    className={cn(
      "flex flex-col space-y-2 text-center sm:text-left",
      className
    )}
    {...props}
  />
);
SheetHeader.displayName = "SheetHeader";

const SheetFooter = ({
  className,
  ...props
}: React.HTMLAttributes<HTMLDivElement>) => (
  <div
    className={cn(
      "flex flex-col-reverse sm:flex-row sm:justify-end sm:space-x-2",
      className
    )}
    {...props}
  />
);
SheetFooter.displayName = "SheetFooter";

const SheetTitle = React.forwardRef<
  React.ElementRef<typeof SheetPrimitive.Title>,
  React.ComponentPropsWithoutRef<typeof SheetPrimitive.Title>
>(({ className, ...props }, ref) => (
  <SheetPrimitive.Title
    ref={ref}
    className={cn("text-lg font-semibold text-foreground", className)}
    {...props}
  />
));
SheetTitle.displayName = SheetPrimitive.Title.displayName;

const SheetDescription = React.forwardRef<
  React.ElementRef<typeof SheetPrimitive.Description>,
  React.ComponentPropsWithoutRef<typeof SheetPrimitive.Description>
>(({ className, ...props }, ref) => (
  <SheetPrimitive.Description
    ref={ref}
    className={cn("text-sm text-muted-foreground", className)}
    {...props}
  />
));
SheetDescription.displayName = SheetPrimitive.Description.displayName;

export {
  Sheet,
  SheetTrigger,
  SheetClose,
  SheetContent,
  SheetHeader,
  SheetFooter,
  SheetTitle,
  SheetDescription,
};
