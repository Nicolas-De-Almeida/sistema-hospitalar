package trabalhoPratico.sistemaHospitalar.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import trabalhoPratico.sistemaHospitalar.model.enums.Especialidade;
import trabalhoPratico.sistemaHospitalar.model.enums.StatusProcedimento;
import trabalhoPratico.sistemaHospitalar.util.Validacao;

// Realiza procedimentos cirúrgicos. Além dos dados comuns, tem a área de atuação cirúrgica.
@Entity
@DiscriminatorValue("CIRURGIAO")
public class Cirurgiao extends ProfissionalSaude {

    // exemplop: "Cirurgia cardíaca", "Cirurgia ortopédica"
    @Column(name = "area_cirurgica", length = 100)
    private String areaCirurgica;

    protected Cirurgiao() {
    }

    public Cirurgiao(String nome, String telefone, String email,
                     String registroProfissional, String areaCirurgica) {
        super(nome, telefone, email, registroProfissional, Especialidade.CIRURGIA_GERAL);
        this.areaCirurgica = Validacao.exigirTexto(areaCirurgica, "área cirúrgica");
    }

    // Procedimentos já realizados (concluídos) por este cirurgião.
    // Cada um traz data efetiva, paciente e descrição.
    public List<ProcedimentoCirurgico> getProcedimentosRealizados() {
        return getAtendimentos().stream()
                .filter(a -> a instanceof ProcedimentoCirurgico)
                .map(a -> (ProcedimentoCirurgico) a)
                .filter(p -> p.getStatus() == StatusProcedimento.CONCLUIDO)
                .toList();
    }

    public String getAreaCirurgica() {
        return areaCirurgica;
    }

    public void setAreaCirurgica(String areaCirurgica) {
        this.areaCirurgica = Validacao.exigirTexto(areaCirurgica, "área cirúrgica");
    }
}