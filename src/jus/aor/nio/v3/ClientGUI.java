/**
 * J<i>ava</i> U<i>tilities</i> for S<i>tudents</i>
 */
package jus.aor.nio.v3;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import jus.util.WriterArea;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;
import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import java.awt.GridLayout;
import java.awt.FlowLayout;

/**
 * @author morat 
 */
public class ClientGUI extends JFrame{
	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	private JPanel panel;
	private JLabel lblNewLabel;
	private JTextField NB_ITERATIONS;
	private JButton START;
	private WriterArea writerArea;
	private JLabel lblHost;
	private JTextField HOST;
	private JLabel lblPort;
	private JTextField PORT;
	private JPanel panel_1;
	private JLabel lblClientId;
	private NioClient nioClient;
	private Logger logger;
	private JButton CLEAR;
	private JScrollPane scrollPane;
	private JTextField ID;
	private boolean allReadyStarted=false;
	private JPanel panel_2;
	private JPanel panel_3;
	private JLabel lblNewLabel_1;

	/**
	 * Launch the application.
	 * @param args 
	 */
	public static void main(String[] args){
		EventQueue.invokeLater(new Runnable(){
			public void run(){
				try{
					ClientGUI frame = new ClientGUI();
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
	public ClientGUI(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 557, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		contentPane.add(getPanel(), BorderLayout.NORTH);
		contentPane.add(getScrollPane(), BorderLayout.CENTER);
		contentPane.add(getPanel_1(), BorderLayout.SOUTH);
	}

	private JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.setLayout(new GridLayout(2, 1, 0, 0));
			panel.add(getPanel_2());
			panel.add(getPanel_3());
		}
		return panel;
	}
	private JLabel getLblNewLabel() {
		if (lblNewLabel == null) {
			lblNewLabel = new JLabel("# itérations :");
		}
		return lblNewLabel;
	}
	private JTextField getNB_ITERATIONS() {
		if (NB_ITERATIONS == null) {
			NB_ITERATIONS = new JTextField();
			NB_ITERATIONS.setText("1000");
			NB_ITERATIONS.setColumns(7);
		}
		return NB_ITERATIONS;
	}
	private JButton getSTART() {
		if (START == null) {
			START = new JButton("Start");
			START.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					int clientId = Integer.parseInt(ID.getText());
					if(!allReadyStarted) {
						// récupération du niveau de log
						java.util.logging.Level level;
						level = Level.FINE;			
						/* Mise en place du logger pour tracer l'application */
						String loggerName = "jus/aor/nio/v3/NioClient."+clientId;
						logger = Logger.getLogger(loggerName);
						logger.setUseParentHandlers(false);
						logger.addHandler(new IOHandler(writerArea.out));
						contentPane.add(getScrollPane(), BorderLayout.CENTER);
						logger.setLevel(level);
						allReadyStarted=true;
					}
					try{
						nioClient = new NioClient(HOST.getText(),Integer.parseInt(PORT.getText()),clientId,Integer.parseInt(NB_ITERATIONS.getText()));
					}catch(NumberFormatException|IOException e){
						e.printStackTrace();
					}
					new Thread(nioClient).start();
				}
			});
		}
		return START;
	}
	private JLabel getLblHost() {
		if (lblHost == null) {
			lblHost = new JLabel("Host :");
		}
		return lblHost;
	}
	private JTextField getHOST() {
		if (HOST == null) {
			HOST = new JTextField();
			HOST.setText("localhost");
			HOST.setColumns(25);
		}
		return HOST;
	}
	private JLabel getLblPort() {
		if (lblPort == null) {
			lblPort = new JLabel("Port :");
		}
		return lblPort;
	}
	private JTextField getPORT() {
		if (PORT == null) {
			PORT = new JTextField();
			PORT.setText("8888");
			PORT.setColumns(5);
		}
		return PORT;
	}
	private JPanel getPanel_1() {
		if (panel_1 == null) {
			panel_1 = new JPanel();
			panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.X_AXIS));
			panel_1.add(getLblNewLabel());
			panel_1.add(getNB_ITERATIONS());
			panel_1.add(getSTART());
			panel_1.add(getCLEAR());
		}
		return panel_1;
	}
	private JLabel getLblClientId() {
		if (lblClientId == null) {
			lblClientId = new JLabel("Client ID : ");
			lblClientId.setHorizontalAlignment(SwingConstants.LEFT);
		}
		return lblClientId;
	}
	private JButton getCLEAR() {
		if (CLEAR == null) {
			CLEAR = new JButton("Clear");
			CLEAR.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					writerArea.clear();
				}
			});
		}
		return CLEAR;
	}
	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(writerArea= new WriterArea());
		}
		return scrollPane;
	}
	private JTextField getID() {
		if (ID == null) {
			ID = new JTextField();
			ID.setText("1");
			ID.setColumns(3);
		}
		return ID;
	}
	private JPanel getPanel_2() {
		if (panel_2 == null) {
			panel_2 = new JPanel();
			panel_2.add(getLblHost());
			panel_2.add(getHOST());
			panel_2.add(getLblPort());
			panel_2.add(getPORT());
			panel_2.add(getLblClientId());
			panel_2.add(getID());
		}
		return panel_2;
	}
	private JPanel getPanel_3() {
		if (panel_3 == null) {
			panel_3 = new JPanel();
			FlowLayout flowLayout = (FlowLayout) panel_3.getLayout();
			flowLayout.setAlignment(FlowLayout.LEFT);
			panel_3.add(getLblNewLabel_1());
		}
		return panel_3;
	}
	private JLabel getLblNewLabel_1() {
		if (lblNewLabel_1 == null) {
			lblNewLabel_1 = new JLabel("                                               Date                id     size     ex    n°");
			lblNewLabel_1.setHorizontalAlignment(SwingConstants.LEFT);
		}
		return lblNewLabel_1;
	}
}
