package org.airhockey.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SettingsWindow extends JFrame {
    private SettingsManager settingsManager;
    private Settings currentSettings;

    private JTextField portField;
    private JTextField nameField;
    private JSlider speedSlider;
    private JCheckBox fpsCheckBox;
    private JButton puckColorBtn;
    private JButton paddleColorBtn;
    private JButton bgColorBtn;

    public SettingsWindow(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
        this.currentSettings = settingsManager.loadSettings();

        initializeUI();
    }

    private void initializeUI() {
        setTitle("Настройки игры");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(50, 50, 90));
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(9, 2, 10, 15));
        mainPanel.setBackground(new Color(50, 50, 90));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        mainPanel.add(createLabel("Порт сервера:"));
        portField = new JTextField(String.valueOf(currentSettings.getPort()));
        mainPanel.add(portField);

        mainPanel.add(createLabel("Имя игрока:"));
        nameField = new JTextField(currentSettings.getPlayerName());
        mainPanel.add(nameField);

        mainPanel.add(createLabel("Скорость игры:"));
        JPanel speedPanel = new JPanel(new BorderLayout());
        speedSlider = new JSlider(50, 200, (int)(currentSettings.getGameSpeed() * 100));
        speedSlider.setMajorTickSpacing(50);
        speedSlider.setMinorTickSpacing(10);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        speedPanel.add(speedSlider, BorderLayout.CENTER);
        mainPanel.add(speedPanel);

        mainPanel.add(createLabel("Показать FPS:"));
        fpsCheckBox = new JCheckBox();
        fpsCheckBox.setSelected(currentSettings.isShowFPS());
        fpsCheckBox.setBackground(new Color(50, 50, 90));
        fpsCheckBox.setForeground(Color.WHITE);
        mainPanel.add(fpsCheckBox);

        mainPanel.add(createLabel("Цвет шайбы:"));
        puckColorBtn = createColorButton(currentSettings.getPuckColor());
        puckColorBtn.addActionListener(e -> chooseColor(puckColorBtn));
        mainPanel.add(puckColorBtn);

        mainPanel.add(createLabel("Цвет ракетки:"));
        paddleColorBtn = createColorButton(currentSettings.getPaddleColor());
        paddleColorBtn.addActionListener(e -> chooseColor(paddleColorBtn));
        mainPanel.add(paddleColorBtn);

        mainPanel.add(createLabel("Цвет фона:"));
        bgColorBtn = createColorButton(currentSettings.getBackgroundColor());
        bgColorBtn.addActionListener(e -> chooseColor(bgColorBtn));
        mainPanel.add(bgColorBtn);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(50, 50, 90));

        JButton saveBtn = createStyledButton("Сохранить");
        saveBtn.addActionListener(e -> saveSettings());

        JButton cancelBtn = createStyledButton("Отмена");
        cancelBtn.addActionListener(e -> dispose());

        JButton defaultBtn = createStyledButton("По умолчанию");
        defaultBtn.addActionListener(e -> resetToDefaults());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        buttonPanel.add(defaultBtn);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        return label;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton createColorButton(Color color) {
        JButton button = new JButton("     ");
        button.setBackground(color);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        button.setPreferredSize(new Dimension(80, 30));
        return button;
    }

    private void chooseColor(JButton colorBtn) {
        Color initialColor = colorBtn.getBackground();
        Color chosenColor = JColorChooser.showDialog(this, "Выберите цвет", initialColor);
        if (chosenColor != null) {
            colorBtn.setBackground(chosenColor);
        }
    }

    private void saveSettings() {
        try {
            currentSettings.setPort(Integer.parseInt(portField.getText()));
            currentSettings.setPlayerName(nameField.getText());
            currentSettings.setGameSpeed(speedSlider.getValue() / 100f);
            currentSettings.setShowFPS(fpsCheckBox.isSelected());
            currentSettings.setPuckColor(puckColorBtn.getBackground());
            currentSettings.setPaddleColor(paddleColorBtn.getBackground());
            currentSettings.setBackgroundColor(bgColorBtn.getBackground());

            settingsManager.saveSettings(currentSettings);
            JOptionPane.showMessageDialog(this,
                    "Настройки сохранены!\nДля применения некоторых настроек требуется перезапуск.",
                    "Успех", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Некорректный номер порта!",
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetToDefaults() {
        Settings defaults = new Settings();
        portField.setText(String.valueOf(defaults.getPort()));
        nameField.setText(defaults.getPlayerName());
        speedSlider.setValue((int)(defaults.getGameSpeed() * 100));
        fpsCheckBox.setSelected(defaults.isShowFPS());
        puckColorBtn.setBackground(defaults.getPuckColor());
        paddleColorBtn.setBackground(defaults.getPaddleColor());
        bgColorBtn.setBackground(defaults.getBackgroundColor());
    }
}