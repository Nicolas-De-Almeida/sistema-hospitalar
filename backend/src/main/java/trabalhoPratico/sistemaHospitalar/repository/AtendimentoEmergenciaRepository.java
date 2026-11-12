package trabalhoPratico.sistemaHospitalar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import trabalhoPratico.sistemaHospitalar.model.AtendimentoEmergencia;
import trabalhoPratico.sistemaHospitalar.model.enums.StatusEmergencia;

public interface AtendimentoEmergenciaRepository extends JpaRepository<AtendimentoEmergencia, Long> {

    List<AtendimentoEmergencia> findByStatus(StatusEmergencia status);
}
