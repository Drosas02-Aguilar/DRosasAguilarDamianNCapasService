package com.digis01.DRosasAguilarDamianNCapasProject.DAO;

import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Colonia;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface IRepositoryColonia extends JpaRepository<Colonia, Integer> {

    // c.Municipio.IdMunicipio y c.Nombre (tal cual)
    @Query("SELECT c FROM Colonia c WHERE c.Municipio.IdMunicipio = :idMunicipio ORDER BY c.Nombre ASC")
    List<Colonia> findAllByMunicipio(@Param("idMunicipio") Integer idMunicipio);
}
