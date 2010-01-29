package practica.jade.adivino.jugador;

import jade.gui.GuiEvent;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import practica.jade.adivino.ontologia.Vocabulario;

@SuppressWarnings("serial")
public class JugadorGUI extends JFrame implements ActionListener, Vocabulario {

	private Jugador jugador;
	
	private JPanel jContentPane = null;
	private JButton jButtonConectar = null;
	private JScrollPane jScrollPane = null;
	private JList jListLog = null;
	private JButton jButtonAdivinar = null;
	private JCheckBox jCheckMetodo = null;
    
	private DefaultListModel defaultListModel = null;
	
	/**
	 * This method initializes 
	 * 
	 */
	public JugadorGUI(Jugador j) {
		super();
		this.jugador=j;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        this.setSize(new Dimension(521, 376));
        this.setContentPane(getJContentPane());
        final JugadorGUI fra = this;
        this.addWindowListener(new java.awt.event.WindowAdapter() {
        	public void windowClosing(java.awt.event.WindowEvent e) {
        		int rep = JOptionPane.showConfirmDialog(fra, "¿Está seguro que quiere salir?",
                        jugador.getLocalName(),
                        JOptionPane.YES_NO_CANCEL_OPTION);
      		if (rep == JOptionPane.YES_OPTION) {
      			GuiEvent ge = new GuiEvent(this, SALIR);
      			jugador.postGuiEvent(ge);
      			fra.dispose();
      		}
      		else
      			fra.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        	}
        });

        this.setTitle("Jugador");
			
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == jButtonConectar) {
			GuiEvent ge = new GuiEvent(this, JUGADOR_CONECTAR);
			jugador.postGuiEvent(ge);
		} else if(e.getSource() == jButtonAdivinar) {
			jButtonAdivinar.setEnabled(false);
			//defaultListModel.removeAllElements();
			GuiEvent ge = new GuiEvent(this, JUGADOR_ADIVINAR);
			jugador.postGuiEvent(ge);
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
			jContentPane.add(getJButtonConectar(), null);
			jContentPane.add(getJButton(), null);
			jContentPane.add(getJCheckMetodo(), null);
			jContentPane.add(getJScrollPane(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jButtonConectar	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonConectar() {
		if (jButtonConectar == null) {
			jButtonConectar = new JButton();
			jButtonConectar.setText("Conectar al Maestro");
			jButtonConectar.addActionListener(this);
		}
		return jButtonConectar;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
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
			defaultListModel = new DefaultListModel();
			jListLog.setDoubleBuffered(true);
			jListLog.setModel(defaultListModel);
		}
		return jListLog;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton() {
		if (jButtonAdivinar == null) {
			jButtonAdivinar = new JButton();
			jButtonAdivinar.setText("Adivinar el Número");
			jButtonAdivinar.setEnabled(false);
			jButtonAdivinar.addActionListener(this);
		}
		return jButtonAdivinar;
	}

	/**
	 * This method initializes jCheckMetodo	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getJCheckMetodo() {
		if (jCheckMetodo == null) {
			jCheckMetodo = new JCheckBox();
			jCheckMetodo.setText("Usar el Método Lento de Adivinación");
			jCheckMetodo.setEnabled(false);
		}
		return jCheckMetodo;
	}

	public void Log(String valor) {
		defaultListModel.add(0,valor);
		jListLog.repaint();
	}

	public void ActivarJuego(boolean b) {
		jButtonConectar.setEnabled(!b);
		jButtonAdivinar.setEnabled(b);
		jCheckMetodo.setEnabled(b);	
	}

	public void ActivarAdivinar(boolean b) {
		jButtonAdivinar.setEnabled(b);
		
	}

	public boolean getAdivinacionLenta() {
		return (jCheckMetodo.getSelectedObjects()!=null);
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
