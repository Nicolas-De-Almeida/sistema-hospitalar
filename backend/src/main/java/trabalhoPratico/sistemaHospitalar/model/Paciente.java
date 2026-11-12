package trabalhoPratico.sistemaHospitalar.model;

import static trabalhoPratico.sistemaHospitalar.util.Validacao.exigir;
import static trabalhoPratico.sistemaHospitalar.util.Validacao.exigirTexto;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.persistence.*;
import trabalhoPratico.sistemaHospitalar.exception.RegraDeNegocioException;

@Entity
@Table(name = "pacientes")
public class Paciente extends Pessoa {

    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @Embedded
    private Endereco endereco;

    // Regra 1 e 6: o paciente guarda TODO o histórico (nada é apagado).
    // mappedBy = a chave estrangeira fica na tabela atendimentos / internacoes.
    @OneToMany(mappedBy = "paciente")
    private List<Atendimento> atendimentos = new ArrayList<>();

    @OneToMany(mappedBy = "paciente")
    private List<Internacao> internacoes = new ArrayList<>();

    protected Paciente() {
    }

    public Paciente(String nome, String telefone, String email,
                    String cpf, LocalDate dataNascimento, Endereco endereco) {
        super(nome, telefone, email);
        setCpf(cpf);
        setDataNascimento(dataNascimento);
        this.endereco = exigir(endereco, "endereço");
    }

    // ---------- Comportamento ----------

    public int idade() {
        return Period.between(dataNascimento, LocalDate.now()).getYears();
    }

    public boolean possuiInternacaoAtiva() {
        return internacoes.stream().anyMatch(Internacao::estaAtiva);
    }

    // Só as consultas (rotina + especializada), filtradas da lista de atendimentos
    public List<Consulta> getConsultas() {
        return atendimentos.stream()
                .filter(a -> a instanceof Consulta)
                .map(a -> (Consulta) a)
                .toList();
    }

    // Usados só pelas classes do próprio pacote para manter os dois lados da relação coerentes
    void adicionarAtendimento(Atendimento atendimento) {
        if (!atendimentos.contains(atendimento)) {
            atendimentos.add(atendimento);
        }
    }

    void adicionarInternacao(Internacao internacao) {
        if (!internacoes.contains(internacao)) {
            internacoes.add(internacao);
        }
    }

    // ---------- Getters e Setters ----------

    public String getCpf() {
        return cpf;
    }

    // Guarda só os 11 números (o front mostra com máscara)
    public void setCpf(String cpf) {
        String digitos = exigirTexto(cpf, "CPF").replaceAll("\\D", "");
        if (digitos.length() != 11) {
            throw new RegraDeNegocioException("CPF inválido: deve ter 11 números.");
        }
        this.cpf = digitos;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        exigir(dataNascimento, "data de nascimento");
        if (dataNascimento.isAfter(LocalDate.now())) {
            throw new RegraDeNegocioException("A data de nascimento não pode estar no futuro.");
        }
        this.dataNascimento = dataNascimento;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = exigir(endereco, "endereço");
    }

    public List<Atendimento> getAtendimentos() {
        return Collections.unmodifiableList(atendimentos);
    }

    public List<Internacao> getInternacoes() {
        return Collections.unmodifiableList(internacoes);
    }
}