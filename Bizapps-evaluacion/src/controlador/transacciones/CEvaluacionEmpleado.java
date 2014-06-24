package controlador.transacciones;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder.Case;

import modelo.maestros.Competencia;
import modelo.maestros.ConductaCompetencia;
import modelo.maestros.Dominio;
import modelo.maestros.Empleado;
import modelo.maestros.Evaluacion;
import modelo.maestros.EvaluacionConducta;
import modelo.maestros.EvaluacionIndicador;
import modelo.maestros.EvaluacionObjetivo;
import modelo.maestros.NivelCompetenciaCargo;
import modelo.maestros.Perspectiva;
import modelo.seguridad.Usuario;

import org.hibernate.hql.internal.ast.tree.CaseNode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Spinner;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import controlador.maestros.CGenerico;
import componentes.Mensaje;


public class CEvaluacionEmpleado extends CGenerico {

	private static final long serialVersionUID = -5393608637902961029L;
	Mensaje msj = new Mensaje();

		@Wire
	private Textbox txtFortalezas;
	@Wire
	private Textbox txtOportunidades;
	@Wire
	private Textbox txtResumen;
	@Wire
	private Textbox txtCompromisos;
	@Wire
	private Textbox txtValorResultado;
	@Wire
	private Textbox txtAnterior;
	@Wire
	private Button btnGuardar;
	@Wire
	private Button btnGuardarObjetivo;
	@Wire
	private Button btnImprimir;
	@Wire
	private Button btnOk;
	@Wire
	private Button btnGuardarIndicador;
	@Wire
	private Listbox lbxIndicadoresAgregados;
	@Wire
	private Listbox lbxObjetivosGuardados;
	@Wire
	private Listbox lbxCompetenciaEspecifica;
	@Wire
	private Combobox cmbObjetivos;
	@Wire
	private Window window;
	@Wire
	private Groupbox gpxAgregar;
	@Wire
	private Groupbox gpxAgregados;
	@Wire
	private Label lblEvaluacion;
	@Wire
	private Label lblFechaCreacion;
	@Wire
	private Label lblRevision;
	@Wire
	private Textbox txtObjetivo;
	@Wire
	private Textbox txtCorresponsables;
	@Wire
	private Textbox txtPeso;
	@Wire
	private Textbox txtTotal;
	@Wire
	private Textbox txtResultados;
	@Wire
	private Button btnAgregar;
	@Wire
	private Button btnCancelar;
	@Wire
	private Button btnCalculo;
	@Wire
	private Listbox lbxCompetenciaRectora;
	@Wire
	private Listbox lbxObjetivos;
	@Wire
	private Label lblFicha;
	@Wire
	private Label lblNombreTrabajador;
	@Wire
	private Label lblCargo;
	@Wire
	private Label lblUnidadOrganizativa;
	@Wire
	private Label lblGerencia;
	@Wire
	private Combobox cmbNivelRequerido;	
	@Wire
	private Window wdwConductasRectoras;
	@Wire
	private Window winEvaluacionEmpleado;
	@Wire
	private Groupbox gpxListaPersonal;
	String tipo = "EVIDENCIADO";
	ListModelList<Dominio> dominio;
	private static Double valor;
	private static Double total = 0.0;
	private Double cambio;

