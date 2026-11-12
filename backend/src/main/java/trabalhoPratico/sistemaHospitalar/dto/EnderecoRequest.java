package trabalhoPratico.sistemaHospitalar.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EnderecoRequest(
        @NotBlank(message = "O logradouro é obrigatório.")
        @Size(max = 255, message = "O logradouro deve ter no máximo 255 caracteres.")
        String logradouro,

        @Size(max = 255, message = "O número deve ter no máximo 255 caracteres.")
        String numero,

        @Size(max = 255, message = "O complemento deve ter no máximo 255 caracteres.")
        String complemento,

        @Size(max = 255, message = "O bairro deve ter no máximo 255 caracteres.")
        String bairro,

        @NotBlank(message = "A cidade é obrigatória.")
        @Size(max = 255, message = "A cidade deve ter no máximo 255 caracteres.")
        String cidade,

        @NotBlank(message = "A UF é obrigatória.")
        @Pattern(regexp = "[A-Za-z]{2}", message = "A UF deve ter 2 letras.")
        String uf,

        @Pattern(regexp = "^$|\\d{5}-?\\d{3}$", message = "O CEP deve ter 8 números.")
        String cep) {
}
