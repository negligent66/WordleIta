package org.giuda;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class GameGUI extends JFrame {
    private int wordLength;
    private int tries = 0;
    private String secret;
    private List<String> words;
    private boolean infiniteMode;
    // Per modalità infinita
    private JPanel[] infiniteBoxesPrev, infiniteBoxesInput;
    private JTextField[] infiniteFieldsInput;
    private JButton checkButton, continueButton;
    // Per modalità classica
    private JPanel[][] classicBoxes;
    private JTextField[][] classicFields;
    private int currentRow = 0;

    public GameGUI(int wordLength, int attempts, List<String> words, String secret) {
        this.wordLength = wordLength;
        this.words = words;
        this.secret = secret;
        this.infiniteMode = (attempts == 0);

        setTitle("Gioco Parole");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel attemptsLabel = new JLabel(infiniteMode ? "Modalità Infinita" : "Modalità Classica: 6 tentativi");
        add(attemptsLabel, BorderLayout.NORTH);

        if (infiniteMode) {
            add(buildInfinitePanel(), BorderLayout.CENTER);
        } else {
            add(buildClassicPanel(), BorderLayout.CENTER);
        }

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Modalità infinita: 2 slot
    private JPanel buildInfinitePanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 5, 5));
        // Slot storico
        JPanel prevPanel = new JPanel(new GridLayout(1, wordLength, 5, 5));
        infiniteBoxesPrev = new JPanel[wordLength];
        for (int i = 0; i < wordLength; i++) {
            JPanel box = new JPanel();
            box.setPreferredSize(new Dimension(50, 50));
            box.setBackground(Color.LIGHT_GRAY);
            box.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            infiniteBoxesPrev[i] = box;
            prevPanel.add(box);
        }
        panel.add(prevPanel);

        // Slot input
        JPanel inputPanel = new JPanel(new GridLayout(1, wordLength, 5, 5));
        infiniteBoxesInput = new JPanel[wordLength];
        infiniteFieldsInput = new JTextField[wordLength];
        for (int i = 0; i < wordLength; i++) {
            JPanel box = new JPanel(new BorderLayout());
            box.setPreferredSize(new Dimension(50, 50));
            box.setBackground(Color.LIGHT_GRAY);
            box.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            JTextField tf = new JTextField(1);
            tf.setHorizontalAlignment(JTextField.CENTER);
            tf.setFont(new Font("Arial", Font.BOLD, 24));
            ((AbstractDocument) tf.getDocument()).setDocumentFilter(new OneLetterFilter(i, infiniteFieldsInput));
            int idx = i;
            tf.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    if (Character.isLetter(e.getKeyChar())) {
                        if (tf.getText().length() == 1 && idx < wordLength - 1) {
                            infiniteFieldsInput[idx + 1].requestFocus();
                        }
                    }
                }

                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE && tf.getText().isEmpty() && idx > 0) {
                        infiniteFieldsInput[idx - 1].requestFocus();
                    }
                }
            });
            infiniteFieldsInput[i] = tf;
            box.add(tf, BorderLayout.CENTER);
            infiniteBoxesInput[i] = box;
            inputPanel.add(box);
        }
        bindGuessKeys(infiniteFieldsInput, this::checkInfiniteWord);
        panel.add(inputPanel);

        // Bottoni
        JPanel btnPanel = new JPanel();
        checkButton = new JButton("Verifica");
        continueButton = new JButton("Continua");
        continueButton.setEnabled(false);
        btnPanel.add(checkButton);
        btnPanel.add(continueButton);
        panel.add(btnPanel);

        checkButton.addActionListener(e -> checkInfiniteWord());
        continueButton.addActionListener(e -> continueInfinite());

        return panel;
    }

    // Modalità classica: 6 slot
    private JPanel buildClassicPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel rowsPanel = new JPanel(new GridLayout(6, 1, 5, 5));
        classicBoxes = new JPanel[6][wordLength];
        classicFields = new JTextField[6][wordLength];
        for (int row = 0; row < 6; row++) {
            JPanel rowPanel = new JPanel(new GridLayout(1, wordLength, 5, 5));
            for (int col = 0; col < wordLength; col++) {
                JPanel box = new JPanel(new BorderLayout());
                box.setPreferredSize(new Dimension(50, 50));
                box.setBackground(Color.LIGHT_GRAY);
                box.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                JTextField tf = new JTextField(1);
                tf.setHorizontalAlignment(JTextField.CENTER);
                tf.setFont(new Font("Arial", Font.BOLD, 24));
                tf.setEnabled(row == 0); // Solo la prima riga abilitata
                ((AbstractDocument) tf.getDocument()).setDocumentFilter(new OneLetterFilter(col, classicFields[row]));
                int idx = col;
                tf.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        if (tf.isEnabled() && Character.isLetter(e.getKeyChar())) {
                            if (tf.getText().length() == 1 && idx < wordLength - 1) {
                                classicFields[currentRow][idx + 1].requestFocus();
                            }
                        }
                    }
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (tf.isEnabled() && e.getKeyCode() == KeyEvent.VK_BACK_SPACE && tf.getText().isEmpty() && idx > 0) {
                            classicFields[currentRow][idx - 1].requestFocus();
                        }
                    }
                });
                classicFields[row][col] = tf;
                box.add(tf, BorderLayout.CENTER);
                classicBoxes[row][col] = box;
                rowPanel.add(box);
            }
            rowsPanel.add(rowPanel);
        }
        for (JTextField[] row : classicFields) {
            bindGuessKeys(row, this::checkClassicWord);
        }
        mainPanel.add(rowsPanel, BorderLayout.CENTER);

        // Bottone centrato
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton checkBtn = new JButton("Verifica");
        checkBtn.addActionListener(e -> checkClassicWord());
        buttonPanel.add(checkBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private void bindGuessKeys(JTextField[] fields, Runnable submit) {
        for (int i = 0; i < fields.length; i++) {
            JTextField field = fields[i];
            int previous = Math.max(0, i - 1);
            int next = Math.min(fields.length - 1, i + 1);
            InputMap inputMap = field.getInputMap(JComponent.WHEN_FOCUSED);
            ActionMap actionMap = field.getActionMap();

            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "previousLetter");
            actionMap.put("previousLetter", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fields[previous].requestFocusInWindow();
                }
            });
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "nextLetter");
            actionMap.put("nextLetter", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fields[next].requestFocusInWindow();
                }
            });
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "submitGuess");
            actionMap.put("submitGuess", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    submit.run();
                }
            });
        }
    }

    // Filtro per una sola lettera e passaggio automatico
    private static class OneLetterFilter extends DocumentFilter {
        private final int idx;
        private final JTextField[] fields;

        public OneLetterFilter(int idx, JTextField[] fields) {
            this.idx = idx;
            this.fields = fields;
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text != null && text.length() > 0) {
                char c = text.charAt(0);
                if (Character.isLetter(c)) {
                    fb.replace(0, fb.getDocument().getLength(), String.valueOf(c), attrs);
                    if (text.length() > 1 && idx < fields.length - 1) {
                        fields[idx + 1].setText(String.valueOf(text.charAt(1)));
                        fields[idx + 1].requestFocus();
                    } else if (idx < fields.length - 1) {
                        fields[idx + 1].requestFocus();
                    }
                }
            }
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            replace(fb, offset, 0, string, attr);
        }
    }

    // --- Modalità infinita ---
    private void checkInfiniteWord() {
        StringBuilder guessBuilder = new StringBuilder();
        for (JTextField tf : infiniteFieldsInput) {
            String text = tf.getText().trim().toLowerCase();
            if (text.length() != 1 || !Character.isLetter(text.charAt(0))) {
                JOptionPane.showMessageDialog(this, "Inserisci una lettera valida in ogni casella.");
                return;
            }
            guessBuilder.append(text);
        }
        String guess = guessBuilder.toString();
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
                case 'W' -> Color.WHITE;
                default -> Color.LIGHT_GRAY;
            };
            infiniteBoxesInput[i].setBackground(color);
            infiniteFieldsInput[i].setEditable(false);

            // Mostra la lettera nella casella (come JLabel)
            infiniteBoxesInput[i].removeAll();
            JLabel l = new JLabel(infiniteFieldsInput[i].getText().toUpperCase(), SwingConstants.CENTER);
            l.setFont(new Font("Arial", Font.BOLD, 24));
            infiniteBoxesInput[i].add(l, BorderLayout.CENTER);
            infiniteBoxesInput[i].revalidate();
            infiniteBoxesInput[i].repaint();
        }
        checkButton.setEnabled(false);
        continueButton.setEnabled(true);
    }

    private void continueInfinite() {
        // Copia input -> storico
        for (int i = 0; i < wordLength; i++) {
            infiniteBoxesPrev[i].setBackground(infiniteBoxesInput[i].getBackground());
            infiniteBoxesPrev[i].removeAll();
            Component[] comps = infiniteBoxesInput[i].getComponents();
            if (comps.length > 0 && comps[0] instanceof JLabel) {
                infiniteBoxesPrev[i].add(comps[0]);
            } else {
                JLabel l = new JLabel(infiniteFieldsInput[i].getText().toUpperCase(), SwingConstants.CENTER);
                l.setFont(new Font("Arial", Font.BOLD, 24));
                infiniteBoxesPrev[i].add(l);
            }
            infiniteBoxesPrev[i].revalidate();
            infiniteBoxesPrev[i].repaint();
        }
        // Svuota input
        for (int i = 0; i < wordLength; i++) {
            infiniteBoxesInput[i].setBackground(Color.LIGHT_GRAY);
            infiniteBoxesInput[i].removeAll();
            infiniteFieldsInput[i].setText(""); // <-- questa riga cancella il testo
            infiniteFieldsInput[i].setEditable(true);
            infiniteBoxesInput[i].add(infiniteFieldsInput[i], BorderLayout.CENTER);
            infiniteBoxesInput[i].revalidate();
            infiniteBoxesInput[i].repaint();
        }
        infiniteFieldsInput[0].requestFocus();
        checkButton.setEnabled(true);
        continueButton.setEnabled(false);
    }

    // --- Modalità classica ---
    private void checkClassicWord() {
        StringBuilder guessBuilder = new StringBuilder();
        for (JTextField tf : classicFields[currentRow]) {
            String text = tf.getText().trim().toLowerCase();
            if (text.length() != 1 || !Character.isLetter(text.charAt(0))) {
                JOptionPane.showMessageDialog(this, "Inserisci una lettera valida in ogni casella.");
                return;
            }
            guessBuilder.append(text);
        }
        String guess = guessBuilder.toString();
        if (!words.contains(guess)) {
            JOptionPane.showMessageDialog(this, "Parola non valida.");
            return;
        }
        String feedback = new GameFunction().getFeedback(secret, guess);
        System.out.println("Feedback: " + feedback);
        for (int i = 0; i < wordLength; i++) {
            char f = feedback.charAt(i);
            Color color = switch (f) {
                case 'G' -> Color.GREEN;
                case 'Y' -> Color.YELLOW;
                case 'W' -> Color.WHITE;
                default -> Color.LIGHT_GRAY;
            };
            System.out.println("Colore per lettera " + i + ": " + color);
            classicFields[currentRow][i].setBackground(color);
            classicFields[currentRow][i].setEditable(false);
        }
        if (guess.equals(secret)) {
            JOptionPane.showMessageDialog(this, "Complimenti! Hai indovinato la parola.");
            System.exit(0);
        }
        currentRow++;
        if (currentRow >= 6) {
            JOptionPane.showMessageDialog(this, "Hai perso! La parola era: " + secret);
            System.exit(0);
        }
        // Abilita la riga successiva
        for (int i = 0; i < wordLength; i++) {
            classicFields[currentRow][i].setEnabled(true);
        }
        classicFields[currentRow][0].requestFocus();
    }
}