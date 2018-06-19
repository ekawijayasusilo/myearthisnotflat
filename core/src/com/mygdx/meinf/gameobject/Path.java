package com.mygdx.meinf.gameobject;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class Path {
    public Texture texture;
    public Vector2 pos;
    public int firstPath;
    public int secondPath;

//    public Path() {
//
//    }

    public Path(int firstPath, int secondPath, String texture) {
        pos=new Vector2(); //DIBUTUHKAN SAAT CREATE TEMPLATE, KARENA PAKAI CONSTRUCTOR (PATH OTHER) SEHINGGA PERLU MENGCOPY POSNYA JUGA
        //CARA LAIN YG LEBIH ELEGAN???
        this.texture=new Texture(texture);
        this.firstPath = firstPath;
        this.secondPath = secondPath;
    }

    public Path(Path other){
        //this();
        this.texture=new Texture(other.texture.getTextureData());
        this.pos = new Vector2(other.pos);
        this.firstPath = other.firstPath;
        this.secondPath = other.secondPath;
    }
}
