/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.digis01.DRosasAguilarDamianNCapasProject.DAO;

import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Result;

/**
 *
 * @author digis
 */
public interface IUsuarioJPADAO {

    Result GetAll();

    Result Add(com.digis01.DRosasAguilarDamianNCapasProject.JPA.Usuario usuario);

    Result Update(com.digis01.DRosasAguilarDamianNCapasProject.JPA.Usuario usuario);

    Result GetByIdUsuario(int IdUsuario );

    Result DireccionesByIdUsuario(int IdUsuario);

    Result Delete(int IdUsuario);

   Result SetActivo(int idUsuario, boolean activo, String usuarioBaja);

    default Result BajaLogica(int idUsuario, String usuarioBaja) {
        return SetActivo(idUsuario, false, usuarioBaja);
    }
    default Result Reactivar(int idUsuario) {
        return SetActivo(idUsuario, true, null);
    }

}

