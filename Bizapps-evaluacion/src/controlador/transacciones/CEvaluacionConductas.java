package controlador.transacciones;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import modelo.maestros.Competencia;
import modelo.maestros.ConductaCompetencia;
import modelo.maestros.Dominio;
import modelo.maestros.Evaluacion;
import modelo.maestros.EvaluacionCompetencia;
import modelo.maestros.EvaluacionConducta;
import modelo.pk.EvaluacionCompetenciaPK;
import modelo.pk.EvaluacionConductaPK;
import modelo.seguridad.Usuario;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import controlador.maestros.CGenerico;
import componentes.Mensaje;

public class CEvaluacionConductas extends CGenerico {

	private static final long serialVersionUID = -5393608637902961029L;
	Mensaje msj = new Mensaje();

	@Wire
	private Listbox lbxConductasRectoras;
	@Wire
	private Listbox lbxConductasEn;
	@Wire
	private Listbox lbxConductas;
	@Wire
	private Groupbox gConductas;
	@Wire
	private Panel panConductas;
	@Wire
	private Window wdwConductasRectoras;
	@Wire
	private Button btnCancelar;
	@Wire
	private Button btnGuardar;
	private static int idCompetencia;
	private static int idDominio;
	private static int numeroEvaluacion;
	private static Integer idEva;

	EvaluacionCompetencia evaluacionCompetencia = new EvaluacionCompetencia();
	EvaluacionConducta evaluacionConducta = new EvaluacionConducta();
	EvaluacionCompetenciaPK evaluacionCompetenciaPk = new EvaluacionCompetenciaPK();
	EvaluacionConductaPK evaluacionConductaPk = new EvaluacionConductaPK();
	Evaluacion evaluacion;
	List<EvaluacionConducta> conductasE = new ArrayList<EvaluacionConducta>();

	@Override
	public void inicializar() throws IOException {

		// Selectors.wireComponents(Component comp, this, false);
		HashMap<String, Object> map = (HashMap<String, Object>) Sessions
				.getCurrent().getAttribute("itemsCatalogo");
		if (map != null) {
			if (map.get("id") != null) {
				String dominio1 = (String) (map.get("idnivel"));
				idEva = (Integer) map.get("idEva");
				System.out.println("vi"+idEva);
				idDominio = Integer.parseInt(dominio1);
				idCompetencia = (Integer) map.get("id");
				conductasE = (List<EvaluacionConducta>) map.get("conductas");
				Competencia competencia = servicioCompetencia
						.buscarCompetencia(idCompetencia);

				String titulo = (String) (map.get("titulo"));
				wdwConductasRectoras.setTitle(titulo + " - "
						+ competencia.getDescripcion());

				// Dominio dominio = servicioDominio.buscarDominio(idDominio);
				Authentication auth = SecurityContextHolder.getContext()
						.getAuthentication();
				Usuario u = servicioUsuario.buscarUsuarioPorNombre(auth
						.getName());
				String ficha = u.getCedula();
				numeroEvaluacion = servicioEvaluacion.buscar(ficha).size();
				evaluacion = servicioEvaluacion.buscarIdEvaluacion(
						numeroEvaluacion, ficha);
				Dominio dominio = servicioDominio.buscarDominio(idDominio);
				List<ConductaCompetencia> conductas = new ArrayList<ConductaCompetencia>();
				conductas = servicioConductaCompetencia
						.buscarConductaCompetenciasDominio(competencia,dominio);
				
				lbxConductasRectoras
						.setModel(new ListModelList<ConductaCompetencia>(
								conductas));
			
				
				multiple();
			
				if (conductasE != null){
					lbxConductasEn.setModel(new ListModelList<EvaluacionConducta>(
							conductasE));
				lbxConductasRectoras.renderAll();
				for (int i = 0; i < conductasE.size(); i++) {
					Integer id = conductasE.get(i).getConductaCompetencia()
							.getId();
					Evaluacion evaluacion = servicioEvaluacion.buscarEvaluacion(idEva);
					for (int j = 0; j < lbxConductasRectoras.getItemCount(); j++) {
						Listitem listItem = lbxConductasRectoras
								.getItemAtIndex(j);
						ConductaCompetencia cc = listItem.getValue();
						int id2 = cc.getId();
						if (id == id2) {
							listItem.setSelected(true);
							EvaluacionConducta ec = servicioEvaluacionConducta.buscar(evaluacion, cc);
							String observacion = ec.getObservacion();
							System.out.println(observacion);
							((Textbox) ((listItem.getChildren().get(2))).getFirstChild()).setValue((observacion));
						}

					}

				}
				}
			}
		}
		lbxConductasEn.setVisible(false);
	}

	public void multiple() {
		lbxConductasRectoras.setCheckmark(false);
		lbxConductasRectoras.setMultiple(false);
		lbxConductasRectoras.setCheckmark(true);
		lbxConductasRectoras.setMultiple(true);

	}

	@Listen("onClick = #btnCancelar")
	public void cancelar() {
		wdwConductasRectoras.onClose();
	}

	@Listen("onClick = #btnGuardar")
	public void guardar() {
		System.out.println("domimio" +idDominio);
		evaluacion = servicioEvaluacion.buscarEvaluacion(idEva);
		Competencia competencia = servicioCompetencia.buscarCompetencia(idCompetencia); 
		evaluacionCompetencia.setCompetencia(competencia);
		evaluacionCompetencia.setEvaluacion(evaluacion);
		evaluacionCompetencia.setIdDominio(idDominio);

//		boolean campoBlanco = false;

		for (int i = 0; i < lbxConductasRectoras.getItems().size(); i++) {
			List<Listitem> listItem2 = lbxConductasRectoras.getItems();

			if (listItem2.get(i).isSelected()) {
				Listitem listItem = lbxConductasRectoras.getItemAtIndex(i);
//				if (((Textbox) ((listItem.getChildren().get(2)))
//						.getFirstChild()).getValue().equals("")) {
//					campoBlanco = true;
//				}
			}
		}
//
//		if (campoBlanco == true) {
//			Messagebox.show(
//					"Debe ingresar una observacion a los items seleccionados",
//					"Error", Messagebox.OK, Messagebox.ERROR);
//
//		} else {
			List<EvaluacionConducta> evaluacionesC = new ArrayList<EvaluacionConducta>();

			for (int i = 0; i < lbxConductasRectoras.getItems().size(); i++) {
				List<Listitem> listItem2 = lbxConductasRectoras.getItems();
				if (listItem2.get(i).isSelected()) {
					Listitem listItem = lbxConductasRectoras.getItemAtIndex(i);
					ConductaCompetencia conductaCompe = listItem2.get(i)
							.getValue();
					String observacion = ((Textbox) ((listItem.getChildren()
							.get(2))).getFirstChild()).getValue();
					evaluacionConducta.setConductaCompetencia(conductaCompe);
					evaluacionConducta.setEvaluacion(evaluacion);
					evaluacionConducta.setObservacion(observacion);
					servicioEvaluacionConducta.guardar(evaluacionConducta);
				}
			}

			servicioEvaluacionCompetencia.guardar(evaluacionCompetencia);
			Messagebox.show("Datos guardados exitosamente", "Información",
					Messagebox.OK, Messagebox.INFORMATION);
			wdwConductasRectoras.onClose();
		}

}