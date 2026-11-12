package trabalhoPratico.sistemaHospitalar.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import trabalhoPratico.sistemaHospitalar.dto.DisponibilidadeRequest;
import trabalhoPratico.sistemaHospitalar.dto.DisponibilidadeResponse;
import trabalhoPratico.sistemaHospitalar.dto.ProfissionalRequest;
import trabalhoPratico.sistemaHospitalar.dto.ProfissionalResponse;
import trabalhoPratico.sistemaHospitalar.dto.StatusProfissionalRequest;
import trabalhoPratico.sistemaHospitalar.model.enums.Especialidade;
import trabalhoPratico.sistemaHospitalar.service.ProfissionalService;

@RestController
@RequestMapping("/api/profissionais")
public class ProfissionalController {

    private final ProfissionalService profissionalService;

    public ProfissionalController(ProfissionalService profissionalService) {
        this.profissionalService = profissionalService;
    }

    @PostMapping
    public ResponseEntity<ProfissionalResponse> cadastrar(@Valid @RequestBody ProfissionalRequest request) {
        ProfissionalResponse response = profissionalService.cadastrar(request);
        URI localizacao = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(localizacao).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ProfissionalResponse>> listar(
            @RequestParam(required = false) Especialidade especialidade) {
        return ResponseEntity.ok(profissionalService.listar(especialidade));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfissionalResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(profissionalService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfissionalResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProfissionalRequest request) {
        return ResponseEntity.ok(profissionalService.atualizar(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ProfissionalResponse> alterarStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusProfissionalRequest request) {
        return ResponseEntity.ok(profissionalService.alterarStatus(id, request));
    }

    @PostMapping("/{id}/disponibilidades")
    public ResponseEntity<DisponibilidadeResponse> adicionarDisponibilidade(
            @PathVariable Long id,
            @Valid @RequestBody DisponibilidadeRequest request) {
        DisponibilidadeResponse response = profissionalService.adicionarDisponibilidade(id, request);
        URI localizacao = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{disponibilidadeId}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(localizacao).body(response);
    }

    @DeleteMapping("/{id}/disponibilidades/{disponibilidadeId}")
    public ResponseEntity<Void> removerDisponibilidade(
            @PathVariable Long id,
            @PathVariable Long disponibilidadeId) {
        profissionalService.removerDisponibilidade(id, disponibilidadeId);
        return ResponseEntity.noContent().build();
    }
}
