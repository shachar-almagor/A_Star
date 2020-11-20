package A_Star;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;

public class Toolbar extends JPanel implements KeyListener{

	private Gameplay gameplay;
	private Settings settings;
	private Algorithms algorithms;

	public Toolbar(Gameplay gameplay, Settings settings, Algorithms algorithms) {
		this.gameplay = gameplay;
		this.settings = settings;
		this.algorithms = algorithms;
		this.setLayout(new BorderLayout());

		setFocusable(true);
		setFocusTraversalKeysEnabled(true);

		this.add(gameplay, BorderLayout.CENTER);
		this.add(settings, BorderLayout.WEST);
		addKeyListener(this);
	}

	public Gameplay getGameplay() {
		return this.gameplay;
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if(key == KeyEvent.VK_SPACE && gameplay.getStart() != null && gameplay.getEnd() != null && !gameplay.getStarted()) {
			if(gameplay.getCurrAlgorithm() == CurrentAlgorithm.BreadthFirstSearch) {
				algorithms.breadthFirstSearch(gameplay);
			} else if(gameplay.getCurrAlgorithm() == CurrentAlgorithm.DepthFirstSearch) {
				algorithms.depthFirstSearch(gameplay);
				algorithms.setEnded(false);
			} else if(gameplay.getCurrAlgorithm() == CurrentAlgorithm.Dijkstra) {
				try {
					algorithms.dijkstra(gameplay);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} else {
				// A*
				gameplay.startAlgorithm();
			}
		} else if(gameplay.getStart() != null && gameplay.getEnd() != null) {
			Node current = gameplay.getCurrent();
			Node temp = current;
			if(!current.isEnd()) {
				// The user hasn't finished the maze
				// FIX - MAKE MOVING INSTANTANEOUS
				if(key == KeyEvent.VK_UP && !current.topWall && current.getRow() > 0) {
					// Only move up if the top wall isn't there
					gameplay.moveUp();
				} else if(key == KeyEvent.VK_RIGHT && !current.rightWall && current.getCol() < gameplay.getTotalRows()) {
					// Only move right if the right wall isn't there
					gameplay.moveRight();
				} else if(key == KeyEvent.VK_DOWN && !current.bottomWall && current.getRow() < gameplay.getTotalRows()) {
					// Only move down if the bottom wall isn't there
					gameplay.moveDown();
				} else if(key == KeyEvent.VK_LEFT && !current.leftWall && current.getCol() > 0) {
					// Only move left if the left wall isn't there
					gameplay.moveLeft();
				}
				current = gameplay.getCurrent();
				if(!temp.isStart() && !temp.isEnd()) {
					temp.makePath();
				}
				if(!current.isStart() && !current.isEnd()) {
					current.makeOpen();
				}
				Graphics graphics = gameplay.getGraphics();
				temp.draw(graphics);
				current.draw(graphics);
				temp.drawLines(graphics);
				current.drawLines(graphics);
			}
		} 
	}

	@Override
	public void keyReleased(KeyEvent e) {}
}
