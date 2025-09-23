package com.digis01.DRosasAguilarDamianNCapasProject.Service;

import com.digis01.DRosasAguilarDamianNCapasProject.DAO.IRepositoryColonia;
import com.digis01.DRosasAguilarDamianNCapasProject.DAO.IRepositoryDireccion;
import com.digis01.DRosasAguilarDamianNCapasProject.DAO.IRepositoryUsuario;
import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Colonia;
import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Direccion;
import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Result;
import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Usuario;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class ServiceDIreccion {

    @Autowired
    private IRepositoryDireccion iRepositoryDireccion;
    @Autowired
    private IRepositoryUsuario iRepositoryUsuario;

    @Autowired
    private IRepositoryColonia iRepositoryColonia; // <--- ahora sí existe

    public Result GetByIdDireccion(int id) {
        Result result = new Result();

        try {

            Optional<Direccion> direccion = iRepositoryDireccion.findById(id);
            if (direccion.isPresent()) {

                result.object = direccion.get();
                result.correct = true;
                result.status = 200;
            } else {
                result.correct = false;
                result.errorMessage = "Direccion con id" + id + "no encontrado";
                result.status = 404;

            }
        } catch (Exception ex) {
            result.ex = ex;
            result.errorMessage = ex.getLocalizedMessage();
            result.correct = false;
            result.status = 500;
        }
        return result;
    }

  public Result AddDireccion(int idUsuario, Direccion direccion) {
    Result result = new Result();
    try {
        // 1) Buscar usuario
        Optional<Usuario> usuario = iRepositoryUsuario.findById(idUsuario);
        if (!usuario.isPresent()) {
            result.correct = false;
            result.status = 404;
            result.errorMessage = "Usuario con id " + idUsuario + " no encontrado.";
            return result;
        }

        if (direccion.getColonia() != null) {
            Integer idColonia = direccion.getColonia().getIdColonia();
            if (idColonia == null || !iRepositoryColonia.existsById(idColonia)) {
                result.correct = false;
                result.status = 404;
                result.errorMessage = "Colonia con id " + idColonia + " no existe.";
                return result;
            }
        }

        // 3) Relación inversa
        direccion.setUsuario(usuario.get());

        // 4) Guardar 
        Direccion creada = iRepositoryDireccion.save(direccion);

        result.object = creada;
        result.correct = true;
        result.status = 201; // Created
        return result;

    } catch (org.springframework.dao.DataIntegrityViolationException dive) {
        result.correct = false;
        result.status = 409; // o 400
        result.errorMessage = "Violación de integridad (FK/NN/UK): " + dive.getMostSpecificCause().getMessage();
        return result;
    } catch (Exception ex) {
        result.ex = ex;
        result.errorMessage = ex.getLocalizedMessage();
        result.correct = false;
        result.status = 500;
        return result;
    }

}
  
  public Result Update(int idDireccion, Direccion direccion) {
    Result result = new Result();
    try {
        Optional<Direccion> opt = iRepositoryDireccion.findById(idDireccion);
        if (!opt.isPresent()) {
            result.correct = false;
            result.status = 404;
            result.errorMessage = "Dirección con id " + idDireccion + " no encontrada.";
            return result;
        }
        Direccion existing = opt.get();

        // --- No permitir cambiar el usuario dueño ---
        if (direccion.getUsuario() != null) {
            // OJO: si getIdUsuario() es int (primitivo), no es null nunca
            int incomingUserId = direccion.getUsuario().getIdUsuario(); // int
            if (incomingUserId > 0) { // solo si mandaron algo (>0 como “valor válido”)
                int currentUserId = existing.getUsuario().getIdUsuario(); // int
                if (incomingUserId != currentUserId) {
                    result.correct = false;
                    result.status = 400;
                    result.errorMessage = "No se permite cambiar el usuario de la dirección.";
                    return result;
                }
            }
        }

        // --- Reasignar colonia si viene (validación opcional de existencia) ---
        if (direccion.getColonia() != null) {
            int idColonia = direccion.getColonia().getIdColonia(); // int
            if (idColonia > 0) {
                 if (!iRepositoryColonia.existsById(idColonia)) {
                     result.correct = false;
                     result.status = 404;
                     result.errorMessage = "Colonia con id " + idColonia + " no existe.";
                     return result;
                 }
                Colonia c = new Colonia();
                c.setIdColonia(idColonia);
                existing.setColonia(c);
            }
        }

        // --- Actualizar solo campos propios de Dirección ---
        if (direccion.getCalle() != null)           existing.setCalle(direccion.getCalle());
        if (direccion.getNumeroExterior() != null)  existing.setNumeroExterior(direccion.getNumeroExterior());
        if (direccion.getNumeroInterior() != null)  existing.setNumeroInterior(direccion.getNumeroInterior());
       

        Direccion actualizada = iRepositoryDireccion.save(existing);

        result.object = actualizada;
        result.correct = true;
        result.status = 200;
        return result;

    } catch (org.springframework.dao.DataIntegrityViolationException dive) {
        result.correct = false;
        result.status = 409; // o 400
        result.errorMessage = "Violación de integridad (FK/NN/UK): " + dive.getMostSpecificCause().getMessage();
        return result;
    } catch (Exception ex) {
        result.ex = ex;
        result.errorMessage = ex.getLocalizedMessage();
        result.correct = false;
        result.status = 500;
        return result;
    }
}
  
  public Result Delete(int idDireccion) {
    Result result = new Result();
    try {
        var opt = iRepositoryDireccion.findById(idDireccion);
        if (!opt.isPresent()) {
            result.correct = false;
            result.status = 404;
            result.errorMessage = "Dirección con id " + idDireccion + " no encontrada.";
            return result;
        }

        var dir = opt.get();

       
        iRepositoryDireccion.delete(dir);

        result.correct = true;
        result.status = 200; // si prefieres, puedes devolver 204 sin body
        result.object = java.util.Map.of("idDireccion", idDireccion, "deleted", true);
        return result;

    } catch (DataIntegrityViolationException dive) {
        result.correct = false;
        result.status = 409;
        result.errorMessage = "No se puede eliminar la dirección por restricción de integridad: " 
                            + dive.getMostSpecificCause().getMessage();
        return result;

    } catch (Exception ex) {
        result.ex = ex;
        result.errorMessage = ex.getLocalizedMessage();
        result.correct = false;
        result.status = 500;
        return result;
    }

}
  
}