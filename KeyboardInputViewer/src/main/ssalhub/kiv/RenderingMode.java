package main.ssalhub.kiv;

import java.awt.AlphaComposite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import main.ssalhub.kiv.panels.BasePanel;
import main.ssalhub.kiv.ui.dialog.LayoutDialog.BindImgDialog;

/**
 * An enum specifying the different
 * text rendering modes
 * 
 * @author Roan
 */
public enum RenderingMode {
    /**
     * Only text rendering except value
     */
    ONLY_T("Only Text") {
        @Override
        protected void setTitleDrawPositionImpl(FontMetrics metrics, Graphics2D g, Font font, BasePanel panel,
                String title) {
            point.move(
                    Main.config.borderOffset + insideOffset
                            + (int) Math.round((getPanelInsideWidth(panel) - metrics.stringWidth(title)) / 2.0D),
                    Main.config.borderOffset + insideOffset + (getPanelInsideHeight(panel) * 5) / 8 + 1);
        }

        // not necessary
        @Override
        protected void setValueDrawPositionImpl(FontMetrics metrics, Graphics2D g, Font font, BasePanel panel,
                String value) {
            point.move(
                    Main.config.borderOffset + insideOffset
                            + (int) Math.round((getPanelInsideWidth(panel) - metrics.stringWidth(value)) / 2.0D),
                    Main.config.borderOffset + insideOffset + (getPanelInsideHeight(panel) * 7) / 8 + 1);
        }

        // not necessary
        @Override
        protected void setBindImgDrawPositionImpl(Graphics2D g, BasePanel panel, Image img) {
            point.move(Main.config.borderOffset + insideOffset,
                    Main.config.borderOffset + insideOffset + (getPanelInsideHeight(panel)) / 8 + 1);
        }

        @Override
        protected int getEffectiveTitleHeight(BasePanel panel) {
            return (getPanelInsideHeight(panel) * 11) / 32;
        }

        @Override
        protected int getEffectiveTitleWidth(BasePanel panel) {
            return getPanelInsideWidth(panel);
        }

        // not necessary
        @Override
        protected int getEffectiveValueHeight(BasePanel panel) {
            return 0;
        }

        // not necessary
        @Override
        protected int getEffectiveValueWidth(BasePanel panel) {
            return 0;
        }

        // not necessary
        @Override
        protected int getEffectiveImgHeight(BasePanel panel) {
            return 0;
        }

        // not necessary
        @Override
        protected int getEffectiveImgWidth(BasePanel panel) {
            return 0;
        }
    },

