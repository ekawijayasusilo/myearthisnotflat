package com.mygdx.meinf.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.meinf.GdxMeinf;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width=GdxMeinf.WIDTH;
		config.height=GdxMeinf.HEIGHT;
		new LwjglApplication(new GdxMeinf(), config);
	}
}
