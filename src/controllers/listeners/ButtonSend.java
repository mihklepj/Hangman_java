package controllers.listeners;

import models.Model;
import views.View;
import views.panels.GameBoard;
import views.panels.GameResult;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonSend implements ActionListener {
    private final View view;
    private final Model model;

    public ButtonSend(View view, Model model) {
        this.view = view;
        this.model = model;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Get the entered character from the text field
        char enteredChar = view.getTxtChar().getText().toUpperCase().charAt(0);
        if (enteredChar != ' ') {
            System.out.println("Character entered: " + enteredChar);
            System.out.println("Get user word: " + model.getUserWord());
            System.out.println("Get new word: " + model.getNewWord());
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
                System.out.println("Updated user word: " + updatedUserWord);
                if (!updatedUserWord.contains("_")) {
                    view.getGameTime().stopTimer(); // Stop gameTime
                    System.out.println("KÕIK TÄHED ON ARVATUD");
                    System.out.println("Mänguaeg: "+view.getGameTime().getPlayedTimeInSeconds());
                    view.showNewButton();
                    view.getGameBoard().getLblError().setText("WELL DONE!");
                    String playerName = JOptionPane.showInputDialog(null, "Write your name:", null);
                    System.out.println("Name: "+ playerName);
                    model.setPlayerName(playerName);
                    model.setGametime(view.getGameTime().getPlayedTimeInSeconds());
                    model.insertToScoreboard();
                }
            } else {
                // Handle the case when the character is not in the newWord
                if (!model.getAllUserChars().contains(enteredChar)) {
                    model.getAllUserChars().add(enteredChar);
                }

                System.out.println("Character not found in the newWord.");

                // Increment the error counter and update the image
                int currentCounter = model.getCounter();
                model.setCounter(currentCounter + 1);
                // Update the image and handle incorrect guesses
                view.setNewImage(model.getCounter());
                view.getGameBoard().getLblError().setText(model.getCounter()+" mistakes. Wrong letters: "+
                        String.join(", ", model.getAllUserChars().stream().
                                map(String::valueOf).toArray(String[]::new)));
                if (model.getCounter() > 10) {
                    System.out.println("Game Over!");
                    view.getGameTime().stopTimer();
                    view.getGameTime().setRunning(false);
                    view.getGameBoard().getLblError().setText("GAME OVER");
                    view.showNewButton();
                }
            }
            System.out.println("Counter: "+model.getCounter());
            System.out.println("Wrong chars: "+model.getAllUserChars());

            // Clear the text field after processing the entered character
            view.getTxtChar().setText("");
            view.getTxtChar().requestFocus();
        }
    }
}
