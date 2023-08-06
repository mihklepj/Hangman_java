package controllers;

import controllers.listeners.*;
import models.Model;
import views.View;

public class Controller {
    public Controller(Model model, View view) {
        view.registerButtonScores(new ButtonScores(model, view));
        view.registerButtonNew(new ButtonNew(model, view));
        view.registerButtonCancel(new ButtonCancel(model, view));
        view.registerButtonSend(new ButtonSend(view, model));
        view.registerComboBoxChange(new CategoryItemChange(model));
        view.registerTxtChar(new TxtCharNew(model, view)); // Use TxtCharNew instead of TxtChar
    }
}
