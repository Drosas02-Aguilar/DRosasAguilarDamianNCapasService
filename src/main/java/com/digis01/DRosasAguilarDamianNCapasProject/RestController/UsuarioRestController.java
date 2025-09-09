package com.digis01.DRosasAguilarDamianNCapasProject.RestController;

import com.digis01.DRosasAguilarDamianNCapasProject.DAO.UsuarioJPADAOImplementation;
import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Direccion;
import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Result;
import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("usuarioapi")
@CrossOrigin(origins = "*")
@Tag(name = "Usuarios", description = "CRUD de usuarios y consultas con direcciones")
public class UsuarioRestController {

    @Autowired
    private UsuarioJPADAOImplementation usuarioJPADAOImplementation;

    @Operation(
            summary = "Listar usuarios",
            description = "Obtiene todos los usuarios. Devuelve `Result.objects` como arreglo de usuarios."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Result.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "correct": true,
                                      "errorMessage": null,
                                      "objects": [
                                        { "idUsuario": 1, "nombre": "Damián", "apellidopaterno": "Rosas", "email": "damian.rosas@gmail.com", "status": 1 },
                                        { "idUsuario": 2, "nombre": "Ana", "apellidopaterno": "Sánchez", "email": "ana.sanchez@gmail.com", "status": 1 }
                                      ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "500", description = "Error interno",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Result.class),
                            examples = @ExampleObject(value = """
                                    { "correct": false, "errorMessage": "Unexpected error", "object": null }
                                    """)))
    })
    @GetMapping()
    public ResponseEntity GetAll() {
        Result result;
        try {
            result = usuarioJPADAOImplementation.GetAll();
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

    @Operation(
            summary = "Agregar usuario",
            description = """
                    Crea un usuario. Puedes incluir direcciones en `usuario.direcciones[]`.
                    Recomendación: usar emails válidos (p. ej. `nombre.apellido@gmail.com`).
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Creado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Result.class),
                            examples = @ExampleObject(name = "OK", value = """
                                    {
                                      "correct": true,
                                      "errorMessage": null,
                                      "object": { "idUsuario": 27, "nombre": "Ana", "email": "ana.sanchez@gmail.com", "status": 1 }
                                    }
                                    """))),
            @ApiResponse(responseCode = "500", description = "Error interno / Validación BD",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Result.class),
                            examples = @ExampleObject(name = "Email duplicado", value = """
                                    {
                                      "correct": false,
                                      "errorMessage": "could not execute statement; constraint [uk_usuario_email]",
                                      "object": null
                                    }
                                    """)))
    })
    @PostMapping("agregar")
    public ResponseEntity Add(
            @RequestBody(
                    description = "Usuario a crear (con o sin direcciones)",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Usuario.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "nombre": "Ana",
                                      "apellidopaterno": "Sánchez",
                                      "apellidomaterno": "López",
                                      "sexo": "F",
                                      "curp": "SALA920101MDFXXX09",
                                      "fechaNacimiento": "1992-01-01",
                                      "username": "ana.sanchez",
                                      "email": "ana.sanchez@gmail.com",
                                      "password": "AnaSegura_2024",
                                      "telefono": "5512345678",
                                      "celular": "5512345678",
                                      "rol": { "idRol": 2 },
                                      "imagen": null,
                                      "direcciones": [
                                        {
                                          "calle": "Av. Centro",
                                          "numeroExterior": "100",
                                          "numeroInterior": "2B",
                                          "colonia": {
                                            "idColonia": 1234,
                                            "codigoPostal": "03100",
                                            "municipio": {
                                              "idMunicipio": 1,
                                              "estado": { "idEstado": 9, "pais": { "idPais": 1 } }
                                            }
                                          }
                                        }
                                      ]
                                    }
                                    """))
            )
            @org.springframework.web.bind.annotation.RequestBody Usuario usuario) {

        Result result;
        try {
            if (usuario.direcciones != null) {
                for (Direccion d : usuario.direcciones) {
                    d.setUsuario(usuario);
                }
            }
            result = usuarioJPADAOImplementation.Add(usuario);
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

    @Operation(
            summary = "Obtener usuario por ID",
            description = "Devuelve el usuario en `Result.object`."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = Result.class),
                            examples = @ExampleObject(value = """
                                    { "correct": true, "errorMessage": null,
                                      "object": { "idUsuario": 27, "nombre": "Ana", "email": "ana.sanchez@gmail.com", "status": 1 } }
                                    """))),
            @ApiResponse(responseCode = "500", description = "Error interno",
                    content = @Content(schema = @Schema(implementation = Result.class)))
    })
    @GetMapping("get/{id}")
    public ResponseEntity GetByIdUsuario(
            @Parameter(description = "ID del usuario", example = "27") @PathVariable int id) {
        Result result;
        try {
            result = usuarioJPADAOImplementation.GetByIdUsuario(id);
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

    @Operation(
            summary = "Actualizar usuario",
            description = "Actualiza campos del usuario. El `id` de la URL prevalece."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Actualizado",
                    content = @Content(schema = @Schema(implementation = Result.class),
                            examples = @ExampleObject(value = """
                                    { "correct": true, "errorMessage": null, "object": { "idUsuario": 27 } }
                                    """))),
            @ApiResponse(responseCode = "500", description = "Error interno / Validación BD",
                    content = @Content(schema = @Schema(implementation = Result.class)))
    })
    @PutMapping("update/{id}")
    public ResponseEntity Update(
            @Parameter(description = "ID del usuario", example = "27") @PathVariable int id,
            @RequestBody(
                    description = "Campos a actualizar",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Usuario.class),
                            examples = @ExampleObject(value = """
                                    {"nombre": "Ana",
                                                                          "apellidopaterno": "Sánchez",
                                                                          "apellidomaterno": "Martinez",
                                                                          "sexo": "F",
                                                                          "curp": "SALA920101MDFXXX09",
                                                                          "fechaNacimiento": "1992-01-01", "email": "ana.sanchez.upd@gmail.com", "telefono": "5511122233", "celular": "5511122233", "tiposangre": "A-", "rol": { "idRol": 3 } }
                                    """))
            )
            @org.springframework.web.bind.annotation.RequestBody Usuario usuario) {
        Result result;
        try {
            usuario.setIdUsuario(id);
            result = usuarioJPADAOImplementation.Update(usuario);
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

    @Operation(
            summary = "Usuario + Direcciones",
            description = "Devuelve el usuario con sus direcciones en `Result.object`."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = Result.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "correct": true,
                                      "errorMessage": null,
                                      "object": {
                                        "idUsuario": 27,
                                        "nombre": "Ana",
                                        "direcciones": [
                                          { "idDireccion": 101, "calle": "Av. Centro", "numeroExterior": "100" }
                                        ]
                                      }
                                    }
                                    """))),
            @ApiResponse(responseCode = "500", description = "Error interno",
                    content = @Content(schema = @Schema(implementation = Result.class)))
    })
    @GetMapping("direcciones/{id}")
    public ResponseEntity DireccionesByIdUsuario(
            @Parameter(description = "ID del usuario", example = "27") @PathVariable int id) {
        Result result;
        try {
            result = usuarioJPADAOImplementation.DireccionesByIdUsuario(id);
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

    @Operation(summary = "Eliminar usuario", description = "Elimina por ID")
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
    public ResponseEntity Delete(
            @Parameter(description = "ID del usuario", example = "27") @PathVariable int id) {
        Result result;
        try {
            result = usuarioJPADAOImplementation.Delete(id);
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

    @Operation(
            summary = "Cambiar estatus activo/inactivo",
            description = "Activa o desactiva un usuario. `usuarioBaja` opcional (default = system)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Actualizado",
                    content = @Content(schema = @Schema(implementation = Result.class),
                            examples = @ExampleObject(value = """
                                    { "correct": true, "errorMessage": null, "object": { "idUsuario": 27, "status": 0 } }
                                    """))),
            @ApiResponse(responseCode = "500", description = "Error interno",
                    content = @Content(schema = @Schema(implementation = Result.class)))
    })
    @PatchMapping("setActivo/{id}")
    public ResponseEntity SetActivo(
            @Parameter(description = "ID del usuario", example = "27") @PathVariable int id,
            @Parameter(description = "true=activar, false=desactivar", example = "false") @RequestParam boolean activo,
            @Parameter(description = "Usuario que realiza la baja (opcional)", example = "admin") @RequestParam(required = false, defaultValue = "system") String usuarioBaja) {
        Result result;
        try {
            result = usuarioJPADAOImplementation.SetActivo(id, activo, usuarioBaja);
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