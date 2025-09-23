package com.digis01.DRosasAguilarDamianNCapasProject.RestServices;

import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Result;
import com.digis01.DRosasAguilarDamianNCapasProject.Service.ServiceCatalogo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("catalogorepositoy")
@CrossOrigin(origins = "*")
public class RestControllerRepositoryCatalogo {

    @Autowired
    private ServiceCatalogo serviceCatalogo;

    @GetMapping(value = "paises", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity GetAllPais() {
        Result result = serviceCatalogo.GetAllPais();
        return ResponseEntity.status(result.status).body(result);
    }

    @GetMapping(value = "estados/{idPais}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity EstadoByidPais(@PathVariable int idPais) {
        Result result = serviceCatalogo.EstadoByidPais(idPais);
        return ResponseEntity.status(result.status).body(result);
    }

    @GetMapping(value = "municipios/{idEstado}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity MunicipioByidEstado(@PathVariable int idEstado) {
        Result result = serviceCatalogo.MunicipioByidEstado(idEstado);
        return ResponseEntity.status(result.status).body(result);
    }

    @GetMapping(value = "colonias/{idMunicipio}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity ColoniaByMunicipio(@PathVariable int idMunicipio) {
        Result result = serviceCatalogo.ColoniaByMunicipio(idMunicipio);
        return ResponseEntity.status(result.status).body(result);
    }
}
