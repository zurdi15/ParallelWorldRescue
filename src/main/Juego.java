package main;

//Created by Zurdi

import items.Moneda;
import items.Misil;
import items.PowerUp;
import items.Puntuaciones;
import nave.Nave;
import org.newdawn.slick.*;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.util.ResourceLoader;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;

public class Juego extends BasicGame implements ComponentListener{

    // Dimensiones del display y los elementos
    private final int ALTO_DISPLAY = 600, ANCHO_DISPLAY = 600;
    private final int RADIO_RAIL = 250;
    private final int RADIO_NAVE = 15;
    private final int RADIO_ITEM = 5;
    private Image skin_fondo;
    private Image skin_agujero;
    private Font font;
    private ArrayList<Puntuaciones> puntos = new  ArrayList<Puntuaciones>(5);
    
    //Con añadir aqui nuevas opciones aparecen en el menu
    private String[] menu= {"Nueva Partida","Controles","Skins"," Puntuaciones"};

    // Control del tiempo y el espacio
    private int delta_aux = 0; //temporizador general
    private final int dif_delta = 5; //
    private float  VELOCIDAD_NAVE = 0.01f;
    private double angle = 0.1;
    private double angle_total = 6.28;
    private int delta_item = 0;
    private final int dif_delta_item = 10;
    private int delta_generar_misil = 0;
    private final int dif_delta_generar_misil = 200;
    private int delta_generar_moneda = 0;
    private final int dif_delta_generar_moneda = 500;
    private int delta_generar_powerup = 0;
    private final int dif_delta_generar_powerup = 5000;
    private float VELOCIDAD_ITEM = 3f;
    private int contador_colision_sprite = 0;
    private float auxX_explosion, auxY_explosion;
    private int contador_fin = 1;
    private final int dif_contador_fin = 2250*2;
    private double angle_aleatorio = 0; // Angulo aleatorio para el misil
    private float tiempo_recorrido_item = RADIO_RAIL/VELOCIDAD_ITEM; // Tiempo en llegar al radio del rail
    private int op = 0; //contador de opciones para el menu inicial
    private int INC_VELOCIDAD = 30; // Numero de monedas para incrementar la velocidad
    private int INC_NIVEL = 30; //Numero de monedas para incrementar nivel
    private int iF = 1; //contador de nivel

    //Menu principal

    private Image main_jugar, main_skins, main_controles, main_flecha, titulo, main_puntuaciones;


    // Submenús
    private Image imgControles, historicoPuntuaciones, mejorPuntImg, ultimaPuntImg, pulseE, enterComenzar, enterElegir;
    
    // Musica
    private Music musica_fondo;
    private Audio colisionEffect, coinEffect;
    private Image musica_imagen;

    // Declaracion de elementos
    private Circle rail;
    private Nave nave;
    private ArrayList<Misil> lista_misiles = new ArrayList<>();
    private Misil misil;
    private ArrayList<Moneda> lista_monedas = new ArrayList<>();
    private Moneda moneda;
    private ArrayList<PowerUp> lista_powerups = new ArrayList<>();
    private PowerUp powerUp;
    
    // Flags
    private boolean intro = true;
    private boolean musica = true;
    private boolean controles = false;
    private boolean skins = false;
    private boolean colision_misil = false, colision_misil_sprite = false;
    private boolean game_over = false;
    private boolean mov_horario = true;
    private boolean comenzar = false;
    private int skin_elegida = 0;
    private int mejorPuntuacion = 0;
    private int ultimaPuntuacion = 0;
    private boolean puntuaciones = false;
    
    public Juego(String title) throws SlickException  {

        super(title);

        // Inicializamos el contenedor
        AppGameContainer appgc = new AppGameContainer(this);

        // Establecemos el tamaño de la pantalla.
        appgc.setDisplayMode(ALTO_DISPLAY, ANCHO_DISPLAY, false);

        // Quitamos los FPS de pantalla.
        appgc.setShowFPS(false);

        // Iniciamos el juego. Despues de esto se ejecuta el método init().
        appgc.start();
    }

