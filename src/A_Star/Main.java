package A_Star;

import javax.swing.JFrame;

public class Main {
	
	private JFrame frame;
	
	public Main() {
		frame = new JFrame();
		Gameplay gameplay = new Gameplay();
		Settings settings = new Settings(frame);
		
		Toolbar toolbar = new Toolbar(gameplay, settings);
		int brickWidth = 800 / gameplay.getTotalRows();
		int width = (gameplay.getTotalRows() * brickWidth) + 400;
		frame.pack();
		frame.setBounds(10, 10, width, 835);
		frame.setTitle("A* Path Finding Algorithm Visualizer");
		frame.setResizable(false);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.add(toolbar);
	}

	public static void main(String[] args) {
		new Main();
	}

}
