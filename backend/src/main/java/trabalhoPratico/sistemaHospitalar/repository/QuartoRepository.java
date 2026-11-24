package trabalhoPratico.sistemaHospitalar.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import trabalhoPratico.sistemaHospitalar.model.Quarto;

public interface QuartoRepository extends JpaRepository<Quarto, Long> {

    // O quarto aparece em alguma troca de quarto
    @Query("select count(t) > 0 from TransferenciaQuarto t "
            + "where t.quartoOrigem.id = :id or t.quartoDestino.id = :id")
    boolean possuiTransferencias(@Param("id") Long id);

    Optional<Quarto> findByNumero(String numero);

    boolean existsByNumero(String numero);

    boolean existsByNumeroAndIdNot(String numero, Long id);
}
