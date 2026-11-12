package trabalhoPratico.sistemaHospitalar.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import trabalhoPratico.sistemaHospitalar.dto.ConclusaoEmergenciaRequest;
import trabalhoPratico.sistemaHospitalar.dto.EmergenciaRequest;
import trabalhoPratico.sistemaHospitalar.dto.EmergenciaResponse;
import trabalhoPratico.sistemaHospitalar.dto.InicioEmergenciaRequest;
import trabalhoPratico.sistemaHospitalar.dto.PrioridadeEmergenciaRequest;
import trabalhoPratico.sistemaHospitalar.model.enums.StatusEmergencia;
import trabalhoPratico.sistemaHospitalar.service.EmergenciaService;

@RestController
@RequestMapping("/api/emergencias")
public class EmergenciaController {

    private final EmergenciaService emergenciaService;

    public EmergenciaController(EmergenciaService emergenciaService) {
        this.emergenciaService = emergenciaService;
    }

    @PostMapping
    public ResponseEntity<EmergenciaResponse> registrar(@Valid @RequestBody EmergenciaRequest request) {
        EmergenciaResponse response = emergenciaService.registrar(request);
        URI localizacao = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(localizacao).body(response);
    }

    @GetMapping
    public ResponseEntity<List<EmergenciaResponse>> listar(
            @RequestParam(required = false) StatusEmergencia status) {
        return ResponseEntity.ok(emergenciaService.listar(status));
    }

    @GetMapping("/fila")
    public ResponseEntity<List<EmergenciaResponse>> listarFila() {
        return ResponseEntity.ok(emergenciaService.listarFila());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmergenciaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(emergenciaService.buscarPorId(id));
    }

    @PatchMapping("/{id}/iniciar")
    public ResponseEntity<EmergenciaResponse> iniciarAvaliacao(
            @PathVariable Long id,
            @Valid @RequestBody InicioEmergenciaRequest request) {
        return ResponseEntity.ok(emergenciaService.iniciarAvaliacao(id, request));
    }

    @PatchMapping("/{id}/prioridade")
    public ResponseEntity<EmergenciaResponse> reclassificar(
            @PathVariable Long id,
            @Valid @RequestBody PrioridadeEmergenciaRequest request) {
        return ResponseEntity.ok(emergenciaService.reclassificar(id, request));
    }

    @PatchMapping("/{id}/concluir")
    public ResponseEntity<EmergenciaResponse> concluir(
            @PathVariable Long id,
            @Valid @RequestBody ConclusaoEmergenciaRequest request) {
        return ResponseEntity.ok(emergenciaService.concluir(id, request));
    }
}
