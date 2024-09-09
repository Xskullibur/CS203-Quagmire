import React, { useEffect, useRef } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faBuilding, faWarehouse, faStore, faLandmark, faMapMarked } from '@fortawesome/free-solid-svg-icons';
import PropTypes from 'prop-types';

const Card = ({ icon, title, description }) => {
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

Card.propTypes = {
    icon: PropTypes.object.isRequired,
    title: PropTypes.string.isRequired,
    description: PropTypes.string.isRequired,
};

const HeroCards = () => {
    const cardsRef = useRef(null);

    useEffect(() => {
        const cards = cardsRef.current;

        const handleMouseMove = (e) => {
            for (const card of cards.getElementsByClassName("card")) {
                const rect = card.getBoundingClientRect(),
                    x = e.clientX - rect.left,
                    y = e.clientY - rect.top;

                card.style.setProperty("--mouse-x", `${x}px`);
                card.style.setProperty("--mouse-y", `${y}px`);
            }
        };

        cards.addEventListener("mousemove", handleMouseMove);


        return () => {
            cards.removeEventListener("mousemove", handleMouseMove);
        };
    }, []);

    return (
        <div
            ref={cardsRef}
            className="flex flex-wrap gap-2 w-[calc(100%-15vw)] m-16 mx-auto justify-center px-20"
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
        </div>
    );
};

export default HeroCards;