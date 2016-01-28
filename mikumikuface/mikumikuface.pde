import gab.opencv.*;
import processing.video.*;
import java.awt.*;

Capture video;
OpenCV opencv;

Rectangle[] faces;
PImage miku;
  
void setup() {
  
  size(640, 480);
  
  //if(frame != null){
    //frame.setResizable(true);
  //}

  //size(displayWidth, displayHeight);
  
  video = new Capture(this, width/2, height/2);
  opencv = new OpenCV(this, width/2, height/2);
  opencv.loadCascade(OpenCV.CASCADE_FRONTALFACE);  
  
  video.start();

  frameRate(50);
  noStroke();

  miku = loadImage("Miku1.jpg" );    
}

void draw() {
  scale(2);
  opencv.loadImage(video);

  //background(0); // Make background black so that the rect looks like it's moving
  //background(255); // Make background white so that the rect looks like it's moving    
  image(video, 0, 0 ); // Every time the image from the vide is set as background

  //fill(0); // Makes the face black
  //fill(255); // Makes the face white
  noFill(); // Make the face seeable
  
  stroke(0, 255, 0); // Make the frame green
  strokeWeight(3); // Make the frame width 3
  //Rectangle[] faces = opencv.detect();
  faces = opencv.detect();  
  println(faces.length);

  for (int i = 0; i < faces.length; i++) {
    //rect(faces[i].x, faces[i].y, faces[i].width, faces[i].height);
    image(miku, faces[i].x, faces[i].y, faces[i].width, faces[i].height);
  }
  
}

void captureEvent(Capture c) {
  c.read();
}

