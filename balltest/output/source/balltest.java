import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class balltest extends PApplet {

/** push balls each other
 *  Copyright 2011 Yutaka Kachi
 *  Licensed by New BSD License
 *
 *  \u00e8\u00a4\u2021\u00e6\u2022\u00b0\u00e3\u0081\u00ae\u00e3\u0192\u0153\u00e3\u0192\u00bc\u00e3\u0192\u00ab\u00e3\u0081\u0152\u00e3\u201a\u00a6\u00e3\u201a\u00a3\u00e3\u0192\u00b3\u00e3\u0192\u2030\u00e3\u201a\u00a6\u00e5\u2020\u2026\u00e3\u201a\u2019\u00e7\u00a7\u00bb\u00e5\u2039\u2022\u00e3\u0081\u2122\u00e3\u201a\u2039
 *  \u00e3\u0192\u0153\u00e3\u0192\u00bc\u00e3\u0192\u00ab\u00e5\u0090\u0152\u00e5\u00a3\u00ab\u00e3\u201a\u201a\u00e3\u0081\u00b6\u00e3\u0081\u00a4\u00e3\u0081\u2039\u00e3\u201a\u2039\u00e3\u20ac\u201a
 **/

float SPEED = 5; // \u00e7\u00a7\u00bb\u00e5\u2039\u2022\u00e9\u2021\u008f
float R = 10; //\u00e5\u008d\u0160\u00e5\u00be\u201e
int NUMBER = 17;  // \u00e3\u0192\u0153\u00e3\u0192\u00bc\u00e3\u0192\u00ab\u00e3\u0081\u00ae\u00e5\u20ac\u2039\u00e6\u2022\u00b0
Ball[] balls = new Ball[NUMBER];

public void setup() {
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

public void draw() {
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
  float x, y; //\u00e3\u0192\u0153\u00e3\u0192\u00bc\u00e3\u0192\u00ab\u00e3\u0081\u00ae\u00e7\u008f\u00be\u00e5\u0153\u00a8\u00e4\u00bd\u008d\u00e7\u00bd\u00ae\u00ef\u00bc\u02c6\u00e4\u00b8\u00ad\u00e5\u00bf\u0192\u00ef\u00bc\u2030
  float vx, vy; //\u00e3\u0192\u0153\u00e3\u0192\u00bc\u00e3\u0192\u00ab\u00e3\u0081\u00ae\u00e7\u00a7\u00bb\u00e5\u2039\u2022\u00e9\u2021\u008f
  PVector target = new PVector(); //\u00e8\u00a1\u009d\u00e7\u00aa\u0081\u00e6\u2122\u201a\u00e3\u0081\u00ab\u00e6\u017d\u2019\u00e9\u2122\u00a4\u00e3\u0081\u2022\u00e3\u201a\u0152\u00e3\u201a\u2039\u00e8\u00b7\u009d\u00e9\u203a\u00a2
  PVector impulse = new PVector(1, 1); //\u00e5\u008f\u008d\u00e4\u00bd\u0153\u00e7\u201d\u00a8
  int id; //\u00e3\u0192\u0153\u00e3\u0192\u00bc\u00e3\u0192\u00ab\u00e3\u0081\u00ae\u00e8\u00ad\u02dc\u00e5\u02c6\u00a5\u00e7\u2022\u00aa\u00e5\u008f\u00b7
  Ball[] others;

  //\u00e3\u201a\u00b3\u00e3\u0192\u00b3\u00e3\u201a\u00b9\u00e3\u0192\u02c6\u00e3\u0192\u00a9\u00e3\u201a\u00af\u00e3\u201a\u00bf
  Ball(
       float _x, float _y, float _vx, float _vy, int _id, Ball[] _others) {
    x = _x;
    y = _y;
    vx = _vx;
    vy = _vy;
    id = _id;
    others = _others;
  }

  public void move() {
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

  public void draw() {
    noFill();
    stroke(255);
    ellipse(x, y, R * 2, R * 2);
  }

  public void clearVector() {
    target.x = 0;
    target.y = 0;
    impulse.x = 1;
    impulse.y = 1;
  }

  //\u00e8\u00a1\u009d\u00e7\u00aa\u0081\u00e5\u02c6\u00a4\u00e5\u00ae\u0161
  public void collide() {
    for (int i = id + 1; i < NUMBER; i++) {
      Ball otherBall = (Ball) others[i];

      //\u00e3\u0192\u0153\u00e3\u0192\u00bc\u00e3\u0192\u00ab\u00e9\u2013\u201c\u00e3\u0081\u00ae\u00e8\u00b7\u009d\u00e9\u203a\u00a2\u00e3\u201a\u2019\u00e6\u00b1\u201a\u00e3\u201a\u0081\u00e3\u201a\u2039
      float dx = otherBall.x - x;
      float dy = otherBall.y - y;
      float distance =sqrt(dx * dx + dy * dy);

      if (distance <= R * 2) {

        //\u00e8\u00b7\u00b3\u00e3\u0081\u00ad\u00e8\u00bf\u201d\u00e3\u201a\u2039\u00e8\u00b7\u009d\u00e9\u203a\u00a2\u00e3\u201a\u2019\u00e6\u00b1\u201a\u00e3\u201a\u0081\u00e3\u201a\u2039
        float angle = atan2(dy, dx);
        float push_distance = R * 2 - distance; // / 2;
        float push_x = push_distance * cos(angle);
        float push_y = push_distance * sin(angle);

        target.x -= push_x;
        target.y -= push_y;
        otherBall.target.x += push_x;
        otherBall.target.y += push_y;

        //\u00e5\u008f\u008d\u00e7\u2122\u00ba\u00e5\u00be\u0152\u00e3\u0081\u00ae\u00e7\u00a7\u00bb\u00e5\u2039\u2022\u00e6\u2013\u00b9\u00e5\u0090\u2018\u00e3\u201a\u2019\u00e8\u00a8\u00ad\u00e5\u00ae\u0161
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

//\u00e3\u0192\u2022\u00e3\u201a\u00a7\u00e3\u0192\u00bc\u00e3\u0192\u2030\u00e3\u201a\u00a2\u00e3\u201a\u00a6\u00e3\u0192\u02c6
public void fadeToBlack() {
  noStroke();
  fill(0, 60);
  rectMode(CORNER);
  rect(0, 0, width, height);
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--full-screen", "--bgcolor=#666666", "--stop-color=#cccccc", "balltest" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