    ONLY_IMAGE("Only image") {
        // not necessary
        @Override
        protected void setTitleDrawPositionImpl(FontMetrics metrics, Graphics2D g, Font font, BasePanel panel,
                String title) {
            point.move(
                    Main.config.borderOffset + insideOffset
                            + (int) Math.round((getPanelInsideWidth(panel) - metrics.stringWidth(title)) / 2.0D),
                    Main.config.borderOffset + insideOffset + (getPanelInsideHeight(panel) * 3) / 8 + 1);
        }

        // not necessary
        @Override
        protected void setValueDrawPositionImpl(FontMetrics metrics, Graphics2D g, Font font, BasePanel panel,
                String value) {
            point.move(0, 0);
        }

        @Override
        protected void setBindImgDrawPositionImpl(Graphics2D g, BasePanel panel, Image img) {
            point.move(
                    Main.config.borderOffset + insideOffset
                            + (int) Math.round((getPanelInsideWidth(panel) - img.getWidth(panel)) / 2.0D),
                    Main.config.borderOffset + insideOffset
                            + (int) Math.round((getPanelInsideHeight(panel) - img.getHeight(panel)) / 2.0D));
        }

        // not necessary
        @Override
        protected int getEffectiveTitleHeight(BasePanel panel) {
            return 0;
        }

        // not necessary
        @Override
        protected int getEffectiveTitleWidth(BasePanel panel) {
            return 0;
        }

        // not necessary
        @Override
        protected int getEffectiveValueHeight(BasePanel panel) {
            return 0;
        }

        // not necessary
        @Override
        protected int getEffectiveValueWidth(BasePanel panel) {
            return 0;
        }

        @Override
        protected int getEffectiveImgHeight(BasePanel panel) {
            return getPanelShorterLength(getPanelInsideWidth(panel), getPanelInsideHeight(panel));
        }

        @Override
        protected int getEffectiveImgWidth(BasePanel panel) {
            return getPanelShorterLength(getPanelInsideWidth(panel), getPanelInsideHeight(panel));
        }
    },
    /**
     * HORIZONTAL text rendering
     * The text is followed by the value
     */
    HORIZONTAL_TN("Text - value") {
        @Override
        protected void setTitleDrawPositionImpl(FontMetrics metrics, Graphics2D g, Font font, BasePanel panel,
                String title) {
            point.move(Main.config.borderOffset + insideOffset + 1,
                    Main.config.borderOffset + insideOffset + (getPanelInsideHeight(panel) + getHeight(g, font)) / 2);
        }

        @Override
        protected void setValueDrawPositionImpl(FontMetrics metrics, Graphics2D g, Font font, BasePanel panel,
                String value) {
            point.move(panel.getWidth() - Main.config.borderOffset - insideOffset - 1 - metrics.stringWidth(value),
                    Main.config.borderOffset + insideOffset + (getPanelInsideHeight(panel) + getHeight(g, font)) / 2);
        }

        @Override
        protected void setBindImgDrawPositionImpl(Graphics2D g, BasePanel panel, Image img) {
            // point.move(Main.config.borderOffset + insideOffset +
            // (int)Math.round((getPanelInsideWidth(panel) - img.getWidth(null)/ 2.0D)),
            // Main.config.borderOffset + insideOffset + (getPanelInsideHeight(panel) * 5) /
            // 8 + 1);
            point.move(Main.config.borderOffset + insideOffset,
                    Main.config.borderOffset + insideOffset + (getPanelInsideHeight(panel)) / 8 + 1);
        }

        @Override
        protected int getEffectiveTitleHeight(BasePanel panel) {
            return (getPanelInsideHeight(panel) * 5) / 8;
        }

        @Override
        protected int getEffectiveTitleWidth(BasePanel panel) {
            return getPanelInsideWidth(panel) / 2;
        }

        @Override
        protected int getEffectiveValueHeight(BasePanel panel) {
            return getPanelInsideHeight(panel) / 2;
        }

        @Override
        protected int getEffectiveValueWidth(BasePanel panel) {
            return getPanelInsideWidth(panel) / 2;
        }

        // not necessary
        @Override
        protected int getEffectiveImgHeight(BasePanel panel) {
            return 0;
        }

        // not necessary
        @Override
        protected int getEffectiveImgWidth(BasePanel panel) {
            return 0;
        }
    },
    /**
     * HORIZONTAL text rendering
     * The value is followed by the text
     */
    HORIZONTAL_NT("Value - text") {
        @Override
        protected void setTitleDrawPositionImpl(FontMetrics metrics, Graphics2D g, Font font, BasePanel panel,
                String title) {
            point.move(panel.getWidth() - Main.config.borderOffset - insideOffset - 1 - metrics.stringWidth(title),
                    Main.config.borderOffset + insideOffset + (getPanelInsideHeight(panel) + getHeight(g, font)) / 2);
        }

        @Override
        protected void setValueDrawPositionImpl(FontMetrics metrics, Graphics2D g, Font font, BasePanel panel,
                String value) {
            point.move(Main.config.borderOffset + insideOffset + 1,
                    Main.config.borderOffset + insideOffset + (getPanelInsideHeight(panel) + getHeight(g, font)) / 2);
        }

        @Override
        protected void setBindImgDrawPositionImpl(Graphics2D g, BasePanel panel, Image img) {
            // point.move(Main.config.borderOffset + insideOffset +
            // (int)Math.round((getPanelInsideWidth(panel) - img.getWidth(null)/ 2.0D)),
            // Main.config.borderOffset + insideOffset + (getPanelInsideHeight(panel) * 5) /
            // 8 + 1);
            point.move(Main.config.borderOffset + insideOffset,
                    Main.config.borderOffset + insideOffset + (getPanelInsideHeight(panel)) / 8 + 1);
        }

        @Override
        protected int getEffectiveTitleHeight(BasePanel panel) {
            return (getPanelInsideHeight(panel) * 5) / 8;
        }

        @Override
        protected int getEffectiveTitleWidth(BasePanel panel) {
            return getPanelInsideWidth(panel) / 2;
        }

        @Override
        protected int getEffectiveValueHeight(BasePanel panel) {
            return getPanelInsideHeight(panel) / 2;
        }

        @Override
        protected int getEffectiveValueWidth(BasePanel panel) {
            return getPanelInsideWidth(panel) / 2;
        }

        // not necessary
        @Override
        protected int getEffectiveImgHeight(BasePanel panel) {
            return 0;
        }

        // not necessary
        @Override
        protected int getEffectiveImgWidth(BasePanel panel) {
            return 0;
        }
    },
    /**
     * DIAGONAL text rendering
     * The text is diagonally right above the value
     */
    DIAGONAL1("Text diagonally right above value") {
        @Override
        protected void setTitleDrawPositionImpl(FontMetrics metrics, Graphics2D g, Font font, BasePanel panel,
                String title) {
            point.move(
                    Main.config.borderOffset + insideOffset + getPanelInsideWidth(panel) - 1
                            - metrics.stringWidth(title),
                    Main.config.borderOffset + insideOffset + 1 + getHeight(g, font));
        }

        @Override
        protected void setValueDrawPositionImpl(FontMetrics metrics, Graphics2D g, Font font, BasePanel panel,
                String value) {
            point.move(Main.config.borderOffset + insideOffset + 1,
                    Main.config.borderOffset + insideOffset + getPanelInsideHeight(panel) - 1);
        }

        @Override
        protected void setBindImgDrawPositionImpl(Graphics2D g, BasePanel panel, Image img) {
            // point.move(Main.config.borderOffset + insideOffset +
            // (int)Math.round((getPanelInsideWidth(panel) - img.getWidth(null)/ 2.0D)),
            // Main.config.borderOffset + insideOffset + (getPanelInsideHeight(panel) * 5) /
            // 8 + 1);
            point.move(Main.config.borderOffset + insideOffset,
                    Main.config.borderOffset + insideOffset + (getPanelInsideHeight(panel)) / 8 + 1);
        }

        @Override
        protected int getEffectiveTitleHeight(BasePanel panel) {
            return getPanelInsideHeight(panel) / 2 - 2;
        }

        @Override
        protected int getEffectiveTitleWidth(BasePanel panel) {
            return getPanelInsideWidth(panel) - 2;
        }

        @Override
        protected int getEffectiveValueHeight(BasePanel panel) {
            return getPanelInsideHeight(panel) / 2 - 2;
        }

        @Override
        protected int getEffectiveValueWidth(BasePanel panel) {
            return getPanelInsideWidth(panel) - 2;
        }

        // not necessary
        @Override
        protected int getEffectiveImgHeight(BasePanel panel) {
            return 0;
        }

        // not necessary
        @Override
        protected int getEffectiveImgWidth(BasePanel panel) {
            return 0;
        }
    },
    /**
     * DIAGONAL text rendering
     * The text is diagonally left under the value
     */
    DIAGONAL2("Text diagonally left under value") {
        @Override
        protected void setTitleDrawPositionImpl(FontMetrics metrics, Graphics2D g, Font font, BasePanel panel,
                String title) {
            point.move(Main.config.borderOffset + insideOffset + 1,
                    Main.config.borderOffset + insideOffset + getPanelInsideHeight(panel) - 1);
        }

        @Override
        protected void setValueDrawPositionImpl(FontMetrics metrics, Graphics2D g, Font font, BasePanel panel,
                String value) {
            point.move(
                    Main.config.borderOffset + insideOffset + getPanelInsideWidth(panel) - 1
                            - metrics.stringWidth(value),
                    Main.config.borderOffset + insideOffset + 1 + getHeight(g, font));
        }

        @Override
        protected void setBindImgDrawPositionImpl(Graphics2D g, BasePanel panel, Image img) {
            // point.move(Main.config.borderOffset + insideOffset +
            // (int)Math.round((getPanelInsideWidth(panel) - img.getWidth(null)/ 2.0D)),
            // Main.config.borderOffset + insideOffset + (getPanelInsideHeight(panel) * 5) /
            // 8 + 1);
            point.move(Main.config.borderOffset + insideOffset,
                    Main.config.borderOffset + insideOffset + (getPanelInsideHeight(panel)) / 8 + 1);
        }

        @Override
        protected int getEffectiveTitleHeight(BasePanel panel) {
            return getPanelInsideHeight(panel) / 2 - 2;
        }

        @Override
        protected int getEffectiveTitleWidth(BasePanel panel) {
            return getPanelInsideWidth(panel) - 2;
        }

        @Override
        protected int getEffectiveValueHeight(BasePanel panel) {
            return getPanelInsideHeight(panel) / 2 - 2;
        }

        @Override
        protected int getEffectiveValueWidth(BasePanel panel) {
            return getPanelInsideWidth(panel) - 2;
        }

        // not necessary
        @Override
        protected int getEffectiveImgHeight(BasePanel panel) {
            return 0;
        }

        // not necessary
        @Override
        protected int getEffectiveImgWidth(BasePanel panel) {
            return 0;
        }
    },
    /**
     * DIAGONAL text rendering
     * The text is diagonally left above the value
     */
    DIAGONAL3("Text diagonally left above value") {
        @Override
        protected void setTitleDrawPositionImpl(FontMetrics metrics, Graphics2D g, Font font, BasePanel panel,
                String title) {
            point.move(Main.config.borderOffset + insideOffset + 1,
                    Main.config.borderOffset + insideOffset + 1 + getHeight(g, font));
        }

        @Override
        protected void setValueDrawPositionImpl(FontMetrics metrics, Graphics2D g, Font font, BasePanel panel,
                String value) {
            point.move(
                    Main.config.borderOffset + insideOffset + getPanelInsideWidth(panel) - 1
                            - metrics.stringWidth(value),
                    Main.config.borderOffset + insideOffset + getPanelInsideHeight(panel) - 1);
        }

        @Override
        protected void setBindImgDrawPositionImpl(Graphics2D g, BasePanel panel, Image img) {
            // point.move(Main.config.borderOffset + insideOffset +
            // (int)Math.round((getPanelInsideWidth(panel) - img.getWidth(null)/ 2.0D)),
            // Main.config.borderOffset + insideOffset + (getPanelInsideHeight(panel) * 5) /
            // 8 + 1);
            point.move(Main.config.borderOffset + insideOffset,
                    Main.config.borderOffset + insideOffset + (getPanelInsideHeight(panel)) / 8 + 1);
        }

        @Override
        protected int getEffectiveTitleHeight(BasePanel panel) {
            return getPanelInsideHeight(panel) / 2 - 2;
        }

        @Override
        protected int getEffectiveTitleWidth(BasePanel panel) {
            return getPanelInsideWidth(panel) - 2;
        }

        @Override
        protected int getEffectiveValueHeight(BasePanel panel) {
            return getPanelInsideHeight(panel) / 2 - 2;
        }

        @Override
        protected int getEffectiveValueWidth(BasePanel panel) {
            return getPanelInsideWidth(panel) - 2;
        }

        // not necessary
        @Override
        protected int getEffectiveImgHeight(BasePanel panel) {
            return 0;
        }

        // not necessary
        @Override
        protected int getEffectiveImgWidth(BasePanel panel) {
            return 0;
        }
    },
    /**
     * DIAGONAL text rendering
     * The text is diagonally right under the value
     */
    DIAGONAL4("Text diagonally right under value") {
        @Override
        protected void setTitleDrawPositionImpl(FontMetrics metrics, Graphics2D g, Font font, BasePanel panel,
                String title) {
            point.move(
                    Main.config.borderOffset + insideOffset + getPanelInsideWidth(panel) - 1
                            - metrics.stringWidth(title),
                    Main.config.borderOffset + insideOffset + getPanelInsideHeight(panel) - 1);
        }

        @Override
        protected void setValueDrawPositionImpl(FontMetrics metrics, Graphics2D g, Font font, BasePanel panel,
                String value) {
            point.move(Main.config.borderOffset + insideOffset + 1,
                    Main.config.borderOffset + insideOffset + 1 + getHeight(g, font));
        }

        @Override
        protected void setBindImgDrawPositionImpl(Graphics2D g, BasePanel panel, Image img) {
            // point.move(Main.config.borderOffset + insideOffset +
            // (int)Math.round((getPanelInsideWidth(panel) - img.getWidth(null)/ 2.0D)),
            // Main.config.borderOffset + insideOffset + (getPanelInsideHeight(panel) * 5) /
            // 8 + 1);
            point.move(Main.config.borderOffset + insideOffset,
                    Main.config.borderOffset + insideOffset + (getPanelInsideHeight(panel)) / 8 + 1);
        }

        @Override
        protected int getEffectiveTitleHeight(BasePanel panel) {
            return getPanelInsideHeight(panel) / 2 - 2;
        }

        @Override
        protected int getEffectiveTitleWidth(BasePanel panel) {
            return getPanelInsideWidth(panel) - 2;
        }

        @Override
        protected int getEffectiveValueHeight(BasePanel panel) {
            return getPanelInsideHeight(panel) / 2 - 2;
        }

        @Override
        protected int getEffectiveValueWidth(BasePanel panel) {
            return getPanelInsideWidth(panel) - 2;
        }

        // not necessary
        @Override
        protected int getEffectiveImgHeight(BasePanel panel) {
            return 0;
        }

        // not necessary
        @Override
        protected int getEffectiveImgWidth(BasePanel panel) {
            return 0;
        }
    },
    /**
     * VERTICAL text rendering
     * The text is above the value
     */
    VERTICAL("Text above value") {
        @Override
        protected void setTitleDrawPositionImpl(FontMetrics metrics, Graphics2D g, Font font, BasePanel panel,
                String title) {
            point.move(
                    Main.config.borderOffset + insideOffset
                            + (int) Math.round((getPanelInsideWidth(panel) - metrics.stringWidth(title)) / 2.0D),
                    Main.config.borderOffset + insideOffset + (getPanelInsideHeight(panel) * 3) / 8 + 1);
        }

        @Override
        protected void setValueDrawPositionImpl(FontMetrics metrics, Graphics2D g, Font font, BasePanel panel,
                String value) {
            point.move(
                    Main.config.borderOffset + insideOffset
                            + (int) Math.round((getPanelInsideWidth(panel) - metrics.stringWidth(value)) / 2.0D),
                    Main.config.borderOffset + insideOffset + (getPanelInsideHeight(panel) * 7) / 8 + 1);
        }

        // not neccessary
        @Override
        protected void setBindImgDrawPositionImpl(Graphics2D g, BasePanel panel, Image img) {
            // point.move(Main.config.borderOffset + insideOffset +
            // (int)Math.round((getPanelInsideWidth(panel) - img.getWidth(null)/ 2.0D)),
            // Main.config.borderOffset + insideOffset + (getPanelInsideHeight(panel) * 5) /
            // 8 + 1);
            point.move(Main.config.borderOffset + insideOffset,
                    Main.config.borderOffset + insideOffset + (getPanelInsideHeight(panel)) / 8 + 1);
        }

        @Override
        protected int getEffectiveTitleHeight(BasePanel panel) {
            return (getPanelInsideHeight(panel) * 11) / 32;
        }

        @Override
        protected int getEffectiveTitleWidth(BasePanel panel) {
            return getPanelInsideWidth(panel);
        }

        @Override
        protected int getEffectiveValueHeight(BasePanel panel) {
            return (getPanelInsideHeight(panel) * 9) / 32;
        }

        @Override
        protected int getEffectiveValueWidth(BasePanel panel) {
            return getPanelInsideWidth(panel);
        }

        // not necessary
        @Override
        protected int getEffectiveImgHeight(BasePanel panel) {
            return 0;
        }

        // not necessary
        @Override
        protected int getEffectiveImgWidth(BasePanel panel) {
            return 0;
        }
    },

