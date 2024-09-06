import React, { useEffect, useRef } from 'react';
import PropTypes from 'prop-types';

const MagicText = ({ children, className = '' }) => {
    const magicRef = useRef(null);

    useEffect(() => {
        const magic = magicRef.current;
        if (!magic) return;

        const animate = (star) => {
            const newLeft = `${Math.floor(Math.random() * 110 - 5)}%`;
            const newTop = `${Math.floor(Math.random() * 120 - 20)}%`;
            star.style.setProperty("--star-left", newLeft);
            star.style.setProperty("--star-top", newTop);
            star.style.animation = "none";
            star.offsetHeight; // Trigger reflow
            star.style.animation = null; // Reset to default
        };

        let intervals = [];

        const animateStarWithDelay = (star, delay) => {
            setTimeout(() => {
                animate(star);
                intervals.push(setInterval(() => animate(star), 1000));
            }, delay);
        };

        const onMouseEnter = () => {
            const stars = magic.querySelectorAll(".magic-star");
            stars.forEach((star, index) => {
                animateStarWithDelay(star, index * 300);
            });
        };

        const onMouseLeave = () => {
            intervals.forEach(clearInterval);
            intervals = [];
        };

        magic.addEventListener('mouseenter', onMouseEnter);
        magic.addEventListener('mouseleave', onMouseLeave);

        return () => {
            magic.removeEventListener('mouseenter', onMouseEnter);
            magic.removeEventListener('mouseleave', onMouseLeave);
            intervals.forEach(clearInterval);
        };
    }, []);

    return (
        <span ref={magicRef} className={`magic inline-block relative ${className}`}>
            {[...Array(3)].map((_, i) => (
                <span key={i} className="magic-star absolute block w-[clamp(20px,1.5vw,30px)] h-[clamp(20px,1.5vw,30px)]" style={{ '--star-left': '50%', '--star-top': '50%' }}>
                    <svg viewBox="0 0 512 512" className="w-full h-full animate-rotate opacity-70">
                        <path d="M512 255.1c0 11.34-7.406 20.86-18.44 23.64l-171.3 42.78l-42.78 171.1C276.7 504.6 267.2 512 255.9 512s-20.84-7.406-23.62-18.44l-42.66-171.2L18.47 279.6C7.406 276.8 0 267.3 0 255.1c0-11.34 7.406-20.83 18.44-23.61l171.2-42.78l42.78-171.1C235.2 7.406 244.7 0 256 0s20.84 7.406 23.62 18.44l42.78 171.2l171.2 42.78C504.6 235.2 512 244.6 512 255.1z" className="fill-violet-500" />
                    </svg>
                </span>
            ))}
            <span className="magic-text inline-block bg-gradient-to-r from-purple-700 via-violet-600 to-pink-400 bg-[length:200%] bg-clip-text text-transparent whitespace-nowrap animate-background-pan">
                {children}
            </span>
        </span>
    );
};

MagicText.propTypes = {
    children: PropTypes.node.isRequired,
    className: PropTypes.string
};

export default MagicText;