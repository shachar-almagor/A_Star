package A_Star;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Event;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Paint;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashSet;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import A_Star.Node;
import net.miginfocom.swing.MigLayout;

public class Settings extends JPanel implements MouseListener, ChangeListener, ItemListener{

	private JButton zigzagBtn;
	private JButton spiralBtn;
	private JButton randomMazeBtn;
	private JButton paintModeBtn;
	private JButton homeBtn;
	private JButton resetBtn;
	private JSpinner numRowsPicker;
	private JComboBox<String> algoBox;
	private JComboBox<String> speedBox;
	private JFrame frame;

	private String[] algorithms = {"Breadth First Search (BFS)", "Depth First Search (DFS)", "Dijkstra", "A* (A star)"}; 
	private String[] speeds = {"Fast", "Medium", "Slow"};
	private MazeGenerator mazeGenerator;

	public Settings(JFrame frame) {
		addMouseListener(this);
		this.frame = frame;
		Font font = new Font(Font.MONOSPACED, Font.BOLD, 20);

		this.setLayout(new MigLayout("gap 0 20"));
		JLabel mazeLabel = new JLabel("Choose Maze:");
		mazeLabel.setForeground(Color.white);
		mazeLabel.setFont(font);

		JLabel algoLabel = new JLabel("Choose Algorithm:");
		algoLabel.setForeground(Color.white);
		algoLabel.setFont(font);

		JLabel speedLabel = new JLabel("Choose Algorithm Speed:");
		speedLabel.setForeground(Color.white);
		speedLabel.setFont(font);

		JLabel rowsLabel = new JLabel("Choose Number of Rows: (2 - 50)");
		rowsLabel.setForeground(Color.white);
		rowsLabel.setFont(font);

		JLabel homeLabel = new JLabel("Back to home screen:");
		homeLabel.setForeground(Color.white);
		homeLabel.setFont(font);

		algoBox = new JComboBox<String>(algorithms);
		speedBox = new JComboBox<String>(speeds);

		JLabel blueLabel = new JLabel("Blue = Start");
		blueLabel.setForeground(Color.blue);
		JLabel orangeLabel = new JLabel("Orange = End");
		orangeLabel.setForeground(Color.orange);

		blueLabel.setFont(font);
		orangeLabel.setFont(font);

		zigzagBtn = new JButton("Zigzag");
		spiralBtn = new JButton("Spiral");
		randomMazeBtn = new JButton("Random Maze");
		paintModeBtn = new JButton("Paint Mode");
		homeBtn = new JButton("Home");
		resetBtn = new JButton("Reset");

		// Spinner setup
		numRowsPicker = new JSpinner(new SpinnerNumberModel(50, 2, 50, 1));

		Component mySpinnerEditor = numRowsPicker.getEditor();

		JFormattedTextField jftf = ((JSpinner.DefaultEditor) mySpinnerEditor).getTextField();

		jftf.setColumns(3);
		numRowsPicker.setFont(font);	
		((DefaultEditor) mySpinnerEditor).getTextField().setHorizontalAlignment(JTextField.CENTER);

		algoBox.setFont(font);
		speedBox.setFont(font);

		numRowsPicker.addChangeListener(this);

		zigzagBtn.addMouseListener(this);
		spiralBtn.addMouseListener(this);
		randomMazeBtn.addMouseListener(this);
		paintModeBtn.addMouseListener(this);
		algoBox.addItemListener(this);
		speedBox.addItemListener(this);
		homeBtn.addMouseListener(this);
		resetBtn.addMouseListener(this);

		zigzagBtn.setFocusable(false);
		spiralBtn.setFocusable(false);
		randomMazeBtn.setFocusable(false);
		paintModeBtn.setFocusable(false);
		algoBox.setFocusable(false);
		speedBox.setFocusable(false);
		homeBtn.setFocusable(false);
		resetBtn.setFocusable(false);
		numRowsPicker.setFocusable(false);

		this.add(mazeLabel, "wrap");
		this.add(zigzagBtn, "split");
		this.add(spiralBtn);
		this.add(randomMazeBtn);
		this.add(paintModeBtn, "wrap");

		this.add(algoLabel, "wrap");
		this.add(algoBox, "wrap");

		this.add(speedLabel, "wrap");
		this.add(speedBox, "wrap");

		this.add(rowsLabel, "wrap");
		this.add(numRowsPicker, "span");

		this.add(blueLabel, "wrap");
		this.add(orangeLabel, "wrap");

		this.add(homeLabel, "wrap");
		this.add(homeBtn, "split");
		this.add(resetBtn);


		this.setBackground(Color.DARK_GRAY);

	}

