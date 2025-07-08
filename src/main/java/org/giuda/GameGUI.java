package org.giuda;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class GameGUI extends JFrame {
    private int wordLength;
    private int attempts;
    private int tries = 0;
    private String secret;
    private List<String> words;
    private JPanel lettersPanel;
    private JTextField inputField;
    private JLabel attemptsLabel;
    private JPanel[] letterBoxes;

    public GameGUI(int wordLength, int attempts, List<String> words, String secret) {
        this.wordLength = wordLength;
        this.attempts = attempts;
        this.words = words;
        this.secret = secret;

        setTitle("Gioco Parole");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Tentativi
        attemptsLabel = new JLabel("Tentativo: 1 / " + (attempts == 0 ? "∞" : attempts));
        add(attemptsLabel, BorderLayout.NORTH);

        // Pannello lettere
        lettersPanel = new JPanel(new GridLayout(1, wordLength, 5, 5));
        letterBoxes = new JPanel[wordLength];
        for (int i = 0; i < wordLength; i++) {
            JPanel box = new JPanel();
            box.setPreferredSize(new Dimension(50, 50));
            box.setBackground(Color.LIGHT_GRAY);
            box.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            lettersPanel.add(box);
            letterBoxes[i] = box;
        }
        add(lettersPanel, BorderLayout.CENTER);

        // Input e bottone
        JPanel inputPanel = new JPanel();
        inputField = new JTextField(wordLength);
        JButton checkButton = new JButton("Verifica");
        inputPanel.add(inputField);
        inputPanel.add(checkButton);
        add(inputPanel, BorderLayout.SOUTH);

        checkButton.addActionListener(e -> checkWord());

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void checkWord() {
        String guess = inputField.getText().trim().toLowerCase();
        if (guess.length() != wordLength) {
            JOptionPane.showMessageDialog(this, "La parola deve essere di " + wordLength + " lettere.");
            return;
        }
        if (!words.contains(guess)) {
            JOptionPane.showMessageDialog(this, "Parola non valida.");
            return;
        }

        String feedback = new GameFunction().getFeedback(secret, guess);
        for (int i = 0; i < wordLength; i++) {
            char f = feedback.charAt(i);
            Color color = switch (f) {
                case 'G' -> Color.GREEN;
                case 'Y' -> Color.YELLOW;
                default -> Color.WHITE;
            };
            letterBoxes[i].setBackground(color);
            letterBoxes[i].removeAll();
            JLabel l = new JLabel(String.valueOf(guess.charAt(i)).toUpperCase(), SwingConstants.CENTER);
            l.setFont(new Font("Arial", Font.BOLD, 24));
            letterBoxes[i].add(l);
            letterBoxes[i].revalidate();
            letterBoxes[i].repaint();
        }

        tries++;
        attemptsLabel.setText("Tentativo: " + (tries + 1) + " / " + (attempts == 0 ? "∞" : attempts));
        if (guess.equals(secret)) {
            JOptionPane.showMessageDialog(this, "Complimenti! Hai indovinato la parola.");
            System.exit(0);
        } else if (attempts != 0 && tries >= attempts) {
            JOptionPane.showMessageDialog(this, "Hai perso! La parola era: " + secret);
            System.exit(0);
        }
        inputField.setText("");
    }
}