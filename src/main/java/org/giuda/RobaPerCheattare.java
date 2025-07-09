package org.giuda;

import java.io.*;
import java.util.*;

public class RobaPerCheattare {

    private static final String STATS_FILE = "game_stats.txt";

    public void trovaParolePossibili() {
        Scanner scanner = new Scanner(System.in);
        List<String> tentativi = new ArrayList<>();
        List<String> colori = new ArrayList<>();

        List<String> parolePossibili = null;

        while (true) {
            System.out.print("Inserisci la parola tentata (invio per uscire): ");
            String parola = scanner.nextLine().trim().toLowerCase();
            if (parola.isEmpty()) break;

            incrementaCheat(); // Conta il cheating

            System.out.print("Inserisci la sequenza colori (G=Verde, Y=Giallo, W=Grigio): ");
            String col = scanner.nextLine().trim().toUpperCase();
            if (parola.length() != col.length()) {
                System.out.println("Errore: la parola e la sequenza colori devono avere la stessa lunghezza.\n");
                continue;
            }
            tentativi.add(parola);
            colori.add(col);

            if (parolePossibili == null) {
                parolePossibili = new ArrayList<>();
                try (BufferedReader br = new BufferedReader(new FileReader("660000_parole_italiane.txt"))) {
                    String linea;
                    while ((linea = br.readLine()) != null) {
                        String voc = linea.trim().toLowerCase();
                        if (voc.length() == parola.length()) {
                            parolePossibili.add(voc);
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Errore nella lettura del file vocabolario: " + e.getMessage());
                    return;
                }
            }

            List<String> nuovePossibili = new ArrayList<>();
            for (String voc : parolePossibili) {
                boolean ok = true;
                for (int t = 0; t < tentativi.size(); t++) {
                    String tent = tentativi.get(t);
                    String colSeq = colori.get(t);
                    for (int i = 0; i < tent.length(); i++) {
                        char c = tent.charAt(i);
                        char v = voc.charAt(i);
                        char colChar = colSeq.charAt(i);
                        if (colChar == 'G') {
                            if (v != c) {
                                ok = false;
                                break;
                            }
                        } else if (colChar == 'Y') {
                            if (v == c || !voc.contains(String.valueOf(c))) {
                                ok = false;
                                break;
                            }
                        }
                    }
                    if (!ok) break;
                    for (int i = 0; i < tent.length(); i++) {
                        char c = tent.charAt(i);
                        if (colSeq.charAt(i) == 'W') {
                            int occorrenzeVerdeGiallo = 0, occorrenzeVoc = 0;
                            for (int j = 0; j < tent.length(); j++) {
                                if (tent.charAt(j) == c && (colSeq.charAt(j) == 'G' || colSeq.charAt(j) == 'Y'))
                                    occorrenzeVerdeGiallo++;
                                if (voc.charAt(j) == c) occorrenzeVoc++;
                            }
                            if (occorrenzeVoc > occorrenzeVerdeGiallo) {
                                ok = false;
                                break;
                            }
                        }
                    }
                    if (!ok) break;
                }
                if (ok) nuovePossibili.add(voc);
            }
            parolePossibili = nuovePossibili;

            System.out.println("Parole possibili:");
            for (String p : parolePossibili) {
                System.out.println(p);
            }
            System.out.println();
        }
    }

    private void incrementaCheat() {
        Properties stats = new Properties();
        File file = new File(STATS_FILE);

        try {
            if (file.exists()) {
                try (FileReader reader = new FileReader(file)) {
                    stats.load(reader);
                }
            }

            int cheatCount = Integer.parseInt(stats.getProperty("cheat", "0"));
            stats.setProperty("cheat", String.valueOf(cheatCount + 1));

            try (FileWriter writer = new FileWriter(file)) {
                stats.store(writer, null);
            }
        } catch (IOException e) {
            System.out.println("Errore aggiornamento cheat: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        RobaPerCheattare cheattatore = new RobaPerCheattare();
        cheattatore.trovaParolePossibili();
    }
}