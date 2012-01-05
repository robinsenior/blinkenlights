import processing.core.*; import processing.blinkenlights.*; import java.applet.*; import java.awt.*; import java.awt.image.*; import java.awt.event.*; import java.io.*; import java.net.*; import java.text.*; import java.util.*; import java.util.zip.*; public class Blinkentest extends PApplet {

int x = 0;
int y = 0;

public void setup()
{
  //PacketSender mySender = new PacketSender(this,"127.0.0.1",2323);
  BMLWriter bl = new BMLWriter(this, 41);
  bl.showGrid(true);
 
 
  bl.setAuthor("Robin Senior");
  bl.setTitle("Crappy Circles");
  bl.setEmail("senior@gmail.com");
  bl.setURL("www.robinsenior.com"); 

  size(960,320);
  frameRate(30);
  colorMode(RGB,255);
  smooth();
  noStroke();

  //bl.startWriting("circle.bml");
}

public void draw()
{
  background(0);

  fill(255,255,255,128);
  ellipse(x,height/2,150,150);
  ellipse(width-x,height/2,150,150);

  x += 5;
  
  if (x>width)
    x=0;
}

  static public void main(String args[]) {     PApplet.main(new String[] { "Blinkentest" });  }}