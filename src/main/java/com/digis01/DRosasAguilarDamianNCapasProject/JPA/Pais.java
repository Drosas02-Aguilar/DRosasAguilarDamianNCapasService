package com.digis01.DRosasAguilarDamianNCapasProject.JPA;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
 @Table(name = "PAIS")
public class Pais {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)   
    @Column(name = "idpais")
    private int IdPais;

    @Column(name = "nombre", nullable = false)
    private String Nombre;

    public Pais() {
    }
    
    
/*   public Pais(com.digis01.DRosasAguilarDamianNCapasProject.ML.Pais pML) {
    if (pML == null) return;
    this.IdPais = pML.getIdPais();             // si vas a INSERT, puedes omitir
    this.Nombre = pML.getNombre();
}
*/
    public Pais(int IdPais, String Nombre) {
        this.IdPais = IdPais;
        this.Nombre = Nombre;
    }

    public int getIdPais() {
        return IdPais;
    }

    public void setIdPais(int IdPais) {
        this.IdPais = IdPais;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String Nombre) {
        this.Nombre = Nombre;
    }

}
