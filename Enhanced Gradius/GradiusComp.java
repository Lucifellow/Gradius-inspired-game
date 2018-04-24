import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.awt.geom.*;
import java.awt.event.*;
import java.util.*;
import java.applet.Applet;
import java.applet.AudioClip;
import java.net.URL;
import java.util.stream.Stream;
import javax.swing.JComponent;
import javax.swing.Timer;
import java.awt.Toolkit;


/** Gradius Component 
	@author Hrishikesh Vyas
	@author Jeremy H @ Langara for CPSC 1181-003 
	@version 2018-03-02
*/
@SuppressWarnings("serial")
public class GradiusComp extends JComponent {
	

 	private final static int GAME_TICK = 1000 / 60;				//Make this game run at 60 fps
	private final static int ASTEROID_MAKE_TICK = 1000/4;		//Make an asteroid every 1000/4
	
	private final static double MAX_HEALTH = 250;				//Maximum Health of the ship and maximum width of health bar
	private final static double DAMAGE = 0.00001;						//Damage from collision with asteroids
	private final static int ADJUST_SCORE = 100;				//Adjusts the score upto two decimal places
	private final static int HEALTH_BAR_INIT_X = 20;			//Starting x co-ordinate of health bar
	private final static int HEALTH_BAR_INIT_Y = 15;			//Starting y co-ordinate of health bar
	private final static int HEALTH_BAR_HEIGHT = 20;			//Height of the health bar 
	private final static int SHIP_INIT_X = 10;					//Intial x coordiante of ship
	private final static int SHIP_INIT_Y = Gradius.HEIGHT/3;	//Initial y coordinate of ship
	private final static int SHIP_VEL_BASE = 2;					//Velocity of ship without pressing shift key
	private final static int SHIP_VEL_FAST = 4;					//Velocity of ship after pressing shift key
	private ArrayList<Timer> gameTick;							//An ArrrayList of time
	private Ship ship;
   private Collection<Asteroid> roids;		// A collection of Asteroids
	private AudioClip music;					//Background music
	private AudioClip collision;				//played when collision occurs
	private AudioClip death;					//Played when player dies
   private double health;							//Health of Ship
	private Image stars; 						//Background stars
	private double score;						//Survival sre of the player
	
	public GradiusComp() {
		addKeyListener(new ShipKeyListener());			//Adds a new ShipKeyListener based on BoxEvent example provided for reference
		gameTick = new ArrayList<Timer>();
		roids = new HashSet<Asteroid>();		//Initialize a collection with HashSet to get unique shapes of asteroid
		
		//Music used: " Galactic Chase" by PlayOnLoop.com Licensed under Creative Commons By Attribution 4.0
		//Music used: " Rocketman" by PlayOnLoop.com Licensed under Creative Commons By Attribution 4.0
		URL url = Gradius.class.getResource("music.wav");	//Music code inspired from: http://www.java2s.com/Tutorial/Java/0120__Development/playsoundwavfilewithAudioClip.htm
		
		//Music used: "06167 Magnetic destroy shot.wav" by Robinhood76 Licensed under Creative Commons By Attribution 3.0
		//Music downlod link: https://freesound.org/people/Robinhood76/sounds/331156/
		URL url2 = Gradius.class.getResource("death.wav");		//Store the sound file reference in url
		
		//Music used: "01667 slow laser shot.wav" by Robinhood76 Licensed under Creative Commons By Attribution 3.0
		//Music downlod link: https://freesound.org/people/Robinhood76/sounds/96556/
		URL url3 = Gradius.class.getResource("collision.wav");
		
		//stars image code: http://www.java2s.com/Tutorial/Java/0261__2D-Graphics/DrawImage.htm
		stars = Toolkit.getDefaultToolkit().getImage("stars.jpg"); //Image source: https://www.wallpaperup.com/19257/Outer_space_stars.html
		
		health = MAX_HEALTH;		//Let initial health of ship be maximum health
		collision = Applet.newAudioClip(url3);	//Music code inspired from: http://www.java2s.com/Tutorial/Java/0120__Development/playsoundwavfilewithAudioClip.htm
		death = Applet.newAudioClip(url2);		//Storing url reference in AudioClip references (creating pointer chains)
		music = Applet.newAudioClip(url);
		score = 0;			//Initial score of the player
		
		gameTick.add(new Timer(GAME_TICK, this::update));			//adds a new timer to arraylist of timers that calls update method for every GAME_TICK times
		gameTick.add(new Timer(ASTEROID_MAKE_TICK,roids->makeAsteroid()));		//Adds a new timer to arraylist of timers that calls makeAsteroid() every ASTEROID_TICK times	
	}
	