    @Override
    public void init(GameContainer gameContainer) throws SlickException {

        // Inicializamos el rail
        rail = new Circle(ANCHO_DISPLAY / 2, ALTO_DISPLAY / 2, RADIO_RAIL);

        // Inicializamos la nave
        nave = new Nave(ANCHO_DISPLAY / 2, ALTO_DISPLAY / 2 - rail.getRadius(), RADIO_NAVE, 0);

        // Inicializamos el angulo y la posicion inicial de la nave
        nave.setCenterX((float) (rail.getRadius() * Math.cos(angle) + rail.getCenterX()));
        nave.setCenterY((float) (rail.getRadius() * Math.sin(angle) + rail.getCenterY()));

        // Inicializamos valores para el movimiento de los items
        tiempo_recorrido_item = rail.getRadius() / VELOCIDAD_ITEM;

        // Inicializamos menu
        main_jugar = new Image("res/imagen/main_menu/jugar.png");
        main_skins = new Image("res/imagen/main_menu/skins.png");
        main_controles = new Image("res/imagen/main_menu/controles.png");
        main_puntuaciones = new Image("res/imagen/main_menu/puntuaciones.png");
        main_flecha = new Image("res/imagen/main_menu/flecha.png");
        titulo = new Image("res/imagen/main_menu/titulo.png");

        // Inicializamos las imágenes de otros menús
        historicoPuntuaciones = new Image("res/imagen/main_menu/historico.png");
        mejorPuntImg = new Image("res/imagen/main_menu/mejorpuntuacion.png");
        ultimaPuntImg = new Image("res/imagen/main_menu/ultimapuntuacion.png");
        pulseE = new Image("res/imagen/main_menu/pulsee.png");
        enterComenzar = new Image("res/imagen/main_menu/entercomenzar.png");
        enterElegir = new Image("res/imagen/main_menu/enterelegir.png");
        
        
        // Inicializamos imagenes del hud
        if(musica) {
            musica_imagen = new Image("res/imagen/hud/music_on.png");
        }
        else{
            musica_imagen = new Image("res/imagen/hud/music_off.png");
        }

        // Inicializamos la imagen de los controles
        imgControles = new Image("res/imagen/controles.png");
        
        // Inicializamos la musica (fondo y efectos)
        try {
            colisionEffect = AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream("res/sonido/colision.ogg"));
            coinEffect = AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream("res/sonido/coin.ogg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        musica_fondo = new Music("res/sonido/intro_star_wars.ogg");
        if(musica) {
            musica_fondo.play();
        }

        font = new AngelCodeFont("res/imagen/font/distance.fnt","res/imagen/font/distance.png");

        // Iinicializamso las skins en la ram para mejorar rendimiento
        skin_fondo = new Image("res/imagen/skin_1/fondo_1.jpg");
        skin_agujero = new Image("res/imagen/skin_1/agujero_1.png");
        skin_fondo = new Image("res/imagen/skin_2/fondo_2.jpg");
        skin_agujero = new Image("res/imagen/skin_2/agujero_2.png");
        skin_fondo = new Image("res/imagen/skin_0/fondo_0.jpg");
        skin_agujero = new Image("res/imagen/skin_0/agujero_0.png");
        skin_fondo = new Image("res/imagen/skin_0/fondo_0.jpg");
        skin_agujero = new Image("res/imagen/skin_0/agujero_0.png");

        // Inicializamos el fondo y el agujero por defecto
        skin_fondo = new Image("res/imagen/main_menu/fondo.png");
        
        try{
       FileInputStream archivo = new FileInputStream("src/res/fichero.dat");
       ObjectInputStream ois = new ObjectInputStream(archivo);
       puntos=(ArrayList<Puntuaciones>)ois.readObject();
        archivo.close();
        }catch(EOFException e){
           
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Juego.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Juego.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Juego.class.getName()).log(Level.SEVERE, null, ex);
        }
        
       
        
    }

