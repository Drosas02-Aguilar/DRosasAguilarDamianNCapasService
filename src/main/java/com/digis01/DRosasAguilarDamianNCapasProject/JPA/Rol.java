package com.digis01.DRosasAguilarDamianNCapasProject.JPA;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ROL")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idrol")
    private int IdRol;
    
    @Column(name = "nombre", nullable = false)
    private String Nombre;

    
    
    public Rol() {
    }

  /*  public Rol(com.digis01.DRosasAguilarDamianNCapasProject.ML.Rol rolML) {
    if (rolML != null) {
        this.IdRol = rolML.getIdRol();   
        this.Nombre = rolML.getNombre();
    }
}*/
    
    public Rol(int IdRol, String Nombre) {
        this.IdRol = IdRol;
        this.Nombre = Nombre;
    }

    public int getIdRol() {
        return IdRol;
    }

    public void setIdRol(int IdRol) {
        this.IdRol = IdRol;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String Nombre) {
        this.Nombre = Nombre;
    }

}
