import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

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

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class mikumiku1 extends PApplet {


// Taken from: http://www.magicandlove.com/blog/2014/03/06/people-detection-in-processing-with-opencv/

















Capture cap;
PImage small;
HOGDescriptor hog;

byte [] bArray;
int [] iArray;
int pixCnt1, pixCnt2;
int w, h;
float ratio;

Sphere[] spheres;
int numSpheres = 20;

PImage miku1;
PImage miku2;
PImage goku1;
PImage goku2;
PImage tsuru1;
PImage tsuru2;

Rect [] rects;

boolean IsDragged = false;

public void setup() {
  size(640, 480);
  //size(720, 480);
  ratio = 0.5f;
  w = PApplet.parseInt(width*ratio);
  h = PApplet.parseInt(height*ratio);

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
  
  // Setup miku
  miku1  = loadImage("Miku1.jpg" );  
  miku2  = loadImage("Miku2.png" );
  goku1  = loadImage("Goku1.jpg" );  
  goku2  = loadImage("Goku2.jpg" );
  tsuru1 = loadImage("Tsuru1.jpg");  
  tsuru2 = loadImage("Tsuru2.png");
  
  // Setup spheres
  spheres = new Sphere[numSpheres];
  for(int i = 0;i < spheres.length;++i){
    float x  = random(width);
    float y  = 0.0f;
    float vx = random(0.5f,15);
    float vy = 0.0f;       
    PVector loc = new PVector(x , y );
    PVector vel = new PVector(vy, vx);
    PVector acc = new PVector(0, 0);
    float diam = random(5);
    spheres[i] = new Sphere(loc, vel, acc, diam);
  }
  
}

public void draw() {
  if (cap.available()) {
    cap.read();
  }
  else {
    return;
  }

  if(keyPressed == true){
    if(IsDragged == false) IsDragged = true ;
    else                   IsDragged = false;
  }
    
  image(cap, 0, 0);
  // Resize the video to a smaller PImage.
  small.copy(cap, 0, 0, width, height, 0, 0, w, h);
  // Copy the webcam image to the temporary integer array iArray.
  arrayCopy(small.pixels, iArray);

  //background(255); // Make background white so that the rect looks like it's moving    
  
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

  hog.detectMultiScale(m3, found, weight, 0, new Size(8, 8), new Size(32, 32), 1.05f, 2, false);

  rects = found.toArray();
  if (rects.length > 0) {
    for (int i=0; i<rects.length; i++) {
      rect(rects[i].x/ratio, rects[i].y/ratio, rects[i].width/ratio, rects[i].height/ratio);
    }
  }

  if(IsDragged == true){
    for(int i = 0;i < spheres.length;++i){
      spheres[i].Update();
      spheres[i].Show();
    }
  }//if
  text("Frame Rate: " + round(frameRate), 500, 50);

  ellipse(0,0,10,10);
  ellipse(640,0,10,10);
  ellipse(0,480,10,10);  
}

public int myNumber(){
  int ret = -1;
  float x = random(3.0f);
  if((0.0f <= x) && (x < 1.0f)) ret = 0;
  if((1.0f <= x) && (x < 2.0f)) ret = 1;
  if((2.0f <= x) && (x < 3.0f)) ret = 2;  
  return ret;
}

float g_const = 9.80665f * 1e-1f; // Gravity acceleration in earth in m/s^2
//float g_const = 0; // Gravity acceleration in earth in m/s^2
PVector acc_g = new PVector(0, g_const);

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
  }

  public void Update(){

    // Did I hit the face?
    for (int i = 0; i < rects.length; ++i) {
      float x1 = rects[i].x/ratio;
      float x2 = rects[i].x/ratio + rects[i].width/ratio;
      float y1 = rects[i].y/ratio;
      float y2 = rects[i].y/ratio + rects[i].height/ratio;

      // (1) Bound on x1
      {
        double nextx = loc.x + vel.x;
        // If I pass the x1-line from left to right and otherway around
        if((loc.x < x1 && nextx >= x1) || (loc.x >= x1 && nextx < x1)) {
          loc.x = x1;
          vel.x = -vel.x;
          didIhit = true;
        }             
      }//(1)      

      // (2) Bound on y1
      {
        double nexty = loc.y + vel.y;
        // If I pass the y1-line from down to up and otherway around
        if((loc.y < y1 && nexty >= y1) || (loc.y >= y1 && nexty < y1)) {
          loc.y = y1;
          vel.y = -vel.y;
          didIhit = true;          
        }             
      }//(2)      

      // (3) Bound on x2
      {
        double nextx = loc.x + vel.x;
        // If I pass the x2-line from left to right and otherway around
        if((loc.x < x2 && nextx >= x2) || (loc.x >= x2 && nextx < x2)) {
          loc.x = x2;
          vel.x = -vel.x;
          didIhit = true;          
        }             
      }//(3)      

      // (4) Bound on y2
      {
        double nexty = loc.y + vel.y;
        // If I pass the y2-line from down to up and otherway around
        if((loc.y < y2 && nexty >= y2) || (loc.y >= y2 && nexty < y2)) {
          loc.y = y2;
          vel.y = -vel.y;
          didIhit = true;          
        }             
      }//(4)      
      
    }//i

    // (0) Am I in the screen?
    if((loc.x <  width && loc.y <  height) && (loc.x >= 0 && loc.y >= 0)){
      amIinScreen = true;      
    }
    else amIinScreen = false;
    
    print("\n");
    print("? loc.x <   width =" + (loc.x <  width ) + "\n");
    print("? loc.y <   height=" + (loc.y <  height) + "\n");
    print("? loc.x >=  0     =" + (loc.x >= 0     ) + "\n");
    print("? loc.y >=  0     =" + (loc.y >= 0     ) + "\n");
    
    if(amIinScreen){
      loc.add(vel);
      vel.add(acc);
      vel.add(acc_g); // effect of gravity
    }
    else{
//      if(loc.x >  width ) loc.x -= width;
//      if(loc.y >  height) loc.y -= height;
//      if(loc.x <= 0.0   ) loc.x += width;
//      if(loc.y <= 0.0   ) loc.y += height;      

      whatIam  = myNumber();
    
      vel.y = random(0.5f,15);
      vel.x = 0.0f;
      
      loc.x = random(width);
      loc.y = 0.0f;
      
      loc.add(vel);
      vel.add(acc);
      vel.add(acc_g); // effect of gravity

      didIhit = false;
          
    }
  }//End update

  public void Show(){
    fill(255); // Make the face seeable    
    stroke(0, 255, 0); // Make the frame green
    strokeWeight(3); // Make the frame width 3    
//    //ellipse(loc.x, loc.y, diameter, diameter);
//    //ellipse(loc.x, loc.y, 10, 10);
//    image(miku, loc.x, loc.y);
    if(!didIhit){
      if(whatIam == 0) image(miku1,  loc.x, loc.y);
      if(whatIam == 1) image(goku1,  loc.x, loc.y);
      if(whatIam == 2) image(tsuru1, loc.x, loc.y);
    }
    else{
      if(whatIam == 0) image(miku2,  loc.x, loc.y);
      if(whatIam == 1) image(goku2,  loc.x, loc.y);
      if(whatIam == 2) image(tsuru2, loc.x, loc.y);
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
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--full-screen", "--bgcolor=#666666", "--stop-color=#cccccc", "mikumiku1" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
