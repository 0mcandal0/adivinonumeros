package practica.jade.adivino.ontologia;

import jade.content.onto.BasicOntology;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.schema.AgentActionSchema;
import jade.content.schema.ConceptSchema;
import jade.content.schema.ObjectSchema;
import jade.content.schema.PrimitiveSchema;

//Clase estática Ontología
@SuppressWarnings("serial")
public class OntologiaAdivino extends Ontology implements Vocabulario {

	public static final String ONTOLOGY_NAME = "Ontologia-Adivino";

	// Creada la clase como singleton
	private static Ontology instance = new OntologiaAdivino();
	public static Ontology getInstance() { return instance; }

	private OntologiaAdivino() {
		super(ONTOLOGY_NAME, BasicOntology.getInstance());
		try {

//Conceptos
			//Acertijo
			ConceptSchema cs = new ConceptSchema(ACERTIJO);
			add(cs, Acertijo.class);
			cs.add(ACERTIJO_VALOR, (PrimitiveSchema) getSchema(BasicOntology.INTEGER), ObjectSchema.MANDATORY);
			cs.add(ACERTIJO_VALOR_INTENTADO, (PrimitiveSchema) getSchema(BasicOntology.INTEGER), ObjectSchema.OPTIONAL);
			cs.add(ACERTIJO_VALOR_MINIMO, (PrimitiveSchema) getSchema(BasicOntology.INTEGER), ObjectSchema.MANDATORY);
			cs.add(ACERTIJO_VALOR_MAXIMO, (PrimitiveSchema) getSchema(BasicOntology.INTEGER), ObjectSchema.MANDATORY);

			//Resultado
			add(cs = new ConceptSchema(RESULTADO), Resultado.class);
			cs.add(RESULTADO_MAXIMO, (PrimitiveSchema) getSchema(BasicOntology.INTEGER), ObjectSchema.MANDATORY);
			cs.add(RESULTADO_MINIMO, (PrimitiveSchema) getSchema(BasicOntology.INTEGER), ObjectSchema.MANDATORY);
			cs.add(RESULTADO_RESULTADO, (PrimitiveSchema) getSchema(BasicOntology.INTEGER), ObjectSchema.MANDATORY);
				
//Acciones
			//Intento
			AgentActionSchema as = new AgentActionSchema(INTENTO);
			add(as, Intento.class);
	        as.add(INTENTO_VALOR, (PrimitiveSchema) getSchema(BasicOntology.INTEGER), ObjectSchema.MANDATORY);

	        //Informar
			add(as = new AgentActionSchema(INFORMAR), Informar.class);
			as.add(INFORMAR_MENSAJE, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);
			
			//Resultado
			add(as = new AgentActionSchema(RESULTADO), Resultado.class);
			as.add(RESULTADO_MINIMO, (PrimitiveSchema) getSchema(BasicOntology.INTEGER), ObjectSchema.MANDATORY);
			as.add(RESULTADO_MAXIMO, (PrimitiveSchema) getSchema(BasicOntology.INTEGER), ObjectSchema.MANDATORY);
			as.add(RESULTADO_RESULTADO, (PrimitiveSchema) getSchema(BasicOntology.INTEGER), ObjectSchema.OPTIONAL);

		}
		catch (OntologyException oe) {
			oe.printStackTrace();
		}
	}


}
