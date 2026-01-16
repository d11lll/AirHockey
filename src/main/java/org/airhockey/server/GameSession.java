package org.airhockey.server;

import org.airhockey.model.Paddle;
import org.airhockey.model.Puck;
import org.airhockey.protocol.GameUpdate;
import org.airhockey.storage.JsonStorage;

import java.net.InetSocketAddress;
import java.util.*;

public class GameSession {
    private final int width = 800;
    private final int height = 600;

    private final Paddle paddleLeft;
    private final Paddle paddleRight;
    private final Puck puck;

    private int scoreLeft = 0;
    private int scoreRight = 0;

    private final Map<String, InetSocketAddress> players = new HashMap<>();
    private boolean gameStarted = false;
    private long gameStartTime = 0;

    public GameSession() {
        paddleLeft = new Paddle(30, height / 2f);
        paddleRight = new Paddle(width - 30, height / 2f);
        puck = new Puck(width / 2f, height / 2f);
    }

    public String addPlayer(InetSocketAddress addr) {
        if (!players.containsKey("LEFT")) {
            players.put("LEFT", addr);
            System.out.println("Левый игрок подключился. Ждем второго...");
            checkIfGameCanStart();
            return "LEFT";
        }
        if (!players.containsKey("RIGHT")) {
            players.put("RIGHT", addr);
            System.out.println("Правый игрок подключился!");
            checkIfGameCanStart();
            return "RIGHT";
        }
        return "FULL";
    }

    public Map<String, InetSocketAddress> getPlayers() {
        return players;
    }

    public Collection<InetSocketAddress> getPlayerAddresses() {
        return players.values();
    }

    public int getConnectedPlayers() {
        return players.size();
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    private void checkIfGameCanStart() {
        if (players.size() == 2 && !gameStarted) {
            gameStarted = true;
            gameStartTime = System.currentTimeMillis();
            scoreLeft = 0;
            scoreRight = 0;
            resetPuck();
            System.out.println("Игра началась! Оба игрока подключены.");
        }
    }


    public void movePlayer(String player, String direction) {
        if (!gameStarted) return;

        float speed = 8f;

        if (player.equals("LEFT")) {
            if (direction.equals("UP")) paddleLeft.y -= speed;
            if (direction.equals("DOWN")) paddleLeft.y += speed;
            paddleLeft.clamp(0, height);
        }

        if (player.equals("RIGHT")) {
            if (direction.equals("UP")) paddleRight.y -= speed;
            if (direction.equals("DOWN")) paddleRight.y += speed;
            paddleRight.clamp(0, height);
        }
    }


    public void update(float dt) {
        if (!gameStarted) return;

        puck.update(dt);

        if (puck.y < 0 || puck.y > height) {
            puck.vy = -puck.vy;
        }

        if (puck.x < 0) {
            scoreRight++;
            checkWin();
            resetPuck();
        }

        if (puck.x > width) {
            scoreLeft++;
            checkWin();
            resetPuck();
        }

        checkPaddleCollision();
    }


    private void checkWin() {
        if (scoreLeft >= 7 || scoreRight >= 7) {
            String winner = (scoreLeft > scoreRight) ? "LEFT" : "RIGHT";
            System.out.println("Матч окончен! Победитель: " + winner);

            JsonStorage.saveMatch(winner, scoreLeft, scoreRight);

            scoreLeft = 0;
            scoreRight = 0;

            resetPuck();
        }
    }

    private void resetPuck() {
        puck.x = width / 2f;
        puck.y = height / 2f;

        puck.vx = (Math.random() > 0.5 ? 1 : -1) * 150;
        puck.vy = (float) ((Math.random() - 0.5) * 200);
    }


    private void checkPaddleCollision() {
        if (Math.abs(puck.x - paddleLeft.x) < 20 &&
                Math.abs(puck.y - paddleLeft.y) < Paddle.HEIGHT / 2) {
            puck.vx = Math.abs(puck.vx);
        }

        if (Math.abs(puck.x - paddleRight.x) < 20 &&
                Math.abs(puck.y - paddleRight.y) < Paddle.HEIGHT / 2) {
            puck.vx = -Math.abs(puck.vx);
        }
    }

    public GameUpdate toUpdate() {
        return new GameUpdate(
                puck.x, puck.y,
                paddleLeft.x, paddleLeft.y,
                paddleRight.x, paddleRight.y,
                scoreLeft, scoreRight,
                gameStarted,
                players.size()
        );
    }
}