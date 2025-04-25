package App.java;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class App extends JFrame implements ActionListener {
    private final JButton[][] buttons = new JButton[3][3];
    private final JLabel scoreLabel = new JLabel("Player X: 0 | Player O: 0", SwingConstants.CENTER);
    private final JLabel turnLabel = new JLabel("Turn: Player X", SwingConstants.CENTER);
    private final JButton newGameButton = new JButton("New Game");
    private final JButton resetScoreButton = new JButton("Reset Score");
    private static final String SCORE_FILE = "scores.txt";

    private boolean isPlayerXTurn = true;
    private int playerXScore = 0;
    private int playerOScore = 0;

    public App() {
        setTitle("Tic Tac Toe");
        setLayout(new BorderLayout());

        JPanel boardPanel = new JPanel(new GridLayout(3, 3));
        initializeButtons(boardPanel);

        newGameButton.addActionListener(e -> resetGame());
        resetScoreButton.addActionListener(e -> resetScores());

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.add(scoreLabel);
        topPanel.add(turnLabel);

        JPanel bottomPanel = new JPanel(new GridLayout(1, 2));
        bottomPanel.add(newGameButton);
        bottomPanel.add(resetScoreButton);

        add(topPanel, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        loadScores();

        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void initializeButtons(JPanel panel) {
        Font font = new Font("Arial", Font.BOLD, 60);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton("");
                buttons[i][j].setFont(font);
                buttons[i][j].setFocusPainted(false);
                buttons[i][j].addActionListener(this);
                panel.add(buttons[i][j]);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton clicked = (JButton) e.getSource();
        if (!clicked.getText().isEmpty()) return;

        String currentPlayer = isPlayerXTurn ? "X" : "O";
        clicked.setText(currentPlayer);

        if (checkWinner()) {
            JOptionPane.showMessageDialog(this, "Player " + currentPlayer + " wins!");
            if (isPlayerXTurn) playerXScore++;
            else playerOScore++;
            updateScoreLabel();
            saveScores();
            resetGame();
        } else if (isBoardFull()) {
            JOptionPane.showMessageDialog(this, "It's a draw!");
            resetGame();
        } else {
            isPlayerXTurn = !isPlayerXTurn;
            turnLabel.setText("Turn: Player " + (isPlayerXTurn ? "X" : "O"));
        }
    }

    private boolean checkWinner() {
        String[][] board = new String[3][3];
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                board[i][j] = buttons[i][j].getText();

        for (int i = 0; i < 3; i++) {
            if (!board[i][0].isEmpty() && board[i][0].equals(board[i][1]) && board[i][1].equals(board[i][2]))
                return true;
            if (!board[0][i].isEmpty() && board[0][i].equals(board[1][i]) && board[1][i].equals(board[2][i]))
                return true;
        }

        return (!board[0][0].isEmpty() && board[0][0].equals(board[1][1]) && board[1][1].equals(board[2][2])) ||
               (!board[0][2].isEmpty() && board[0][2].equals(board[1][1]) && board[1][1].equals(board[2][0]));
    }

    private boolean isBoardFull() {
        for (JButton[] row : buttons)
            for (JButton button : row)
                if (button.getText().isEmpty()) return false;
        return true;
    }

    private void resetGame() {
        for (JButton[] row : buttons)
            for (JButton button : row)
                button.setText("");
        isPlayerXTurn = true;
        turnLabel.setText("Turn: Player X");
    }

    private void resetScores() {
        playerXScore = 0;
        playerOScore = 0;
        updateScoreLabel();
        saveScores();
    }

    private void updateScoreLabel() {
        scoreLabel.setText("Player X: " + playerXScore + " | Player O: " + playerOScore);
    }

    private void saveScores() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SCORE_FILE))) {
            writer.write(playerXScore + "\n" + playerOScore + "\n");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving scores!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadScores() {
        File file = new File(SCORE_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            playerXScore = Integer.parseInt(reader.readLine());
            playerOScore = Integer.parseInt(reader.readLine());
        } catch (IOException | NumberFormatException e) {
            playerXScore = 0;
            playerOScore = 0;
        }
        updateScoreLabel();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::new);
    }
}
