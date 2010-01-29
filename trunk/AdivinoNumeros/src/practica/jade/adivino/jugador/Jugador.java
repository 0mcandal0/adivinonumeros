package practica.jade.adivino.jugador;

import jade.content.AgentAction;
import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import practica.jade.adivino.ontologia.Informar;
import practica.jade.adivino.ontologia.Intento;
import practica.jade.adivino.ontologia.OntologiaAdivino;
import practica.jade.adivino.ontologia.Resultado;
import practica.jade.adivino.ontologia.Vocabulario;

@SuppressWarnings("serial")
public class Jugador extends GuiAgent implements Vocabulario {

	// Maestro de juego
	private AID maestro;

	// Interfaz gráfica
	transient protected JugadorGUI gui;

	// Último valor intentado, intervalo de búsqueda del valor
	private long valorintentado;
	private long valorminmaximointentado;
	private long valormaxminimointentado;

	// Último resultado enviado por el maestro
	private Resultado ultimoresultado;
	
	ControlEspera controlespera=null;

	// Definición del lenguaje y la ontología
	private Codec codec = new SLCodec();
	private Ontology ontologia = OntologiaAdivino.getInstance();

	// Espera la respuesta del maestro
	class ControlEspera extends WakerBehaviour {

		@Override
		protected void handleElapsedTimeout() {
			gui.Log("ERROR - No hay respuesta del maestro.");
			falloCFP();
			gui.ActivarJuego(false);
		}

		public ControlEspera(Agent a, long timeout) {
			super(a, timeout);
		}
	
	}
	
	// Recibe y maneja las respuestas del maestro de juego
	class RecibirMensajes extends CyclicBehaviour {
		
		RecibirMensajes(Agent a) { super(a); }

