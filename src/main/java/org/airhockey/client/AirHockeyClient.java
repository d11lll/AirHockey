package org.airhockey.client;

import org.airhockey.server.AirHockeyServer;
import javax.swing.*;
import java.net.InetAddress;

public class AirHockeyClient {
    public static void main(String[] args) throws Exception {
        try {
            String localIP = InetAddress.getLocalHost().getHostAddress();

            String ip = JOptionPane.showInputDialog(null,
                    "Ваш IP адрес: " + localIP +
                            "\n\nВведите IP сервера:\n(оставьте пустым чтобы создать сервер)",
                    "Air Hockey - Подключение",
                    JOptionPane.QUESTION_MESSAGE);

            if (ip == null) return;

            if (ip.isBlank()) {
                String serverIP = InetAddress.getLocalHost().getHostAddress();
                JOptionPane.showMessageDialog(null,
                        "Сервер создан!\n" +
                                "IP: " + serverIP + "\n" +
                                "Порт: 8888\n\n" +
                                "Сообщите этот IP другим игрокам",
                        "Сервер запущен",
                        JOptionPane.INFORMATION_MESSAGE);

                new Thread(() -> {
                    try {
                        new AirHockeyServer(8888).start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
                ip = "localhost";
            }

            NetworkClient net = new NetworkClient(ip, 8888);

            SwingUtilities.invokeLater(() -> {
                new GameWindow(net).setVisible(true);
            });

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Ошибка: " + e.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}