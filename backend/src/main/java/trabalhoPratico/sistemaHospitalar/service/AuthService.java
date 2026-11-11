package trabalhoPratico.sistemaHospitalar.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import trabalhoPratico.sistemaHospitalar.dto.LoginRequest;
import trabalhoPratico.sistemaHospitalar.dto.LoginResponse;
import trabalhoPratico.sistemaHospitalar.exception.CredenciaisInvalidasException;
import trabalhoPratico.sistemaHospitalar.model.Usuario;
import trabalhoPratico.sistemaHospitalar.repository.UsuarioRepository;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse autenticar(LoginRequest request) {
        String email = request.email().trim().toLowerCase();

        // Mesma exceção para e-mail inexistente e senha errada,
        // assim quem tenta invadir não descobre quais e-mails existem.
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(CredenciaisInvalidasException::new);

        if (!passwordEncoder.matches(request.senha(), usuario.getSenhaHash())) {
            throw new CredenciaisInvalidasException();
        }

        return new LoginResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPerfil().getDescricao());
    }
}
