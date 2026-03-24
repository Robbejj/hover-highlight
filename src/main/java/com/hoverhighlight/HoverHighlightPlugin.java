package com.hoverhighlight;

import com.google.inject.Provides;
import javax.inject.Inject;

import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "Hover Highlight",
	description = "Highlights items, prayers and spells with a customizable glow when hovering over",
	tags = {"inventory", "highlight", "hover", "glow", "mouse", "prayer", "spellbook"}
)
public class HoverHighlightPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private HoverHighlightOverlay overlay;

	@Override
	protected void startUp() throws Exception
	{
		if (client.getGameState() == GameState.LOGGED_IN)
		{
			boolean transparentSidePanel = client.getVarbitValue(VarbitID.SIDE_TRANSPARENCY) == 0;
			overlay.updateLayer(transparentSidePanel);
		}
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGED_IN)
		{
			updateOverlayLayer();
		}
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		if (event.getVarbitId() == VarbitID.SIDE_TRANSPARENCY)
		{
			updateOverlayLayer();
		}
	}

	private void updateOverlayLayer()
	{
		overlayManager.remove(overlay);
		boolean transparentSidePanel = client.getVarbitValue(VarbitID.SIDE_TRANSPARENCY) == 0;
		overlay.updateLayer(transparentSidePanel);
		overlayManager.add(overlay);
	}

	@Provides
	HoverHighlightConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(HoverHighlightConfig.class);
	}
}
