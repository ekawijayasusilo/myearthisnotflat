package com.mygdx.meinf.gameobject;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.meinf.GdxMeinf;
import com.mygdx.meinf.states.PlayState;

public class Human {
    public static final float TOTALMOVESPEED=1f;
    public static final int[] PATHSET={0,2,1,4,3};
    public static final double[] HUNGERMAXSPEED={3.0 , 4.0 , 5.0}; // easy maxspeed=3, medium maxspeed=4, hard maxspeed=5
    public static final Vector2[] LINKLOOKUP={new Vector2() , new Vector2(0,-1) , new Vector2(0,1) , new Vector2(-1,0) , new Vector2(1,0)};
    public static final int [][] ANMLOOKUP={{6,7,3},{4,8,1},{5,0,2}};

    public boolean isMoving;
    public Vector2 target;
    public int targetPosInTile;
    public int xDir;
    public int yDir;
    public float targetRange;
    public Path currentPath;
    public float actualSpeed;

    public Vector2 position;
    public Vector2 tileIndex;
    public int posInTile;

    public Texture textureHuman;
    public HumanAnimation humanAnimation;

    public double hunger;
    public double hungerSpeed;
    public double hungerMultiplier;
    public double maxHungerSpeed;
    public Texture textureHunger;
    public Texture textureFrame;
    public Vector2 hungerPos;
    public Vector2 framePos;

    //public Sound soundwalk;

    public Human(){
        textureHuman=new Texture("explorers.png");
        humanAnimation = new HumanAnimation(new TextureRegion(textureHuman),8,8,0.5f);
        textureHunger=new Texture("hunger.png");
        textureFrame=new Texture("hungerframe.png");
        hungerPos=new Vector2(GdxMeinf.WIDTH*0.1f , GdxMeinf.HEIGHT*0.5f-textureHunger.getHeight()*0.5f);
        framePos=new Vector2(hungerPos.x, GdxMeinf.HEIGHT*0.5f-textureFrame.getHeight()*0.5f);

        //soundwalk = Gdx.audio.newSound(Gdx.files.internal("walk.mp3"));

        isMoving=false;
        target=new Vector2(0f,0f);
        targetRange=1f;

        hunger=100.0;
        hungerSpeed=0.5;
        hungerMultiplier=1.08;
    }

    public void setRemainingVar(Vector2 position, Vector2 tileIndex, int posInTile, int gameDifficulty){
        this.position=new Vector2(position);
        this.tileIndex=new Vector2(tileIndex); //SEHARUSNYA SEMUA YANG MEMANGGIL INI DI PLAYSTATE SUDAH PAKAI VECTOR BARU, TAPI UNTUK AMANNYA DINEW LAGI
        this.posInTile=posInTile;
        this.maxHungerSpeed=HUNGERMAXSPEED[gameDifficulty];
    }

    public void UpdateHunger(){
        hunger-=hungerSpeed;
    }
    public void MultiplyHunger(){
        if(hungerSpeed<HUNGERMAXSPEED[PlayState.gameDifficulty]){
            hungerSpeed*=hungerMultiplier;
        }
    }

    public boolean CheckMoveNewTile(Tile newTile){
        boolean checkResult;
        checkResult=newTile.tileIndex.x==tileIndex.x+LINKLOOKUP[posInTile].x && newTile.tileIndex.y==tileIndex.y+LINKLOOKUP[posInTile].y ? true:false;
        if(checkResult) {
            posInTile = PATHSET[posInTile];
            position = new Vector2(newTile.points.get(posInTile));
            tileIndex = new Vector2(newTile.tileIndex);
            for (Path i : newTile.paths) {
                if (posInTile != 4) {
                    if (i.firstPath == posInTile) {
                        target = new Vector2(newTile.points.get(i.secondPath));
                        targetPosInTile=i.secondPath;
                        currentPath=i;
                        break;
                    }
                }
                if (posInTile != 1) {
                    if (i.secondPath == posInTile) {
                        target = new Vector2(newTile.points.get(i.firstPath));
                        targetPosInTile=i.firstPath;
                        currentPath=i;
                        break;
                    }
                }
            }

            xDir=(int)((target.x - position.x) / Math.abs(target.x - position.x));
            yDir=(int)((target.y - position.y) / Math.abs(target.y - position.y));
            humanAnimation.setFrameY(ANMLOOKUP[xDir+1][yDir+1]);
            actualSpeed=TOTALMOVESPEED/(Math.abs(xDir)+Math.abs(yDir));
            if(newTile.foods>0){
                newTile.foods-=1;
                hunger=100.0;
            }

        }

        isMoving=checkResult; // Pergantian value isMoving harus paling akhir
        return checkResult;
    }

    public boolean CheckMoveNeighborTile(Tile neighborTile){
        boolean checkResult=false;
        for(Path i : neighborTile.paths){
            if(posInTile!=2) {
                if(i.secondPath == PATHSET[posInTile]){
                    checkResult=true;
                    target = new Vector2(neighborTile.points.get(i.firstPath));
                    targetPosInTile=i.firstPath;
                    currentPath=i;
                }
            }
            if(posInTile!=3){
                if(i.firstPath == PATHSET[posInTile]){
                    checkResult=true;
                    target = new Vector2(neighborTile.points.get(i.secondPath));
                    targetPosInTile=i.secondPath;
                    currentPath=i;
                }
            }
        }
        if(checkResult){
            posInTile = PATHSET[posInTile];
            position = new Vector2(neighborTile.points.get(posInTile));
            tileIndex = new Vector2(neighborTile.tileIndex);
            xDir=(int)((target.x - position.x) / Math.abs(target.x - position.x));
            yDir=(int)((target.y - position.y) / Math.abs(target.y - position.y));
            humanAnimation.setFrameY(ANMLOOKUP[xDir+1][yDir+1]);
            actualSpeed=TOTALMOVESPEED/(Math.abs(xDir)+Math.abs(yDir));
            if(neighborTile.foods>0){
                neighborTile.foods-=1;
                hunger=100.0;
            }
        }

        isMoving=checkResult; // Pergantian value isMoving harus paling akhir
        return checkResult;
    }

    public void Move(float dt){
        //soundwalk.play(0.5f);
        humanAnimation.update(dt);
        if(position.x!=target.x) {
            position.x += xDir * actualSpeed;
        }
        if(position.y!=target.y) {
            position.y += yDir * actualSpeed;
        }
        if(position.x<=target.x+targetRange && position.x>=target.x-targetRange && position.y<=target.y+targetRange && position.y>=target.y-targetRange){
            posInTile=targetPosInTile;
            position=new Vector2(target);
            MultiplyHunger();
            isMoving=false;
        }
    }

    public void drawExplorer(SpriteBatch sb){
        sb.draw(humanAnimation.getFrame(), position.x , position.y);
        if(hunger<25.0){
            sb.setColor(1f,0.32f,0.32f,1f);
            sb.draw(textureHunger, hungerPos.x, hungerPos.y, textureHunger.getWidth(), (float)(textureHunger.getHeight()*hunger/100.0));
            sb.draw(textureFrame, framePos.x, framePos.y);
            sb.setColor(1f,1f,1f,1f);
        }else{
            sb.draw(textureHunger, hungerPos.x, hungerPos.y, textureHunger.getWidth(), (float)(textureHunger.getHeight()*hunger/100.0));
            sb.draw(textureFrame, framePos.x, framePos.y);
        }

    }
}
