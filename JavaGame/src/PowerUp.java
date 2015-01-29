import java.awt.*;

public class PowerUp {

 
 //FIELDS
 private double x;
 private double y;
 private int r; 
 //* No "dx" and "dy" because the PowerUps are moving only down.
 //* The "PowerUps" are squares. The "Enemies" are circles. 
 
 private int type;
 //* Have "type" for different types of PowerUps.
 
 private Color color1;
 //* We need different colors for every type.
 
 // 1 -- +1 life
 // 2 -- +1 power
 // 3 -- +2 power
 // 4 -- slow down time
 //* The above comments are in regard to the 4 different PowerUps types.
 //* Type 1 will pop up less.
 
 
 //CONSTRUCTOR
 public PowerUp(int type, double x, double y){
  
  this.type = type;
  this.x = x;
  this.y = y;
  
  if(type == 1){
   color1 = Color.PINK;
   r = 3;
  }
  if (type == 2) {
   color1 = Color.YELLOW;
   r = 3;
  }
  if (type == 3) {
   color1 = Color.YELLOW;
   r = 5;
  }
  if (type == 4) {
   color1 = Color.WHITE;
   r = 3;
  }
  //* We need different colors for every type.
  //* "r" sets the radius of the types.
 }
 
 //FUNCTIONS
 public double getx() { return x; }
 public double gety() { return y; } 
 public double getr() { return r; }
 
 public int getType() { return type; }
 //* Different types of PowerUps.
 
 public boolean update(){
  //* Traveling only downwards at slower speed.
  //* We are using boolean because we want to remove it.
  y += 2;
  
  if (y > GamePanel.HEIGHT + r) {
   return true; //* if we remove it.
  }
  
  return false;
 }
 
 public void draw(Graphics2D g){
  
  g.setColor(color1);
  g.fillRect((int) (x - r), (int) (y - r), 2 * r, 2 * r);
  //*it fills the rectangles.
  
  g.setStroke(new BasicStroke(3));
  g.setColor(color1.darker());//* set border colors.
  g.drawRect((int) (x - r), (int) (y - r), 2 * r, 2 * r);
  g.setStroke(new BasicStroke(1));
  
 }
}
 