package com.digis01.DRosasAguilarDamianNCapasProject.RestController;


import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("demo")
public class DemoRestController {
    
    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping
    public Usuario index(){
    
        return new Usuario();
    
    }
    
    @GetMapping("/suma")
        public String Suma(@RequestParam int numeroA, @RequestParam int numeroB){
            return "La suma es " + (numeroA + numeroB);
        }
        
        @PostMapping("Saludo")
        public String Saludo(@RequestBody Usuario usuario){
            return "Hola " + usuario.getNombre();
        }
        
            @PostMapping("/suma-arreglo")
            public Map<String,Object> sumaArreglo(@RequestBody List<Integer>numeros){
            
                int suma = numeros.stream().mapToInt(Integer::intValue).sum();
                Map<String, Object> resp = new LinkedHashMap<>();
                    resp.put("numeros", numeros);
                    resp.put("suma", suma);
                    return resp;
            }

                @PostMapping("/saludos")
                public ResponseEntity<Map<String,Object>> saludos(@RequestBody Usuario usuario){
                Map<String,Object> resp = new LinkedHashMap<>();
                resp.put("mensaje", "hola " + usuario.getNombre());
                resp.put("usuario",usuario);
                return ResponseEntity.ok(resp);
                
                }

                
                public static class SaludoRequest {
        private Integer idUsuario;
        public Integer getIdUsuario() { return idUsuario; }
        public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }
    }

                /*
    @PostMapping("/saludoss")
    public ResponseEntity<Map<String, Object>> saludo(@RequestBody SaludoRequest req) {

        

        try {
            Usuario u = entityManager.createQuery(
                    "SELECT u FROM Usuario u " +
                    "LEFT JOIN FETCH u.Rol r " +
                    "LEFT JOIN FETCH u.direcciones d " +
                    "WHERE u.IdUsuario = :id", Usuario.class)
                .setParameter("id", req.getIdUsuario())
                .getSingleResult();

            com.digis01.DRosasAguilarDamianNCapasProject.JPA.Usuario usuarioML =
                    new com.digis01.DRosasAguilarDamianNCapasProject.JPA.Usuario(u);

            Map<String, Object> resp = new LinkedHashMap<>();
            resp.put("mensaje", "¡Hola " + u.getNombre() + "!");
            resp.put("usuario", usuarioML); // devuelve TODA su info (según tu constructor ML)
            return ResponseEntity.ok(resp);

        } catch (NoResultException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No existe el usuario con id " + req.getIdUsuario());
        }
    }
                

   @PatchMapping("/nombres/{posicion}")
    public Map<String, Object> actualizarNombre(
            @PathVariable int posicion,
            @RequestParam String nombre,
            @RequestBody List<String> nombres) {

        if (posicion < 0 || posicion >= nombres.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "posición fuera de rango");
        }

        List<String> antes = new ArrayList<>(nombres);
        nombres.set(posicion, nombre);

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("antes", antes);
        resp.put("despues", nombres);
        return resp;
    }
    
    
    */
}
