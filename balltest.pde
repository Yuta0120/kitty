/** push balls each other
 *  Copyright 2011 Yutaka Kachi
 *  Licensed by New BSD License
 *
 *  è¤‡æ•°ã®ãƒœãƒ¼ãƒ«ãŒã‚¦ã‚£ãƒ³ãƒ‰ã‚¦å†…ã‚’ç§»å‹•ã™ã‚‹
 *  ãƒœãƒ¼ãƒ«åŒå£«ã‚‚ã¶ã¤ã‹ã‚‹ã€‚
 **/

float SPEED = 5; // ç§»å‹•é‡
float R = 10; //åŠå¾„
int NUMBER = 17;  // ãƒœãƒ¼ãƒ«ã®å€‹æ•°
Ball[] balls = new Ball[NUMBER];

void setup() {
  size(250, 250);
  frameRate(20);
  background(0);

  float angle = TWO_PI / NUMBER;
  for (int i = 0; i < NUMBER; i++) {
    float addx = cos(angle * i);
    float addy = sin(angle * i);
    balls[i] = new Ball(
                        width / 2 + addx * 50, height / 2 + addy * 50,
                        SPEED * addx + 1, SPEED * addy  + 1, i, balls);
  }
}

void draw() {
  //background(0);
  fadeToBlack();

  for (int i = 0; i < NUMBER; i++) {
    balls[i].clearVector();
  }
  for (int i = 0; i < NUMBER; i++) {
    Ball ball = (Ball) balls[i];
    ball.collide();
    ball.move();
    ball.draw();
  }
}

class Ball
{
  float x, y; //ãƒœãƒ¼ãƒ«ã®ç¾åœ¨ä½ç½®ï¼ˆä¸­å¿ƒï¼‰
  float vx, vy; //ãƒœãƒ¼ãƒ«ã®ç§»å‹•é‡
  PVector target = new PVector(); //è¡çªæ™‚ã«æŽ’é™¤ã•ã‚Œã‚‹è·é›¢
  PVector impulse = new PVector(1, 1); //åä½œç”¨
  int id; //ãƒœãƒ¼ãƒ«ã®è­˜åˆ¥ç•ªå·
  Ball[] others;

  //ã‚³ãƒ³ã‚¹ãƒˆãƒ©ã‚¯ã‚¿
  Ball(
       float _x, float _y, float _vx, float _vy, int _id, Ball[] _others) {
    x = _x;
    y = _y;
    vx = _vx;
    vy = _vy;
    id = _id;
    others = _others;
  }

  void move() {
    vx *= impulse.x;
    x = x + vx + target.x;

    if (x - R <= 0) {
      x = R;
      vx *= -1;
    }
    if (x + R >= width) {
      x = width - R;
      vx *= -1;
    }

    vy *= impulse.y;
    y = y + vy + target.y;

    if (y - R <= 0) {
      y = R;
      vy *= -1;
    }
    if (y + R >= height) {
      y = height - R;
      vy *= -1;
    }
  }

  void draw() {
    noFill();
    stroke(255);
    ellipse(x, y, R * 2, R * 2);
  }

  void clearVector() {
    target.x = 0;
    target.y = 0;
    impulse.x = 1;
    impulse.y = 1;
  }

  //è¡çªåˆ¤å®š
  void collide() {
    for (int i = id + 1; i < NUMBER; i++) {
      Ball otherBall = (Ball) others[i];

      //ãƒœãƒ¼ãƒ«é–“ã®è·é›¢ã‚’æ±‚ã‚ã‚‹
      float dx = otherBall.x - x;
      float dy = otherBall.y - y;
      float distance =sqrt(dx * dx + dy * dy);

      if (distance <= R * 2) {

        //è·³ã­è¿”ã‚‹è·é›¢ã‚’æ±‚ã‚ã‚‹
        float angle = atan2(dy, dx);
        float push_distance = R * 2 - distance; // / 2;
        float push_x = push_distance * cos(angle);
        float push_y = push_distance * sin(angle);

        target.x -= push_x;
        target.y -= push_y;
        otherBall.target.x += push_x;
        otherBall.target.y += push_y;

        //åç™ºå¾Œã®ç§»å‹•æ–¹å‘ã‚’è¨­å®š
        if ((vx >= 0 && vx - otherBall.vx >= 0) || (vx < 0 && vx - otherBall.vx < 0)) {
          impulse.x = -1;
        }

        if (vx * otherBall.vx <= 0) {
          otherBall.impulse.x = -1;
        }

        if ((vy >= 0 && vy - otherBall.vy >= 0) || (vy < 0 && vy - otherBall.vy < 0)) {
          impulse.y = -1;
        }

        if (vy *  otherBall.vy <= 0) {
          otherBall.impulse.y = -1;
        }
      }
    }
  }
}

//ãƒ•ã‚§ãƒ¼ãƒ‰ã‚¢ã‚¦ãƒˆ
void fadeToBlack() {
  noStroke();
  fill(0, 60);
  rectMode(CORNER);
  rect(0, 0, width, height);
}
