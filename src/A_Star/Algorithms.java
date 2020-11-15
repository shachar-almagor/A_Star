package A_Star;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;


public class Algorithms {

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
					markNeighborBFS(current, neighbor, queue, gameplay);
				}
			} else {
				for(int i = 0; i < current.getNeighbors().length; i++) {
					Node neighbor = current.getNeighbors()[i];
					markNeighborBFS(current, neighbor, queue, gameplay);
				}
			}
		}
		return false;
	}
	
	public void markNeighborBFS(Node current, Node neighbor, LinkedList<Node> queue, Gameplay gameplay) {
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

	public void depthFirstSearch(Gameplay gameplay) {
		initAlgorithm(gameplay);		
		gameplay.setStarted(true);
		Node start = gameplay.getStart();
		start.setVisited(true);
		depthFirstSearchRec(gameplay, start);
	}

	public void depthFirstSearchRec(Gameplay gameplay, Node current) {
		try {
			// Make this controlled by user (5 - fast, 50 - medium, 200 - slow)
			TimeUnit.MILLISECONDS.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if(!gameplay.getStarted()) return;

		if(current.equals(gameplay.getEnd())) {
			gameplay.getEnd().setVisited(true);
			gameplay.reconstruct_path(gameplay.getEnd());
			gameplay.setStarted(false);
			return;
		}
		if(!current.equals(gameplay.getEnd())) {
			if(gameplay.getIsPaintMode()) {
				for(int i = 0; i < current.getPaintModeNeighbors().size(); i++) {
					Node neighbor = current.getPaintModeNeighbors().get(i);
					markNeighborDFS(current, neighbor, gameplay);
				}
			} else {
				for(int i = 0; i < current.getNeighbors().length; i++) {
					Node neighbor = current.getNeighbors()[i];
					markNeighborDFS(current, neighbor, gameplay);
				}
			}
		}
	}
	
	public void markNeighborDFS(Node current, Node neighbor, Gameplay gameplay) {
		if(neighbor != null && !neighbor.getVisited() && gameplay.getStarted()) {
			neighbor.setVisited(true);
			neighbor.set_came_From(current);
			if(!neighbor.equals(gameplay.getEnd()) && !neighbor.equals(gameplay.getStart())) {
				neighbor.makeClosed();
				neighbor.draw(gameplay.getGraphics());
				neighbor.drawLines(gameplay.getGraphics());
			} 
			depthFirstSearchRec(gameplay, neighbor);
		}
	}

	public void dijkstra(Gameplay gameplay) throws InterruptedException {
		initAlgorithm(gameplay);
		int count = 0;
		PriorityQueue<Node> unvisited_set = new PriorityQueue<Node>(5, new Comparator<Node>() {
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
		HashSet<Node> visited_set = new HashSet<Node>();

		Node current = gameplay.getStart();
		current.setDistance(0);
		current.setCount(count++);
		unvisited_set.add(current);

		makeUnvisited(gameplay, unvisited_set);

		while(!unvisited_set.isEmpty()) {
			current = unvisited_set.poll();
			current.setVisited(true);
			visited_set.add(current);
			
			if(!current.isStart() && !current.isEnd()) {
				current.makeClosed();
				current.draw(gameplay.getGraphics());
				current.drawLines(gameplay.getGraphics());
			}

			if(gameplay.getIsPaintMode()) {
				for(int i = 0; i < current.getPaintModeNeighbors().size(); i++) {
					Node neighbor = current.getPaintModeNeighbors().get(i);
					markNeighborDijkstra(current, neighbor, gameplay, unvisited_set, count);
				}
			} else {
				for(int i = 0; i < current.getNeighbors().length; i++) {
					Node neighbor = current.getNeighbors()[i];
					markNeighborDijkstra(current, neighbor, gameplay, unvisited_set, count);
				}
			}	
		}

		gameplay.reconstruct_path(gameplay.getEnd());
		gameplay.getEnd().makeEnd();
		gameplay.getStart().makeStart();
		gameplay.setStarted(false);
	}
	
	public void markNeighborDijkstra(Node current, Node neighbor, Gameplay gameplay, PriorityQueue<Node> unvisited_set, int count) {
		if(neighbor != null && !neighbor.getVisited()) {
			double temp = 1 + current.getDistance();
			if(temp < neighbor.getDistance()) {
				unvisited_set.add(neighbor);
				neighbor.setDistance(temp);
				neighbor.set_came_From(current);
				neighbor.setCount(count++);
			}
		}
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

				curr.updateNeighbors(top, right, bottom, left, gameplay);
				curr.setVisited(false);
			}
		}
	}

	public void makeUnvisited(Gameplay gameplay, PriorityQueue<Node> unvisited_set) {
		for(int i = 0; i < gameplay.getTotalRows(); i++) {
			for(int j = 0; j < gameplay.getTotalRows(); j++) {
				Node curr = gameplay.getGrid().get(i)[j];
				if(curr != null && !curr.isBarrier()) {
					curr.setVisited(false);
				}
			}
		}
	}
}
