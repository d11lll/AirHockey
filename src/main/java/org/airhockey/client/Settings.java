package org.airhockey.client;

import java.awt.Color;

public class Settings {
    private int port = 7777;
    private Color puckColor = Color.RED;
    private Color paddleColor = Color.WHITE;
    private Color backgroundColor = new Color(30, 30, 70);
    private Color lineColor = Color.WHITE;
    private boolean showFPS = true;
    private float gameSpeed = 1.0f;
    private String playerName = "Игрок";

    public Settings() {}

    public int getPort() { return port; }
    public Color getPuckColor() { return puckColor; }
    public Color getPaddleColor() { return paddleColor; }
    public Color getBackgroundColor() { return backgroundColor; }
    public Color getLineColor() { return lineColor; }
    public boolean isShowFPS() { return showFPS; }
    public float getGameSpeed() { return gameSpeed; }
    public String getPlayerName() { return playerName; }

    public void setPort(int port) { this.port = port; }
    public void setPuckColor(Color puckColor) { this.puckColor = puckColor; }
    public void setPaddleColor(Color paddleColor) { this.paddleColor = paddleColor; }
    public void setBackgroundColor(Color backgroundColor) { this.backgroundColor = backgroundColor; }
    public void setLineColor(Color lineColor) { this.lineColor = lineColor; }
    public void setShowFPS(boolean showFPS) { this.showFPS = showFPS; }
    public void setGameSpeed(float gameSpeed) { this.gameSpeed = gameSpeed; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    @Override
    public String toString() {
        return "Settings{" +
                "port=" + port +
                ", playerName='" + playerName + '\'' +
                ", gameSpeed=" + gameSpeed +
                '}';
    }
}