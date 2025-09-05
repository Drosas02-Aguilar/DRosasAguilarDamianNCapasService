package com.digis01.DRosasAguilarDamianNCapasProject.RestController;

import com.digis01.DRosasAguilarDamianNCapasProject.DAO.UsuarioJPADAOImplementation;
import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Direccion;
import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Result;
import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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

@RestController
@RequestMapping("usuarioapi")
@CrossOrigin(origins = "*") 
public class UsuarioRestController {

    @Autowired
    private UsuarioJPADAOImplementation usuarioJPADAOImplementation;

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

    /*===================== AGREGAR USUARIO ==========================*/
    @PostMapping("agregar")
    public ResponseEntity Add(@RequestBody Usuario usuario) {

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

    /*===================== SELECCIOANR USUARIO ==========================*/
    @GetMapping("get/{id}")
    public ResponseEntity GetByIdUsuario(@PathVariable int id) {

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

    /*===================== ACTUALIZAR ==========================*/
    @PutMapping("update/{id}")
    public ResponseEntity Update(@PathVariable int id, @RequestBody Usuario usuario) {
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


    /*===================== ALUMNO Y DIRECCIONES ==========================*/
    @GetMapping("direcciones/{id}")
    public ResponseEntity DireccionesByIdUsuario(@PathVariable int id) {

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

    /*===================== DELETE ==========================*/
    @DeleteMapping("delete/{id}")
    public ResponseEntity Delete(@PathVariable int id) {
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

    /*===================== STATUS ==========================*/

    @PatchMapping("setActivo/{id}")
    public ResponseEntity SetActivo(
            @PathVariable int id,
            @RequestParam boolean activo,
            @RequestParam(required = false, defaultValue = "system") String usuarioBaja) {
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
