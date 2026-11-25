package trabalhoPratico.sistemaHospitalar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Isolation;
import trabalhoPratico.sistemaHospitalar.model.Internacao;
import trabalhoPratico.sistemaHospitalar.model.enums.StatusInternacao;

public interface InternacaoRepository extends JpaRepository<Internacao, Long> {

    List<Internacao> findByStatus(StatusInternacao status);
}
