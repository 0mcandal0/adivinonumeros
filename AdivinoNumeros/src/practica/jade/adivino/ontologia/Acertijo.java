package practica.jade.adivino.ontologia;

import java.util.Random;

//Clase acertijo que manejar� el agente maestro de juego para generar el n�mero
//a adivinar as� como el intervalo para acertar con �l.
public class Acertijo {
	long valorminimo;
	long valormaximo;
	long valor;
	long valorintentado;
	
	
	public Acertijo(long minimo,long maximo) {
		valorminimo=minimo;
		valormaximo=maximo;
		Random randomGenerator = new Random();
		long rango = maximo - minimo + 1;
	    long fraccion = (long)(rango * randomGenerator.nextDouble());
	    valor = fraccion + minimo;    
	}
	
	public long getValorminimo() {
		return valorminimo;
	}
	public void setValorm�nimo(long minimo) {
		this.valorminimo = minimo;
	}
	public long getValormaximo() {
		return valormaximo;
	}
	public void setValormaximo(long valormaximo) {
		this.valormaximo = valormaximo;
	}
	public long getValor() {
		return valor;
	}
	public void setValor(long l) {
		this.valor = l;
	}
	public long getValorintentado() {
		return valorintentado;
	}
	public void setValorintentado(long valorintentado) {
		this.valorintentado = valorintentado;
	}

}