    VERTICAL_TEXT_WITH_IMAGE("Text above image") {
        @Override
        protected void setTitleDrawPositionImpl(FontMetrics metrics, Graphics2D g, Font font, BasePanel panel,
                String title) {
            point.move(
                    Main.config.borderOffset + insideOffset
                            + (int) Math.round((getPanelInsideWidth(panel) - metrics.stringWidth(title)) / 2.0D),
                    Main.config.borderOffset + insideOffset + (getPanelInsideHeight(panel) * 3) / 8 + 1);
        }

        // not necessary
        @Override
        protected void setValueDrawPositionImpl(FontMetrics metrics, Graphics2D g, Font font, BasePanel panel,
                String value) {
            point.move(0, 0);
        }

        @Override
        protected void setBindImgDrawPositionImpl(Graphics2D g, BasePanel panel, Image img) {
            point.move(
                    Main.config.borderOffset + insideOffset
                            + (int) Math.round((getPanelInsideWidth(panel) - img.getWidth(panel)) / 2.0D),
                    Main.config.borderOffset + insideOffset
                            + (int) ((getPanelInsideHeight(panel) * 0.7D - img.getHeight(panel) / 2.0D)));
        }

        @Override
        protected int getEffectiveTitleHeight(BasePanel panel) {
            return (getPanelInsideHeight(panel) * 11) / 32;
        }

        @Override
        protected int getEffectiveTitleWidth(BasePanel panel) {
            return getPanelInsideWidth(panel);
        }

        @Override
        protected int getEffectiveValueHeight(BasePanel panel) {
            return 0;
        }

        @Override
        protected int getEffectiveValueWidth(BasePanel panel) {
            return 0;
        }

        @Override
        protected int getEffectiveImgHeight(BasePanel panel) {
            return (getPanelShorterLength(getPanelInsideWidth(panel), getPanelInsideHeight(panel)) * 11) / 19;
        }

        @Override
        protected int getEffectiveImgWidth(BasePanel panel) {
            return (getPanelShorterLength(getPanelInsideWidth(panel), getPanelInsideHeight(panel)) * 11) / 19;
        }
    },

