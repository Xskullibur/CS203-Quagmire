import React, { useState, useRef, useEffect } from 'react';
import { motion } from 'framer-motion';

const HoverVideoGuide = ({ steps }) => {
    const [activeStep, setActiveStep] = useState(steps[0]);
    const sectionRef = useRef(null);
    const videoRef = useRef(null);
    const [activeIndex, setActiveIndex] = useState(0);
    const [isMobile, setIsMobile] = useState(false);

    useEffect(() => {
        const checkMobile = () => {
            setIsMobile(window.innerWidth < 1024); // Assuming 1024px is the breakpoint for lg
        };

        checkMobile();
        window.addEventListener('resize', checkMobile);

        return () => window.removeEventListener('resize', checkMobile);
    }, []);

    useEffect(() => {
        const observer = new IntersectionObserver(
            (entries) => {
                if (entries[0].isIntersecting) {
                    setActiveStep(steps[0]);
                    setActiveIndex(0);
                }
            },
            { threshold: 0.1 }
        );

        if (sectionRef.current) {
            observer.observe(sectionRef.current);
        }

        return () => {
            if (sectionRef.current) {
                observer.unobserve(sectionRef.current);
            }
        };
    }, [steps]);

    useEffect(() => {
        if (activeStep && videoRef.current) {
            videoRef.current.src = activeStep.videoUrl;
            videoRef.current.play().catch(error => console.log("Auto-play was prevented:", error));
        }
    }, [activeStep]);

    const handleStepClick = (step, index) => {
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
                Guide.
            </motion.p>

            <div className="flex flex-col lg:flex-row items-start justify-between gap-8 mt-16">
                <div className="w-full lg:w-1/2 space-y-4">
                    {steps.map((step, index) => (
                        <motion.div
                            key={index}
                            initial={{ opacity: 0, x: -20 }}
                            animate={{ opacity: 1, x: 0 }}
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
                </div>
                <div className="w-full lg:w-1/2 mt-8 lg:mt-0 lg:sticky lg:top-8">
                    <motion.div
                        className="w-full rounded-lg overflow-hidden shadow-lg shadow-zinc-800"
                        animate={!isMobile ? {
                            y: `${activeIndex * 4}rem`
                        } : {}}
                        transition={{ duration: 0.3 }}
                    >
                        <div className="relative w-full pt-[56.25%]">
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