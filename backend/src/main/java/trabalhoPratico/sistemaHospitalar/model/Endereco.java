package trabalhoPratico.sistemaHospitalar.model;

import static trabalhoPratico.sistemaHospitalar.util.Validacao.exigirTexto;

import jakarta.persistence.Embeddable;
import trabalhoPratico.sistemaHospitalar.exception.RegraDeNegocioException;

// Objeto de valor (value object): não tem id nem tabela própria.
// Suas colunas ficam dentro da tabela do Paciente (@Embeddable + @Embedded).
@Embeddable
public class Endereco {

    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String uf;
    private String cep;

    protected Endereco() {
    }

    public Endereco(String logradouro, String numero, String complemento,
                    String bairro, String cidade, String uf, String cep) {
        this.logradouro = exigirTexto(logradouro, "logradouro");
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = exigirTexto(cidade, "cidade");
        setUf(uf);
        setCep(cep);
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = exigirTexto(logradouro, "logradouro");
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = exigirTexto(cidade, "cidade");
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        String valor = exigirTexto(uf, "UF").toUpperCase();
        if (valor.length() != 2) {
            throw new RegraDeNegocioException("A UF deve ter 2 letras (ex.: MG).");
        }
        this.uf = valor;
    }

    public String getCep() {
        return cep;
    }

    // Guarda só os números; o front formata na tela
    public void setCep(String cep) {
        if (cep == null || cep.isBlank()) {
            this.cep = null;
            return;
        }
        String digitos = cep.replaceAll("\\D", "");
        if (digitos.length() != 8) {
            throw new RegraDeNegocioException("CEP inválido: deve ter 8 números.");
        }
        this.cep = digitos;
    }
}