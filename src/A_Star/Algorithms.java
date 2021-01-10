package A_Star;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;


public class Algorithms {
	private boolean ended = false;
	private int count = 0;
	private int delay = 0;

	public boolean breadthFirstSearch(Gameplay gameplay) {
		initAlgorithm(gameplay);

		gameplay.setStarted(true);
		LinkedList<Node> queue = new LinkedList<Node>();
		Node start = gameplay.getStart();
		start.setVisited(true);
		queue.add(start);
		
		while(!queue.isEmpty()) {
			try {
				TimeUnit.MICROSECONDS.sleep(this.delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Node current = queue.poll();

			if(current.isEnd()) {
				// Reconstruct path
				gameplay.reconstructPath(gameplay.getEnd());
				return true;
			}
			if(!current.isStart()) {
				current.makeClosed();
				current.draw(gameplay.getGraphics());
				current.drawLines(gameplay.getGraphics());
			}
			for(int i = 0; i < current.getNeighbors().length; i++) {
				Node neighbor = gameplay.getState() == State.Paint ? current.getPaintModeNeighbors()[i] : current.getNeighbors()[i];
				markNeighborBFS(current, neighbor, queue, gameplay);
			}
		}
		return false;
	}

	public void markNeighborBFS(Node current, Node neighbor, LinkedList<Node> queue, Gameplay gameplay) {
		if(neighbor != null && !neighbor.getVisited() && !neighbor.isBarrier()) {
			neighbor.setVisited(true);
			neighbor.setCameFrom(current);
			queue.add(neighbor);
			if(!neighbor.isEnd()) {
				neighbor.makeOpen();
				neighbor.draw(gameplay.getGraphics());
				neighbor.drawLines(gameplay.getGraphics());
			}
		}
	}

	public void depthFirstSearch(Gameplay gameplay) {
		initAlgorithm(gameplay);		
		gameplay.setStarted(true);
		Node start = gameplay.getStart();
		start.setVisited(true);
		depthFirstSearchRec(gameplay, start);
	}

	public void depthFirstSearchRec(Gameplay gameplay, Node current) {
		try {
			TimeUnit.MICROSECONDS.sleep(this.delay * 5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(current.isEnd()) {
			gameplay.getEnd().setVisited(true);
			gameplay.reconstructPath(gameplay.getEnd());
			ended = true;
			return;
		} else if(!current.isStart()) {
			current.makeClosed();
			current.draw(gameplay.getGraphics());
			current.drawLines(gameplay.getGraphics());
		}

		for(int i = 0; i < current.getNeighbors().length; i++) {
			Node neighbor = gameplay.getState() == State.Paint ? current.getPaintModeNeighbors()[i] : current.getNeighbors()[i];
			markNeighborDFS(current, neighbor, gameplay);
		}

	}

	public void markNeighborDFS(Node current, Node neighbor, Gameplay gameplay) {
		if(this.ended) return;

		if(neighbor != null && !neighbor.getVisited() && gameplay.getStarted() && !neighbor.isBarrier()) {
			neighbor.setVisited(true);
			neighbor.setCameFrom(current);
			if(!neighbor.isEnd() && !neighbor.isStart()) {
				neighbor.makeOpen();
				neighbor.draw(gameplay.getGraphics());
				neighbor.drawLines(gameplay.getGraphics());
			} 
			depthFirstSearchRec(gameplay, neighbor);
		}
	}

	public void dijkstra(Gameplay gameplay) throws InterruptedException {

		initAlgorithm(gameplay);

		gameplay.setStarted(true);
		int count = 0;
		PriorityQueue<Node> unvisitedSet = new PriorityQueue<Node>(5, new Comparator<Node>() {
			@Override
			public int compare(Node o1, Node o2) {
				if(o1.getDistance() < o2.getDistance()) {
					return -1;
				} else if(o2.getDistance() < o1.getDistance()) {
					return 1;
				} else {
					// The two nodes have identical distances
					if(o1.getCount() <= o2.getCount()) {
						return -1;
					} else if(o2.getCount() < o1.getCount()) {
						return 1;
					}
				}
				// Compilation requirement
				return 0;
			}
		});
		HashSet<Node> visitedSet = new HashSet<Node>();

		Node current = gameplay.getStart();
		current.setCount(count++);
		unvisitedSet.add(current);

		current.setDistance(0);

		while(!unvisitedSet.isEmpty()) {
			try {
				TimeUnit.MICROSECONDS.sleep(this.delay / 5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			current = unvisitedSet.poll();
			current.setVisited(true);
			visitedSet.add(current);

			if(!current.isStart() && !current.isEnd()) {
				current.makeClosed();
				current.draw(gameplay.getGraphics());
				current.drawLines(gameplay.getGraphics());
			}

			for(int i = 0; i < current.getNeighbors().length; i++) {
				Node neighbor = gameplay.getState() == State.Paint ? current.getPaintModeNeighbors()[i] : current.getNeighbors()[i];
				markNeighborDijkstra(current, neighbor, gameplay, unvisitedSet, count);
			}
		}

		gameplay.reconstructPath(gameplay.getEnd());
		gameplay.getEnd().makeEnd();
		gameplay.getStart().makeStart();
	}

	public void markNeighborDijkstra(Node current, Node neighbor, Gameplay gameplay, PriorityQueue<Node> unvisitedSet, int count) {
		if(neighbor != null && !neighbor.getVisited()) {
			double temp = 1 + current.getDistance();
			if(temp < neighbor.getDistance()) {
				unvisitedSet.add(neighbor);
				neighbor.setDistance(temp);
				neighbor.setCameFrom(current);
				neighbor.setCount(count++);
				if(!neighbor.isEnd()) {
					neighbor.makeOpen();
					neighbor.draw(gameplay.getGraphics());
				}
			}
		}
	}
	
	public void aStar(Gameplay gameplay) {
		Node start = gameplay.getStart();
		Node end = gameplay.getEnd();
		if(!gameplay.getStarted() && start != null && end != null) {
			initAlgorithm(gameplay);
			gameplay.setStarted(true);
			
			PriorityQueue<Node> openSet = new PriorityQueue<Node>(5, new Comparator<Node>() {
				@Override
				public int compare(Node o1, Node o2) {
					if(o1.getfScore() < o2.getfScore()) {
						return -1;
					} else if(o2.getfScore() < o1.getfScore()) {
						return 1;
					} else {
						// The two nodes have identical fScores
						if(o1.getCount() <= o2.getCount()) {
							return -1;
						} else if(o2.getCount() < o1.getCount()) {
							return 1;
						}
					}
					// Compilation requirement
					return 0;
				}
			});
			
			count = 0;
			start.setgScore(0);
			start.setfScore(h(start, end));
			openSet.add(start);
			HashSet<Node> openSetHash = new HashSet<Node>();
			openSetHash.add(start);

			while(!openSet.isEmpty()) {
				try {
					TimeUnit.MICROSECONDS.sleep(this.delay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				Node current = openSet.poll();
				openSetHash.remove(current);

				if(current.isEnd()) {
					// Algorithm done
					gameplay.reconstructPath(end);
					end.makeEnd();
					start.makeStart();
					break;
				}
				if(!current.isStart()) {
					current.makeClosed();
					openSet.remove(current);
					openSetHash.remove(current);
					current.draw(gameplay.getGraphics());
					current.drawLines(gameplay.getGraphics());
				}
				for(int i = 0; i < current.getNeighbors().length; i++) {
					Node neighbor = gameplay.getState() == State.Paint ? current.getPaintModeNeighbors()[i] : current.getNeighbors()[i];
					markNeighborAStar(current, neighbor, openSet, openSetHash, end, gameplay);
				}
			}
		}
	}

	public void markNeighborAStar(Node current, Node neighbor, PriorityQueue<Node> openSet, HashSet<Node> openSetHash, Node end, Gameplay gameplay) {
		if(neighbor != null && !neighbor.isClosed() && !neighbor.isBarrier()) {
			// If the node is closed, disregard it
			double tempgScore = current.getgScore() + 1;
			if(tempgScore < neighbor.getgScore()) {
				neighbor.setCameFrom(current);
				neighbor.setgScore(tempgScore);
				neighbor.setfScore(h(neighbor, end));

				if(!openSetHash.contains(neighbor)) {
					count++;
					neighbor.setCount(count);
					openSet.add(neighbor);
					openSetHash.add(neighbor);
					if(!neighbor.isEnd()) {
						neighbor.makeOpen();
						neighbor.drawLines(gameplay.getGraphics());
						neighbor.draw(gameplay.getGraphics());
					}
				}
			}
		}
	}

	public void initAlgorithm(Gameplay gameplay) {
		// Start algorithm
		for(int i = 0; i < gameplay.getTotalRows(); i++) {
			for(int j = 0; j < gameplay.getTotalRows(); j++) {

				Node curr = gameplay.getGrid().get(i)[j];

				Node top = curr.getRow() > 0 ? gameplay.getGrid().get(i - 1)[j] : null;
				Node right = curr.getCol() < curr.getTotalRows() - 1 ? gameplay.getGrid().get(i)[j + 1] : null;
				Node bottom = curr.getRow() < curr.getTotalRows() - 1 ? gameplay.getGrid().get(i + 1)[j] : null;
				Node left = curr.getCol() > 0 ? gameplay.getGrid().get(i)[j - 1] : null;

				curr.updateNeighbors(top, right, bottom, left, gameplay);
				if(!curr.isBarrier()) {
					curr.setVisited(false);
					curr.setDistance(Double.POSITIVE_INFINITY);
					curr.setfScore(Double.POSITIVE_INFINITY);
					curr.setgScore(Double.POSITIVE_INFINITY);
				}
			}
		}
	}
	
	public double h(Node p1, Node p2) {
		// Measures the distance between p1 and p2 
		int x1 = p1.getX();
		int y1 = p1.getY();
		int x2 = p2.getX();
		int y2 = p2.getY();

		return (Math.abs(x1 - x2) + Math.abs(y1 - y2));
	}

	public boolean getEnded() {
		return this.ended;
	}

	public void setEnded(boolean ended) {
		this.ended = ended;
	}
	
	public int getDelay() {
		return this.delay;
	}
	
	public void setDelay(int delay) {
		this.delay = delay;
	}
}
