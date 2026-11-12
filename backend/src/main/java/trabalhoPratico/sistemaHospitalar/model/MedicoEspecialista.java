package trabalhoPratico.sistemaHospitalar.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import trabalhoPratico.sistemaHospitalar.exception.RegraDeNegocioException;
import trabalhoPratico.sistemaHospitalar.model.enums.Especialidade;

// Atua em uma área específica.
@Entity
@DiscriminatorValue("ESPECIALISTA")
public class MedicoEspecialista extends ProfissionalSaude {

    protected MedicoEspecialista() {
    }

    public MedicoEspecialista(String nome, String telefone, String email,
                              String registroProfissional, Especialidade especialidade) {
        super(nome, telefone, email, registroProfissional, especialidade);
        validarEspecialidade(especialidade);
    }

    public void setEspecialidade(Especialidade especialidade) {
        validarEspecialidade(especialidade);
        definirEspecialidade(especialidade);
    }

    // Especialista não pode ser "clínico geral" nem "cirurgia geral" (são outras categorias)
    private static void validarEspecialidade(Especialidade especialidade) {
        if (especialidade == Especialidade.CLINICO_GERAL || especialidade == Especialidade.CIRURGIA_GERAL) {
            throw new RegraDeNegocioException(
                    "Um médico especialista precisa de uma especialidade própria (ex.: Cardiologia).");
        }
    }
}