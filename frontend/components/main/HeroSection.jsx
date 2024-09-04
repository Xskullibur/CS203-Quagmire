// components/HeroSection.jsx
import Image from 'next/image';
import { Button } from "@/components/ui/button";

export default function HeroSection() {
    return (
        <div className="flex flex-col items-center justify-center min-h-screen w-full text-foreground p-4">
            <div className="text-center mb-8 mt-8">
                <span className="text-6xl">💪</span>
                <h1 className="text-4xl md:text-6xl font-bold mb-4 mt-4 bg-clip-text text-transparent bg-gradient-to-r from-primary-pink to-secondary-blue">
                    Hand Hathaway
                </h1>
                <p className="text-xl text-muted-foreground mb-8">
                    Welcome to the ultimate arm wrestling platform
                    <br />
                    Compete against others in solo queue or tournaments
                    <br />
                    Rise to the top and claim your title as the arm wrestling champion.
                </p>
                <div className="mb-8 flex flex-col sm:flex-row justify-center gap-4">
                    <Button size="lg" className="bg-primary text-primary-foreground hover:bg-primary/90 transition">
                        Solo Queue
                    </Button>
                    <Button size="lg" className="bg-primary text-primary-foreground hover:bg-primary/90 transition">
                        Tournament
                    </Button>
                </div>
            </div>

            <div className="relative w-full max-w-5xl">
                <Image
                    src="/linear.png"
                    alt="Hero image"
                    width={1920}
                    height={1080}
                    layout="responsive"
                    className="rounded-lg"
                />
                <div className="absolute inset-0 bg-gradient-to-b from-transparent to-background rounded-lg" />
            </div>
        </div>
    );
}