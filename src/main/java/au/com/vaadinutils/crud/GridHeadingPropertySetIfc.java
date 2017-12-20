package au.com.vaadinutils.crud;

import com.vaadin.ui.Grid;

public interface GridHeadingPropertySetIfc<E>
{

	void applySettingsToColumns();

	void setDeferLoadSettings(boolean b);

	void applyToGrid(Class<E> type, Grid grid, String uniqueId);

}
