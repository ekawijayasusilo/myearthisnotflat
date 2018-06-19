package com.mygdx.meinf.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.meinf.GdxMeinf;

public class MenuState extends State{

    private Texture background;
    public Texture title1;
    public Texture title2;
    public float titlePos1;
    public float titlePos2;
    public float titleAlpha;

    private Texture myBtn;
    private Texture exitBtn;
    public Rectangle easyBound;
    public Rectangle medBound;
    public Rectangle hardBound;
    public Rectangle exitBound;
    public Vector2 easyBtnPos;
    public Vector2 medBtnPos;
    public Vector2 hardBtnPos;
    public Vector2 exitBtnPos;
    public BitmapFont myFont;
    public int fontSize=42;

    public Texture fadeScreen;
    public float fadeAlpha;
    public boolean isClicked;
    public int diffChosen;

    public Music music;

    public MenuState(GameStateManager gsm) {
        super(gsm);

        cam.setToOrtho(false,GdxMeinf.WIDTH, GdxMeinf.HEIGHT);
        background = new Texture("startbg.jpg");
        title1=new Texture("title1.png");
        title2=new Texture("title2.png");
        myBtn = new Texture("btn.png");
        exitBtn = new Texture("exit.png");
        fadeScreen=new Texture("gameOverBG.png");

        titlePos1=0-0.1f*GdxMeinf.WIDTH;
        titlePos2=0+0.1f*GdxMeinf.WIDTH;
        titleAlpha=0f;
        fadeAlpha=0f;
        isClicked=false;

        easyBtnPos=new Vector2(0.25f*GdxMeinf.WIDTH-myBtn.getWidth()/2 , 0.3f*GdxMeinf.HEIGHT-myBtn.getHeight()/2);
        medBtnPos=new Vector2(0.5f*GdxMeinf.WIDTH-myBtn.getWidth()/2,0.3f*GdxMeinf.HEIGHT-myBtn.getHeight()/2);
        hardBtnPos=new Vector2(0.75f*GdxMeinf.WIDTH-myBtn.getWidth()/2,0.3f*GdxMeinf.HEIGHT-myBtn.getHeight()/2);
        exitBtnPos=new Vector2(10,GdxMeinf.HEIGHT-exitBtn.getHeight()-10);

        easyBound = new Rectangle(easyBtnPos.x, easyBtnPos.y, myBtn.getWidth(), myBtn.getHeight());
        medBound = new Rectangle(medBtnPos.x, medBtnPos.y, myBtn.getWidth(), myBtn.getHeight());
        hardBound = new Rectangle(hardBtnPos.x, hardBtnPos.y, myBtn.getWidth(), myBtn.getHeight());
        exitBound = new Rectangle(exitBtnPos.x, exitBtnPos.y, exitBtn.getWidth(), exitBtn.getHeight());

        music = Gdx.audio.newMusic(Gdx.files.internal("menu.mp3"));
        music.setLooping(true);
        music.setVolume(0.7f);
        music.play();

        FreeTypeFontGenerator generator=new FreeTypeFontGenerator(Gdx.files.internal("megrim.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter params=new FreeTypeFontGenerator.FreeTypeFontParameter();
        params.size=fontSize;
        params.color=Color.WHITE;
        myFont=generator.generateFont(params);
        myFont.setColor(0.984f, 0.98f, 0.97f,1);
        generator.dispose();
    }

    @Override
    public void handleInput() {
        if(Gdx.input.justTouched()){
            if(easyBound.contains(Gdx.input.getX() , GdxMeinf.HEIGHT-Gdx.input.getY())) {
                diffChosen=0;
                isClicked = true;
            }else if(medBound.contains(Gdx.input.getX() , GdxMeinf.HEIGHT-Gdx.input.getY())){
                diffChosen=1;
                isClicked = true;
            }else if(hardBound.contains(Gdx.input.getX() , GdxMeinf.HEIGHT-Gdx.input.getY())){
                diffChosen=2;
                isClicked = true;
            }else if(exitBound.contains(Gdx.input.getX() , GdxMeinf.HEIGHT-Gdx.input.getY())){
                Gdx.app.exit();
            }
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();

        sb.draw(background, 0,0,GdxMeinf.WIDTH,GdxMeinf.HEIGHT);

        sb.setColor(1,1,1,titleAlpha);
        sb.draw(myBtn,easyBtnPos.x,easyBtnPos.y);
        sb.draw(myBtn,medBtnPos.x, medBtnPos.y);
        sb.draw(myBtn, hardBtnPos.x, hardBtnPos.y);
        sb.draw(exitBtn, exitBtnPos.x, exitBtnPos.y);
        myFont.setColor(0.984f, 0.98f, 0.97f,titleAlpha);
        myFont.draw(sb,"E",0.25f*GdxMeinf.WIDTH-fontSize/4,0.3f*GdxMeinf.HEIGHT+fontSize/3);
        myFont.draw(sb,"M",0.5f*GdxMeinf.WIDTH-fontSize/4,0.3f*GdxMeinf.HEIGHT+fontSize/3);
        myFont.draw(sb,"H",0.75f*GdxMeinf.WIDTH-fontSize/4,0.3f*GdxMeinf.HEIGHT+fontSize/3);

        sb.draw(title1,titlePos1,0,GdxMeinf.WIDTH,GdxMeinf.HEIGHT);
        sb.draw(title2,titlePos2,0,GdxMeinf.WIDTH,GdxMeinf.HEIGHT);
        if(titlePos1<0){
            titlePos1+=(0.002f*GdxMeinf.WIDTH);
        }
        if(titlePos2>0){
            titlePos2-=(0.002f*GdxMeinf.WIDTH);
        }
        if(titleAlpha<0.99f){
            titleAlpha+=0.02f;
        }
        if(!isClicked) {
            sb.setColor(1, 1, 1, 1f - titleAlpha);
            sb.draw(fadeScreen, 0, 0, GdxMeinf.WIDTH, GdxMeinf.HEIGHT);
        }else{
            if(fadeAlpha<1f){
                fadeAlpha+=0.05f;
            }else{
                gsm.set(new PlayState(gsm,diffChosen));
            }
            sb.setColor(1, 1, 1, fadeAlpha);
            sb.draw(fadeScreen, 0, 0, GdxMeinf.WIDTH, GdxMeinf.HEIGHT);
        }
        sb.setColor(1,1,1,1);

        sb.end();
    }

    @Override
    public void dispose() {
        background.dispose();
        title1.dispose();
        title2.dispose();
        myBtn.dispose();
        exitBtn.dispose();
        myFont.dispose();
        fadeScreen.dispose();
        music.dispose();
    }
}
