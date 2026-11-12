package trabalhoPratico.sistemaHospitalar.model;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import trabalhoPratico.sistemaHospitalar.model.enums.Especialidade;

// Faz consultas de rotina e avaliações iniciais; pode encaminhar para especialistas.
@Entity
@DiscriminatorValue("CLINICO_GERAL")
public class MedicoClinicoGeral extends ProfissionalSaude {

    protected MedicoClinicoGeral() {
    }

    public MedicoClinicoGeral(String nome, String telefone, String email, String registroProfissional) {
        // a especialidade de um clínico geral é sempre CLINICO_GERAL
        super(nome, telefone, email, registroProfissional, Especialidade.CLINICO_GERAL);
    }

    // Pode encaminhar pacientes para atendimento especializado, criando a consulta com o especialista (já validando disponibilidade e conflito de horário).
    public ConsultaEspecializada encaminhar(Paciente paciente, MedicoEspecialista especialista,
                                            LocalDate data, LocalTime horario, String motivo) {
        return new ConsultaEspecializada(paciente, especialista, data, horario,
                motivo, especialista.getEspecialidade());
    }
}