package trabalhoPratico.sistemaHospitalar.model;

import static trabalhoPratico.sistemaHospitalar.util.Validacao.exigirTexto;

import jakarta.persistence.*;
import trabalhoPratico.sistemaHospitalar.exception.RegraDeNegocioException;

// Superclasse abstrata: tudo que Paciente e ProfissionalSaude têm em comum.
// @MappedSuperclass = não vai virar tabela própria; as colunas são copiadas para a tabela de cada filha (pacientes e profissionais_saude).
@MappedSuperclass
public abstract class Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, length = 20)
    private String telefone;

    @Column(nullable = false, length = 150)
    private String email;

    protected Pessoa() {
    }

    protected Pessoa(String nome, String telefone, String email) {
        this.nome = exigirTexto(nome, "nome");
        this.telefone = exigirTexto(telefone, "telefone");
        this.email = validarEmail(email);
    }

    private static String validarEmail(String email) {
        String valor = exigirTexto(email, "e-mail").toLowerCase();
        if (!valor.contains("@") || valor.startsWith("@") || valor.endsWith("@")) {
            throw new RegraDeNegocioException("E-mail inválido.");
        }
        return valor;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = exigirTexto(nome, "nome");
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = exigirTexto(telefone, "telefone");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = validarEmail(email);
    }
}