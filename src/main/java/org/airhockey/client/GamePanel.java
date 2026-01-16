package org.airhockey.client;

import org.airhockey.protocol.GameUpdate;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private final NetworkClient net;

    public GamePanel(NetworkClient net) {
        this.net = net;
        setBackground(Color.BLACK);

        Timer timer = new Timer(16, e -> repaint());
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        GameUpdate u = net.getLastUpdate();
        if (u == null) {
            drawWaitingScreen(g, "Подключение к серверу...");
            return;
        }

        if (!u.gameStarted) {
            drawWaitingScreen(g,
                    "Ожидание второго игрока... (" +
                            u.connectedPlayers + "/2 подключено)");
            return;
        }

        float scaleX = getWidth() / 800f;
        float scaleY = getHeight() / 600f;

        drawField(g, scaleX, scaleY);

        g.setColor(Color.WHITE);
        g.fillRect((int)(u.leftX * scaleX) - 10,
                (int)(u.leftY * scaleY) - 40,
                20, 80);
        g.fillRect((int)(u.rightX * scaleX) - 10,
                (int)(u.rightY * scaleY) - 40,
                20, 80);

        g.setColor(Color.RED);
        g.fillOval((int)(u.puckX * scaleX) - 10,
                (int)(u.puckY * scaleY) - 10,
                20, 20);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        String score = u.scoreLeft + " : " + u.scoreRight;
        g.drawString(score, getWidth()/2 - 40, 50);

        g.setFont(new Font("Arial", Font.PLAIN, 16));
        String side = net.getPlayerSide();
        if (!side.isEmpty()) {
            g.drawString("Вы: " + side + " игрок", 10, getHeight() - 30);
            g.drawString("Управление: Стрелки ВВЕРХ/ВНИЗ", 10, getHeight() - 10);
        }
    }

    private void drawField(Graphics g, float scaleX, float scaleY) {
        int centerLineWidth = (int)(4 * scaleX);
        int dashHeight = (int)(10 * scaleY);
        int dashSpacing = (int)(20 * scaleY);

        for (int y = 0; y < getHeight(); y += dashSpacing) {
            g.fillRect(getWidth()/2 - centerLineWidth/2, y, centerLineWidth, dashHeight);
        }
    }

    private void drawWaitingScreen(Graphics g, String message) {
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));

        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(message);
        int x = (getWidth() - textWidth) / 2;
        int y = getHeight() / 2;

        g.drawString(message, x, y);

        long time = System.currentTimeMillis();
        int dotCount = (int)((time / 500) % 4);
        String dots = ".".repeat(dotCount);
        g.drawString(dots, x + textWidth + 5, y);

        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("Игра начнется автоматически, когда оба игрока будут подключены",
                getWidth()/2 - 200, y + 40);
    }
}