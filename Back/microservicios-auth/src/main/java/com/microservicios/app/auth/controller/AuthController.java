package com.microservicios.app.auth.controller;

import com.microservicios.app.auth.model.AuthDTOs.*;
import com.microservicios.app.auth.model.Rol;
import com.microservicios.app.auth.model.Usuario;
import com.microservicios.app.auth.service.JwtUtils;
import com.microservicios.app.auth.service.RolRepository;
import com.microservicios.app.auth.service.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private RolRepository rolRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Usuario usuario = usuarioRepository.findByUsername(userDetails.getUsername()).orElseThrow();

        return ResponseEntity.ok(new JwtResponse(jwt, usuario.getId(),
                userDetails.getUsername(), usuario.getEmail(), roles));
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registro(@Valid @RequestBody RegisterRequest request) {
        if (usuarioRepository.existsByUsername(request.getUsername()))
            return ResponseEntity.badRequest().body(new MessageResponse("Error: username ya en uso"));

        if (usuarioRepository.existsByEmail(request.getEmail()))
            return ResponseEntity.badRequest().body(new MessageResponse("Error: email ya registrado"));

        Usuario usuario = new Usuario(request.getUsername(), request.getEmail(),
                passwordEncoder.encode(request.getPassword()));

        Set<String> rolesReq = request.getRoles();
        Set<Rol> roles = new HashSet<>();

        if (rolesReq == null || rolesReq.isEmpty()) {
            roles.add(rolRepository.findByNombre(Rol.NombreRol.ROLE_ALUMNO)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado")));
        } else {
            rolesReq.forEach(r -> {
                switch (r.toUpperCase()) {
                    case "ADMIN", "ROLE_ADMIN" ->
                        roles.add(rolRepository.findByNombre(Rol.NombreRol.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Rol no encontrado")));
                    case "DOCENTE", "ROLE_DOCENTE" ->
                        roles.add(rolRepository.findByNombre(Rol.NombreRol.ROLE_DOCENTE)
                                .orElseThrow(() -> new RuntimeException("Rol no encontrado")));
                    default ->
                        roles.add(rolRepository.findByNombre(Rol.NombreRol.ROLE_ALUMNO)
                                .orElseThrow(() -> new RuntimeException("Rol no encontrado")));
                }
            });
        }

        usuario.setRoles(roles);
        usuarioRepository.save(usuario);
        return ResponseEntity.ok(new MessageResponse("Usuario '" + request.getUsername() + "' registrado correctamente"));
    }
}