	@Override
	public void inicializar() throws IOException {
		
		HashMap<String, Object> map = (HashMap<String, Object>) Sessions
				.getCurrent().getAttribute("itemsCatalogo");
		if (map != null) {
			if (map.get("id") != null) {
				Integer idEvaluacion = (Integer) map.get("id");
				String fichaMap  =  (String) map.get("titulo");
				
				System.out.println(idEvaluacion);
			
		Evaluacion evaluacion = servicioEvaluacion.buscarEvaluacion(idEvaluacion);
		txtCompromisos.setValue(evaluacion.getCompromisos());
		txtFortalezas.setValue(evaluacion.getFortalezas());
		txtOportunidades.setValue(evaluacion.getOportunidades());
		txtResumen.setValue(evaluacion.getResumen());
		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
		String ficha = evaluacion.getFicha();
		Integer numeroEvaluacion = servicioEvaluacion.buscar(ficha).size();
		Empleado empleado = servicioEmpleado.buscarPorFicha(ficha);
		
		List<EvaluacionObjetivo> evaluacionObjetivo = new ArrayList<EvaluacionObjetivo> ();
		evaluacionObjetivo = servicioEvaluacionObjetivo.buscarObjetivosEvaluar(idEvaluacion);
		lbxObjetivosGuardados.setModel(new ListModelList<EvaluacionObjetivo>(evaluacionObjetivo));
		
		List<EvaluacionObjetivo> evaluacionObjetivoIndicadores = servicioEvaluacionObjetivo.buscarObjetivosEvaluar(idEvaluacion);
		cmbObjetivos.setModel(new ListModelList<EvaluacionObjetivo>(evaluacionObjetivoIndicadores));
		
		String cargo = empleado.getCargo().getDescripcion();
		String unidadOrganizativa = empleado.getUnidadOrganizativa()
				.getDescripcion();
		String gerenciaReporte = empleado.getUnidadOrganizativa().getGerencia()
				.getDescripcion();
		String nombreTrabajador = empleado.getNombre();
		
		List<NivelCompetenciaCargo> nivel = new ArrayList<NivelCompetenciaCargo>();
		List<NivelCompetenciaCargo> nivel2 = new ArrayList<NivelCompetenciaCargo>();
		List<NivelCompetenciaCargo> nivel3 = new ArrayList<NivelCompetenciaCargo>();
		List<NivelCompetenciaCargo> nivel4 = new ArrayList<NivelCompetenciaCargo>();
		NivelCompetenciaCargo nivelRectoras = new NivelCompetenciaCargo ();
		NivelCompetenciaCargo nivelEspecificas = new NivelCompetenciaCargo ();
		
		nivel = servicioNivelCompetenciaCargo.buscar(empleado.getCargo());
		for (int j = 0; j < nivel.size(); j++) {
			if (nivel.get(j).getCompetencia().getNivel().equals("RECTORAS")) {
				nivelRectoras = nivel.get(j);
				nivel2.add(nivelRectoras);
			}
				else {
				nivel.remove(j);
			}
				lbxCompetenciaRectora
						.setModel(new ListModelList<NivelCompetenciaCargo>(
								nivel2));
		}
		nivel4 = servicioNivelCompetenciaCargo.buscar(empleado.getCargo());
		for (int j = 0; j < nivel4.size(); j++) {
			if (nivel4.get(j).getCompetencia().getNivel().equals("ESPECIFICAS")) {
				nivelEspecificas = nivel4.get(j);
				nivel3.add(nivelEspecificas);
			}
				else {
				nivel4.remove(j);
			}
			lbxCompetenciaEspecifica
						.setModel(new ListModelList<NivelCompetenciaCargo>(
								nivel3));
		}
		
		
		lblFicha.setValue(ficha);
		lblNombreTrabajador.setValue(nombreTrabajador);
		lblCargo.setValue(cargo);
		lblUnidadOrganizativa.setValue(unidadOrganizativa);
		lblGerencia.setValue(gerenciaReporte);
		lblEvaluacion.setValue(numeroEvaluacion.toString());
		lblFechaCreacion.setValue(formatoFecha.format(fechaHora));
	}
		}
	}
	
	
	public Double getCambio() {
		evaluar ();
		return cambio;
	}

	public ListModelList<Dominio> getDominio(){
		dominio = new ListModelList<Dominio> (servicioDominio.buscarPorTipo(tipo));
		return dominio;
	}
	
