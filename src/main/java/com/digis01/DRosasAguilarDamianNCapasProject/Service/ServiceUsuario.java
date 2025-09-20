package com.digis01.DRosasAguilarDamianNCapasProject.Service;

import com.digis01.DRosasAguilarDamianNCapasProject.DAO.IRepositoryUsuario;
import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Direccion;
import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Result;
import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Rol;
import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Usuario;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

@Service
public class ServiceUsuario {

    @Autowired
    private IRepositoryUsuario iRepositoryUsuario;

    public Result GetAlll() {
        Result result = new Result();
        try {
            result.correct = true;
            result.object = iRepositoryUsuario.findAll();
            result.status = 200;

        } catch (Exception ex) {
            result.ex = ex;
            result.errorMessage = ex.getLocalizedMessage();
            result.correct = false;
            result.status = 500;
        }
        return result;
    }

    public Result GetById(int id) {
        Result result = new Result();
        try {
            Optional<Usuario> usuario = iRepositoryUsuario.findById(id);
            if (usuario.isPresent()) {
                result.object = usuario.get();
                result.correct = true;
                result.status = 200;
            } else {
                result.correct = false;
                result.errorMessage = "Usuario con id " + id + " no encontrado.";
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

    public Result DireccionesByIdUsuario(int id) {
        Result result = new Result();

        try {
            Optional<Usuario> usuario = iRepositoryUsuario.findById(id);
            
            if (usuario.isPresent()) {
                result.object = usuario.get();
                result.correct = true;
                result.status = 200;
            } else {
                result.correct = false;
                result.errorMessage = "Usuario con id " + id + " no encontrado.";
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
    
public Result Add(Usuario usuario) {
    Result result = new Result();
    try {
        // si tienes @OneToMany direcciones: asegúrate de setear la relación inversa
        if (usuario.getDirecciones() != null) {
            usuario.getDirecciones().forEach(d -> d.setUsuario(usuario));
        }

        Usuario usuariobd = iRepositoryUsuario.save(usuario);
        result.correct = true;
        result.status = 201; // Created
    } catch (Exception ex) {
        result.ex = ex;
        result.errorMessage = ex.getLocalizedMessage();
        result.correct = false;
        result.status = 500;
    }
    return result;
}



public Result Update(int id, Usuario incoming) {
    Result result = new Result();
    try {
        // 1) Buscar existente
        Optional<Usuario> opt = iRepositoryUsuario.findById(id);
        if (opt.isEmpty()) {
            result.correct = false;
            result.status = 404;
            result.errorMessage = "Usuario con id " + id + " no encontrado.";
            return result;
        }
        Usuario existing = opt.get();

        // 2) Rol  si vino en el body
       if (incoming.getRol() != null) {
    int idRol = incoming.getRol().getIdRol(); 
    if (idRol > 0) {                        
        Rol r = new Rol();
        r.setIdRol(idRol);
        existing.setRol(r);
    }
}

        existing.setNombre(incoming.getNombre());
        existing.setApellidopaterno(incoming.getApellidopaterno()); // <-- corrige nombre del setter
        existing.setApellidomaterno(incoming.getApellidomaterno());
        existing.setSexo(incoming.getSexo());
        existing.setCurp(incoming.getCurp());
        existing.setFechaNacimiento(incoming.getFechaNacimiento());
        existing.setUsername(incoming.getUsername());
        existing.setEmail(incoming.getEmail());
        existing.setPassword(incoming.getPassword());
        existing.setTelefono(incoming.getTelefono());
        existing.setCelular(incoming.getCelular());
        existing.setStatus(incoming.getStatus());
        existing.setFechaBaja(incoming.getFechaBaja());
        existing.setUsuarioBaja(incoming.getUsuarioBaja());
        existing.setImagen(incoming.getImagen());

        

        // 5) Guardar
        Usuario usActualizado = iRepositoryUsuario.save(existing);

        result.object = usActualizado;
        result.correct = true;
        result.status = 200;

    }  catch (Exception ex) {
        result.ex = ex;
        result.errorMessage = ex.getLocalizedMessage();
        result.correct = false;
        result.status = 500;
    }
    return result;
}



    
    public Result Delete(int id) {
        Result result = new Result();
        try {
            boolean exists = iRepositoryUsuario.existsById(id);
            if (!exists) {
                result.correct = false;
                result.errorMessage = "Usuario con id " + id + " no encontrado.";
                result.status = 404;
                return result;
            }

            iRepositoryUsuario.deleteById(id);
            result.correct = true;
            result.status = 200;
            result.object = null; // opcional: puedes devolver el id borrado
        } catch (Exception ex) {
            result.ex = ex;
            result.errorMessage = ex.getLocalizedMessage();
            result.correct = false;
            result.status = 500;
        }
        return result;
    }
    
    public Result SetActivo(int id, boolean activo, String usuarioBaja) {
    Result result = new Result();
    try {
        Optional<Usuario> usuario = iRepositoryUsuario.findById(id);

        if (!usuario.isPresent()) {
            result.correct = false;
            result.status = 404;
            result.errorMessage = "Usuario con id " + id + " no encontrado.";
            return result;
        }

        Usuario u = usuario.get();

        if (activo) {
            // Reactivar
            u.setStatus(1);
            u.setFechaBaja(null);
            u.setUsuarioBaja(null);
        } else {
            // Dar de baja (lógica)
            u.setStatus(0);
            u.setFechaBaja(LocalDateTime.now()); // cambia a LocalDate si tu columna es DATE sin tiempo
            u.setUsuarioBaja(
                (usuarioBaja == null || usuarioBaja.isBlank()) ? "system" : usuarioBaja
            );
        }

        iRepositoryUsuario.save(u);

        result.correct = true;
        result.status = 200;
        result.object = java.util.Map.of(
            "idUsuario", u.getIdUsuario(),
            "activo", activo,
            "status", u.getStatus(),
            "fechaBaja", u.getFechaBaja(),
            "usuarioBaja", u.getUsuarioBaja()
        );

    } catch (Exception ex) {
        result.ex = ex;
        result.errorMessage = ex.getLocalizedMessage();
        result.correct = false;
        result.status = 500;
    }
    return result;
}
    

}
