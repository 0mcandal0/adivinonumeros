package practica.jade.adivino.maestro;

import jade.content.AgentAction;
import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

import java.util.Vector;

import practica.jade.adivino.ontologia.Acertijo;
import practica.jade.adivino.ontologia.Informar;
import practica.jade.adivino.ontologia.Intento;
import practica.jade.adivino.ontologia.OntologiaAdivino;
import practica.jade.adivino.ontologia.Resultado;
import practica.jade.adivino.ontologia.Vocabulario;

@SuppressWarnings("serial")
public class Maestro extends GuiAgent implements Vocabulario {

	// Vector con los jugadores que contactaron con el maestro
	Vector<AID> jugadores = new Vector<AID>();

	// Acertijo actual a adivinar
	Acertijo acertijo;

	//Variable de control para el bloqueo de mensajes antes de empezar el juego
	private boolean juegoiniciado = false;
	
	//Comportamientos con el sistema bloqueo y la recepción y respuesta de mensajes
	//Se utilizan variables para tener un mayor grado de control en la activación y
	//desactivación de los comportamientos
	BloquearMaestro bloquearmaestro = null;
	RecibirMensajes recibirmensajes = null;

	// Definición del lenguaje y la ontología
	private Codec codec = new SLCodec();
	private Ontology ontologia = OntologiaAdivino.getInstance();

	// Referencia al intefaz gráfico
	transient protected MaestroGUI gui;

	// Configuración inicial del agente
	@Override
	protected void setup() {
		gui = new MaestroGUI(this);
		gui.setVisible(true);

		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontologia);

		addBehaviour(new RegistrarEnDF(this));

