package org.kyaru.pixiv.view.ui.panel;

import org.kyaru.pixiv.Main;
import org.kyaru.pixiv.view.ui.MainFrame;
import org.kyaru.pixiv.view.ui.Components;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.Path;

public class OutputPanel extends MainFrame.ExtendedPanel {

    private final JProgressBar progressBar = new JProgressBar(0, 100);
    private final JTextArea area = new JTextArea();
    private final MainFrame frame;
    private final JScrollPane scrollPane = new JScrollPane();

    public OutputPanel(MainFrame frame) {
        this.frame = frame;
        setPreferredSize(new Dimension(950, 550));
        setLayout(null);

        progressBar.setBounds(0, 0, MainFrame.WIDTH, 20);
        progressBar.setBackground(Color.DARK_GRAY);
        progressBar.setForeground(Components.PINK);
        progressBar.setBorderPainted(false);

        area.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 10));
        area.setBounds (0, 25, MainFrame.WIDTH, 20);
        area.setBackground(Components.grayWithBrightness(60));
        area.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 15));
        area.setForeground(Color.LIGHT_GRAY);
        area.setEditable(false);

        scrollPane.setBounds(0, 50, MainFrame.WIDTH, MainFrame.HEIGHT - 50);

        area.setLineWrap(true);
        add(scrollPane);
        add(progressBar);
        add(area);
    }

    /**
     * @param value 0 ~ 100
     */
    public void setProgressBar(int value) {
        this.progressBar.setValue(value);
    }

    public void setAreaText(String s) {
        this.area.setText(s);
    }

    public void setImage(byte[] bytes) {
        ImageIcon icon = new ImageIcon(bytes);
        this.scrollPane.setViewportView(new JLabel(icon));
    }

    public void setImage(BufferedImage image) {
        ImageIcon icon = new ImageIcon(image);
        this.scrollPane.setViewportView(new JLabel(icon));
    }


    @Override
    public void paint(Graphics g) {
        Graphics2D g2D = (Graphics2D) g;
        g2D.setColor(Components.grayWithBrightness(240));
        g2D.fillRect(0, 0, getWidth(), getHeight());
        super.paint(g);
    }

    @Override
    protected void reload() {

    }
}
