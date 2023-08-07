package org.kyaru.pixiv.view;

import org.kyaru.pixiv.view.ui.MainFrame;

import java.awt.image.BufferedImage;

public class ViewInterface {
    public static ViewInterface getViewInterface() {
        if (ViewInterface.viewInterface == null) {
            ViewInterface.viewInterface = new ViewInterface();
        }
        return ViewInterface.viewInterface;
    }

    private static ViewInterface viewInterface = null;

    private ViewInterface(){
    }

    private final MainFrame mainFrame =  new MainFrame();

    public void display(String message) {
        this.mainFrame.outputPanel.setAreaText(message);
    }

    public void updateProcess(int process) {
        this.mainFrame.outputPanel.setProgressBar(process);
    }

    public void addDisplayImage(BufferedImage image) {
        this.mainFrame.outputPanel.setImage(image);
    }
}
