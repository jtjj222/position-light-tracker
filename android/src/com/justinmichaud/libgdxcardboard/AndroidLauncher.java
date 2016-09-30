package com.justinmichaud.libgdxcardboard;

import android.os.Bundle;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.justinmichaud.libgdxcardboard.CardboardGame;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        final CardboardGame game = new CardboardGame();

		initialize(new ApplicationAdapter() {
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
