import { Pregunta } from './pregunta';
import { Alumno } from "./alumno";

export class Respuesta {
        //mongo db
        id: string;
        texto: string;
        alumno: Alumno
        pregunta: Pregunta;
}
