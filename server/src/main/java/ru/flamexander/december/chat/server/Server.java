package ru.flamexander.december.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private int port;
    private Map<String, ClientHandler> clients;
    private UserService userService;

    public Server(int port) {
        this.port = port;
        this.clients = new HashMap<>();
    }

    public UserService getUserService() {
        return userService;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.printf("Сервер запущен на порту %d. Ожидание подключения клиентов\n", port);
            userService = new InMemoryUserService();
            System.out.println("Запущен сервис для работы с пользователями");
            while (true) {
                Socket socket = serverSocket.accept();
                try {
                    new ClientHandler(this, socket);
                } catch (IOException e) {
                    System.out.println("Не удалось подключить клиента");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void broadcastMessage(String message) {
        for (ClientHandler clientHandler : clients.values()) {
            clientHandler.sendMessage(message);
        }
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        clients.put(clientHandler.getUsername(), clientHandler);
        System.out.println("Подключился новый клиент " + clientHandler.getUsername());
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler.getUsername());
        System.out.println("Отключился клиент " + clientHandler.getUsername());
    }

    public synchronized void sendPrivateMessage(ClientHandler sender, String receiverUsername, String message) {
        for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
            if (entry.getKey().equals(receiverUsername)) {
                sender.sendMessage("wisp:" + receiverUsername + ": " + message);
                entry.getValue().sendMessage("wisp:" + sender.getUsername() + ": " + message);
            }
        }
    }

    public synchronized boolean isUserBusy(String username) {
        for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
            if (entry.getKey().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public void kickUser(ClientHandler clientHandler, String userRole, String userName) {
        if (userRole.equals("admin")) {
            if (clients.containsKey(userName)) {
                clients.get(userName).disconnect();
            }
            else {
                clientHandler.sendMessage("такого пользователя не существует");
            }
        } else {
            clientHandler.sendMessage("недостаточно прав");
        }
    }
}