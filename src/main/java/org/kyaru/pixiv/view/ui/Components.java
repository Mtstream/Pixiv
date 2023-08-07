package org.kyaru.pixiv.view.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class Components {
    public static final Color PINK = new Color(0xfcbad6);
    public static final Color GRAY = new Color(0x565656);
    public static final String YA_HEI = "Microsoft YaHei UI";

    public static class RectangleButton extends JButton{
        private final Color enteredColor;

        public RectangleButton(String display, Consumer<ActionEvent> action, Color color) {
            super(display);
            setBackground(color);
            this.enteredColor = color;
            this.setContentAreaFilled(false);
            this.setBorderPainted(false);
            this.setFocusPainted(false);
            this.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 20));
            this.addActionListener(e -> action.accept(e));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    setBackground(enteredColor);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2D = (Graphics2D) g;
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2D.setColor(getBackground());
            g2D.fillRect(0, 0, getWidth(), getHeight());
            super.paintComponent(g);
        }

    }

    public static class RectangleTextField extends JTextField{
        public RectangleTextField(int i){
            super(i);
            setBorder(new EmptyBorder(5,5,5,5));
            setBackground(new Color(0, 0, 0, 0));
            setOpaque(false);
            setSelectionColor(grayWithBrightness(50));
            setFont(new Font("sansserif", Font.PLAIN, 20));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2D = (Graphics2D) g;
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2D.setColor(Color.GRAY);
            g2D.fillRect(0, 0, getWidth(), getHeight());
            g2D.setColor(grayWithBrightness(240));
            g2D.fillRect(3, 3, getWidth()-6, getHeight()-6);
            super.paintComponent(g);
        }
    }

    public static Color grayWithBrightness(int brightness){
        return new Color(brightness, brightness, Math.min(brightness + 10, 255));
    }
}
