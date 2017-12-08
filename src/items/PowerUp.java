package items;

//Created by Zurdi

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Circle;

public class PowerUp extends Circle {

    private float centroX_display, centroY_display;
    private boolean radio_rail = false;
    private double angle;
    private float vector_desplazamiento_x, vector_desplazamiento_y;
    private String tipo;
    private Image powerup_image;

    public PowerUp(float centerPointX, float centerPointY, float radius, String tipo) throws SlickException {
        super(centerPointX, centerPointY, radius);
        this.centroX_display = centerPointX;
        this.centroY_display = centerPointY;
        this.tipo = tipo;
        this.setPowerup_image();
    }



    // -- GETTERS AND SETTERS --

    public float getCentroX_display() {
        return centroX_display;
    }

    public void setCentroX_display(float centroX_display) {
        this.centroX_display = centroX_display;
    }

    public float getCentroY_display() {
        return centroY_display;
    }

    public void setCentroY_display(float centroY_display) {
        this.centroY_display = centroY_display;
    }

    public boolean isRadio_rail() {
        return radio_rail;
    }

    public void setRadio_rail(boolean radio_rail) {
        this.radio_rail = radio_rail;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public float getVector_desplazamiento_x() {
        return vector_desplazamiento_x;
    }

    public void setVector_desplazamiento_x(float vector_desplazamiento_x) {
        this.vector_desplazamiento_x = vector_desplazamiento_x;
    }

    public float getVector_desplazamiento_y() {
        return vector_desplazamiento_y;
    }

    public void setVector_desplazamiento_y(float vector_desplazamiento_y) {
        this.vector_desplazamiento_y = vector_desplazamiento_y;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Image getPowerup_image() {
        return powerup_image;
    }

    public void setPowerup_image() throws SlickException {
        switch (tipo){
            case "escudo":
                this.powerup_image = new Image("res/imagen/skin_0/escudo_0.png");
                break;
            case "salto":
                this.powerup_image = new Image("res/imagen/skin_0/salto_0.png");
            default:
                break;
        }
    }
}
