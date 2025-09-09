package com.digis01.DRosasAguilarDamianNCapasProject.RestController;

import com.digis01.DRosasAguilarDamianNCapasProject.DAO.IRolJPADAO;
import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("rolapi")
@Tag(name = "Roles", description = "Catálogo de roles")
public class RolRestController {

    @Autowired
    private IRolJPADAO rolDAO;

    @Operation(
        operationId = "roles-listar",
        summary = "Listar roles",
        description = "Obtiene el catálogo de roles. La lista viene en `Result.object`."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Result.class),
                examples = {
                    @ExampleObject(name = "Con roles", value = """
                        {
                          "correct": true,
                          "object": [
                            { "idRol": 1, "nombre": "diseñador UIX" },
                            { "idRol": 2, "nombre": "desarrolador" }
                          ]
                        }
                    """),
                    @ExampleObject(name = "Vacío", value = """
                        { "correct": true, "object": [] }
                    """)
                }
            )
        ),
        @ApiResponse(responseCode = "500", description = "Error interno",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Result.class),
                examples = @ExampleObject(name = "Error genérico", value = """
                    { "correct": false, "errorMessage": "Unexpected error", "object": null }
                """)
            )
        )
    })
    @GetMapping("getall")
    public ResponseEntity GetAllRol() {
        Result result;
        try {
            result = rolDAO.GetAllRol();
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