package com.npl5035.swprototype.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.npl5035.swprototype.SWPrototype;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new SWPrototype(), config);
		config.title = "Project NightHammer";
		//config.fullscreen = true;
		config.vSyncEnabled = true;
		config.width = 1920;
		config.height = 1080;


	}
}
