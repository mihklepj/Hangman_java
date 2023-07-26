package controllers.listeners;

import models.Model;
import views.View;
import views.panels.GameBoard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonSend implements ActionListener {
    private final View view;
    private final Model model;
    private final GameBoard gameBoard;


    public ButtonSend(View view, Model model, GameBoard gameBoard) {
        this.view = view;
        this.model = model;
        this.gameBoard = gameBoard;

    }
    public void actionPerformed(ActionEvent e) {
        if(!view.getTxtChar().getText().isEmpty()) {
            // Check if the letter entered is in the word
            System.out.println(gameBoard.txtChar);
            // If the word contains the letter then add the letter(s) below
            // Else change the image
            // and on GameBoard lblError include the wrong letters
            // lblResult and lblError full caps
            // If all letters guessed, stop time and show live time

        }
    }
}
