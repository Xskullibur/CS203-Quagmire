import React from 'react';
import dynamic from 'next/dynamic';

const MapContainer = dynamic(
    () => import('react-leaflet').then((mod) => mod.MapContainer),
    { ssr: false }
);
const TileLayer = dynamic(
    () => import('react-leaflet').then((mod) => mod.TileLayer),
    { ssr: false }
);
const Marker = dynamic(
    () => import('react-leaflet').then((mod) => mod.Marker),
    { ssr: false }
);
const Popup = dynamic(
    () => import('react-leaflet').then((mod) => mod.Popup),
    { ssr: false }
);

import 'leaflet/dist/leaflet.css';
import L from 'leaflet';

interface MatchMapProps {
    meetingPoint: [number, number];
    playerLocation: [number, number];
}

/**
 * MatchMap component displays a map with markers for the player's location and a meeting point.
 * 
 * @component
 * @param {MatchMapProps} props - The properties for the MatchMap component.
 * @param {L.LatLngExpression} props.meetingPoint - The coordinates of the meeting point.
 * @param {L.LatLngExpression} props.playerLocation - The coordinates of the player's location.
 * 
 * @returns {JSX.Element | null} A JSX element containing the map with markers, or null if rendered on the server side.
 * 
 * @example
 * ```tsx
 * <MatchMap 
 *   meetingPoint={[51.505, -0.09]} 
 *   playerLocation={[51.505, -0.09]} 
 * />
 * ```
 */
const MatchMap: React.FC<MatchMapProps> = ({ meetingPoint, playerLocation }) => {
    const icon = L.icon({
        iconUrl: '/apple.png',
        iconSize: [25, 41],
        iconAnchor: [12, 41],
    });

    if (typeof window === 'undefined') {
        return null;
    }

    return (

        <MapContainer center={playerLocation} zoom={20} style={{ height: '400px', width: '100%' }} className='rounded-lg'>
            <TileLayer
                url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
            />
            <Marker position={playerLocation} icon={icon}>
                <Popup>Your location</Popup>
            </Marker>
            <Marker position={meetingPoint} icon={icon}>
                <Popup>Meeting point</Popup>
            </Marker>
        </MapContainer>

    );
};

export default MatchMap;