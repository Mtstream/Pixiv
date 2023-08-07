package org.kyaru.pixiv;


import org.kyaru.pixiv.service.ServiceInterface;
import org.kyaru.pixiv.view.ViewInterface;
import org.kyaru.pixiv.view.ui.MainFrame;

public class Main {
    public static void main(String[] args) {
//        ViewInterface.getViewInterface();
        ServiceInterface.getServiceInterface().downloadByAuthor("10678011");
    }
}