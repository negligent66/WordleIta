package org.giuda;

import java.io.*;
import java.util.*;

public class RobaPerCheattare {
    public void trovaParolePossibili() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Inserisci la parola tentata (invio per uscire): ");
            String parola = scanner.nextLine().trim().toLowerCase();
            if (parola.isEmpty()) break;
            System.out.print("Inserisci la sequenza colori (V=Verde, G=Giallo, W=Grigio): ");
            String colori = scanner.nextLine().trim().toUpperCase();
            if (parola.length() != colori.length()) {
                System.out.println("Errore: la parola e la sequenza colori devono avere la stessa lunghezza.\n");
                continue;
            }
            List<String> possibili = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader("660000_parole_italiane.txt"))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    String voc = linea.trim().toLowerCase();
                    if (voc.length() != parola.length()) continue;
                    boolean ok = true;
                    Set<Character> usate = new HashSet<>();
                    for (int i = 0; i < parola.length(); i++) {
                        char c = parola.charAt(i);
                        char v = voc.charAt(i);
                        char col = colori.charAt(i);
                        if (col == 'G') { // Verde: deve essere uguale e in posizione giusta
                            if (v != c) { ok = false; break; }
                        } else if (col == 'Y') { // Giallo: deve essere presente ma NON in quella posizione
                            if (v == c || !voc.contains(String.valueOf(c))) { ok = false; break; }
                        }
                    }
                    // Controllo lettere grigie (W): non devono apparire più volte di quante sono verdi/gialle
                    for (int i = 0; i < parola.length(); i++) {
                        char c = parola.charAt(i);
                        if (colori.charAt(i) == 'W') {
                            int occorrenzeVerdeGiallo = 0, occorrenzeVoc = 0;
                            for (int j = 0; j < parola.length(); j++) {
                                if (parola.charAt(j) == c && (colori.charAt(j) == 'G' || colori.charAt(j) == 'Y')) occorrenzeVerdeGiallo++;
                                if (voc.charAt(j) == c) occorrenzeVoc++;
                            }
                            if (occorrenzeVoc > occorrenzeVerdeGiallo) { ok = false; break; }
                        }
                    }
                    if (ok) possibili.add(voc);
                }
            } catch (IOException e) {
                System.out.println("Errore nella lettura del file vocabolario: " + e.getMessage());
                continue;
            }
            System.out.println("Parole possibili:");
            for (String p : possibili) {
                System.out.println(p);
            }
            System.out.println();
        }
    }
    public static void main(String[] args) {
        RobaPerCheattare cheattatore = new RobaPerCheattare();
        cheattatore.trovaParolePossibili();
    }
}