	@Listen("onDoubleClick = #lbxCompetenciaRectora")
	public void mostrarCompetenciasR() {
			
			
			if (lbxCompetenciaRectora.getItemCount() != 0) {
				
				Listitem listItem = lbxCompetenciaRectora.getSelectedItem();	
				if (listItem != null) {
					if ( ((Combobox) ((listItem.getChildren().get(2)))
							.getFirstChild()).getValue() == ""){
						Messagebox.show("Debe Seleccionar un nivel de dominio",
								"Error", Messagebox.OK,
								Messagebox.ERROR);
					}else{
						String nivel = ((Combobox) ((listItem.getChildren().get(2)))
								.getFirstChild()).getSelectedItem().getDescription();
						String titulo = "Competencias Rectoras";
					NivelCompetenciaCargo competencia = (NivelCompetenciaCargo) listItem.getValue();
					final HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("id", competencia.getCompetencia().getId());
					map.put("idnivel", nivel);
					map.put("titulo", titulo);
					Sessions.getCurrent().setAttribute("itemsCatalogo", map);
					wdwConductasRectoras = (Window) Executions.createComponents("/vistas/transacciones/VEvaluacionConductas.zul", null, map);
					wdwConductasRectoras.doModal();				
					}
					}							
				
			}
	
}
	
	@Listen("onSelect = #cmbObjetivos")
	public void mostrarIndicadores(){
		lbxIndicadoresAgregados.getItems().clear();
		List<EvaluacionIndicador> evaluacionObjetivoIndicador = new ArrayList<EvaluacionIndicador> ();
		Integer idObjetivo = Integer.parseInt(cmbObjetivos.getSelectedItem().getContext());
		evaluacionObjetivoIndicador = servicioEvaluacionIndicador.buscarIndicadores(idObjetivo);
		lbxIndicadoresAgregados.setModel(new ListModelList<EvaluacionIndicador>(evaluacionObjetivoIndicador));
		
	}
	
	@Listen("onDoubleClick = #lbxCompetenciaEspecifica")
	public void mostrarCompetenciasE() {
			
			
			if (lbxCompetenciaEspecifica.getItemCount() != 0) {
				
				Listitem listItem = lbxCompetenciaEspecifica.getSelectedItem();	
				if (listItem != null) {
					if ( ((Combobox) ((listItem.getChildren().get(2)))
							.getFirstChild()).getValue() == ""){
						Messagebox.show("Debe Seleccionar un nivel de dominio",
								"Error", Messagebox.OK,
								Messagebox.ERROR);
					}else{
						String nivel = ((Combobox) ((listItem.getChildren().get(2)))
								.getFirstChild()).getSelectedItem().getDescription();
								
					NivelCompetenciaCargo competencia = (NivelCompetenciaCargo) listItem.getValue();
					final HashMap<String, Object> map = new HashMap<String, Object>();
					String titulo = "Competencias Especificas";
					map.put("id", competencia.getCompetencia().getId());
					map.put("idnivel", nivel);
					map.put("titulo", titulo);
					Sessions.getCurrent().setAttribute("itemsCatalogo", map);
					wdwConductasRectoras = (Window) Executions.createComponents("/vistas/transacciones/VEvaluacionConductas.zul", null, map);
					wdwConductasRectoras.doModal();				
					}
					}							
				
			}
		
	}
	
