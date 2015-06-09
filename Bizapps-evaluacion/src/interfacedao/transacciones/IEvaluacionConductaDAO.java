package interfacedao.transacciones;


import java.util.List;

import modelo.maestros.Competencia;
import modelo.maestros.ConductaCompetencia;
import modelo.maestros.Evaluacion;
import modelo.maestros.EvaluacionConducta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IEvaluacionConductaDAO extends JpaRepository<EvaluacionConducta, Integer> {
	
	public List <EvaluacionConducta> findByEvaluacionIdEvaluacion (Integer id);

	@Query("select ec from EvaluacionConducta ec where ec.evaluacion.idEvaluacion = ?1")
	public EvaluacionConducta buscar(Integer id);
	
	public EvaluacionConducta findByEvaluacionAndConductaCompetencia (Evaluacion eva, ConductaCompetencia cc);
	
	public List <EvaluacionConducta> findByEvaluacionAndCompetencia (Evaluacion eva, Competencia con);
		
	public List <EvaluacionConducta> findByEvaluacion (Evaluacion eva);
	
	

}
