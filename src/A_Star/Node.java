package A_Star;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import A_Star.Node;

public class Node{

	private int row;
	private int col;
	private int x;
	private int y;
	private Color color;
	private Node[] neighbors;
	private ArrayList<Node> paintmModeNeighbors;
	private boolean visited;
	
	boolean topWall;
	boolean rightWall;
	boolean bottomWall;
	boolean leftWall;


	private int count;
	private double f_score;
	private double g_score;
	private double distance;
	private Node cameFrom;

	private int width;
	private int totalRows;

	public Node(int row, int col, int width, int totalRows) {
		this.row = row;
		this.col = col;
		this.width = width;
		this.totalRows = totalRows;	
		this.x = (col * width);
		this.y = (row * width);
		this.f_score = Double.POSITIVE_INFINITY;
		this.g_score = Double.POSITIVE_INFINITY;
		this.distance = Double.POSITIVE_INFINITY;
		this.cameFrom = null;
		this.visited = false;
		this.topWall = true;
		this.rightWall = true;
		this.bottomWall = true;
		this.leftWall = true;

		this.color = Color.white;
		this.neighbors = new Node[4];
		this.paintmModeNeighbors = new ArrayList<Node>();

	}

	// GETTERS

	public int getRow() {
		return this.row;
	}

	public int getCol() {
		return this.col;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public double get_f_score() {
		return this.f_score;
	}

	public double get_g_score() {
		return this.g_score;
	}
	
	public double getDistance() {
		return this.distance;
	}

	public Node getCameFrom() {
		return this.cameFrom;
	}

	public int getCount() {
		return this.count;
	}

	public int getTotalRows() {
		return totalRows;
	}

	public int getWidth() {
		return width;
	}

	public Node[] getNeighbors(){
		return this.neighbors;
	}
	
	public ArrayList<Node> getPaintModeNeighbors(){
		return this.paintmModeNeighbors;
	}

	public boolean getVisited() {
		return this.visited;
	}

	// SETTERS
	public void set_f_score(double newScore) {
		this.f_score = newScore;
	}

	public void set_g_score(double newScore) {
		this.g_score = newScore;
	}
	
	public void setDistance(double distance) {
		this.distance = distance;
	}

	public void set_came_From(Node current) {
		this.cameFrom = current;
	}

	public boolean isClosed() {
		// is the current node in the closedSet
		return this.color == Color.red;
	}

	public boolean isOpen() {
		// is the current node in the openSet
		return this.color == Color.green;
	}

	public boolean isStart() {
		// is the current node the unique start node
		return this.color == Color.blue;
	}

	public boolean isPath() {
		// is the current node a path node
		return this.color == Color.magenta;
	}

	public boolean isEnd() {
		// is the current node the unique end node
		return this.color == Color.orange;
	}
	
	public boolean isBarrier() {
		// is the current node a barrier
		return this.color == Color.black;
	}
	

	public void reset() {
		// Reset this node color to white
		this.color = Color.white;
	}

	public void makeClosed() {
		// Change this node's color to red
		this.color = Color.red;
	}

	public void makeOpen() {
		// Change this node's color to green
		this.color = Color.green;
	}

	public void makeStart() {
		// Change this node's color to blue
		this.color = Color.blue;
	}

	public void makePath() {
		// Change this node's color to magenta
		this.color = Color.magenta;
	}

	public void makeEnd() {
		// Change this node's color to orange
		this.color = Color.orange;
	}
	
	public void makeBarrier() {
		// Change this node's color to black
		this.color = Color.black;
	}

	public void setTopWall(boolean toDraw) {
		this.topWall = toDraw;
	}
	
	public void setRightWall(boolean toDraw) {
		this.rightWall = toDraw;
	}
	
	public void setBottomWall(boolean toDraw) {
		this.bottomWall = toDraw;
	}
	
	public void setLeftWall(boolean toDraw) {
		this.leftWall = toDraw;
	}
	
	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	public void draw(Graphics g) {
		g.setColor(this.color);
		g.fillRect(this.x, this.y, this.width, this.width);
	}

	public void drawLines(Graphics g) {
		if(this.topWall) {
			// Top Line
			g.setColor(Color.black);
			g.drawLine(this.x, this.y, this.x + this.width, this.y);
		} else {
			g.setColor(this.color);
			g.drawLine(this.x, this.y, this.x + this.width, this.y);
		}
		if(this.rightWall) {
			// Right Line
			g.setColor(Color.black);
			g.drawLine(this.x + this.width, this.y, this.x + this.width, this.y + this.width);
		} else {
			g.setColor(this.color);
			g.drawLine(this.x + this.width, this.y, this.x + this.width, this.y + this.width);
		}
		if(this.bottomWall) {
			// Bottom Line
			g.setColor(Color.black);
			g.drawLine(this.x + this.width, this.y + this.width, this.x, this.y + this.width);
		} else {
			g.setColor(this.color);
			g.drawLine(this.x + this.width, this.y + this.width, this.x, this.y + this.width);
		}
		if(this.leftWall) {
			// Left Line
			g.setColor(Color.black);
			g.drawLine(this.x, this.y + this.width, this.x, this.y);
		} else {
			g.setColor(this.color);
			g.drawLine(this.x, this.y + this.width, this.x, this.y);
		}

	}

	public void initializeNeighbors(Node top, Node right, Node bottom, Node left) {
		this.neighbors[0] = top;
		this.neighbors[1] = right;
		this.neighbors[2] = bottom;
		this.neighbors[3] = left;
	}
	
	public void updateNeighbors(Node left, Node down, Node right, Node up, boolean isPaintMode) {
		if(!isPaintMode) {
			if(this.topWall) {
				this.neighbors[0] = null;
			}
			if(this.rightWall) {
				this.neighbors[1] = null;
			}
			if(this.bottomWall) {
				this.neighbors[2] = null;
			}
			if(this.leftWall) {
				this.neighbors[3] = null;
			}
		} else {
			// Paint Mode
			if(left != null && !(left.isBarrier()) && !(left.isClosed())) {
				// LEFT
				this.paintmModeNeighbors.add(left);
			}
			if(down != null && !(down.isBarrier()) && !(down.isClosed())) {
				// DOWN
				this.paintmModeNeighbors.add(down);
			}
			if(right != null && !(right.isBarrier()) && !(right.isClosed())) {
				// RIGHT
				this.paintmModeNeighbors.add(right);
			}
			if(up != null && !(up.isBarrier()) && !(up.isClosed())) {
				// UP
				this.paintmModeNeighbors.add(up);
			}
		}
	}

	public boolean checkNeighbors(Node top, Node right, Node bottom, Node left) {
		if((top != null && !top.visited) || (right != null && !right.visited) || (bottom != null && !bottom.visited) || (left != null && !left.visited)) {
			// If at least one neighbor is unvisited
			return true;
		}
		return false;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
