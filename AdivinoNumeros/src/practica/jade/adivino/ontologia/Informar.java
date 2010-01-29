package practica.jade.adivino.ontologia;

import jade.content.AgentAction;

//Clase informar que estará contemplada como acción de la ontología
@SuppressWarnings("serial")
public class Informar implements AgentAction {
	String mensaje;

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
}
