package com.digis01.DRosasAguilarDamianNCapasProject.Service;

import com.digis01.DRosasAguilarDamianNCapasProject.DAO.*;
import com.digis01.DRosasAguilarDamianNCapasProject.JPA.*;
import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Result;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class ServiceCatalogo {

    @Autowired private IRepositoryPais iRepositoryPais;
    @Autowired private IRepositoryEstado iRepositoryEstado;
    @Autowired private IRepositoryMunicipio iRepositoryMunicipio;
    @Autowired private IRepositoryColonia iRepositoryColonia;

    @Transactional(readOnly = true)
    public Result GetAllPais(){
        Result r = new Result();
        try {
            List<Pais> lista = iRepositoryPais.findAllOrderByNombre();
            r.object = (lista != null) ? lista : Collections.emptyList();
            r.correct = true; r.status = 200;
        } catch (Exception ex) {
            r.ex = ex; r.errorMessage = ex.getLocalizedMessage();
            r.correct = false; r.status = 500;
        }
        return r;
    }

    @Transactional(readOnly = true)
    public Result EstadoByidPais(int idPais) {
        Result r = new Result();
        try {
            List<Estado> lista = iRepositoryEstado.findAllByPais(idPais);
            r.object = (lista != null) ? lista : Collections.emptyList();
            r.correct = true; r.status = 200;
        } catch (Exception ex) {
            r.ex = ex; r.errorMessage = ex.getLocalizedMessage();
            r.correct = false; r.status = 500;
        }
        return r;
    }

    @Transactional(readOnly = true)
    public Result MunicipioByidEstado(int idEstado) {
        Result r = new Result();
        try {
            List<Municipio> lista = iRepositoryMunicipio.findAllByEstado(idEstado);
            r.object = (lista != null) ? lista : Collections.emptyList();
            r.correct = true; r.status = 200;
        } catch (Exception ex) {
            r.ex = ex; r.errorMessage = ex.getLocalizedMessage();
            r.correct = false; r.status = 500;
        }
        return r;
    }

    @Transactional(readOnly = true)
    public Result ColoniaByMunicipio(int idMunicipio) {
        Result r = new Result();
        try {
            List<Colonia> lista = iRepositoryColonia.findAllByMunicipio(idMunicipio);
            r.object = (lista != null) ? lista : Collections.emptyList();
            r.correct = true; r.status = 200;
        } catch (Exception ex) {
            r.ex = ex; r.errorMessage = ex.getLocalizedMessage();
            r.correct = false; r.status = 500;
        }
        return r;
    }
}
