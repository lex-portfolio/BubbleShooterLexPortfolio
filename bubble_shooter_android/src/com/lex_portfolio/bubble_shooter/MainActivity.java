package com.lex_portfolio.bubble_shooter;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.lex_portfolio.engine.android.StandardAndroidInterface;

public class MainActivity extends AndroidApplication {

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new GameBubbleShooter(new StandardAndroidInterface(this)), config);
	}
}
