package org.example.app.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class ClientHandler implements Runnable {
    private final ClientInfo clientInfo;
    private final Map<String, ClientInfo> clients;
    private final Map<String, PrintWriter> clientWriters;

    public ClientHandler(ClientInfo clientInfo, Map<String, ClientInfo> clients, Map<String, PrintWriter> clientWriters) {
        this.clientInfo = clientInfo;
        this.clients = clients;
        this.clientWriters = clientWriters;
    }

    @Override
    public void run() {
        try (
                Socket socket = clientInfo.getSocket();
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            clientWriters.put(clientInfo.getName(), out);
            out.println("[SERVER] Ви підключені як " + clientInfo.getName() + " Time: "+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) );

            String line;
            while ((line = in.readLine()) != null) {
                if ("exit".equalsIgnoreCase(line.trim())) {
                    out.println("[SERVER] Вихід із чату...");
                    break;
                } else if ("/list".equalsIgnoreCase(line.trim())) {
                    out.println("[SERVER] Активні клієнти:");
                    for (ClientInfo ci : clients.values()) {
                        out.println(" - " + ci);
                    }
                } else {
                    System.out.println("[" + clientInfo.getName() + "]: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("[SERVER] Клієнт " + clientInfo.getName() + " відключився з помилкою: " + e.getMessage());
        } finally {
            clients.remove(clientInfo.getName());
            clientWriters.remove(clientInfo.getName());
            System.out.println("[SERVER] " + clientInfo + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " відключився");
        }
    }
}
