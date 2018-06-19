package com.mygdx.meinf.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.meinf.GdxMeinf;
import com.mygdx.meinf.gameobject.Human;
import com.mygdx.meinf.gameobject.Path;
import com.mygdx.meinf.gameobject.Tile;

import java.util.ArrayList;
import java.util.Random;

public class PlayState extends State{

    /* Texture Positioning Constants */
    public static final float FRONTVIEWOFFSET=12f;
    public static final float LOWERINGTILE=0f;
    public static final float SHIFTINGTILE=0f;
    public static final int TOTALAREA=5;
    public static final int STARTINGPOSINTILE=1;

    /* Look Up Constants */
    public static final Path[] PATHTEMPLATE={new Path(1,2,"12.png") ,
                                            new Path(3,4,"34.png") ,
                                            new Path(1,3,"13.png") ,
                                            new Path(2,4,"24.png") ,
                                            new Path(1,4,"14.png") ,
                                            new Path(2,3,"23.png")};
    public static final ArrayList<ArrayList<Path>> TILEPATHTEMPLATE=new ArrayList<ArrayList<Path>>();
    public static final int NUMTILEPATHTEMPLATE=3;
    public static final int[] GOALDAYS={30,40,50};
    public static final String[] WARNINGTEMPLATE={"he is your only follower, please don't kill him",
                                                "he wants a tour around the world, not a tour to the afterlife",
                                                "error 404 prayer for demise not found",
                                                "dude, this is not a smasher game",
                                                "he asks for guidance, not violence"};
    public static final String[] STARVINGTEMPLATE={"why make him thinner when he can get more dinner",
                                                    "death by starvation is slow - mary austin",
                                                    "can't you just share your goddamn snicker bars?",
                                                    "wtf = where's the food"};
    public static final String[] FLATTEMPLATE={"it always hurts when you lose a secret - elie wiesel",
                                                "flat earth society would be proud of you",
                                                "nobody must know the secret of my box, spongebob. not even.. squidward's house",
                                                "common sense is what tells us the earth is flat - einstein"};
    public static final String[] WINNINGTEMPLATE={"number of satisfied follower : 1",
                                                "new skill mastered : road construction",
                                                "1 prayer successfully answered, only 99 to go",
                                                "play to win, but enjoy the fun - david ogilvy"};

    /* Background */
    private Texture daybg;
    private Texture nightbg;
    private Sprite daySprite;
    private Sprite nightSprite;
    private float myAlpha;
    private boolean toNight;
    public Texture gameOverBG;
    public Sprite gameOverSprite;
    public Texture gameOverTitle1;
    public Texture gameOverTitle2;
    public float gotPos1;
    public float gotPos2;
    public float gotAlpha;
    public float daynightSpeed;
    public float distanceToTarget;

    /* Play Area */
    public Tile[][] tiles;
    public Vector2 topleftPos;
    public Vector2 tileSize;
    public Human explorer;
    public Vector2 explorerSize;
    public float timer;
    public int gameOverCode; // 0=running 1=lose by starving 2=lose by flat earth 3=win 4=paused
    public static int gameDifficulty;
    public int totalDays;
    public int remainingDays;

    /*Music & Sound Effect*/
    public Music music;
    //public Sound soundpath;

    /* UI Area */
    public ArrayList<Tile> nextTile;
    public Tile newRandomTile;
    public Vector2 nextTileStartingPos;
    public boolean isRunning;
    public Random random=new Random();
    public Texture selector;
    public Vector2 selectorPos;
    public float floatingEffect;
    public boolean isIncrement;
    public float nextTileFloatingEffect;
    public float floatingEffectTimer;
    public BitmapFont myFont;
    public int fontSize=24;
    public int fontSizeGO=20;
    public int FONTOFFSET=5;
    public int STRINGOFFSET=3;
    public String totalDaysString;
    public String passedDaysString;
    public String remainingDaysString;
    public String warningString;
    public String gameOverString;