    VERTICAL_VALUE_WITH_IMAGE("Image above value") {
        // not necessary
        @Override
        protected void setTitleDrawPositionImpl(FontMetrics metrics, Graphics2D g, Font font, BasePanel panel,
                String title) {
            point.move(
                    Main.config.borderOffset + insideOffset
                            + (int) Math.round((getPanelInsideWidth(panel) - metrics.stringWidth(title)) / 2.0D),
                    Main.config.borderOffset + insideOffset + (getPanelInsideHeight(panel) * 3) / 8 + 1);
        }

        @Override
        protected void setValueDrawPositionImpl(FontMetrics metrics, Graphics2D g, Font font, BasePanel panel,
                String value) {
            point.move(
                    Main.config.borderOffset + insideOffset
                            + (int) Math.round((getPanelInsideWidth(panel) - metrics.stringWidth(value)) / 2.0D),
                    Main.config.borderOffset + insideOffset + (getPanelInsideHeight(panel) * 7) / 8 + 1);
        }

        @Override
        protected void setBindImgDrawPositionImpl(Graphics2D g, BasePanel panel, Image img) {
            point.move(
                    Main.config.borderOffset + insideOffset
                            + (int) Math.round((getPanelInsideWidth(panel) - img.getWidth(panel)) / 2.0D),
                    Main.config.borderOffset + insideOffset
                            + (int) Math.round((getPanelInsideHeight(panel) * 0.3D - img.getHeight(panel) / 2.0D)));
        }

        @Override
        protected int getEffectiveTitleHeight(BasePanel panel) {
            return 0;
        }

        @Override
        protected int getEffectiveTitleWidth(BasePanel panel) {
            return 0;
        }

        // not necessary
        @Override
        protected int getEffectiveValueHeight(BasePanel panel) {
            return (getPanelInsideHeight(panel) * 9) / 32;
        }

        // not necessary
        @Override
        protected int getEffectiveValueWidth(BasePanel panel) {
            return getPanelInsideWidth(panel);
        }

        @Override
        protected int getEffectiveImgHeight(BasePanel panel) {
            return (getPanelInsideHeight(panel) * 9) / 19;
        }

        @Override
        protected int getEffectiveImgWidth(BasePanel panel) {
            return (getPanelInsideHeight(panel) * 9) / 19;
        }
    },

