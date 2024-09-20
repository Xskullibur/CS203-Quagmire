// hooks/useGeolocation.ts

import { useState, useEffect } from 'react';

interface Location {
    latitude: number;
    longitude: number;
}

export const useGeolocation = () => {
    const [location, setLocation] = useState<Location | null>(null);

    useEffect(() => {
        if ('geolocation' in navigator) {
            navigator.geolocation.getCurrentPosition(
                (position) => {
                    setLocation({
                        latitude: position.coords.latitude,
                        longitude: position.coords.longitude
                    });
                },
                (error) => {
                    console.error('Error getting location:', error);
                }
            );
        } else {
            console.error('Geolocation is not supported by this browser.');
        }
    }, []);

    return { location };
};