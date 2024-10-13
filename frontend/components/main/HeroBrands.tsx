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

/**
 * HeroBrands component displays a horizontally scrolling list of brand logos.
 * The logos scroll continuously from right to left and reset to the beginning
 * once they reach the halfway point of the scrollable width.
 *
 * @component
 * @example
 * // Example usage:
 * <HeroBrands />
 *
 * @returns {JSX.Element} The HeroBrands component.
 *
 * @remarks
 * This component uses the `useRef` hook to reference the scrollable container
 * and the outer container. The `useEffect` hook is used to initiate the scrolling
 * animation and reset the scroll position when it reaches halfway.
 *
 * The scrolling effect is achieved by incrementing the `scrollPosition` and
 * applying a CSS transform to translate the scrollable container. When the
 * `scrollPosition` reaches half of the scrollable width, it resets to zero
 * and removes the transition temporarily to create a seamless scrolling effect.
 *
 * The component renders a list of brand logos, which are duplicated to create
 * a continuous scrolling effect. Each logo is displayed within a fixed-size
 * container and has a hover effect to change its opacity.
 *
 * @hook
 * - `useRef` to create references for the scrollable container and outer container.
 * - `useEffect` to handle the scrolling animation and cleanup.
 *
 * @dependencies
 * - `Image` component for displaying brand logos.
 *
 * @styles
 * - The component uses Tailwind CSS classes for styling.
 * - The logos have a hover effect to change opacity.
 *
 * @note
 * Ensure that the `brands` array is defined and contains the necessary brand
 * information (name and logoUrl) for the component to render correctly.
 */
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