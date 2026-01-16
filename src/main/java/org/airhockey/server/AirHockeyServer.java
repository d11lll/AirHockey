package org.airhockey.server;

import org.airhockey.protocol.GameUpdate;
import org.airhockey.protocol.Message;
import org.airhockey.protocol.MessageType;
import com.google.gson.Gson;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class AirHockeyServer {
    private final DatagramSocket socket;
    private final GameSession session;
    private final Gson gson = new Gson();

    public AirHockeyServer(int port) throws Exception {
        socket = new DatagramSocket(port);
        session = new GameSession();
        System.out.println("Сервер запущен на порту " + port);
    }

    public void start() {
        Thread t = new Thread(this::listen);
        t.start();

        new Thread(() -> {
            long prev = System.nanoTime();
            while (true) {
                long now = System.nanoTime();
                float dt = (now - prev) / 1_000_000_000f;
                prev = now;

                session.update(dt);

                GameUpdate u = session.toUpdate();
                Message m = new Message(MessageType.UPDATE, "", gson.toJson(u));
                broadcast(m);

                try {
                    Thread.sleep(16);
                } catch (Exception ignored) {}
            }
        }).start();
    }

    private void listen() {
        try {
            while (true) {
                byte[] buf = new byte[2048];
                DatagramPacket p = new DatagramPacket(buf, buf.length);
                socket.receive(p);

                String json = new String(p.getData(), 0, p.getLength());
                Message msg = gson.fromJson(json, Message.class);

                InetSocketAddress addr = new InetSocketAddress(p.getAddress(), p.getPort());
                handle(msg, addr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handle(Message msg, InetSocketAddress addr) {
        switch (msg.type) {
            case CONNECT -> {
                String side = session.addPlayer(addr);
                System.out.println("CONNECT " + addr + " -> " + side);

                if ("FULL".equals(side)) {
                    send(addr, new Message(MessageType.ACCEPT, "FULL", ""));
                } else {
                    send(addr, new Message(MessageType.ACCEPT, side, ""));
                    if (session.getConnectedPlayers() == 2) {
                        System.out.println("Оба игрока подключены, игра начинается!");
                    }
                }
            }

            case MOVE -> {
                if (session.isGameStarted()) {  // Проверяем, началась ли игра
                    session.movePlayer(msg.sender, msg.payload);
                }
            }
        }
    }

    private void send(InetSocketAddress addr, Message msg) {
        try {
            byte[] data = gson.toJson(msg).getBytes();
            DatagramPacket p = new DatagramPacket(
                    data, data.length,
                    addr.getAddress(), addr.getPort());

            socket.send(p);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void broadcast(Message msg) {
        byte[] data = gson.toJson(msg).getBytes();

        for (InetSocketAddress addr : session.getPlayerAddresses()) {
            try {
                DatagramPacket p = new DatagramPacket(
                        data, data.length,
                        addr.getAddress(), addr.getPort());

                socket.send(p);
            } catch (Exception ignored) {}
        }
    }

    public static void main(String[] args) throws Exception {
        AirHockeyServer server = new AirHockeyServer(8888);
        server.start();
    }
}