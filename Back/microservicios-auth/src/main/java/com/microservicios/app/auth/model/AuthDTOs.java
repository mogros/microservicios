package com.microservicios.app.auth.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Set;

public class AuthDTOs {

    public static class LoginRequest {
        @NotBlank(message = "El username es obligatorio")
        private String username;
        @NotBlank(message = "La contrasena es obligatoria")
        private String password;
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class RegisterRequest {
        @NotBlank @Size(min = 3, max = 50)
        private String username;
        @NotBlank @Email
        private String email;
        @NotBlank @Size(min = 8, max = 40)
        private String password;
        private Set<String> roles;
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public Set<String> getRoles() { return roles; }
        public void setRoles(Set<String> roles) { this.roles = roles; }
    }

    public static class JwtResponse {
        private String token;
        private final String type = "Bearer";
        private Long id;
        private String username;
        private String email;
        private List<String> roles;
        public JwtResponse(String token, Long id, String username, String email, List<String> roles) {
            this.token = token; this.id = id; this.username = username;
            this.email = email; this.roles = roles;
        }
        public String getToken() { return token; }
        public String getType() { return type; }
        public Long getId() { return id; }
        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public List<String> getRoles() { return roles; }
    }

    public static class MessageResponse {
        private String message;
        public MessageResponse(String message) { this.message = message; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
