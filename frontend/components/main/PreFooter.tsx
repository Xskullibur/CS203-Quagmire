import { Button } from "@/components/ui/button";
import MagicText from "@/components/ui/MagicText";

/**
 * PreFooter component renders a section with a call-to-action for hosting tournaments.
 * 
 * This component includes a heading and a button, styled with Tailwind CSS classes.
 * The heading uses the `MagicText` component to highlight the word "hosting".
 * 
 * @component
 * @example
 * return (
 *   <PreFooter />
 * )
 * 
 * @returns {JSX.Element} A section element containing a heading and a button.
 * 
 * @remarks
 * The section has a gradient background and responsive padding.
 * The heading is responsive and adjusts its size based on the screen width.
 * The button is styled as a large, secondary variant with rounded corners.
 * 
 * @see {@link MagicText} for the text highlighting component.
 * @see {@link Button} for the button component.
 */
export default function PreFooter() {
    return (
        <section className="w-full py-24 px-4 md:px-24 bg-gradient-to-b from-background via-background to-[#141516]">
            <div className="container mx-auto">
                <div className="flex flex-col md:flex-row justify-between items-center gap-8">
                    <h2 className="text-foreground text-3xl md:text-4xl font-bold leading-tight text-center md:text-left group">
                        Interested in{' '}
                        <MagicText className="text-3xl md:text-4xl font-bold">
                            hosting
                        </MagicText>
                        <br />
                        your own tournaments?
                    </h2>
                    <div>
                        <Button size="lg" variant="secondary" className="rounded-full"
                        onClick={() => window.location.href = "mailto:quagmire.smu@gmail.com?subject=Quagmire Administration Application"}>
                            Contact Us
                        </Button>
                    </div>
                </div>
            </div>
        </section>
    );
}