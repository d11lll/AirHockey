package org.airhockey.model;

public class Puck {
    public float x, y;
    public float vx = 150;
    public float vy = 60;

    public Puck(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void update(float dt) {
        x += vx * dt;
        y += vy * dt;
    }
}