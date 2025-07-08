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
                        if (col == 'V') {
                            if (v != c) { ok = false; break; }
                            usate.add(c);
                        } else if (col == 'G') {
                            if (v == c || !voc.contains(String.valueOf(c))) { ok = false; break; }
                            usate.add(c);
                        } else if (col == 'W') {
                            if (voc.contains(String.valueOf(c)) && !usate.contains(c)) { ok = false; break; }
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
