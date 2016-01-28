
// Taken from: http://www.magicandlove.com/blog/2014/03/06/people-detection-in-processing-with-opencv/

// Human detection
// Goku
// Miku
// Tsuru
// Ame
// Should be made stable

import processing.video.*;

import java.util.*;
import java.nio.*;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.CvType;
import org.opencv.core.Scalar;
import org.opencv.objdetect.HOGDescriptor;
import org.opencv.core.MatOfRect;
import org.opencv.core.MatOfDouble;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

Capture cap;
PImage small;
HOGDescriptor hog;

byte [] bArray;
int [] iArray;
int pixCnt1, pixCnt2;
int w, h;
float ratio;

Rect [] rects;

Sphere[] spheres;
int numSpheres = 30;

PImage miku1;
PImage miku2;
PImage goku1;
PImage goku2;
PImage tsuru1;
PImage tsuru2;

void setup() {

  size(640, 480);
  //size(720, 480);
  //size(1280, 720);  
  ratio = 0.5;
  w = int(width*ratio);
  h = int(height*ratio);

  if(frame != null){
    frame.setResizable(true);
  }

  size(displayWidth, displayHeight);
  
  ratio = 0.5;
  w = int(width*ratio);
  h = int(height*ratio);

  background(0);
  // Define and initialise the default capture device.
  cap = new Capture(this, width, height);
  cap.start();

  // Load the OpenCV native library.
  System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
  println(Core.VERSION);

  // pixCnt1 is the number of bytes in the pixel buffer.
  // pixCnt2 is the number of integers in the PImage pixels buffer.
  pixCnt1 = w*h*4;
  pixCnt2 = w*h;

  // bArray is the temporary byte array buffer for OpenCV cv::Mat.
  // iArray is the temporary integer array buffer for PImage pixels.
  bArray = new byte[pixCnt1];
  iArray = new int[pixCnt2];

  small = createImage(w, h, ARGB);
  hog = new HOGDescriptor();
  hog.setSVMDetector(HOGDescriptor.getDefaultPeopleDetector());
  noFill();
  stroke(255, 255, 0);

  //////////////////////////////////////////////////
  // Setup miku (if this one is not here, it works)
  //////////////////////////////////////////////////
  miku1  = loadImage("Miku1.png" );  
  miku2  = loadImage("Miku2.png" );
  goku1  = loadImage("Goku1.png" );  
  goku2  = loadImage("Goku2.png" );
  tsuru1 = loadImage("Tsuru1.png");  
  tsuru2 = loadImage("Tsuru2.png");
  
//for ame  ame1   = loadImage("Ame1.png");  
//for ame  ame2   = loadImage("Ame2.png");
  
  // Setup spheres
  spheres = new Sphere[numSpheres];
  for(int i = 0;i < spheres.length;++i){
    float x  = random(width);
    float y  = 0.0;
    float vx = random(0.5,15);
    float vy = 0.0;       
    PVector loc = new PVector(x , y );
    PVector vel = new PVector(vy, vx);
    PVector acc = new PVector(0, 0);
    float diam = random(5);
    spheres[i] = new Sphere(loc, vel, acc, diam);
  }
  ////////////////////////////////////////////////////  
}

void draw() {
  if (cap.available()) {
    cap.read();
  }
  else {
    return;
  }
  image(cap, 0, 0);
  // Resize the video to a smaller PImage.
  small.copy(cap, 0, 0, width, height, 0, 0, w, h);
  // Copy the webcam image to the temporary integer array iArray.
  arrayCopy(small.pixels, iArray);

  // Define the temporary Java byte and integer buffers.
  // They share the same storage.
  ByteBuffer bBuf = ByteBuffer.allocate(pixCnt1);
  IntBuffer iBuf = bBuf.asIntBuffer();

  // Copy the webcam image to the byte buffer iBuf.
  iBuf.put(iArray);

  // Copy the webcam image to the byte array bArray.
  bBuf.get(bArray);

  // Create the OpenCV cv::Mat.
  Mat m1 = new Mat(h, w, CvType.CV_8UC4);

  // Initialise the matrix m1 with content from bArray.
  m1.put(0, 0, bArray);
  // Prepare the grayscale matrix.
  Mat m3 = new Mat(h, w, CvType.CV_8UC1);
  Imgproc.cvtColor(m1, m3, Imgproc.COLOR_BGRA2GRAY);

  MatOfRect found = new MatOfRect();
  MatOfDouble weight = new MatOfDouble();

  hog.detectMultiScale(m3, found, weight, 0, new Size(8, 8), new Size(32, 32), 1.05, 2, false);

  //Rect [] rects = found.toArray();
  rects = found.toArray();  
  if (rects.length > 0) {
    for (int i=0; i<rects.length; i++) {
      // To eliminate frame, comment out this
      rect(rects[i].x/ratio, rects[i].y/ratio, rects[i].width/ratio, rects[i].height/ratio);
    }
  }
  text("Frame Rate: " + round(frameRate), 500, 50);

  //////////////////////////////////////////////
  {
    for(int i = 0;i < spheres.length;++i){
      spheres[i].Update();
      spheres[i].Show();
    }
  }
  //////////////////////////////////////////////
  
  ellipse(0,0,10,10);
  ellipse(displayWidth,0,10,10);
  ellipse(0,displayHeight,10,10);
}

