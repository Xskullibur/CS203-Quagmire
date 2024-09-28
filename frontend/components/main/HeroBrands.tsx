import React, { useEffect, useRef } from 'react';
import Image from 'next/image';

const brands = [
    { name: 'PPP Coffee', logoUrl: '/brands/brand1.png' },
    { name: 'The Providore', logoUrl: '/brands/brand1.png' },
    { name: 'Pok Pok', logoUrl: '/brands/brand1.png' },
    { name: 'Alt Pizza', logoUrl: '/brands/brand1.png' },
    { name: 'Mui Kee', logoUrl: '/brands/brand1.png' },
    { name: 'Peperoni', logoUrl: '/brands/brand1.png' },
    { name: 'Killiney', logoUrl: '/brands/brand1.png' },
    { name: 'Poke Theory', logoUrl: '/brands/brand1.png' },
    { name: 'Tart', logoUrl: '/brands/brand1.png' },
];

export function HeroBrands() {
    const scrollRef = useRef<HTMLDivElement>(null);
    const containerRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
        const scrollContainer = scrollRef.current;
        const container = containerRef.current;
        if (!scrollContainer || !container) return;

        const scrollWidth = scrollContainer.scrollWidth;

        let scrollPosition = 0;
        let animationId: number;

        const scroll = () => {
            scrollPosition += 0.5;
            if (scrollPosition >= scrollWidth / 2) {
                scrollPosition = 0;
                scrollContainer.style.transition = 'none';
                scrollContainer.style.transform = `translateX(0)`;
                setTimeout(() => {
                    scrollContainer.style.transition = 'transform 0.5s linear';
                }, 10);
            } else {
                scrollContainer.style.transform = `translateX(-${scrollPosition}px)`;
            }
            animationId = requestAnimationFrame(scroll);
        };

        animationId = requestAnimationFrame(scroll);

        return () => cancelAnimationFrame(animationId);
    }, []);

    return (
        <div className="w-full py-12 text-white mt-16">
            <div className="container mx-auto px-4">
                <p className="text-sm text-center mb-4 font-mono text-zinc-400">Our Glorious Partners</p>
                <div ref={containerRef} className="overflow-hidden">
                    <div
                        ref={scrollRef}
                        className="flex space-x-24 whitespace-nowrap"
                        style={{ width: 'fit-content' }}
                    >
                        {[...brands, ...brands].map((brand, index) => (
                            <div key={`${brand.name}-${index}`} className="inline-block">
                                <div className="w-48 h-48 relative">
                                    <Image
                                        className='opacity-85 hover:opacity-100 transition-opacity duration-300'
                                        src={brand.logoUrl}
                                        alt={brand.name}
                                        fill
                                        style={{ objectFit: 'contain' }}
                                    />
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </div>
    );
}