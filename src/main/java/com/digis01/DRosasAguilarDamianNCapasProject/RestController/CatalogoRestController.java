package com.digis01.DRosasAguilarDamianNCapasProject.RestController;

import com.digis01.DRosasAguilarDamianNCapasProject.DAO.IPaisJPADAO;
import com.digis01.DRosasAguilarDamianNCapasProject.DAO.IEstadoJPADAO;
import com.digis01.DRosasAguilarDamianNCapasProject.DAO.IMunicipioJPADAO;
import com.digis01.DRosasAguilarDamianNCapasProject.DAO.IColoniaJPADAO;
import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Result;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("catalogoapi")
@CrossOrigin(origins = "*") 
public class CatalogoRestController {

    @Autowired
    private IPaisJPADAO paisDAO;

    @Autowired
    private IEstadoJPADAO estadoDAO;

    @Autowired
    private IMunicipioJPADAO municipioDAO;

    @Autowired
    private IColoniaJPADAO coloniaDAO;

    // =======================
    // PAISES
    // =======================
    @GetMapping("paises")
    public ResponseEntity GetAllPais() {
        Result result;
        try {
            result = paisDAO.GetAllPais();
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

    // =======================
    // ESTADOS por País
    // =======================
    @GetMapping("estados/{idPais}")
    public ResponseEntity EstadoByidPais(@PathVariable int idPais) {
        Result result;
        try {
            result = estadoDAO.EstadoByidPais(idPais);
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

    // =======================
    // MUNICIPIOS por Estado
    // =======================
    @GetMapping("municipios/{idEstado}")
    public ResponseEntity MunicipioByidEstado(@PathVariable int idEstado) {
        Result result;
        try {
            result = municipioDAO.MunicipioByidEstado(idEstado);
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

    // =======================
    // COLONIAS por Municipio
    // =======================
    @GetMapping("colonias/{idMunicipio}")
    public ResponseEntity ColoniaByMunicipio(@PathVariable int idMunicipio) {
        Result result;
        try {
            result = coloniaDAO.ColoniaByMunicipio(idMunicipio);
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
