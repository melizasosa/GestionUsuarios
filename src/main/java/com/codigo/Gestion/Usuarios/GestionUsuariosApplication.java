package com.codigo.Gestion.Usuarios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@EnableFeignClients // Añade esta anotación
public class GestionUsuariosApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionUsuariosApplication.class, args);
	}

}
