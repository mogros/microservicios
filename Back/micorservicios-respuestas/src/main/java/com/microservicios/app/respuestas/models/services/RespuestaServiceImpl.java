package com.microservicios.app.respuestas.models.services;

import com.microservicios.app.respuestas.models.entity.Respuesta;
import com.microservicios.app.respuestas.models.repository.RespuestaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class RespuestaServiceImpl implements RespuestaService {

    private static final Logger log = LoggerFactory.getLogger(RespuestaServiceImpl.class);

    @Autowired
    private RespuestaRepository repository;

    @Override
    public List<Respuesta> saveAll(Iterable<Respuesta> respuestas) {
        return repository.saveAll(respuestas);
    }

    @Override
    public List<Respuesta> findRespuestaByAlumnoByExamen(Long alumnoId, Long examenId) {
        return repository.findRespuestaByAlumnoByExamen(alumnoId, examenId);
    }

    @Override
    public List<Respuesta> findRespuestaByAlumnoByExamenByIntento(
            Long alumnoId, Long examenId, Integer numeroIntento) {
        return repository.findRespuestaByAlumnoByExamenByIntento(alumnoId, examenId, numeroIntento);
    }

    @Override
    public List<Long> findExamenesIdsConRespuestasByAlumno(Long alumnoId) {
        return repository.findExamenesIdsConRespuestasByAlumno(alumnoId)
                .stream()
                .filter(r -> r.getPregunta() != null
                          && r.getPregunta().getExamen() != null)
                .map(r -> r.getPregunta().getExamen().getId())
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<Respuesta> findByAlumnoId(Long alumnoId) {
        return repository.findByAlumnoId(alumnoId);
    }

    @Override
    public int calcularSiguienteIntento(Long alumnoId, Long examenId) {
        return contarIntentos(alumnoId, examenId) + 1;
    }

    @Override
    public int contarIntentos(Long alumnoId, Long examenId) {
        List<Respuesta> anteriores = repository.findAllByAlumnoAndExamen(alumnoId, examenId);
        return anteriores.stream()
                .map(Respuesta::getNumeroIntento)
                .filter(n -> n != null)
                .max(Integer::compareTo)
                .orElse(0);
    }
}
