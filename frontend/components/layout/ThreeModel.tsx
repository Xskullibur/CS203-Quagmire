// components/ThreeModel.tsx

'use client';

import React, { Suspense, useEffect, memo, useMemo, useRef } from 'react';
import { Canvas, useFrame } from '@react-three/fiber';
import { useGLTF, useAnimations } from '@react-three/drei';
import { Group, Material, Object3D, Vector3 } from 'three';
import { GLTF } from 'three-stdlib';

type ModelProps = {
    url: string;
    modelPosition: Vector3;
};

type GLTFResult = GLTF & {
    nodes: { [key: string]: Object3D };
    materials: { [key: string]: Material };
};

const Model: React.FC<ModelProps> = ({ url, modelPosition }) => {
    const { scene, animations } = useGLTF(url) as GLTFResult;
    const { ref, mixer } = useAnimations(animations, scene);

    useEffect(() => {
        if (animations.length > 0) {
            animations.forEach((clip) => mixer.clipAction(clip).play());
        }
    }, [animations, mixer]);

    const memoizedScene = useMemo(() => {
        scene.traverse((child) => {
            if ((child as Object3D & { material: Material }).material) {
                ((child as Object3D & { material: Material }).material as Material).transparent = false;
                ((child as Object3D & { material: Material }).material as Material).opacity = 1;
            }
        });

        return scene;
    }, [scene]);

    return <primitive object={memoizedScene} ref={ref} position={modelPosition} />;
};

const RotatingModel: React.FC<ModelProps> = ({ url, modelPosition }) => {
    const groupRef = useRef<Group>(null);

    useFrame((state, delta) => {
        if (groupRef.current) {
            groupRef.current.rotation.y += delta * 0.05;
        }
    });

    return (
        <group ref={groupRef}>
            <Model url={url} modelPosition={modelPosition} />
        </group>
    );
};

type ThreeModelProps = {
    modelUrl: string;
    zoomLength: number;
    position: Vector3;
    modelPosition: Vector3;
};

const ThreeModel: React.FC<ThreeModelProps> = memo(({ modelUrl, zoomLength, position, modelPosition }) => {
    return (
        <Canvas camera={{ position: position, zoom: zoomLength }}>
            <Suspense fallback={null}>
                <ambientLight intensity={0.75} />
                <RotatingModel url={modelUrl} modelPosition={modelPosition} />
            </Suspense>
        </Canvas>
    );
});

ThreeModel.displayName = 'ThreeModel';

export default ThreeModel;