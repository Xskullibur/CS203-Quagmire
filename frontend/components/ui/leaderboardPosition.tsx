import * as React from 'react';

import { Card, CardHeader, CardDescription, CardContent, CardTitle } from '@/components/ui/card';



const LeaderboardPosition = (props: any) => {
    return (
        <Card>
            <CardContent className='flex'>
                <div>{props.name}</div>
                <div>{props.position}</div>
                <div>{props.rating}</div>
            </CardContent>
        </Card >);
}

export {LeaderboardPosition};
