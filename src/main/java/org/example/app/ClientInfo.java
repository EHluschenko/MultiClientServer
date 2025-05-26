package org.example.app;

import java.net.Socket;


public class ClientInfo {

    private final String name;

    private final Socket socket;

    public ClientInfo(String name, Socket socket) {
        this.name = name;
        this.socket = socket;

    }

    public String getName() {
        return name;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override

    public String toString() {
        return "Name: " + name + " сокет: " + socket + " LogTime: ";
    }
}
