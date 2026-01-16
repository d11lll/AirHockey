package org.airhockey.client;

import com.google.gson.Gson;
import org.airhockey.protocol.*;

import javax.swing.*;
import java.net.*;

public class NetworkClient {
    private final Gson gson = new Gson();
    private DatagramSocket socket;
    private InetAddress serverAddress;
    private int serverPort;
    private volatile GameUpdate lastUpdate = null;
    private String playerSide = "";

    public NetworkClient(String ip, int port) throws Exception {
        this.serverPort = port;
        socket = new DatagramSocket();
        serverAddress = InetAddress.getByName(ip);

        sendConnect();
        new Thread(this::listen).start();
    }

    private void sendConnect() {
        Message msg = new Message(MessageType.CONNECT, "", "");
        send(msg);
    }

    public void sendMove(String direction) {
        if (playerSide.isEmpty()) return;
        Message msg = new Message(MessageType.MOVE, playerSide, direction);
        send(msg);
    }

    private void send(Message msg) {
        try {
            byte[] data = gson.toJson(msg).getBytes();
            DatagramPacket packet = new DatagramPacket(
                    data, data.length, serverAddress, serverPort);
            socket.send(packet);
        } catch (Exception e) {
            System.err.println("Ошибка отправки: " + e.getMessage());
        }
    }

    private void listen() {
        try {
            while (true) {
                byte[] buf = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                String json = new String(packet.getData(), 0, packet.getLength());
                Message msg = gson.fromJson(json, Message.class);

                switch (msg.type) {
                    case ACCEPT -> {
                        if ("FULL".equals(msg.sender)) {
                            JOptionPane.showMessageDialog(null,
                                    "Сервер заполнен! Максимум 2 игрока.");
                            System.exit(0);
                        }
                        playerSide = msg.sender;
                        System.out.println("Вы играете за: " + playerSide);
                    }
                    case UPDATE -> {
                        lastUpdate = gson.fromJson(msg.payload, GameUpdate.class);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка соединения: " + e.getMessage());
        }
    }

    public GameUpdate getLastUpdate() {
        return lastUpdate;
    }

    public String getPlayerSide() {
        return playerSide;
    }
}