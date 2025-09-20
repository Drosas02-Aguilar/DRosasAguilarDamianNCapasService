/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.digis01.DRosasAguilarDamianNCapasProject.RestServices;

import com.digis01.DRosasAguilarDamianNCapasProject.DAO.IRepositoryColonia;
import com.digis01.DRosasAguilarDamianNCapasProject.DAO.IRepositoryDireccion;
import com.digis01.DRosasAguilarDamianNCapasProject.DAO.IRepositoryUsuario;
import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Direccion;
import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Result;
import com.digis01.DRosasAguilarDamianNCapasProject.Service.ServiceDIreccion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("direccionrepositoy")
@CrossOrigin(origins = "*")
public class RestControllerRepositoryDireccion {
    
    @Autowired
    private IRepositoryColonia iRepositoryColonia;
    @Autowired
    private IRepositoryDireccion iRepositoryDireccion;
    @Autowired
    private IRepositoryUsuario iRepositoryUsuario;
    
    @Autowired
    ServiceDIreccion serviceDIreccion;
            
    
   
    
    @GetMapping("get/{id}")
    public ResponseEntity GetByIdDireccion(@PathVariable int id){
        Result result = serviceDIreccion.GetByIdDireccion(id);
        return ResponseEntity.status(result.status).body(result);
    
    
    }
    
    
    @PostMapping("usuario/{idUsuario}/agregar")
public ResponseEntity AddDireccion(@PathVariable int idUsuario,
                                   @RequestBody Direccion direccion) {
    Result result =  serviceDIreccion.AddDireccion(idUsuario, direccion);
    return ResponseEntity.status(result.status).body(result);

}


    @PutMapping("update/{id}")
public ResponseEntity Update(@PathVariable int id,
                             @RequestBody Direccion direccion) {
    // delega y respeta el status que regrese el service/dao
    Result result = serviceDIreccion.Update(id, direccion);
    return ResponseEntity.status(result.status).body(result);
}
    
    
}
