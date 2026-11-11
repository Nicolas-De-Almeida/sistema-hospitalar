package trabalhoPratico.sistemaHospitalar.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import trabalhoPratico.sistemaHospitalar.dto.QuartoRequest;
import trabalhoPratico.sistemaHospitalar.dto.QuartoResponse;
import trabalhoPratico.sistemaHospitalar.service.QuartoService;

@RestController
@RequestMapping("/api/quartos")
public class QuartoController {

    private final QuartoService quartoService;

    public QuartoController(QuartoService quartoService) {
        this.quartoService = quartoService;
    }

    @PostMapping
    public ResponseEntity<QuartoResponse> cadastrar(@Valid @RequestBody QuartoRequest request) {
        QuartoResponse response = quartoService.cadastrar(request);
        URI localizacao = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(localizacao).body(response);
    }

    @GetMapping
    public ResponseEntity<List<QuartoResponse>> listar() {
        return ResponseEntity.ok(quartoService.listar());
    }

    @GetMapping("/disponiveis")
    public ResponseEntity<List<QuartoResponse>> listarDisponiveis() {
        return ResponseEntity.ok(quartoService.listarDisponiveis());
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuartoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(quartoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuartoResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody QuartoRequest request) {
        return ResponseEntity.ok(quartoService.atualizar(id, request));
    }
}
