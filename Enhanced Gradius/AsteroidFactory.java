import java.awt.Rectangle;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.util.Random;

/** Asteroid factory to make asteroids and implement Asteroid interface with inner class
	@author Hrishikesh Vyas
	@author Jeremy H @ Langara for CPSC 1181-003 
	@version 2018-03-02
*/
public class AsteroidFactory {

	private final static int ASTEROID_SIZE_MIN = 10;
	private final static int ASTEROID_SIZE_MAX = 40;
	private final static int ASTEROID_VEL_MIN = 1;
	private final static int ASTEROID_VEL_MAX = 4;

	private static Rectangle startBounds;
	private static Rectangle moveBounds;

	private AsteroidFactory() {}

	/**
	Sets the start bounds for making asteroids
	
	@param r a rectangle for starting bounds of asteroids
	*/
	public static void setStartBounds(Rectangle r) {
		startBounds = r;
	}
	
	/**
	sets the move bounds for the asteroids
	
	@param r a rectangle for setting move bounds of the asteroids
	*/
	public static void setMoveBounds(Rectangle r) {
		moveBounds = r;
	}

	/**
	Makes asteroids with the help of AsteroidImpl inner class
	
	@return An asteroid
	*/
	public static Asteroid makeAsteroid() {
		return (new AsteroidImpl((int)startBounds.getWidth(),				//Set the x to be width of the startBounds
		random(0,(int)startBounds.getHeight()),								//Set y to be a random number from 0 to the height of the startBounds
		random(ASTEROID_SIZE_MIN,ASTEROID_SIZE_MAX),							//sets the width of the Asteroid
		random(ASTEROID_SIZE_MIN,ASTEROID_SIZE_MAX),							//Sets the height of the Asteroid 
		random(ASTEROID_VEL_MIN,ASTEROID_VEL_MAX)));							//Sets the velocity of the Asteroid
	}

   /**
	Generates a random number between given min and max
	
	@param min lower boundary
	@param max higher boundary
   
	@return Integer 
	*/
	private static int random(int min, int max) {
		if(max-min == 0) { return min; }
		Random rand = java.util.concurrent.ThreadLocalRandom.current();
		return min + rand.nextInt(max + 1);
	}

	/**
	An inner class that extends SpriteImpl and implements Asteroid for abstraction
	*/
	private static class AsteroidImpl extends SpriteImpl implements Asteroid {
		private final static int SPEED_BREAKER = 4;		//Reduces speed of asteroids to make game easier
		
		public AsteroidImpl(int x, int y, int w, int h, float v) {
			super(new Ellipse2D.Float(x,y,w,h), moveBounds, false, 
         new Color((int)Math.floor(Math.random()*256),
         (int)Math.floor(Math.random()*256),
         (int)Math.floor(Math.random()*256))); //Multi color asteroids ()Random r g b value)
			super.setVelocity(v/SPEED_BREAKER,0);			//Sets velocity of asteroids to move from right to left
		}
	}
}
