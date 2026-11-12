package trabalhoPratico.sistemaHospitalar.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import trabalhoPratico.sistemaHospitalar.model.ProfissionalSaude;
import trabalhoPratico.sistemaHospitalar.model.enums.Especialidade;

public interface ProfissionalSaudeRepository extends JpaRepository<ProfissionalSaude, Long> {

    Optional<ProfissionalSaude> findByRegistroProfissional(String registroProfissional);

    boolean existsByRegistroProfissional(String registroProfissional);

    boolean existsByRegistroProfissionalAndIdNot(String registroProfissional, Long id);

    List<ProfissionalSaude> findByEspecialidade(Especialidade especialidade);
}