	@Listen("onClick = #btnGuardarObjetivo")
	public void guardarObjetivos() {

		boolean campoBlanco = false;

		for (int i = 0; i < lbxObjetivosGuardados.getItems().size(); i++) {
			List<Listitem> listItem2 = lbxObjetivosGuardados.getItems();
			
			Listitem listItem = lbxObjetivosGuardados.getItemAtIndex(i);		
			if (((Textbox) ((listItem.getChildren().get(5))).getFirstChild())
					.getValue().equals("") && ((Textbox) ((listItem.getChildren().get(6))).getFirstChild())
					.getValue().equals("")) {
				campoBlanco = true;
			}
			}
		
		
		if (campoBlanco == true) {
			Messagebox.show(
					"Debe ingresar la Evaluacion de los Objetivos",
					"Error", Messagebox.OK, Messagebox.ERROR);

		} else {
			List<EvaluacionObjetivo> evaluacionesO = new ArrayList<EvaluacionObjetivo>();

			for (int i = 0; i < lbxObjetivosGuardados.getItems().size(); i++) {
				List<Listitem> listItem2 = lbxObjetivosGuardados.getItems();
				EvaluacionObjetivo EvaluacionO = listItem2.get(i).getValue();
				Integer idObjetivo = EvaluacionO.getIdObjetivo();
				Listitem listItem = lbxObjetivosGuardados.getItemAtIndex(i);
				EvaluacionObjetivo objetivos = listItem2.get(i).getValue();
				String totalIndicador = ((Textbox) ((listItem.getChildren().get(5)))
						.getFirstChild()).getValue();
				String resultado = ((Textbox) ((listItem.getChildren().get(6)))
						.getFirstChild()).getValue();
				
				EvaluacionObjetivo evaluacion = servicioEvaluacionObjetivo.buscarObjetivosId(idObjetivo);
				evaluacion.setTotalInd(Double.parseDouble(totalIndicador));
				evaluacion.setResultado(Double.parseDouble(resultado));
				servicioEvaluacionObjetivo.guardar(evaluacion);
				}
			

			Messagebox.show("Datos guardados exitosamente", "Información",
					Messagebox.OK, Messagebox.INFORMATION);
		}

	}
	
	@Listen("onClick = #btnGuardarIndicador")
	public void prueba () {
		evaluar ();
	}
	
//	@Listen("onClick = #btnGuardarIndicador")
	public void guardarIndicadores() {

		boolean campoBlanco = false;

		for (int i = 0; i < lbxIndicadoresAgregados.getItems().size(); i++) {
			List<Listitem> listItem2 = lbxIndicadoresAgregados.getItems();
			
			Listitem listItem = lbxIndicadoresAgregados.getItemAtIndex(i);		
			if (((Textbox) ((listItem.getChildren().get(6))).getFirstChild())
					.getValue().equals("") && ((Textbox) ((listItem.getChildren().get(7))).getFirstChild())
					.getValue().equals("") && ((Textbox) ((listItem.getChildren().get(8))).getFirstChild())
					.getValue().equals("") && ((Textbox) ((listItem.getChildren().get(9))).getFirstChild())
					.getValue().equals("") ) {
				campoBlanco = true;
			}
			}
		
		
		if (campoBlanco == true) {
			Messagebox.show(
					"Debe ingresar la Evaluacion de los Indicadores",
					"Error", Messagebox.OK, Messagebox.ERROR);

		} else {
			List<EvaluacionIndicador> evaluacionesI = new ArrayList<EvaluacionIndicador>();

			for (int i = 0; i < lbxIndicadoresAgregados.getItems().size(); i++) {
				List<Listitem> listItem2 = lbxIndicadoresAgregados.getItems();
				EvaluacionIndicador EvaluacionI = listItem2.get(i).getValue();
				Integer idIndicador = EvaluacionI.getIdIndicador();
				Listitem listItem = lbxIndicadoresAgregados.getItemAtIndex(i);
				EvaluacionIndicador indicadores = listItem2.get(i).getValue();
				Integer valorResultado = ((Spinner) ((listItem.getChildren().get(6)))
						.getFirstChild()).getValue();
				Integer resultadoFyAnterior = ((Spinner) ((listItem.getChildren().get(7)))
						.getFirstChild()).getValue();
				String resultado = ((Textbox) ((listItem.getChildren().get(8)))
						.getFirstChild()).getValue();
				String resultadoPeso = ((Textbox) ((listItem.getChildren().get(9)))
						.getFirstChild()).getValue();
				
				EvaluacionIndicador indicador = servicioEvaluacionIndicador.buscarIndicadorId(idIndicador);
				indicador.setValorResultado(valorResultado);
				indicador.setResultadoFyAnterior(resultadoFyAnterior);
				indicador.setResultadoPorc(Double.parseDouble(resultado));
				indicador.setResultadoPeso(Double.parseDouble(resultadoPeso));
				indicador.setTotal(total);
				servicioEvaluacionIndicador.guardar(indicador);
				}
			

			Messagebox.show("Datos guardados exitosamente", "Información",
					Messagebox.OK, Messagebox.INFORMATION);
		}

	}
	
