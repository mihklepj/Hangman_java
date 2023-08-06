package controllers.listeners;

import models.Model;
import views.View;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TxtCharNew implements ActionListener {
    private final View view;
    private final Model model;

    public TxtCharNew(Model model, View view) {
        this.model = model;
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String enteredChar = view.getTxtChar().getText().toUpperCase(); // Get the entered character and convert to uppercase
        if (!enteredChar.isEmpty()) {
            System.out.println("Character entered: " + enteredChar);

            // Additional logic related to the entered character can be added here
            // For example, you can pass the enteredChar to the Model to process it in the Hangman game logic
            // model.processEnteredCharacter(enteredChar);

            // Clear the text field after processing the entered character
            view.getTxtChar().setText("");
        }
    }
}
