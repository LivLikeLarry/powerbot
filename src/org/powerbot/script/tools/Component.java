package org.powerbot.script.tools;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import org.powerbot.bot.client.Client;
import org.powerbot.bot.client.RSInterface;
import org.powerbot.bot.client.RSInterfaceNode;
import org.powerbot.script.internal.wrappers.HashTable;
import org.powerbot.script.util.Calculations;
import org.powerbot.util.StringUtils;

public class Component extends Interactive implements Drawable, Displayable {
	public static final Color TARGET_FILL_COLOR = new Color(0, 0, 0, 50);
	public static final Color TARGET_STROKE_COLOR = new Color(0, 255, 0, 150);
	private final Widget widget;
	private final Component parent;
	private final int index;

	public Component(final MethodContext ctx, final Widget widget, final int index) {
		this(ctx, widget, null, index);
	}

	public Component(final MethodContext ctx, final Widget widget, final Component parent, final int index) {
		super(ctx);
		this.widget = widget;
		this.parent = parent;
		this.index = index;
	}

	public Widget getWidget() {
		return this.widget;
	}

	public Component getParent() {
		return this.parent;
	}

	public int getIndex() {
		return this.index;
	}

	@Override
	public void setBounds(final int x1, final int x2, final int y1, final int y2, final int z1, final int z2) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Component getComponent() {
		return this;
	}

	public Component[] getChildren() {
		final RSInterface component = getInternalComponent();
		final RSInterface[] interfaces;
		if (component != null && (interfaces = component.getComponents()) != null) {
			final Component[] components = new Component[interfaces.length];
			for (int i = 0; i < interfaces.length; i++) {
				components[i] = new Component(ctx, widget, this, i);
			}
			return components;
		}
		return new Component[0];
	}

	public int getChildrenCount() {
		final RSInterface component = getInternalComponent();
		final RSInterface[] interfaces;
		if (component != null && (interfaces = component.getComponents()) != null) {
			return interfaces.length;
		}
		return 0;
	}

	public Component getChild(final int index) {
		if (index < 0) {
			throw new IndexOutOfBoundsException(index + " < " + 0);
		}
		return new Component(ctx, widget, this, index);
	}

	public String[] getActions() {
		final RSInterface component = getInternalComponent();
		String[] actions = new String[0];
		if (component != null) {
			if ((actions = component.getActions()) == null) {
				actions = new String[0];
			}
		}
		return actions;
	}

