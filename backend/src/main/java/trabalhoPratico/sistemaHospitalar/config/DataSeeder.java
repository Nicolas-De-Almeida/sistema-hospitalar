package trabalhoPratico.sistemaHospitalar.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import trabalhoPratico.sistemaHospitalar.model.Perfil;
import trabalhoPratico.sistemaHospitalar.model.Usuario;
import trabalhoPratico.sistemaHospitalar.repository.UsuarioRepository;

// Cria um usuário de teste quando o sistema sobe e a tabela está vazia.
// Só roda se app.seed-admin=true (ver application-dev.properties).
// Em um sistema real, isso não existiria.
@Component
@ConditionalOnProperty(name = "app.seed-admin", havingValue = "true")
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario(
                    "Cleitin Santana",
                    "admin@hospital.com",
                    passwordEncoder.encode("admin123"),
                    Perfil.ADMINISTRADOR);
            usuarioRepository.save(admin);
        }
    }
}
