package com.microservicios.app.respuestas.service;

import com.microservicios.app.respuestas.models.entity.Respuesta;
import com.microservicios.app.respuestas.models.repository.RespuestaRepository;
import com.microservicios.app.respuestas.models.services.RespuestaServiceImpl;
import com.microservicios.commons.examenesmodels.entity.Examen;
import com.microservicios.commons.examenesmodels.entity.Pregunta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RespuestaService — lógica de intentos")
class RespuestaServiceTest {

    @Mock
    private RespuestaRepository repository;

    @InjectMocks
    private RespuestaServiceImpl service;

    private Respuesta respuesta1;
    private Respuesta respuesta2;

    @BeforeEach
    void setUp() {
        Examen examen = new Examen();
        examen.setId(10L);

        Pregunta pregunta = new Pregunta();
        pregunta.setId(1L);
        pregunta.setTexto("¿Qué es Java?");
        pregunta.setExamen(examen);

        respuesta1 = new Respuesta();
        respuesta1.setId("mongo-id-1");
        respuesta1.setAlumnoId(5L);
        respuesta1.setPreguntaId(1L);
        respuesta1.setPregunta(pregunta);
        respuesta1.setTexto("Un lenguaje de programación");
        respuesta1.setNumeroIntento(1);
        respuesta1.setFechaRespuesta(LocalDateTime.now().minusDays(1));

        respuesta2 = new Respuesta();
        respuesta2.setId("mongo-id-2");
        respuesta2.setAlumnoId(5L);
        respuesta2.setPreguntaId(1L);
        respuesta2.setPregunta(pregunta);
        respuesta2.setTexto("Lenguaje orientado a objetos multiplataforma");
        respuesta2.setNumeroIntento(2);
        respuesta2.setFechaRespuesta(LocalDateTime.now());
    }

    @Test
    @DisplayName("contarIntentos devuelve 0 si el alumno nunca respondió")
    void contarIntentos_sinRespuestas_retornaCero() {
        when(repository.findAllByAlumnoAndExamen(5L, 10L)).thenReturn(List.of());

        int resultado = service.contarIntentos(5L, 10L);

        assertThat(resultado).isZero();
    }

    @Test
    @DisplayName("contarIntentos devuelve el máximo número de intento")
    void contarIntentos_conDosIntentos_retornaDos() {
        when(repository.findAllByAlumnoAndExamen(5L, 10L))
                .thenReturn(List.of(respuesta1, respuesta2));

        int resultado = service.contarIntentos(5L, 10L);

        assertThat(resultado).isEqualTo(2);
    }

    @Test
    @DisplayName("calcularSiguienteIntento retorna 1 si no hay intentos previos")
    void calcularSiguienteIntento_sinHistorial_retornaUno() {
        when(repository.findAllByAlumnoAndExamen(anyLong(), anyLong())).thenReturn(List.of());

        int siguiente = service.calcularSiguienteIntento(5L, 10L);

        assertThat(siguiente).isEqualTo(1);
    }

    @Test
    @DisplayName("calcularSiguienteIntento retorna MAX + 1")
    void calcularSiguienteIntento_conDosIntentos_retornaTres() {
        when(repository.findAllByAlumnoAndExamen(5L, 10L))
                .thenReturn(List.of(respuesta1, respuesta2));

        int siguiente = service.calcularSiguienteIntento(5L, 10L);

        assertThat(siguiente).isEqualTo(3);
    }

    @Test
    @DisplayName("saveAll delega en el repositorio y retorna los documentos guardados")
    void saveAll_delegaEnRepositorio() {
        List<Respuesta> input = List.of(respuesta1);
        when(repository.saveAll(input)).thenReturn(input);

        List<Respuesta> resultado = service.saveAll(input);

        assertThat(resultado).hasSize(1);
        verify(repository, times(1)).saveAll(input);
    }

    @Test
    @DisplayName("findRespuestaByAlumnoByExamenByIntento retorna solo las del intento pedido")
    void findByIntento_retornaRespuestasDelIntentoCorrecto() {
        when(repository.findRespuestaByAlumnoByExamenByIntento(5L, 10L, 1))
                .thenReturn(List.of(respuesta1));

        List<Respuesta> resultado = service.findRespuestaByAlumnoByExamenByIntento(5L, 10L, 1);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNumeroIntento()).isEqualTo(1);
    }
}