///////////////////////////
// Sphere
///////////////////////////

//float g_const = 9.80665 * 1e-1; // Gravity acceleration in earth in m/s^2
float g_const = 1; // Gravity acceleration in earth in m/s^2
PVector acc_g = new PVector(0, g_const);

int myNumber(){
  int ret = -1;
  float x = random(3.0);
  //float x = random(4.0);  // for ame
  if((0.0 <= x) && (x < 1.0)) ret = 0;
  if((1.0 <= x) && (x < 2.0)) ret = 1;
  if((2.0 <= x) && (x < 3.0)) ret = 2;
  //if((3.0 <= x) && (x < 4.0)) ret = 3; // for ame    
  return ret;
}

class Sphere{
  PVector loc;     // location
  PVector vel;     // velocity
  PVector acc;     // Acceleration
  float diameter;  // Diameter of the circle
  float mass;      // Weight
  int whatIam;     // 0=>miku, 1=>goku, 2=>tsuru
  boolean amIinScreen;// true=> in screen, false=>not in screen
  boolean didIhit; // Hit or not
  
  // (x,y)    = (x0, y0)
  // (dx,dy)  = (v_x0, v_y0)
  // (ax, ay) = (a_x0, a_y0)
  Sphere(PVector loc0, PVector vel0, PVector acc0, float diam){
    loc      = loc0;
    vel      = vel0;
    acc      = acc0;
    diameter = diam;
    whatIam  = myNumber();
    didIhit  = false;
  }

  void Update(){

    // Did I hit the face?
    for (int i = 0; i < rects.length; ++i) {
      float x1 = rects[i].x/ratio;
      float x2 = rects[i].x/ratio + rects[i].width/ratio;
      float y1 = rects[i].y/ratio;
      float y2 = rects[i].y/ratio + rects[i].height/ratio;
      double nexty = loc.y + vel.y;
      
      // (1) point is inside human
      if((x1 <= loc.x) && (loc.x <= x2) && (y1 <= loc.y) && (loc.y <= y1)){
          loc.y = y1;
          vel.x = random(-50,50);
          vel.y = -vel.y;          
          didIhit = true;        
      }

      // (1) point is inside human
      if((x1 <= loc.x) && (loc.x <= x2) && (y1 <= nexty) && (loc.y <= y1)){
          loc.y = y1;
          vel.x = random(-50,50);
          vel.y = -vel.y;          
          didIhit = true;        
      }
            
    }//i

    // (0) Am I in the screen?
    if((loc.x <  displayWidth && loc.y <  displayHeight) && (loc.x >= 0 && loc.y >= 0)){
      amIinScreen = true;      
    }
    else amIinScreen = false;
    
    print("\n");
    print("? loc.x <   width =" + (loc.x <  displayWidth ) + "\n");
    print("? loc.y <   height=" + (loc.y <  displayHeight) + "\n");
    print("? loc.x >=  0     =" + (loc.x >= 0            ) + "\n");
    print("? loc.y >=  0     =" + (loc.y >= 0            ) + "\n");
    
    if(amIinScreen){
      loc.add(vel);
      vel.add(acc);
      vel.add(acc_g); // effect of gravity
    }
    else{
      whatIam  = myNumber();
    
      vel.y = random(0.5,15);
      vel.x = 0.0;
      
      loc.x = random(width);
      loc.y = 0.0;
      
      loc.add(vel);
      vel.add(acc);
      vel.add(acc_g); // effect of gravity

      didIhit = false;
          
    }
  }//End update

  void Show(){
    //fill(255); // Make the face seeable    
    //stroke(0, 255, 0); // Make the frame green
    //strokeWeight(3); // Make the frame width 3    
    //ellipse(loc.x, loc.y, 10, 10);
    
    if(!didIhit){
      if(whatIam == 0) image(miku1,  loc.x, loc.y);
      if(whatIam == 1) image(goku1,  loc.x, loc.y);
      if(whatIam == 2) image(tsuru1, loc.x, loc.y);
      //if(whatIam == 3) image(ame1, loc.x, loc.y); // for ame
    }
    else{
      if(whatIam == 0) image(miku2,  loc.x, loc.y);
      if(whatIam == 1) image(goku2,  loc.x, loc.y);
      if(whatIam == 2) image(tsuru2, loc.x, loc.y);
      //if(whatIam == 3) image(ame2, loc.x, loc.y); // for ame
    }
    
    // Did I hit the face?
    for (int i = 0; i < rects.length; ++i) {
      ellipse(rects[i].x/ratio                     , rects[i].y/ratio                      , 5, 5);            
      ellipse(rects[i].x/ratio                     , rects[i].y/ratio+rects[i].height/ratio, 5, 5);            
      ellipse(rects[i].x/ratio+rects[i].width/ratio, rects[i].y/ratio                      , 5, 5);
      ellipse(rects[i].x/ratio+rects[i].width/ratio, rects[i].y/ratio+rects[i].height/ratio, 5, 5);            
    }
    
  }
}
