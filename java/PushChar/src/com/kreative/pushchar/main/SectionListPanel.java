package com.kreative.pushchar.main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.Scrollable;

public class SectionListPanel extends JPanel implements Scrollable {
	private static final long serialVersionUID = 1L;
	
	private final JPanel mainPanel;
	private final List<Section> sections;
	private final List<SectionPanel> panels;
	private final List<SectionPanelListener> listeners;
	private boolean autoselects;
	
	public SectionListPanel() {
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		setLayout(new BorderLayout());
		add(mainPanel, BorderLayout.PAGE_START);
		
		sections = new ArrayList<Section>();
		panels = new ArrayList<SectionPanel>();
		listeners = new ArrayList<SectionPanelListener>();
		autoselects = false;
	}
	
	public SectionListPanel(List<Section> sects, Font font) {
		this();
		setFont(font);
		setSections(sects);
	}
	
	public void clearSections() {
		for (SectionPanel sp : panels) {
			sp.setAutoselects(false);
			sp.removeSectionPanelListener(mux);
		}
		mainPanel.removeAll();
		sections.clear();
		panels.clear();
		revalidate();
	}
	
	public void setSections(List<Section> sects) {
		clearSections();
		for (Section s : sects) {
			SectionPanel sp = new SectionPanel(s, getFont());
			sp.setAutoselects(autoselects);
			sp.addSectionPanelListener(mux);
			mainPanel.add(sp);
			sections.add(s);
			panels.add(sp);
		}
		revalidate();
	}
	
	public void setAutoselects(boolean autoselects) {
		this.autoselects = autoselects;
		if (panels != null) {
			for (SectionPanel sp : panels) {
				sp.setAutoselects(autoselects);
			}
		}
	}
	
	@Override
	public void setFont(Font font) {
		super.setFont(font);
		if (panels != null) {
			for (SectionPanel sp : panels) {
				sp.setFont(font);
			}
		}
		revalidate();
	}
	
	public void addSectionPanelListener(SectionPanelListener l) {
		listeners.add(l);
	}
	
	public void removeSectionPanelListener(SectionPanelListener l) {
		listeners.remove(l);
	}
	
	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}
	
	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}
	
	@Override
	public boolean getScrollableTracksViewportWidth() {
		return true;
	}
	
	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		if (panels.isEmpty()) return 12;
		return panels.get(0).getScrollableUnitIncrement(visibleRect, orientation, direction);
	}
	
	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return visibleRect.height;
	}
	
	private final SectionPanelListener mux = new SectionPanelListener() {
		@Override
		public void selectionChanged(SectionPanel sp, Section s, int row, int column) {
			for (SectionPanelListener l : listeners) {
				l.selectionChanged(sp, s, row, column);
			}
		}
	};
}
