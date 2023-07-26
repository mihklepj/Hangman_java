package controllers;

import controllers.listeners.ButtonCancel;
import controllers.listeners.ButtonNew;
import controllers.listeners.ButtonScores;
import controllers.listeners.ButtonSend;
import controllers.listeners.CategoryItemChange;
import models.Model;
import views.View;
import views.panels.GameBoard;

public class Controller {
    // private final Model model;
    // private final View view;

    public Controller(Model model, View view, GameBoard gameBoard) {
        // this.model = model;
        // this.view = view;

        view.registerButtonScores(new ButtonScores(model, view)); // Make a Leaderboard ActionListener for the button
        view.registerButtonNew(new ButtonNew(model, view)); // Make a New Game ActionListener for the button
        view.registerButtonCancel(new ButtonCancel(model, view)); // Make a Cancel game ActionListener for the button
        view.registerButtonSend(new ButtonSend(view, model, gameBoard));  // Create Send ActionListener for the button
        view.registerComboBoxChange(new CategoryItemChange(model)); // Make a ComboBox ActionListener for the ComboBox
    }
}
