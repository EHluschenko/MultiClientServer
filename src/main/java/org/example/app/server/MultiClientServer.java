package org.example.app.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class MultiClientServer {
    private static int clientCounter = 0;
    private static final Map<String, ClientInfo> clients = new ConcurrentHashMap<>();
    private static final Map<String, PrintWriter> clientWriters = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        int port = 8083;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("[SERVER] Сервер запущено на порту " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                clientCounter++;
                String clientName = "client-" + clientCounter;

                ClientInfo clientInfo = new ClientInfo(clientName, clientSocket);
                clients.put(clientName, clientInfo);

                System.out.println("[SERVER] " + clientInfo + "Time: "+LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))+ " підключився");

                Thread thread = new Thread(new ClientHandler(clientInfo, clients, clientWriters));
                thread.start();
            }
        } catch (IOException e) {
            System.err.println("[SERVER] Помилка сервера: " + e.getMessage());
        }
    }
}
