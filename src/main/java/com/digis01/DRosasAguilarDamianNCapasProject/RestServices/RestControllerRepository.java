package com.digis01.DRosasAguilarDamianNCapasProject.RestServices;

import com.digis01.DRosasAguilarDamianNCapasProject.DAO.IRepositoryUsuario;
import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Result;
import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Usuario;
import com.digis01.DRosasAguilarDamianNCapasProject.Service.ServiceUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("usuariorepositoy")
@RestController
@CrossOrigin(origins = "*")

public class RestControllerRepository {

    @Autowired
    private IRepositoryUsuario iRepositoryUsuario;
    
    @Autowired
    private ServiceUsuario serviceUsuario;

    @GetMapping()
    public ResponseEntity GetAlll() {

     Result result = serviceUsuario.GetAlll();
     return ResponseEntity.status(result.status).body(result);

    }

  @GetMapping("get/{id}")
    public ResponseEntity GetByIdUsuario(@PathVariable int id) {
        Result result = serviceUsuario.GetById(id);
        return ResponseEntity.status(result.status).body(result);
    }
    

    @GetMapping("direcciones/{id}")
    public ResponseEntity DireccionesByIdUsuario(@PathVariable int id) {
        Result result = serviceUsuario.DireccionesByIdUsuario(id);
        return ResponseEntity.status(result.status).body(result); 

    }
    
    
    @PostMapping("agregar")
public ResponseEntity Add(@RequestBody Usuario usuario){
    Result result = serviceUsuario.Add(usuario);
    return ResponseEntity.status(result.status).body(result);
}

@PutMapping("update/{id}")
public ResponseEntity Update(@PathVariable int id,
                             @RequestBody Usuario usuario) {
    Result result = serviceUsuario.Update(id, usuario);
    return ResponseEntity.status(result.status).body(result);
}


 @DeleteMapping("delete/{id}")
    public ResponseEntity Delete(@PathVariable int id) {
        Result result = serviceUsuario.Delete(id);
        return ResponseEntity.status(result.status).body(result);
    }
    
    @PatchMapping("setActivo/{id}")
public ResponseEntity SetActivo(@PathVariable int id,
                                @RequestParam boolean activo,
                                @RequestParam(required = false, defaultValue = "system") String usuarioBaja) {
    Result result = serviceUsuario.SetActivo(id, activo, usuarioBaja);
    return ResponseEntity.status(result.status).body(result);
}


}