		@Override
		public void action() {
			//Sólo ve los mensajes enviados por el maestro a este jugador
			ACLMessage msg = receive(MessageTemplate.MatchSender(maestro));
			if (msg == null) {
				block();
				return;
			}
			
			try {
				ContentElement content = getContentManager().extractContent(msg);
				Concept accion = ((Action) content).getAction();
				Informar informar;
				Intento intento;
			
				// Según la performativa se responde de modo diferente
				switch (msg.getPerformative()) {
//El maestro de juego envía la propuesta y el jugador contesta aceptándola			
				case ACLMessage.PROPOSE:
					gui.Log("PROPOSE - Llegó la propuesta del maestro para empezar el juego");
					
					//Se desactiva el control de espera para el CFP inicial de arranque
					if(controlespera!=null)	removeBehaviour(controlespera);
					
					gui.Log("ACCEPT_PROPOSAL - Acepto la propuesta del maestro para empezar el juego");
					informar = new Informar();
					informar.setMensaje("Acepto la propuesta");
					addBehaviour(new EnviarMensaje(myAgent, ACLMessage.ACCEPT_PROPOSAL, informar));
					
					break;
				case ACLMessage.REQUEST:
// El maestro envía el mínimo y máximo valor si confirmó la propuesta aceptada del jugador
					if (accion instanceof Resultado) {
						gui.Log("REQUEST - Llegó la propuesta del maestro para empezar el juego");
						ultimoresultado = (Resultado) accion;
						ultimoresultado.setResultado(NO_DISPONIBLE);
						
						if(!gui.getAdivinacionLenta()) 
							adivinarNumero();	
						else
							adivinarNumeroLento();
						
						intento = new Intento();
						intento.setValor(valorintentado);
						
						gui.Log("QUERY_IF - Probando con..." + String.valueOf(valorintentado));
						//Respondo al maestro con un intento
						addBehaviour(new EnviarMensaje(myAgent, ACLMessage.QUERY_IF, intento));
					} else {
						System.out.println("\n\tResultado no esperado del maestro "	+ content.toString());
					}
					break;
				case ACLMessage.INFORM:
//El maestro informa sobre el intento enviado por el jugador					
					if (accion instanceof Resultado) {
						ultimoresultado = (Resultado) accion;
						if (ultimoresultado.getResultado() != IGUAL) {
							if (ultimoresultado.getResultado() == MAYOR)
								gui.Log("INFORM - El número a adivinar es MAYOR que " + String.valueOf(valorintentado));
							else
								gui.Log("INFORM - El número a adivinar es MENOR que " + String.valueOf(valorintentado));
							// Nuevo intento
							if(!gui.getAdivinacionLenta()) 
								adivinarNumero();	
							else
								adivinarNumeroLento();
							intento = new Intento();
							intento.setValor(valorintentado);
							gui.Log("QUERY_IF - Probando con..." + String.valueOf(valorintentado));
							//El jugador lo intenta otra vez
							addBehaviour(new EnviarMensaje(myAgent, ACLMessage.QUERY_IF, intento));

						} else {
							informar = new Informar();
							informar.setMensaje("Gracias");
							gui.Log("INFORM - ¡¡¡Acerté!!! El número es: " + String.valueOf(valorintentado));
							//Enviamos mensaje de jugador enterado de su acierto
							addBehaviour(new EnviarMensaje(myAgent, ACLMessage.CONFIRM, informar));
						}
						gui.ActivarAdivinar(true);
					} else {
						System.out.println("\n\tResultado no esperado del maestro");
					}
					break;
				case ACLMessage.INFORM_IF:
//El maestro envía a los participantes del juego la información con el ganador del juego
					if (accion instanceof Informar) {
						Informar inf = (Informar) accion;
						gui.Log("INFORM_IF - " + inf.getMensaje());
						gui.ActivarAdivinar(true);
						informar = new Informar();
						informar.setMensaje("Una pena, otra vez será");
						addBehaviour(new EnviarMensaje(myAgent, ACLMessage.CONFIRM, informar));
					} else {
						System.out.println("\n\tResultado no esperado del maestro");
					}
					break;
				case ACLMessage.REFUSE:
//El maestro envía este mensaje para indicar que aún no está preparado (modo bloqueado)					
					if(controlespera!=null)	removeBehaviour(controlespera);
					if (accion instanceof Informar) {
						Informar inf = (Informar) accion;
						gui.Log("REFUSE - " + inf.getMensaje());
						gui.ActivarAdivinar(true);
					} else {
						System.out.println("\n\tResultado no esperado del maestro");
					}
					break;
			
				case ACLMessage.NOT_UNDERSTOOD:
//El maestro envía este mensaje cuando no entiende el mensaje enviado por el jugador					
					if(controlespera!=null)	removeBehaviour(controlespera);
					gui.Log("NOT_UNDERSTOOD - El maestro no entendió el mensaje");
					break;
				default:
					if(controlespera!=null)	removeBehaviour(controlespera);
					System.out.println("\nMensaje no esperado del maestro");
				}
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}

		}
	}

	// Función para la adivinación del número según un intervalo
	// Se utiliza la técnica del punto medio e ir acotando el intervalo de
	// búsqueda
	private void adivinarNumero() {
		if (ultimoresultado.getResultado() == NO_DISPONIBLE
				|| (ultimoresultado.getResultado() != IGUAL && (valorminmaximointentado == valorintentado || valormaxminimointentado == valorintentado))) {
			valorintentado = (long) ((float) (ultimoresultado.getMaximo() - ultimoresultado
					.getMinimo()) / 2);
			valormaxminimointentado = ultimoresultado.getMinimo();
			valorminmaximointentado = ultimoresultado.getMaximo();
		}
		if (ultimoresultado.getResultado() == MAYOR) {
			valormaxminimointentado = valorintentado;
			valorintentado += (long) ((float) (valorminmaximointentado - valorintentado) / 2);
		} else if (ultimoresultado.getResultado() == MENOR) {
			valorminmaximointentado = valorintentado;
			valorintentado = valormaxminimointentado
					+ (long) ((float) (valorintentado - valormaxminimointentado) / 2);
		}
	}

