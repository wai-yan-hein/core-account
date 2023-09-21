package com;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RecyclerViewExample extends JFrame {

    private List<String> itemList;
    private JPanel itemPanel;

    public RecyclerViewExample() {
        setTitle("RecyclerView Example");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        itemList = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            itemList.add("Item " + i);
        }

        itemPanel = new JPanel();
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));

        for (String item : itemList) {
            JLabel label = new JLabel(item);
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            itemPanel.add(label);
        }

        JScrollPane scrollPane = new JScrollPane(itemPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        getContentPane().add(scrollPane);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RecyclerViewExample::new);
    }
}
