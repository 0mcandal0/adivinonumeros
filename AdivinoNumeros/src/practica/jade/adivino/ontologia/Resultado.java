package practica.jade.adivino.ontologia;

import jade.content.AgentAction;

//Clase resultado que estará contemplada como acción de la ontología
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
