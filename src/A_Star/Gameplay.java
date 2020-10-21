package A_Star;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;

import A_Star.Node;
import A_Star.NodeComparator;

public class Gameplay extends JPanel implements MouseListener, MouseMotionListener{

	// initial state
	private boolean run = true;
	private boolean started = false;
	private boolean isPaintMode = false;
	private boolean leftMousePressed = false;
	private boolean rightMousePressed = false;

	private int totalRows = 50;
	private int brickWidth;
	private int count = 0;

	private Node start;
	private Node end;
	private Node current;
	
	private boolean landing;
	private String currAlgorithm;
	private String state;

	private ArrayList<Node[]> grid;

	// Constructor
	public Gameplay() {
		addMouseListener(this);
		addMouseMotionListener(this);
		setFocusable(true);
		setFocusTraversalKeysEnabled(true);
		makeGrid(totalRows, 800);
		landing = true;
		this.currAlgorithm = "Breadth First Search (BFS)";
		this.state = "home";
	}

	public void paint(Graphics g) {

		// Background
		g.setColor(Color.white);
		g.fillRect(1, 1, 792, 792);
		
		if(landing) {
			// Draw landing page
			drawLandingPage(g);
		} else {
			// Grid
			drawGrid(totalRows, 800, g);
			drawGridLines(totalRows, 800, g);
		}


		g.dispose();
	}

	public int getTotalRows() {
		return this.totalRows;
	}

	public Node getStart() {
		return this.start;
	}

	public Node getEnd() {
		return this.end;
	}

	public ArrayList<Node[]> getGrid(){
		return this.grid;
	}
	
	public boolean getLanding() {
		return this.landing;
	}
	
	public String getCurrAlgorithm() {
		return this.currAlgorithm;
	}
	
	public boolean getIsPaintMode() {
		return isPaintMode;
	}

	public void makeGrid(int rows, int width) {
		brickWidth = width / rows;
		grid = new ArrayList<Node[]>();
		for(int i = 0; i < totalRows; i++) {
			Node[] nodeRow = new Node[totalRows];
			grid.add(nodeRow);
			for(int j = 0; j < totalRows; j++) {
				Node node = new Node(i, j, brickWidth, totalRows);
				// Add nodes to each node row
				grid.get(i)[j] = node;

			}
		}

		for(int i = 0; i < totalRows; i++) {
			for(int j = 0; j < totalRows; j++) {
				Node curr = grid.get(i)[j];

				Node top = curr.getRow() > 0 ? grid.get(i - 1)[j] : null;
				Node right = curr.getCol() < curr.getTotalRows() - 1 ? grid.get(i)[j + 1] : null;
				Node bottom = curr.getRow() < curr.getTotalRows() - 1 ? grid.get(i + 1)[j] : null;
				Node left = curr.getCol() > 0 ? grid.get(i)[j - 1] : null;

				curr.initializeNeighbors(top, right, bottom, left);
			}
		}


	}

	public void drawGrid(int rows, int width, Graphics g) {
		for(int i = 0; i < grid.size(); i++) {
			for(int j = 0; j < totalRows; j++) {
				Node curr = grid.get(i)[j];
				if(curr != null) {
					curr.draw(g);
				}
			}
		}
	}

	public void drawGridLines(int rows, int width, Graphics g) {
		g.setColor(Color.black);

		for(int i = 0; i < totalRows; i++) {
			for(int j = 0; j < totalRows; j++) {
				grid.get(i)[j].drawLines(g);
			}
		}
	}
	
	public void drawLandingPage(Graphics g) {
		g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
		int midHeight = this.getHeight() / 2;
		g.setColor(Color.BLUE);
		g.drawString("This project shows various path finding algorithms.", 80, midHeight - 80);
		g.setColor(Color.black);
		g.drawString("First, choose a maze.", 80, midHeight - 40);
		g.drawString("Then, click on the grid to create the start and end nodes.", 80, midHeight);
		g.drawString("Finally, Select the path finding algorithm you would like", 80, midHeight + 40);
		g.drawString("to use, and press space to watch it in action.", 80, midHeight + 80);

	}

