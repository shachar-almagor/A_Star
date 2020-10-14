package A_Star;

import java.util.Comparator;

import A_Star.Node;

public class NodeComparator implements Comparator<Node>{

	@Override
	public int compare(Node o1, Node o2) {
		if(o1.get_f_score() < o2.get_f_score()) {
			return -1;
		} else if(o2.get_f_score() < o1.get_f_score()) {
			return 1;
		} else {
			// The two nodes have identical f_Scores
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
