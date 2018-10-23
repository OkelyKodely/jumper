import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Game {

    public Random rd = new Random();
    
    public int score;
    
    public Keyboard keyboard;
    public Bird bird;
    public Castle castle;
    public Background background;
    public Background background2;
    public Ball[] balls;
    public ArrayList<Bomb> bombs;
    public Star[] stars;
    public Plane[] planes;
    
    public Boolean paused;
    public Boolean gameover;
    public Boolean started;

    public int pauseDelay;
    public int restartDelay;
    
    public JFrame frame;
    
    private Thread t;

    public Game (JFrame frame) {
        score = 100;
        
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                runScore();
            }
        });
        t.start();

        keyboard = Keyboard.getInstance();
        castle = new Castle();
        background = new Background();
        background2 = new Background();
        
        balls = new Ball[100];

        stars = new Star[100];
        
        planes = new Plane[100];

        bombs = new ArrayList<Bomb>();

        this.frame = frame;
        
        initBalls();

        initStars();
        
        initPlanes();
        
        restart();
    }
    
    private void runScore() {
        while(true) {
            try {
                Thread.sleep(1000);
            } catch(Exception e) {}
            score--;
        }
    }

    public void update () {

        if (!started && keyboard.isDown(KeyEvent.VK_SPACE)) {
            started = true;
        }

        if (!started)
            return;

        if (pauseDelay > 0)
            pauseDelay--;

        if (keyboard.isDown(KeyEvent.VK_P) && pauseDelay <= 0) {
            paused = !paused;
            pauseDelay = 10;
        }

        if (restartDelay > 0)
            restartDelay--;

        if (keyboard.isDown(KeyEvent.VK_R) && restartDelay <= 0) {
            restart();
            restartDelay = 10;
        }

        if (!paused && !gameover) {

            bird.update();
                
            for(int i=0; i<100; i++) {
                planes[i].x-=3;
                int value = rd.nextInt(232);
                if(value == 1) {
                    bombs.add(new Bomb());
                    bombs.get(bombs.size() - 1).x = planes[i].x;
                    bombs.get(bombs.size() - 1).y = planes[i].y;
                }
            }
            
            for(int i=0; i<bombs.size(); i++) {
                bombs.get(i).y+=3;
                if(bird.x >= bombs.get(i).x && bird.x <= bombs.get(i).x + 59 &&
                        ((bird.y >= bombs.get(i).y && bird.y <= bombs.get(i).y + 80) || 
                        bombs.get(i).y > 348)) {
                    gameover = true;
                }
                if(bombs.get(i).y > 348) {
                    bombs.remove(bombs.get(i));
                }
            }

            for(int i=0; i<100; i++) {
                try {
                    stars[i].update();

                    if(!stars[i].ate && bird.x >= stars[i].x && bird.x <= stars[i].x + 40 && bird.y >= 386) {
                        score += 3;
                        stars[i].ate = true;
                    }
                } catch(Exception e) {}
            }
            
            for(int i=0; i<100; i++) {
                balls[i].update();
                
                if(bird.x >= balls[i].x && bird.x <= balls[i].x + 49 && bird.y >= 386) {
                    gameover = true;
                }
            }
            
            if(gameover || score == 0) {
                JOptionPane.showMessageDialog(frame, "Game Over");
                restart();
            }

            if(castle.x + 210 < bird.x) {
                gameover = true;
                JOptionPane.showMessageDialog(frame, "You win.");
                restart();
            }
            
            if (bird.y + bird.height > App.HEIGHT - 80) {
                
                gameover = false;
                
                bird.y = 388;

                // keep the bird above ground
                bird.y = App.HEIGHT - 80 - bird.height;
            }
        }
    }

    public void restart () {
        score = 100;

        background.x = background2.x = 0;
        
        bird = new Bird(background, background2, balls, stars, castle, planes, bombs);

        paused = false;
        gameover = false;
        started = true;

        pauseDelay = 0;
        restartDelay = 0;

        initBalls();
        
        initStars();
        
        initPlanes();
    }
    
    private void initBalls() {
        Random random = new Random();
        int max;
        max = 0;
        for(int i=0; i<100; i++) {
            balls[i] = new Ball();
            balls[i].x = 500 + i * (random.nextInt(100) + 360);
            if(balls[i].x > max) {
                max = balls[i].x;
            }
        }
        castle.x = max + 200;
    }
    
    private void initStars() {
        Random random = new Random();
        for(int i=0; i<100; i++) {
            stars[i] = new Star();
            stars[i].x = 500 + i*100 + random.nextInt(30000);
        }
    }

    private void initPlanes() {
        Random random = new Random();
        for(int i=0; i<100; i++) {
            planes[i] = new Plane();
            planes[i].x = 500 + i*100 + random.nextInt(30000);
        }
    }

    public ArrayList<Render> getRenders() {
        try {
            ArrayList<Render> renders = new ArrayList<Render>();
            renders.add(background.getRender());
            renders.add(background2.getRender());
            renders.add(bird.getRender());
            for(int i=0; i<100; i++)
                renders.add(balls[i].getRender());
            for(int i=0; i<100; i++)
                if(!stars[i].ate)
                    renders.add(stars[i].getRender());
            for(int i=0; i<100; i++)
                renders.add(planes[i].getRender());
            for(int i=0; i<bombs.size(); i++)
                renders.add(bombs.get(i).getRender());
            renders.add(castle.getRender());
            return renders;
        } catch(Exception e) {
            return null;
        }
    }
}
