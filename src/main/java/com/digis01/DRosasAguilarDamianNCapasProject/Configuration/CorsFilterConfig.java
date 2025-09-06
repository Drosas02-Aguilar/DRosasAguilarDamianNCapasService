
package com.digis01.DRosasAguilarDamianNCapasProject.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

public class CorsFilterConfig {
    @Bean // terminar una configuración personalizada
    public CorsFilter corsFilter(){
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration  corsConfig = new CorsConfiguration();
        
        corsConfig.addAllowedOrigin("*");
        
        corsConfig.addAllowedMethod("GET");
        corsConfig.addAllowedMethod("POST");
        corsConfig.addAllowedMethod("PUT");
        corsConfig.addAllowedMethod("DELETE");
        corsConfig.addAllowedMethod("PATCH");
        
        corsConfig.addAllowedHeader("*");
        
        source.registerCorsConfiguration("/**", corsConfig);
        
        return new CorsFilter(source);
    }
}
