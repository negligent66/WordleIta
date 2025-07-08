package org.giuda;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class GameFunction {

    private static final String WORDS_FILE = "660000_parole_italiane.txt";
    private static final int MIN_LENGTH = 3;

    public void startGame() {
        Scanner scanner = new Scanner(System.in);

        // Chiedi la lunghezza della parola
        int wordLength = 0;
        while (wordLength < MIN_LENGTH) {
            System.out.print("Quante lettere deve avere la parola? (minimo " + MIN_LENGTH + "): ");
            wordLength = scanner.nextInt();
            scanner.nextLine();
            if (wordLength < MIN_LENGTH) {
                System.out.println("La parola deve essere lunga almeno " + MIN_LENGTH + " lettere.");
            }
        }

        // Carica le parole della lunghezza richiesta
        List<String> words = loadWords(wordLength);
        if (words.isEmpty()) {
            System.out.println("Nessuna parola trovata di " + wordLength + " lettere.");
            return;
        }

        // Scegli una parola casuale
        String secret = words.get(new Random().nextInt(words.size())).toLowerCase();

        // Chiedi il numero di tentativi
        int attempts = -1;
        while (attempts != 0 && attempts != 6) {
            System.out.print("modalità infinita(0) o classica(6): ");
            attempts = scanner.nextInt();
            scanner.nextLine();
        }
        boolean infinite = attempts == 0;

        // Inizia il gioco
        int tries = 0;
        boolean guessed = false;
        while (infinite || tries < attempts) {
            System.out.print("Tentativo " + (tries + 1) + ": ");
            String guess = scanner.nextLine().trim().toLowerCase();

            if (guess.length() != wordLength) {
                System.out.println("La parola deve essere di " + wordLength + " lettere.");
                continue;
            }
            if (!words.contains(guess)) {
                System.out.println("Parola non valida.");
                continue;
            }

            String feedback = getFeedback(secret, guess);
            System.out.println(feedback);

            if (guess.equals(secret)) {
                System.out.println("Complimenti! Hai indovinato la parola.");
                guessed = true;
                break;
            }
            tries++;
        }
        if (!guessed) {
            System.out.println("Hai perso! La parola era: " + secret);
        }
    }

    List<String> loadWords(int length) {
        List<String> result = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(WORDS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String word = line.trim().toLowerCase();
                if (word.length() == length && word.chars().allMatch(Character::isLetter)) {
                    result.add(word);
                }
            }
        } catch (IOException e) {
            System.out.println("Errore nella lettura del file delle parole.");
        }
        return result;
    }

    // Restituisce feedback stile Wordle: [G]iusta, [S]bagliata, [P]resente
    String getFeedback(String secret, String guess) {
        char[] feedback = new char[secret.length()];
        boolean[] secretUsed = new boolean[secret.length()];
        boolean[] guessUsed = new boolean[secret.length()];

        // Verde
        for (int i = 0; i < secret.length(); i++) {
            if (guess.charAt(i) == secret.charAt(i)) {
                feedback[i] = 'G';
                secretUsed[i] = true;
                guessUsed[i] = true;
            }
        }

        // Giallo o bianco
        for (int i = 0; i < secret.length(); i++) {
            if (!guessUsed[i]) {
                boolean found = false;
                for (int j = 0; j < secret.length(); j++) {
                    if (!secretUsed[j] && guess.charAt(i) == secret.charAt(j)) {
                        found = true;
                        secretUsed[j] = true;
                        break;
                    }
                }
                if (!found) {
                    feedback[i] = 'W'; // Bianco
                } else {
                    feedback[i] = 'Y'; // Giallo
                }
            }
        }

        return new String(feedback);
    }
    public static void main(String[] args) {
        GameFunction game = new GameFunction();
        game.startGame();
    }

}