package org.giuda;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class GameFunction {

    private static String WORDS_FILE = "660000_parole_italiane.txt";
    private static final String STATS_FILE = "game_stats.txt";
    private static final int MIN_LENGTH = 3;
    private final boolean GOD_MODE = false;

    public void startGame() {
        Scanner scanner = new Scanner(System.in);

        int wordLength = 0;
        while (wordLength < MIN_LENGTH) {
            System.out.print("Quante lettere deve avere la parola? (minimo " + MIN_LENGTH + "): ");
            wordLength = scanner.nextInt();
            scanner.nextLine();
            /*if(wordLength == 5) {
                WORDS_FILE = "parole.txt";
            }*/
            if (wordLength < MIN_LENGTH) {
                System.out.println("La parola deve essere lunga almeno " + MIN_LENGTH + " lettere.");
            }
        }

        List<String> words = loadWords(wordLength);
        if (words.isEmpty()) {
            System.out.println("Nessuna parola trovata di " + wordLength + " lettere.");
            return;
        }

        String secret = !GOD_MODE ? words.get(new Random().nextInt(words.size())).toLowerCase() : "acereti";

        int attempts = -1;
        while (attempts != 0 && attempts != 6) {
            System.out.print("modalità infinita(0) o classica(6): ");
            attempts = scanner.nextInt();
            scanner.nextLine();
        }
        boolean infinite = attempts == 0;

        int tries = 0;
        boolean guessed = false;
        while (infinite || tries < attempts) {
            System.out.print("Tentativo " + (tries + 1) + ": ");
            String guess = scanner.nextLine().trim().toLowerCase();

            if (guess.equals("exit")) {
                System.out.println("Hai abbandonato la partita.");
                updateInfiniteStats(false, tries);
                return;
            }
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
        if (!guessed){
            System.out.println("Negraccio hai perso! La parola era: " + secret);
        }

        if (infinite) {
            updateInfiniteStats(guessed, tries);
        } else {
            updateClassicStats(guessed);
        }
    }

    private void updateClassicStats(boolean guessed) {
        Properties stats = new Properties();
        File file = new File(STATS_FILE);

        try {
            if (file.exists()) {
                try (FileReader reader = new FileReader(file)) {
                    stats.load(reader);
                }
            }

            int wins = Integer.parseInt(stats.getProperty("vittorie", "0"));
            int losses = Integer.parseInt(stats.getProperty("sconfitte", "0"));

            if (guessed) wins++;
            else losses++;

            stats.setProperty("vittorie", String.valueOf(wins));
            stats.setProperty("sconfitte", String.valueOf(losses));

            try (FileWriter writer = new FileWriter(file)) {
                stats.store(writer, null);
            }

        } catch (IOException e) {
            System.out.println("Errore salvataggio statistiche: " + e.getMessage());
        }
    }

    private void updateInfiniteStats(boolean guessed, int tries) {
        Properties stats = new Properties();
        File file = new File(STATS_FILE);

        try {
            if (file.exists()) {
                try (FileReader reader = new FileReader(file)) {
                    stats.load(reader);
                }
            }

            int min = Integer.parseInt(stats.getProperty("min_tentativi", String.valueOf(Integer.MAX_VALUE)));
            int max = Integer.parseInt(stats.getProperty("max_tentativi", "0"));
            int abbandoni = Integer.parseInt(stats.getProperty("abbandoni", "0"));

            if (guessed) {
                min = Math.min(min, tries);
                max = Math.max(max, tries);
                stats.setProperty("min_tentativi", String.valueOf(min == Integer.MAX_VALUE ? tries : min));
                stats.setProperty("max_tentativi", String.valueOf(max));
            } else {
                abbandoni++;
                stats.setProperty("abbandoni", String.valueOf(abbandoni));
            }

            try (FileWriter writer = new FileWriter(file)) {
                stats.store(writer, null);
            }

        } catch (IOException e) {
            System.out.println("Errore salvataggio statistiche: " + e.getMessage());
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

    String getFeedback(String secret, String guess) {
        char[] feedback = new char[secret.length()];
        boolean[] secretUsed = new boolean[secret.length()];
        boolean[] guessUsed = new boolean[secret.length()];

        for (int i = 0; i < secret.length(); i++) {
            if (guess.charAt(i) == secret.charAt(i)) {
                feedback[i] = 'G';
                secretUsed[i] = true;
                guessUsed[i] = true;
            }
        }

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
                feedback[i] = found ? 'Y' : 'W';
            }
        }
        return new String(feedback);
    }

    private void contaParole() {
        int i = 3;
        List<String> parole = loadWords(i);
        while (!parole.isEmpty()) {
            System.out.println("Parole di " + i + " lettere: " + parole.size());
            i++;
            parole = loadWords(i);
        }
    }

    public static void main(String[] args) {
        GameFunction game = new GameFunction();
        System.out.println("INFORMAZIONI DI GIOCO");
        System.out.println("--------------------------------------------------");
        game.contaParole();
        game.startGame();
    }
}
