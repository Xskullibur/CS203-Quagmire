// AutoAdvanceMatch.tsx
'use client'
import React from "react";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";

type AutoAdvanceMatchProps = {
  player: { userId: string; id: string };
};

const AutoAdvanceMatch: React.FC<AutoAdvanceMatchProps> = ({ player }) => {
  return (
    <Card className="mx-auto">
      <CardHeader>
        <CardTitle>Auto Advancement</CardTitle>
      </CardHeader>
      <CardContent>
        <div className="text-center">
          <p className="text-lg font-semibold text-green-600">
            {player.userId} advances automatically.
          </p>
        </div>
      </CardContent>
    </Card>
  );
};

export default AutoAdvanceMatch;
