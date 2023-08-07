package org.kyaru.pixiv.view.ui.panel;

import org.kyaru.pixiv.service.ServiceInterface;
import org.kyaru.pixiv.view.ui.MainFrame;
import org.kyaru.pixiv.view.ui.Components;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;

public class SettingPanel extends MainFrame.ExtendedPanel {
    int returnButtonSpace = 30;
    int numberOfField = 4;

    private final MainFrame frame;

    private final Components.RectangleTextField sourceLimitSettingField;
    private final Components.RectangleTextField norOutputFileSettingField;
    private final Components.RectangleTextField r18OutputFileSettingField;
    private final Components.RectangleTextField cookieSettingField;

    private Components.RectangleTextField createSettingField(String settingName, int sequence) {
        int eachBoundSpace = (MainFrame.HEIGHT) / (numberOfField + 1);
        int eachFieldHeight = eachBoundSpace / 2;

        JLabel settingTitle = new JLabel(settingName);
        settingTitle.setFont(new Font(Components.YA_HEI, Font.PLAIN, 20));
        settingTitle.setForeground(Color.WHITE);
        settingTitle.setBounds(5, eachBoundSpace * sequence, MainFrame.WIDTH - 10, eachFieldHeight);

        Components.RectangleTextField settingField = new Components.RectangleTextField(5);
        settingField.setFont(new Font(Components.YA_HEI, Font.PLAIN, 15));
        settingField.setForeground(Color.BLACK);
        settingField.setBounds(5, eachFieldHeight + eachBoundSpace * sequence, MainFrame.WIDTH - 10, eachFieldHeight);

        this.add(settingTitle);
        this.add(settingField);
        return settingField;
    }

    private void createButtons() {
        Components.RectangleButton applyButton = new Components.RectangleButton
                (
                        "Apply",
                        (e) -> {
                            ServiceInterface.ParameterSet parameterSet = new ServiceInterface.ParameterSet(
                                    Integer.parseInt(this.sourceLimitSettingField.getText()),
                                    this.norOutputFileSettingField.getText(),
                                    this.r18OutputFileSettingField.getText(),
                                    this.cookieSettingField.getText()
                            );
                            ServiceInterface.getServiceInterface().resetParameterSet(parameterSet);

                            this.frame.changePanel(this.frame.selectPanel);
                        },
                        Components.PINK
                );
        applyButton.setBounds (-5, MainFrame.HEIGHT - 2 * returnButtonSpace + 5, MainFrame.WIDTH / 2, returnButtonSpace);

        Components.RectangleButton cancelButton = new Components.RectangleButton(
                "Cancel",
                (e) -> this.frame.changePanel(this.frame.selectPanel),
                Components.PINK
        );
        cancelButton.setBounds (MainFrame.WIDTH / 2 + 5, MainFrame.HEIGHT - 2 * returnButtonSpace + 5, MainFrame.WIDTH / 2, returnButtonSpace);

        this.add(applyButton);
        this.add(cancelButton);
    }

    public SettingPanel(MainFrame frame) throws FileNotFoundException {
        this.frame = frame;
        setPreferredSize(new Dimension(950, 550));
        setLayout(null);
        this.sourceLimitSettingField = createSettingField("source limit", 0);
        this.norOutputFileSettingField = createSettingField("normal output file path", 1);
        this.r18OutputFileSettingField = createSettingField("R18 output file path", 2);
        this.cookieSettingField = createSettingField("cookie", 3);
        this.reload();
        createButtons();
    }

    public void reload() {
        ServiceInterface.ParameterSet settingDataSet = ServiceInterface.getServiceInterface().getUsingParameterSet();
        this.sourceLimitSettingField.setText(settingDataSet.sourceLimit() == -1 ? "" : String.valueOf(settingDataSet.sourceLimit()));
        this.norOutputFileSettingField.setText(settingDataSet.norFilePath());
        this.r18OutputFileSettingField.setText(settingDataSet.r18FilePath());
        this.cookieSettingField.setText(settingDataSet.cookie());
    }
}
