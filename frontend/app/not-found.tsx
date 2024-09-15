// app/not-found.tsx
'use client';

import React from 'react';
import { useRouter } from 'next/navigation';
import { Button } from '@/components/ui/button';
import ThreeModel from '@/components/layout/ThreeModel';
import { Vector3 } from 'three';

const models = [
    {
        url: './renders/charmander.glb',
        zoomLength: 1,
        position: new Vector3(0, 0, 70),
        modelPosition: new Vector3(0, -20, 0)
    },
];

export default function NotFound() {
    const router = useRouter();

    return (
        <div className="flex flex-col items-center justify-center h-screen z-10">
            <div className="w-3/5 h-1/3">
                <ThreeModel
                    aria-label="3D model of charmander"
                    modelUrl={models[0].url}
                    zoomLength={models[0].zoomLength}
                    position={models[0].position}
                    modelPosition={models[0].modelPosition}
                />
            </div>
            <h2 className="text-2xl font-bold mb-4 mt-8">404 - Page Not Found</h2>
            <p className="text-lg mb-4 text-muted-foreground">Oops! The page you&apos;re looking for doesn&apos;t exist.</p>
            <Button variant="outline" className='hover:bg-muted transition duration-300' onClick={() => router.push('/')}>Go Home</Button>
        </div>
    );
}