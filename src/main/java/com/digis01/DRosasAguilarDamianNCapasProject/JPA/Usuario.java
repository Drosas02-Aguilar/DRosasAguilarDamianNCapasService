package com.digis01.DRosasAguilarDamianNCapasProject.JPA;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.SQLDelete;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "USUARIO")
//@SQLDelete(sql = "UPDATE USUARIO SET STATUS = 0, FECHA_BAJA=SYSTIMESTAMP WHERE IDUSUARIO=?")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idusuario")
    private int IdUsuario;
    
    @Column(name = "username", nullable  = false)
    private String Username;
    
    @Column(name = "nombre", nullable = false)
    private String Nombre;
    
    @Column(name = "apellidopaterno")
    private String Apellidopaterno;
    
    @Column(name = "apellidomaterno")
    private String Apellidomaterno;
    
    @Column (name = "email", nullable = false)
    private String Email;
    
    @Column (name = "password", nullable = false)
    private String Password;
    
    @Column (name = "telefono", nullable = false)
    private String Telefono;
    
    @Column (name = "curp")
    private String Curp;
    
    @Column(name = "celular")
    private String Celular;
    
    @Column(name = "sexo", nullable = false)
    private String Sexo;
    
    @Column(name = "tiposangre")
    private String Tiposangre;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    @Column(name = "fechanacimiento")
    private Date FechaNacimiento;
    
    @ManyToOne()
    @JoinColumn(name = "Idrol", nullable = false)
    public Rol Rol;
    
@OneToMany(
        mappedBy = "usuario",         
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
public List<Direccion> direcciones = new ArrayList<>();
   
    @Lob 
    @Column(name = "imagen")
    private String Imagen;
    
    @Column(name = "status", nullable = false)
    private Integer Status = 1;
    
    @Column(name = "fecha_baja")
    private java.time.LocalDateTime FechaBaja;
    
    @Column(name = "usuario_baja")
    private String UsuarioBaja;
    
    public Usuario() {
        
    }
    

    public List<Direccion> getDirecciones() { return direcciones; }
    public void setDirecciones(List<Direccion> direcciones) { this.direcciones = direcciones; }

    public int getIdUsuario() { return IdUsuario; }
    public void setIdUsuario(int idUsuario) { IdUsuario = idUsuario; }

    public String getNombre() { return Nombre; }
    public void setNombre(String nombre) { Nombre = nombre; }

    public String getUsername() { return Username; }
    public void setUsername(String username) { Username = username; }

    public String getApellidopaterno() { return Apellidopaterno; }
    public void setApellidopaterno(String apellidopaterno) { Apellidopaterno = apellidopaterno; }

    public String getApellidomaterno() { return Apellidomaterno; }
    public void setApellidomaterno(String apellidomaterno) { Apellidomaterno = apellidomaterno; }

    public String getEmail() { return Email; }
    public void setEmail(String email) { Email = email; }

    public String getPassword() { return Password; }
    public void setPassword(String password) { Password = password; }

    public String getTelefono() { return Telefono; }
    public void setTelefono(String telefono) { Telefono = telefono; }

    public String getCurp() { return Curp; }
    public void setCurp(String curp) { Curp = curp; }

    public String getCelular() { return Celular; }
    public void setCelular(String celular) { Celular = celular; }

    public String getSexo() { return Sexo; }
    public void setSexo(String sexo) { Sexo = sexo; }

    public String getTiposangre() { return Tiposangre; }
    public void setTiposangre(String tiposangre) { Tiposangre = tiposangre; }

    public Date getFechaNacimiento() { return FechaNacimiento; }
    public void setFechaNacimiento(Date FechaNacimiento) { this.FechaNacimiento = FechaNacimiento; }

    public String getImagen() {
        return Imagen;
    }

    public void setImagen(String Imagen) {
        this.Imagen = Imagen;
    }


    public Integer getStatus() {
        return Status;
    }

    public void setStatus(Integer Status) {
        this.Status = Status;
    }

    public LocalDateTime getFechaBaja() {
        return FechaBaja;
    }

    public void setFechaBaja(LocalDateTime FechaBaja) {
        this.FechaBaja = FechaBaja;
    }

    public String getUsuarioBaja() {
        return UsuarioBaja;
    }

    public void setUsuarioBaja(String UsuarioBaja) {
        this.UsuarioBaja = UsuarioBaja;
    }

    public Usuario(LocalDateTime FechaBaja, String UsuarioBaja) {
        this.FechaBaja = FechaBaja;
        this.UsuarioBaja = UsuarioBaja;
    }



}