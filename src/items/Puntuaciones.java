package items;

import java.io.Serializable;

public class Puntuaciones implements Serializable {
    private String nombre;
    private int puntuacion;

    public Puntuaciones(String nombre, int puntuacion) {
        this.nombre = nombre;
        this.puntuacion = puntuacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPuntuacion() {
        return puntuacion;
    }
    
}


