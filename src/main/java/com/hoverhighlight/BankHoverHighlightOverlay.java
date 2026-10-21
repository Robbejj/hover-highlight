package com.hoverhighlight;

import net.runelite.api.Client;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;

import javax.inject.Inject;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

public class BankHoverHighlightOverlay extends HoverHighlightOverlay
{
	private final Client client;
	private final HoverHighlightConfig config;

	@Inject
	public BankHoverHighlightOverlay(Client client, HoverHighlightConfig config)
	{
		super(client, config);
		this.client = client;
		this.config = config;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.highlightBank())
		{
			return null;
		}

		Point mousePoint = getMousePoint();
		if (mousePoint == null)
		{
			return null;
		}

		Widget bankItems = client.getWidget(InterfaceID.Bankmain.ITEMS);
		if (bankItems == null || bankItems.isHidden())
		{
			return null;
		}

		Widget potionStore = client.getWidget(InterfaceID.Bankmain.POTIONSTORE_CONTAINER);
		if (potionStore != null && !potionStore.isHidden())
		{
			return null;
		}

		Rectangle visibleArea = bankItems.getBounds();
		if (!visibleArea.contains(mousePoint))
		{
			return null;
		}

		Graphics2D clipped = (Graphics2D) graphics.create();
		try
		{
			clipped.clip(visibleArea);
			renderForWidget(clipped, mousePoint, bankItems);
		}
		finally
		{
			clipped.dispose();
		}

		return null;
	}
}
