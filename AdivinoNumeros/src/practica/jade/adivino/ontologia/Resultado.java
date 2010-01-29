package practica.jade.adivino.ontologia;

import jade.content.AgentAction;

//Clase resultado que estar� contemplada como acci�n de la ontolog�a
@SuppressWarnings("serial")
public class Resultado implements AgentAction {
	long resultado; //0 >, 1 < , 2 ==
	long minimo;
	long maximo;
	
	
	public long getMinimo() {
		return minimo;
	}
	public void setMinimo(long l) {
		this.minimo = l;
	}
	public long getMaximo() {
		return maximo;
	}
	public void setMaximo(long maximo) {
		this.maximo = maximo;
	}
	
	public long getResultado() {
		return resultado;
	}
	public void setResultado(long resultado) {
		this.resultado = resultado;
	}
	
}
