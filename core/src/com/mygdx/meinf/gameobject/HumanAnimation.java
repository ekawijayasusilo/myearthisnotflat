package com.mygdx.meinf.gameobject;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Verrell on 19/10/2017.
 */

public class HumanAnimation {
    public Array<Array<TextureRegion>>frames;
    private float maxFrameTime;
    private float currentFrameTime;
    private int frameCountHeight;
    private int frameCountWidth;
    private Vector2 frame;

    public HumanAnimation(TextureRegion region, int frameCountWidth, int frameCountHeight, float cycleTime){
        frames = new Array<Array<TextureRegion>>();
        int frameWidth=region.getRegionWidth()/frameCountWidth;
        int frameHeight=region.getRegionWidth()/frameCountHeight;
        for (int i=0; i<frameCountHeight; i++){
            frames.add(new Array<TextureRegion>());
        }
        for (int i=0; i<frameCountHeight; i++){
            for (int j=0; j<frameCountWidth; j++){
                frames.get(i).add(new TextureRegion(region, frameWidth*j, frameHeight*i, frameWidth, frameHeight));
            }
        }
        this.frameCountWidth=frameCountWidth;
        this.frameCountHeight=frameCountHeight;
        maxFrameTime=cycleTime/frameCountWidth;
        frame = new Vector2(0,1);
    }

    public void update(float dt){
        currentFrameTime+=dt;
        if (currentFrameTime>maxFrameTime){
            frame=new Vector2(frame.x+1,frame.y);
            currentFrameTime=0;
        }
        if (frame.x>=frameCountWidth){
            frame.x=0;
        }
    }

    public void setFrameY(int y){
        frame=new Vector2(frame.x,y);
    }

    public TextureRegion getFrame(){
        return frames.get((int)frame.y).get((int)frame.x);
    }
}