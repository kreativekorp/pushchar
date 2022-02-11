package com.kreative.pushchar.main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.Border;

public class HotSpotPanel extends JComponent {
	private static final long serialVersionUID = 1L;
	
	private static final Color MAC_COLOR_1 = new Color(0xFFCDCFE6);
	private static final Color MAC_COLOR_2 = new Color(0xFF7C04E8);
	private static final Color WIN_COLOR_1 = new Color(0xFFB9D8E9);
	private static final Color WIN_COLOR_2 = new Color(0xFF0178D6);
	private static final Color LINUX_COLOR_1 = new Color(0xFF84377F);
	private static final Color LINUX_COLOR_2 = new Color(0xFFE3562B);
	
	private static final AlphaComposite FULL = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);
	private static final AlphaComposite HALF = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
	
	private final Color color1;
	private final Color color2;
	private final BufferedImage push;
	private final BufferedImage search;
	
	private TriggerWindow.Position pushPosition = TriggerWindow.Position.NORTHWEST;
	private TriggerWindow.Position searchPosition = TriggerWindow.Position.NORTHWEST;
	private TriggerWindow.Orientation orientation = TriggerWindow.Orientation.WEST_EAST;
	
	private final Map<BufferedImage,Rectangle> rects = new HashMap<BufferedImage,Rectangle>();
	private BufferedImage draggingImage = null;
	private int draggingX = 0;
	private int draggingY = 0;
	private int draggingDX = 0;
	private int draggingDY = 0;
	
	public HotSpotPanel() {
		boolean isMac;
		boolean isWin;
		try {
			String osName = System.getProperty("os.name").toUpperCase();
			isMac = osName.contains("MAC OS");
			isWin = osName.contains("WINDOWS");
		} catch (Exception e) {
			isMac = false;
			isWin = false;
		}
		this.color1 = isMac ? MAC_COLOR_1 : isWin ? WIN_COLOR_1 : LINUX_COLOR_1;
		this.color2 = isMac ? MAC_COLOR_2 : isWin ? WIN_COLOR_2 : LINUX_COLOR_2;
		
		BufferedImage tmpPush;
		BufferedImage tmpSearch;
		try {
			tmpPush = ImageIO.read(HotSpotPanel.class.getResource("push.png"));
			tmpSearch = ImageIO.read(HotSpotPanel.class.getResource("search.png"));
		} catch (Exception e) {
			tmpPush = new BufferedImage(22, 22, BufferedImage.TYPE_INT_ARGB);
			tmpSearch = new BufferedImage(22, 22, BufferedImage.TYPE_INT_ARGB);
		}
		this.push = tmpPush;
		this.search = tmpSearch;
		
		Border raised = BorderFactory.createRaisedBevelBorder();
		Border lowered = BorderFactory.createLoweredBevelBorder();
		Border outerBezel = BorderFactory.createCompoundBorder(raised, lowered);
		Border innerBezel = BorderFactory.createMatteBorder(4, 4, 4, 4, Color.black);
		this.setBorder(BorderFactory.createCompoundBorder(outerBezel, innerBezel));
		
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent evt) {
				draggingImage = null;
				draggingX = evt.getX();
				draggingY = evt.getY();
				for (Map.Entry<BufferedImage,Rectangle> e : rects.entrySet()) {
					Rectangle r = e.getValue();
					if (r.contains(draggingX, draggingY)) {
						draggingImage = e.getKey();
						draggingDX = draggingX - r.x;
						draggingDY = draggingY - r.y;
					}
				}
				repaint();
			}
			@Override
			public void mouseReleased(MouseEvent evt) {
				draggingImage = null;
				repaint();
			}
		});
		this.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent evt) {
				if (draggingImage == null) return;
				Rectangle r = rects.get(null);
				draggingX = evt.getX();
				draggingY = evt.getY();
				boolean top = draggingY < (r.y + r.height / 2);
				boolean left = draggingX < (r.x + r.width / 2);
				float xpos = ((draggingX - r.x) / (float)r.width);
				float ypos = ((draggingY - r.y) / (float)r.height);
				boolean a = ypos < xpos;
				boolean b = (1 - ypos) < xpos;
				TriggerWindow.Position p;
				if (top) {
					if (left) p = TriggerWindow.Position.NORTHWEST;
					else p = TriggerWindow.Position.NORTHEAST;
				} else {
					if (left) p = TriggerWindow.Position.SOUTHWEST;
					else p = TriggerWindow.Position.SOUTHEAST;
				}
				if (draggingImage == push) {
					pushPosition = p;
					if ((top == left) == b) {
						if (a != b) orientation = TriggerWindow.Orientation.WEST_EAST;
						else orientation = TriggerWindow.Orientation.NORTH_SOUTH;
					} else {
						if (a != b) orientation = TriggerWindow.Orientation.EAST_WEST;
						else orientation = TriggerWindow.Orientation.SOUTH_NORTH;
					}
				}
				if (draggingImage == search) {
					searchPosition = p;
					if ((top == left) != b) {
						if (a != b) orientation = TriggerWindow.Orientation.WEST_EAST;
						else orientation = TriggerWindow.Orientation.NORTH_SOUTH;
					} else {
						if (a != b) orientation = TriggerWindow.Orientation.EAST_WEST;
						else orientation = TriggerWindow.Orientation.SOUTH_NORTH;
					}
				}
				repaint();
			}
		});
	}
	
	@Override
	public Dimension getPreferredSize() {
		Insets i = getInsets();
		int w = i.left + i.right + 168;
		int h = i.top + i.bottom + 108;
		return new Dimension(w, h);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		Composite oldComposite = g2.getComposite();
		Paint oldPaint = g2.getPaint();
		
		Insets i = getInsets();
		int w = getWidth() - i.left - i.right;
		int h = getHeight() - i.top - i.bottom;
		rects.put(null, new Rectangle(i.left, i.top, w, h));
		g2.setPaint(new GradientPaint(i.left, i.top, color1, i.left+w, i.top+h, color2));
		g2.fillRect(i.left, i.top, w, h);
		
		List<BufferedImage> nw = new ArrayList<BufferedImage>();
		List<BufferedImage> ne = new ArrayList<BufferedImage>();
		List<BufferedImage> sw = new ArrayList<BufferedImage>();
		List<BufferedImage> se = new ArrayList<BufferedImage>();
		
		switch (pushPosition) {
			case NORTHWEST: nw.add(push); break;
			case NORTHEAST: ne.add(push); break;
			case SOUTHWEST: sw.add(push); break;
			case SOUTHEAST: se.add(push); break;
		}
		
		switch (searchPosition) {
			case NORTHWEST: nw.add(search); break;
			case NORTHEAST: ne.add(search); break;
			case SOUTHWEST: sw.add(search); break;
			case SOUTHEAST: se.add(search); break;
		}
		
		Rectangle r = new Rectangle(i.left, i.top, w, h);
		paintHotSpot(g2, r, TriggerWindow.Position.NORTHWEST, orientation, nw);
		paintHotSpot(g2, r, TriggerWindow.Position.NORTHEAST, orientation, ne);
		paintHotSpot(g2, r, TriggerWindow.Position.SOUTHWEST, orientation, sw);
		paintHotSpot(g2, r, TriggerWindow.Position.SOUTHEAST, orientation, se);
		
		if (draggingImage != null) {
			g2.setComposite(FULL);
			g2.drawImage(draggingImage, draggingX-draggingDX, draggingY-draggingDY, null);
		}
		
		g2.setComposite(oldComposite);
		g2.setPaint(oldPaint);
	}
	
	private void paintHotSpot(
		Graphics2D g2, Rectangle r,
		TriggerWindow.Position p,
		TriggerWindow.Orientation o,
		List<BufferedImage> l
	) {
		if (l.isEmpty()) return;
		
		Dimension d = new Dimension(l.get(0).getWidth(), l.get(0).getHeight());
		switch (o) {
			case WEST_EAST: case EAST_WEST: d.width *= l.size(); break;
			case NORTH_SOUTH: case SOUTH_NORTH: d.height *= l.size(); break;
		}
		
		Point pt = p.getLocation(r, d);
		switch (o) {
			case EAST_WEST:
				Collections.reverse(l);
			case WEST_EAST:
				for (BufferedImage i : l) {
					g2.setComposite((i == draggingImage) ? HALF : FULL);
					g2.drawImage(i, pt.x, pt.y, null);
					rects.put(i, new Rectangle(pt.x, pt.y, i.getWidth(), i.getHeight()));
					pt.x += i.getWidth();
				}
				break;
			case SOUTH_NORTH:
				Collections.reverse(l);
			case NORTH_SOUTH:
				for (BufferedImage i : l) {
					g2.setComposite((i == draggingImage) ? HALF : FULL);
					g2.drawImage(i, pt.x, pt.y, null);
					rects.put(i, new Rectangle(pt.x, pt.y, i.getWidth(), i.getHeight()));
					pt.y += i.getHeight();
				}
				break;
		}
	}
	
	public void getPositionAndOrientation(Options o) {
		o.pushPosition = this.pushPosition;
		o.searchPosition = this.searchPosition;
		o.orientation = this.orientation;
	}
	
	public void setPositionAndOrientation(Options o) {
		this.pushPosition = o.pushPosition;
		this.searchPosition = o.searchPosition;
		this.orientation = o.orientation;
		this.repaint();
	}
}
