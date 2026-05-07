package com.microservicios.app.auth.config;

import com.microservicios.app.auth.model.Rol;
import com.microservicios.app.auth.model.Usuario;
import com.microservicios.app.auth.service.RolRepository;
import com.microservicios.app.auth.service.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired private RolRepository rolRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initRoles();
        initAdminUser();
    }

    private void initRoles() {
        for (Rol.NombreRol nombre : Rol.NombreRol.values()) {
            if (rolRepository.findByNombre(nombre).isEmpty()) {
                rolRepository.save(new Rol(nombre));
                log.info("Rol creado: {}", nombre);
            }
        }
    }

    private void initAdminUser() {
        if (!usuarioRepository.existsByUsername("admin")) {
            Rol rolAdmin = rolRepository.findByNombre(Rol.NombreRol.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Rol ADMIN no encontrado"));
            Usuario admin = new Usuario(
                    "admin",
                    "admin@microservicios.com",
                    passwordEncoder.encode("Admin123!")
            );
            admin.setRoles(Set.of(rolAdmin));
            usuarioRepository.save(admin);
            log.info("Usuario admin creado. Cambiar password en produccion.");
        }
    }
}
