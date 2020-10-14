package A_Star;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

public class MazeGenerator {

	private Gameplay gameplay;
	private int totalRows;

	public MazeGenerator(Gameplay gameplay, int totalRows) {
		this.gameplay = gameplay;
		this.totalRows = totalRows;
	}

	public void makeZigzag() {
		for(int i = 0; i < this.totalRows; i ++) {
			int rand = (int) (Math.random() * this.totalRows);
			for(int j = 0; j < this.totalRows; j++) {

				Node current = this.gameplay.getGrid().get(i)[j];
				current.reset();
				Node top = i > 0 ? this.gameplay.getGrid().get(i - 1)[j]: null;

				if(i % 2 == 0) {
					// Odd row
					current.setRightWall(false);
					current.setLeftWall(false);
					if(j == rand) {
						current.setTopWall(false);
						if(top != null) {
							top.setBottomWall(false);
						}
					}
				} else if(j == rand) {
					current.setTopWall(false);
					current.setRightWall(false);
					current.setLeftWall(false);

					if(top != null) {
						top.setBottomWall(false);
					}

				}
				else {
					current.setRightWall(false);
					current.setLeftWall(false);
				}
			}
		}
	}

	public void makeSpiral(Node current, int count) {
		if(count >= (this.gameplay.getTotalRows() / 2) + 1) {
			return;
		}
		else {
			int currRow = current.getRow();
			// Top line
			for(int i = -1 + count; i < totalRows - count; i++) {
				Node curr = gameplay.getGrid().get(currRow)[i];

				curr.setRightWall(false);
				curr.setLeftWall(false);
				curr.reset();
				if(i == totalRows - count - 1) {
					curr.setBottomWall(false);
					curr.setRightWall(true);
				}
				if(i == -1 + count && i - 1 >= 0) {
					gameplay.getGrid().get(currRow)[i - 1].setBottomWall(false);			
					gameplay.getGrid().get(currRow)[i - 1].reset();				
				}
			}

			// Right line
			for(int i = count; i < totalRows - count; i++) {
				Node curr = gameplay.getGrid().get(i)[totalRows - count - 1];

				curr.setTopWall(false);
				curr.setBottomWall(false);
				curr.reset();
				if(i == totalRows - count - 1) {
					curr.setBottomWall(true);
					curr.setRightWall(true);
				}		
			}

			count++;

			// Bottom line
			for(int i = totalRows - count; i > count - 1; i--) {
				Node curr = gameplay.getGrid().get(totalRows - currRow - 2)[i];

				curr.setRightWall(false);
				curr.setLeftWall(false);
				curr.reset();

				if(i == totalRows - count) {
					curr.setRightWall(true);
				}	
				if(i == count) {
					curr.setLeftWall(false);
				}
			}

			count++;
			// Left line
			for(int i = totalRows - count + 1; i > count - 1; i--) {
				Node curr = gameplay.getGrid().get(i)[currRow + 1];

				curr.setTopWall(false);
				curr.setBottomWall(false);
				curr.reset();

				if(i == totalRows - count + 1) {
					curr.setBottomWall(true);
					curr.setRightWall(false);
				}	
				if(i == count) {
					gameplay.getGrid().get(i - 1)[currRow + 1].setRightWall(false);
					gameplay.getGrid().get(i - 1)[currRow + 1].reset();
				}
			}
			if(currRow + 2 <= gameplay.getTotalRows() && current.getCol() + 2 <= gameplay.getTotalRows() / 2) {
				current = gameplay.getGrid().get(currRow + 2)[current.getCol() + 2];
			} else {
				return;
			}
			makeSpiral(current, count);
		}
	}

	public void makeRandomMaze(Node current, Stack<Node> path, HashSet<Node> unvisited_set_hash, Gameplay gameplay, Graphics g) throws InterruptedException {
		while(!unvisited_set_hash.isEmpty()) {
			// While there are unvisited cells
			Node top = current.getNeighbors()[0];
			Node right = current.getNeighbors()[1];
			Node bottom = current.getNeighbors()[2];
			Node left = current.getNeighbors()[3];

			ArrayList<Node> unvisited_neighbors = new ArrayList<Node>();

			if(current.checkNeighbors(top, right, bottom, left)) {
				// One of the neighbors is unvisited
				if(top != null && !top.getVisited()) {
					unvisited_neighbors.add(top);
				}
				if(right != null && !right.getVisited()) {
					unvisited_neighbors.add(right);
				}
				if(bottom != null && !bottom.getVisited()) {
					unvisited_neighbors.add(bottom);
				}
				if(left != null && !left.getVisited()) {
					unvisited_neighbors.add(left);
				}

				int rand = (int) (Math.random() * unvisited_neighbors.size());
				path.push(current);
				g.setColor(Color.black);
				Node temp = current;
				Node removed = unvisited_neighbors.remove(rand);
				if(removed.equals(top)) {
					// TOP
					current.setTopWall(false);
					current = removed;
					current.setBottomWall(false);
				} else if(removed.equals(right)) {
					// RIGHT
					current.setRightWall(false);
					current = removed;
					current.setLeftWall(false);

				} else if(removed.equals(bottom)) {
					// BOTTOM
					current.setBottomWall(false);
					current = removed;
					current.setTopWall(false);

				} else if(removed.equals(left)) {
					// LEFT
					current.setLeftWall(false);
					current = removed;
					current.setRightWall(false);

				} 
				temp.reset();
				current.reset();
				temp.draw(g);
				current.draw(g);
				temp.drawLines(g);
				current.drawLines(g);
				current.setVisited(true);
				unvisited_set_hash.remove(current);

				TimeUnit.MILLISECONDS.sleep(2);
			} else if(path.size() > 0) {
				current = path.pop();
			}
		}
		
	}
}
