package A_Star;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
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
	private JSpinner numRowsPicker;
	private JComboBox<String> algoBox;
	private JFrame frame;
	private String[] algorithms = {"Breadth First Search (BFS)", "Depth First Search (DFS)", "Dijkstra", "A* (A star)"}; 

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
		
		JLabel homeLabel = new JLabel("Back to home screen:");
		homeLabel.setForeground(Color.white);
		homeLabel.setFont(font);

		JLabel rowsLabel = new JLabel("Choose Number of Rows:");
		rowsLabel.setForeground(Color.white);
		rowsLabel.setFont(font);
		
		algoBox = new JComboBox<String>(algorithms);

		JLabel blueLabel = new JLabel("Blue = Start");
		blueLabel.setForeground(Color.blue);
		JLabel orangeLabel = new JLabel("Orange = End");
		orangeLabel.setForeground(Color.orange);

		JLabel description2 = new JLabel("using the left mouse button. Use the");
		JLabel description3 = new JLabel("right mouse button to delete your painting");
		description2.setForeground(Color.white);
		description3.setForeground(Color.white);

		blueLabel.setFont(font);
		orangeLabel.setFont(font);
		description2.setFont(font);
		description3.setFont(font);

		zigzagBtn = new JButton("Zigzag");
		spiralBtn = new JButton("Spiral");
		randomMazeBtn = new JButton("Random Maze");
		paintModeBtn = new JButton("Paint Mode");
		homeBtn = new JButton("Home");
		
		// Spinner setup
		numRowsPicker = new JSpinner(new SpinnerNumberModel(50, 2, 50, 1));
		
		
		Component mySpinnerEditor = numRowsPicker.getEditor();
		
		JFormattedTextField jftf = ((JSpinner.DefaultEditor) mySpinnerEditor).getTextField();
		
		jftf.setColumns(3);
		numRowsPicker.setFont(font);	
		((DefaultEditor) mySpinnerEditor).getTextField().setHorizontalAlignment(JTextField.CENTER);
        //
		
		algoBox.setFont(font);

		numRowsPicker.addChangeListener(this);

		zigzagBtn.addMouseListener(this);
		spiralBtn.addMouseListener(this);
		randomMazeBtn.addMouseListener(this);
		paintModeBtn.addMouseListener(this);
		algoBox.addItemListener(this);
		homeBtn.addMouseListener(this);

		zigzagBtn.setFocusable(false);
		spiralBtn.setFocusable(false);
		randomMazeBtn.setFocusable(false);
		paintModeBtn.setFocusable(false);
		algoBox.setFocusable(false);
		homeBtn.setFocusable(false);
		numRowsPicker.setFocusable(false);

		this.add(mazeLabel, "wrap");
		this.add(zigzagBtn, "split");
		this.add(spiralBtn);
		this.add(randomMazeBtn);
		this.add(paintModeBtn, "wrap");
		
		this.add(algoLabel, "wrap");
		this.add(algoBox, "wrap");
		


		this.add(rowsLabel, "wrap");
		this.add(numRowsPicker, "span");

		this.add(blueLabel, "wrap");
		this.add(orangeLabel, "wrap");
		
		this.add(homeLabel, "wrap");
		this.add(homeBtn, "wrap");
		//		this.add(description2, "wrap");
		//		this.add(description3, "wrap");


		this.setBackground(Color.DARK_GRAY);

	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getSource() == zigzagBtn) {
			Gameplay gameplay = ((Toolbar) this.getParent()).getGameplay();
			mazeGenerator = new MazeGenerator(gameplay, gameplay.getTotalRows());

			gameplay.clearBoard();
			gameplay.setLanding(false);
			gameplay.setIsPaintMode(false);

			mazeGenerator.makeZigzag();
			Toolbar t = (Toolbar) this.getParent();
			t.grabFocus();
			gameplay.repaint();
		} else if(e.getSource() == spiralBtn){
			Gameplay gameplay = ((Toolbar) this.getParent()).getGameplay();
			mazeGenerator = new MazeGenerator(gameplay, gameplay.getTotalRows());

			gameplay.clearBoard();
			gameplay.setLanding(false);
			gameplay.setIsPaintMode(false);

			for(int i = 0; i < gameplay.getTotalRows(); i++) {
				for(int j = 0; j < gameplay.getTotalRows(); j++) {
					gameplay.getGrid().get(i)[j].makeBarrier();
				}
			}
			
			gameplay.repaint();

			mazeGenerator.makeSpiral(gameplay.getGrid().get(0)[0], 1);
			Toolbar t = (Toolbar) this.getParent();
			t.grabFocus();
			gameplay.repaint();		
		} else if(e.getSource() == randomMazeBtn) {
			Gameplay gameplay = ((Toolbar) this.getParent()).getGameplay();
			mazeGenerator = new MazeGenerator(gameplay, gameplay.getTotalRows());
			
			gameplay.setLanding(false);
			gameplay.setIsPaintMode(false);

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
				gameplay.setLanding(false);
				mazeGenerator.makeRandomMaze(current, path, unvisited_set_hash, gameplay, g);
				Toolbar t = (Toolbar) this.getParent();
				t.grabFocus();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

		} else if(e.getSource() == paintModeBtn) {
			Gameplay gameplay = ((Toolbar) this.getParent()).getGameplay();
			Toolbar t = (Toolbar) this.getParent();
			t.grabFocus();
			gameplay.clearBoard();
			gameplay.setLanding(false);
			gameplay.setIsPaintMode(true);

		} else if(e.getSource() == homeBtn) {
			Gameplay gameplay = ((Toolbar) this.getParent()).getGameplay();
			Toolbar t = (Toolbar) this.getParent();
			t.grabFocus();
			gameplay.clearBoard();
			gameplay.setIsPaintMode(false);
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
		if(!gameplay.getLanding()) {
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
		if(e.getSource() == algoBox) {
			Gameplay gameplay = ((Toolbar) this.getParent()).getGameplay();
			
			gameplay.setCurrAlgorithm((String) algoBox.getSelectedItem());
		}
	}
}
