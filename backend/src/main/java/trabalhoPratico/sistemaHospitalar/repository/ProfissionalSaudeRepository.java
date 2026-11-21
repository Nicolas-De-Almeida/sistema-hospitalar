package trabalhoPratico.sistemaHospitalar.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import trabalhoPratico.sistemaHospitalar.model.ProfissionalSaude;
import trabalhoPratico.sistemaHospitalar.model.enums.Especialidade;

public interface ProfissionalSaudeRepository extends JpaRepository<ProfissionalSaude, Long> {

    Optional<ProfissionalSaude> findByRegistroProfissional(String registroProfissional);

    boolean existsByRegistroProfissional(String registroProfissional);

    boolean existsByRegistroProfissionalAndIdNot(String registroProfissional, Long id);

    List<ProfissionalSaude> findByEspecialidade(Especialidade especialidade);

    @Query("select count(i) > 0 from Internacao i where i.profissional.id = :id")
    boolean possuiInternacoes(@Param("id") Long id);
}
