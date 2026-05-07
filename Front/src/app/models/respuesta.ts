import { Pregunta } from './pregunta';
import { Alumno } from './alumno';

export class Respuesta {
    id: string;
    texto: string;
    alumno: Alumno;
    pregunta: Pregunta;
    /** Número de intento (1, 2, 3…). Lo asigna el backend al guardar. */
    numeroIntento: number;
    /** Fecha ISO en que se guardó la respuesta. */
    fechaRespuesta: string;
}

/** Agrupa las respuestas de un intento concreto con su metadata */
export interface IntentoExamen {
    numeroIntento: number;
    fechaRespuesta: string;
    respuestas: Respuesta[];
}

/** Respuesta del endpoint /intentos */
export interface ResumenIntentos {
    alumnoId: number;
    examenId: number;
    totalIntentos: number;
    siguienteIntento: number;
}
