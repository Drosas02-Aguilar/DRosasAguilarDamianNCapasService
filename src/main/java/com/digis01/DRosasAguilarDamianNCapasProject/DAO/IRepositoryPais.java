package com.digis01.DRosasAguilarDamianNCapasProject.DAO;

import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Pais;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface IRepositoryPais extends JpaRepository<Pais, Integer> {

    // Usa el NOMBRE DE CAMPO exacto en tu entidad: "Nombre" (N mayúscula)
    @Query("SELECT p FROM Pais p ORDER BY p.Nombre ASC")
    List<Pais> findAllOrderByNombre();
}