	@Override
	public void mousePressed(MouseEvent e) {
		Gameplay gameplay = ((Toolbar) this.getParent()).getGameplay();
		Toolbar t = (Toolbar) this.getParent();
		if(e.getSource() == zigzagBtn) {
			mazeGenerator = new MazeGenerator(gameplay, gameplay.getTotalRows());
			gameplay.setState(State.Zigzag);
			gameplay.setStarted(false);

			gameplay.clearBoard();

			mazeGenerator.makeZigzag();
			t.grabFocus();
			gameplay.repaint();
		} else if(e.getSource() == spiralBtn){
			mazeGenerator = new MazeGenerator(gameplay, gameplay.getTotalRows());
			gameplay.setState(State.Spiral);
			gameplay.setStarted(false);

			gameplay.clearBoard();

			for(int i = 0; i < gameplay.getTotalRows(); i++) {
				for(int j = 0; j < gameplay.getTotalRows(); j++) {
					gameplay.getGrid().get(i)[j].makeBarrier();
				}
			}

			gameplay.repaint();

			mazeGenerator.makeSpiral(gameplay.getGrid().get(0)[0], 1);
			t.grabFocus();
			gameplay.repaint();		
		} else if(e.getSource() == randomMazeBtn) {
			mazeGenerator = new MazeGenerator(gameplay, gameplay.getTotalRows());
			gameplay.setState(State.Random);
			gameplay.setStarted(false);

			gameplay.clearBoard();

			gameplay.getGrid().get(0)[0].setVisited(true);
			HashSet<Node> unvisited_set_hash = new HashSet<Node>();

			for(int i = 0; i < gameplay.getTotalRows(); i++) {
				for(int j = 0; j < gameplay.getTotalRows(); j++) {
					// Adding all of the nodes to the unvisited set
					unvisited_set_hash.add(gameplay.getGrid().get(i)[j]);
					gameplay.getGrid().get(i)[j].makeBarrier();
					gameplay.getGrid().get(i)[j].draw(gameplay.getGraphics());
				}
			}

			Node current = gameplay.getGrid().get(0)[0];
			Graphics g = gameplay.getGraphics();

			Stack<Node> path = new Stack<Node>();
			current.setVisited(true);
			unvisited_set_hash.remove(current);
			current.makeStart();
			current.draw(g);
			current.drawLines(g);
			path.push(current);

			try {
				mazeGenerator.makeRandomMaze(current, path, unvisited_set_hash, gameplay, g);
				t.grabFocus();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

		} else if(e.getSource() == paintModeBtn) {

			gameplay.setState(State.Paint);
			gameplay.setStarted(false);

			t.grabFocus();
			gameplay.clearBoard();

		} else if(e.getSource() == homeBtn) {
			gameplay.setState(State.Home);
			gameplay.setStarted(false);

			t.grabFocus();
			if(!gameplay.getStarted()) {
				gameplay.setStart(null);
				gameplay.setEnd(null);
				gameplay.makeGrid(gameplay.getTotalRows(), 800);
			}
			gameplay.repaint();
		} else if(e.getSource() == resetBtn) {

			t.grabFocus();
			if(gameplay.getStarted()) {
				gameplay.reset();
				gameplay.repaint();
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void stateChanged(ChangeEvent e) {
		int value = (int) numRowsPicker.getValue();
		Gameplay gameplay = ((Toolbar) this.getParent()).getGameplay();

		gameplay.setTotalRows(value);
		gameplay.makeGrid(value, 800);
		if(!(gameplay.getState() == State.Paint)) {
			gameplay.drawGrid(value, 800, gameplay.getGraphics());
			gameplay.drawGridLines(value, 800, gameplay.getGraphics());
		}

		int brickWidth = 800 / gameplay.getTotalRows();
		int width = (gameplay.getTotalRows() * brickWidth) + 400;
		frame.setBounds(10, 10, width, gameplay.getTotalRows() * brickWidth + 35);

		Toolbar t = (Toolbar) this.getParent();
		t.grabFocus();

	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		Gameplay gameplay = ((Toolbar) this.getParent()).getGameplay();
		if(e.getSource() == algoBox) {
			switch((String) algoBox.getSelectedItem()) {
			case("Breadth First Search (BFS)"):
				gameplay.setCurrAlgorithm(CurrentAlgorithm.BreadthFirstSearch);
			break;
			case("Depth First Search (DFS)"):
				gameplay.setCurrAlgorithm(CurrentAlgorithm.DepthFirstSearch);
			break;

			case("Dijkstra"):
				gameplay.setCurrAlgorithm(CurrentAlgorithm.Dijkstra);
			break;

			case("A* (A star)"):
				gameplay.setCurrAlgorithm(CurrentAlgorithm.A_Star);
			break;
			}
		} else if(e.getSource() == speedBox) {
			switch((String) speedBox.getSelectedItem()) {
			case("Fast"):
				gameplay.setDelay(100);
				gameplay.setSpeed(Speed.Fast);
			break;
			case("Medium"):
				gameplay.setDelay(1000);
				gameplay.setSpeed(Speed.Medium);
			break;

			case("Slow"):
				gameplay.setDelay(5000);
				gameplay.setSpeed(Speed.Slow);
			break;
			}
		}
	}
}
