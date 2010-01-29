package practica.jade.adivino.ontologia;

import java.util.Random;

//Clase acertijo que manejará el agente maestro de juego para generar el número
//a adivinar así como el intervalo para acertar con él.
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
	public void setValormínimo(long minimo) {
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
