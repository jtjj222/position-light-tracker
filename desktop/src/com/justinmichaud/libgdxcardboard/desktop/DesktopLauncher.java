package com.justinmichaud.libgdxcardboard.desktop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.justinmichaud.libgdxcardboard.CardboardGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		final CardboardGame game = new CardboardGame();

		new LwjglApplication(new ApplicationAdapter() {
			@Override
			public void create() {
				game.create();
			}

			@Override
			public void render() {
                game.render();
			}

			@Override
			public void dispose() {
                game.dispose();
			}
		}, config);
	}
}
