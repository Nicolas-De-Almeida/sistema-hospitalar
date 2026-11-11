package trabalhoPratico.sistemaHospitalar.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import trabalhoPratico.sistemaHospitalar.model.enums.CategoriaProfissional;
import trabalhoPratico.sistemaHospitalar.model.enums.Especialidade;

public record ProfissionalRequest(
        @NotBlank(message = "O nome é obrigatório.")
        @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres.")
        String nome,

        @NotBlank(message = "O telefone é obrigatório.")
        @Size(max = 20, message = "O telefone deve ter no máximo 20 caracteres.")
        String telefone,

        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "E-mail inválido.")
        @Size(max = 150, message = "O e-mail deve ter no máximo 150 caracteres.")
        String email,

        @NotBlank(message = "O registro profissional é obrigatório.")
        @Size(max = 30, message = "O registro profissional deve ter no máximo 30 caracteres.")
        String registroProfissional,

        @NotNull(message = "A categoria profissional é obrigatória.")
        CategoriaProfissional categoria,

        Especialidade especialidade,

        @Size(max = 100, message = "A área cirúrgica deve ter no máximo 100 caracteres.")
        String areaCirurgica) {
}