	public int getTextureId() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getTextureID() : -1;
	}

	public int getBorderThickness() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getBorderThinkness() : -1;
	}

	public int getId() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getID() : -1;
	}

	public int getItemIndex() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getComponentIndex() : -1;
	}

	public String getItemName() {
		final RSInterface component = getInternalComponent();
		String name = "";
		if (component != null && (name = component.getComponentName()) == null) {
			name = "";
		}
		return StringUtils.stripHtml(name);
	}

	public int getItemId() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getComponentID() : -1;
	}

	public int getItemStackSize() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getComponentStackSize() : -1;
	}

	public int getModelId() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getModelID() : -1;
	}

	public int getModelType() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getModelType() : -1;
	}

	public int getModelZoom() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getModelZoom() : -1;
	}

	public int getParentId() {
		final Client client = ctx.getClient();
		final RSInterface component = getInternalComponent();
		if (client == null || component == null) {
			return -1;
		}

		final int pId = component.getParentID();
		if (pId != -1) {
			return pId;
		}

		final int containerId = getId() >>> 16;
		final HashTable ncI = new HashTable(client.getRSInterfaceNC());
		for (RSInterfaceNode node = (RSInterfaceNode) ncI.getFirst(); node != null; node = (RSInterfaceNode) ncI.getNext()) {
			if (containerId == node.getMainID()) {
				return (int) node.getId();
			}
		}

		return -1;
	}

	public Point getAbsoluteLocation() {
		final Client client = ctx.getClient();
		final RSInterface component = getInternalComponent();
		if (client == null || component == null) {
			return new Point(-1, -1);
		}
		final int pId = getParentId();
		int x = 0, y = 0;
		if (pId != -1) {
			final Point point = ctx.widgets.get(pId >> 16, pId & 0xffff).getAbsoluteLocation();
			x = point.x;
			y = point.y;
		} else {
			final Rectangle[] bounds = client.getRSInterfaceBoundsArray();
			final int index = component.getBoundsArrayIndex();
			if (bounds != null && index > 0 && index < bounds.length && bounds[index] != null) {
				return new Point(bounds[index].x, bounds[index].y);
			}
			//x = getMasterX();
			//y = getMasterY();
		}
		if (pId != -1) {
			final Component child = ctx.widgets.get(pId >> 16, pId & 0xffff);
			final int horizontalScrollSize = child.getMaxHorizontalScroll(), verticalScrollSize = child.getMaxVerticalScroll();
			if (horizontalScrollSize > 0 || verticalScrollSize > 0) {
				x -= child.getScrollX();
				y -= child.getScrollY();
			}
		}
		x += component.getX();
		y += component.getY();
		return new Point(x, y);
	}

	public Point getRelativeLocation() {
		final RSInterface component = getInternalComponent();
		return component != null ? new Point(component.getX(), component.getY()) : new Point(-1, -1);
	}

	public String getSelectedAction() {
		final RSInterface component = getInternalComponent();
		String action = "";
		if (component != null && (action = component.getSelectedActionName()) == null) {
			action = "";
		}
		return action;
	}

	public int getShadowColor() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getShadowColor() : -1;
	}

	public int getContentType() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getSpecialType() : -1;
	}

	public String getText() {
		final RSInterface component = getInternalComponent();
		String text = "";
		if (component != null && (text = component.getText()) == null) {
			text = "";
		}
		return text;
	}

	public int getTextColor() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getTextColor() : -1;
	}

	public String getTooltip() {
		final RSInterface component = getInternalComponent();
		String tip = "";
		if (component != null && (tip = component.getTooltip()) == null) {
			tip = "";
		}
		return tip;
	}

	public int getType() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getType() : -1;
	}

	public int getWidth() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getWidth() : -1;
	}

	public int getHeight() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getHeight() : -1;
	}

	public int getXRotation() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getXRotation() : -1;
	}

	public int getYRotation() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getYRotation() : -1;
	}

	public int getZRotation() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getZRotation() : -1;
	}

	public boolean isVerticallyFlipped() {
		final RSInterface component = getInternalComponent();
		return component != null && component.isVerticallyFlipped();
	}

	public boolean isHorizontallyFlipped() {
		final RSInterface component = getInternalComponent();
		return component != null && component.isHorizontallyFlipped();
	}

	public int getScrollX() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getHorizontalScrollbarPosition() : -1;
	}

	public int getMaxHorizontalScroll() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getHorizontalScrollbarSize() : -1;
	}

	public int getScrollWidth() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getHorizontalScrollbarThumbSize() : -1;
	}

	public int getScrollY() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getVerticalScrollbarPosition() : -1;
	}

	public int getMaxVerticalScroll() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getVerticalScrollbarSize() : -1;
	}

	public int getScrollHeight() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getVerticalScrollbarThumbSize() : -1;
	}

	public boolean isInventory() {
		final RSInterface component = getInternalComponent();
		return component != null && component.isInventoryInterface();
	}

	public boolean isVisible() {
		final RSInterface internal = getInternalComponent();
		int id = 0;
		if (internal != null && isValid() && !internal.isHidden()) {
			id = getParentId();
		}
		return id == -1 || (id != 0 && ctx.widgets.get(id >> 16, id & 0xffff).isVisible());
	}

	public Rectangle getBoundingRect() {
		final Point absLocation = getAbsoluteLocation();
		if (absLocation.x == -1 && absLocation.y == -1) {
			return new Rectangle(0, 0, -1, -1);
		}
		return new Rectangle(absLocation.x, absLocation.y,
				getWidth(),
				getHeight()
		);
	}

	public Rectangle getViewportRect() {
		final Point absLocation = getAbsoluteLocation();
		if (absLocation.x == -1 && absLocation.y == -1) {
			return new Rectangle(0, 0, -1, -1);
		}
		return new Rectangle(absLocation.x, absLocation.y,
				getScrollWidth(),
				getScrollHeight()
		);
	}

	@Override
	public Point getInteractPoint() {
		return getNextPoint();
	}

	@Override
	public Point getNextPoint() {
		final Rectangle interact = getInteractRectangle();
		final int x = interact.x, y = interact.y;
		final int w = interact.width, h = interact.height;
		if (interact.width != -1 && interact.height != -1) {
			return Calculations.nextPoint(interact, new Rectangle(x + w / 2, y + h / 2, w / 4, h / 4));
		}
		return new Point(-1, -1);
	}

	@Override
	public Point getCenterPoint() {
		final Rectangle interact = getInteractRectangle();
		return interact.getWidth() != -1 && interact.getHeight() != -1 ? new Point((int) interact.getCenterX(), (int) interact.getCenterY()) : new Point(-1, -1);
	}

	@Override
	public boolean contains(final Point point) {
		return getInteractRectangle().contains(point);
	}

	@Override
	public boolean isValid() {
		final RSInterface internal = getInternalComponent();
		return internal != null && (parent == null || parent.isVisible()) &&
				getId() != -1 && internal.getBoundsArrayIndex() != -1;
	}

	@Override
	public void draw(final Graphics render) {
		draw(render, 50);
	}

	@Override
	public void draw(final Graphics render, int alpha) {
		final Rectangle rectangle = getInteractRectangle();
		if (rectangle.getWidth() == -1 || rectangle.getHeight() == -1) {
			return;
		}
		Color c = TARGET_FILL_COLOR;
		int rgb = c.getRGB();
		if (((rgb >> 24) & 0xff) != alpha) {
			c = new Color((rgb >> 16) & 0xff, (rgb >> 8) & 0xff, rgb & 0xff, alpha);
		}
		render.setColor(c);
		render.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
		c = TARGET_STROKE_COLOR;
		rgb = c.getRGB();
		alpha *= 3;
		if (((rgb >> 24) & 0xff) != alpha) {
			c = new Color((rgb >> 16) & 0xff, (rgb >> 8) & 0xff, rgb & 0xff, alpha);
		}
		render.setColor(c);
		render.drawRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
	}

	private Rectangle getInteractRectangle() {
		final Rectangle r = getViewportRect();
		r.grow(-1, -1);
		return r;
	}

	@SuppressWarnings("UnusedDeclaration")
	private boolean isInScrollableArea() {
		int pId = getParentId();
		if (pId == -1) {
			return false;
		}

		Component scrollableArea = ctx.widgets.get(pId >> 16, pId & 0xffff);
		while (scrollableArea.getMaxVerticalScroll() == 0 && (pId = scrollableArea.getParentId()) != -1) {
			scrollableArea = ctx.widgets.get(pId >> 16, pId & 0xffff);
		}

		return scrollableArea.getMaxVerticalScroll() != 0;
	}

	private RSInterface getInternalComponent() {
		final RSInterface[] components;
		if (parent != null) {
			final RSInterface parentComponent = parent.getInternalComponent();
			components = parentComponent != null ? parentComponent.getComponents() : null;
		} else {
			components = widget.getInternalComponents();
		}
		return components != null && index < components.length ? components[index] : null;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + index + (parent != null ? "/" + parent : "") + "]@" + widget;
	}

	@Override
	public int hashCode() {
		return widget.getIndex() * 31 + index;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || !(o instanceof Component)) {
			return false;
		}
		final Component c = (Component) o;
		return c.widget.equals(widget) && c.index == index &&
				(parent == null && c.parent == null || (parent != null && parent.equals(c.parent)));
	}
}