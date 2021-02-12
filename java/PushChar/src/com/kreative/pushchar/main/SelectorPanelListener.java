package com.kreative.pushchar.main;

import java.awt.Font;

public interface SelectorPanelListener {
	public void fontFamilyChanged(SelectorPanel sp, Font font, SectionBuilder builder);
	public void fontStyleChanged(SelectorPanel sp, Font font, SectionBuilder builder);
	public void sectionBuilderChanged(SelectorPanel sp, Font font, SectionBuilder builder);
}
