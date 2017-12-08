package items;

//Created by Zurdi

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Circle;

public class Misil extends Circle{

    private float centroX_display, centroY_display;
    private double angle;
    private float vector_desplazamiento_x, vector_desplazamiento_y;
    private int skin = 0;
    private Image misil_image = new Image("res/imagen/skin_0/misil_0.png");

    public Misil(float centerPointX, float centerPointY, float radius) throws SlickException {
        super(centerPointX, centerPointY, radius);
        centroX_display = centerPointX;
        centroY_display = centerPointY;
    }



    // -- GETTERS AND SETTERS

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

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public Image getMisil_image() {
        return misil_image;
    }

    public void setMisil_image() throws SlickException {
        switch (this.skin){
            case 0:
                this.misil_image = new Image("res/imagen/skin_0/misil_0.png");
                break;
            case 1:
                this.misil_image = new Image("res/imagen/skin_1/misil_1.png");
                break;
            case 2:
                this.misil_image = new Image("res/imagen/skin_2/misil_2.png");
                break;
            default:
                this.misil_image = new Image("res/imagen/skin_0/misil_0.png");
                break;
        }
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

    public int getSkin() {
        return skin;
    }

    public void setSkin(int skin) throws SlickException {
        this.skin = skin;
        setMisil_image();
    }
}
