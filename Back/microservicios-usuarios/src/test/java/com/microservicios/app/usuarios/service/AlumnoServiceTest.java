package com.microservicios.app.usuarios.service;

import com.microservicios.commons.alumnos.models.entity.Alumno;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("AlumnoService — validaciones de entidad")
class AlumnoServiceTest {

    @Test
    @DisplayName("Alumno equals compara por ID")
    void alumno_equalsById() {
        Alumno a = new Alumno();
        a.setId(1L);
        Alumno b = new Alumno();
        b.setId(1L);
        Alumno c = new Alumno();
        c.setId(2L);

        assertThat(a).isEqualTo(b);
        assertThat(a).isNotEqualTo(c);
    }

    @Test
    @DisplayName("Alumno con id null no es igual a ninguno")
    void alumno_sinId_noEsIgual() {
        Alumno a = new Alumno();
        Alumno b = new Alumno();

        assertThat(a).isNotEqualTo(b);
    }
}