	//Función lenta de adivinación
	private void adivinarNumeroLento() {
		if (ultimoresultado.getResultado() == NO_DISPONIBLE) {
			valorintentado = ultimoresultado.getMinimo();
		}
		if (ultimoresultado.getResultado() == MAYOR) {
			valorintentado++;
		} else if (ultimoresultado.getResultado() == MENOR) {
			valorintentado --;
		}
	}
	
	// Función para buscar el agente maestro de juego
	private void buscarMaestro() {

		ServiceDescription sd = new ServiceDescription();
		sd.setType(MAESTRO_TIPO);
		sd.setName(MAESTRO);
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.addServices(sd);
		try {
			DFAgentDescription[] dfds = DFService.search(this, dfd);
			if (dfds.length > 0) {
				maestro = dfds[0].getName();
				gui.Log("Maestro localizado: " + maestro.getLocalName());
				gui.ActivarJuego(true);
			} else {
				gui.Log("No puedo localizar el maestro");
				gui.ActivarJuego(false);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			gui.Log("Error al buscar en las páginas amarillas");
			gui.ActivarJuego(false);
		}

	}

	
	
	// Envío de un mensaje al maestro de juego, encapsulando una acción definida
	// en la ontología
	class EnviarMensaje extends OneShotBehaviour {
		int performativa;
		AgentAction accion;

		EnviarMensaje(Agent a, int performativa, AgentAction accion) {
			super(a);
			this.performativa = performativa;
			this.accion = accion;
		}

		@Override
		public void action() {
			if (maestro == null) buscarMaestro();
			if (maestro == null){
				gui.Log("ERROR - No soy capaz de localizar " + maestro.getLocalName() + ". Se canceló la operación");
				return;
			}
			ACLMessage msg = new ACLMessage(performativa);
			msg.setLanguage(codec.getName());
			msg.setOntology(ontologia.getName());
			try {
				getContentManager().fillContent(msg,new Action(maestro, accion));
				msg.addReceiver(maestro);
				send(msg);

				System.out.println("Contactando con " + maestro.getLocalName()	+ "...");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}


	// Envío de mensaje CFP al maestro
	void cfp() {
		Informar i = new Informar();
		i.setMensaje("Petición para empezar el juego");
		addBehaviour(new EnviarMensaje(this, ACLMessage.CFP, i));
		//Se crea una espera de 5000 para controlar la respuesta del maestro al CFP
		controlespera = new ControlEspera(this,5000);
		addBehaviour(controlespera);
	}

	void falloCFP() {
		addBehaviour(new EnviarMensaje(this,ACLMessage.FAILURE,new Informar()));
	}

	// Envío de mensaje REJECT_PROPOSAL al maestro
	void rechazarPropuesta() {
		Informar i = new Informar();
		i.setMensaje("Rechazo la propuesta");
		addBehaviour(new EnviarMensaje(this, ACLMessage.REJECT_PROPOSAL, i));
	}
	
	

	protected void setup() {
		gui = new JugadorGUI(this);
		gui.setVisible(true);
		gui.setTitle(this.getLocalName());
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontologia);
		
		//Se arranca el comportamiento cíclico de lectura de mensajes
		addBehaviour(new RecibirMensajes(this));
	}

	@Override
	protected void onGuiEvent(GuiEvent ge) {
		int comando = ge.getType();
		switch (comando) {
		case JUGADOR_CONECTAR:
			gui.Log("Conectando al maestro");
			buscarMaestro();
			break;
		case JUGADOR_ADIVINAR:
			gui.Log("Comenzando proceso de adivinación");
			cfp();
			break;
		case SALIR:
			System.out.println("Cerrando agente");
			this.doDelete();
		}

	}

	// Limpieza del agente
	@Override
	protected void takeDown() {
		if(gui!=null) {
			gui.dispose();
			gui.setVisible(false);
		}
		System.exit(0);
	}

}
