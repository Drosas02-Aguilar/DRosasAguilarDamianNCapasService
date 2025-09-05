
package com.digis01.DRosasAguilarDamianNCapasProject.RestController;
import com.digis01.DRosasAguilarDamianNCapasProject.DAO.DireccionJPADAOImplementation;
import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Direccion;
import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Result;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("direccionapi")
@CrossOrigin(origins = "*") 

public class DireccionRestController {
    
    @Autowired 
            private DireccionJPADAOImplementation direccionJPADAOImplementation;
    
    // POST: agregar dirección a un usuario
    @PostMapping("usuario/{idUsuario}/agregar")
    public ResponseEntity AddDireccion(@PathVariable int idUsuario, @RequestBody Direccion direccion) {
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

    // GET: dirección por Id
    @GetMapping("get/{id}")
    public ResponseEntity GetById(@PathVariable int id) {
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

    // PUT: actualizar dirección (solo dirección; no cambia usuario)
    @PutMapping("update/{id}")
    public ResponseEntity Update(@PathVariable int id, @RequestBody Direccion direccion) {
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

    // DELETE: eliminar dirección
    @DeleteMapping("delete/{id}")
    public ResponseEntity Delete(@PathVariable int id) {
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
