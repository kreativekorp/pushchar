package com.kreative.pushchar.main;

import javax.swing.JFrame;

public class WindowManager {
	private JFrame pushWindow = null;
	private JFrame searchWindow = null;
	private TriggerWindow northWestTrigger = null;
	private TriggerWindow northEastTrigger = null;
	private TriggerWindow southWestTrigger = null;
	private TriggerWindow southEastTrigger = null;
	
	public JFrame getPushWindow() {
		return pushWindow;
	}
	
	public JFrame getSearchWindow() {
		return searchWindow;
	}
	
	public void setPushWindow(JFrame pushWindow) {
		this.pushWindow = pushWindow;
	}
	
	public void setSearchWindow(JFrame searchWindow) {
		this.searchWindow = searchWindow;
	}
	
	public void createTriggers(Options o) {
		disposeTriggers();
		getTriggerWindow(o.pushPosition, o.orientation).setPushWindow(pushWindow);
		getTriggerWindow(o.searchPosition, o.orientation).setSearchWindow(searchWindow);
	}
	
	private TriggerWindow getTriggerWindow(TriggerWindow.Position p, TriggerWindow.Orientation o) {
		switch (p) {
			case NORTHWEST:
				if (northWestTrigger == null) northWestTrigger = new TriggerWindow(p, o);
				return northWestTrigger;
			case NORTHEAST:
				if (northEastTrigger == null) northEastTrigger = new TriggerWindow(p, o);
				return northEastTrigger;
			case SOUTHWEST:
				if (southWestTrigger == null) southWestTrigger = new TriggerWindow(p, o);
				return southWestTrigger;
			case SOUTHEAST:
				if (southEastTrigger == null) southEastTrigger = new TriggerWindow(p, o);
				return southEastTrigger;
			default:
				throw new IllegalArgumentException();
		}
	}
	
	public void setTriggersVisible(boolean visible) {
		if (northWestTrigger != null) northWestTrigger.setVisible(visible);
		if (northEastTrigger != null) northEastTrigger.setVisible(visible);
		if (southWestTrigger != null) southWestTrigger.setVisible(visible);
		if (southEastTrigger != null) southEastTrigger.setVisible(visible);
	}
	
	public void disposeTriggers() {
		if (northWestTrigger != null) { northWestTrigger.dispose(); northWestTrigger = null; }
		if (northEastTrigger != null) { northEastTrigger.dispose(); northEastTrigger = null; }
		if (southWestTrigger != null) { southWestTrigger.dispose(); southWestTrigger = null; }
		if (southEastTrigger != null) { southEastTrigger.dispose(); southEastTrigger = null; }
	}
}
