import processing.core.*; import processing.blinkenlights.*; import java.applet.*; import java.awt.*; import java.awt.image.*; import java.awt.event.*; import java.io.*; import java.net.*; import java.text.*; import java.util.*; import java.util.zip.*; public class BlinkenCube extends PApplet {


/**
 * RGB Cube.
 * 
 * The three primary colors of the additive color model are red, green, and blue.
 * This RGB color cube displays smooth transitions between these colors. 
 */
 
float xmag, ymag = 0;
float newXmag, newYmag = 0; 
 
public void setup() 
{ 
  BMLWriter bl = new BMLWriter(this, 41);
  bl.showGrid(true);
  
  bl.setAuthor("Robin Senior");
  bl.setTitle("3D Cube");
  bl.setEmail("senior@gmail.com");
  bl.setURL("www.robinsenior.com");
  
  size(960, 320, P3D); 
  noStroke(); 
  colorMode(RGB, 1); 
  //bl.startWriting("cube.bml");
} 
 
public void draw() 
{
  
  background(0);
  
  pushMatrix(); 
 
  translate(700, height/2, -30); 
  
  newXmag = mouseX/PApplet.parseFloat(width) * TWO_PI;
  newYmag = mouseY/PApplet.parseFloat(height) * TWO_PI;
  
  float diff = xmag-newXmag;
  if (abs(diff) >  0.01f) { xmag -= diff/4.0f; }
  
  diff = ymag-newYmag;
  if (abs(diff) >  0.01f) { ymag -= diff/4.0f; }
  
  rotateX(-ymag); 
  rotateY(-xmag); 
  
  scale(50);
  beginShape(QUADS);

  fill(0, 1, 1); vertex(-1,  1,  1);
  fill(1, 1, 1); vertex( 1,  1,  1);
  fill(1, 0, 1); vertex( 1, -1,  1);
  fill(0, 0, 1); vertex(-1, -1,  1);

  fill(1, 1, 1); vertex( 1,  1,  1);
  fill(1, 1, 0); vertex( 1,  1, -1);
  fill(1, 0, 0); vertex( 1, -1, -1);
  fill(1, 0, 1); vertex( 1, -1,  1);

  fill(1, 1, 0); vertex( 1,  1, -1);
  fill(0, 1, 0); vertex(-1,  1, -1);
  fill(0, 0, 0); vertex(-1, -1, -1);
  fill(1, 0, 0); vertex( 1, -1, -1);

  fill(0, 1, 0); vertex(-1,  1, -1);
  fill(0, 1, 1); vertex(-1,  1,  1);
  fill(0, 0, 1); vertex(-1, -1,  1);
  fill(0, 0, 0); vertex(-1, -1, -1);

  fill(0, 1, 0); vertex(-1,  1, -1);
  fill(1, 1, 0); vertex( 1,  1, -1);
  fill(1, 1, 1); vertex( 1,  1,  1);
  fill(0, 1, 1); vertex(-1,  1,  1);

  fill(0, 0, 0); vertex(-1, -1, -1);
  fill(1, 0, 0); vertex( 1, -1, -1);
  fill(1, 0, 1); vertex( 1, -1,  1);
  fill(0, 0, 1); vertex(-1, -1,  1);

  endShape();
  
  popMatrix(); 
} 
  static public void main(String args[]) {     PApplet.main(new String[] { "BlinkenCube" });  }}