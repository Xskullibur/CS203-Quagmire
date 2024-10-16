import React, { useEffect, useRef } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faBuilding, faWarehouse, faStore, faLandmark, faMapMarked, IconDefinition } from '@fortawesome/free-solid-svg-icons';

interface CardProps {
    icon: IconDefinition;
    title: string;
    description: string;
}

const Card: React.FC<CardProps> = ({ icon, title, description }) => {
    return (
        <div className="card bg-opacity-10 bg-white rounded-lg cursor-pointer flex flex-col relative w-[300px] h-[260px] overflow-hidden group">
            <div className="absolute inset-0 opacity-0 group-hover:opacity-100 transition-opacity duration-500 bg-[radial-gradient(800px_circle_at_var(--mouse-x)_var(--mouse-y),rgba(255,255,255,0.06),transparent_40%)] z-10"></div>
            <div className="absolute inset-0 opacity-0 group-hover:opacity-100 transition-opacity duration-500 bg-[radial-gradient(600px_circle_at_var(--mouse-x)_var(--mouse-y),rgba(255,255,255,0.4),transparent_40%)] z-[1]"></div>
            <div className="card-content bg-[#171717] rounded-lg flex flex-col flex-grow m-[1px] p-[10px] z-[2]">
                <div className="card-image flex items-center justify-center h-[140px] overflow-hidden">
                    <FontAwesomeIcon icon={icon} className="text-[6em] opacity-25" />
                </div>
                <div className="card-info-wrapper flex items-center flex-grow justify-start px-[20px]">
                    <div className="card-info flex items-start gap-[10px]">
                        <FontAwesomeIcon icon={icon} className="text-[1em] leading-[20px]" />
                        <div className="card-info-title">
                            <h3 className="text-[1.1em] leading-[20px]">{title}</h3>
                            <h4 className="text-[0.85em] mt-[8px] text-white/50">{description}</h4>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

/**
 * HeroCards component renders a collection of Card components with interactive mouse movement effects.
 * 
 * This component uses a `useRef` hook to reference the container div and a `useEffect` hook to add
 * and clean up a mousemove event listener. The event listener updates custom CSS properties 
 * (`--mouse-x` and `--mouse-y`) on each card to create a dynamic visual effect based on the mouse position.
 * 
 * @component
 * @returns {JSX.Element} The rendered HeroCards component.
 * 
 * @example
 * // Example usage of HeroCards component
 * import HeroCards from './HeroCards';
 * 
 * function App() {
 *   return (
 *     <div>
 *       <HeroCards />
 *     </div>
 *   );
 * }
 * 
 * @remarks
 * - The component assumes that each Card component has a class name of "card".
 * - The CSS properties `--mouse-x` and `--mouse-y` should be used within the Card component's styles to achieve the desired effect.
 * - Ensure that the FontAwesome icons (faBuilding, faWarehouse, etc.) are imported and available in the scope.
 * 
 * @see {@link https://reactjs.org/docs/hooks-reference.html#useref | useRef}
 * @see {@link https://reactjs.org/docs/hooks-reference.html#useeffect | useEffect}
 */
const HeroCards: React.FC = () => {
    const cardsRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
        const cards = cardsRef.current;

        const handleMouseMove = (e: MouseEvent) => {
            if (!cards) return;
            for (const card of Array.from(cards.getElementsByClassName("card"))) {
                const rect = card.getBoundingClientRect(),
                    x = e.clientX - rect.left,
                    y = e.clientY - rect.top;

                (card as HTMLElement).style.setProperty("--mouse-x", `${x}px`);
                (card as HTMLElement).style.setProperty("--mouse-y", `${y}px`);
            }
        };

        cards?.addEventListener("mousemove", handleMouseMove);

        return () => {
            cards?.removeEventListener("mousemove", handleMouseMove);
        };
    }, []);

    return (
        <div
            ref={cardsRef}
            className="flex flex-wrap gap-2 mx-auto justify-center py-8"
        >
            <Card
                icon={faBuilding}
                title="Apartments"
                description="Places to be apart. Wait, what?"
            />
            <Card
                icon={faWarehouse}
                title="Warehouses"
                description="Store your stuff here. Or not."
            />
            <Card
                icon={faStore}
                title="Stores"
                description="Buy stuff you don't need."
            />
            <Card
                icon={faBuilding}
                title="Buildings"
                description="Where you go to do stuff."
            />
            <Card
                icon={faLandmark}
                title="Landmarks"
                description="Places you can't miss."
            />
            <Card
                icon={faMapMarked}
                title="Maps"
                description="Find your way around."
            />
            {/* Add more Card components here */}
            <div className="circular-gradient bg-gradient-to-b from-black to-white"></div>
        </div>
    );
};

export default HeroCards;