'use client';

import React, { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { Button } from '@/components/ui/button';
import Image from 'next/image';

export default function VerificationSuccess() {

    const router = useRouter();

    return (
        <div className="flex flex-col items-center justify-center h-screen z-10">
            <div className="w-3/5 h-1/3 relative">
                <Image src="/img/Verified-pana.svg" alt='verified' layout='fill' objectFit='contain'/>
            </div>
            <h2 className="text-2xl font-bold mb-4 mt-8">Email Successfully Verified</h2>
            <p className="text-lg mb-4 text-muted-foreground">Your email has been successfully verified. Please log in to continue.</p>
            <Button variant="outline" className='hover:bg-muted transition duration-300' onClick={() => router.push('/auth/login')}>Login to Continue</Button>
        </div>
    );
}