package au.com.vaadinutils.crud.splitFields;

import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;

/**
 * @deprecated Replaced in V14 migration.
 */
public class SplitTextArea extends TextArea implements SplitField {
    private static final long serialVersionUID = 7753660388792217050L;
    private Label label;

    public SplitTextArea(String label) {
        this.label = new Label(label);
        setCaption(label);

    }

    @Override
    public void setVisible(boolean visible) {
        label.setVisible(visible);
        super.setVisible(visible);
    }

    @Override
    public Label getLabel() {
        return label;
    }

    @Override
    public String getCaption() {
        return label.getValue();
    }

    @Override
    public void hideLabel() {
        setCaption(null);

    }
}
