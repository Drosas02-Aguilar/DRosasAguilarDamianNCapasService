/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.digis01.DRosasAguilarDamianNCapasProject.DAO;

import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Direccion;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author digis
 */
public interface IRepositoryDireccion extends JpaRepository<Direccion, Integer> {
    
}