    VERTICAL_TEXT_VALUE_IMAGE("Text above value, image in background") {
        // not necessary
        @Override
        protected void setTitleDrawPositionImpl(FontMetrics metrics, Graphics2D g, Font font, BasePanel panel,
                String title) {
            point.move(
                    Main.config.borderOffset + insideOffset
                            + (int) Math.round((getPanelInsideWidth(panel) - metrics.stringWidth(title)) / 2.0D),
                    Main.config.borderOffset + insideOffset + (getPanelInsideHeight(panel) * 3) / 8 + 1);
        }

        // not necessary
        @Override
        protected void setValueDrawPositionImpl(FontMetrics metrics, Graphics2D g, Font font, BasePanel panel,
                String value) {
            point.move(
                    Main.config.borderOffset + insideOffset
                            + (int) Math.round((getPanelInsideWidth(panel) - metrics.stringWidth(value)) / 2.0D),
                    Main.config.borderOffset + insideOffset + (getPanelInsideHeight(panel) * 7) / 8 + 1);
        }

        @Override
        protected void setBindImgDrawPositionImpl(Graphics2D g, BasePanel panel, Image img) {
            point.move(
                    Main.config.borderOffset + insideOffset
                            + (int) Math.round((getPanelInsideWidth(panel) - img.getWidth(panel)) / 2.0D),
                    Main.config.borderOffset + insideOffset
                            + (int) Math.round((getPanelInsideHeight(panel) - img.getHeight(panel)) / 2.0D));
        }

        @Override
        protected int getEffectiveTitleHeight(BasePanel panel) {
            return (getPanelInsideHeight(panel) * 11) / 32;
        }

        @Override
        protected int getEffectiveTitleWidth(BasePanel panel) {
            return getPanelInsideWidth(panel);
        }

        @Override
        protected int getEffectiveValueHeight(BasePanel panel) {
            return (getPanelInsideHeight(panel) * 9) / 32;
        }

        @Override
        protected int getEffectiveValueWidth(BasePanel panel) {
            return getPanelInsideWidth(panel);
        }

        @Override
        protected int getEffectiveImgHeight(BasePanel panel) {
            return getPanelShorterLength(getPanelInsideWidth(panel), getPanelInsideHeight(panel));
        }

        @Override
        protected int getEffectiveImgWidth(BasePanel panel) {
            return getPanelShorterLength(getPanelInsideWidth(panel), getPanelInsideHeight(panel));
        }
    };

