import React, { useState, useEffect, useCallback } from "react";
import { useWebSocket } from "@/hooks/useWebSocket";
import { Button } from "@/components/ui/button";
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";
import { PlayerProfile } from "@/types/player-profile";
import { useGeolocation } from "@/hooks/useGeolocation";

interface QueueManagementProps {
  playerId: string;
  onMatchFound: (
    matchFound: boolean,
    opponentName: string,
    meetingPoint: [number, number],
    opponentProfile: PlayerProfile
  ) => void;
}

interface MatchNotification {
  matchId: string;
  meetingLatitude: number;
  meetingLongitude: number;
  opponentName: string;
  opponentProfile: PlayerProfile;
}

/**
 * QueueManagement component handles the logic for a player to join and leave a matchmaking queue.
 * It uses WebSocket for real-time communication and Geolocation for player's location.
 *
 * @component
 * @param {QueueManagementProps} props - The props for the component.
 * @param {string} props.playerId - The ID of the player.
 * @param {function} props.onMatchFound - Callback function to handle when a match is found.
 *
 * @returns {JSX.Element} The rendered component.
 *
 * @example
 * <QueueManagement playerId="player123" onMatchFound={handleMatchFound} />
 *
 * @remarks
 * - The component subscribes to a WebSocket topic to receive match notifications.
 * - It uses geolocation to send the player's location when joining the queue.
 * - The component manages its own state for queue status, queue time, and match found status.
 *
 * @internal
 * This component is intended to be used within the matchmaking system of the application.
 */
const QueueManagement: React.FC<QueueManagementProps> = ({
  playerId,
  onMatchFound,
}) => {
  const [inQueue, setInQueue] = useState(false);
  const [queueTime, setQueueTime] = useState(0);
  const [matchFound, setMatchFound] = useState(false);
  const [opponentName, setOpponentName] = useState("");
  const { client, connected } = useWebSocket();
  const { location, error } = useGeolocation();

  useEffect(() => {
    if (client && connected) {
      const subscription = client.subscribe(
        `/topic/solo/match/${playerId}`,
        (message) => {
          try {
            const notification: MatchNotification = JSON.parse(message.body);
            console.log("Match found:", notification);
            setMatchFound(true);
            setInQueue(false);
            setOpponentName(notification.opponentName);
            onMatchFound(
              true,
              notification.opponentName,
              [notification.meetingLatitude, notification.meetingLongitude],
              notification.opponentProfile
            );
          } catch (error) {
            console.error("Error parsing match data:", error);
          }
        }
      );

      return () => {
        subscription.unsubscribe();
      };
    }
  }, [client, connected, playerId, onMatchFound]);

  useEffect(() => {
    let interval: NodeJS.Timeout;
    if (inQueue) {
      interval = setInterval(() => {
        setQueueTime((prevTime) => prevTime + 1);
      }, 1000);
    } else {
      setQueueTime(0);
    }
    return () => clearInterval(interval);
  }, [inQueue]);

  const leaveQueue = useCallback(() => {
    if (client && connected) {
      client.publish({
        destination: "/app/solo/dequeue",
        body: JSON.stringify({ playerId }),
      });
    }
    setInQueue(false);
  }, [client, connected, playerId]);

  useEffect(() => {
    // This effect will run when the component unmounts
    const inQueueRef = { current: inQueue };
    inQueueRef.current = inQueue;

    return () => {
      if (inQueueRef.current) {
        leaveQueue();
      }
    };
  }, [leaveQueue]);

  const joinQueue = useCallback(() => {
    if (client && connected && location) {
      console.log("Sending join queue request for player:", playerId);
      client.publish({
        destination: "/app/solo/queue",
        body: JSON.stringify({
          playerId: playerId,
          location: {
            latitude: location.latitude,
            longitude: location.longitude,
          },
        }),
      });
      setInQueue(true);
    } else {
      console.error(
        "Cannot join queue: client not connected or location not available"
      );
      // Show an error message to the user
      alert(
        "Unable to join queue. Please ensure location services are enabled and try again."
      );
    }
  }, [client, connected, location, playerId]);

  return (
    <div className="p-4 mx-auto max-w-md text-center">
      {error && (
        <Alert variant="destructive">
          <AlertTitle>Location Error</AlertTitle>
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      )}
      {matchFound ? (
        <Alert>
          <AlertTitle>Match Found!</AlertTitle>
          <AlertDescription>
            Prepare for your arm wrestling match against {opponentName}.
          </AlertDescription>
        </Alert>
      ) : inQueue ? (
        <div>
          <p>
            In Queue: {Math.floor(queueTime / 60)}:
            {queueTime % 60 < 10 ? "0" : ""}
            {queueTime % 60}
          </p>
          <Button onClick={leaveQueue}>Leave Queue</Button>
        </div>
      ) : (
        <Button
          variant="outline"
          onClick={joinQueue}
          className="hover:text-muted"
          disabled={!connected || !location}
        >
          Join Queue
        </Button>
      )}
    </div>
  );
};

export default QueueManagement;
