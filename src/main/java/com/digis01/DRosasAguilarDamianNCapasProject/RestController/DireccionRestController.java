
package com.digis01.DRosasAguilarDamianNCapasProject.RestController;
import com.digis01.DRosasAguilarDamianNCapasProject.DAO.DireccionJPADAOImplementation;
import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Direccion;
import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Result;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("direccionapi")
@CrossOrigin(origins = "*")
@Tag(
    name = "Direcciones",
    description = "CRUD de direcciones",
    externalDocs = @io.swagger.v3.oas.annotations.ExternalDocumentation(
        description = "Guía de uso de la API de Direcciones",
        url = "https://tu-dominio.com/docs/direcciones"
    )
)
public class DireccionRestController {

    @Autowired
    private DireccionJPADAOImplementation direccionJPADAOImplementation;

    // --------- CREAR ----------
    @Operation(
        operationId = "direcciones-crear",
        summary = "Agregar dirección a un usuario",
        description = """
            Crea una dirección y la asocia al usuario indicado por `idUsuario`.
            La dirección mínima requiere `calle`, `numeroExterior` y `colonia.idColonia`.
            El resultado viene en `Result.object`.
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Creado",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Result.class),
                examples = {
                    @ExampleObject(name = "OK básico", value = """
                        { "correct": true, "errorMessage": null, "object": { "idDireccion": 202 } }
                    """),
                    @ExampleObject(name = "OK con datos completos", value = """
                        {
                          "correct": true,
                          "errorMessage": null,
                          "object": {
                            "idDireccion": 202,
                            "calle": "Calle Reforma",
                            "numeroExterior": "250",
                            "numeroInterior": "1A"
                          }
                        }
                    """)
                })),
        @ApiResponse(responseCode = "500", description = "Error interno / Validación BD",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Result.class),
                examples = @ExampleObject(name = "Error SQL (ejemplo)", value = """
                    { "correct": false, "errorMessage": "could not execute statement; FK_COLONIA", "object": null }
                """)))
    })
    @PostMapping("usuario/{idUsuario}/agregar")
    public ResponseEntity AddDireccion(
        @Parameter(description = "ID del usuario a quien se asociará la dirección", example = "27")
        @PathVariable int idUsuario,
        @RequestBody(
            description = "Datos de la dirección a crear",
            required = true,
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Direccion.class),
                examples = {
                    @ExampleObject(name = "Mínimo requerido", value = """
                        {
                          "calle": "Calle Reforma",
                          "numeroExterior": "250",
                          "colonia": { "idColonia": 5678 }
                        }
                    """),
                    @ExampleObject(name = "Completo", value = """
                        {
                          "calle": "Calle Reforma",
                          "numeroExterior": "250",
                          "numeroInterior": "1A",
                          "colonia": {
                            "idColonia": 5678,
                            "codigoPostal": "02080",
                            "municipio": {
                              "idMunicipio": 15,
                              "estado": { "idEstado": 9, "pais": { "idPais": 1 } }
                            }
                          }
                        }
                    """)
                })
        )
        @org.springframework.web.bind.annotation.RequestBody Direccion direccion) {

        Result result;
        try {
            result = direccionJPADAOImplementation.AddDireccion(idUsuario, direccion);
            return ResponseEntity.status(200).body(result);
        } catch (Exception ex) {
            result = new Result();
            result.ex = ex;
            result.errorMessage = ex.getLocalizedMessage();
            result.correct = false;
            return ResponseEntity.status(500).body(result);
        }
    }

    // --------- OBTENER POR ID ----------
    @Operation(
        operationId = "direcciones-obtener-por-id",
        summary = "Obtener dirección por ID",
        description = "Devuelve la dirección en `Result.object`."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Result.class),
                examples = @ExampleObject(name = "OK", value = """
                    { "correct": true, "errorMessage": null, "object": { "idDireccion": 202, "calle": "Calle Reforma" } }
                """))),
        @ApiResponse(responseCode = "500", description = "Error interno",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Result.class),
                examples = @ExampleObject(name = "Error genérico", value = """
                    { "correct": false, "errorMessage": "Unexpected error", "object": null }
                """)))
    })
    @GetMapping("get/{id}")
    public ResponseEntity GetById(
        @Parameter(description = "ID de la dirección", example = "202") @PathVariable int id) {

        Result result;
        try {
            result = direccionJPADAOImplementation.GetByIdDireccion(id);
            result.correct = true;
            return ResponseEntity.status(200).body(result);
        } catch (Exception ex) {
            result = new Result();
            result.ex = ex;
            result.errorMessage = ex.getLocalizedMessage();
            result.correct = false;
            return ResponseEntity.status(500).body(result);
        }
    }

    // --------- ACTUALIZAR ----------
    @Operation(
        operationId = "direcciones-actualizar",
        summary = "Actualizar dirección",
        description = "Actualiza campos de la dirección. El `id` de la URL prevalece."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Actualizado",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Result.class),
                examples = @ExampleObject(name = "OK", value = """
                    { "correct": true, "errorMessage": null, "object": { "idDireccion": 202 } }
                """))),
        @ApiResponse(responseCode = "500", description = "Error interno / Validación BD",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Result.class),
                examples = @ExampleObject(name = "Error de validación", value = """
                    { "correct": false, "errorMessage": "FK_COLONIA no encontrada", "object": null }
                """)))
    })
    @PutMapping("update/{id}")
    public ResponseEntity Update(
        @Parameter(description = "ID de la dirección a actualizar", example = "202") @PathVariable int id,
        @RequestBody(
            description = "Campos a modificar (solo los presentes serán procesados por tu capa JPA/DAO)",
            required = true,
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Direccion.class),
                examples = {
                    @ExampleObject(name = "Cambios simples", value = """
                        { "calle": "Calle Reforma Norte", "numeroExterior": "252" }
                    """),
                    @ExampleObject(name = "Con interior y colonia", value = """
                        { "calle": "Calle Reforma Norte", "numeroExterior": "252", "numeroInterior": "2B", "colonia": { "idColonia": 5678 } }
                    """)
                })
        )
        @org.springframework.web.bind.annotation.RequestBody Direccion direccion) {

        Result result;
        try {
            direccion.setIdDireccion(id);
            result = direccionJPADAOImplementation.Update(direccion);
            result.correct = true;
            return ResponseEntity.status(200).body(result);
        } catch (Exception ex) {
            result = new Result();
            result.ex = ex;
            result.errorMessage = ex.getLocalizedMessage();
            result.correct = false;
            return ResponseEntity.status(500).body(result);
        }
    }

    // --------- ELIMINAR ----------
    @Operation(
        operationId = "direcciones-eliminar",
        summary = "Eliminar dirección",
        description = "Elimina por ID"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Eliminado",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Result.class),
                examples = @ExampleObject(name = "OK", value = """
                    { "correct": true, "errorMessage": null, "object": null }
                """))),
        @ApiResponse(responseCode = "500", description = "Error interno",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Result.class),
                examples = @ExampleObject(name = "Error genérico", value = """
                    { "correct": false, "errorMessage": "Unexpected error", "object": null }
                """)))
    })
    @DeleteMapping("delete/{id}")
    public ResponseEntity Delete(
        @Parameter(description = "ID de la dirección a eliminar", example = "202") @PathVariable int id) {

        Result result;
        try {
            result = direccionJPADAOImplementation.Delete(id);
            result.correct = true;
            return ResponseEntity.status(200).body(result);
        } catch (Exception ex) {
            result = new Result();
            result.ex = ex;
            result.errorMessage = ex.getLocalizedMessage();
            result.correct = false;
            return ResponseEntity.status(500).body(result);
        }
    }
}