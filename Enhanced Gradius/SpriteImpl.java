import java.awt.*;
import java.awt.geom.*;

/** Implements Sprite interface
	@author Hrishikesh Vyas
	@author Jeremy H @ Langara for CPSC 1181-003 
	@version 2018-03-02
*/
public abstract class SpriteImpl implements Sprite {

	private final static int WALL_BOUNCE = -6;	//When ship touches any frame border bounce it back by 6 pixels 
	
	// drawing
	private Shape shape;
	private final Color border;
	private final Color fill;

	// movement
	private float dx, dy;
	private final Rectangle2D bounds;
	private final boolean isBoundsEnforced;

	protected SpriteImpl(Shape shape, Rectangle2D bounds, boolean boundsEnforced, Color border, Color fill) {
		this.shape = shape;
		this.bounds = bounds;
		this.isBoundsEnforced = boundsEnforced;
		this.border = border;
		this.fill = fill;
	}
	
	protected SpriteImpl(Shape shape, Rectangle2D bounds, boolean boundsEnforced, Color fill) {
		this(shape, bounds, boundsEnforced, null, fill);
	}

	/**
	returns a shape of the sprite
	
	@return A shape
	*/
	public Shape getShape() {
		return shape;
	}

	/**
	Sets velocity of sprite
	
	@param dx displacement in x direction
	@param dy displacement in y direction
	*/
	public void setVelocity(float dx, float dy) {
		this.dx = dx;
		this.dy = dy;
	}
	
	/**
	Checks if the bounds are enforced and the shape is in bounding box then creates a transformed shape in dx
	and dy translateinstance. If not then it creates transformedshape at -dx and -dy instance
	*/
	public void move() {
		if(isBoundsEnforced && isInBounds()){		//If shape is within bounds and bounds is enforced
			shape = AffineTransform.getTranslateInstance(dx,dy).createTransformedShape(shape);
		}
		//Else section below is Inspired from the hint given by Jeremy in Email : " The easiest fix is to undo a move if it will take it off the edge."
      else {					//else bounce ship back inside the bounding box 
      	shape = AffineTransform.getTranslateInstance((WALL_BOUNCE)*dx,(WALL_BOUNCE)*dy).createTransformedShape(shape);  
      }
	}
	
	/**
	Checks if the shape of the sprite is out of the bounds
	
	@return true if shape is out of bounds or false if it is not
	*/
	public boolean isOutOfBounds() {
      if(!shape.intersects(bounds)){
			return true;
      }
		return false;
	}
	
	public boolean isInBounds() {
		return isInBounds(bounds, shape);
	}
	
	/**
	Checks if the shape is the rectangle bounds
	
	@param bounds the rectangle component
	@param s the shape of the sprite
	
	@return true if it is or else false 
	
	*/
	private static boolean isInBounds(Rectangle2D bounds, Shape s) {
		if(bounds.contains(s.getBounds2D())){
			return true;
		}
		return false;
	}

	/**
	Draws and fills the shape
	
	@param g2 a Graphics2D 
	*/
	public void draw(Graphics2D g2) {
		g2.setColor(fill);
		g2.fill(shape);
		g2.setColor(border);
		g2.draw(shape);
	}

	public boolean intersects(Sprite other) {
		return intersects(other.getShape());
	}
	
	/**
	Checks if bounding box and area of two Shapes are intersecting with each other at any point
	
	@param other A Shape
	
	@return True if the bounding box of the shapes and their areas are intersecting with each other
				or else flase
	*/
	private boolean intersects(Shape other) {
		if(this.getShape().getBounds2D().intersects(other.getBounds2D())){
			if(intersects(new Area(this.getShape()),new Area(other))){
				return true;
			}
		}
		return false;
	}
	
	/**
	Checks if two areas are intersecting with each other
	
	@param a Area of one Shape
	@param b Area of other Shape
	
	@return true if areas intersects or else false 
	*/
	private static boolean intersects(Area a, Area b) {
		//Based on the example of intersect method of Area provided by doc.oracle
		//Link : https://docs.oracle.com/javase/7/docs/api/java/awt/geom/Area.html#intersect(java.awt.geom.Area)
		a.intersect(b);	//Sets the shape of Area a to the shape of the intersecting area of a and b
		if(!a.isEmpty()){	//After setting the shape if area is not empty
			return true;	//That means there is an area of intersection stored in a , so return true
		}
		return false;		//or else return false
	}
}
