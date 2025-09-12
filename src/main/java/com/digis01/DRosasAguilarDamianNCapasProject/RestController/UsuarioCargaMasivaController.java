package com.digis01.DRosasAguilarDamianNCapasProject.RestController;

import com.digis01.DRosasAguilarDamianNCapasProject.cargamasiva.CargaMasiva.CargaMasivaService;
import com.digis01.DRosasAguilarDamianNCapasProject.cargamasiva.CargaMasiva.BulkJob;
import com.digis01.DRosasAguilarDamianNCapasProject.cargamasiva.CargaMasiva.BulkStatus;
import com.digis01.DRosasAguilarDamianNCapasProject.cargamasiva.CargaMasiva.CargaError;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/usuarioapi/cargamasiva")
@Tag(name = "Carga Masiva de Usuarios", description = "Endpoints para subir y procesar archivos de carga masiva")
public class UsuarioCargaMasivaController {

    private final CargaMasivaService service;

    public UsuarioCargaMasivaController(CargaMasivaService service) {
        this.service = service;
    }

    // POST 1/2: SUBIR (solo multipart/form-data con key "file")
    @Operation(
        summary = "Subir archivo y registrar job",
        description = "Acepta .xlsx, .xls, .txt, .csv. Si el SHA1 ya está PROCESADO y sobrescribir=false, retorna ERROR (409)."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Job registrado correctamente", content = @Content(schema = @Schema(implementation = BulkJob.class))),
        @ApiResponse(responseCode = "409", description = "Error de negocio (por ej. 'Mismo archivo ya PROCESADO (por LOG)')", content = @Content(schema = @Schema(implementation = BulkJob.class))),
        @ApiResponse(responseCode = "403", description = "Prohibido", content = @Content(schema = @Schema(implementation = BulkJob.class))),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(schema = @Schema(implementation = BulkJob.class)))
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BulkJob> upload(
            @Parameter(
                name = "file",
                description = "Archivo de carga (.xlsx, .xls, .txt, .csv)",
                required = true,
                schema = @Schema(type = "string", format = "binary")
            )
            @RequestParam("file") MultipartFile file,

            @Parameter(
                description = "Permite reintentar aunque el mismo archivo (SHA1) ya fue PROCESADO",
                example = "false"
            )
            @RequestParam(name = "sobrescribir", defaultValue = "false") boolean sobrescribir
    ) {
        try {
            if (file == null || file.isEmpty()) {
                BulkJob err = new BulkJob();
                err.status = BulkStatus.ERROR;
                err.observacion = "No se encontró archivo: usa form-data con key 'file'.";
                return ResponseEntity.badRequest().body(err);
            }
            BulkJob job = service.registrarUpload(file.getOriginalFilename(), file.getBytes(), sobrescribir);
            HttpStatus st = (job.status == BulkStatus.ERROR) ? HttpStatus.CONFLICT : HttpStatus.OK;
            return new ResponseEntity<>(job, st);
        } catch (SecurityException se) {
            BulkJob err = new BulkJob();
            err.status = BulkStatus.ERROR;
            err.observacion = se.getMessage();
            return new ResponseEntity<>(err, HttpStatus.FORBIDDEN);
        } catch (Exception ex) {
            BulkJob err = new BulkJob();
            err.status = BulkStatus.ERROR;
            err.observacion = ex.getMessage();
            return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
        summary = "Procesar archivo previamente registrado",
        description = "Ejecuta parseo, validaciones y altas/actualizaciones. Devuelve lista de errores por fila/campo si aplica."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Resultado del procesamiento", content = @Content(schema = @Schema(implementation = BulkJob.class))),
        @ApiResponse(responseCode = "409", description = "Error de negocio (por ejemplo TTL vencido)", content = @Content(schema = @Schema(implementation = BulkJob.class)))
    })
    @PostMapping(value = "/procesar/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BulkJob> procesar(
            @Parameter(description = "ID del job devuelto al subir el archivo", required = true, example = "d37d4768-c5be-4719-be69-c20980c1a247")
            @PathVariable String id
    ) {
        BulkJob job = service.procesar(id);
        HttpStatus st = (job.status == BulkStatus.ERROR) ? HttpStatus.CONFLICT : HttpStatus.OK;
        return new ResponseEntity<>(job, st);
    }
}
