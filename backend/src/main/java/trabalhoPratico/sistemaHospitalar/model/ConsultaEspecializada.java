package trabalhoPratico.sistemaHospitalar.model;

import static trabalhoPratico.sistemaHospitalar.util.Validacao.exigir;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.*;
import trabalhoPratico.sistemaHospitalar.exception.RegraDeNegocioException;
import trabalhoPratico.sistemaHospitalar.model.enums.Especialidade;

// Atendimento de uma especialidade: o profissional precisa atuar nessa mesma área.
@Entity
@DiscriminatorValue("CONSULTA_ESPECIALIZADA")
public class ConsultaEspecializada extends Consulta {

    @Enumerated(EnumType.STRING)
    @Column(name = "especialidade_solicitada", length = 30)
    private Especialidade especialidade;

    protected ConsultaEspecializada() {
    }

    public ConsultaEspecializada(Paciente paciente, MedicoEspecialista medico,
                                 LocalDate data, LocalTime horario,
                                 String motivo, Especialidade especialidade) {
        super(paciente, exigir(medico, "médico especialista"), data, horario, motivo);
        this.especialidade = exigir(especialidade, "especialidade");
        finalizarCriacao();
    }

    // Condições próprias da consulta especializada
    @Override
    public void validarCondicoes() {
        if (!(getProfissional() instanceof MedicoEspecialista)) {
            throw new RegraDeNegocioException("A consulta especializada deve ser feita por um médico especialista.");
        }
        if (getProfissional().getEspecialidade() != especialidade) {
            throw new RegraDeNegocioException("O profissional " + getProfissional().getNome()
                    + " não atua em " + especialidade.getDescricao() + ".");
        }
        exigirProfissionalDisponivel(true);
    }

    public Especialidade getEspecialidade() {
        return especialidade;
    }
}