import Image from 'next/image';
import { Button } from "@/components/ui/button";
import { motion } from 'framer-motion';

export default function HeroSection() {
    return (
        <div className="flex flex-col items-center justify-center min-h-screen w-full text-foreground p-4">
            <motion.div
                className="text-center mb-8 mt-8"
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.8 }}
            >
                <motion.span
                    className="text-6xl"
                    initial={{ scale: 0 }}
                    animate={{ scale: 1 }}
                    transition={{ delay: 0.2, type: 'spring', stiffness: 260, damping: 20 }}
                >
                    💪
                </motion.span>
                <motion.h1
                    className="text-4xl md:text-6xl font-bold mb-4 mt-4 bg-clip-text text-transparent bg-gradient-to-r from-primary-pink to-secondary-blue"
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 1 }}
                    transition={{ delay: 0.4, duration: 0.8 }}
                >
                    Hand Hathaway
                </motion.h1>
                <motion.p
                    className="text-xl text-muted-foreground mb-8"
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 1 }}
                    transition={{ delay: 0.6, duration: 0.8 }}
                >
                    Welcome to the ultimate arm wrestling platform
                    <br />
                    Compete against others in solo queue or tournaments
                    <br />
                    Rise to the top and claim your title as the arm wrestling champion.
                </motion.p>
                <motion.div
                    className="mb-8 flex flex-col sm:flex-row justify-center gap-4"
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ delay: 0.8, duration: 0.8 }}
                >
                    <Button size="lg" className="bg-primary text-primary-foreground hover:bg-primary/90 transition">
                        Solo Queue
                    </Button>
                    <Button size="lg" className="bg-primary text-primary-foreground hover:bg-primary/90 transition">
                        Tournament
                    </Button>
                </motion.div>
            </motion.div>
            <motion.div
                className="relative w-full max-w-5xl"
                initial={{ opacity: 0, scale: 0.9 }}
                animate={{ opacity: 1, scale: 1 }}
                transition={{ delay: 1, duration: 0.8 }}
            >
                <Image
                    src="/linear.png"
                    alt="Hero image"
                    width={1920}
                    height={1080}
                    layout="responsive"
                    className="rounded-lg"
                />
                <div className="absolute inset-0 bg-gradient-to-b from-transparent to-background rounded-lg" />
            </motion.div>
        </div>
    );
}