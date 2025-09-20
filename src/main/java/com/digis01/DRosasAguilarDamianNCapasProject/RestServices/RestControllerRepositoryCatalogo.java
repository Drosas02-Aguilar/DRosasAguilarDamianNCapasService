package com.digis01.DRosasAguilarDamianNCapasProject.RestServices;

import com.digis01.DRosasAguilarDamianNCapasProject.DAO.IRepositoryColonia;
import com.digis01.DRosasAguilarDamianNCapasProject.DAO.IRepositoryEstado;
import com.digis01.DRosasAguilarDamianNCapasProject.DAO.IRepositoryMunicipio;
import com.digis01.DRosasAguilarDamianNCapasProject.DAO.IRepositoryPais;
import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Result;
import com.digis01.DRosasAguilarDamianNCapasProject.Service.ServiceCatalogo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("catalogorepositoy")
@CrossOrigin(origins = "*")
public class RestControllerRepositoryCatalogo {

    @Autowired
    private IRepositoryPais iRepositoryPais;
    @Autowired
    private IRepositoryEstado iRepositoryEstado;
    @Autowired
    private IRepositoryMunicipio iRepositoryMunicipio;
    @Autowired
    private IRepositoryColonia iRepositoryColonia;

    @Autowired
    ServiceCatalogo serviceCatalogo;

    @GetMapping("paises")
    public ResponseEntity GetAllPais() {

        Result result = serviceCatalogo.GetAllPais();
        return ResponseEntity.status(result.status).body(result);
    }

    @GetMapping("estados/{idPais}")
    public ResponseEntity EstadoByidPais(@PathVariable int idPais) {
        Result result = serviceCatalogo.EstadoByidPais(idPais);
        return ResponseEntity.status(result.status).body(result);

    }

    @GetMapping("municipios/{idEstado}")
    public ResponseEntity MunicipioByidEstado(@PathVariable int idEstado) {
        Result result = serviceCatalogo.MunicipioByidEstado(idEstado);
        return ResponseEntity.status(result.status).body(result);
    }

    @GetMapping("colonias/{idMunicipio}")
    public ResponseEntity ColoniaByMunicipio(@PathVariable int idMunicipio) {
        Result result = serviceCatalogo.ColoniaByMunicipio(idMunicipio);
        return ResponseEntity.status(result.status).body(result);

    }

}
