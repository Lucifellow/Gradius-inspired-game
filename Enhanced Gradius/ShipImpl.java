import java.awt.Color;
import java.awt.Polygon;
import java.awt.geom.Rectangle2D;
/** Implements Ship interface and extends SpriteImpl
	@author Hrishikesh Vyas
	@author Jeremy H @ Langara for CPSC 1181-003 
	@version 2018-03-02
*/
public class ShipImpl  extends SpriteImpl implements Ship {

	private final static Color FILL = Color.GREEN;
	private final static Color BORDER = Color.BLACK;

	private final static int HEIGHT = 20;
	private final static int WIDTH = HEIGHT;

	public ShipImpl(int x, int y, Rectangle2D moveBounds) {
		super(new Polygon(new int[]{x,x+WIDTH,x},new int[]{y,y+(HEIGHT/2),y+HEIGHT},3),  
      moveBounds, true, BORDER, FILL);
	}
}
