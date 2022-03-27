package com.theme;

import com.formdev.flatlaf.FlatLightLaf;

public class FlatTheme
	extends FlatLightLaf
{
	public static final String NAME = "FlatTheme";

	public static boolean setup() {
		return setup( new FlatTheme() );
	}

	public static void installLafInfo() {
		installLafInfo( NAME, FlatTheme.class );
	}

	@Override
	public String getName() {
		return NAME;
	}
}