    public float warningTimer;
    public Color darkColor=new Color(0.082f,0.196f,0.263f,1);
    public Color lightColor=new Color(0.984f, 0.98f, 0.97f,1);
    public boolean isGameStarted;
    public float fadeAlpha;
    public BitmapFont gameOverFont;
    public Texture pauseBtn;
    public Vector2 pausePos;
    public Rectangle pauseBound;
    public Texture gamePaused;
    public float nextTileSpeed;
    public float nextTileDistance;
    public float nextTileIncrementValue;
    public boolean isAnimated=false;

    public Texture myBtn;
    public Vector2 resumeBtnPos;
    public Vector2 menuBtnPos;
    public Vector2 backBtnPos;
    public Rectangle resumeBound;
    public Rectangle menuBound;
    public Rectangle backBound;

    public PlayState(GameStateManager gsm, int difficulty) { //0=easy 1=medium 2=hard
        super(gsm);

        cam.setToOrtho(false,GdxMeinf.WIDTH,GdxMeinf.HEIGHT);

        CreateTemplates();
        InitGameVariables();
        RandomizeStartingNextTiles();

        music = Gdx.audio.newMusic(Gdx.files.internal("play.mp3"));
        music.setLooping(true);
        music.setVolume(0.7f);
        music.play();
        //soundpath = Gdx.audio.newSound(Gdx.files.internal("path.mp3"));

        myAlpha=0.05f;
        fadeAlpha=1f;
        toNight=true;

        isRunning=false;
        isGameStarted=false;
        timer=0f;
        gameOverCode=0;
        gameDifficulty=difficulty;
        totalDays=GOALDAYS[gameDifficulty];
        remainingDays=totalDays;
        totalDaysString="your followers desires a "+totalDays+"-days journey";
        passedDaysString=totalDays-remainingDays+" days have passed";
        remainingDaysString=remainingDays+" days to go";

        floatingEffect=0f;
        isIncrement=true;
        floatingEffectTimer=0f;

        FreeTypeFontGenerator generator=new FreeTypeFontGenerator(Gdx.files.internal("megrim.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter params=new FreeTypeFontGenerator.FreeTypeFontParameter();
        params.size=fontSize;
        params.color=Color.WHITE;
        myFont=generator.generateFont(params);
        myFont.setColor(darkColor);
        params.size=fontSizeGO;
        gameOverFont=generator.generateFont(params);
        generator.dispose();

        selector=new Texture("selector.png");
        selectorPos=new Vector2(nextTile.get(0).pos.x-(selector.getWidth()-nextTile.get(0).texture.getWidth())*0.5f , nextTile.get(0).pos.y-(selector.getHeight()-nextTile.get(0).texture.getHeight())*0.5f);

        daybg = new Texture("daybg.jpg");
        nightbg = new Texture("nightbg.jpg");
        gameOverBG=new Texture("gameOverBG.png");
        gameOverTitle1=new Texture("gameOverTitle1.png");
        gameOverTitle2=new Texture("gameOverTitle2.png");
        gamePaused=new Texture("gamePaused.png");
        myBtn = new Texture("btn.png");
        daySprite = new Sprite(daybg);
        nightSprite = new Sprite(nightbg);
        gameOverSprite=new Sprite(gameOverBG);
        daySprite.setSize(GdxMeinf.WIDTH,GdxMeinf.HEIGHT);
        nightSprite.setSize(GdxMeinf.WIDTH,GdxMeinf.HEIGHT);
        gameOverSprite.setSize(GdxMeinf.WIDTH,GdxMeinf.HEIGHT);
        nightSprite.setAlpha(myAlpha);
        gameOverSprite.setAlpha(0.97f);

        gotPos1=0-0.1f*GdxMeinf.WIDTH;
        gotPos2=0+0.1f*GdxMeinf.WIDTH;
        gotAlpha=0f;
        warningTimer=-1f;

        pauseBtn=new Texture("pause.png");
        pausePos=new Vector2(10,GdxMeinf.HEIGHT-pauseBtn.getHeight()-10);
        pauseBound = new Rectangle(pausePos.x, pausePos.y, pauseBtn.getWidth(), pauseBtn.getHeight());

        resumeBtnPos=new Vector2(0.35f*GdxMeinf.WIDTH-myBtn.getWidth()/2 , 0.5f*GdxMeinf.HEIGHT-myBtn.getHeight()/2);
        menuBtnPos=new Vector2(0.65f*GdxMeinf.WIDTH-myBtn.getWidth()/2 , 0.5f*GdxMeinf.HEIGHT-myBtn.getHeight()/2);
        backBtnPos=new Vector2(0.5f*GdxMeinf.WIDTH-myBtn.getWidth()/2 , 0.4f*GdxMeinf.HEIGHT-myBtn.getHeight()/2);
        resumeBound=new Rectangle(resumeBtnPos.x, resumeBtnPos.y, myBtn.getWidth(), myBtn.getHeight());
        menuBound=new Rectangle(menuBtnPos.x, menuBtnPos.y, myBtn.getWidth(), myBtn.getHeight());
        backBound=new Rectangle(backBtnPos.x, backBtnPos.y, myBtn.getWidth(), myBtn.getHeight());

        nextTileSpeed = nextTileDistance * explorer.TOTALMOVESPEED / Math.abs(tiles[2][2].points.get(1).y-tiles[2][2].points.get(2).y);
    }


    /*_____________________StartAbstractArea________________________*/
    @Override
    protected void handleInput() {
        if(Gdx.input.justTouched() && !explorer.isMoving && !isRunning && !isAnimated) { // JIKA ANIMASI GERAKAN NEXTTILE BLM SELESAI,BELUM BISA KLIK LAGI
            isRunning=true;
            Vector3 tmp=cam.unproject(new Vector3(Gdx.input.getX(),Gdx.input.getY(),0));

            //if(pauseBound.contains(Gdx.input.getX() , GdxMeinf.HEIGHT-Gdx.input.getY())){
            if(pauseBound.contains(tmp.x ,tmp.y)){
                gameOverCode=4;
            }else {
                Vector2 clickedTileIndex = new Vector2();
                boolean isFound = false;

                myOuterLoop:
                for (int i = 0; i < TOTALAREA; i++) {
                    for (int j = 0; j < TOTALAREA; j++) {
                        //if (tiles[i][j].bounds.contains(Gdx.input.getX(), GdxMeinf.HEIGHT - Gdx.input.getY())) {
                        if(tiles[i][j].bounds.contains(tmp.x ,tmp.y)){
                            clickedTileIndex = new Vector2(j, i);
                            if (clickedTileIndex.x == explorer.tileIndex.x && clickedTileIndex.y == explorer.tileIndex.y) {
                                isFound = false;
                                warningString = WARNINGTEMPLATE[random.nextInt(WARNINGTEMPLATE.length)];
                                warningTimer = 0f;
                            } else {
                                isFound = true;
                            }
                            break myOuterLoop;
                        }
                    }
                }

                if (isFound) {
                    nextTileIncrementValue=0f;
                    isAnimated=true;
                    //soundpath.play(0.5f);
                    tiles[(int) clickedTileIndex.y][(int) clickedTileIndex.x].setTile(nextTile.get(0));
                    if (explorer.CheckMoveNewTile(tiles[(int) clickedTileIndex.y][(int) clickedTileIndex.x])) {
                        distanceToTarget = explorer.target.x - explorer.position.x != 0 ? explorer.target.x - explorer.position.x : explorer.target.y - explorer.position.y;
                        daynightSpeed = 1.8f * explorer.actualSpeed / Math.abs(distanceToTarget);
                    }
                }
            }
            isRunning=false;
        }
    }

//    public void addTouchListener(){
//
//    }

    public void handlePauseInput() {
        if(Gdx.input.justTouched()){
            if(resumeBound.contains(Gdx.input.getX() , GdxMeinf.HEIGHT-Gdx.input.getY())){
                gameOverCode=0;
            }else if(menuBound.contains(Gdx.input.getX() , GdxMeinf.HEIGHT-Gdx.input.getY())){
                gsm.set(new MenuState(gsm));
            }
        }
    }

    public void handleGOInput() {
        if(Gdx.input.justTouched()){
            if(backBound.contains(Gdx.input.getX() , GdxMeinf.HEIGHT-Gdx.input.getY())){
                gsm.set(new MenuState(gsm));
            }
        }
    }

    @Override
    public void update(float dt) {
        if(isGameStarted) {
            if (gameOverCode == 0) {
                handleInput();

                timer += dt;
                if (timer > 0.5f) {
                    timer = 0f;
                    explorer.UpdateHunger();
                    if (explorer.hunger <= 0) {
                        explorer.hunger = 0;
                        gameOverString=STARVINGTEMPLATE[random.nextInt(STARVINGTEMPLATE.length)];
                        gameOverCode = 1;
                    }
                }
                floatingEffectTimer += dt;
                if (floatingEffectTimer > 0.15f) {
                    floatingEffectTimer = 0f;
                    floatingEffect = isIncrement ? floatingEffect + 1 : floatingEffect - 1;
                    isIncrement = floatingEffect >= 3 ? false : isIncrement;
                    isIncrement = floatingEffect <= -3 ? true : isIncrement;
                }

                if (warningTimer != -1) {
                    warningTimer += dt;
                    if (warningTimer > 2f) {
                        warningTimer = -1;
                    }
                }

                if(isAnimated) {
                    nextTileIncrementValue += nextTileSpeed;
                    if(nextTileIncrementValue<=nextTileDistance){
                        isAnimated=false;
                        RandomizeNewNextTile();
                    }
                }

                if (explorer.isMoving) {
                    if(myAlpha>=0.95f){
                        toNight=false;
                    }else if(myAlpha<=0.05f){
                        toNight=true;
                    }
                    myAlpha=toNight?myAlpha+daynightSpeed:myAlpha-daynightSpeed;
                    nightSprite.setAlpha(myAlpha);

                    explorer.Move(dt);
                    if (!explorer.isMoving) {
                        myAlpha=0.05f;
                        tiles[(int) explorer.tileIndex.y][(int) explorer.tileIndex.x].paths.remove(explorer.currentPath);
                        remainingDays -= 1;
                        passedDaysString = totalDays - remainingDays + " days have passed";
                        remainingDaysString = remainingDays + " days to go";
                        if (CheckWorldEnd()) {
                            if(explorer.CheckMoveNeighborTile(tiles[(int) (explorer.tileIndex.y + explorer.LINKLOOKUP[explorer.posInTile].y)][(int) (explorer.tileIndex.x + explorer.LINKLOOKUP[explorer.posInTile].x)])){
                                distanceToTarget = explorer.target.x-explorer.position.x!=0 ? explorer.target.x-explorer.position.x:explorer.target.y-explorer.position.y;
                                daynightSpeed=1.8f * explorer.actualSpeed / Math.abs(distanceToTarget);
                            }
                        } else {
                            gameOverString=FLATTEMPLATE[random.nextInt(FLATTEMPLATE.length)];
                            gameOverCode = 2;
                        }
                        if (remainingDays == 0) {
                            gameOverString=WINNINGTEMPLATE[random.nextInt(WINNINGTEMPLATE.length)];
                            gameOverCode = 3;
                        }
                    }
                }
            }else{
                if(gameOverCode==4) {
                    handlePauseInput();
                }else{
                    handleGOInput();
                }
            }
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();

        daySprite.draw(sb);
        nightSprite.draw(sb);

        sb.setColor(1, 1f-myAlpha/2, 1, 1f);
        for(int i=0; i<TOTALAREA; i++){
            for(int j=0; j<TOTALAREA; j++){
                if(i==explorer.tileIndex.y && j==explorer.tileIndex.x){
                    sb.setColor(0.375f, 0.49f, 0.545f, 1f);
                    tiles[i][j].drawTile(sb,floatingEffect);
                    sb.setColor(1, 1f-myAlpha/2, 1f, 1f);
                }else {
                    tiles[i][j].drawTile(sb,floatingEffect);
                }
            }
        }
        if(isAnimated) {
            for (int i = 1; i < NUMTILEPATHTEMPLATE; i++) {
                nextTile.get(i).drawAnimatedNextTile(sb, nextTileIncrementValue);
            }
        }else{
            for (int i = 0; i < NUMTILEPATHTEMPLATE; i++) {
                nextTileFloatingEffect = (float) (floatingEffect * Math.pow(-1.0, i + 1));
                nextTile.get(i).drawNextTile(sb, nextTileFloatingEffect);
            }
        }
        sb.setColor(1, 1, 1, 1f);

        sb.draw(selector, selectorPos.x, selectorPos.y+floatingEffect);
        sb.draw(pauseBtn, pausePos.x, pausePos.y);
        explorer.drawExplorer(sb);

        myFont.draw(sb , totalDaysString , GdxMeinf.WIDTH/2-totalDaysString.length()*fontSize/4, GdxMeinf.HEIGHT-FONTOFFSET);
        myFont.draw(sb , passedDaysString , GdxMeinf.WIDTH/2-passedDaysString.length()*fontSize/4, GdxMeinf.HEIGHT-fontSize-STRINGOFFSET-FONTOFFSET);
        myFont.draw(sb , remainingDaysString , GdxMeinf.WIDTH/2-remainingDaysString.length()*fontSize/4, GdxMeinf.HEIGHT-2*fontSize-2*STRINGOFFSET-FONTOFFSET);

        if(warningTimer!=-1){
            myFont.setColor(lightColor);
            myFont.draw(sb , warningString , GdxMeinf.WIDTH/2-warningString.length()*fontSize/4, FONTOFFSET+fontSize);
            myFont.setColor(darkColor);
        }

        if(gameOverCode!=0){
            gameOverSprite.draw(sb);
            sb.setColor(1,1,1,gotAlpha);
            sb.draw(gameOverTitle1,gotPos1,0,GdxMeinf.WIDTH,GdxMeinf.HEIGHT);
            if(gameOverCode!=4) {
                sb.draw(gameOverTitle2, gotPos2, 0, GdxMeinf.WIDTH, GdxMeinf.HEIGHT);
                sb.draw(myBtn, backBtnPos.x, backBtnPos.y);
                gameOverFont.setColor(0.984f, 0.98f, 0.97f, gotAlpha);
                gameOverFont.draw(sb, gameOverString, GdxMeinf.WIDTH / 2 - (gameOverString.length() * fontSizeGO) / 4, GdxMeinf.HEIGHT / 2 + fontSizeGO / 2);
                gameOverFont.draw(sb, "MENU", backBtnPos.x + (4 * fontSizeGO) / 4, backBtnPos.y + myBtn.getHeight()/2 + fontSizeGO/2);
            }else{
                sb.draw(gamePaused, gotPos2, 0, GdxMeinf.WIDTH, GdxMeinf.HEIGHT);
                sb.draw(myBtn, resumeBtnPos.x, resumeBtnPos.y);
                sb.draw(myBtn, menuBtnPos.x, menuBtnPos.y);
                gameOverFont.setColor(0.984f, 0.98f, 0.97f, gotAlpha);
                gameOverFont.draw(sb, "RESUME", resumeBtnPos.x + (3 * fontSizeGO) / 4, resumeBtnPos.y + myBtn.getHeight()/2 + fontSizeGO/2);
                gameOverFont.draw(sb, "MENU", menuBtnPos.x + (4 * fontSizeGO) / 4, menuBtnPos.y + myBtn.getHeight()/2 + fontSizeGO/2);
            }
            if(gotPos1<0){
                gotPos1+=(0.002f*GdxMeinf.WIDTH);
            }
            if(gotPos2>0){
                gotPos2-=(0.002f*GdxMeinf.WIDTH);
            }
            if(gotAlpha<0.99f){
                gotAlpha+=0.02f;
            }
            sb.setColor(1,1,1,1);
        }

        if(!isGameStarted){
            if(fadeAlpha>0f){
                fadeAlpha-=0.05f;
            }else{
                isGameStarted=true;
            }
            sb.setColor(1,1,1,fadeAlpha);
            sb.draw(gameOverBG,0,0,GdxMeinf.WIDTH,GdxMeinf.HEIGHT);
        }

        sb.end();
    }

    @Override
    public void dispose() {
        /* MASIH BANYAK YANG BELUM DIDISPOSE !!!!! */
        daySprite.getTexture().dispose();
        nightSprite.getTexture().dispose();
        gameOverSprite.getTexture().dispose();
        gameOverTitle1.dispose();
        gameOverTitle2.dispose();
        gamePaused.dispose();
        for(int i=0; i<TOTALAREA; i++) {
            for (int j = 0; j < TOTALAREA; j++) {
                tiles[i][j].texture.dispose();
                tiles[i][j].textureFood.dispose();
            }
        }
        for(int i=0; i<NUMTILEPATHTEMPLATE; i++){
            nextTile.get(i).texture.dispose();
            nextTile.get(i).textureFood.dispose();
        }
        newRandomTile.texture.dispose();
        newRandomTile.textureFood.dispose();
        explorer.textureHuman.dispose();
        selector.dispose();
        myFont.dispose();
        gameOverFont.dispose();
        pauseBtn.dispose();
        myBtn.dispose();
        music.dispose();
        //soundpath.dispose();
        //explorer.soundwalk.dispose();
        TILEPATHTEMPLATE.clear();
    }
    /*_____________________EndAbstractArea________________________*/


/*-----------------------------------------------------------------------------------------------------------------------*/


    /*_____________________StartFunctionArea________________________*/
    public void CreateTemplates(){
        for(int i=0; i<NUMTILEPATHTEMPLATE; i++) {
            TILEPATHTEMPLATE.add(new ArrayList<Path>());
        }

        /* Plus Shape */
        TILEPATHTEMPLATE.get(0).add(new Path(PATHTEMPLATE[0]));
        TILEPATHTEMPLATE.get(0).add(new Path(PATHTEMPLATE[1]));

        /* Diagonal TopRight-BottomLeft */
        TILEPATHTEMPLATE.get(1).add(new Path(PATHTEMPLATE[2]));
        TILEPATHTEMPLATE.get(1).add(new Path(PATHTEMPLATE[3]));

        /* Diagonal TopRight-BottomLeft */
        TILEPATHTEMPLATE.get(2).add(new Path(PATHTEMPLATE[4]));
        TILEPATHTEMPLATE.get(2).add(new Path(PATHTEMPLATE[5]));
    }

    public void InitGameVariables(){
        explorer=new Human();
        explorerSize=new Vector2(explorer.textureHuman.getWidth()/8, explorer.textureHuman.getHeight()/8);

        tiles=new Tile[TOTALAREA][TOTALAREA];
        tiles[0][0]=new Tile(); //CARA BRUTAL, G ELEGAN
        tileSize=new Vector2(tiles[0][0].texture.getWidth(),tiles[0][0].texture.getHeight()-FRONTVIEWOFFSET);
        topleftPos=new Vector2((GdxMeinf.WIDTH / 2)-((((float)TOTALAREA)/2)*tileSize.x)-SHIFTINGTILE , (GdxMeinf.HEIGHT / 2)+((((float)TOTALAREA)/2 - 1f)*tileSize.y)-LOWERINGTILE);
        for(int i=0; i<TOTALAREA; i++){
            for(int j=0; j<TOTALAREA; j++){
                tiles[i][j]=new Tile(new Vector2(j,i) , new Vector2(topleftPos.x+j*tileSize.x , topleftPos.y-i*tileSize.y) , explorerSize);
            }
        }
        explorer.setRemainingVar(tiles[TOTALAREA/2][TOTALAREA/2].points.get(STARTINGPOSINTILE) , new Vector2(TOTALAREA/2,TOTALAREA/2) , STARTINGPOSINTILE, gameDifficulty);
    }

    public void RandomizeStartingNextTiles(){
        nextTile=new ArrayList<Tile>();
        newRandomTile=new Tile();
        float startingPercentPositioning=0.25f;
        float endingPercentPositioning=0.75f;
        float offsetPercentPositioning=(startingPercentPositioning-endingPercentPositioning)/(NUMTILEPATHTEMPLATE-1);

        /* NOTE : JUMLAH NEXTTILE YANG DISEDIAKAN DISAMAKAN DENGAN JUMLAH TIPE TILEPATH + LIMIT RANDOM FOOD MUNGKIN MENDING DIBUAT VARIABEL?? */
        for(int i=0; i<NUMTILEPATHTEMPLATE; i++){
            /* FATAL SEKALI !!!!!!!!!!!!!!! KEMUNGKINAN FOOD UNTUK 0 SEHARUSNYA JAUH LEBIH TINGGI DARIPADA ADA FOODNYA, SHINGGA TIDAK BISA DIRANDOM BIASA, HRS PAKAI PROBABILITY */
            nextTileStartingPos=new Vector2(0.85f * ((float)GdxMeinf.WIDTH) , (startingPercentPositioning-offsetPercentPositioning*i) * ((float)GdxMeinf.HEIGHT) + i * 1f/(NUMTILEPATHTEMPLATE-1) * (tileSize.y+FRONTVIEWOFFSET) - (tileSize.y+FRONTVIEWOFFSET));
            nextTile.add(new Tile(nextTileStartingPos , TILEPATHTEMPLATE.get(random.nextInt(TILEPATHTEMPLATE.size())) , RandomizeFoods() , explorerSize));
        }
        nextTileDistance=nextTile.get(0).pos.y-nextTile.get(1).pos.y;
    }

    public void RandomizeNewNextTile(){
        newRandomTile.texture.dispose();
        newRandomTile.textureFood.dispose();
        newRandomTile=new Tile(new Vector2() , TILEPATHTEMPLATE.get(random.nextInt(TILEPATHTEMPLATE.size())) , RandomizeFoods() , explorerSize);
        for(int i=0; i<nextTile.size()-1; i++){
            nextTile.get(i).setTile(nextTile.get(i+1));
        }
        nextTile.get(nextTile.size()-1).setTile(newRandomTile);
    }

    public boolean CheckWorldEnd(){
        if(explorer.tileIndex.x==0 && explorer.posInTile==3){
            return false;
        }
        if(explorer.tileIndex.x==TOTALAREA-1 && explorer.posInTile==4){
            return false;
        }
        if(explorer.tileIndex.y==0 && explorer.posInTile==1){
            return false;
        }
        if(explorer.tileIndex.y==TOTALAREA-1 && explorer.posInTile==2){
            return false;
        }
        return true;
    }

    public int RandomizeFoods(){
        int randomFoodProb=random.nextInt(100);
        if(randomFoodProb<55){
            return 0;
        }else if(randomFoodProb<70){
            return 1;
        }else{
            return 2;
        }

    }
    /*_____________________EndFunctionArea________________________*/
}
