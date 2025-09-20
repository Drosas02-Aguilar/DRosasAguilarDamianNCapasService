/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.digis01.DRosasAguilarDamianNCapasProject.RestServices;

import com.digis01.DRosasAguilarDamianNCapasProject.DAO.IRepositoryRol;
import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Result;
import com.digis01.DRosasAguilarDamianNCapasProject.Service.ServiceRol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RequestMapping("rolrepositoy")
@RestController
@CrossOrigin(origins = "*")


public class RestControllerRepositoryRol {
    
    @Autowired
     private IRepositoryRol iRepositoryRol;
    @Autowired
    private ServiceRol serviceRol;
    
    
    @GetMapping("getall")
    public ResponseEntity GetAllRol(){
    
        Result result = serviceRol.GetAllRol();
        return ResponseEntity.status(result.status).body(result);
    }
    
    
    
}
