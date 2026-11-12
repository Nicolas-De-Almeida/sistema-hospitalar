package trabalhoPratico.sistemaHospitalar.model;

import static trabalhoPratico.sistemaHospitalar.util.Validacao.exigir;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import trabalhoPratico.sistemaHospitalar.exception.RegraDeNegocioException;

// Avaliação geral do paciente, feita por um médico clínico geral.
@Entity
@DiscriminatorValue("CONSULTA_ROTINA")
public class ConsultaRotina extends Consulta {

    protected ConsultaRotina() {
    }

    public ConsultaRotina(Paciente paciente, MedicoClinicoGeral medico,
                          LocalDate data, LocalTime horario, String motivo) {
        super(paciente, exigir(medico, "médico responsável"), data, horario, motivo);
        finalizarCriacao();
    }

    // Condições próprias da consulta de rotina
    @Override
    public void validarCondicoes() {
        if (!(getProfissional() instanceof MedicoClinicoGeral)) {
            throw new RegraDeNegocioException("A consulta de rotina deve ser feita por um médico clínico geral.");
        }
        exigirProfissionalDisponivel(true);
    }
}