package com.digis01.DRosasAguilarDamianNCapasProject.RestController;

import com.digis01.DRosasAguilarDamianNCapasProject.DAO.IRolJPADAO;
import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("rolapi")
public class RolRestController {

    @Autowired
    private IRolJPADAO rolDAO;

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
