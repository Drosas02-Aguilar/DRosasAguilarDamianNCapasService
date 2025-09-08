package com.digis01.DRosasAguilarDamianNCapasProject.RestController;

import com.digis01.DRosasAguilarDamianNCapasProject.DAO.IPaisJPADAO;
import com.digis01.DRosasAguilarDamianNCapasProject.DAO.IEstadoJPADAO;
import com.digis01.DRosasAguilarDamianNCapasProject.DAO.IMunicipioJPADAO;
import com.digis01.DRosasAguilarDamianNCapasProject.DAO.IColoniaJPADAO;
import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Result;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("catalogoapi")
@CrossOrigin(origins = "*")
@Tag(name = "Catálogos", description = "Países, Estados, Municipios, Colonias")
public class CatalogoRestController {

    @Autowired private IPaisJPADAO paisDAO;
    @Autowired private IEstadoJPADAO estadoDAO;
    @Autowired private IMunicipioJPADAO municipioDAO;
    @Autowired private IColoniaJPADAO coloniaDAO;

    @Operation(summary = "Listar países")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = Result.class),
                            examples = @ExampleObject(value = """
                                    { "correct": true, "object": [ { "idPais": 1, "nombre": "México" }, { "idPais": 2, "nombre": "Estados Unidos" } ] }
                                    """))),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = Result.class)))
    })
    @GetMapping("paises")
    public ResponseEntity GetAllPais() {
        Result result;
        try {
            result = paisDAO.GetAllPais();
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

    @Operation(summary = "Estados por país")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = Result.class),
                            examples = @ExampleObject(value = """
                                    { "correct": true, "object": [ { "idEstado": 9, "nombre": "Ciudad de México", "pais": { "idPais": 1 } } ] }
                                    """))),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = Result.class)))
    })
    @GetMapping("estados/{idPais}")
    public ResponseEntity EstadoByidPais(@Parameter(description = "ID del país", example = "1") @PathVariable int idPais) {
        Result result;
        try {
            result = estadoDAO.EstadoByidPais(idPais);
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

    @Operation(summary = "Municipios por estado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = Result.class),
                            examples = @ExampleObject(value = """
                                    { "correct": true, "object": [ { "idMunicipio": 15, "nombre": "Benito Juárez", "estado": { "idEstado": 9 } } ] }
                                    """))),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = Result.class)))
    })
    @GetMapping("municipios/{idEstado}")
    public ResponseEntity MunicipioByidEstado(@Parameter(description = "ID del estado", example = "9") @PathVariable int idEstado) {
        Result result;
        try {
            result = municipioDAO.MunicipioByidEstado(idEstado);
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

    @Operation(summary = "Colonias por municipio")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = Result.class),
                            examples = @ExampleObject(value = """
                                    { "correct": true, "object": [ { "idColonia": 5678, "nombre": "Del Valle", "codigoPostal": "03100" } ] }
                                    """))),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = Result.class)))
    })
    @GetMapping("colonias/{idMunicipio}")
    public ResponseEntity ColoniaByMunicipio(@Parameter(description = "ID del municipio", example = "15") @PathVariable int idMunicipio) {
        Result result;
        try {
            result = coloniaDAO.ColoniaByMunicipio(idMunicipio);
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