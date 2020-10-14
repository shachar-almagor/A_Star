package A_Star;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashSet;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import A_Star.Node;
import net.miginfocom.swing.MigLayout;

public class Settings extends JPanel implements MouseListener, ChangeListener{

	private JButton zigzagBtn;
	private JButton spiralBtn;
	private JButton randomMazeBtn;
	private JButton clearBtn;
	private JSpinner numRowsPicker;
	private JFrame frame;

	private MazeGenerator mazeGenerator;

	public Settings(JFrame frame) {
		addMouseListener(this);
		this.frame = frame;
		Font font = new Font(Font.MONOSPACED, Font.BOLD, 14);

		this.setLayout(new MigLayout());
		JLabel mazeLabel = new JLabel("Choose Maze:");
		mazeLabel.setForeground(Color.white);
		mazeLabel.setFont(font);

		JLabel rowsLabel = new JLabel("Choose Number of Rows:");
		rowsLabel.setForeground(Color.white);
		rowsLabel.setFont(font);

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
		clearBtn = new JButton("Clear Board");

		numRowsPicker = new JSpinner(new SpinnerNumberModel(50, 2, 50, 1));

		numRowsPicker.setBounds(100, 100, 270, 270);

		numRowsPicker.addChangeListener(this);

		zigzagBtn.addMouseListener(this);
		spiralBtn.addMouseListener(this);
		randomMazeBtn.addMouseListener(this);
		clearBtn.addMouseListener(this);

		zigzagBtn.setFocusable(false);
		spiralBtn.setFocusable(false);
		randomMazeBtn.setFocusable(false);
		clearBtn.setFocusable(false);
		numRowsPicker.setFocusable(false);


		this.add(mazeLabel, "wrap");
		this.add(zigzagBtn, "split");
		this.add(spiralBtn);
		this.add(randomMazeBtn);
		this.add(clearBtn, "wrap");

		this.add(rowsLabel, "wrap");
		this.add(numRowsPicker, "span");

		this.add(blueLabel, "wrap");
		this.add(orangeLabel, "wrap");
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

			mazeGenerator.makeZigzag();
			Toolbar t = (Toolbar) this.getParent();
			t.grabFocus();
			gameplay.repaint();
		} else if(e.getSource() == spiralBtn){
			Gameplay gameplay = ((Toolbar) this.getParent()).getGameplay();
			mazeGenerator = new MazeGenerator(gameplay, gameplay.getTotalRows());

			gameplay.clearBoard();

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
				Toolbar t = (Toolbar) this.getParent();
				t.grabFocus();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

		} else if(e.getSource() == clearBtn) {
			Gameplay gameplay = ((Toolbar) this.getParent()).getGameplay();
			Toolbar t = (Toolbar) this.getParent();
			t.grabFocus();
			gameplay.clearBoard();
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
		gameplay.drawGrid(value, 800, gameplay.getGraphics());
		gameplay.drawGridLines(value, 800, gameplay.getGraphics());
		int brickWidth = 800 / gameplay.getTotalRows();
		int width = (gameplay.getTotalRows() * brickWidth) + 400;
		frame.setBounds(10, 10, width, gameplay.getTotalRows() * brickWidth + 35);

		Toolbar t = (Toolbar) this.getParent();
		t.grabFocus();

	}
}
