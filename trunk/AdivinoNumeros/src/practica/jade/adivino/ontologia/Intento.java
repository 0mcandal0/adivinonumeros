package practica.jade.adivino.ontologia;

import jade.content.AgentAction;

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