    @Override
    public void update(GameContainer gameContainer, int delta) throws SlickException {
        // Menu principal
        if(intro | game_over){
          
            try {
                updateIntro(gameContainer);
            } catch (IOException ex) {
                Logger.getLogger(Juego.class.getName()).log(Level.SEVERE, null, ex);
            }
           
        }

        // Menu controles
        else if(controles){
            if(gameContainer.getInput().isKeyPressed(Input.KEY_ESCAPE)){
                intro = true;
                controles = false;
            }
        }

        // Menu skins
        else if(skins){
            if(gameContainer.getInput().isKeyPressed(Input.KEY_RIGHT) || gameContainer.getInput().isKeyPressed(Input.KEY_D)){
                skin_elegida++;
                skin_elegida = skin_elegida % 3;
            }
            if(gameContainer.getInput().isKeyPressed(Input.KEY_LEFT) || gameContainer.getInput().isKeyPressed(Input.KEY_A)){
                skin_elegida--;
                if(skin_elegida == -1) skin_elegida = 2;
                skin_elegida = skin_elegida % 3;
            }

            nave.setSkin(skin_elegida);

            if(gameContainer.getInput().isKeyPressed(Input.KEY_ESCAPE) || gameContainer.getInput().isKeyPressed(Input.KEY_ENTER)){
                intro = true;
                skins = false;
            }
        }

        // Menu puntuaciones
        else if (puntuaciones){
            if(gameContainer.getInput().isKeyPressed(Input.KEY_ESCAPE)){
                intro = true;
                puntuaciones = false;
            }
        }

        // Juego - Nivel
        else {
            try {
                updateJuego(gameContainer, delta);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Switch de la musica
        if (gameContainer.getInput().isKeyPressed(Input.KEY_M)) {
                detectarTecla("m");
        }

        // Pausar juego
        if (gameContainer.getInput().isKeyPressed(Input.KEY_ESCAPE)) {
            if (!gameContainer.isPaused()) gameContainer.pause();
            else gameContainer.resume();
        }

        // Menu de pausa
        if (gameContainer.isPaused()) {
            // Si pulsamos E salimos del nivel
            if (gameContainer.getInput().isKeyPressed(Input.KEY_E)){
                try {
                    resetJuego(false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                gameContainer.resume();
                intro = true;
                musica_fondo = new Music("res/sonido/intro_star_wars.ogg");
                if(musica) {
                    musica_fondo.play();
                }
            }
        }

    }

    // Update del menu principal
    private void updateIntro(GameContainer gameContainer) throws SlickException, IOException {
        // Reseteo del menu al volver de perder la partida
        if(contador_fin == 0){
            musica_fondo = new Music("res/sonido/intro_star_wars.ogg");
            if(musica) {
                musica_fondo.play();
            }
            contador_fin = 1;
        }
        if (gameContainer.getInput().isKeyPressed(Input.KEY_DOWN) || gameContainer.getInput().isKeyPressed(Input.KEY_S)) {
            op++;
            if (op >= menu.length) {
                op = 0;
            }
        }
        if (gameContainer.getInput().isKeyPressed(Input.KEY_UP) || gameContainer.getInput().isKeyPressed(Input.KEY_W)) {
            op--;
            if (op < 0) {
                op = menu.length-1;
            }
        }
        if (gameContainer.getInput().isKeyPressed(Input.KEY_ENTER))
            switch (op){
                case 0:
                    intro = false;
                    musica_fondo = new Music("res/sonido/level_music.ogg");
                    if(musica)
                        musica_fondo.play();
                    break;
                case 1:
                    intro = false;
                    controles = true;
                    break;
                case 2:
                    intro = false;
                    skins = true;
                    break;
                case 3:
                    intro = false;
                    puntuaciones = true;
                    break;
            }

        // Esc. Salir
        if (gameContainer.getInput().isKeyPressed(Input.KEY_ESCAPE)){
            
           
            gameContainer.exit();
        }
    }


    // Update del juego - nivel
    private void updateJuego(GameContainer gameContainer, int delta) throws SlickException, IOException {

        // Preparamos los graficos antes de comenzar para la skin seleccionada
        if(!comenzar) {
            switch (skin_elegida) {
                case 0:
                    skin_fondo = new Image("res/imagen/skin_0/fondo_0.jpg");
                    skin_agujero = new Image("res/imagen/skin_0/agujero_0.png");
                    break;
                case 1:
                    skin_fondo = new Image("res/imagen/skin_1/fondo_1.jpg");
                    skin_agujero = new Image("res/imagen/skin_1/agujero_1.png");
                    break;
                case 2:
                    skin_fondo = new Image("res/imagen/skin_2/fondo_2.jpg");
                    skin_agujero = new Image("res/imagen/skin_2/agujero_2.png");
                    break;
                default:
                    skin_fondo = new Image("res/imagen/skin_0/fondo_0.jpg");
                    skin_agujero = new Image("res/imagen/skin_0/agujero_0.png");
                    break;
            }
        }

        // Espera de preparacion
        if(gameContainer.getInput().isKeyPressed(Input.KEY_ENTER)){
            comenzar = true;
        }

        // Logica una vez ha comenzado el juego
        if(comenzar) {

            // Se compara la puntuacion para aumentar la velocidad
            if ((nave.getPuntuacion() % INC_VELOCIDAD == 0) && (nave.getPuntuacion() != 0)) {
                VELOCIDAD_NAVE = (float) (VELOCIDAD_NAVE * 1.2);
                VELOCIDAD_ITEM = (float) (VELOCIDAD_ITEM * 1.2);
                tiempo_recorrido_item = RADIO_RAIL/VELOCIDAD_ITEM;
                INC_VELOCIDAD += INC_VELOCIDAD;
            }

            // El juego continua mientras tengas vidas suficientes
            if (nave.getVidas() > 0) {

                // Incrementamos los temporizadores delta
                delta_aux += delta;
                delta_item += delta;
                delta_generar_misil += delta;
                delta_generar_moneda += delta;
                delta_generar_powerup += delta;

                // Se ejecutan los updates logicos cuando llega al limite impuesto para el temporizador
                if (delta_aux > dif_delta) {

                    // Detectamos colisiones
                    detectarColision();

                    // Movemos la nave automaticamente
                    moverNave();

                    // Generamos nuevos items
                    if (delta_item > dif_delta_item) {
                        if(delta_generar_misil > dif_delta_generar_misil){
                            generarMisil();
                            // Reseteamos el temporizador delta de los misiles
                            delta_generar_misil = 0;
                        }

                        if(delta_generar_moneda > dif_delta_generar_moneda){
                            if(lista_monedas.size()<=19)
                            generarMoneda();
                            // Reseteamos el temporizador delta de las monedas
                            delta_generar_moneda = 0;
                        }
                        if(delta_generar_powerup > dif_delta_generar_powerup){
                            if(lista_powerups.size()<=1){
                            int p = (int) (Math.random()*(0-2)+2);
                            switch (p){
                                case 0:
                                    generarPowerUp("escudo");
                                    break;
                                case 1:
                                    generarPowerUp("salto");
                                    break;
                            }
                            }
                            // Reseteamos el temporizador delta de los powerups
                            delta_generar_powerup = 0;
                        }

                        // Movemos los items automaticamente
                        moverItems();

                        // Comprobamos si algun misil se ha salido de la pantalla para destruirlo
                        for (Misil misil: lista_misiles) {
                            if(misil.getCenterX() < 0 || misil.getCenterX() > ANCHO_DISPLAY || misil.getCenterY() > ALTO_DISPLAY || misil.getCenterY() < 0){
                                lista_misiles.remove(misil);
                                break;
                            }
                        }

                        // Reseteamos el temporizador delta de los items
                        delta_item = 0;
                    }

                    // Cambiamos la direccion de la nave
                    if (gameContainer.getInput().isKeyPressed(Input.KEY_RIGHT) || gameContainer.getInput().isKeyDown(Input.KEY_D)) {
                        detectarTecla("d");
                    }
                    if (gameContainer.getInput().isKeyPressed(Input.KEY_LEFT) || gameContainer.getInput().isKeyDown(Input.KEY_A)) {
                        detectarTecla("a");
                    }

                    // Restamos tiempo a los powerups activos
                    if(nave.isEscudo()) {
                        nave.setEscudo_tiempo(nave.getEscudo_tiempo() - 1);
                        // Si el tiempo llega a 0, se quita el escudo
                        if(nave.getEscudo_tiempo() == 0){
                            nave.setEscudo(false);
                        }
                    }
                    if(nave.isSalto()){
                        if (gameContainer.getInput().isKeyPressed(Input.KEY_SPACE)) {
                            detectarTecla("espacio");                           
                        }
                        nave.setSalto_tiempo(nave.getSalto_tiempo() - 1);
                        // Si el tiempo llega a 0, se quita el salto
                        if(nave.getSalto_tiempo() == 0){
                            nave.setSalto(false);
                        }
                    }
                    // Reseteamos las teclas ya pulsadas
                    gameContainer.getInput().clearKeyPressedRecord();
                    // Reseteamos el temporizador delta general
                    delta_aux = 0;
                }
            }
            // Si se acaban las vidas se resetea el juego
            else resetJuego(true);
        }
    }

    @Override
    public void render(GameContainer gameContainer, Graphics g) throws SlickException {

        // Dibujamos el fondo
        g.scale(1.0f, 1.0f);
        g.drawImage(skin_fondo, 0,0);

        // Dibujamos el menu principal
        if(intro){
            skin_fondo = new Image("res/imagen/main_menu/fondo.png");
            renderIntro(g);
        }

        // Pantalla de game over
        else if(game_over){
            renderGameOver(g);
        }

        // Pantalla de controles
        else if(controles){
            renderControles(g);
        }

        // Pantalla de skins
        else if(skins){
            renderSkins(g);
        }

        // Pantalla de puntuaciones
        else if(puntuaciones){
            try {
                renderPuntuaciones(g);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Juego.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        // Juego - Nivel
        else {
            renderJuego(gameContainer, g);
        }
    }

    // Render pantalla Intro
    private void renderIntro(Graphics g){
        g.drawImage(titulo, 50, 15);
        // Cambio de escala para el menu principal
        g.setFont(font);
        g.setColor(Color.white);
        g.drawImage(main_jugar, ANCHO_DISPLAY/2-main_jugar.getWidth()/2, ALTO_DISPLAY/4-40);
        g.drawImage(main_controles, ANCHO_DISPLAY/2-main_controles.getWidth()/2, ALTO_DISPLAY/4+60);
        g.drawImage(main_skins, ANCHO_DISPLAY/2-main_skins.getWidth()/2, ALTO_DISPLAY/2+10);
        g.drawImage(main_puntuaciones, ANCHO_DISPLAY/2-main_puntuaciones.getWidth()/2, ALTO_DISPLAY/2+130);
        g.scale(1.2f, 1.2f);

        switch (op) {
            case 0:
                g.drawImage(main_flecha, ANCHO_DISPLAY/4-130, ALTO_DISPLAY/4-40);
                break;
            case 1:
                g.drawImage(main_flecha, ANCHO_DISPLAY/4-130, ALTO_DISPLAY/4+45);
                break;
            case 2:
                g.drawImage(main_flecha, ANCHO_DISPLAY/4-130, ALTO_DISPLAY/2-20);
                break;
            case 3:
                g.drawImage(main_flecha, ANCHO_DISPLAY/4-130, ALTO_DISPLAY/2+70);
                break;
            default:
                break;
        }

        g.scale(0.8f, 0.8f);
        g.drawString("(ESC) Salir",10, ALTO_DISPLAY - 50);
    }

    // Render pantalla Controles
    private void renderControles(Graphics g){
        // Cambio de escala para el menu principal
        g.drawImage(imgControles, ANCHO_DISPLAY/2-imgControles.getWidth()/2, ALTO_DISPLAY/2-imgControles.getHeight()/2);
    }

    // Render pantalla Skins
    private void renderSkins(Graphics g) throws SlickException {
        g.setFont(font);
        g.setColor(Color.white);
        g.drawString("<-- SKIN " + (skin_elegida+1) + " ELEGIDA -->", ANCHO_DISPLAY/2-170, ALTO_DISPLAY/4);
        g.drawImage(enterElegir, ANCHO_DISPLAY/2-enterElegir.getWidth()/2, ALTO_DISPLAY-ALTO_DISPLAY/4);
        g.scale(2.0f, 2.0f);
        g.drawImage(nave.getNave_image(), ANCHO_DISPLAY/4-nave.getNave_image().getWidth()/2, ALTO_DISPLAY/4);
        switch (skin_elegida) {
            case 0:
                skin_fondo = new Image("res/imagen/skin_0/fondo_0.jpg");
                break;
            case 1:
                skin_fondo = new Image("res/imagen/skin_1/fondo_1.jpg");
                break;
            case 2:
                skin_fondo = new Image("res/imagen/skin_2/fondo_2.jpg");
                break;
            default:
                skin_fondo = new Image("res/imagen/skin_0/fondo_0.jpg");
                break;
        }
        g.drawString("(ESC) Atras",10, ALTO_DISPLAY - 50);
    }

    // Render pantalla puntuaciones
    private void renderPuntuaciones(Graphics g) throws IOException, ClassNotFoundException {
        // Cambio de escala para el menu principal
        g.setFont(font);
        g.scale(1.2f, 1.2f);
        g.setColor(Color.red);
        g.drawImage(mejorPuntImg, 50, ALTO_DISPLAY/4 - 100);
        g.drawString("" + mejorPuntuacion, 300, ALTO_DISPLAY/4 - 110);
        g.drawImage(ultimaPuntImg, 50, ALTO_DISPLAY/4 - 50);
        g.drawString("" + ultimaPuntuacion, 300, ALTO_DISPLAY/4 - 60);
        g.drawImage(historicoPuntuaciones, 50, ALTO_DISPLAY/4 + 20);
        g.scale(0.8f, 0.8f);
        g.setColor(Color.white);
        g.drawString("(ESC) Atras", 10, ALTO_DISPLAY - 50);
        g.setColor(Color.red);
        leerPuntuacion(g);
        g.setColor(Color.white);
    }

    // Leer puntuaciones del archivo puntuaciones.txt
    private void leerPuntuacion(Graphics g) throws IOException, ClassNotFoundException {
        g.setFont(font);
        g.scale(1.2f, 1.2f);
        
         for (int n=0; n<puntos.size()&n<5;) { 
                g.drawString(puntos.get(n).getNombre()+" " +puntos.get(n).getPuntuacion(), 60, ALTO_DISPLAY/4+70+(40*n++));
         }

    }

    // Guardar puntuaciones
    private void guardarPuntuacion() throws IOException {
        FileOutputStream  archivo = new FileOutputStream("src/res/fichero.dat");
        Puntuaciones puntuaciones= new Puntuaciones(System.getProperty("user.name"),ultimaPuntuacion),punt;
        puntos.add(puntuaciones);
        for(int i=0;i<puntos.size()-1;i++){
              for(int j=0;j<puntos.size()-i-1;j++){
                   if(puntos.get(j+1).getPuntuacion()>puntos.get(j).getPuntuacion()){
                      punt=puntos.get(j+1);
                      puntos.set(j+1, puntos.get(j));
                      puntos.set(j, punt);
                   }
              }
        }
        ObjectOutputStream oos = new ObjectOutputStream(archivo);
        oos.writeObject(puntos);
        archivo.close();
        
       
    }

    // Render juego - nivel
    private void renderJuego(GameContainer gameContainer, Graphics g) throws  SlickException{
        // Dibujar hud
        renderHUD(gameContainer, g);

        // Dibujamos la nave
         renderNave(g);

        // Dibujamos el agujero
        g.drawImage(skin_agujero, ANCHO_DISPLAY / 2 - (skin_agujero.getWidth()/2), ALTO_DISPLAY / 2 - (skin_agujero.getHeight()/2));

        // Dibujamos los misiles
        for (Misil misil : lista_misiles) {
            g.drawImage(misil.getMisil_image(), misil.getCenterX()-(misil.getMisil_image().getWidth()/2), misil.getCenterY()-(misil.getMisil_image().getHeight()/2));
        }

        // Dibujamos la explosion de la colision_misil
        if(colision_misil_sprite){
            if(contador_colision_sprite < 200) {
                Image explosion = new Image("res/imagen/hud/explosion.png");
                g.drawImage(explosion, auxX_explosion - (explosion.getWidth() / 2), auxY_explosion - (explosion.getHeight() / 2));
                contador_colision_sprite++;
            }
            else{
                contador_colision_sprite = 0;
                colision_misil_sprite = false;
            }
        }

        // Dibujamos las monedas
        for (Moneda moneda : lista_monedas) {
            g.drawImage(moneda.getMoneda_image(), moneda.getCenterX()-(moneda.getMoneda_image().getWidth()/2), moneda.getCenterY()-(moneda.getMoneda_image().getHeight()/2));
        }

        // Dibujamos los powerups como item
        for (PowerUp powerUp : lista_powerups) {
            g.drawImage(powerUp.getPowerup_image(), powerUp.getCenterX()-(powerUp.getPowerup_image().getWidth()/2), powerUp.getCenterY()-(powerUp.getPowerup_image().getHeight()/2));
        }

        // Dibujamos los efectos de los powerups
        renderPowerup(g);
    }

    // Render nave
    private void renderNave(Graphics g){
        double ang;
        ang = Math.asin((nave.getCenterX()-rail.getCenterX())/rail.radius);
        if(nave.getCenterY()>=rail.getCenterY()) { //si esta entre 180 y 360
            ang = Math.PI - ang;
        }

        ang=Math.toDegrees(ang);

        g.translate(nave.getCenterX() - (nave.getNave_image().getWidth()/2), nave.getCenterY() - (nave.getNave_image().getHeight()/2));	
        g.rotate((nave.getNave_image().getWidth()/2),(nave.getNave_image().getHeight()/2), (float)(ang));
        if(mov_horario)
            g.drawImage(nave.getNave_image().getFlippedCopy(true, false),0,0);
        else
            g.drawImage(nave.getNave_image(),0,0);
        g.resetTransform();
    }

    // Render hud
    private void renderHUD(GameContainer gameContainer, Graphics g) throws SlickException {

        // Flag de musica
        g.setColor(Color.white);
        g.drawImage(musica_imagen, 5, 5);

        // Dibujamos las vidas restantes
        for(int i=0, k = 5; i<nave.getVidas(); i++, k+=nave.getNave_vida().getWidth()+5){
            g.drawImage(nave.getNave_vida(), k, ALTO_DISPLAY-(nave.getNave_vida().getHeight()-5)-10);
        }

        // Dibujamos el nivel actual
        g.drawString("Nivel: " + iF, ANCHO_DISPLAY/2-50, 5);
        g.drawString(INC_NIVEL - nave.getPuntuacion() + ": siguiente nivel", ANCHO_DISPLAY/2-100, 17);

        // Dibujamos la puntuacion
        g.drawString("Puntuacion: " + nave.getPuntuacion(), ANCHO_DISPLAY-150, 5);
        if (nave.getPuntuacion() == INC_NIVEL){
            INC_NIVEL += 30; //puntuación necesaria para siguiente nivel
            iF++; //contador de nivel
        }
        g.drawString("Mejor Puntuacion: "+mejorPuntuacion, ANCHO_DISPLAY-204, 17);
        g.drawString("Ultima Puntuacion: "+ultimaPuntuacion, ANCHO_DISPLAY-213, 29);

        // Dibujamos el menu de pausa
        if(gameContainer.isPaused()) {
            g.drawString("PAUSA", ANCHO_DISPLAY/2 - 25, ALTO_DISPLAY / 8+50);
            g.drawImage(pulseE, ANCHO_DISPLAY/2 - pulseE.getWidth()/2, ALTO_DISPLAY/8 + 70);
        }

        // Dibujamos la frase de preparacion
        if(!comenzar){
            g.drawImage(enterComenzar, ANCHO_DISPLAY/2 - enterComenzar.getWidth()/2, ALTO_DISPLAY/8 +50);
        }

        // Dibujamos el temporizador del powerup
        if(nave.isEscudo()){
            g.drawString(""+(nave.getEscudo_tiempo()/100)+"", nave.getCenterX()-7, nave.getCenterY()+nave.getNave_image().getHeight()+3);
        }
        if(nave.isSalto()){
            g.drawString(""+(nave.getSalto_tiempo()/100)+"", nave.getCenterX()-7, nave.getCenterY()+nave.getNave_image().getHeight()+19);
        }
    }

    // Render de los powerups
    private void renderPowerup(Graphics g){
        //Comprobamos los powerups activos
        if(nave.isEscudo()){
            g.drawImage(nave.getEscudo_image(), nave.getCenterX()-nave.getNave_image().getHeight()/2, nave.getCenterY()-nave.getNave_image().getWidth()/2);
        }

        if(nave.isSalto()){
            g.drawImage(nave.getSalto_image(), nave.getCenterX()-nave.getNave_image().getHeight()/2, nave.getCenterY()-nave.getNave_image().getWidth()/2);
        }
    }

    // Render pantalla GameOver
    private void renderGameOver(Graphics g){
        
        if(contador_fin < dif_contador_fin) {
            g.setColor(Color.black);
            Rectangle rectangle = new Rectangle(0, 0, ANCHO_DISPLAY, ALTO_DISPLAY);
            g.fill(rectangle);
            g.setColor(Color.red);
            g.drawString("GAME OVER", ANCHO_DISPLAY / 2 - 40, ALTO_DISPLAY / 2 - 3);
            g.drawString("Puntuacion obtenida: " + ultimaPuntuacion, ANCHO_DISPLAY / 2 - 80, ALTO_DISPLAY / 2 + 20);
            contador_fin++;
        }
        else{
            contador_fin = 0;
            game_over = false;
            // Volvemos al menu principal
            intro = true;
        }
    }

    // Generar misiles
    private void generarMisil() throws SlickException {

        misil = new Misil(ANCHO_DISPLAY / 2, ALTO_DISPLAY / 2, RADIO_ITEM);
        lista_misiles.add(misil);

        angle_aleatorio = Math.random()*(0-angle_total)+angle_total;

        //creamos unas cordenadas de trayectoria para el objeto basadas en dicho angulo
        //como variables puesto que despues no las necesitaremos
        float xFinal = (float)(rail.getRadius() * Math.cos(angle_aleatorio));
        float yFinal = (float)(rail.getRadius() * Math.sin(angle_aleatorio));
        //creamos un vector de desplazamiento para el objeto
        lista_misiles.get(lista_misiles.size()-1).setVector_desplazamiento_x((xFinal)/tiempo_recorrido_item);
        lista_misiles.get(lista_misiles.size()-1).setVector_desplazamiento_y((yFinal)/tiempo_recorrido_item);

    }

    // Generar monedas
    private void generarMoneda() throws SlickException {

        moneda = new Moneda(ANCHO_DISPLAY / 2, ALTO_DISPLAY / 2, RADIO_ITEM);
        lista_monedas.add(moneda);

        // Generamos un angulo aleatorio para la moneda
        angle_aleatorio = Math.random()*(0-angle_total)+angle_total;

        //creamos unas cordenadas de trayectoria para el objeto basadas en dicho angulo
        //como variables puesto que despues no las necesitaremos
        float xFinal = (float)(rail.getRadius() * Math.cos(angle_aleatorio));
        float yFinal = (float)(rail.getRadius() * Math.sin(angle_aleatorio));
        //creamos un vector de desplazamiento para el objeto
        lista_monedas.get(lista_monedas.size()-1).setVector_desplazamiento_x((xFinal)/tiempo_recorrido_item);
        lista_monedas.get(lista_monedas.size()-1).setVector_desplazamiento_y((yFinal)/tiempo_recorrido_item);
    }

    // Generar powerup
    private void generarPowerUp(String tipo) throws SlickException {

        // Generamos el tipo del powerup
        powerUp = new PowerUp(ANCHO_DISPLAY / 2, ALTO_DISPLAY / 2, RADIO_ITEM, tipo);
        lista_powerups.add(powerUp);

        // Generamos un angulo aleatorio para la moneda
        angle_aleatorio = Math.random()*(0-angle_total)+angle_total;

        //creamos unas cordenadas de trayectoria para el objeto basadas en dicho angulo
        //como variables puesto que despues no las necesitaremos
        float xFinal = (float)(rail.getRadius() * Math.cos(angle_aleatorio));
        float yFinal = (float)(rail.getRadius() * Math.sin(angle_aleatorio));
        //creamos un vector de desplazamiento para el objeto
        lista_powerups.get(lista_powerups.size()-1).setVector_desplazamiento_x((xFinal)/tiempo_recorrido_item);
        lista_powerups.get(lista_powerups.size()-1).setVector_desplazamiento_y((yFinal)/tiempo_recorrido_item);
    }

    // Control de los items
    private void moverItems(){
        for (Misil misil: lista_misiles) {
            misil.setCenterX(misil.getCenterX()+misil.getVector_desplazamiento_x());
            misil.setCenterY(misil.getCenterY()+misil.getVector_desplazamiento_y());
        }
        for (Moneda moneda: lista_monedas) {
            // Comprobamos si la moneda esta en el radio del rail --> distancia(radio_rail) = sqrt((x2-x1)+(y2-y1))
            if(Math.sqrt((Math.pow((double)(moneda.getCenterX()-moneda.getCentroX_display()), 2)+Math.pow((double)(moneda.getCenterY()-moneda.getCentroY_display()), 2))) >= RADIO_RAIL ){
                moneda.setRadio_rail(true);
            }
            if(!moneda.isRadio_rail()) {
                moneda.setCenterX(moneda.getCenterX() + moneda.getVector_desplazamiento_x());
                moneda.setCenterY(moneda.getCenterY() + moneda.getVector_desplazamiento_y());
            }
        }
        for (PowerUp powerup: lista_powerups) {
            if(Math.sqrt((Math.pow((double)(powerup.getCenterX()-powerup.getCentroX_display()), 2)+Math.pow((double)(powerup.getCenterY()-powerup.getCentroY_display()), 2))) >= RADIO_RAIL ){
                powerup.setRadio_rail(true);
            }
            if(!powerup.isRadio_rail()) {
                powerup.setCenterX(powerup.getCenterX() + powerup.getVector_desplazamiento_x());
                powerup.setCenterY(powerup.getCenterY() + powerup.getVector_desplazamiento_y());
            }
        }
    }

    // Movemos la nave
    private void moverNave(){
        if(mov_horario){
            nave.setCenterX((float)(rail.getRadius()*Math.cos(angle)+rail.getCenterX()));
            nave.setCenterY((float)(rail.getRadius()*Math.sin(angle)+rail.getCenterY()));
            angle += VELOCIDAD_NAVE;
        }
        else{
            nave.setCenterX((float)(rail.getRadius()*Math.cos(angle)+rail.getCenterX()));
            nave.setCenterY((float)(rail.getRadius()*Math.sin(angle)+rail.getCenterY()));
            angle -= VELOCIDAD_NAVE;
        }
    }

    // Control de la nave
    private void cambiarDireccion(String tecla){
        // Decidimos la direccion de la nave
        switch (tecla){
            case "d":
                mov_horario = true;
                break;

            case "a":
                mov_horario = false;
                break;
        }
    }

    // Detector de colisiones
    private void detectarColision() throws SlickException {

        // Comprobamos si la nave colisiona con algun item

        for (Misil misil : lista_misiles){
            if(nave.intersects(misil)){
                colision_misil = true;
                // Se compara el contador de vidas para que no salga la ultima explosion en la partida nueva
                if(nave.getVidas()>1) colision_misil_sprite = true;
                lista_misiles.remove(misil);
                break;
            }
        }

        for (Moneda moneda : lista_monedas){
            if(nave.intersects(moneda)){
                // Sonido de recoger moneda
                if(musica) {
                    coinEffect.playAsSoundEffect(1.0f, 1.0f, false);
                }
                nave.setPuntuacion(nave.getPuntuacion()+1);
                lista_monedas.remove(moneda);
                break;
            }
        }

        for (PowerUp powerUp : lista_powerups){
            if(nave.intersects(powerUp)){
                // Sonido de recoger moneda
                if(musica) {
                    coinEffect.playAsSoundEffect(1.0f, 1.0f, false);
                }

                // Detectamos que tipo de powerup es
                switch (powerUp.getTipo()) {
                    case "escudo":
                        // Ponemos el escudo a true
                        nave.setEscudo(true);
                        // Ponemos el tiempo del escudo
                        nave.setEscudo_tiempo(500);
                        break;
                    case "salto":
                        // Ponemos el salto a true
                        nave.setSalto(true);
                        nave.setSalto_tiempo(500);
                }
                // Lo eliminamos de la pantalla
                lista_powerups.remove(powerUp);
                break;
            }
        }


        // Logica de colision con misil
        if(colision_misil){

            // Sonido de colision_misil
            if(musica) {
                colisionEffect.playAsSoundEffect(1.0f, 1.0f, false);
            }

            // Restamos una vida a la nave en caso de no tener escudo activo
            if(!nave.isEscudo()) {
                nave.setVidas(nave.getVidas() - 1);
            }

            // Recogemos la posicion donde la nave colisiono
            auxX_explosion = nave.getCenterX();
            auxY_explosion = nave.getCenterY();

            colision_misil = false;
        }
    }

    // Detector de tecla
    private void detectarTecla(String tecla) throws SlickException {
        switch (tecla){
            case "m":
                if(musica) {
                    musica_fondo.stop();
                    musica = false;
                    musica_imagen = new Image("res/imagen/hud/music_off.png");
                }
                else{
                    musica_fondo.play();
                    musica = true;
                    musica_imagen = new Image("res/imagen/hud/music_on.png");
                }
                break;
            case "d":
                cambiarDireccion(tecla);
                break;
            case "a":
                cambiarDireccion(tecla);
                break;
            case "espacio":
                angle = angle + angle_total/2;
                break;
            default:
                break;

        }
    }

    // Resetear el juego
    private void resetJuego(boolean lose) throws SlickException, IOException {

        if(lose) {
            game_over = true;
            // Ponemos musica de game over
            musica_fondo = new Music("res/sonido/game_over.ogg");
            if (musica) {
                musica_fondo.play();
            }
        }

        // Reseteamos vidas
        nave.setVidas(4);

        // Reseteamos powerups
        nave.setEscudo_tiempo(0);
        nave.setEscudo(false);
        nave.setSalto_tiempo(0);
        nave.setSalto(false);

        // Reseteamos nave
        angle = 0.1;
        nave.setCenterX((float) (rail.getRadius() * Math.cos(angle) + rail.getCenterX()));
        nave.setCenterY((float) (rail.getRadius() * Math.sin(angle) + rail.getCenterY()));

        //Reseteamos la puntuacion
        ultimaPuntuacion = nave.getPuntuacion();
        if(ultimaPuntuacion >= mejorPuntuacion) {
            mejorPuntuacion = ultimaPuntuacion;
        }
        guardarPuntuacion();
        nave.setPuntuacion(0);

        // Borramos todos los items
        lista_misiles.clear();
        lista_monedas.clear();
        lista_powerups.clear();
        // Reseteamos el flag de comienzo
        comenzar = false;
        INC_VELOCIDAD = 30;
        INC_NIVEL = 30;
        iF = 1;
        VELOCIDAD_NAVE = 0.01f;
        VELOCIDAD_ITEM = 3f;
        // Reseteamos el fondo
        skin_fondo = new Image("res/imagen/main_menu/fondo.png");
    }

    @Override
    public void componentActivated(AbstractComponent source) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
