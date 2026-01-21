package com.hoverhighlight;

import com.google.inject.Provides;
import javax.inject.Inject;

import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "Hover Highlight",
	description = "Adds a customizable glow effect behind inventory items when hovering over them",
	tags = {"inventory", "highlight", "hover", "glow", "mouse"}
)
public class HoverHighlightPlugin extends Plugin
{
	@Inject
	private OverlayManager overlayManager;

	@Inject
	private HoverHighlightOverlay overlay;

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
	}

	@Provides
	HoverHighlightConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(HoverHighlightConfig.class);
	}
}
