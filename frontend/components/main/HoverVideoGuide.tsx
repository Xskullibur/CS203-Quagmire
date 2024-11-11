import React, { useState, useRef, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';

interface Step {
    title: string;
    description: string;
    videoUrl: string;
}

interface HoverVideoGuideProps {
    steps: Step[];
}

/**
 * HoverVideoGuide component displays a list of steps with associated videos.
 * When a step is clicked, the corresponding video is played.
 * The component also adjusts its layout based on the screen size.
 *
 * @component
 * @param {HoverVideoGuideProps} props - The properties for the HoverVideoGuide component.
 * @param {Step[]} props.steps - An array of steps, each containing a title, description, and video URL.
 *
 * @example
 * const steps = [
 *   { title: 'Step 1', description: 'Description for step 1', videoUrl: 'video1.mp4' },
 *   { title: 'Step 2', description: 'Description for step 2', videoUrl: 'video2.mp4' },
 * ];
 * <HoverVideoGuide steps={steps} />
 *
 * @returns {JSX.Element} The rendered HoverVideoGuide component.
 *
 * @remarks
 * The component uses IntersectionObserver to detect when the section is in view and sets the active step accordingly.
 * It also listens for window resize events to determine if the screen is mobile-sized.
 *
 * @internal
 * @function
 * @name HoverVideoGuide
 */
const HoverVideoGuide: React.FC<HoverVideoGuideProps> = ({ steps }) => {
    const [activeStep, setActiveStep] = useState<Step>(steps[0]);
    const sectionRef = useRef<HTMLDivElement>(null);
    const videoRef = useRef<HTMLVideoElement>(null);
    const [activeIndex, setActiveIndex] = useState<number>(0);
    const [isMobile, setIsMobile] = useState<boolean>(false);

    useEffect(() => {
        const checkMobile = () => {
            setIsMobile(window.innerWidth < 1024);
        };

        checkMobile();
        window.addEventListener('resize', checkMobile);

        return () => window.removeEventListener('resize', checkMobile);
    }, []);

    useEffect(() => {
        const currentRef = sectionRef.current; // Capture the current value

        const observer = new IntersectionObserver(
            (entries) => {
                if (entries[0].isIntersecting) {
                    setActiveStep(steps[0]);
                    setActiveIndex(0);
                }
            },
            { threshold: 0.1 }
        );

        if (currentRef) {
            observer.observe(currentRef);
        }

        return () => {
            if (currentRef) {
                observer.unobserve(currentRef);
            }
        };
    }, [steps]);

    useEffect(() => {
        if (activeStep && videoRef.current) {
            videoRef.current.src = activeStep.videoUrl;
            videoRef.current.play().catch(error => console.log("Auto-play was prevented:", error));
        }
    }, [activeStep]);

    const handleStepClick = (step: Step, index: number) => {
        setActiveStep(step);
        setActiveIndex(index);
    };

    return (
        <motion.div
            ref={sectionRef}
            className="container mx-auto py-8 px-4 lg:px-20 text-white"
            initial={{ opacity: 0, y: 50 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5 }}
        >
            <motion.p
                className="text-sm text-center mb-4 font-mono text-zinc-400"
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.5, delay: 0.2 }}
            >
                Solo Queue Guide.
            </motion.p>

            <div className="flex flex-col lg:flex-row items-start justify-between gap-8 mt-16">
                <div className="w-full lg:w-1/2 space-y-4">
                    <AnimatePresence>
                        {steps.map((step, index) => (
                            <motion.div
                                key={index}
                                initial={{ opacity: 0, x: -20 }}
                                animate={{ opacity: 1, x: 0 }}
                                exit={{ opacity: 0, x: -20 }}
                                transition={{ duration: 0.5, delay: 0.1 * index }}
                                className={`p-4 rounded-lg transition-all duration-300 cursor-pointer ${activeStep === step
                                    ? 'bg-zinc-800 text-white border border-zinc-700'
                                    : 'text-white hover:bg-zinc-900'
                                    }`}
                                onClick={() => handleStepClick(step, index)}
                            >
                                <h3 className="text-lg md:text-xl font-semibold">{index + 1}. {step.title}</h3>
                                <p className="text-sm mt-2">{step.description}</p>
                            </motion.div>
                        ))}
                    </AnimatePresence>
                </div>
                <div className="w-full lg:w-1/2 mt-8 lg:mt-0 lg:sticky lg:top-8">
                    <motion.div
                        className="w-full rounded-lg overflow-hidden shadow-lg shadow-zinc-800 relative"
                        animate={!isMobile ? {
                            y: `${activeIndex * 4}rem`
                        } : {}}
                        transition={{ duration: 0.3 }}
                    >
                        <div className="relative w-full pt-[56.25%] z-10">
                            <video
                                ref={videoRef}
                                src={activeStep.videoUrl}
                                autoPlay
                                loop
                                muted
                                playsInline
                                className="absolute top-0 left-0 w-full h-full object-contain bg-black"
                            />
                        </div>
                    </motion.div>
                </div>
            </div>
        </motion.div>
    );
};

export default HoverVideoGuide;