package trabalhoPratico.sistemaHospitalar.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import trabalhoPratico.sistemaHospitalar.model.DisponibilidadeAtendimento;

public interface DisponibilidadeAtendimentoRepository
        extends JpaRepository<DisponibilidadeAtendimento, Long> {
}
