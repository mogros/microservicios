package com.microservicios.app.respuestas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservicios.app.respuestas.models.controllers.RespuestaController;
import com.microservicios.app.respuestas.models.entity.Respuesta;
import com.microservicios.app.respuestas.models.services.RespuestaService;
import com.microservicios.commons.alumnos.models.entity.Alumno;
import com.microservicios.commons.examenesmodels.entity.Examen;
import com.microservicios.commons.examenesmodels.entity.Pregunta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RespuestaController.class)
@DisplayName("RespuestaController — endpoints HTTP")
class RespuestaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RespuestaService service;

    private Respuesta respuestaGuardada;

    @BeforeEach
    void setUp() {
        Alumno alumno = new Alumno();
        alumno.setId(5L);

        Examen examen = new Examen();
        examen.setId(10L);

        Pregunta pregunta = new Pregunta();
        pregunta.setId(1L);
        pregunta.setTexto("¿Qué es Java?");
        pregunta.setExamen(examen);

        respuestaGuardada = new Respuesta();
        respuestaGuardada.setId("mongo-abc");
        respuestaGuardada.setAlumno(alumno);
        respuestaGuardada.setAlumnoId(5L);
        respuestaGuardada.setPregunta(pregunta);
        respuestaGuardada.setPreguntaId(1L);
        respuestaGuardada.setTexto("Lenguaje de programación");
        respuestaGuardada.setNumeroIntento(1);
        respuestaGuardada.setFechaRespuesta(LocalDateTime.now());
    }

    @Test
    @WithMockUser
    @DisplayName("POST / — guarda respuestas y retorna 201")
    void crear_retornaCreated() throws Exception {
        when(service.calcularSiguienteIntento(5L, 10L)).thenReturn(1);
        when(service.saveAll(any())).thenReturn(List.of(respuestaGuardada));

        String body = objectMapper.writeValueAsString(List.of(respuestaGuardada));

        mockMvc.perform(post("/")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].numeroIntento").value(1))
                .andExpect(jsonPath("$[0].texto").value("Lenguaje de programación"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /alumno/{id}/examen/{id}/intentos — retorna conteo")
    void contarIntentos_retornaJson() throws Exception {
        when(service.contarIntentos(5L, 10L)).thenReturn(2);

        mockMvc.perform(get("/alumno/5/examen/10/intentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalIntentos").value(2))
                .andExpect(jsonPath("$.siguienteIntento").value(3));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /alumno/{id}/examen/{id}/intento/{n} — retorna respuestas del intento")
    void obtenerPorIntento_retornaRespuestas() throws Exception {
        when(service.findRespuestaByAlumnoByExamenByIntento(5L, 10L, 1))
                .thenReturn(List.of(respuestaGuardada));

        mockMvc.perform(get("/alumno/5/examen/10/intento/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].numeroIntento").value(1));
    }
}
