
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
@Tag(name = "Direcciones", description = "CRUD de direcciones")
public class DireccionRestController {

    @Autowired
    private DireccionJPADAOImplementation direccionJPADAOImplementation;

    @Operation(summary = "Agregar dirección a un usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Creado",
                    content = @Content(schema = @Schema(implementation = Result.class),
                            examples = @ExampleObject(value = """
                                    { "correct": true, "errorMessage": null, "object": { "idDireccion": 202 } }
                                    """))),
            @ApiResponse(responseCode = "500", description = "Error interno / Validación BD",
                    content = @Content(schema = @Schema(implementation = Result.class)))
    })
    @PostMapping("usuario/{idUsuario}/agregar")
    public ResponseEntity AddDireccion(
            @Parameter(description = "ID del usuario", example = "27") @PathVariable int idUsuario,
            @RequestBody(
                    description = "Datos de la dirección",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Direccion.class),
                            examples = @ExampleObject(value = """
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
                                    """)))
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

    @Operation(summary = "Obtener dirección por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = Result.class),
                            examples = @ExampleObject(value = """
                                    { "correct": true, "errorMessage": null, "object": { "idDireccion": 202, "calle": "Calle Reforma" } }
                                    """))),
            @ApiResponse(responseCode = "500", description = "Error interno",
                    content = @Content(schema = @Schema(implementation = Result.class)))
    })
    @GetMapping("get/{id}")
    public ResponseEntity GetById(@Parameter(description = "ID de la dirección", example = "202") @PathVariable int id) {
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

    @Operation(summary = "Actualizar dirección")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Actualizado",
                    content = @Content(schema = @Schema(implementation = Result.class),
                            examples = @ExampleObject(value = """
                                    { "correct": true, "errorMessage": null, "object": { "idDireccion": 202 } }
                                    """))),
            @ApiResponse(responseCode = "500", description = "Error interno / Validación BD",
                    content = @Content(schema = @Schema(implementation = Result.class)))
    })
    @PutMapping("update/{id}")
    public ResponseEntity Update(
            @Parameter(description = "ID de la dirección", example = "202") @PathVariable int id,
            @RequestBody(
                    description = "Campos a modificar",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Direccion.class),
                            examples = @ExampleObject(value = """
                                    { "calle": "Calle Reforma Norte", "numeroExterior": "252", "numeroInterior": "2B", "colonia": { "idColonia": 5678 } }
                                    """)))
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

    @Operation(summary = "Eliminar dirección")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Eliminado",
                    content = @Content(schema = @Schema(implementation = Result.class),
                            examples = @ExampleObject(value = """
                                    { "correct": true, "errorMessage": null, "object": null }
                                    """))),
            @ApiResponse(responseCode = "500", description = "Error interno",
                    content = @Content(schema = @Schema(implementation = Result.class)))
    })
    @DeleteMapping("delete/{id}")
    public ResponseEntity Delete(@Parameter(description = "ID de la dirección", example = "202") @PathVariable int id) {
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
