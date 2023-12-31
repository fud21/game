//20215095 장채령
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Game {

    static class MyFrame extends JFrame {

        static int BALL_WIDTH = 20;
        static int BALL_HEIGHT = 20;
        static int BLOCK_ROWS = 5;
        static int BLOCK_COLUMNS = 10;
        static int BLOCK_WIDTH = 40;
        static int BLOCK_HEIGHT = 20;
        static int BLOCK_GAP = 3;
        static int BAR_WIDTH = 100;
        static int BAR_HEIGHT = 10;
        static int CANVAS_WIDTH = 400 + (BLOCK_GAP * BLOCK_COLUMNS) - BLOCK_GAP;
        static int CANVAS_HEIGHT = 600;
        static MyPanel myPanel;
        static int score = 0;
        static Timer timer;
        static Block[][] blocks = new Block[BLOCK_ROWS][BLOCK_COLUMNS];
        static Bar bar = new Bar();
        static Ball ball = new Ball();
        static int barXTarget = bar.x;
        static int dir = 0; //0 : Up-Right 1 : Down-Right 2 : Up-Left 3 : Down-Left
        static int ballSpeed = 3;
        static boolean isGameFinish = false;
        static boolean isGameStarted = false;
        static JButton startButton;
        static JButton instructionsButton;
        static JButton endGameButton;
        static JLabel instructionLabel;
        static JPanel buttonPanel; // start, instruction
        static JPanel buttonPanel2; // end 
        static JLabel remainingTimeLabel;
        static int gameTimeLimit = 30;
        static int remainingTime = gameTimeLimit;

        static class Ball {
            int x = CANVAS_WIDTH / 2 - BALL_WIDTH / 2;
            int y = CANVAS_HEIGHT / 2 - BALL_HEIGHT / 2;
            int width = BALL_WIDTH;
            int height = BALL_HEIGHT;
            
            // 공의 좌표 반환 - 충돌 검사
            Point getCenter() {
                return new Point(x + (BALL_WIDTH / 2), y + (BALL_HEIGHT / 2));
            }

            Point getBottomCenter() {
                return new Point(x + (BALL_WIDTH / 2), y + (BALL_HEIGHT));
            }

            Point getTopCenter() {
                return new Point(x + (BALL_WIDTH / 2), y);
            }

            Point getLeftCenter() {
                return new Point(x, y + (BALL_HEIGHT / 2));
            }

            Point getRightCenter() {
                return new Point(x + (BALL_WIDTH), y + (BALL_HEIGHT / 2));
            }
        }

        static class Bar {
            int x = CANVAS_WIDTH / 2 - BAR_WIDTH / 2;
            int y = CANVAS_HEIGHT - 100;
            int width = BAR_WIDTH;
            int height = BAR_HEIGHT;
        }

        static class Block {
            int x = 0;
            int y = 0;
            int width = BLOCK_WIDTH;
            int height = BLOCK_HEIGHT;
            int color = 0;
            boolean isHidden = false; // 충돌하면 사라짐
        }

        static class MyPanel extends JPanel { 

            private int currentState; // 0: Start Screen, 1: Game Screen, 2: Game Over Screen 3: Game Instruction Screen

            private void drawStartScreen(Graphics2D g2d) {
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("TimesRoman", Font.BOLD, 40));
                g2d.drawString("Block Game", 100, 200);

                g2d.setFont(new Font("TimesRoman", Font.PLAIN, 15));
                g2d.drawString("Press SPACE to Start", 138, 230);
                g2d.drawString("Press I for Instructions", 138, 260);
                
                endGameButton.setVisible(false);
              
            }
            
            private void drawGameScreen(Graphics2D g2d) {
                // blocks
                for (int i = 0; i < BLOCK_ROWS; i++) {
                    for (int j = 0; j < BLOCK_COLUMNS; j++) {
                        if (blocks[i][j].isHidden) {
                            continue;
                        }
                        if (blocks[i][j].color == 0) {
                            g2d.setColor(Color.RED);
                        } else if (blocks[i][j].color == 1) {
                            g2d.setColor(Color.ORANGE);
                        } else if (blocks[i][j].color == 2) {
                            g2d.setColor(Color.YELLOW);
                        } else if (blocks[i][j].color == 3) {
                            g2d.setColor(Color.GREEN);
                        } else if (blocks[i][j].color == 4) {
                            g2d.setColor(Color.CYAN);
                        }
                        g2d.fillRect(blocks[i][j].x, blocks[i][j].y, blocks[i][j].width, blocks[i][j].height);
                    }

                    // score
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("TimesRoman", Font.BOLD, 20));
                    g2d.drawString("Score: " + score, 165, 20);
                    // remaining Time
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("Arial", Font.PLAIN, 16));
                    g2d.drawString("Time: " + remainingTime + " sec", 10, 30);
                }

                // Ball
                g2d.setColor(Color.WHITE);
                g2d.fillOval(ball.x, ball.y, BALL_WIDTH, BALL_HEIGHT);

                // Bar
                g2d.setColor(Color.WHITE);
                g2d.fillRect(bar.x, bar.y, bar.width, bar.height);
            }
            
            private void drawGameOverScreen(Graphics2D g2d) {
                g2d.setColor(Color.RED);
                g2d.setFont(new Font("TimesRoman", Font.BOLD, 30));
                g2d.drawString("Game Over!", 130, 250);

                g2d.setFont(new Font("TimesRoman", Font.PLAIN, 20));
                g2d.drawString("Score : " + score, 165, 300);
               
                buttonPanel.setVisible(false);
                instructionLabel.setVisible(false);
                buttonPanel2.setVisible(false);
                
            }
            
            private void drawGameInstructionsScreen(Graphics2D g2d) {
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("TimesRoman", Font.BOLD, 20));
                g2d.drawString("게임 설명", 30, 120);


                g2d.setFont(new Font("TimesRoman", Font.PLAIN, 15));
                g2d.drawString("1. bar를 사용해 공을 튕겨서 벽돌을 깨는 것입니다. ", 30, 170);
                g2d.drawString("2. 게임이 시작하면 벽돌 50개가 5줄로 놓여있습니다.", 30, 200);
                g2d.drawString("- 벽돌은 위로 갈수록 점수가 높습니다.", 30, 220);
                g2d.drawString("3. 30초 안에 최대한 많은 점수를 획득하세요!", 30, 250);
                g2d.drawString("- 공이 바닥에 떨어지면 게임이 종료됩니다.", 30, 270);
                g2d.drawString("- 30초가 지나면 게임이 종료됩니다.", 30, 290);
                g2d.setFont(new Font("TimesRoman", Font.PLAIN, 20));
                g2d.drawString("Good luck!", 30, 325);

                startButton.setBounds(CANVAS_WIDTH / 2 - 75, CANVAS_HEIGHT - 100, 150, 30);
                startButton.setFont(new Font("TimesRoman", Font.PLAIN, 20));
                startButton.setText("Start Game");
                startButton.setVisible(true);
                
                endGameButton.setVisible(false);
            }
            
            public MyPanel() {
                this.setSize(CANVAS_WIDTH, CANVAS_HEIGHT);
                this.setBackground(Color.BLACK);

                currentState = 0;
            }

            public void setCurrentState(int state) {  // 화면
                currentState = state;
            }

            @Override
            
            public void paint(Graphics g) {
                super.paint(g);
                Graphics2D g2d = (Graphics2D) g;
                drawUI(g2d);
            }

            private void drawUI(Graphics2D g2d) {
                if (currentState == 0) { // Start Screen
                    drawStartScreen(g2d);
                } else if (currentState == 1) { // Game Screen
                    drawGameScreen(g2d);
                } else if (currentState == 2) { // Game Over Screen
                    drawGameOverScreen(g2d);
                } else if (currentState == 3) { // Game Instruction Screen
                   drawGameInstructionsScreen(g2d);
                }
            }

            

            
        }

        public MyFrame(String title) {
            super(title);
            this.setVisible(true);
            this.setSize(CANVAS_WIDTH, CANVAS_HEIGHT);
            this.setLocation(400, 300);
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            myPanel = new MyPanel();
            this.add("Center", myPanel);

            buttonPanel = new JPanel();
            buttonPanel2 = new JPanel();
            startButton = new JButton("Start Game");
            instructionsButton = new JButton("Game Instructions");
            endGameButton = new JButton("End Game");
            
            instructionLabel = new JLabel();
            instructionLabel.setForeground(Color.WHITE);

            buttonPanel.add(startButton);
            buttonPanel.add(instructionsButton);
            buttonPanel.add(instructionLabel);
            buttonPanel2.add(endGameButton);

            this.add(buttonPanel, BorderLayout.SOUTH);
            this.add(buttonPanel2, BorderLayout.NORTH);
            
            setButtonListeners();
            setKeyListener(); 

            this.setVisible(true);
            this.setFocusable(true);
            
            
            initData();
        }
            
            private void gameover() {
                isGameFinish = true;
                timer.stop();
                myPanel.setCurrentState(2); // 게임종료화면으로 전환
                buttonPanel.setVisible(true);
                isGameStarted = false;
                myPanel.repaint();
          
            
        }
            private void initGameUI() {
                // 남은 시간을 표시할 레이블 생성 및 초기화
                remainingTimeLabel = new JLabel();
                remainingTimeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
                remainingTimeLabel.setForeground(Color.WHITE);
                remainingTimeLabel.setBounds(10, 10, 200, 30); // 위치와 크기 설정
                myPanel.add(remainingTimeLabel); // 레이블을 게임 화면에 추가
            }
        

        public void setButtonListeners() {
            startButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!isGameStarted) {
                        isGameStarted = true;
                        myPanel.setCurrentState(1);  // 화면 전환
                        startTimer();
                        buttonPanel.setVisible(false);
                        instructionLabel.setVisible(false);
                        myPanel.repaint();
                        endGameButton.setVisible(true); 
                    }
                }
            });

            instructionsButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!isGameStarted) {
                        myPanel.setCurrentState(3);  // 화면 전환
                        instructionLabel.setVisible(true);
                        myPanel.repaint();
                        endGameButton.setVisible(true); 
                    }
                }
            });
            endGameButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (isGameStarted) {
                        isGameStarted = false; 
                        isGameFinish = true;
                        timer.stop();
                        myPanel.setCurrentState(2); // 화면 전환
                        buttonPanel.setVisible(true);
                        instructionLabel.setVisible(false);
                        myPanel.repaint();
                    }
                }
            });
        }

        
        public void setKeyListener() {
            this.setFocusable(true); 
            this.addKeyListener(new KeyAdapter() {
                @Override   
                    public void keyPressed(KeyEvent e) { // Key Event
                        if (!isGameStarted) { // 시작화면
                            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                                isGameStarted = true;
                                myPanel.setCurrentState(1); // 화면 전환
                                startTimer();
                                buttonPanel.setVisible(false);
                                instructionLabel.setVisible(false);
                                myPanel.repaint();
                            } else if (e.getKeyCode() == KeyEvent.VK_I) {
                                myPanel.setCurrentState(3); // 화면 전환
                                instructionLabel.setVisible(true);
                                myPanel.repaint();
                            }
                        } else { // 게임 진행 화면
                            if (e.getKeyCode() == KeyEvent.VK_LEFT) {  // 방향키를 누르면 bar가 이동
                                System.out.println("Pressed Left Key");
                                if (bar.x >= 20) { 
                                    barXTarget = bar.x - 20;
                                }
                            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                                System.out.println("Pressed Right Key");
                                if (bar.x <= CANVAS_WIDTH - bar.width - 20) { 
                                    barXTarget = bar.x + 20;
                                }
                            }
                        }
                    }
                });
            }
        public void initData() {  // 초기화
           isGameFinish = false;
            for (int i = 0; i < BLOCK_ROWS; i++) {
                for (int j = 0; j < BLOCK_COLUMNS; j++) {
                    blocks[i][j] = new Block();
                    blocks[i][j].x = BLOCK_WIDTH * j + BLOCK_GAP * j;
                    blocks[i][j].y = 100 + BLOCK_HEIGHT * i + BLOCK_GAP * i;
                    blocks[i][j].width = BLOCK_WIDTH;
                    blocks[i][j].height = BLOCK_HEIGHT;
                    blocks[i][j].color = 4 - i;
                    blocks[i][j].isHidden = false;
                    
                }
            }
            initGameUI();
            endGameButton.setVisible(true); 
        }

        public void startTimer() { // 타이머
            timer = new Timer(20, new ActionListener() { // 게임 루프 타이머
                @Override
                public void actionPerformed(ActionEvent e) { 
                    movement();
                    checkCollision();
                    checkCollisionBlock();
                    myPanel.repaint();
                    isGameFinish(); 
                    
                    if (remainingTime <= 0) {
                        gameover();  
                    }
  
                }
            });
            timer.start();
            
            Timer gameTimer = new Timer(1000, new ActionListener() { // 남은시간 타이머
                @Override
                public void actionPerformed(ActionEvent e) {
                   remainingTime--;
                    if (remainingTime <= 0) {
                        gameover();
                    }
                }
            });
            gameTimer.start();
        
        }

        public void isGameFinish() {   // 게임 종료
            if (isGameFinish) {
                return;
            }
            int count = 0;
            for (int i = 0; i < BLOCK_ROWS; i++) {
                for (int j = 0; j < BLOCK_COLUMNS; j++) {
                    Block block = blocks[i][j];
                    if (block.isHidden)
                        count++;
                }
            }
            if (count == BLOCK_ROWS * BLOCK_COLUMNS) {
                isGameFinish = true;
                timer.stop();
                myPanel.setCurrentState(2);  // 게임종료화면 전환
                buttonPanel.setVisible(false);
                isGameStarted = false;
                myPanel.repaint();
            }
        }


        public void movement() {  // 바와 공의 움직임
            if (bar.x < barXTarget) {  // 바가 오른쪽으로
                bar.x += 5;
            } else if (bar.x > barXTarget) {  // 왼쪽으로 
                bar.x -= 5;
            }

            if (dir == 0) { // 공의 방향이 0이면 오른쪼 위로
                ball.x += ballSpeed;
                ball.y -= ballSpeed;
            } else if (dir == 1) { // 오른쪽 아래로
                ball.x += ballSpeed;
                ball.y += ballSpeed;
            } else if (dir == 2) { // 왼쪽 위로
                ball.x -= ballSpeed;
                ball.y -= ballSpeed;
            } else if (dir == 3) { // 왼쪽 아래로
                ball.x -= ballSpeed;
                ball.y += ballSpeed;
            }

        }


        public boolean duplRect(Rectangle rect1, Rectangle rect2) {  // 사각형이 겹치는 지
            return rect1.intersects(rect2); 
        }

        public void checkCollision() { // 공의 충돌 (화면, 바)
            if (dir == 0) { // Up-Right
                if (ball.y < 0) { // 공이 상단 경계를 넘어가면
                    dir = 1; // 방향을 Down-Right로
                }
             // 공이 우측 경계를 넘어가면
                if (ball.x > CANVAS_WIDTH - BALL_WIDTH) { 
                    dir = 2; // 방향을 Up-Left로 
                }
            } else if (dir == 1) { // Down-Right
                // 공이 우측 경계를 넘어가면
                if (ball.x > CANVAS_WIDTH - BALL_WIDTH) {
                    dir = 3; // 방향을 Down-Left로 
                }
                // bar와 충돌하면
                if (ball.getBottomCenter().y >= bar.y) {
                    if (duplRect(new Rectangle(ball.x, ball.y, ball.width, ball.height),
                            new Rectangle(bar.x, bar.y, bar.width, bar.height))) {
                        dir = 0; // 방향을 Up-Right로
                    }
                }
            } else if (dir == 2) { // 2 : Up-Left
                if (ball.y < 0) { // 공이 상단 경계를 넘어가면
                    dir = 3; // 방향을 Down-Left로 
                }
                if (ball.x < 0) { // 공이 좌측 경계를 넘어가면
                    dir = 0; // 방향을 Up-Right로
                }
            } else if (dir == 3) { // 3 : Down-Left
                if (ball.x < 0) { // 공이 좌측 경계를 넘어가면
                    dir = 1; // 방향을 Down-Right로
                }
             // bar와 충돌하면
                if (ball.getBottomCenter().y >= bar.y) {
                    if (duplRect(new Rectangle(ball.x, ball.y, ball.width, ball.height),
                            new Rectangle(bar.x, bar.y, bar.width, bar.height))) {
                        dir = 2;
                    }
                }
            }
            // 공이 바닥 경계를 넘어가면
            if (ball.y > CANVAS_HEIGHT - BALL_HEIGHT) {
                gameover(); // 게임 종료
            }
        }

        public void checkCollisionBlock() { // 벽돌에 충돌할 때
           if(isGameFinish) { 
              return;
           }
           // 모든 블록 충돌 확인
            for (int i = 0; i < BLOCK_ROWS; i++) {
                for (int j = 0; j < BLOCK_COLUMNS; j++) {
                    Block block = blocks[i][j]; 
                    if (block.isHidden == false) { // 이미 사라진 블록은 패스
                        if (dir == 0) { // 0 : Up-Right
                           // 공과 블록이 충돌하면
                            if (duplRect(new Rectangle(ball.x, ball.y, ball.width, ball.height),
                                    new Rectangle(block.x, block.y, block.width, block.height))) {
                                if (ball.x > block.x + 2 && ball.getRightCenter().x <= block.x + block.width - 2) {
                                    dir = 1; // 방향 변경
                                } else {
                                    dir = 2; // 방향 변경
                                }
                                block.isHidden = true; // 충돌하면 블록 사라짐
                                // 충돌한 블록에 따라 점수 다르게 설정
                                if (block.color == 0) {
                                    score += 10;
                                } else if (block.color == 1) {
                                    score += 20;
                                } else if (block.color == 2) {
                                    score += 30;
                                } else if (block.color == 3) {
                                    score += 40;
                                } else if (block.color == 4) {
                                    score += 50;
                                }                       
                            }
                        } else if (dir == 1) { // 1 : Down-Right
                            if (duplRect(new Rectangle(ball.x, ball.y, ball.width, ball.height),
                                    new Rectangle(block.x, block.y, block.width, block.height))) {
                                if (ball.x > block.x + 2 && ball.getRightCenter().x <= block.x + block.width - 2) {
                                    // 블록 위쪽과 충돌
                                    dir = 0;
                                } else { // 블록 왼쪽과 충돌
                                    dir = 3;
                                }
                                block.isHidden = true;
                                if (block.color == 0) {
                                    score += 10;
                                } else if (block.color == 1) {
                                    score += 20;
                                } else if (block.color == 2) {
                                    score += 30;
                                } else if (block.color == 3) {
                                    score += 40;
                                } else if (block.color == 4) {
                                    score += 50;
                                }
                            }
                        } else if (dir == 2) { // 2 : Up-Left
                            if (duplRect(new Rectangle(ball.x, ball.y, ball.width, ball.height),
                                    new Rectangle(block.x, block.y, block.width, block.height))) {
                                if (ball.x > block.x + 2 && ball.getRightCenter().x <= block.x + block.width - 2) {
                                    // 블록 바닥과 충돌
                                    dir = 3;
                                } else { // 블록 오르쪽과 충돌
                                    dir = 0;
                                }
                                block.isHidden = true;
                                if (block.color == 0) {
                                    score += 10;
                                } else if (block.color == 1) {
                                    score += 20;
                                } else if (block.color == 2) {
                                    score += 30;
                                } else if (block.color == 3) {
                                    score += 40;
                                } else if (block.color == 4) {
                                    score += 50;
                                }
                            }
                        } else if (dir == 3) { // 3 : Down-Left
                            if (duplRect(new Rectangle(ball.x, ball.y, ball.width, ball.height),
                                    new Rectangle(block.x, block.y, block.width, block.height))) {
                                if (ball.x > block.x + 2 && ball.getRightCenter().x <= block.x + block.width - 2) {
                                    // 블록 위쪽과 충돌
                                    dir = 2;
                                } else { // 블록 왼쪽과 충돌
                                    dir = 1;
                                }
                                block.isHidden = true;
                                if (block.color == 0) {
                                    score += 10;
                                } else if (block.color == 1) {
                                    score += 20;
                                } else if (block.color == 2) {
                                    score += 30;
                                } else if (block.color == 3) {
                                    score += 40;
                                } else if (block.color == 4) {
                                    score += 50;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        new MyFrame("Block Game");
    }

}