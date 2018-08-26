package me.roan.kps.panels;

import me.roan.kps.Main;
import me.roan.kps.RenderingMode;

/**
 * Panel used to display the
 * current keys pressed per second<br>
 * However since the actual 'current'
 * time frame is still on going this
 * actually displays the keys per second
 * from the previous second
 * @author Roan
 */
public final class NowPanel extends BasePanel {
	/**
	 * Serial ID
	 */
	private static final long serialVersionUID = 8816524158873355997L;

	@Override
	protected String getTitle() {
		return "CUR";
	}

	@Override
	protected String getValue() {
		return String.valueOf(Main.prev);
	}
	
	@Override
	public int getLayoutX() {
		return Main.config.cur_x;
	}

	@Override
	public int getLayoutY() {
		return Main.config.cur_y;
	}

	@Override
	public int getLayoutWidth() {
		return Main.config.cur_w;
	}

	@Override
	public int getLayoutHeight() {
		return Main.config.cur_h;
	}

	@Override
	protected RenderingMode getRenderingMode() {
		return Main.config.cur_mode;
	}
}