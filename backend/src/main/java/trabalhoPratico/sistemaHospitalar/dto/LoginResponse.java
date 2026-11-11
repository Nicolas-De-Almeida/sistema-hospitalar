package trabalhoPratico.sistemaHospitalar.dto;

// Só devolvemos o que o front precisa. A senha (nem o hash) nunca sai daqui.
public record LoginResponse(Long id, String nome, String email, String funcao) {
}
