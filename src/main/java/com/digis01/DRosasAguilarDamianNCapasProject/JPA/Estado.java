package com.digis01.DRosasAguilarDamianNCapasProject.JPA;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "ESTADO")
public class Estado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idestado")
    private int IdEstado;

    @Column(name = "nombre", nullable = false)
    private String Nombre;

    @ManyToOne()
    @JoinColumn(name = "Idpais", nullable = false)
    public Pais Pais;

    public Pais getPais() {

        return Pais;
    }

    public Estado() {
    }
    
  
  

    public Estado(int IdEstado, String Nombre) {
        this.IdEstado = IdEstado;
        this.Nombre = Nombre;
    }

    public void setPais(Pais Pais) {
        this.Pais = Pais;
    }

    public int getIdEstado() {
        return IdEstado;
    }

    public void setIdEstado(int IdEstado) {
        this.IdEstado = IdEstado;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String Nombre) {
        this.Nombre = Nombre;

    }

}
