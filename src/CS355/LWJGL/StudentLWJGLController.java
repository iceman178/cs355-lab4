package CS355.LWJGL;

//You might notice a lot of imports here.
//You are probably wondering why I didn't just import org.lwjgl.opengl.GL11.*
//Well, I did it as a hint to you.
//OpenGL has a lot of commands, and it can be kind of intimidating.
//This is a list of all the commands I used when I implemented my project.
//Therefore, if a command appears in this list, you probably need it.
//If it doesn't appear in this list, you probably don't.
//Of course, your mileage may vary. Don't feel restricted by this list of imports.
import org.lwjgl.input.Keyboard;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex3d;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.util.glu.GLU.gluPerspective;

import java.util.Iterator;

import org.lwjgl.util.vector.Vector3f;

import javafx.scene.Camera;

import static org.lwjgl.opengl.GL11.glPopMatrix;

public class StudentLWJGLController implements CS355LWJGLController 
{
	private enum projectionType {
		ORTHOGRAPHIC, PERSPECTIVE
	}
	
	// This is a model of a house.
	// It has a single method that returns an iterator full of Line3Ds.
	// A "Line3D" is a wrapper class around two Point2Ds.
	// It should all be fairly intuitive if you look at those classes.
	// If not, I apologize.
	private WireFrame model = new HouseModel();
	
	private projectionType projection;
	private Vector3f position = new Vector3f();
	private float yaw = 0.0f;
	private static final float UNIT = 1.0f;

	// This method is called to "resize" the viewport to match the screen.
	// When you first start, have it be in perspective mode.
	@Override
	public void resizeGL() 
	{
		this.projection = projectionType.PERSPECTIVE;
		
		glViewport(0, 0, LWJGLSandbox.DISPLAY_WIDTH, LWJGLSandbox.DISPLAY_HEIGHT); //setup viewport
		
		glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        
		moveHome();
	}

	@Override
	public void update() 
	{
		// DO NOTHING
	}

	// This is called every frame, and should be responsible for keyboard
	// updates.
	// An example keyboard event is captured below.
	// The "Keyboard" static class should contain everything you need to finish
	// this up.
	@Override
	public void updateKeyboard() 
	{
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) { // Move left
			this.moveLeft();
		}
		else if (Keyboard.isKeyDown(Keyboard.KEY_D)) { // Move right
			this.moveRight();
		}
		else if (Keyboard.isKeyDown(Keyboard.KEY_W)) { // Move forward
			this.moveForward();
		}
		else if (Keyboard.isKeyDown(Keyboard.KEY_S)) { // Move backward
			this.moveBackward();
		}
		else if (Keyboard.isKeyDown(Keyboard.KEY_Q)) { // Turn left
			this.turnLeft();
		}		
		else if (Keyboard.isKeyDown(Keyboard.KEY_E)) { // Turn right
			this.turnRight();
		}		
		else if (Keyboard.isKeyDown(Keyboard.KEY_R)) { // Move up
			this.moveUp();
		}		
		else if (Keyboard.isKeyDown(Keyboard.KEY_F)) { // Move down
			this.moveDown();
		}		
		else if (Keyboard.isKeyDown(Keyboard.KEY_H)) { // Return to the original "home" position and orientation
			this.moveHome();
		}		
		else if (Keyboard.isKeyDown(Keyboard.KEY_O)) { // Switch to orthographic projection
			this.projection = projectionType.ORTHOGRAPHIC;
		}		
		else if (Keyboard.isKeyDown(Keyboard.KEY_P)) { // Switch to perspective projection
			this.projection = projectionType.PERSPECTIVE;
		}		
	}

	// This method is the one that actually draws to the screen.
	@Override
	public void render() 
	{
		// This clears the screen.
		glClear(GL_COLOR_BUFFER_BIT);

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		
		switch(this.projection)
		{
			case PERSPECTIVE:
				gluPerspective(48.0f, (float) LWJGLSandbox.DISPLAY_WIDTH / LWJGLSandbox.DISPLAY_HEIGHT, 1.0f, 300.0f);
				break;
			case ORTHOGRAPHIC:
				glOrtho(-24.0f, 24.0f, -24.0f, 24.0f, 1.0f, 300.0f);
				break;
		}
		
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		
		glRotatef(yaw, 0.0f, 1.0f, 0.0f);
		glTranslatef(position.x, position.y, position.z);
		
		this.drawNeighborhood();	
	}

	
	private void drawHouse() 
	{	
		glBegin(GL_LINES);
		
		Iterator<Line3D> it = model.getLines();
		
		while (it.hasNext())
		{
			Line3D line = it.next();
			glVertex3d(line.start.x, line.start.y, line.start.z);
			glVertex3d(line.end.x, line.end.y, line.end.z);
		}
		glEnd();
	}
	
	private void drawNeighborhood() 
	{	
		for(float z=0.0f; z < 10.0f; z += 1.0f) // Makes 10 houses in a row
		{
			for(float x = -1.0f; x <= 1.0f; x += 2.0f) // Makes 2 houses that face each other 
			{
				float rotation = 0.0f;
				if (x == 1)	{
					rotation = 270.0f;
				}
				else {
					rotation = 90.0f;
				}
				
				float r = 1.0f - (( z + 1.0f ) * 0.1f);
				float g = 0.5f - (x * 0.50f);
				float b = (z - 1.0f) * 0.1f;
				
				glPushMatrix();
				glColor3f(r, g, b);
				glTranslatef(x * 15, 0, z * -15);
				glRotatef(rotation, 0.0f, 1.0f, 0.0f);
				this.drawHouse();
				glPopMatrix();	
			}
		}
		this.drawHouse(); // Draws the normal house
	}

	// House functions
	
	private void moveHome() {
		position.x = 0;
		position.y = -1.5f;
		position.z = -20;
		yaw = 0.0f;
	}
	
	private void moveLeft() {
		position.x -= UNIT * (float)Math.sin(Math.toRadians(yaw - 90));
	    position.z += UNIT * (float)Math.cos(Math.toRadians(yaw - 90));
	}
	
	private void moveRight() {
		position.x -= UNIT * (float)Math.sin(Math.toRadians(yaw + 90));
	    position.z += UNIT * (float)Math.cos(Math.toRadians(yaw + 90));
	}
		
	private void moveForward() {
		position.x -= UNIT * (float)Math.sin(Math.toRadians(yaw));
	    position.z += UNIT * (float)Math.cos(Math.toRadians(yaw));
	}
		
	private void moveBackward() {
		position.x += UNIT * (float)Math.sin(Math.toRadians(yaw));
	    position.z -= UNIT * (float)Math.cos(Math.toRadians(yaw));
	}		
	
	private void turnLeft() {
		yaw -= UNIT;
	}	
		
	private void turnRight() {
		yaw += UNIT;
	}	
		
	private void moveUp() {
		position.y -= UNIT;
	}
	
	private void moveDown() {
		position.y += UNIT;
	}
	
	
}
