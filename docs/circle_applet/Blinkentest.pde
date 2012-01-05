import processing.blinkenlights.*;

int x = 0;
int y = 0;

void setup()
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

void draw()
{
  background(0);

  fill(255,255,255,128);
  ellipse(x,height/2,150,150);
  ellipse(width-x,height/2,150,150);

  x += 5;
  
  if (x>width)
    x=0;
}

