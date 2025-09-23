package com.digis01.DRosasAguilarDamianNCapasProject.DAO;

import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Estado;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface IRepositoryEstado extends JpaRepository<Estado, Integer> {

    // Tus campos son e.Pais.IdPais y e.Nombre (respetando mayúsculas)
    @Query("SELECT e FROM Estado e WHERE e.Pais.IdPais = :idPais ORDER BY e.Nombre ASC")
    List<Estado> findAllByPais(@Param("idPais") Integer idPais);
}
