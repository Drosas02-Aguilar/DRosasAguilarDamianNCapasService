package com.digis01.DRosasAguilarDamianNCapasProject.JPA;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Municipio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idmunicipio")
    private int IdMunicipio;

    @Column(name = "nombre", nullable = false)
    private String Nombre;

     @ManyToOne()
    @JoinColumn(name = "idestado", nullable = false)
    public Estado Estado;

    public Municipio() {
    }
    
   /* public Municipio(com.digis01.DRosasAguilarDamianNCapasProject.ML.Municipio mML) {
    if (mML == null) return;
    this.IdMunicipio = mML.getIdMunicipio();   // si vas a INSERT, puedes omitir
    this.Nombre      = mML.getNombre();

    if (mML.getEstado() != null) {
        // Variante completa:
        this.Estado = new Estado(mML.getEstado());

       
    }
}*/

    public Municipio(int IdMunicipio, String Nombre) {
        this.IdMunicipio = IdMunicipio;
        this.Nombre = Nombre;
    }

    public Estado getEstado() {
        return Estado;
    }

    public void setEstado(Estado Estado) {
        this.Estado = Estado;
    }

    public int getIdMunicipio() {
        return IdMunicipio;
    }

    public void setIdMunicipio(int IdMunicipio) {
        this.IdMunicipio = IdMunicipio;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String Nombre) {
        this.Nombre = Nombre;
    }

}
