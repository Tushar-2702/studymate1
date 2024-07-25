package com.studymate;

import com.studymate.controller.Logincontroller;
import javafx.application.Application;

public class Main {
    public static void main(String[] args) {
        Logincontroller loginController = new Logincontroller();
        //page2 page2App = new page2();
        
        Application.launch(Logincontroller.class, args);
    }
}