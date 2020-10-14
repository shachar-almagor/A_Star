package A_Star;

import java.awt.Color;
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

	private int totalRows = 50;
	private int brickWidth;
	private int count = 0;

	private Node start;
	private Node end;
	private Node current;

	private ArrayList<Node[]> grid;

	// Constructor
	public Gameplay() {
		addMouseListener(this);
		addMouseMotionListener(this);
		setFocusable(true);
		setFocusTraversalKeysEnabled(true);
		makeGrid(totalRows, 800);
	}

	public void paint(Graphics g) {

		// Background
		g.setColor(Color.white);
		g.fillRect(1, 1, 792, 792);

		// Grid
		drawGrid(totalRows, 800, g);
		drawGridLines(totalRows, 800, g);

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

	public void startAlgorithm() {
		if(!started && start != null && end != null) {
			started = true;
			// Start algorithm
			for(int i = 0; i < totalRows; i++) {
				for(int j = 0; j < totalRows; j++) {

					Node curr = grid.get(i)[j];

					curr.updateNeighbors();
				}
			}
			Graphics g = this.getGraphics();
			algorithm(g);
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

	public void algorithm(Graphics g){
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
	
	public void setCurrent(Node current) {
		this.current = current;
	}

	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
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
		repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {}

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
					if(start == null && !clicked.isEnd() && !clicked.isBarrier()) {
						clicked.makeStart();
						current = clicked;
						start = clicked;
					} else if(end == null && !clicked.isStart() && !clicked.isBarrier()) {
						clicked.makeEnd();
						end = clicked;
					}
				} else if(e.getButton() == 3) {
					// Right click
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
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
}

