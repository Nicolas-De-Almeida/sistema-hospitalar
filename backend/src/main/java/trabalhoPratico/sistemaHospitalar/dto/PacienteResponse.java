package trabalhoPratico.sistemaHospitalar.dto;

import java.time.LocalDate;

import trabalhoPratico.sistemaHospitalar.model.Paciente;

public record PacienteResponse(
        Long id,
        String nome,
        String telefone,
        String email,
        String cpf,
        LocalDate dataNascimento,
        int idade,
        EnderecoResponse endereco) {

    public static PacienteResponse de(Paciente paciente) {
        return new PacienteResponse(
                paciente.getId(),
                paciente.getNome(),
                paciente.getTelefone(),
                paciente.getEmail(),
                paciente.getCpf(),
                paciente.getDataNascimento(),
                paciente.idade(),
                EnderecoResponse.de(paciente.getEndereco()));
    }
}
