/**
 * Created by Elvis on 03.10.2016.
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class SnakeGame {

    final String TITLE_OF_PROGRAM = "Snake Game";
    final String GAME_OVER_MSG = "GAME OVER";
    final int POINT_RADIUS = 13; // in pix
    final int FIELD_HEIGHT = 40;  // in point
    final int FIELD_WIDTH = 60;
    final int FIELD_DX = 6;
    final int FIELD_DY = 28;
    final int START_SNAKE_SIZE = 6;
    final int START_SNAKE_X = 10;
    final int START_SNAKE_Y = 10;
    final int SHOW_DELAY = 125;
    final int LEFT = 37;
    final int RIGHT = 39;
    final int UP = 38;
    final int DOWN = 40;
    final int STAT_DIRECTION = RIGHT;
    final Color DEFAULT_COLOR = Color.black;
    final Color FOOD_COLOR = Color.green;
    final Color POISON_COLOR = Color.red;
    Snake snake;
    Food food;
    Poison poison;
    JFrame frame;
    Canvas canvasPanel;
    Random random = new Random();
    boolean gameOver = false;

    public static void main(String[] args) {
        new SnakeGame().go();
    }

    void go() {
        frame = new JFrame(TITLE_OF_PROGRAM + " : " + START_SNAKE_SIZE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(FIELD_WIDTH * POINT_RADIUS + FIELD_DX, FIELD_HEIGHT * POINT_RADIUS + FIELD_DY);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        canvasPanel = new Canvas();
        canvasPanel.setBackground(Color.white);

        frame.getContentPane().add(BorderLayout.CENTER, canvasPanel);
        frame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                snake.setDirection(e.getKeyCode());
            }
        });

        frame.setVisible(true);

        snake = new Snake(START_SNAKE_X, START_SNAKE_Y, START_SNAKE_SIZE, STAT_DIRECTION);
        food = new Food();
        poison = new Poison();
        while (!gameOver) {
            snake.move();
            if (food.isEaten()) {
                food.next();
                poison.add();
            }
            canvasPanel.repaint();
            try {
                Thread.sleep(SHOW_DELAY);
            } catch (Exception e) {
            }
        }
    }

    class Snake {
        ArrayList<Point> snake = new ArrayList<>();
        private int direction;

        public Snake(int x, int y, int length, int direction) {
            for (int i = 0; i < length; i++) {
                Point point = new Point(x - i, y);
                snake.add(point);
            }
            this.direction = direction;
        }

        boolean isInsideSnake(int x, int y) {
            for (Point point : snake) {
                if ((point.getX() == x) && (point.getY() == y)) {
                    return true;
                }
            }
            return false;
        }

        boolean isFood(Point food) {
            return ((snake.get(0).getX() == food.getX()) && (snake.get(0).getY() == food.getY()));
        }

        void move() {
            int x = snake.get(0).getX();
            int y = snake.get(0).getY();
            if (direction == LEFT) {
                x--;
            }
            if (direction == RIGHT) {
                x++;
            }
            if (direction == UP) {
                y--;
            }
            if (direction == DOWN) {
                y++;
            }
            if (x > FIELD_WIDTH - 1) {
                x = 0;
            }
            if (x < 0) {
                x = FIELD_WIDTH - 1;
            }
            if (y > FIELD_HEIGHT - 1) {
                y = 0;
            }
            if (y < 0) {
                y = FIELD_HEIGHT - 1;
            }
            gameOver = isInsideSnake(x, y) || poison.isPoison(x, y); // check for scross itselves

            snake.add(0, new Point(x, y));
            if (isFood(food)) {
                food.eat();
                frame.setTitle(TITLE_OF_PROGRAM + " : " + snake.size());
            } else {
                snake.remove(snake.size() - 1);
            }
        }

        void setDirection(int direction) {
            if ((direction >= LEFT) && (direction <= DOWN)) {
                if (Math.abs(this.direction - direction) != 2) {
                    this.direction = direction;
                }
            }
        }

        void paint(Graphics g) {
            for (Point point : snake) {
                point.paint(g);
            }
        }
    }

    class Point {
        private int x;
        private int y;
        private Color color = DEFAULT_COLOR;

        public Point(int x, int y) {
            this.setXY(x, y);
        }

        public Point(int x, int y, Color color) {
            this.setXY(x, y);
            this.color = color;
        }

        void setXY(int x, int y) {
            this.x = x;
            this.y = y;
        }

        void paint(Graphics g) {
            g.setColor(color);
            g.fillOval(x * POINT_RADIUS, y * POINT_RADIUS, POINT_RADIUS, POINT_RADIUS);
        }
        int getX() {
            return x;
        }
        int getY() {
            return y;
        }
    }

    class Poison {
        ArrayList<Point> poison = new ArrayList<>();
        Color color = POISON_COLOR;

        boolean isPoison(int x, int y) {
            for (Point point : poison) {
                if ((point.getX() == x) && (point.getY() == y)) {
                    return true;
                }
            }
            return false;
        }

        void add() {
            int x;
            int y;
            do {
                x = random.nextInt(FIELD_WIDTH);
                //System.out.print("poison x =" + x);
                y = random.nextInt(FIELD_HEIGHT);
                //System.out.print("  poison y = " + y + "\n");
            } while (isPoison(x, y) || snake.isInsideSnake(x, y) || food.isFood(x, y));
            poison.add(new Point(x, y, color));
        }

        void paint(Graphics g) {
            for (Point point : poison) {
                point.paint(g);
            }
        }
    }

    class Food extends Point {
        private Color color = FOOD_COLOR;

        public Food() {
            super(-1, -1);
            super.color = this.color;
        }

        boolean isFood(int x, int y) {
            if ((food.getX() == x) && (food.getY() == y)) {
                return true;
            }
            return false;
        }

        void eat() {
            this.setXY(-1, -1);
        }

        boolean isEaten() {
            return this.getX() == -1;
        }

        void next() {
            int x;
            int y;
            do {
                x = random.nextInt(FIELD_WIDTH);
                //System.out.print("food x = " + x);
                y = random.nextInt(FIELD_HEIGHT);
                //System.out.print("   food y = " + y + "\n");
            } while (snake.isInsideSnake(x, y));
            this.setXY(x, y);
        }
    }

    public class Canvas extends JPanel {

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            snake.paint(g);
            food.paint(g);
            poison.paint(g);
            if (gameOver) {
                g.setColor(new Color(178, 34, 34));
                g.setFont(new Font("Times New Roman", Font.BOLD, 100));
                FontMetrics fm = g.getFontMetrics();
                g.drawString(GAME_OVER_MSG, (FIELD_WIDTH * POINT_RADIUS - fm.stringWidth(GAME_OVER_MSG)) / 2, (FIELD_HEIGHT * POINT_RADIUS) / 2);
            }
        }
    }
}
