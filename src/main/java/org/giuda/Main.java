package org.giuda;

import javax.swing.*;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        // Chiedi la lunghezza della parola
        int wordLength = Integer.parseInt(JOptionPane.showInputDialog("Quante lettere deve avere la parola? (minimo 3)"));
        // Chiedi il numero di tentativi
        int attempts = Integer.parseInt(JOptionPane.showInputDialog("Quanti tentativi vuoi? (0 per infiniti)"));

        // Carica le parole e scegli la parola segreta
        GameFunction gf = new GameFunction();
        List<String> words = gf.loadWords(wordLength);
        if (words.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nessuna parola trovata di " + wordLength + " lettere.");
            System.exit(0);
        }
        String secret = words.get(new Random().nextInt(words.size())).toLowerCase();

        // Avvia la GUI
        SwingUtilities.invokeLater(() -> new GameGUI(wordLength, attempts, words, secret));
    }
}