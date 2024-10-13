// hooks/useGeolocation.ts

import { useState, useEffect } from 'react';

interface GeolocationState {
    location: { latitude: number; longitude: number } | null;
    error: string | null;
    loading: boolean;
}

/**
 * Custom hook to get the current geolocation of the user.
 *
 * @param {Object} [options={}] - Optional configuration object for geolocation.
 * @param {boolean} [options.enableHighAccuracy=true] - Indicates if high accuracy is desired.
 * @param {number} [options.timeout=10000] - Maximum time (in milliseconds) to wait for a position.
 * @param {number} [options.maximumAge=0] - Maximum age (in milliseconds) of a possible cached position.
 *
 * @returns {Object} state - The state object containing geolocation information.
 * @returns {Object|null} state.location - The current location of the user.
 * @returns {number} state.location.latitude - The latitude of the current location.
 * @returns {number} state.location.longitude - The longitude of the current location.
 * @returns {string|null} state.error - Error message if geolocation fails.
 * @returns {boolean} state.loading - Indicates if the geolocation request is in progress.
 *
 * @example
 * const { location, error, loading } = useGeolocation();
 * if (loading) {
 *   return <p>Loading...</p>;
 * }
 * if (error) {
 *   return <p>Error: {error}</p>;
 * }
 * return <p>Latitude: {location.latitude}, Longitude: {location.longitude}</p>;
 */
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