package org.example.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

                ClientInfo info = new ClientInfo(clientName, clientSocket);
                clients.put(clientName, info);

                System.out.println("[SERVER] " +  info + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " успішно підключився");

                new Thread(() -> handleClient(info)).start();
            }
        } catch (IOException e) {
            System.out.println("[SERVER] Помилка: " + e.getMessage());
        }
    }

    private static void handleClient(ClientInfo clientInfo) {
        try (
                Socket socket = clientInfo.getSocket();
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            clientWriters.put(clientInfo.getName(), out);
            out.println("[SERVER] Ви підключені як " + clientInfo.getName());

            String line;
            while ((line = in.readLine()) != null) {
                if (line.trim().equalsIgnoreCase("exit")) {
                    out.println("[SERVER] Вихід із чату...");
                    break;
                } else if (line.trim().equalsIgnoreCase("/list")) {
                    out.println("[SERVER] Активні клієнти:");
                    for (ClientInfo ci : clients.values()) {
                        out.println(" - " + ci);
                    }
                } else {
                    System.out.println("[" + clientInfo.getName() + "] " + line);
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
