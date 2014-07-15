package controlador.transacciones;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import modelo.maestros.Area;
import modelo.maestros.Cargo;
import modelo.maestros.Clase;
import modelo.maestros.Competencia;
import modelo.maestros.Curso;
import modelo.maestros.Dominio;
import modelo.maestros.Empleado;
import modelo.maestros.EmpleadoClase;
import modelo.maestros.EmpleadoCurso;
import modelo.maestros.NivelCompetenciaCargo;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Doublespinner;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import componentes.Botonera;
import componentes.Catalogo;
import componentes.Mensaje;
import controlador.maestros.CGenerico;

public class CEmpleadoClase extends CGenerico {

	@Wire
	private Window wdwVEmpleadoClase;
	@Wire
	private Button btnGuardar;
	@Wire
	private Button btnLimpiar;
	@Wire
	private Button btnSalir;
	@Wire
	private Textbox txtClaseEmpleadoClase;
	@Wire
	private Button btnBuscarClase;
	@Wire
	private Listbox lsbEmpleado;
	@Wire
	private Div divCatalogoClase;
	List<EmpleadoCurso> empleadosCurso = new ArrayList<EmpleadoCurso>();
	private int idClase = 0;
	private int idCurso = 0;

	Mensaje msj = new Mensaje();
	Catalogo<Clase> catalogoClase;

	@Override
	public void inicializar() throws IOException {
		// TODO Auto-generated method stub

		txtClaseEmpleadoClase.setFocus(true);

	}

	@Listen("onClick = #btnBuscarClase")
	public void mostrarCatalogoClase() {
		final List<Clase> listClase = servicioClase.buscarTodas();
		catalogoClase = new Catalogo<Clase>(divCatalogoClase,
				"Catalogo de Clases", listClase, "Curso", "Contenido",
				"Objetivo", "Facilitador", "Entidad Did�ctica", "Fecha",
				"Duraci�n", "Lugar", "Tipo de Entrenamiento", "Modalidad") {

			@Override
			protected List<Clase> buscarCampos(List<String> valores) {
				List<Clase> lista = new ArrayList<Clase>();

				for (Clase clase : listClase) {
					if (clase.getCurso().getNombre().toLowerCase()
							.startsWith(valores.get(0))
							&& clase.getContenido().toLowerCase()
									.startsWith(valores.get(1))
							&& clase.getObjetivo().toLowerCase()
									.startsWith(valores.get(2))
							&& clase.getFacilitador().toLowerCase()
									.startsWith(valores.get(3))
							&& clase.getEntidadDidactica().toLowerCase()
									.startsWith(valores.get(4))
							&& String
									.valueOf(
											formatoFecha.format(clase
													.getFecha())).toLowerCase()
									.startsWith(valores.get(5))
							&& String.valueOf(clase.getDuracion())
									.toLowerCase().startsWith(valores.get(6))
							&& clase.getLugar().toLowerCase()
									.startsWith(valores.get(7))
							&& clase.getTipoEntrenamiento().toLowerCase()
									.startsWith(valores.get(8))
							&& clase.getModalidad().toLowerCase()
									.startsWith(valores.get(10))) {
						lista.add(clase);
					}
				}
				return lista;

			}

			@Override
			protected String[] crearRegistros(Clase clase) {
				String[] registros = new String[10];
				registros[0] = clase.getCurso().getNombre();
				registros[1] = clase.getContenido();
				registros[2] = clase.getObjetivo();
				registros[3] = clase.getFacilitador();
				registros[4] = clase.getEntidadDidactica();
				registros[5] = String.valueOf(formatoFecha.format(clase
						.getFecha()));
				registros[6] = String.valueOf(clase.getDuracion());
				registros[7] = clase.getLugar();
				registros[8] = clase.getTipoEntrenamiento();
				registros[9] = clase.getModalidad();

				return registros;
			}

			@Override
			protected List<Clase> buscar(String valor, String combo) {
				// TODO Auto-generated method stub
				if (combo.equals("Curso"))
					return servicioClase.filtroCurso(valor);
				else if (combo.equals("Contenido"))
					return servicioClase.filtroContenido(valor);
				else if (combo.equals("Objetivo"))
					return servicioClase.filtroObjetivo(valor);
				else if (combo.equals("Facilitador"))
					return servicioClase.filtroFacilitador(valor);
				else if (combo.equals("Entidad Did�ctica"))
					return servicioClase.filtroEntidadDidactica(valor);
				else if (combo.equals("Fecha"))
					return servicioClase.filtroFecha(valor);
				else if (combo.equals("Duraci�n"))
					return servicioClase.filtroDuracion(valor);
				else if (combo.equals("Lugar"))
					return servicioClase.filtroLugar(valor);
				else if (combo.equals("Tipo de Entrenamiento"))
					return servicioClase.filtroTipoEntrenamiento(valor);
				else if (combo.equals("Modalidad"))
					return servicioClase.filtroModalidad(valor);
				return servicioClase.buscarTodas();
			}

		};

		catalogoClase.setClosable(true);
		catalogoClase.setWidth("80%");
		catalogoClase.setParent(divCatalogoClase);
		catalogoClase.doModal();
	}