	public void startAlgorithm() {
		if(!started && start != null && end != null) {
			started = true;
			// Start algorithm
			for(int i = 0; i < totalRows; i++) {
				for(int j = 0; j < totalRows; j++) {

					Node curr = grid.get(i)[j];
					
					Node top = curr.getRow() > 0 ? grid.get(i - 1)[j] : null;
					Node right = curr.getCol() < curr.getTotalRows() - 1 ? grid.get(i)[j + 1] : null;
					Node bottom = curr.getRow() < curr.getTotalRows() - 1 ? grid.get(i + 1)[j] : null;
					Node left = curr.getCol() > 0 ? grid.get(i)[j - 1] : null;

					curr.updateNeighbors(top, right, bottom, left, isPaintMode);
				}
			}
			Graphics g = this.getGraphics();
			algorithm(g, isPaintMode);
		}
		repaint();
	}

	public double h(Node p1, Node p2) {
		// Measures the distance between p1 and p2 
		int x1 = p1.getX();
		int y1 = p1.getY();
		int x2 = p2.getX();
		int y2 = p2.getY();

		return (Math.abs(x1 - x2) + Math.abs(y1 - y2));
	}

	public void algorithm(Graphics g, boolean isPaintMode){
		PriorityQueue<Node> open_set = new PriorityQueue<Node>(5, new NodeComparator());
		int count = 0;
		open_set.add(start);
		start.set_g_score(0);
		start.set_f_score(h(start, end));
		HashSet<Node> open_set_hash = new HashSet<Node>();
		open_set_hash.add(start);

		while(!open_set.isEmpty()) {
			try {
				// Make this controlled by user (5 - fast, 50 - medium, 200 - slow)
				TimeUnit.MILLISECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			Node current = open_set.poll();
			open_set_hash.remove(current);

			if(current.equals(end)) {
				reconstruct_path(end);
				end.makeEnd();
				start.makeStart();
				// Algorithm done
				started = false;
				break;
			}
			
			if(isPaintMode) {
				for(int i = 0; i < current.getPaintModeNeighbors().size(); i++) {
					Node neighbor = current.getPaintModeNeighbors().get(i);

					if(!neighbor.isClosed()) {
						// If the node is closed, disregard it

						double temp_g_score = current.get_g_score() + 1;
						if(temp_g_score < neighbor.get_g_score()) {
							neighbor.set_came_From(current);
							neighbor.set_g_score(temp_g_score);
							neighbor.set_f_score(h(neighbor, end));

							if(!open_set_hash.contains(neighbor)) {
								count++;
								neighbor.setCount(count);
								open_set.add(neighbor);
								open_set_hash.add(neighbor);
								neighbor.makeOpen();
								neighbor.draw(g);
//								drawGridLines(totalRows, brickWidth, g);
							}
						}
					}
					if(!current.equals(start)) {
						current.makeClosed();
						open_set.remove(current);
						open_set_hash.remove(current);
						current.draw(g);
//						drawGridLines(totalRows, brickWidth, g);
					}
				}
			} else {
				for(int i = 0; i < current.getNeighbors().length; i++) {
					Node neighbor = current.getNeighbors()[i];

					if(neighbor != null && !neighbor.isClosed()) {
						// If the node is closed, or there is a wall between this node and it's neighbor, disregard it
						double temp_g_score = current.get_g_score() + 1;
						if(temp_g_score < neighbor.get_g_score()) {
							neighbor.set_came_From(current);
							neighbor.set_g_score(temp_g_score);
							neighbor.set_f_score(h(neighbor, end));

							if(!open_set_hash.contains(neighbor)) {
								count++;
								neighbor.setCount(count);
								open_set.add(neighbor);
								open_set_hash.add(neighbor);
								if(!neighbor.equals(end)) {
									neighbor.makeOpen();
									neighbor.drawLines(g);
									neighbor.draw(g);
								}
							}
						}
					}
					if(!current.equals(start)) {
						current.makeClosed();
						open_set.remove(current);
						open_set_hash.remove(current);
						current.draw(g);
						current.drawLines(g);
					}
				}
			}
		}
	}

	public void reconstruct_path(Node current) {
		while(current.getCameFrom() != null) {
			try {
				TimeUnit.MILLISECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			current = current.getCameFrom();
			if(!current.equals(start) && !current.equals(end)) {
				current.makePath();
				Graphics g = this.getGraphics();
				current.draw(g);
				current.drawLines(g);
			}
		}
		this.current = this.end;
	}

	public Node getClickedNode(int x, int y) {
		if(x > this.getWidth() || x < 0 || y > this.getHeight() || y < 0) {
			return null;
		} else {
			int rowClicked = y / brickWidth;
			int colClicked = x / brickWidth;

			if(rowClicked < this.totalRows && colClicked < this.totalRows) {
				Node clicked = grid.get(rowClicked)[colClicked];
				return clicked;
			}

			return null;
		}
	}
	
	public Node getCurrent() {
		return this.current;
	}
	
	public boolean getStarted() {
		return this.started;
	}
	
	public void setCurrent(Node current) {
		this.current = current;
	}
	
	public void setStart(Node start) {
		this.start = start;
	}
	
	public void setEnd(Node end) {
		this.end = end;
	}
	
	public String getState() {
		return this.state;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
	public void setStarted(boolean started) {
		this.started = started;
	}

	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
	}
	
	public void setLanding(boolean landing) {
		this.landing = landing;
	}
	
	public void setCurrAlgorithm(String currAlgorithm) {
		this.currAlgorithm = currAlgorithm;
	}
	
	public void setIsPaintMode(boolean isPaintMode){
		this.isPaintMode = isPaintMode;
	}
	
	public void moveUp() {
		current = this.grid.get(current.getRow() - 1)[current.getCol()];
	}
	
	public void moveRight() {
		current = this.grid.get(current.getRow())[current.getCol() + 1];
	}
	
	public void moveDown() {
		current = this.grid.get(current.getRow() + 1)[current.getCol()];
	}
	
	public void moveLeft() {
		current = this.grid.get(current.getRow())[current.getCol() - 1];
	}

	public void clearBoard() {
		if(!started) {
			started = false;
			start = null;
			end = null;
			makeGrid(totalRows, 800);
		}
//		this.landing = true;
		repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();

		if(!started) {
			Node clicked = getClickedNode(x, y);

			if(clicked != null) {
				if(leftMousePressed) {
					if(start == null && !clicked.isEnd()) {
						clicked.makeStart();
						start = clicked;
					} else if(end == null && !clicked.isStart()) {
						clicked.makeEnd();
						end = clicked;
					} else if(clicked != start && clicked != end && this.state == "paint"){
						clicked.makeBarrier();
					}
				} else if(rightMousePressed){
					if(clicked.isStart()) {
						start = null;
					} else if(clicked.isEnd()) {
						end = null;
					}
					clicked.reset();
				}
			}
		}
		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();

		if(!started) {
			Node clicked = getClickedNode(x, y);

			if(clicked != null) {
				if(e.getButton() == 1) {
					// Left click
					leftMousePressed = true;

					if(start == null && !clicked.isEnd() && !clicked.isBarrier()) {
						clicked.makeStart();
						current = clicked;
						start = clicked;
					} else if(end == null && !clicked.isStart() && !clicked.isBarrier()) {
						clicked.makeEnd();
						end = clicked;
					} else if(isPaintMode && !clicked.isStart() && !clicked.isEnd() && this.state == "paint") {
						clicked.makeBarrier();
					}
				} else if(e.getButton() == 3) {
					// Right click
					rightMousePressed = true;
					if(clicked.isStart()) {
						current = null;
						start = null;
					} else if(clicked.isEnd()) {
						end = null;
					}
					clicked.reset();
				}	
			}
		}
		repaint();		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == 1) {
			// Left click released
			leftMousePressed = false;
		} else if(e.getButton() == 3) {
			// Right click released
			rightMousePressed = false;
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
}

