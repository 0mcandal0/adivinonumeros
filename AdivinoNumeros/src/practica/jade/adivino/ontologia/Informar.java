package practica.jade.adivino.ontologia;

import jade.content.AgentAction;

//Clase informar que estar� contemplada como acci�n de la ontolog�a
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
