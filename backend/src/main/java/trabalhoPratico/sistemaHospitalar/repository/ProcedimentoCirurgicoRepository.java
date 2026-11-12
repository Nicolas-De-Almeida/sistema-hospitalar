package trabalhoPratico.sistemaHospitalar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import trabalhoPratico.sistemaHospitalar.model.ProcedimentoCirurgico;
import trabalhoPratico.sistemaHospitalar.model.enums.StatusProcedimento;

public interface ProcedimentoCirurgicoRepository extends JpaRepository<ProcedimentoCirurgico, Long> {

    List<ProcedimentoCirurgico> findByStatus(StatusProcedimento status);
}
