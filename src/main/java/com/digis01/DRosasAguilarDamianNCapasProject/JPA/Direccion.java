package com.digis01.DRosasAguilarDamianNCapasProject.JPA;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.Fetch;

@Entity
@Table(name = "DIRECCION")
public class Direccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "iddireccion")
    private int IdDireccion;

    @Column(name = "calle", nullable = false)
    private String Calle;
    @Column(name = "numerointerior")
    private String NumeroInterior;

    @Column(name = "numeroexterior", nullable = false)
    private String NumeroExterior;

    @ManyToOne()
    @JoinColumn(name = "idcolonia", nullable = false)
    public Colonia Colonia;

    @ManyToOne()
    @JsonIgnore
    @JoinColumn(name = "idusuario", nullable = false)
    public Usuario usuario;
    
    

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Colonia getColonia() {
        return Colonia;
    }

    public void setColonia(Colonia Colonia) {
        this.Colonia = Colonia;
    }

    public int getIdDireccion() {
        return IdDireccion;
    }

    public Direccion() {
    }


    public Direccion(int IdDireccion, String Calle, String NumeroExterior, String NumeroInterior) {
        this.IdDireccion = IdDireccion;
        this.Calle = Calle;
        this.NumeroExterior = NumeroExterior;
        this.NumeroInterior = NumeroInterior;
    }

    public void setIdDireccion(int IdDireccion) {
        this.IdDireccion = IdDireccion;
    }

    public String getCalle() {
        return Calle;
    }

    public void setCalle(String Calle) {
        this.Calle = Calle;
    }

    public String getNumeroInterior() {
        return NumeroInterior;
    }

    public void setNumeroInterior(String NumeroInterior) {
        this.NumeroInterior = NumeroInterior;
    }

    public String getNumeroExterior() {
        return NumeroExterior;
    }

    public void setNumeroExterior(String NumeroExterior) {
        this.NumeroExterior = NumeroExterior;
    }

 

}
