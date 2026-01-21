package com.hoverhighlight;

import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.RoundRectangle2D;

/**
 * Overlay that renders a glow behind inventory slots on mouse hover.
 */
public class HoverHighlightOverlay extends Overlay
{
	private final Client client;
	private final HoverHighlightConfig config;

	@Inject
	public HoverHighlightOverlay(Client client, HoverHighlightConfig config)
	{
		this.client = client;
		this.config = config;

		setLayer(OverlayLayer.UNDER_WIDGETS);
		setPosition(OverlayPosition.DYNAMIC);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return null;
		}

		if (client.getDraggedWidget() != null)
		{
			return null;
		}

		net.runelite.api.Point mousePos = client.getMouseCanvasPosition();
		if (mousePos == null)
		{
			return null;
		}

		Point mousePoint = new Point(mousePos.getX(), mousePos.getY());

		if (renderForInventory(graphics, mousePoint))
		{
			return null;
		}

		if (renderForBankInventory(graphics, mousePoint))
		{
			return null;
		}

		return null;
	}

	/**
	 * Renders glow for items in the main inventory.
	 * @return true if a glow was rendered
	 */
	private boolean renderForInventory(Graphics2D graphics, Point mousePoint)
	{
		Widget inventoryWidget = client.getWidget(InterfaceID.Inventory.ITEMS);
		if (inventoryWidget == null || inventoryWidget.isHidden())
		{
			return false;
		}

		return renderForWidget(graphics, mousePoint, inventoryWidget);
	}

	/**
	 * Renders glow for items in the bank-side inventory.
	 * @return true if a glow was rendered
	 */
	private boolean renderForBankInventory(Graphics2D graphics, Point mousePoint)
	{
		Widget bankSideWidget = client.getWidget(InterfaceID.Bankside.ITEMS);
		if (bankSideWidget == null || bankSideWidget.isHidden())
		{
			return false;
		}

		return renderForWidget(graphics, mousePoint, bankSideWidget);
	}

	/**
	 * Checks item slots for mouse hover and renders glow.
	 * @return true if a glow was rendered
	 */
	private boolean renderForWidget(Graphics2D graphics, Point mousePoint, Widget parentWidget)
	{
		if (!parentWidget.isIf3())
		{
			return false;
		}

		for (int i = 0; i < 28; ++i)
		{
			Widget child = parentWidget.getChild(i);
			if (child == null)
			{
				continue;
			}

			int itemId = child.getItemId();
			if (itemId <= 0 || itemId == 6512)
			{
				continue;
			}

			net.runelite.api.Point canvasLocation = child.getCanvasLocation();
			if (canvasLocation == null)
			{
				continue;
			}

			int width = child.getWidth();
			int height = child.getHeight();
			if (width <= 0 || height <= 0)
			{
				continue;
			}

			Rectangle bounds = new Rectangle(canvasLocation.getX(), canvasLocation.getY(), width, height);

			if (bounds.contains(mousePoint))
			{
				drawGlow(graphics, bounds);
				return true;
			}
		}

		return false;
	}

	/**
	 * Draws a glowing rectangle behind the item.
	 */
	private void drawGlow(Graphics2D graphics, Rectangle bounds)
	{
		Color glowColor = config.glowColor();
		int padding = config.glowPadding();

		RoundRectangle2D glowRect = new RoundRectangle2D.Float(
			bounds.x - padding,
			bounds.y - padding,
			bounds.width + (padding * 2),
			bounds.height + (padding * 2),
			7,
			7
		);

		graphics.setColor(glowColor);
		graphics.fill(glowRect);
	}
}
