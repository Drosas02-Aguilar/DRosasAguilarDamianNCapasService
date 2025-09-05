package com.digis01.DRosasAguilarDamianNCapasProject.DAO;

import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Estado;
import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Result;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class EstadoJPADAOImplementation implements IEstadoJPADAO {

    @Autowired

    private EntityManager entityManager;

    @Transactional
    @Override
    public Result EstadoByidPais(int IdPais) {
        Result result = new Result();
        try {

            TypedQuery<com.digis01.DRosasAguilarDamianNCapasProject.JPA.Estado> queryEstado
                    = entityManager.createQuery("FROM Estado e WHERE e.Pais.IdPais = :idPais ORDER BY e.Nombre",
                            com.digis01.DRosasAguilarDamianNCapasProject.JPA.Estado.class).setParameter("idPais", IdPais);
            result.object = queryEstado.getResultList();

            result.correct = true;

        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }
        return result;
    }

}
