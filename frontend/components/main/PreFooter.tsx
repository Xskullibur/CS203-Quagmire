import { Button } from "@/components/ui/button";
import MagicText from "@/components/ui/MagicText";

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
                        <Button size="lg" variant="secondary" className="rounded-full">
                            Get started
                        </Button>
                    </div>
                </div>
            </div>
        </section>
    );
}