    /**
     * The display name of the
     * enum constant, used in dialogs
     */
    private String name;
    /**
     * Cache point that is constantly being reused and
     * returned by the methods from this enum. This prevents
     * the creation of a lot of extra objects. But prevents
     * multiple points from this enum from existing at the same time.
     */
    private static final Point point = new Point();
    /**
     * Reference character for string width and height measurements.
     * This is used to give strings with the same number of characters
     * the same appearance.
     */
    private static final char[] ref = new char[] { 'R' };
    /**
     * Offset from the panel border offset to the inside
     * of the panel (inside the image)
     */
    public static final int insideOffset = 3;

    /**
     * Constructs a new RenderingMode
     * with the given name
     * 
     * @param n The display name of the mode
     */
    private RenderingMode(String n) {
        name = n;
    }

    /**
     * Mode specific logic for the title position
     * 
     * @param metrics FontMetrics that will be used to draw the title
     * @param g       The graphics used to render the title
     * @param font    The font used to draw the title
     * @param panel   The panel the title will be drawn on
     * @param title   The title that will be drawn
     */
    protected abstract void setTitleDrawPositionImpl(FontMetrics metrics, Graphics2D g, Font font, BasePanel panel,
            String title);

    /**
     * Mode specific logic for the value position
     * 
     * @param metrics FontMetrics that will be used to draw the value
     * @param g       The graphics used to render the value
     * @param font    The font used to draw the value
     * @param panel   The panel the value will be drawn on
     * @param value   The value that will be drawn
     */
    protected abstract void setValueDrawPositionImpl(FontMetrics metrics, Graphics2D g, Font font, BasePanel panel,
            String value);

    protected abstract void setBindImgDrawPositionImpl(Graphics2D g, BasePanel panel, Image img);

    /**
     * Gets the effective height available to draw the title
     * 
     * @param panel The panel to get the effective height for
     * @return The effective height available to draw the title
     */
    protected abstract int getEffectiveTitleHeight(BasePanel panel);

    /**
     * Gets the effective width available to draw the title
     * 
     * @param panel The panel to get the effective width for
     * @return The effective width available to draw the title
     */
    protected abstract int getEffectiveTitleWidth(BasePanel panel);

    /**
     * Gets the effective height available to draw the value
     * 
     * @param panel The panel to get the effective height for
     * @return The effective height available to draw the value
     */
    protected abstract int getEffectiveValueHeight(BasePanel panel);

    /**
     * Gets the effective width available to draw the value
     * 
     * @param panel The panel to get the effective width for
     * @return The effective width available to draw the value
     */
    protected abstract int getEffectiveValueWidth(BasePanel panel);

    protected abstract int getEffectiveImgHeight(BasePanel panel);

    protected abstract int getEffectiveImgWidth(BasePanel panel);

