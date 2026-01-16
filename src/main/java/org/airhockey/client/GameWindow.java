package org.airhockey.client;

import javax.swing.*;
import java.awt.event.*;

public class GameWindow extends JFrame {
    public GameWindow(NetworkClient net) {
        setTitle("Air Hockey");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        GamePanel panel = new GamePanel(net);
        add(panel);

        setFocusable(true);
        requestFocusInWindow();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP -> net.sendMove("UP");
                    case KeyEvent.VK_DOWN -> net.sendMove("DOWN");
                    case KeyEvent.VK_ESCAPE -> System.exit(0);
                }
            }
        });

        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Игра");
        JMenuItem historyItem = new JMenuItem("История матчей");
        JMenuItem exitItem = new JMenuItem("Выход");

        historyItem.addActionListener(e -> new HistoryWindow().setVisible(true));
        exitItem.addActionListener(e -> System.exit(0));

        gameMenu.add(historyItem);
        gameMenu.addSeparator();
        gameMenu.add(exitItem);
        menuBar.add(gameMenu);
        setJMenuBar(menuBar);
    }
}