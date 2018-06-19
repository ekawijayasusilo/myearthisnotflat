package com.mygdx.meinf.gameobject;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.meinf.states.PlayState;

import java.util.ArrayList;

public class Tile {

    public Vector2 tileIndex;
    public Vector2 pos;

    public Texture texture;
    public Texture textureFood;

    public int foods;
    public float offsetFood;

    public ArrayList<Path> paths;
    public ArrayList<Vector2> points; // 0=mid 1=top 2=bottom 3=left 4=right

    public Rectangle bounds;

    public Tile() {
       texture = new Texture("land.png");
       textureFood = new Texture("water.png");
       offsetFood=12f; //baru asal isi
       foods=0;

       paths=new ArrayList<Path>();
       points=new ArrayList<Vector2>();
    }

    public Tile(Vector2 tileIndex, Vector2 pos, Vector2 es) {
        this();
        this.tileIndex = new Vector2(tileIndex); //SEHARUSNYA SEMUA YANG MEMANGGIL INI DI PLAYSTATE SUDAH PAKAI VECTOR BARU, TAPI UNTUK AMANNYA DINEW LAGI
        this.pos = new Vector2(pos); //SEHARUSNYA SEMUA YANG MEMANGGIL INI DI PLAYSTATE SUDAH PAKAI VECTOR BARU, TAPI UNTUK AMANNYA DINEW LAGI

        setAllPoints(es);

        bounds = new Rectangle(pos.x, pos.y+PlayState.FRONTVIEWOFFSET, texture.getWidth(), texture.getHeight()-PlayState.FRONTVIEWOFFSET);
    }

    public Tile(Vector2 pos, ArrayList<Path> paths, int foods, Vector2 es) {
        this();
        this.pos = new Vector2(pos); //SEHARUSNYA SEMUA YANG MEMANGGIL INI DI PLAYSTATE SUDAH PAKAI VECTOR BARU, TAPI UNTUK AMANNYA DINEW LAGI
        this.paths = new ArrayList<Path>(paths);
        this.foods = foods;

        setAllPoints(es);
    }

    public void setAllPoints(Vector2 es){
        points.add(new Vector2(pos.x+texture.getWidth()/2-es.x/2 ,pos.y+(texture.getHeight()-PlayState.FRONTVIEWOFFSET)/2 + PlayState.FRONTVIEWOFFSET - es.y*0.35f)); //mid
        points.add(new Vector2(points.get(0).x ,pos.y+texture.getHeight()-es.y*0.35f)); //top
        points.add(new Vector2(points.get(0).x , pos.y+ PlayState.FRONTVIEWOFFSET-es.y*0.35f)); //bottom
        points.add(new Vector2(pos.x-es.x/2, points.get(0).y));//left
        points.add(new Vector2(pos.x+texture.getWidth()-es.x/2 , points.get(0).y));//right
    }

    public void setTile(Tile other){
        this.paths = new ArrayList<Path>(other.paths);
        this.foods = other.foods;
    }

    public void drawTile(SpriteBatch sb, float floatingEffect){
        sb.draw(texture , pos.x , pos.y);
        for(int i=0; i<paths.size(); i++){
            sb.draw(paths.get(i).texture, pos.x, pos.y + PlayState.FRONTVIEWOFFSET);
        }
        for (int i=0; i<foods; i++){
            sb.draw(textureFood,pos.x+i*offsetFood - textureFood.getWidth()/2,pos.y + texture.getWidth() - textureFood.getHeight()/4 + floatingEffect + floatingEffect*0.8f);
        }
    }

    public void drawNextTile(SpriteBatch sb, float floatingEffect){
        sb.draw(texture , pos.x , pos.y + floatingEffect);
        for(int i=0; i<paths.size(); i++){
            sb.draw(paths.get(i).texture, pos.x, pos.y + PlayState.FRONTVIEWOFFSET + floatingEffect);
        }
        for (int i=0; i<foods; i++){
            sb.draw(textureFood,pos.x+i*offsetFood - textureFood.getWidth()/2,pos.y + texture.getWidth() - textureFood.getHeight()/4 + floatingEffect + floatingEffect*0.8f);
        }
    }

    public void drawAnimatedNextTile(SpriteBatch sb, float nextTileIncrementValue){
        sb.draw(texture , pos.x , pos.y + nextTileIncrementValue);
        for(int i=0; i<paths.size(); i++){
            sb.draw(paths.get(i).texture, pos.x, pos.y + PlayState.FRONTVIEWOFFSET + nextTileIncrementValue);
        }
        for (int i=0; i<foods; i++){
            sb.draw(textureFood,pos.x+i*offsetFood - textureFood.getWidth()/2,pos.y + texture.getWidth() - textureFood.getHeight()/4 + nextTileIncrementValue);
        }
    }

}
