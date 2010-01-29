package practica.jade.adivino.ontologia;


//Interfaz con el vocabulario común a la ontología
public interface Vocabulario {
	
	public static final String MAESTRO="Maestro";
	public static final String MAESTRO_TIPO="Maestro-Adivinanza";
	
	public static final String JUGADOR="Jugador-Adivinanza";
	
	
	public static final int EMPEZAR_JUEGO=0;
	public static final int TERMINAR_JUEGO=1;
	public static final int SALIR=2;
	
	public static final int JUGADOR_CONECTAR=0;
	public static final int JUGADOR_ADIVINAR=1;
	
	public static final int NO_DISPONIBLE=0;
	public static final int MAYOR=1;
	public static final int MENOR=2;
	public static final int IGUAL=3;
	
	
	public static final String MAXIMO="maximo";
	public static final String MINIMO="minimo";
	
	public static final String ACERTIJO="Acertijo";
	public static final String ACERTIJO_VALOR="valor";
	public static final String ACERTIJO_VALOR_INTENTADO="intentado";
	public static final String ACERTIJO_VALOR_MINIMO="minimo";
	public static final String ACERTIJO_VALOR_MAXIMO="maximo";
	
	public static final String RESULTADO="Resultado";
	public static final String RESULTADO_MAXIMO="maximo";
	public static final String RESULTADO_MINIMO="minimo";
	public static final String RESULTADO_RESULTADO="resultado";
	
	public static final String INTENTO="Intento";
	public static final String INTENTO_VALOR="valor";
	
	public static final String INFORMAR="Informar";
	public static final String INFORMAR_MENSAJE="mensaje";
	
}
