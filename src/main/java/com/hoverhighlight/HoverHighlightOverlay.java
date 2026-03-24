package com.hoverhighlight;

import net.runelite.api.Client;
import net.runelite.api.EnumComposition;
import net.runelite.api.EnumID;
import net.runelite.api.GameState;
import net.runelite.api.ItemComposition;
import net.runelite.api.ParamID;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.RoundRectangle2D;

/**
 * Overlay that renders a highlight on side panel items on mouse hover.
 */
public class HoverHighlightOverlay extends Overlay
{
	private static final float ABOVE_WIDGETS_OPACITY_MULTIPLIER = 0.5f;

	private final Client client;
	private final HoverHighlightConfig config;
	private boolean isAboveWidgets = true;

	@Inject
	public HoverHighlightOverlay(Client client, HoverHighlightConfig config)
	{
		this.client = client;
		this.config = config;

		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
	}

	public void updateLayer(boolean transparentSidePanel)
	{
		if (transparentSidePanel)
		{
			setLayer(OverlayLayer.UNDER_WIDGETS);
			isAboveWidgets = false;
		}
		else
		{
			setLayer(OverlayLayer.ABOVE_WIDGETS);
			isAboveWidgets = true;
		}
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

		if (renderForInventory(graphics, mousePoint)
			|| renderForBankInventory(graphics, mousePoint)
			|| renderForPrayerBook(graphics, mousePoint)
			|| renderForSpellBook(graphics, mousePoint))
		{
			return null;
		}

		return null;
	}

	private boolean renderForInventory(Graphics2D graphics, Point mousePoint)
	{
		if (!config.highlightInventory())
		{
			return false;
		}

		Widget inventoryWidget = client.getWidget(InterfaceID.Inventory.ITEMS);
		if (inventoryWidget == null || inventoryWidget.isHidden())
		{
			return false;
		}

		return renderForWidget(graphics, mousePoint, inventoryWidget);
	}

	private boolean renderForBankInventory(Graphics2D graphics, Point mousePoint)
	{
		if (!config.highlightInventory())
		{
			return false;
		}

		Widget bankSideWidget = client.getWidget(InterfaceID.Bankside.ITEMS);
		if (bankSideWidget == null || bankSideWidget.isHidden())
		{
			return false;
		}

		return renderForWidget(graphics, mousePoint, bankSideWidget);
	}

	private boolean renderForPrayerBook(Graphics2D graphics, Point mousePoint)
	{
		if (!config.highlightPrayerBook() || client.getWidget(InterfaceID.PRAYERBOOK, 0) == null)
		{
			return false;
		}

		return renderFromEnum(graphics, mousePoint, getPrayerBookEnum(), ParamID.OC_PRAYER_COMPONENT);
	}

	private boolean renderForSpellBook(Graphics2D graphics, Point mousePoint)
	{
		if (!config.highlightSpellBook() || client.getWidget(InterfaceID.MAGIC_SPELLBOOK, 0) == null)
		{
			return false;
		}

		int subSpellbookId = client.getEnum(EnumID.SPELLBOOKS_SUB).getIntValue(client.getVarbitValue(VarbitID.SPELLBOOK));
		int spellbookId = client.getEnum(subSpellbookId).getIntValue(client.getVarbitValue(VarbitID.SPELLBOOK_SUBLIST));

		return renderFromEnum(graphics, mousePoint, client.getEnum(spellbookId), ParamID.SPELL_BUTTON);
	}

	private boolean renderFromEnum(Graphics2D graphics, Point mousePoint, EnumComposition itemEnum, int componentParam)
	{
		for (int i = 0; i < itemEnum.size(); ++i)
		{
			ItemComposition obj = client.getItemDefinition(itemEnum.getIntValue(i));
			Widget widget = client.getWidget(obj.getIntValue(componentParam));
			if (drawIfHovered(graphics, mousePoint, widget))
			{
				return true;
			}
		}
		return false;
	}

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

			if (drawIfHovered(graphics, mousePoint, child))
			{
				return true;
			}
		}

		return false;
	}

	private boolean drawIfHovered(Graphics2D graphics, Point mousePoint, Widget widget)
	{
		if (widget == null || widget.isHidden())
		{
			return false;
		}

		net.runelite.api.Point canvasLocation = widget.getCanvasLocation();
		if (canvasLocation == null)
		{
			return false;
		}

		int width = widget.getWidth();
		int height = widget.getHeight();
		if (width <= 0 || height <= 0)
		{
			return false;
		}

		Rectangle bounds = new Rectangle(canvasLocation.getX(), canvasLocation.getY(), width, height);
		if (!bounds.contains(mousePoint))
		{
			return false;
		}

		drawHighlight(graphics, bounds);
		return true;
	}

	private EnumComposition getPrayerBookEnum()
	{
		if (client.getVarbitValue(VarbitID.PRAYERBOOK) == 1)
		{
			return client.getEnum(EnumID.PRAYERS_RUINOUS);
		}

		boolean deadeye = client.getVarbitValue(VarbitID.PRAYER_DEADEYE_UNLOCKED) != 0;
		boolean vigour = client.getVarbitValue(VarbitID.PRAYER_MYSTIC_VIGOUR_UNLOCKED) != 0;

		if (deadeye && vigour)
		{
			return client.getEnum(EnumID.PRAYERS_NORMAL_DEADEYE_MYSTIC_VIGOUR);
		}
		else if (deadeye)
		{
			return client.getEnum(EnumID.PRAYERS_NORMAL_DEADEYE);
		}
		else if (vigour)
		{
			return client.getEnum(EnumID.PRAYERS_NORMAL_MYSTIC_VIGOUR);
		}
		else
		{
			return client.getEnum(EnumID.PRAYERS_NORMAL);
		}
	}

	private void drawHighlight(Graphics2D graphics, Rectangle bounds)
	{
		Color baseColor = config.glowColor();
		int padding = config.glowPadding();
		HoverHighlightConfig.HighlightStyle style = config.highlightStyle();

		Color color;
		if (isAboveWidgets)
		{
			int reducedAlpha = (int) (baseColor.getAlpha() * ABOVE_WIDGETS_OPACITY_MULTIPLIER);
			color = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), reducedAlpha);
		}
		else
		{
			color = baseColor;
		}

		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		RoundRectangle2D rect = new RoundRectangle2D.Float(
			bounds.x - padding,
			bounds.y - padding,
			bounds.width + (padding * 2),
			bounds.height + (padding * 2),
			7,
			7
		);

		graphics.setColor(color);

		if (style == HoverHighlightConfig.HighlightStyle.FILLED)
		{
			graphics.fill(rect);
		}
		else
		{
			Stroke originalStroke = graphics.getStroke();
			graphics.setStroke(new BasicStroke(config.borderWidth()));
			graphics.draw(rect);
			graphics.setStroke(originalStroke);
		}
	}
}
