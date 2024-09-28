import { useEffect, useState } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from "sockjs-client";

export function useWebSocket() {
    const [client, setClient] = useState<Client | null>(null);
    const [connected, setConnected] = useState(false);

    useEffect(() => {
        const socket = new SockJS(`${process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL}/ws`);

        const stompClient = new Client({
            webSocketFactory: () => socket,
            onConnect: () => {
                setConnected(true)
            },
            debug: (msg) => console.log(msg),
            onDisconnect: () => setConnected(false),
        });

        stompClient.activate();
        setClient(stompClient);

        return () => {
            stompClient.deactivate();
        };
    }, []);

    return { client, connected };
}