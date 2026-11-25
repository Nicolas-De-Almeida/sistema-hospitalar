package trabalhoPratico.sistemaHospitalar.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;

import trabalhoPratico.sistemaHospitalar.model.Quarto;

public interface QuartoRepository extends JpaRepository<Quarto, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)

    @Query("select q from Quarto q where q.id = :id")

    Optional<Quarto> bloquearParaAtualizacao(@Param("id") Long id);
    
    // O quarto aparece em alguma troca de quarto
    @Query("select count(t) > 0 from TransferenciaQuarto t "
            + "where t.quartoOrigem.id = :id or t.quartoDestino.id = :id")
    boolean possuiTransferencias(@Param("id") Long id);

    Optional<Quarto> findByNumero(String numero);

    boolean existsByNumero(String numero);

    boolean existsByNumeroAndIdNot(String numero, Long id);
}
