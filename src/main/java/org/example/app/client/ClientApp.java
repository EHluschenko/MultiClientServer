package org.example.app.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientApp {

    public static void main(String[] args) {
        String host = "localhost";
        int port = 8083;

        try (
                Socket socket = new Socket(host, port);
                BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            Thread listener = new Thread(() -> {
                String response;
                try {
                    while ((response = in.readLine()) != null) {
                        System.out.println(response);
                    }
                } catch (IOException e) {
                    System.out.println("[CLIENT] З’єднання з сервером втрачено.");
                }
            });
            listener.start();

            String input;
            while ((input = userInput.readLine()) != null) {
                out.println(input);
                if ("exit".equalsIgnoreCase(input.trim())) {
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("[CLIENT] Помилка клієнта: " + e.getMessage());
        }
    }
}