	@Listen("onSeleccion = #divCatalogoClase")
	public void seleccionClase() {
		Clase clase = catalogoClase.objetoSeleccionadoDelCatalogo();
		idClase = clase.getId();
		txtClaseEmpleadoClase.setValue(clase.getContenido());
		idClase = clase.getId();
		idCurso = clase.getCurso().getId();
		catalogoClase.setParent(null);
		llenarLista();
	}

	@Listen("onChange = #txtClaseEmpleadoClase")
	public void buscarClase() {
		List<Clase> clases = servicioClase
				.buscarPorContenidos(txtClaseEmpleadoClase.getValue());

		if (clases.size() == 0) {
			msj.mensajeAlerta(Mensaje.codigoClase);
			txtClaseEmpleadoClase.setFocus(true);
		} else {
			idClase = clases.get(0).getId();
			idCurso = clases.get(0).getCurso().getId();
			llenarLista();
		}

	}

	@Listen("onClick = #btnLimpiar")
	public void limpiarCampos() {

		idClase = 0;
		idCurso = 0;
		txtClaseEmpleadoClase.setValue("");
		lsbEmpleado.setModel(new ListModelList<Empleado>());
		txtClaseEmpleadoClase.setFocus(true);
	}

	public boolean camposLLenos() {
		if (txtClaseEmpleadoClase.getText().compareTo("") == 0) {
			return false;
		} else
			return true;
	}

	protected boolean validar() {

		if (!camposLLenos()) {
			msj.mensajeAlerta(Mensaje.camposVacios);
			return false;
		} else
			return true;

	}

	@Listen("onClick = #btnSalir")
	public void salir() {

		cerrarVentana1(wdwVEmpleadoClase, "Control de Asistencia");
	}

	public void llenarLista() {

		List<Empleado> empleados = new ArrayList<Empleado>();
		Curso curso = servicioCurso.buscarCurso(idCurso);
		empleadosCurso = servicioEmpleadoCurso.buscar(curso);

		for (int i = 0; i < empleadosCurso.size(); i++) {

			empleados.add(empleadosCurso.get(i).getEmpleado());
		}

		lsbEmpleado.setModel(new ListModelList<Empleado>(empleados));

	}

	@Listen("onClick = #btnGuardar")
	public void guardar() {

		boolean guardar = true;
		boolean errorCampo = false;
		guardar = validar();
		if (guardar) {

			Clase claseEmpleado = servicioClase.buscarClase(idClase);

			if (claseEmpleado != null) {

				if (lsbEmpleado.getItemCount() != 0) {
					for (int i = 0; i < lsbEmpleado.getItemCount(); i++) {

						Listitem listItem = lsbEmpleado.getItemAtIndex(i);

						if (((Combobox) ((listItem.getChildren().get(5)))
								.getFirstChild()).getValue().equals("")) {

							errorCampo = true;
						}

					}

					System.out.println(errorCampo);
					if (errorCampo == true) {
						Messagebox.show(
								"Debe ingresar la asistencia de los empleados",
								"Advertencia", Messagebox.OK,
								Messagebox.EXCLAMATION);
					} else {

						for (int i = 0; i < lsbEmpleado.getItemCount(); i++) {

							Listitem listItem = lsbEmpleado.getItemAtIndex(i);

							int codigoEmpleado = ((Intbox) ((listItem
									.getChildren().get(0))).getFirstChild())
									.getValue();
							String asistencia = ((Combobox) ((listItem
									.getChildren().get(5))).getFirstChild())
									.getValue();

							Empleado empleado = servicioEmpleado
									.buscar(codigoEmpleado);
							Clase clase = servicioClase.buscarClase(idClase);

							EmpleadoClase empleadosClase = new EmpleadoClase(
									clase, empleado, asistencia);
							servicioEmpleadoClase.guardar(empleadosClase);

						}

						msj.mensajeInformacion(Mensaje.guardado);
						limpiarCampos();
					}

				}

			} else {

				msj.mensajeAlerta(Mensaje.codigoClase);
				txtClaseEmpleadoClase.setFocus(true);

			}

		}

	}

}