	@Listen("onClick = #btnCancelar")
	public void salir() {

		winEvaluacionEmpleado.onClose();
	}

	public void evaluar (){
	
		for (int i = 0; i < lbxIndicadoresAgregados.getItems().size(); i++) {
			List<Listitem> listItem2 = lbxIndicadoresAgregados.getItems();
			EvaluacionIndicador EvaluacionI = listItem2.get(i).getValue();
			Listitem listItem = lbxIndicadoresAgregados.getItemAtIndex(i);
			Integer valorResultado = ((Spinner) ((listItem.getChildren().get(6)))
					.getFirstChild()).getValue();
			Integer resultadoFyAnterior = ((Spinner) ((listItem.getChildren().get(7)))
					.getFirstChild()).getValue();
			String resultado = ((Textbox) ((listItem.getChildren().get(8)))
					.getFirstChild()).getValue();
			String resultadoPeso = ((Textbox) ((listItem.getChildren().get(9)))
					.getFirstChild()).getValue();
			if(EvaluacionI.getMedicion().getDescripcionMedicion().equals("CONTINUA")) {
				if ((valorResultado) < EvaluacionI.getValorMeta()) {
	                if ((valorResultado) >= (resultadoFyAnterior)) {
	                	resultado =   "85";
	                }
	                else{
	                	resultado=   "0";
	                    }
	                }
				else {
	                   if ((valorResultado) > EvaluacionI.getValorMeta()) {
	                	   resultado =  "115";
	                    }
	                   else {
	                       if ((valorResultado) == EvaluacionI.getValorMeta()){
	                    	   resultado =  "100";
	                       }
	                   }
				}
			}
			else{
						valor = ((valorResultado / EvaluacionI.getValorMeta()) * 100);
						System.out.println("valor" +valor);
						Integer valor1= valor.intValue();
						System.out.println("valor1" +valor1);
				        resultado  = calcularPorcentajeMetaIndicado(valor1).toString();
				        System.out.println("resultado" +resultado);
				            }
			
			resultadoPeso = String.valueOf((Double.parseDouble(resultado) * (EvaluacionI.getPeso()) / 100));
			((Textbox) ((listItem.getChildren().get(8)))
					.getFirstChild()).setValue((resultado));
			((Textbox) ((listItem.getChildren().get(9)))
					.getFirstChild()).setValue((resultadoPeso));
			total = total + Double.parseDouble(resultadoPeso);
			System.out.println(total);
			
		}
		
	}

	public Integer calcularPorcentajeMetaIndicado (Integer valor) {
		System.out.println("pasooooo");
		if (valor < 0)
			valor =   0;
		if  (valor >= 0 && valor <= 24)
			valor =  0;
		if (valor >= 25 && valor <= 49)
			valor =  25;
		if (valor >= 50 && valor <= 84)
			valor = 50;
		if (valor >= 85 && valor <= 94)
			valor =  85;
		if (valor >= 95 && valor <= 100)
			valor =  100;
		if (valor >= 101)
			valor =  115;
		System.out.println(valor);
		return valor;
	}
		
//		switch (valor) {
//			case    0..24:
//				if (valor < 0)
//				valor =   0;
//			break;
//			case 2:
//				if (valor >= 0 && valor <= 24)
//					valor =  0;
//			break;
//			case 3:
//				if (valor >= 25 && valor <= 49)
//					valor =  25;
//			break;
//			case 4:
//				if (valor >= 50 && valor <= 84)
//					valor = 50;
//			break;
//			case 90:
//				if (valor >= 85 && valor <= 94)
//					System.out.println("entrooo");
//				valor =  85;
//			break;
//			case 6:
//				if (valor >= 95 && valor <= 100)
//					valor =  100;
//			break;
//			case 7:
//				if (valor <= 101)
//					valor =  115;
//			break;
//		default:
//			break;
//		
//	}
//		return valor;
//}
	

}