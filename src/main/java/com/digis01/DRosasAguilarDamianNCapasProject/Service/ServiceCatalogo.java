
package com.digis01.DRosasAguilarDamianNCapasProject.Service;

import com.digis01.DRosasAguilarDamianNCapasProject.DAO.IRepositoryColonia;
import com.digis01.DRosasAguilarDamianNCapasProject.DAO.IRepositoryEstado;
import com.digis01.DRosasAguilarDamianNCapasProject.DAO.IRepositoryMunicipio;
import com.digis01.DRosasAguilarDamianNCapasProject.DAO.IRepositoryPais;
import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Colonia;
import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Estado;
import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Municipio;
import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Result;
import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Usuario;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceCatalogo {
    
    @Autowired
    private IRepositoryPais iRepositoryPais;
    @Autowired
    private IRepositoryEstado iRepositoryEstado;
    @Autowired
    private IRepositoryMunicipio iRepositoryMunicipio;
    @Autowired
    private IRepositoryColonia iRepositoryColonia;
    
    public Result GetAllPais(){
     Result result = new Result();
        try {
            result.correct = true;
            result.object = iRepositoryPais.findAll();
            result.status = 200;

        } catch (Exception ex) {
            result.ex = ex;
            result.errorMessage = ex.getLocalizedMessage();
            result.correct = false;
            result.status = 500;
        }
        return result;
    
    }
    
        public Result EstadoByidPais(int idPais) {
        Result result = new Result();
        try {
            Optional<Estado> estado = iRepositoryEstado.findById(idPais);
            if (estado.isPresent()) {
                result.object = estado.get();
                result.correct = true;
                result.status = 200;
            } else {
                result.correct = false;
                result.errorMessage = "Usuario con id " + idPais + " no encontrado.";
                result.status = 404;
            }
        } catch (Exception ex) {
            result.ex = ex;
            result.errorMessage = ex.getLocalizedMessage();
            result.correct = false;
            result.status = 500;
        }
        return result;
    }
        
        
        public Result MunicipioByidEstado(int idEstado) {
        Result result = new Result();
        try {
            Optional<Municipio> municipio = iRepositoryMunicipio.findById(idEstado);
            if (municipio.isPresent()) {
                result.object = municipio.get();
                result.correct = true;
                result.status = 200;
            } else {
                result.correct = false;
                result.errorMessage = "Usuario con id " + idEstado + " no encontrado.";
                result.status = 404;
            }
        } catch (Exception ex) {
            result.ex = ex;
            result.errorMessage = ex.getLocalizedMessage();
            result.correct = false;
            result.status = 500;
        }
        return result;
    }
        
        
             public Result ColoniaByMunicipio(int idMunicipio) {
        Result result = new Result();
        try {
            Optional<Colonia> colonia = iRepositoryColonia.findById(idMunicipio);
            if (colonia.isPresent()) {
                result.object = colonia.get();
                result.correct = true;
                result.status = 200;
            } else {
                result.correct = false;
                result.errorMessage = "Usuario con id " + idMunicipio + " no encontrado.";
                result.status = 404;
            }
        } catch (Exception ex) {
            result.ex = ex;
            result.errorMessage = ex.getLocalizedMessage();
            result.correct = false;
            result.status = 500;
        }
        return result;
    }
    
    
    
    
    
}