    /**
     * Gets the location at which the panel title should be drawn
     * Points returned by this class are dynamic and change on a call
     * to either {@link #getTitleDrawPosition(Graphics2D, BasePanel, String, Font)}
     * or {@link #getValueDrawPosition(Graphics2D, BasePanel, String, Font)}.
     * 
     * @param g     The graphics used to draw the title
     * @param panel The panel the title will be drawn on
     * @param title The title that is going to be drawn
     * @param font  The font with which the title is going to be drawn
     * @return The location at which the title should be drawn
     */
    protected Point getTitleDrawPosition(Graphics2D g, BasePanel panel, String title, Font font) {
        setTitleDrawPositionImpl(g.getFontMetrics(font), g, font, panel, title);
        return point;
    }

    /**
     * Gets the location at which the panel title should be drawn
     * Points returned by this class are dynamic and change on a call
     * to either {@link #getTitleDrawPosition(Graphics2D, BasePanel, String, Font)}
     * or {@link #getValueDrawPosition(Graphics2D, BasePanel, String, Font)}.
     * 
     * @param g     The graphics used to draw the value
     * @param panel The panel the value will be drawn on
     * @param value The value that is going to be drawn
     * @param font  The font with which the value is going to be drawn
     * @return The location at which the value should be drawn
     */
    protected Point getValueDrawPosition(Graphics2D g, BasePanel panel, String value, Font font) {
        setValueDrawPositionImpl(g.getFontMetrics(font), g, font, panel, value);
        return point;
    }

    protected Point getBindImgeDrawPosition(Graphics2D g, BasePanel panel, Image img) {
        setBindImgDrawPositionImpl(g, panel, img);
        return point;
    }

    /**
     * Gets the title font that should be used to draw the
     * panel title
     * 
     * @param text        The title that is going to be drawn
     * @param g           The graphics that will be used to draw the title
     * @param panel       The panel the title will be drawn on
     * @param currentFont The font last used to draw this title
     * @return The font to use this time to draw the title
     */
    protected Font getTitleFont(String text, Graphics2D g, BasePanel panel, Font currentFont) {
        return resolveFont(text, g, getEffectiveTitleWidth(panel), getEffectiveTitleHeight(panel), Font.BOLD,
                currentFont);
    }

    /**
     * Gets the value font that should be used to draw the
     * panel value
     * 
     * @param text        The value that is going to be drawn
     * @param g           The graphics that will be used to draw the value
     * @param panel       The panel the value will be drawn on
     * @param currentFont The font last used to draw this value
     * @return The font to use this time to draw the value
     */
    protected Font getValueFont(String text, Graphics2D g, BasePanel panel, Font currentFont) {
        return resolveFont(text, g, getEffectiveValueWidth(panel), getEffectiveValueHeight(panel), Font.PLAIN,
                currentFont);
    }

    protected Image getScaledImg(Image img, Graphics2D g, BasePanel panel) {
        return scaleImg(img, g, getEffectiveImgWidth(panel) - 1, getEffectiveImgHeight(panel) - 1);
    }

    /**
     * Gets the inside height in pixel of the
     * given panel. The inside height is the
     * height inside of the border image
     * 
     * @param panel The panel to get the inside height for
     * @return The inside height of the panel
     */
    private static final int getPanelInsideHeight(BasePanel panel) {
        return panel.getHeight() - (Main.config.borderOffset + insideOffset) * 2;
    }

    /**
     * Gets the inside width in pixel of the
     * given panel. The inside width is the
     * width inside of the border image
     * 
     * @param panel The panel to get the inside width for
     * @return The inside width of the panel
     */
    private static final int getPanelInsideWidth(BasePanel panel) {
        return panel.getWidth() - (Main.config.borderOffset + insideOffset) * 2;
    }

    private static final int getPanelShorterLength(int w, int h) {
        return w < h ? w : h;
    }

    /**
     * Computes the font that should be used to
     * draw the given text based on the given
     * constraints
     * 
     * @param text        The text that is going to be drawn
     * @param g           The graphics that are going to be used to draw the text
     * @param maxWidth    The maximum width the drawn text may be
     * @param maxHeight   The maximum height the drawn text may be
     * @param properties  The flags to pass on to the Font
     * @param currentFont The font currently being used by the panel
     * @return The new font to use to draw the text for the panel
     */
    private static final Font resolveFont(String text, Graphics2D g, int maxWidth, int maxHeight, int properties,
            Font currentFont) {
        FontMetrics fm;
        if (currentFont != null) {
            fm = g.getFontMetrics(currentFont);
            if (getHeight(g, currentFont) <= maxHeight && stringWidth(text, fm) <= maxWidth) {
                return currentFont;
            }
        }

        int size = (int) (maxHeight * (Toolkit.getDefaultToolkit().getScreenResolution() / 72.0));
        Font font;
        do {
            font = new Font("Dialog", properties, size);
            fm = g.getFontMetrics(font);
            size--;
        } while (!(getHeight(g, font) <= maxHeight && stringWidth(text, fm) <= maxWidth));

        return font;
    }

    private static final Image scaleImg(Image img, Graphics2D g, int imgWidth, int imgHeight) {
        Image scaledImg = img;
        scaledImg = img.getScaledInstance(imgWidth, imgHeight, Image.SCALE_SMOOTH);
        return scaledImg;
    }

