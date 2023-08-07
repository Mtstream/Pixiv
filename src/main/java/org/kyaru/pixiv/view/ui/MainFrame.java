package org.kyaru.pixiv.view.ui;

import org.kyaru.pixiv.view.ui.Components;
import org.kyaru.pixiv.view.ui.panel.OutputPanel;
import org.kyaru.pixiv.view.ui.panel.SelectPanel;
import org.kyaru.pixiv.view.ui.panel.SettingPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;

public class MainFrame extends JFrame {
    public final static int WIDTH = 350;
    public final static int HEIGHT = 300;

    public final SettingPanel settingPanel;
    public final SelectPanel selectPanel;
    public final OutputPanel outputPanel;

    public static final String FRAME_TITLE = "pixiv crawler";

    public MainFrame() {
        this.outputPanel = new OutputPanel(this);
        this.selectPanel = new SelectPanel(this);
        try {
            this.settingPanel = new SettingPanel(this);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        setSize(WIDTH, HEIGHT);
        setResizable(false);
        setTitle(FRAME_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(this.selectPanel);
        getContentPane().setBackground(Components.grayWithBrightness(40));
        setVisible(true);
    }

    public void display(String message) {
        this.outputPanel.setAreaText(message);
    }

    public void updateProcess(int process) {
        this.outputPanel.setProgressBar(process);
    }

    public void addDisplayImage(BufferedImage image) {
        this.outputPanel.setImage(image);
    }

    public void changePanel(ExtendedPanel panel) {
        panel.reload();
        this.setContentPane(panel);
        repaint();
        setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Components.grayWithBrightness(40));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        repaint();
    }

    public static abstract class ExtendedPanel extends JPanel {
        protected abstract void reload();
    }
}
