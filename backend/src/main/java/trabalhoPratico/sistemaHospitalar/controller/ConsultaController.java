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
import trabalhoPratico.sistemaHospitalar.dto.ConclusaoConsultaRequest;
import trabalhoPratico.sistemaHospitalar.dto.ConsultaRequest;
import trabalhoPratico.sistemaHospitalar.dto.ConsultaResponse;
import trabalhoPratico.sistemaHospitalar.dto.RemarcacaoConsultaRequest;
import trabalhoPratico.sistemaHospitalar.model.enums.StatusConsulta;
import trabalhoPratico.sistemaHospitalar.service.ConsultaService;

@RestController
@RequestMapping("/api/consultas")
public class ConsultaController {

    private final ConsultaService consultaService;

    public ConsultaController(ConsultaService consultaService) {
        this.consultaService = consultaService;
    }

    @PostMapping
    public ResponseEntity<ConsultaResponse> agendar(@Valid @RequestBody ConsultaRequest request) {
        ConsultaResponse response = consultaService.agendar(request);
        URI localizacao = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(localizacao).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ConsultaResponse>> listar(
            @RequestParam(required = false) StatusConsulta status) {
        return ResponseEntity.ok(consultaService.listar(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(consultaService.buscarPorId(id));
    }

    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<ConsultaResponse> confirmar(@PathVariable Long id) {
        return ResponseEntity.ok(consultaService.confirmar(id));
    }

    @PatchMapping("/{id}/iniciar")
    public ResponseEntity<ConsultaResponse> iniciar(@PathVariable Long id) {
        return ResponseEntity.ok(consultaService.iniciar(id));
    }

    @PatchMapping("/{id}/concluir")
    public ResponseEntity<ConsultaResponse> concluir(
            @PathVariable Long id,
            @Valid @RequestBody ConclusaoConsultaRequest request) {
        return ResponseEntity.ok(consultaService.concluir(id, request));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ConsultaResponse> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(consultaService.cancelar(id));
    }

    @PatchMapping("/{id}/falta")
    public ResponseEntity<ConsultaResponse> registrarFalta(@PathVariable Long id) {
        return ResponseEntity.ok(consultaService.registrarFalta(id));
    }

    @PatchMapping("/{id}/remarcar")
    public ResponseEntity<ConsultaResponse> remarcar(
            @PathVariable Long id,
            @Valid @RequestBody RemarcacaoConsultaRequest request) {
        return ResponseEntity.ok(consultaService.remarcar(id, request));
    }
}
