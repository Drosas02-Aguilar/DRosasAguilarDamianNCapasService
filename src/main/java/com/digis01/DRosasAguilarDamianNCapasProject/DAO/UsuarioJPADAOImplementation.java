package com.digis01.DRosasAguilarDamianNCapasProject.DAO;

import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Result;
import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Usuario;
import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Direccion;
import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Rol;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


@Repository
public class UsuarioJPADAOImplementation implements IUsuarioJPADAO {

    @Autowired
    private EntityManager entityManager;

    @Override
    public Result GetAll() {
        Result result = new Result();

        try {
            TypedQuery<Usuario> queryUsuario = entityManager.createQuery(
                    "FROM Usuario ORDER BY  IdUsuario", Usuario.class);
           result.object = queryUsuario.getResultList();

            result.correct = true;


        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getMessage();
            result.ex = ex;
        }

        return result;
    }

    @Transactional
    @Override
    public Result Add(com.digis01.DRosasAguilarDamianNCapasProject.JPA.Usuario usuarioJPA) {

        Result result = new Result();

        try {

            entityManager.persist(usuarioJPA);
            result.object = usuarioJPA;
            result.correct = true;

        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }
        return result;
    }

    @Transactional
    @Override
    public Result Update(com.digis01.DRosasAguilarDamianNCapasProject.JPA.Usuario usuarioJPA) {

        Result result = new Result();

        try {
        Usuario usuarioBD = entityManager.find(Usuario.class, usuarioJPA.getIdUsuario());

        if (usuarioBD == null) {
                result.correct = false;
                result.errorMessage = "Usuario no encontrado con Id: " + usuarioJPA.getIdUsuario();
                return result;
            }
         if (usuarioBD.direcciones != null) {
            usuarioJPA.direcciones = usuarioBD.direcciones;
        }

        entityManager.merge(usuarioJPA);


    ;

        result.correct = true;
        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }
        return result;

    }

    @Transactional
@Override
public Result GetByIdUsuario(int IdUsuario) {
    Result result = new Result();

    try {
        Usuario usuarioJPA = entityManager.find(Usuario.class, IdUsuario);

        if (usuarioJPA != null) {
            result.object = usuarioJPA;  
            result.correct = true;
        } else {
            result.correct = false;
            result.errorMessage = "Usuario no encontrado con Id: " + IdUsuario;
        }

    } catch (Exception ex) {
        result.correct = false;
        result.errorMessage = ex.getMessage();
        result.ex = ex;
    }
    return result;
}


    
    
    @Transactional
    @Override
    public Result DireccionesByIdUsuario(int IdUsuario) {
        Result result = new Result();

        try {

            Usuario usuario = entityManager.find(Usuario.class, IdUsuario);

            result.object = usuario;
            result.correct = true;
        

        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getMessage();
            result.ex = ex;
        }

        return result;
    }

    
    @Transactional
    @Override
    public Result Delete(int IdUsuario) {
        Result result = new Result();

        try {

           Usuario usuarioJPA = entityManager.find(Usuario.class, IdUsuario);
            if (usuarioJPA == null) {
                result.correct = false;
                result.errorMessage = "Usuario no encontrado con Id: " + IdUsuario;
                return result;
            }
            entityManager.remove(usuarioJPA);
            result.correct = true;


        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }

        return result;
    }
      
    
    
    @Transactional
    @Override
    public Result SetActivo(int idUsuario, boolean activo, String usuarioBaja) {
        Result result = new Result();
        try {
            entityManager.createQuery(
                "UPDATE Usuario u " +
                "SET u.Status = :a, " +
                "    u.FechaBaja = CASE WHEN :a=0 THEN CURRENT_TIMESTAMP ELSE NULL END, " +
                "    u.UsuarioBaja = CASE WHEN :a=0 THEN :ub ELSE NULL END " +
                "WHERE u.IdUsuario = :id")
            .setParameter("a", activo ? 1 : 0)
            .setParameter("ub", usuarioBaja)
            .setParameter("id", idUsuario)
            .executeUpdate();

    
            result.correct = true;
        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getMessage();
            result.ex = ex;
        }
        return result;
    } 
    
}
