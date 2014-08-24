package interfacedao.transacciones;

import java.util.List;

import modelo.maestros.Evaluacion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IEvaluacionDAO extends JpaRepository<Evaluacion, Integer> {
	
	
	public List<Evaluacion> findByFichaOrderByIdEvaluacionSecundarioDesc(String ficha);
	
	//@Query("select e from Evaluacion e where e.estadoEvaluacion <> 'EN EDICION' and e.ficha = ?1")
	@Query("select e from Evaluacion e where  e.ficha = ?1 order by e.idEvaluacion desc")
	public List<Evaluacion> buscarEstado(String ficha);
	
	public List<Evaluacion> findByFichaAndEstadoEvaluacion (String ficha, String estado);
	
	@Query("select max(idEvaluacion) from Evaluacion")
	public Integer buscar();
	
	@Query("select max(idEvaluacionSecundario) from Evaluacion e where e.ficha = ?1")
	public Integer buscarIdSecundario(String ficha);

	public Evaluacion findByIdEvaluacionSecundarioAndFicha (Integer id, String ficha); 
	
	public Evaluacion findByIdEvaluacion(Integer idEvaluacion);

	@Query("select  ev from Empleado em, Evaluacion ev, Empresa e, Revision r , UnidadOrganizativa uo, Gerencia g " +
			"where " +
			"em.ficha = ev.ficha and " +
			"r.id = ev.revision.id " +
			"and r.estadoRevision = 'ACTIVO' AND " +
			"e.nombre = ?1 AND  " +
			"em.nombre = ?2  " +
			"AND em.fichaSupervisor = ?3 and " +
			"em.unidadOrganizativa.id = uo.id and " +
			"uo.gerencia.id = g.id and " +
			"g.descripcion = ?4 and ev.valoracion = ?5 ")
	public List<Evaluacion> buscarEvaluacionCalibracion(String empresa, String nombreE, String fichaE, String gerencia, String valoracion);

	@Query("select e from Evaluacion e, Revision r " +
			"where r.id = e.revision.id and" +
			" r.estadoRevision = 'ACTIVO'")
	public List<Evaluacion> buscarEvaluacionesRevision();
	
}
