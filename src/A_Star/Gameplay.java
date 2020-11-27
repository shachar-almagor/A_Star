package A_Star;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;

import A_Star.Node;

public class Gameplay extends JPanel implements MouseListener, MouseMotionListener{

	private static final long serialVersionUID = 1L;
	// initial state
	private boolean started = false;
	private boolean leftMousePressed = false;
	private boolean rightMousePressed = false;

	private int totalRows = 50;
	private int brickWidth;

	private Node start;
	private Node end;
	private Node current;
	private Speed speed;
	private Algorithms algs;

	private CurrentAlgorithm currentAlgorithm;
	private State state;

	private ArrayList<Node[]> grid;

	// Constructor
	public Gameplay() {
		addMouseListener(this);
		addMouseMotionListener(this);
		setFocusable(true);
		setFocusTraversalKeysEnabled(true);
		makeGrid(totalRows, 800);
		this.currentAlgorithm = CurrentAlgorithm.BreadthFirstSearch;
		this.state = State.Home;
		this.speed = Speed.Fast;
	}

	public void paint(Graphics g) {
		// Background
		g.setColor(Color.white);
		g.fillRect(1, 1, 792, 792);

		if(getState() == State.Home) {
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

	public CurrentAlgorithm getCurrAlgorithm() {
		return this.currentAlgorithm;
	}
	
	public Speed getSpeed() {
		return this.speed;
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

	public void reconstructPath(Node current) {
		algs = ((Toolbar) this.getParent()).getAlgortihms();
		if(current.getCameFrom() != null) {
			reconstructPath(current.getCameFrom());
			if(!current.isStart() && !current.isEnd()) {
				try {
					if(this.currentAlgorithm == CurrentAlgorithm.Dijkstra) {
						TimeUnit.MICROSECONDS.sleep(algs.getDelay() / 5);
					} else {
						TimeUnit.MICROSECONDS.sleep(algs.getDelay());
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				current.makePath();
				Graphics g = this.getGraphics();
				current.draw(g);
				current.drawLines(g);
			}
		} else {
			this.current = this.end;
		}
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

	public State getState() {
		return this.state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}

	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
	}

	public void setCurrAlgorithm(CurrentAlgorithm currentAlgorithm) {
		this.currentAlgorithm = currentAlgorithm;
	}
	
	public void setSpeed(Speed speed) {
		this.speed = speed;
	}
	
	public Algorithms getAlgorithms() {
		return this.algs;
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
		if(!this.started) {
			start = null;
			end = null;
			makeGrid(totalRows, 800);
		}
		repaint();
	}

	public void reset() {
		if(this.started) {
			this.started = false;
			for(Node[] nodeRow : this.grid) {
				for(Node node : nodeRow) {
					if(!node.isStart() && !node.isEnd() && !node.isBarrier()) {
						node.reset();
					}
				}
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();

		if(!started) {
			Node clicked = getClickedNode(x, y);

			if(clicked != null) {
				if(leftMousePressed) {
					if(start == null && !clicked.isEnd() && !clicked.isBarrier()) {
						clicked.makeStart();
						start = clicked;
					} else if(end == null && !clicked.isStart() && !clicked.isBarrier()) {
						clicked.makeEnd();
						end = clicked;
					} else if(clicked != start && clicked != end && this.state == State.Paint){
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
					} else if(!clicked.isStart() && !clicked.isEnd() && this.state == State.Paint) {
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

