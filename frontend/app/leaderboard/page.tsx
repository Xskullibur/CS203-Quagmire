'use client'

import React from 'react';
import { LeaderboardPosition } from '@/components/ui/leaderboardPosition';
import {data} from '@/app/leaderboard/db.js';

data.sort((a,b)=> {
    if(a.score < b.score){
        return -1;
    } else {
        return 1
    }
});

export default function Leaderboard() {
    return (
        <div className="flex flex-col items-center justify-center min-h-screen">
            <h2 className="text-2xl font-bold mb-4">Leaderboard</h2>
            <div className="w-4/5 h-auto bg-card">
                <ul>
                    <li>
                        <LeaderboardPosition name="name" position="position" rating="rating"></LeaderboardPosition>
                    </li>
                    <li>
            
                    </li>
                </ul>

            </div>
        </div>
    );
}