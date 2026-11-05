package org.giuda;

import javax.swing.*;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        int wordLength = Integer.parseInt(JOptionPane.showInputDialog("Quante lettere deve avere la parola? (minimo 3)"));
        int attempts = Integer.parseInt(JOptionPane.showInputDialog("Quanti tentativi vuoi? (0 per infiniti, qualsiasi altro valore per classica)"));
        GameFunction gf = new GameFunction();
        List<String> words = gf.loadWords(wordLength);
        if (words.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nessuna parola trovata di " + wordLength + " lettere.");
            System.exit(0);
        }
        String secret = words.get(new Random().nextInt(words.size())).toLowerCase();
        //System.out.println("Parola segreta: " + secret); // Solo per debug, rimuovere in produzione
        SwingUtilities.invokeLater(() -> new GameGUI(wordLength, attempts, words, secret));
    }
}