package snakegame;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;
    static final int WIDTH = 500;
    static final int HEIGHT = 500;
    static final int UNIT_SIZE = 20;

    final int[] x = new int[(WIDTH * HEIGHT) / (UNIT_SIZE * UNIT_SIZE)];
    final int[] y = new int[(WIDTH * HEIGHT) / (UNIT_SIZE * UNIT_SIZE)];

    int length = 5;
    int foodEaten;
    int foodX;
    int foodY;
    char direction = 'D';
    boolean running = false;
    Timer timer;
    Random random;
    JButton playAgainButton;

    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.DARK_GRAY);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        initializeSnake();
        play();
    }

    private void initializeSnake() {
        int startX = WIDTH / 2;
        int startY = HEIGHT / 2;
        for (int i = 0; i < length; i++) {
            x[i] = startX - (i * UNIT_SIZE);
            y[i] = startY;
        }
    }

    public void play() {
        running = true;
        length = 5;
        foodEaten = 0;
        direction = 'D';
        initializeSnake();
        addFood();
        if (timer != null) timer.stop();
        timer = new Timer(100, this);
        timer.start();

        if (playAgainButton != null) {
            this.remove(playAgainButton);
            playAgainButton = null;
        }
        this.requestFocusInWindow();
    }

    public void addFood() {
        boolean onSnake;
        do {
            onSnake = false;
            foodX = random.nextInt(WIDTH / UNIT_SIZE) * UNIT_SIZE;
            foodY = random.nextInt(HEIGHT / UNIT_SIZE) * UNIT_SIZE;
            for (int i = 0; i < length; i++) {
                if (foodX == x[i] && foodY == y[i]) {
                    onSnake = true;
                    break;
                }
            }
        } while (onSnake);
    }

    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkFood();
            checkHit();
        }
        repaint();
    }

    public void move() {
        for (int i = length - 1; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        switch (direction) {
            case 'U': y[0] -= UNIT_SIZE; break;
            case 'D': y[0] += UNIT_SIZE; break;
            case 'L': x[0] -= UNIT_SIZE; break;
            case 'R': x[0] += UNIT_SIZE; break;
        }
    }

    public void checkFood() {
        // Check if the snake's head intersects with the food
        Rectangle snakeHead = new Rectangle(x[0], y[0], UNIT_SIZE, UNIT_SIZE);
        Rectangle food = new Rectangle(foodX, foodY, UNIT_SIZE, UNIT_SIZE);
        
        if (snakeHead.intersects(food)) {
            length++;
            foodEaten++;
            addFood();
        }
    }

    public void checkHit() {
        for (int i = 1; i < length; i++) {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
            }
        }
        if (x[0] < 0 || x[0] >= WIDTH || y[0] < 0 || y[0] >= HEIGHT) {
            running = false;
        }
        if (!running) {
            timer.stop();
            showPlayAgainButton();
        }
    }

    public void showPlayAgainButton() {
        playAgainButton = new JButton("Play Again");
        playAgainButton.setBounds(WIDTH / 2 - 75, HEIGHT / 2 + 40, 150, 40);
        playAgainButton.setFocusPainted(false);
        playAgainButton.addActionListener(e -> play());
        this.setLayout(null);
        this.add(playAgainButton);
        this.revalidate();
        this.repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (running) {
            // Draw food
            g.setColor(Color.RED);
            g.fillOval(foodX, foodY, UNIT_SIZE, UNIT_SIZE);
            
            // Draw snake
            for (int i = 0; i < length; i++) {
                if (i == 0) {
                    g.setColor(Color.GREEN);
                } else {
                    g.setColor(Color.LIGHT_GRAY);
                }
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }
            
            // Draw score
            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 20));
            g.drawString("Score: " + foodEaten, 10, 20);
        } else {
            g.setColor(Color.RED);
            g.setFont(new Font("SansSerif", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            String msg = "Game Over! Score: " + foodEaten;
            g.drawString(msg, (WIDTH - metrics.stringWidth(msg)) / 2, HEIGHT / 2);
        }
    }

    private class MyKeyAdapter extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT: if (direction != 'R') direction = 'L'; break;
                case KeyEvent.VK_RIGHT: if (direction != 'L') direction = 'R'; break;
                case KeyEvent.VK_UP: if (direction != 'D') direction = 'U'; break;
                case KeyEvent.VK_DOWN: if (direction != 'U') direction = 'D'; break;
            }
        }
    }
}