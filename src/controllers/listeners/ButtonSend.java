package controllers.listeners;

import models.Model;
import views.View;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * ActionListener for the "Send" button in the Hangman game.
 * This class handles user input and updates the game state accordingly.
 */
public class ButtonSend implements ActionListener {
    private final View view;
    private final Model model;

    /**
     * Constructor for the ButtonSend ActionListener.
     *
     * @param view  The View instance representing the GUI of the Hangman game.
     * @param model The Model instance representing the game's logic and data.
     */
    public ButtonSend(View view, Model model) {
        this.view = view;
        this.model = model;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Get the entered character from the text field
        char enteredChar = view.getTxtChar().getText().toUpperCase().charAt(0);
        if (enteredChar != ' ') {
            // Check if the entered character is in the newWord
            boolean foundInWord = model.getNewWord().toUpperCase().contains(String.valueOf(enteredChar));

            if (foundInWord) {
                // Process the entered character and update the user word
                if (!model.getCorrectChars().contains(enteredChar)) {
                    model.getCorrectChars().add(enteredChar);
                }

                model.changeUserInput(enteredChar);
                String updatedUserWord = model.getUserWord();
                this.view.getLblResult().setText(this.model.getUserWord());
                if (!updatedUserWord.contains("_")) {
                    view.getGameTime().stopTimer(); // Stop gameTime
                    view.showNewButton();
                    view.getGameBoard().getLblError().setText("WELL DONE!");
                    String playerName = JOptionPane.showInputDialog(null, "Write your name:", null);
                    model.setPlayerName(playerName);
                    model.setGametime(view.getGameTime().getPlayedTimeInSeconds());
                    model.insertToScoreboard();
                }
            } else {
                // Handle the case when the character is not in the newWord
                if (!model.getAllUserChars().contains(enteredChar)) {
                    model.getAllUserChars().add(enteredChar);
                }

                // Increment the error counter and update the image
                int currentCounter = model.getCounter();
                model.setCounter(currentCounter + 1);
                // Update the image and handle incorrect guesses
                view.setNewImage(model.getCounter());
                view.getGameBoard().getLblError().setText(model.getCounter() + " mistakes. Wrong letters: " +
                        String.join(", ", model.getAllUserChars().stream().
                                map(String::valueOf).toArray(String[]::new)));
                if (model.getCounter() > 10) {
                    view.getGameTime().stopTimer();
                    view.getGameTime().setRunning(false);
                    view.getGameBoard().getLblError().setText("GAME OVER");
                    view.showNewButton();
                }
            }

            // Clear the text field after processing the entered character
            view.getTxtChar().setText("");
            view.getTxtChar().requestFocus();
        }
    }
}
