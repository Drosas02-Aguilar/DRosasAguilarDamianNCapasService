package com.digis01.DRosasAguilarDamianNCapasProject.DAO;

import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Municipio;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface IRepositoryMunicipio extends JpaRepository<Municipio, Integer> {

    // m.Estado.IdEstado y m.Nombre (tal cual en tus entidades)
    @Query("SELECT m FROM Municipio m WHERE m.Estado.IdEstado = :idEstado ORDER BY m.Nombre ASC")
    List<Municipio> findAllByEstado(@Param("idEstado") Integer idEstado);
}
