package trabalhoPratico.sistemaHospitalar.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import trabalhoPratico.sistemaHospitalar.dto.LoginRequest;
import trabalhoPratico.sistemaHospitalar.dto.LoginResponse;
import trabalhoPratico.sistemaHospitalar.exception.CredenciaisInvalidasException;
import trabalhoPratico.sistemaHospitalar.model.Perfil;
import trabalhoPratico.sistemaHospitalar.model.Usuario;
import trabalhoPratico.sistemaHospitalar.repository.UsuarioRepository;

class AuthServiceTest {

    private final UsuarioRepository repository = mock(UsuarioRepository.class);
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private final AuthService service = new AuthService(repository, encoder);

    private Usuario usuarioDeTeste() {
        return new Usuario("Maria Souza", "maria@hospital.com", encoder.encode("senha123"), Perfil.ADMINISTRADOR);
    }

    @Test
    void loginComCredenciaisCorretasRetornaOUsuario() {
        when(repository.findByEmail("maria@hospital.com")).thenReturn(Optional.of(usuarioDeTeste()));

        LoginResponse resposta = service.autenticar(new LoginRequest("  Maria@Hospital.com ", "senha123"));

        assertEquals("Maria Souza", resposta.nome());
        assertEquals("Administrador", resposta.funcao());
    }

    @Test
    void loginComSenhaErradaLancaExcecao() {
        when(repository.findByEmail("maria@hospital.com")).thenReturn(Optional.of(usuarioDeTeste()));

        assertThrows(CredenciaisInvalidasException.class,
                () -> service.autenticar(new LoginRequest("maria@hospital.com", "errada")));
    }

    @Test
    void loginComEmailInexistenteLancaExcecao() {
        when(repository.findByEmail("ninguem@hospital.com")).thenReturn(Optional.empty());

        assertThrows(CredenciaisInvalidasException.class,
                () -> service.autenticar(new LoginRequest("ninguem@hospital.com", "qualquer")));
    }
}
