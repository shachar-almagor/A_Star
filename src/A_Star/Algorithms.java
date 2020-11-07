package A_Star;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;


public class Algorithms {
	boolean ended = false;

	public boolean breadthFirstSearch(Gameplay gameplay) {
		initAlgorithm(gameplay);
		gameplay.setStarted(true);
		LinkedList<Node> queue = new LinkedList<Node>();
		Node start = gameplay.getStart();
		start.setVisited(true);
		queue.add(start);

		while(!queue.isEmpty()) {
			try {
				// Make this controlled by user (5 - fast, 50 - medium, 200 - slow)
				TimeUnit.MILLISECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Node current = queue.poll();

			if(current.equals(gameplay.getEnd())) {
				// Reconstruct path
				gameplay.reconstruct_path(gameplay.getEnd());
				gameplay.setStarted(false);
				return true;
			}

			if(gameplay.getIsPaintMode()) {
				for(int i = 0; i < current.getPaintModeNeighbors().size(); i++) {
					Node neighbor = current.getPaintModeNeighbors().get(i);

					if(neighbor != null && !neighbor.getVisited()) {
						neighbor.setVisited(true);
						neighbor.set_came_From(current);
						queue.add(neighbor);
						if(!neighbor.equals(gameplay.getEnd())) {
							neighbor.makeClosed();
							neighbor.draw(gameplay.getGraphics());
							neighbor.drawLines(gameplay.getGraphics());
						}
					}
				}
			} else {

				for(int i = 0; i < current.getNeighbors().length; i++) {
					Node neighbor = current.getNeighbors()[i];

					if(neighbor != null && !neighbor.getVisited()) {
						neighbor.setVisited(true);
						neighbor.set_came_From(current);
						queue.add(neighbor);
						if(!neighbor.equals(gameplay.getEnd())) {
							neighbor.makeClosed();
							neighbor.draw(gameplay.getGraphics());
							neighbor.drawLines(gameplay.getGraphics());
						}
					}
				}
			}
		}


		return false;
	}

	public void depthFirstSearch(Gameplay gameplay) {
		initAlgorithm(gameplay);		
		this.ended = false;
		gameplay.setStarted(true);
		Node start = gameplay.getStart();
		start.setVisited(true);
		depthFirstSearchRec(gameplay, start, ended);
	}

	public void depthFirstSearchRec(Gameplay gameplay, Node current, boolean ended) {
		try {
			// Make this controlled by user (5 - fast, 50 - medium, 200 - slow)
			TimeUnit.MILLISECONDS.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if(this.ended) return;

		if(current.equals(gameplay.getEnd())) {
			gameplay.getEnd().setVisited(true);
			gameplay.reconstruct_path(gameplay.getEnd());
			gameplay.setStarted(false);
			this.ended = true;
			return;
		}
		if(!current.equals(gameplay.getEnd())) {
			if(gameplay.getIsPaintMode()) {
				for(int i = 0; i < current.getPaintModeNeighbors().size(); i++) {
					Node neighbor = current.getPaintModeNeighbors().get(i);
					if(neighbor != null && !neighbor.getVisited() && !this.ended) {
						neighbor.setVisited(true);
						neighbor.set_came_From(current);
						if(!neighbor.equals(gameplay.getEnd()) && !this.ended) {
							neighbor.makeClosed();
							neighbor.draw(gameplay.getGraphics());
							neighbor.drawLines(gameplay.getGraphics());
							depthFirstSearchRec(gameplay, neighbor, this.ended);
						} else {
							depthFirstSearchRec(gameplay, neighbor, this.ended);
							this.ended = true;
							break;
						}
					}
				}
			} else {
				for(int i = 0; i < current.getNeighbors().length; i++) {
					Node neighbor = current.getNeighbors()[i];
					if(neighbor != null && !neighbor.getVisited() && !this.ended) {
						neighbor.setVisited(true);
						neighbor.set_came_From(current);
						if(!neighbor.equals(gameplay.getEnd()) && !neighbor.equals(gameplay.getStart())) {
							neighbor.makeClosed();
							neighbor.draw(gameplay.getGraphics());
							neighbor.drawLines(gameplay.getGraphics());
							depthFirstSearchRec(gameplay, neighbor, this.ended);
						} else {
							depthFirstSearchRec(gameplay, neighbor, this.ended);
							this.ended = true;
							break;
						}
					}
				}
			}
		}
	}

	public void dijkstra(Gameplay gameplay) throws InterruptedException {
		initAlgorithm(gameplay);
		int count = 0;
		PriorityQueue<Node> unvisited_set = new PriorityQueue<Node>(5, new DistanceComparator());
		HashSet<Node> visited_set = new HashSet<Node>();

		Node current = gameplay.getStart();
		current.setDistance(0);

		makeUnvisited(gameplay, unvisited_set);

		while(!unvisited_set.isEmpty()) {
//			TimeUnit.NANOSECONDS.sleep(1);
			current = getCurrent(unvisited_set);
			unvisited_set.remove(current);
			current.setVisited(true);
			visited_set.add(current);

			if(gameplay.getIsPaintMode()) {
				for(int i = 0; i < current.getPaintModeNeighbors().size(); i++) {
					Node neighbor = current.getPaintModeNeighbors().get(i);

					if(neighbor != null && !neighbor.getVisited()) {
						double temp = 1 + current.getDistance();
						if(temp < neighbor.getDistance()) {
							neighbor.setDistance(temp);
							neighbor.set_came_From(current);
						}
					}
				}
				if(!current.isStart() && !current.isEnd()) {
					current.makeClosed();
					current.draw(gameplay.getGraphics());
					current.drawLines(gameplay.getGraphics());
				}

			} else {
				for(int i = 0; i < current.getNeighbors().length; i++) {
					Node neighbor = current.getNeighbors()[i];

					if(neighbor != null && !neighbor.getVisited()) {
						double temp = 1 + current.getDistance();
						if(temp < neighbor.getDistance()) {
							neighbor.setDistance(temp);
							neighbor.set_came_From(current);
						}
					}
				}
				if(!current.isStart() && !current.isEnd()) {
					current.makeClosed();
					current.draw(gameplay.getGraphics());
					current.drawLines(gameplay.getGraphics());
				}
			}	
		}

		gameplay.reconstruct_path(gameplay.getEnd());
		gameplay.getEnd().makeEnd();
		gameplay.getStart().makeStart();
		gameplay.setStarted(false);
	}

	public void initAlgorithm(Gameplay gameplay) {
		boolean isPaintMode = gameplay.getIsPaintMode();
		// Start algorithm
		for(int i = 0; i < gameplay.getTotalRows(); i++) {
			for(int j = 0; j < gameplay.getTotalRows(); j++) {

				Node curr = gameplay.getGrid().get(i)[j];

				Node top = curr.getRow() > 0 ? gameplay.getGrid().get(i - 1)[j] : null;
				Node right = curr.getCol() < curr.getTotalRows() - 1 ? gameplay.getGrid().get(i)[j + 1] : null;
				Node bottom = curr.getRow() < curr.getTotalRows() - 1 ? gameplay.getGrid().get(i + 1)[j] : null;
				Node left = curr.getCol() > 0 ? gameplay.getGrid().get(i)[j - 1] : null;

				curr.updateNeighbors(top, right, bottom, left, isPaintMode);
				curr.setVisited(false);
			}
		}
	}

	public void makeUnvisited(Gameplay gameplay, PriorityQueue<Node> unvisited_set) {
		for(int i = 0; i < gameplay.getTotalRows(); i++) {
			for(int j = 0; j < gameplay.getTotalRows(); j++) {
				Node curr = gameplay.getGrid().get(i)[j];
				if(curr != null && !curr.isBarrier()) {
					unvisited_set.add(curr);
					curr.setVisited(false);
				}
			}
		}
	}

	public Node getCurrent(PriorityQueue<Node> unvisited_set) {
		Node minNode = null;
		for (Node node : unvisited_set) {
			if(minNode != null) {
				if(node.getDistance() < minNode.getDistance()) {
					minNode = node;
				}
			} else {
				minNode = node;
			}
		}
		return minNode;
	}


}