		//El agente maestro de juego comienza con un comportamiento cíclico 
		//bloqueado cualquier mensaje de inicio de juego
		bloquearmaestro = new BloquearMaestro();
		addBehaviour(bloquearmaestro);
		
	}
	

	// Registra el agente en las páginas amarillas
	class RegistrarEnDF extends OneShotBehaviour {
		RegistrarEnDF(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			ServiceDescription sd = new ServiceDescription();
			sd.setType(MAESTRO_TIPO);
			sd.setName(MAESTRO);
			DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName(getAID());
			dfd.addServices(sd);
			try {
				DFAgentDescription[] dfds = DFService.search(myAgent, dfd);
				if (dfds.length > 0) {
					DFService.deregister(myAgent, dfd);
				}
				DFService.register(myAgent, dfd);
				gui.Log("Maestro iniciado y registrado en DF");
			} catch (Exception ex) {
				System.out.println("Error al intentar registrar el agente en las páginas amarillas.");
				ex.printStackTrace();
				doDelete();
			}
		}
	}

	//Envía la propuesta y espera 5 segs a que el jugador responda
	//Este comportamiento está definido con WHEN_ANY. En el momento que termine uno, terminan todos 
	class EsperarRespuesta extends ParallelBehaviour {
		EsperarRespuesta(Agent a) {
			
			super(a, WHEN_ANY); 
			
			if(recibirmensajes!=null) removeBehaviour(recibirmensajes);
			recibirmensajes = new RecibirMensajes(myAgent);
			addSubBehaviour(recibirmensajes);
			addSubBehaviour(new WakerBehaviour(myAgent, 5000) {
				@Override
				protected void handleElapsedTimeout() {
					gui.Log("ERROR - No hay respuesta del jugador.");
				}
			});
		}

	}

	// Envía un mensaje incluyendo una acción contemplada en la ontología al
	// jugador destino
	class EnviarMensaje extends OneShotBehaviour {
		int performativa;
		AID destino;
		AgentAction accion;

		EnviarMensaje(Agent a, int performativa, AID destino, AgentAction accion) {
			super(a);
			this.performativa = performativa;
			this.destino = destino;
			this.accion = accion;
		}

		@Override
		public void action() {
			if (destino == null) {
				gui.Log("ERROR - No soy capaz de localizar " + destino.getLocalName() + ". Se canceló la operación");
				return;
			}
			ACLMessage msg = new ACLMessage(performativa);
			msg.setLanguage(codec.getName());
			msg.setOntology(ontologia.getName());
			try {
				getContentManager().fillContent(msg,
						new Action(destino, accion));
				msg.addReceiver(destino);
				send(msg);
				System.out.println("Contactando con " + destino.getLocalName() + "...");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

	// Informa a todos los jugadores menos al acertante que se terminó el juego
	class InformarJugadoresFinPartida extends OneShotBehaviour {

		AID jugadoracertante;

		InformarJugadoresFinPartida(Agent a, AID jugadoracertante) {
			super(a);
			this.jugadoracertante = jugadoracertante;
		}

		@Override
		public void action() {
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM_IF);
			msg.setLanguage(codec.getName());
			msg.setOntology(ontologia.getName());
			try {
				if (jugadores.size() > 1) {
					for (AID jug : jugadores) {
						if (!jugadoracertante.equals(jug))
							msg.addReceiver(jug);
					}
					Informar inf = new Informar();
					inf.setMensaje("Fin de la partida. Jugador Acertante: "	+ jugadoracertante.getLocalName());
					Action act = new Action();
					act.setActor(myAgent.getAID());
					act.setAction(inf);
					getContentManager().fillContent(msg, act);
					send(msg);

				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}

	}

	// Recibe mensajes de los jugadores e inicia la respuesta de los mismos al
	// jugador
	class RecibirMensajes extends SimpleBehaviour {
		
		private boolean terminado = false;

		public RecibirMensajes(Agent a) {
			super(a);
		}

		public void action() {
			ACLMessage msg = receive();
		
			if (msg == null) {
				block();
				return;
			}
			try {
				
				ContentElement content = getContentManager().extractContent(msg);
				Concept action = ((Action) content).getAction();

				// Según la performativa se responde de modo diferente
				switch (msg.getPerformative()) {
				case ACLMessage.CFP:
//Recibe el mensaje CFP del jugador y responde con una propuesta
					gui.Log("CFP - Recibida CFP de " + msg.getSender().getLocalName());
					gui.Log("PROPOSE - Haciendo PROPOSE a " + msg.getSender().getLocalName());
					// Se envía un mensaje de tipo propuesta al jugador y se
					// espera la respuesta
					addBehaviour(new EnviarMensaje(myAgent,ACLMessage.PROPOSE, msg.getSender(),new Informar()));
					addBehaviour(new EsperarRespuesta(myAgent));
					break;
				case ACLMessage.FAILURE:
//El jugador fallo al manejar el CFP					
					gui.Log("FAILURE - " + msg.getSender().getLocalName() + " falló en la respuesta al CFP");
					break;
				case ACLMessage.REJECT_PROPOSAL:
//El jugador rechazó la propuesta					
					gui.Log("REJECT_PROPOSAL - " + msg.getSender().getLocalName() + " rechazó la propuesta de juego");
					break;
				case ACLMessage.ACCEPT_PROPOSAL:
//El jugador aceptó la propuesta
					gui.Log("ACCEPT_PROPOSAL - " + msg.getSender().getLocalName() + " aceptó la propuesta de juego");

					// Añade el jugador al vector con jugadores del juego
					if (!jugadores.contains(msg.getSender())) {
						jugadores.add(msg.getSender());
					}

					// Envía el intervalo donde se encuentra el valor a adivinar
					// en la acción resultado
					Resultado res = new Resultado();
					res.setMinimo(acertijo.getValorminimo());
					res.setMaximo(acertijo.getValormaximo());

					// Envía el mensaje con el intervalo al jugador y espera respuesta
					addBehaviour(new EnviarMensaje(myAgent,ACLMessage.REQUEST, msg.getSender(), res));
					addBehaviour(new EsperarRespuesta(myAgent));

					break;
				case ACLMessage.QUERY_IF:
//El jugador envía un intento al maestro. El maestro informa si acertó o si el resultado es > o <					
					if (action instanceof Intento) {
						System.out.println(msg.getSender().getLocalName() + " lo intenta con el " + String.valueOf(((Intento) action).getValor()));

						res = comprobarintento((Intento) action);
						SequentialBehaviour sb = new SequentialBehaviour();
						// Informar del resultado al jugador
						sb.addSubBehaviour(new EnviarMensaje(myAgent,ACLMessage.INFORM, msg.getSender(), res));
 						if (res.getResultado() == IGUAL) {
							gui.Log(msg.getSender().getLocalName() + " acertó el número " + String.valueOf(acertijo.getValor()));
							sb.addSubBehaviour(new InformarJugadoresFinPartida(myAgent, msg.getSender()));
						}	
						sb.addSubBehaviour(new EsperarRespuesta(myAgent));
						addBehaviour(sb);
					} else
						responderNoComprendo(msg);
					break;
				case ACLMessage.CONFIRM:
//El jugador envía un mensaje de confirmación del resultado. El maestro para el juego.				
					gui.Log("CONFIRM - " + msg.getSender().getLocalName() + " está de acuerdo con el resultado");
					gui.PararJuego();
					break;
				default:
					responderNoComprendo(msg);
				}
			} catch (Exception ex) {
				gui.Log("ERROR - " + ex.toString() + msg.toString());
			}
			terminado = true;
		}

		@Override
		public boolean done() {
			return terminado;
		}
	}

	// Comprueba un intento
	private Resultado comprobarintento(Intento intento) {
		Resultado ri = new Resultado();
		ri.setMinimo(acertijo.getValorminimo());
		ri.setMaximo(acertijo.getValormaximo());
		if (intento.getValor() < acertijo.getValor())
			ri.setResultado(MAYOR);
		else if (intento.getValor() > acertijo.getValor())
			ri.setResultado(MENOR);
		else
			ri.setResultado(IGUAL);

		return ri;

	}

	//Se bloquean todos los mensajes enviados al maestro
	//Para evitar que la cola de mensajes se llene antes de empezar el juego
	class BloquearMaestro extends Behaviour {
		@Override
		public void action() {
			ACLMessage msg = receive();
			if(msg !=null) {
				
				switch (msg.getPerformative()) {
				case ACLMessage.CONFIRM:
//En este comportamiento cíclico se recogen las confirmaciones de fin de juego
//en aquellos jugadores que respondieron tarde
					gui.Log("CONFIRM - " + msg.getSender().getLocalName() + " está de acuerdo con el resultado");
					break;
				default:
					Informar i = new Informar();
					i.setMensaje("No se ha iniciado ningún juego");
					addBehaviour(new EnviarMensaje(myAgent, ACLMessage.REFUSE, msg.getSender(), i));
				}
			}
			else
				block();
		}

		@Override
		public boolean done() {
			return juegoiniciado;
		}
	}
	
	

	// Responde al agente si no comprendió un mensaje
	private void responderNoComprendo(ACLMessage msg) {
		try {
			ContentElement content = getContentManager().extractContent(msg);
			ACLMessage reply = msg.createReply();
			reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
			getContentManager().fillContent(reply, content);
			send(reply);
			System.out.println("¡No comprendo!");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// Actualización que viene del interfaz gráfico
	@Override
	protected void onGuiEvent(final GuiEvent ge) {
		int comando = ge.getType();
		switch (comando) {
		case EMPEZAR_JUEGO:
//Evento del GUI para indicar que empieza el juego			
			//Se desbloquea el agente
			juegoiniciado = true;
			if(bloquearmaestro!=null) removeBehaviour(bloquearmaestro);
			
			gui.BorrarLog();
			
			// Creación de un nuevo acertijo y activado del juego
			acertijo = new Acertijo((Integer) ge.getParameter(0), (Integer) ge.getParameter(1));
			gui.setValorAcertar(acertijo.getValor());
			gui.Log("Comienzo del juego");
			
			//Se arranca el comportamiento simple de recepción de mensajes
			if(recibirmensajes!=null) removeBehaviour(recibirmensajes);
			recibirmensajes = new RecibirMensajes(this);
			addBehaviour(recibirmensajes);
			break;
		case TERMINAR_JUEGO:
//Evento del GUI para indicar que termina el juego

			//Se cancela el comportamiento de recepción de mensajes
			if(recibirmensajes!=null) removeBehaviour(recibirmensajes);

			//Se bloquea el agente maestro
			juegoiniciado = false;
			if(bloquearmaestro!=null) removeBehaviour(bloquearmaestro);
			bloquearmaestro = new BloquearMaestro();
			addBehaviour(bloquearmaestro);
			
			gui.Log("Fin de la partida");
			break;
		case SALIR:
//Evento del GUI para cerrar el agente maestro			
			System.out.println("Cerrando agente");
			this.doDelete();
		}

	}
	
	
	// Limpieza del agente al salir
	@Override
	protected void takeDown() {
		if(gui!=null) {
			gui.dispose();
			gui.setVisible(false);
		}

		// Deregistro de las páginas amarillas
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		System.exit(0);
	}

}
