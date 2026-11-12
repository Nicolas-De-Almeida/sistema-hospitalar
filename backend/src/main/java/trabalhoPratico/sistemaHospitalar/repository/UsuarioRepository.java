package trabalhoPratico.sistemaHospitalar.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import trabalhoPratico.sistemaHospitalar.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // O Spring Data cria o SELECT sozinho a partir do nome do método
    Optional<Usuario> findByEmail(String email);
}