	/**
	Adds an Asteroid returned by calling AsteroidFactory.makeAsteroid() to roids Collection
	*/
	public void makeAsteroid() {
		roids.add(AsteroidFactory.makeAsteroid());		//Populate roids with asteroid by calling AsteroidFactory.makeAsteroid()
	}

	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.clearRect(0,0,getWidth(),getHeight());
		g2.setRenderingHint(
			RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		paintComponent(g2);
	}
	
	private void paintComponent(Graphics2D g2) {

		g2.drawImage(stars,0,0,getWidth(),getHeight(),null);			//Draws the background
		setFontSize(15,g2);		//Sets font to display score in real time
		g2.drawString(Double.toString(score/ADJUST_SCORE),getWidth()-80,HEALTH_BAR_INIT_Y);		//Draws score in real time
		drawHealthbar(g2);				//Calls method to draw healthbar
		roids.stream()								//Draws each Asteroid in the stream of roids
		.forEach(Asteroid-> Asteroid.draw(g2));		// Based on example given by Jeremy in email: shapes.stream().forEach(g2::draw);
		
		if(health <= 0) {		//If the health of the ship is less than or equal to zero draw final score and Game over
			setFontSize(50,g2);	//Font for Game Over
			g2.drawString("Game Over",((getWidth()/2)-ADJUST_SCORE),getHeight()/2);		//Adjusting string based on width and height of string and component
			setFontSize(30,g2);		//Font for displaying final score
			g2.drawString("Your score : " + Double.toString(score/ADJUST_SCORE),((getWidth()/2)-ADJUST_SCORE),getHeight()/2 + 60 );
		}
		
		ship.draw(g2);				//Calls draw method of ship interface 
	}
	
	/**
	Paints healthbar of the ship and reduces its width as spaceship takes damage from asteroids(health)
	
	@param g2 Graphics2D
	*/
	private void drawHealthbar(Graphics2D g2){
		g2.draw( new Rectangle2D.Double(HEALTH_BAR_INIT_X,HEALTH_BAR_INIT_Y,health,HEALTH_BAR_HEIGHT));		//Draws and fills rectangle based on the remaining health of the space ship
		g2.setColor(Color.RED);
		g2.fill( new Rectangle2D.Double(HEALTH_BAR_INIT_X,HEALTH_BAR_INIT_Y,health,HEALTH_BAR_HEIGHT));
	}
	
	/**
	Sets the size of the fonts based on the integer passed
	
	@param n Size of the fonts
	@param g2 Graphics2D 
	*/
	private void setFontSize(int n,Graphics2D g2) {
		Font f = new Font("Arial",Font.BOLD,n);
		g2.setFont(f);
		g2.setColor(Color.RED);
	}
	
