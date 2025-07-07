package org.giuda;

public class Main {
    public static void main(String[] args) {
        System.out.println("REGOLINE:");
        System.out.println("W -> white, la parola non è presente");
        System.out.println("Y -> yellow, la parola è presente ma non in posizione errata");
        System.out.println("G -> green, la parola è presente e in posizione corretta");
        System.out.println("--------------------------------------------------------------------------");
        new GameFunction().startGame();
    }
}