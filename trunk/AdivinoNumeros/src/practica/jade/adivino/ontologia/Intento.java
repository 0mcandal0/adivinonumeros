package practica.jade.adivino.ontologia;

import jade.content.AgentAction;

//Clase intento que estar� contemplada como acci�n de la ontolog�a
@SuppressWarnings("serial")
public class Intento implements AgentAction {

	long valor;

	public long getValor() {
		return valor;
	}

	public void setValor(long valorintentado) {
		this.valor = valorintentado;
	}

}
