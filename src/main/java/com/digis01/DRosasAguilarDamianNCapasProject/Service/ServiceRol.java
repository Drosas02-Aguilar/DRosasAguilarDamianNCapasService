

package com.digis01.DRosasAguilarDamianNCapasProject.Service;

import com.digis01.DRosasAguilarDamianNCapasProject.DAO.IRepositoryRol;
import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceRol {
    
    @Autowired
     private IRepositoryRol iRepositoryRol;
    
    public Result GetAllRol(){
        Result result = new Result();
        
        try {
            result.correct = true;
            result.object = iRepositoryRol.findAll();
            result.status = 200;
        }
        catch(Exception ex) {
            result.ex = ex;
            result.errorMessage = ex.getLocalizedMessage();
            result.correct = false;
            result.status = 500;
        }
        return result;
}
    
}
