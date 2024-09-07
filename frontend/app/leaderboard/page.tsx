'use client'

import React from 'react';
import { LeaderboardPosition } from '@/components/ui/leaderboardPosition';
import { Card } from '@/components/ui/card'
import { data } from '@/app/leaderboard/db.js';

data.sort((a, b) => {
    if (a.score < b.score) {
        return 1;
    } else {
        return -1
    }
});

export default function Leaderboard() {
    return (
        <div className="flex flex-col items-center justify-center min-h-screen">
            <h2 className="text-2xl font-bold mb-4">Leaderboard</h2>
            <div className="w-4/5 h-auto bg-card">
                <ul>
                    {data.map((user, idx) => (
                        <li>
                            <LeaderboardPosition key={idx} name={user.name} position={idx} rating={user.score} image={user.img}></LeaderboardPosition>
                        </li>
                    ))}
                </ul>

            </div>
        </div>
    );
}