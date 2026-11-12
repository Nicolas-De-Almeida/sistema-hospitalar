package trabalhoPratico.sistemaHospitalar.dto;

import trabalhoPratico.sistemaHospitalar.model.Endereco;

public record EnderecoResponse(
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String uf,
        String cep) {

    public static EnderecoResponse de(Endereco endereco) {
        return new EnderecoResponse(
                endereco.getLogradouro(),
                endereco.getNumero(),
                endereco.getComplemento(),
                endereco.getBairro(),
                endereco.getCidade(),
                endereco.getUf(),
                endereco.getCep());
    }
}
