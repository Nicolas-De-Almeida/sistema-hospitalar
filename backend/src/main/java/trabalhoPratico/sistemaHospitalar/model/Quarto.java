package trabalhoPratico.sistemaHospitalar.model;

import static trabalhoPratico.sistemaHospitalar.util.Validacao.exigir;
import static trabalhoPratico.sistemaHospitalar.util.Validacao.exigirTexto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.persistence.*;
import trabalhoPratico.sistemaHospitalar.exception.RegraDeNegocioException;
import trabalhoPratico.sistemaHospitalar.model.enums.SituacaoQuarto;
import trabalhoPratico.sistemaHospitalar.model.enums.TipoQuarto;

@Entity
@Table(name = "quartos")
public class Quarto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String numero;

    @Column(nullable = false)
    private int andar;

    @Column(name = "capacidade_maxima", nullable = false)
    private int capacidadeMaxima;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoQuarto tipo;

    @OneToMany(mappedBy = "quarto")
    private List<Internacao> internacoes = new ArrayList<>();

    protected Quarto() {
    }

    public Quarto(String numero, int andar, int capacidadeMaxima, TipoQuarto tipo) {
        this.numero = exigirTexto(numero, "número do quarto");
        this.andar = andar;
        this.tipo = exigir(tipo, "tipo do quarto");
        validarCapacidade(capacidadeMaxima, tipo);
        this.capacidadeMaxima = capacidadeMaxima;
    }


    // Conta só as internações ATIVAS (quem já teve alta libera a vaga)
    public int ocupacaoAtual() {
        return (int) internacoes.stream().filter(Internacao::estaAtiva).count();
    }

    public boolean temVaga() {
        return ocupacaoAtual() < capacidadeMaxima;
    }

    // A situação é CALCULADA, não guardada no banco: assim nunca fica desatualizada
    public SituacaoQuarto situacao() {
        return temVaga() ? SituacaoQuarto.DISPONIVEL : SituacaoQuarto.OCUPADO;
    }

    // Chamado pela Internacao ao ser criada. Barra a internação se o quarto estiver lotado.
    void registrarInternacao(Internacao internacao) {
        if (!temVaga()) {
            throw new RegraDeNegocioException("O quarto " + numero + " está lotado (capacidade máxima: "
                    + capacidadeMaxima + ").");
        }
        internacoes.add(internacao);
    }

    private void validarCapacidade(int capacidade, TipoQuarto tipoDoQuarto) {
        if (capacidade < 1) {
            throw new RegraDeNegocioException("A capacidade do quarto deve ser de pelo menos 1 paciente.");
        }
        if (capacidade > tipoDoQuarto.getLimiteDePacientes()) {
            throw new RegraDeNegocioException("Um quarto do tipo " + tipoDoQuarto.getDescricao()
                    + " comporta no máximo " + tipoDoQuarto.getLimiteDePacientes() + " paciente(s).");
        }
    }

    // Atualiza os dados dependentes de forma atômica, validando a combinação final
    // de tipo e capacidade antes de alterar a entidade.
    public void atualizarCaracteristicas(String numero, int andar, int capacidadeMaxima, TipoQuarto tipo) {
        String novoNumero = exigirTexto(numero, "número do quarto");
        TipoQuarto novoTipo = exigir(tipo, "tipo do quarto");
        validarCapacidade(capacidadeMaxima, novoTipo);
        if (capacidadeMaxima < ocupacaoAtual()) {
            throw new RegraDeNegocioException("O quarto tem " + ocupacaoAtual()
                    + " paciente(s) internado(s); a capacidade não pode ser menor que isso.");
        }
        this.numero = novoNumero;
        this.andar = andar;
        this.capacidadeMaxima = capacidadeMaxima;
        this.tipo = novoTipo;
    }

    // ---------- Getters e Setters ----------

    public Long getId() {
        return id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = exigirTexto(numero, "número do quarto");
    }

    public int getAndar() {
        return andar;
    }

    public void setAndar(int andar) {
        this.andar = andar;
    }

    public int getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    // Não deixa reduzir a capacidade abaixo de quem já está internado
    public void setCapacidadeMaxima(int capacidadeMaxima) {
        validarCapacidade(capacidadeMaxima, tipo);
        if (capacidadeMaxima < ocupacaoAtual()) {
            throw new RegraDeNegocioException("O quarto tem " + ocupacaoAtual()
                    + " paciente(s) internado(s); a capacidade não pode ser menor que isso.");
        }
        this.capacidadeMaxima = capacidadeMaxima;
    }

    public TipoQuarto getTipo() {
        return tipo;
    }

    public void setTipo(TipoQuarto tipo) {
        exigir(tipo, "tipo do quarto");
        validarCapacidade(capacidadeMaxima, tipo);
        this.tipo = tipo;
    }

    public List<Internacao> getInternacoes() {
        return Collections.unmodifiableList(internacoes);
    }
}
