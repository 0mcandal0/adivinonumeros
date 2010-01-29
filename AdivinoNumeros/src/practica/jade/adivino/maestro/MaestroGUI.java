package practica.jade.adivino.maestro;

import jade.gui.GuiEvent;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import practica.jade.adivino.ontologia.Vocabulario;
import javax.swing.ListSelectionModel;
import javax.swing.DefaultListModel;

@SuppressWarnings("serial")
public class MaestroGUI extends JFrame implements ActionListener, Vocabulario {

	
	private Maestro maestro;
	
	private JPanel jContentPane = null;
	private JButton jButtonArrancar = null;
	private JScrollPane jScrollPane = null;
	private JList jListLog = null;
	private JPanel jPanel = null;
	private JLabel jLabel = null;
	private JTextField jTextFieldMinimo = null;
	private JLabel jLabel1 = null;
	private JTextField jTextFieldMaximo = null;

	private JLabel jLabel2 = null;

	private JTextField jTextFieldValor = null;
	DefaultListModel defaultListModel = null;
	/**
	 * This method initializes 
	 * 
	 */
	public MaestroGUI(Maestro a) {
		super();
		this.maestro = a;
		initialize();
	}
	
	
	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        this.setSize(new Dimension(850, 400));
        this.setMinimumSize(new Dimension(850, 400));
        this.setPreferredSize(new Dimension(850, 400));
        this.setContentPane(getJContentPane());
        this.setTitle("Maestro");
        final MaestroGUI fra = this;
        this.addWindowListener(new java.awt.event.WindowAdapter() {
        	public void windowClosing(java.awt.event.WindowEvent e) {
        		int rep = JOptionPane.showConfirmDialog(fra, "¿Está seguro que quiere salir?",
                          maestro.getLocalName(), JOptionPane.YES_NO_CANCEL_OPTION);
        		if (rep == JOptionPane.YES_OPTION) {
        			GuiEvent ge = new GuiEvent(this, SALIR);
        			maestro.postGuiEvent(ge);
        			fra.dispose();
        		}
        		else
        			fra.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        	}
        });
			
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource()==jButtonArrancar) {
			if(jButtonArrancar.getText().equals("Empezar Juego")) {
				jButtonArrancar.setText("Parar Juego");
				GuiEvent ge = new GuiEvent(this, EMPEZAR_JUEGO);
				ge.addParameter(Integer.parseInt(jTextFieldMinimo.getText()));
				ge.addParameter(Integer.parseInt(jTextFieldMaximo.getText()));
				maestro.postGuiEvent(ge);
			} else {
				jButtonArrancar.setText("Empezar Juego");
				GuiEvent ge = new GuiEvent(this, TERMINAR_JUEGO);
				maestro.postGuiEvent(ge);
			}
		}
	}

	/**
	 * This method initializes jContentPane	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BoxLayout(getJContentPane(), BoxLayout.Y_AXIS));
			jContentPane.setPreferredSize(new Dimension(850, 400));
			jContentPane.add(getJPanel(), null);
			jContentPane.add(getJScrollPane(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jButtonArrancar	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonArrancar() {
		if (jButtonArrancar == null) {
			jButtonArrancar = new JButton();
			jButtonArrancar.setText("Empezar Juego");
			jButtonArrancar.addActionListener(this);
		}
		return jButtonArrancar;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setPreferredSize(new Dimension(850, 400));
			jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			jScrollPane.setViewportView(getJListLog());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jListLog	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getJListLog() {
		if (jListLog == null) {
			jListLog = new JList();
			jListLog.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			jListLog.setSize(new Dimension(850, 400));
			jListLog.setDoubleBuffered(true);
			defaultListModel = new DefaultListModel();
			jListLog.setModel(defaultListModel);
		}
		return jListLog;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jLabel2 = new JLabel();
			jLabel2.setText("Valor a Adivinar");
			jLabel2.setDisplayedMnemonic(KeyEvent.VK_UNDEFINED);
			jLabel2.setHorizontalAlignment(SwingConstants.CENTER);
			GridLayout gridLayout = new GridLayout();
			gridLayout.setRows(1);
			gridLayout.setColumns(7);
			jLabel = new JLabel();
			jLabel.setText("Mínimo");
			jLabel.setHorizontalAlignment(SwingConstants.CENTER);
			jPanel = new JPanel();
			jPanel.setPreferredSize(new Dimension(850, 30));
			jPanel.setLayout(gridLayout);
			jPanel.add(jLabel, null);
			jPanel.add(getJTextFieldMinimo(), null);
			jPanel.add(getJLabel1(), null);
			jPanel.add(getJTextFieldMaximo(), null);
			jPanel.add(jLabel2, null);
			jPanel.add(getJTextFieldValor(), null);
			jPanel.add(getJButtonArrancar(), null);
		}
		return jPanel;
	}

	/**
	 * This method initializes jTextFieldMinimo	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldMinimo() {
		if (jTextFieldMinimo == null) {
			jTextFieldMinimo = new JTextField();
			jTextFieldMinimo.setHorizontalAlignment(JTextField.CENTER);
			jTextFieldMinimo.setFont(new Font("Dialog", Font.BOLD, 24));
			jTextFieldMinimo.setText("0");
		}
		return jTextFieldMinimo;
	}

	/**
	 * This method initializes jLabel1	
	 * 	
	 * @return javax.swing.JLabel	
	 */
	private JLabel getJLabel1() {
		if (jLabel1 == null) {
			jLabel1 = new JLabel();
			jLabel1.setText("Máximo");
			jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return jLabel1;
	}

	/**
	 * This method initializes jTextFieldMaximo	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldMaximo() {
		if (jTextFieldMaximo == null) {
			jTextFieldMaximo = new JTextField();
			jTextFieldMaximo.setHorizontalAlignment(JTextField.CENTER);
			jTextFieldMaximo.setFont(new Font("Dialog", Font.BOLD, 24));
			jTextFieldMaximo.setText("3000000");
		}
		return jTextFieldMaximo;
	}


	/**
	 * This method initializes jTextFieldValor	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldValor() {
		if (jTextFieldValor == null) {
			jTextFieldValor = new JTextField();
			jTextFieldValor.setHorizontalAlignment(JTextField.CENTER);
			jTextFieldValor.setFont(new Font("Dialog", Font.BOLD, 24));
		}
		return jTextFieldValor;
	}


	public void setValorAcertar(long valor) {
		jTextFieldValor.setText(String.valueOf(valor));
	}


	public void Log(String valor) {
		defaultListModel.add(0,valor);
		jListLog.repaint();
	}


	public void BorrarLog() {
		defaultListModel.removeAllElements();
	}


	public void PararJuego() {
		jButtonArrancar.setText("Empezar Juego");
		GuiEvent ge = new GuiEvent(this, TERMINAR_JUEGO);
		maestro.postGuiEvent(ge);
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
