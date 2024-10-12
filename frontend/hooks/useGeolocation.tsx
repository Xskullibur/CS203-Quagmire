// hooks/useGeolocation.ts

import { useState, useEffect } from 'react';

interface GeolocationState {
    location: { latitude: number; longitude: number } | null;
    error: string | null;
    loading: boolean;
}

export const useGeolocation = (options = {}) => {
    const [state, setState] = useState<GeolocationState>({
        location: null,
        error: null,
        loading: true,
    });

    useEffect(() => {
        if (!navigator.geolocation) {
            setState(prev => ({ ...prev, error: "Geolocation is not supported by your browser", loading: false }));
            return;
        }

        const handleSuccess = (position: GeolocationPosition) => {
            setState({
                location: {
                    latitude: position.coords.latitude,
                    longitude: position.coords.longitude,
                },
                error: null,
                loading: false,
            });
        };

        const handleError = (error: GeolocationPositionError) => {
            setState(prev => ({ ...prev, error: error.message, loading: false }));
        };

        const geoOptions = {
            enableHighAccuracy: true,
            timeout: 10000,  // Increased timeout to 10 seconds
            maximumAge: 0,
            ...options,
        };

        const watchId = navigator.geolocation.watchPosition(handleSuccess, handleError, geoOptions);

        return () => navigator.geolocation.clearWatch(watchId);
    }, [options]);

    return state;
};