	public void start() {
		ship = new ShipImpl(SHIP_INIT_X,SHIP_INIT_Y,new Rectangle2D.Double(0,0,getWidth(),getHeight()));	//Initializes ship with its x and y along with moveBounds
		music.loop(); // Loops the background music
		AsteroidFactory.setStartBounds(new Rectangle(0,0,getWidth(),getHeight()));			//Sets start Bounds of asteroid
		AsteroidFactory.setMoveBounds(new Rectangle(0,0,getWidth()+1,getHeight()+1));		//Sets move bounds of asteroid
		gameTick.stream()
		.forEach(Timer -> Timer.start());							//Starts all timers in the gameTick arrayList
	}
	
	
	public void update(ActionEvent e){
		score++;							//Increment the score of player until timer is running
		requestFocusInWindow();		//Keeps focus on the player controls
		if(health <= 0) {				//If at any time health of the ship is zero or less
			gameTick.stream()
			.forEach(Timer -> Timer.stop());		// stop all timers
			music.stop(); // Stops background music when player dies
			death.play();	//Play game over sound once when player dies
		}
		
		roids.parallelStream()		//Check if at any moment the ship is colliding  with Asteroid
			.forEach(Asteroid -> {
											if(Asteroid.intersects(ship)){
												health = health - DAMAGE;			//Reduce health of ship by Damage for every second it is colliding with asteroid
												collision.play();				//Play collision sound each time collision occurs
											}
										}
						);
		
		roids.parallelStream()
		.forEach(Asteroid -> Asteroid.move());		//Calls the move method for each asteroid in the parallelStream
		ship.move();
		roids.removeIf(Asteroid->Asteroid.isOutOfBounds());		//If asteroid is completely out of frame then delete it from the roids collection to fix memory leak
		repaint();	//Repaint the whole component
	}

	private class ShipKeyListener extends KeyAdapter {

		private boolean up;			//up state
		private boolean down;		//down state
		private boolean left;		//left state
		private boolean right;		//right state
		
		private void setVelocity(KeyEvent e) {
			setPosition(e);				//calls helper method
			final int speed = e.isShiftDown()?SHIP_VEL_FAST:SHIP_VEL_BASE;	//Increase the velocity of ship if shift is down 
			
			//Based on the Box Event example provided for reference from next line to end of this class
			int dx = 0;		//Velocity in x
			int dy = 0;		//Velocity in y
			
			if(up && !down) {
				dy = -speed;	//ship goes up when only up state is true
			}
			else if (down && !up) {
				dy = speed;		//ship goes down when only down state is true
			}
			if(left && !right) {
				dx = -speed;	//ship goes left when only left state is true
			}
			else if(right && !left) {
				dx =speed;		//Ship goes right when only right state is true
			}
			ship.setVelocity(dx,dy);		//Sets ships velocity in 2D
		}
		private void setPosition(KeyEvent e) {
			final boolean state;
			
			switch(e.getID()) {				//When the key is pressed state is true and its false when the key is released
				case KeyEvent.KEY_PRESSED: state = true; break;
				case KeyEvent.KEY_RELEASED: state =false; break;
				default: return;
			}
			switch(e.getKeyCode()) {
				case KeyEvent.VK_W:
				case KeyEvent.VK_UP:
				case KeyEvent.VK_KP_UP:
					up = state ;		//When w or up arrow or 8 from numpad is pressed state up is true
					break;
				case KeyEvent.VK_S: 
				case KeyEvent.VK_DOWN:
				case KeyEvent.VK_KP_DOWN:
					down = state ;		//When s or down arrow or 2 from numpad is pressed state down is true
					break;
				case KeyEvent.VK_A: 
				case KeyEvent.VK_LEFT:
				case KeyEvent.VK_KP_LEFT:
					left = state ;		//When a or left arrow or 4 from numpad is pressed state left is true
					break;
				case KeyEvent.VK_D: 
				case KeyEvent.VK_RIGHT:
				case KeyEvent.VK_KP_RIGHT:
					right = state ;	//When d or right arrow or 6 from numpad is pressed state right is true
					break;
			}
		}
		@Override
		public void keyPressed(KeyEvent e) {
			setVelocity(e);
		}
		@Override
		public void keyReleased(KeyEvent e) {
			setVelocity(e);
		}
	};
   
}
