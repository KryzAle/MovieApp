/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Config;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 *
 * @author labctr
 */
public class Conexion {
    
    public DriverManagerDataSource Conectar(){
        
        DriverManagerDataSource dts =new DriverManagerDataSource();
        dts.setDriverClassName("com.mysql.jdbc.Driver");
        dts.setUrl("jdbc:mysql://localhost:3306/video");
        dts.setUsername("root");
        dts.setPassword("");
        return dts;
    }
    
}
