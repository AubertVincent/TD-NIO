/**
 * J<i>ava</i> U<i>tilities</i> for S<i>tudents</i>
 */
package jus.aor.nio.v3;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import jus.util.WriterArea;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import java.awt.GridLayout;
import java.awt.FlowLayout;

/**
 * @author morat 
 */
public class ServerGUI extends JFrame{
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JPanel panel;
	private JLabel lblHost;
	private JTextField PORT;
	private JButton START;
	private NioServer nioServer;
  private Logger logger;
  private JScrollPane scrollPane;
	private WriterArea writerArea;
	private JButton btnClear;
	private JPanel panel_1;
	private JPanel panel_2;
	private JLabel label;

	/**
	 * Launch the application.
	 * @param args 
	 */
	public static void main(String[] args){
		EventQueue.invokeLater(new Runnable(){
			public void run(){
				try{
					ServerGUI frame = new ServerGUI();
					frame.setVisible(true);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ServerGUI(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		contentPane.add(getPanel(), BorderLayout.NORTH);
		contentPane.add(getScrollPane(), BorderLayout.CENTER);
		//----------------------------------------------------
		try{
			nioServer = new NioServer(Integer.parseInt(PORT.getText()));
		}catch(NumberFormatException|IOException e){
			e.printStackTrace();
		}
		// récupération du niveau de log
		java.util.logging.Level level;
		level = Level.FINE;			
		/* Mise en place du logger pour tracer l'application */
		String loggerName = "jus/aor/nio/v3/NioServer."+PORT.getText();
		logger = Logger.getLogger(loggerName);
		logger.addHandler(new IOHandler(writerArea.out));
		logger.setUseParentHandlers(false);
		logger.setLevel(level);
	}

	private JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.setLayout(new GridLayout(0, 1, 0, 0));
			panel.add(getPanel_1());
			panel.add(getPanel_2());
		}
		return panel;
	}
	private JLabel getLblHost() {
		if (lblHost == null) {
			lblHost = new JLabel("Port :");
		}
		return lblHost;
	}
	private JTextField getPORT() {
		if (PORT == null) {
			PORT = new JTextField();
			PORT.setText("8888");
			PORT.setColumns(5);
		}
		return PORT;
	}
	private JButton getSTART() {
		if (START == null) {
			START = new JButton("Start");
			START.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					new Thread(nioServer).start();
				}
			});
		}
		return START;
	}
	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(writerArea=new WriterArea());
		}
		return scrollPane;
	}
	private JButton getBtnClear() {
		if (btnClear == null) {
			btnClear = new JButton("Clear");
			btnClear.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					writerArea.clear();
				}
			});
		}
		return btnClear;
	}
	private JPanel getPanel_1() {
		if (panel_1 == null) {
			panel_1 = new JPanel();
			panel_1.add(getLblHost());
			panel_1.add(getPORT());
			panel_1.add(getSTART());
			panel_1.add(getBtnClear());
		}
		return panel_1;
	}
	private JPanel getPanel_2() {
		if (panel_2 == null) {
			panel_2 = new JPanel();
			FlowLayout flowLayout = (FlowLayout) panel_2.getLayout();
			flowLayout.setAlignment(FlowLayout.LEFT);
			panel_2.add(getLabel());
		}
		return panel_2;
	}
	private JLabel getLabel() {
		if (label == null) {
			label = new JLabel("                                               Date                id     size     ex    n°");
			label.setHorizontalAlignment(SwingConstants.LEFT);
		}
		return label;
	}
}
