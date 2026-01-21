package com.hoverhighlight;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class HoverHighlightPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(HoverHighlightPlugin.class);
		RuneLite.main(args);
	}
}