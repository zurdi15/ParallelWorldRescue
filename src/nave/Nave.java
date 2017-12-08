package nave;

//Created by Zurdi

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Circle;

public class Nave extends Circle{

    private int vidas = 4;
    private int skin = 0;
    private int puntuacion = 0;
    private Image nave_image = new Image("src/res/imagen/skin_0/nave_0.png");
    private Image nave_vida = new Image("src/res/imagen/skin_0/nave_vida_0.png");

    private boolean escudo = false;
    private int escudo_tiempo;
    private Image escudo_image = new Image("src/res/imagen/hud/escudo.png");
    private boolean salto = false;
    private int salto_tiempo;
    private Image salto_image = new Image("src/res/imagen/hud/salto.png");

    public Nave(float centroX, float centroY, float radio, int skin) throws SlickException {
        super(centroX, centroY, radio);
        this.skin = skin;
    }

    public Nave(float centroX, float centroY, float radio) throws SlickException {
        super(centroX, centroY, radio);
    }


    // -- GETTERS AND SETTERS

    public int getVidas() {
        return vidas;
    }

    public void setVidas(int vidas) {
        this.vidas = vidas;
    }

    public int getSkin() {
        return skin;
    }

    public void setSkin(int skin) throws SlickException {
        this.skin = skin;
        setNave_image();
    }

    public Image getNave_image() {
        return nave_image;
    }

    public void setNave_image() throws SlickException {
        switch (this.skin){
            case 0:
                this.nave_image = new Image("src/res/imagen/skin_0/nave_0.png");
                this.nave_vida = new Image("src/res/imagen/skin_0/nave_vida_0.png");
                break;
            case 1:
                this.nave_image = new Image("src/res/imagen/skin_1/nave_1.png");
                this.nave_vida = new Image("src/res/imagen/skin_1/nave_vida_1.png");
                break;
            case 2:
                this.nave_image = new Image("src/res/imagen/skin_2/nave_2.png");
                this.nave_vida = new Image("src/res/imagen/skin_2/nave_vida_2.png");
                break;
            default:
                this.nave_image = new Image("src/res/imagen/skin_0/nave_0.png");
                this.nave_vida = new Image("src/res/imagen/skin_0/nave_vida_0.png");
                break;
        }
    }

    public Image getNave_vida() {
        return nave_vida;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }

    public boolean isEscudo() {
        return escudo;
    }

    public void setEscudo(boolean escudo) {
        this.escudo = escudo;
    }

    public Image getEscudo_image() {
        return escudo_image;
    }

    public int getEscudo_tiempo() {
        return escudo_tiempo;
    }

    public void setEscudo_tiempo(int escudo_tiempo) {
        this.escudo_tiempo = escudo_tiempo;
    }

    public boolean isSalto() {
        return salto;
    }

    public void setSalto(boolean salto) {
        this.salto = salto;
    }

    public int getSalto_tiempo() {
        return salto_tiempo;
    }

    public void setSalto_tiempo(int salto_tiempo) {
        this.salto_tiempo = salto_tiempo;
    }

    public Image getSalto_image() {
        return salto_image;
    }
}
