package A_Star;

import java.util.Comparator;

import A_Star.Node;

public class DistanceComparator implements Comparator<Node>{

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

}
