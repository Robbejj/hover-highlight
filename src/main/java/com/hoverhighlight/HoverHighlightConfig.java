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
	enum HighlightStyle
	{
		FILLED("Filled"),
		BORDER("Border only");

		private final String name;

		HighlightStyle(String name)
		{
			this.name = name;
		}

		@Override
		public String toString()
		{
			return name;
		}
	}

	@ConfigItem(
		keyName = "highlightStyle",
		name = "Style",
		description = "Filled: works with transparent inventory. Border: works with opaque inventory.",
		position = 1
	)
	default HighlightStyle highlightStyle()
	{
		return HighlightStyle.BORDER;
	}

	@Alpha
	@ConfigItem(
		keyName = "glowColor",
		name = "Highlight Color",
		description = "The color of the highlight effect",
		position = 2
	)
	default Color glowColor()
	{
		return new Color(107, 97, 97, 255);
	}

	@Range(min = 1, max = 10)
	@ConfigItem(
		keyName = "borderWidth",
		name = "Border Width",
		description = "Width of the border highlight (only applies to Border style)",
		position = 3
	)
	default int borderWidth()
	{
		return 3;
	}

	@Range(min = 0, max = 20)
	@ConfigItem(
		keyName = "glowPadding",
		name = "Padding",
		description = "How many pixels the highlight extends beyond the item bounds",
		position = 4
	)
	default int glowPadding()
	{
		return 2;
	}
}
