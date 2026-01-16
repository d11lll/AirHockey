package org.airhockey.client;

import org.airhockey.storage.JsonStorage;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class HistoryWindow extends JFrame {
    public HistoryWindow() {
        setTitle("История матчей");
        setSize(600, 400);
        setLocationRelativeTo(null);

        String[] columns = {"Дата", "Победитель", "Счет"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        List<Map<String, Object>> history = JsonStorage.loadHistory();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM HH:mm");

        for (Map<String, Object> match : history) {
            String winner = (String) match.get("winner");
            int left = ((Number) match.get("leftScore")).intValue();
            int right = ((Number) match.get("rightScore")).intValue();
            long time = ((Number) match.get("time")).longValue();

            model.addRow(new Object[]{
                    sdf.format(new java.util.Date(time)),
                    winner,
                    left + " : " + right
            });
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);

        JButton closeBtn = new JButton("Закрыть");
        closeBtn.addActionListener(e -> dispose());
        add(closeBtn, BorderLayout.SOUTH);
    }
}