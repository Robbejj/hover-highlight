package com.hoverhighlight;

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

import java.awt.Color;

@ConfigGroup("hoverhighlight")
public interface HoverHighlightConfig extends Config
{
	@Alpha
	@ConfigItem(
		keyName = "glowColor",
		name = "Glow Color",
		description = "The color of the glow effect behind hovered items",
		position = 1
	)
	default Color glowColor()
	{
		return new Color(25, 23, 21, 128);
	}

	@Range(min = 0, max = 20)
	@ConfigItem(
		keyName = "glowPadding",
		name = "Glow Padding",
		description = "How many pixels the glow extends beyond the item bounds",
		position = 2
	)
	default int glowPadding()
	{
		return 2;
	}
}
