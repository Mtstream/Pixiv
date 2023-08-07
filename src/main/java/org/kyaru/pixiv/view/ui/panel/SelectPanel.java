package org.kyaru.pixiv.view.ui.panel;

import org.kyaru.pixiv.service.ServiceInterface;
import org.kyaru.pixiv.view.ui.MainFrame;
import org.kyaru.pixiv.view.ui.Components;

import java.awt.*;
import java.util.function.Consumer;

public class SelectPanel extends MainFrame.ExtendedPanel {

    private final MainFrame frame;

    private Components.RectangleButton createDownloadButton(String name, Consumer<ServiceInterface> action, int sequence) {
        int eachButtonSpace = (MainFrame.HEIGHT - 90) / 5;
        Components.RectangleButton button = new Components.RectangleButton(
                name,
                e->{
                    action.accept(ServiceInterface.getServiceInterface());
                    this.frame.changePanel(this.frame.outputPanel);
                },
                Components.PINK
                );
        button.setBounds (5, 60 + eachButtonSpace * sequence, MainFrame.WIDTH - 10, eachButtonSpace/2);
        add(button);
        return button;
    }

    private Components.RectangleButton createSettingButton() {
        Components.RectangleButton button = new Components.RectangleButton(
                "Setting",
                e -> this.frame.changePanel(this.frame.settingPanel),
                Components.GRAY
        );
        button.setBounds (5, MainFrame.HEIGHT - 70, MainFrame.WIDTH - 10, 30);
        add(button);
        return button;
    }
    private Components.RectangleTextField createParameterField() {
        Components.RectangleTextField field = new Components.RectangleTextField(5);
        field.setFont(new Font(Components.YA_HEI, Font.PLAIN, 30));
        field.setForeground(Color.DARK_GRAY);
        field.setBounds(0, 0, MainFrame.WIDTH, 45);
        add(field);
        return field;
    }

    public SelectPanel(MainFrame frame) {
        this.frame = frame;
        setPreferredSize(new Dimension(950, 550));
        setLayout(null);

        Components.RectangleTextField parameterField = createParameterField();
        this.createDownloadButton("Download By Author", (e) -> e.downloadByAuthor(parameterField.getText()), 0);

        this.createDownloadButton("Download By Artwork", (e) -> e.downloadByArtwork(parameterField.getText()), 1);

        this.createDownloadButton("Download By Following", ServiceInterface::downloadByFollowing, 2);

        this.createDownloadButton("Download By Ranking", ServiceInterface::downloadByRanking, 3);

        createSettingButton();
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
