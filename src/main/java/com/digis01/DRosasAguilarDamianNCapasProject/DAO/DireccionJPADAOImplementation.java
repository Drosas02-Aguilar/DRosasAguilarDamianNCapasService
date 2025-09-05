package com.digis01.DRosasAguilarDamianNCapasProject.DAO;

import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Colonia;
import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Direccion;
import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Result;
import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Usuario;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DireccionJPADAOImplementation implements IDireccionJPADAO {

    @Autowired
    private EntityManager entityManager;

    @Transactional
    @Override
    public Result AddDireccion(int IdUsuario, Direccion direccion) {
        Result result = new Result();
        try {

            direccion.usuario = new Usuario();
          
            Usuario usario = entityManager.find(Usuario.class, IdUsuario);
            
            direccion.usuario.setIdUsuario(IdUsuario);
            entityManager.persist(direccion);
            
            result.object = direccion; 
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
    public Result GetByIdDireccion(int IdDireccion) {
        Result result = new Result();
        try {
            Direccion direccionJPA = entityManager.find(Direccion.class, IdDireccion);
            if (direccionJPA != null) {
                result.object = direccionJPA; 
                result.correct = true;
            } else {
                result.correct = false;
                result.errorMessage = "Dirección no encontrada con Id: " + IdDireccion;
            }
        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }
        return result;
    }

    @Transactional
    @Override
    public Result Update(Direccion direccion) {
        Result result = new Result();
        try {
            Direccion direccionBD = entityManager.find(Direccion.class, direccion.getIdDireccion());
            if (direccionBD == null) {
                result.correct = false;
                result.errorMessage = "No existe la dirección con Id " + direccion.getIdDireccion();
                return result;
            }

            direccion.setUsuario(direccionBD.getUsuario());

            if (direccion.getColonia() != null && direccion.getColonia().getIdColonia() > 0) {
                direccion.setColonia(
                    entityManager.getReference(Colonia.class, direccion.getColonia().getIdColonia())
                );
            } else {
                direccion.setColonia(direccionBD.getColonia());
            }

            entityManager.merge(direccion);
            entityManager.flush();

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
    public Result Delete(int IdDireccion) {
        Result result = new Result();
        try {
            Direccion direccionJPA = entityManager.find(Direccion.class, IdDireccion);
            if (direccionJPA == null) {
                result.correct = false;
                result.errorMessage = "Dirección no encontrada con Id: " + IdDireccion;
                return result;
            }
            entityManager.remove(direccionJPA);
            result.correct = true;
        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }
        return result;
    }
}
