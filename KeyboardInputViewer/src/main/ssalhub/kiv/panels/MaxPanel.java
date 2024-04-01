package main.ssalhub.kiv.panels;

import main.ssalhub.kiv.Main;
import main.ssalhub.kiv.RenderingMode;

/**
 * Panel used to display the
 * maximum keys pressed per second
 * 
 * @author Roan
 */
public final class MaxPanel extends BasePanel {
    /**
     * Serial ID
     */
    private static final long serialVersionUID = 8816524158873355997L;
    /**
     * Static instance of this panel that is reused all the time
     */
    public static final MaxPanel INSTANCE = new MaxPanel();

    /**
     * Constructs a new maximum panel
     */
    private MaxPanel() {
        sizeChanged();
    }

    @Override
    protected String getTitle() {
        return "MAX";
    }

    @Override
    protected String getValue() {
        return String.valueOf(Main.max);
    }

    @Override
    public int getLayoutX() {
        return Main.config.max_x;
    }

    @Override
    public int getLayoutY() {
        return Main.config.max_y;
    }

    @Override
    public int getLayoutWidth() {
        return Main.config.max_w;
    }

    @Override
    public int getLayoutHeight() {
        return Main.config.max_h;
    }

    @Override
    protected RenderingMode getRenderingMode() {
        return Main.config.max_mode;
    }

    @Override
    protected String getBindImg() {
        return Main.config.max_bImg;
    }
}