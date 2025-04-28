import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.io.File;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private Timer timer;
    private int birdY = 300;
    private int velocity = 0;
    private final int gravity = 1;
    private ArrayList<Rectangle> pipes;
    private Random random;
    private int score = 0;
    private boolean gameOver = false;
    private Image birdImage, pipeImage, backgroundImage;
    
    public GamePanel() {
        loadAssets();
        timer = new Timer(20, this);
        random = new Random();
        pipes = new ArrayList<>();
        addPipe(); addPipe(); addPipe();
        timer.start();
        addKeyListener(this);
        setFocusable(true);
    }
    
    private void loadAssets() {
        birdImage = new ImageIcon("assets/bird.png").getImage();
        pipeImage = new ImageIcon("assets/pipe.png").getImage();
        backgroundImage = new ImageIcon("assets/background.jpg").getImage();
    }
    
    private void playSound(String filename) {
        try {
            File soundFile = new File("assets/" + filename);
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(soundFile));
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void addPipe() {
        int pipeHeight = 50 + random.nextInt(300);
        int gap = 150;
        int x = 800 + pipes.size() * 300;
        pipes.add(new Rectangle(x, 0, 80, pipeHeight));
        pipes.add(new Rectangle(x, pipeHeight + gap, 80, 600 - pipeHeight - gap));
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            velocity += gravity;
            birdY += velocity;
            
            for (Rectangle pipe : pipes) {
                pipe.x -= 5;
            }
            
            if (pipes.get(0).x + pipes.get(0).width < 0) {
                pipes.remove(0);
                pipes.remove(0);
                addPipe();
                score++;
                playSound("point.wav");
            }
            
            for (Rectangle pipe : pipes) {
                if (pipe.intersects(new Rectangle(100, birdY, 34, 24))) {
                    gameOver = true;
                    playSound("hit.wav");
                }
            }
            
            if (birdY > getHeight() || birdY < 0) {
                gameOver = true;
                playSound("hit.wav");
            }
            
            repaint();
        }
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        
        for (Rectangle pipe : pipes) {
            g.drawImage(pipeImage, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }
        
        g.drawImage(birdImage, 100, birdY, 34, 34, null);
        
        g.setColor(Color.black);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        g.drawString("Score: " + score, 10, 40);
        
        if (gameOver) {
            g.setColor(Color.red);
            g.drawString("Game Over! Press R to restart", 200, 300);
        }
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE && !gameOver) {
            velocity = -10;
            playSound("flap.wav");
        }
        if (e.getKeyCode() == KeyEvent.VK_R && gameOver) {
            birdY = 300;
            velocity = 0;
            pipes.clear();
            score = 0;
            gameOver = false;
            addPipe(); addPipe(); addPipe();
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}
}
