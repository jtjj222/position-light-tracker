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


		initialize(new ApplicationAdapter() {
			@Override
			public void create() {
				super.create();
			}

			@Override
			public void render() {
				super.render();
			}

			@Override
			public void dispose() {
				super.dispose();
			}
		}, config);
	}
}
