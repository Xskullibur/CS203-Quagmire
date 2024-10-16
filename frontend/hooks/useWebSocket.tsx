import { useEffect, useState } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from "sockjs-client";

/**
 * Custom hook to manage a WebSocket connection using SockJS and STOMP.
 *
 * This hook initializes a WebSocket connection to the server specified by the
 * `NEXT_PUBLIC_SPRINGBOOT_API_URL` environment variable. It uses SockJS for the
 * WebSocket connection and STOMP for messaging.
 *
 * @returns An object containing:
 * - `client`: The STOMP client instance or `null` if not connected.
 * - `connected`: A boolean indicating whether the WebSocket connection is active.
 *
 * @example
 * const { client, connected } = useWebSocket();
 *
 * if (connected) {
 *   console.log('WebSocket is connected');
 * }
 *
 * @remarks
 * The WebSocket connection is established when the component mounts and is
 * cleaned up when the component unmounts.
 *
 * @see {@link https://stomp-js.github.io/stomp-websocket/codo/extra/docs-src/Usage.md|STOMP.js Usage}
 * @see {@link https://github.com/sockjs/sockjs-client|SockJS Client}
 */
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