    /**
     * Gets the height of drawn text in pixels
     * for the given font
     * 
     * @param g    The graphics that will be used for drawing
     * @param font The font to check
     * @return The height of drawn text in the given font
     */
    private static final int getHeight(Graphics2D g, Font font) {
        return font.createGlyphVector(g.getFontRenderContext(), ref).getPixelBounds(null, 0.0F, 0.0F).height;
    }

    /**
     * Gets the width of the given string
     * using the given font metrics
     * 
     * @param text The text to get the width for
     * @param fm   The font metrics to used to find this width
     * @return The width of the given string
     */
    private static final int stringWidth(String text, FontMetrics fm) {
        return Math.max(fm.charWidth(ref[0]) * text.length(), fm.stringWidth(text));
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Simple cache that caches rendering information
     * so that it does not have to be recomputed for
     * every repaint event
     * 
     * @author Roan
     */
    public static class RenderCache {
        /**
         * The rendering mode this cache is for
         */
        private RenderingMode mode;
        /**
         * The last valid draw position for the value string
         */
        private Point valuePos;
        /**
         * The last valid draw position for the title string
         */
        private Point titlePos;
        /**
         * The last valid font for the value string
         */
        private Font valueFont;
        /**
         * The last valid font for the title string
         */
        private Font titleFont;
        /**
         * The last known length of the title string
         */
        private int titleLen;
        /**
         * The last known length of the value string
         */
        private int valueLen;

        private static Map<String, Image> bindImgMap = new HashMap<String, Image>();

        private static Image DUMMY_KEY = null;

        /**
         * Initialises and resets this cache
         * for the given rendering mode
         * 
         * @param mode The rendering mode to
         *             initialise this cache for
         */
        public final void init(RenderingMode mode) {
            this.mode = mode;
            valuePos = null;
            titlePos = null;
            valueFont = null;
            titleFont = null;
            titleLen = -1;
            valueLen = -1;
        }

        /**
         * Renders the title string on the given panel with
         * the given graphics according to the cached information
         * 
         * @param title The title to render
         * @param g     The graphics to use to draw this title
         * @param panel The panel to draw this title on
         */
        public final void renderTitle(String title, Graphics2D g, BasePanel panel) {
            if (titleLen != title.length()) {
                titleLen = title.length();
                titleFont = mode.getTitleFont(title, g, panel, titleFont);
                titlePos = mode.getTitleDrawPosition(g, panel, title, titleFont).getLocation();
            }

            g.setFont(titleFont);
            g.drawString(title, titlePos.x, titlePos.y);
        }

        /**
         * Renders the value string on the given panel with
         * the given graphics according to the cached information
         * 
         * @param value The value to render
         * @param g     The graphics to use to draw this value
         * @param panel The panel to draw this value on
         */
        public final void renderValue(String value, Graphics2D g, BasePanel panel) {
            if (valueLen != value.length()) {
                valueLen = value.length();
                valueFont = mode.getValueFont(value, g, panel, valueFont);
                valuePos = mode.getValueDrawPosition(g, panel, value, valueFont).getLocation();
            }

            g.setFont(valueFont);
            g.drawString(value, valuePos.x, valuePos.y);
        }

        public final void renderBindImg(String bImg, Graphics2D g, BasePanel panel, RenderingMode mode,
                boolean active) {
            // alpha => 0~100 (100 is opacity)
            int alpha = 0;
            Image img;
            Point imagePos;

            switch (mode) {
                case ONLY_IMAGE:
                case VERTICAL_TEXT_WITH_IMAGE:
                case VERTICAL_VALUE_WITH_IMAGE:
                    alpha = 100;
                    break;
                case VERTICAL_TEXT_VALUE_IMAGE:
                    alpha = 60;
                default:
            }

            if (!bindImgMap.containsKey(bImg)) {
                try {
                    if (BindImgDialog.class.getResource("").getProtocol() == "file") {
                        String projectPath = new File(".").getAbsoluteFile().toString().replace("\\", "/");
                        String filePath = projectPath.substring(0, projectPath.lastIndexOf("K")) + "images/" + bImg;

                        // bindImgMap.put(bImg,
                        // ImageIO.read(RenderCache.class.getResourceAsStream("/LS/" + bImg)));
                        bindImgMap.put(bImg, ImageIO.read(new File(filePath)));
                    } else {
                        String filePath = "";
                        String curPath = BindImgDialog.class.getProtectionDomain().getCodeSource().getLocation()
                                .getPath().toString();
                        filePath = curPath.substring(0, curPath.lastIndexOf("/")) + "/images/" + bImg;
                        bindImgMap.put(bImg, ImageIO.read(new File(filePath)));
                    }
                } catch (IOException e) {
                } catch (IllegalArgumentException e) {
                }
            }

            img = bindImgMap.getOrDefault(bImg, DUMMY_KEY);
            if (img != null) {
                img = mode.getScaledImg(img, g, panel);
                imagePos = mode.getBindImgeDrawPosition(g, panel, img).getLocation();
                if (active) {
                    alpha = 30;
                }
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha / 100.0F));
                g.drawImage(img, imagePos.x, imagePos.y, panel);
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0));

            }
        }
    }
}
