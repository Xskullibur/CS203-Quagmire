import * as React from 'react';
import Image from 'next/image';

import { Card, CardHeader, CardDescription, CardContent, CardTitle } from '@/components/ui/card';



const LeaderboardPosition = (props: any) => {
    return (
        <Card>
            <CardContent className='flex justify-between items-end py-4 items-center'>
                {(props.image && <img className="rounded-full w-10 h-10" src={props.image} height={40} width={40} alt="pfp"/>)}
                <div className=''>{props.position}</div>
                <div className='w-40'>{props.name}</div>
                <div className='w-10'>{props.rating}</div>
            </CardContent>
        </Card >);
}

export {LeaderboardPosition};
