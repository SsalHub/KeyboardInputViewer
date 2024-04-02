package main.ssalhub.kiv.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Window;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.imageio.ImageIO;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import main.ssalhub.kiv.GraphMode;
import main.ssalhub.kiv.Main;
import main.ssalhub.kiv.Main.KeyInformation;
import main.ssalhub.kiv.RenderingMode;
import main.ssalhub.kiv.layout.LayoutValidator;
import main.ssalhub.kiv.layout.Positionable;
import main.ssalhub.kiv.panels.BasePanel;
import main.ssalhub.kiv.ui.model.DynamicInteger;
import main.ssalhub.kiv.ui.model.EndNumberModel;
import main.ssalhub.kiv.ui.model.MaxNumberModel;
import locallib.roanh.util.Dialog;

/**
 * Logic for the layout configuration dialog.
 * 
 * @author Roan
 */
public class LayoutDialog {
    /**
     * Positionable for the average panel.
     */

    public static boolean live;
    public static Window parent;

    private static final Positionable avgItem = new Positionable() {

        @Override
        public void setX(int x) {
            Main.config.avg_x = x;
        }

        @Override
        public void setY(int y) {
            Main.config.avg_y = y;
        }

        @Override
        public void setWidth(int w) {
            Main.config.avg_w = w;
        }

        @Override
        public void setHeight(int h) {
            Main.config.avg_h = h;
        }

        @Override
        public String getName() {
            return "AVG";
        }

        @Override
        public int getX() {
            return Main.config.avg_x;
        }

        @Override
        public int getY() {
            return Main.config.avg_y;
        }

        @Override
        public int getWidth() {
            return Main.config.avg_w;
        }

        @Override
        public int getHeight() {
            return Main.config.avg_h;
        }

        @Override
        public RenderingMode getRenderingMode() {
            return Main.config.avg_mode;
        }

        @Override
        public void setRenderingMode(RenderingMode mode) {
            Main.config.avg_mode = mode;
        }

        @Override
        public String getBindingImg() {
            return Main.config.avg_bImg;
        }

        @Override
        public void setBindingImg(String bImg) {
            Main.config.avg_bImg = bImg;
        }
    };
    /**
     * Positionable for the maximum panel.
     */
    private static final Positionable maxItem = new Positionable() {

        @Override
        public void setX(int x) {
            Main.config.max_x = x;
        }

        @Override
        public void setY(int y) {
            Main.config.max_y = y;
        }

        @Override
        public void setWidth(int w) {
            Main.config.max_w = w;
        }

        @Override
        public void setHeight(int h) {
            Main.config.max_h = h;
        }

        @Override
        public String getName() {
            return "MAX";
        }

        @Override
        public int getX() {
            return Main.config.max_x;
        }

        @Override
        public int getY() {
            return Main.config.max_y;
        }

        @Override
        public int getWidth() {
            return Main.config.max_w;
        }

        @Override
        public int getHeight() {
            return Main.config.max_h;
        }

        @Override
        public RenderingMode getRenderingMode() {
            return Main.config.max_mode;
        }

        @Override
        public void setRenderingMode(RenderingMode mode) {
            Main.config.max_mode = mode;
        }

        @Override
        public String getBindingImg() {
            return Main.config.max_bImg;
        }

        @Override
        public void setBindingImg(String bImg) {
            Main.config.max_bImg = bImg;
        }
    };
    /**
     * Positionable for the current panel.
     */
    private static final Positionable curItem = new Positionable() {

        @Override
        public void setX(int x) {
            Main.config.cur_x = x;
        }

        @Override
        public void setY(int y) {
            Main.config.cur_y = y;
        }

        @Override
        public void setWidth(int w) {
            Main.config.cur_w = w;
        }

        @Override
        public void setHeight(int h) {
            Main.config.cur_h = h;
        }

        @Override
        public String getName() {
            return "CUR";
        }

        @Override
        public int getX() {
            return Main.config.cur_x;
        }

        @Override
        public int getY() {
            return Main.config.cur_y;
        }

        @Override
        public int getWidth() {
            return Main.config.cur_w;
        }

        @Override
        public int getHeight() {
            return Main.config.cur_h;
        }

        @Override
        public RenderingMode getRenderingMode() {
            return Main.config.cur_mode;
        }

        @Override
        public void setRenderingMode(RenderingMode mode) {
            Main.config.cur_mode = mode;
        }

        @Override
        public String getBindingImg() {
            return Main.config.cur_bImg;
        }

        @Override
        public void setBindingImg(String bImg) {
            Main.config.cur_bImg = bImg;
        }
    };
    /**
     * Positionable for the total panel.
     */
    private static final Positionable totItem = new Positionable() {

        @Override
        public void setX(int x) {
            Main.config.tot_x = x;
        }

        @Override
        public void setY(int y) {
            Main.config.tot_y = y;
        }

        @Override
        public void setWidth(int w) {
            Main.config.tot_w = w;
        }

        @Override
        public void setHeight(int h) {
            Main.config.tot_h = h;
        }

        @Override
        public String getName() {
            return "TOT";
        }

        @Override
        public int getX() {
            return Main.config.tot_x;
        }

        @Override
        public int getY() {
            return Main.config.tot_y;
        }

        @Override
        public int getWidth() {
            return Main.config.tot_w;
        }

        @Override
        public int getHeight() {
            return Main.config.tot_h;
        }

        @Override
        public RenderingMode getRenderingMode() {
            return Main.config.tot_mode;
        }

        @Override
        public void setRenderingMode(RenderingMode mode) {
            Main.config.tot_mode = mode;
        }

        @Override
        public String getBindingImg() {
            return Main.config.tot_bImg;
        }

        @Override
        public void setBindingImg(String bImg) {
            Main.config.tot_bImg = bImg;
        }

    };
    private static final Positionable tmpItem = new Positionable() {
        int width = 0;
        int height = 0;

        @Override
        public void setX(int x) {
            return;
        }

        @Override
        public void setY(int y) {
            return;
        }

        @Override
        public void setWidth(int w) {
            this.width = w;
        }

        @Override
        public void setHeight(int h) {
            this.height = h;
        }

        @Override
        public String getName() {
            return "";
        }

        @Override
        public int getX() {
            return -1;
        }

        @Override
        public int getY() {
            return 0;
        }

        @Override
        public int getWidth() {
            return width;
        }

        @Override
        public int getHeight() {
            return height;
        }

        @Override
        public RenderingMode getRenderingMode() {
            return null;
        }

        @Override
        public void setRenderingMode(RenderingMode mode) {
            // Main.config.tot_mode = mode;
            for (KeyInformation i : Main.config.keyinfo) {
                i.setRenderingMode(mode);
            }
            if (Main.config.showAvg) {
                Main.config.avg_mode = mode;
            }
            if (Main.config.showMax) {
                Main.config.max_mode = mode;
            }
            if (Main.config.showCur) {
                Main.config.cur_mode = mode;
            }
            if (Main.config.showTotal) {
                Main.config.tot_mode = mode;
            }
        }

        @Override
        public String getBindingImg() {
            return null;
        }

        @Override
        public void setBindingImg(String bImg) {
            return;
        }
    };

    private static final Positionable allItem = new Positionable() {

        @Override
        public void setX(int x) {
            return;
        }

        @Override
        public void setY(int y) {
            return;
        }

        @Override
        public void setWidth(int w) {
            for (KeyInformation i : Main.config.keyinfo) {
                i.setWidth(w);
            }
            if (Main.config.showAvg) {
                Main.config.avg_w = w;
            }
            if (Main.config.showMax) {
                Main.config.max_w = w;
            }
            if (Main.config.showCur) {
                Main.config.cur_w = w;
            }
            if (Main.config.showTotal) {
                Main.config.tot_w = w;
            }
        }

        @Override
        public void setHeight(int h) {
            for (KeyInformation i : Main.config.keyinfo) {
                i.setHeight(h);
            }
            if (Main.config.showAvg) {
                Main.config.avg_h = h;
            }
            if (Main.config.showMax) {
                Main.config.max_h = h;
            }
            if (Main.config.showCur) {
                Main.config.cur_h = h;
            }
            if (Main.config.showTotal) {
                Main.config.tot_h = h;
            }
        }

        @Override
        public String getName() {
            return "";
        }

        @Override
        public int getX() {
            return 0;
        }

        @Override
        public int getY() {
            return 0;
        }

        @Override
        public int getWidth() {
            return 0;
        }

        @Override
        public int getHeight() {
            return 0;
        }

        @Override
        public RenderingMode getRenderingMode() {
            return null;
        }

        @Override
        public void setRenderingMode(RenderingMode mode) {
            // Main.config.tot_mode = mode;
            for (KeyInformation i : Main.config.keyinfo) {
                i.setRenderingMode(mode);
            }
            if (Main.config.showAvg) {
                Main.config.avg_mode = mode;
            }
            if (Main.config.showMax) {
                Main.config.max_mode = mode;
            }
            if (Main.config.showCur) {
                Main.config.cur_mode = mode;
            }
            if (Main.config.showTotal) {
                Main.config.tot_mode = mode;
            }
        }

        @Override
        public String getBindingImg() {
            return null;
        }

        @Override
        public void setBindingImg(String bImg) {
            return;
        }
    };

    /**
     * Shows the layout configuration dialog
     * 
     * @param live Whether or not changes should be
     *             applied in real time
     */
    public static final void configureLayout(boolean live) {
        Main.content.showGrid();

        JPanel form = new JPanel(new BorderLayout());
        Image icon;
        String[] options = new String[] { "OK" };

        try {
            icon = ImageIO.read(ClassLoader.getSystemResource("kps_small.png"));
        } catch (IOException e) {
            icon = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
        }

        JOptionPane optionPane = new JOptionPane(form, JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, options, 0);
        optionPane.setActionMap(new ActionMap());

        JDialog dialog = optionPane.createDialog(parent, "Layout Configuration");

        JPanel fields = new JPanel(new GridLayout(0, 5, 2, 2));
        JPanel modes = new JPanel(new GridLayout(0, 1, 0, 2));
        JPanel bindImgs = new JPanel(new GridLayout(0, 1, 0, 2));

        fields.add(new JLabel("Key", SwingConstants.CENTER));
        fields.add(new JLabel("X", SwingConstants.CENTER));
        fields.add(new JLabel("Y", SwingConstants.CENTER));
        fields.add(new JLabel("Width", SwingConstants.CENTER));
        fields.add(new JLabel("Height", SwingConstants.CENTER));
        modes.add(new JLabel("Mode", SwingConstants.CENTER));
        bindImgs.add(new JLabel("Bind", SwingConstants.CENTER));

        ArrayList<JSpinner[]> borderSpinners = new ArrayList<JSpinner[]>();
        ArrayList<JComboBox<RenderingMode>> modeComboBoxes = new ArrayList<JComboBox<RenderingMode>>();
        JPanel keys = new JPanel(new BorderLayout());

        for (KeyInformation i : Main.config.keyinfo) {
            createListItem(dialog, i, fields, borderSpinners, modes, modeComboBoxes, bindImgs, live);
        }
        if (Main.config.showAvg) {
            createListItem(dialog, avgItem, fields, borderSpinners, modes, modeComboBoxes, bindImgs, live);
        }
        if (Main.config.showMax) {
            createListItem(dialog, maxItem, fields, borderSpinners, modes, modeComboBoxes, bindImgs, live);
        }
        if (Main.config.showCur) {
            createListItem(dialog, curItem, fields, borderSpinners, modes, modeComboBoxes, bindImgs, live);
        }
        if (Main.config.showTotal) {
            createListItem(dialog, totItem, fields, borderSpinners, modes, modeComboBoxes, bindImgs, live);
        }

        /**
         * Advanced Settings Button
         */
        JButton advSettingsBtn = new JButton("Advanced Settings");
        advSettingsBtn.setBounds(100, 150, 0, 50);
        advSettingsBtn.addActionListener(((e) -> {
            LayoutAdvSettingDialog.live = false;
            LayoutAdvSettingDialog.parentSpinners = borderSpinners;
            LayoutAdvSettingDialog.parentModes = modeComboBoxes;
            LayoutAdvSettingDialog.configureLayoutAdvSetting(live);
        }));

        // JPanel keys = new JPanel(new BorderLayout());
        keys.add(advSettingsBtn, BorderLayout.PAGE_START);
        keys.add(fields, BorderLayout.LINE_START);
        keys.add(modes, BorderLayout.CENTER);
        keys.add(bindImgs, BorderLayout.LINE_END);

        JPanel view = new JPanel(new BorderLayout());
        view.add(keys, BorderLayout.PAGE_START);
        view.add(new JPanel(), BorderLayout.CENTER);
        view.add(new JPanel(), BorderLayout.PAGE_END);

        JPanel gridSize = new JPanel(new GridLayout(2, 2, 0, 5));
        gridSize.setBorder(BorderFactory.createTitledBorder("Size"));
        gridSize.add(new JLabel("Cell size: "));
        JSpinner gridSpinner = new JSpinner(
                new SpinnerNumberModel(Main.config.cellSize, BasePanel.imageSize, Integer.MAX_VALUE, 1));
        gridSize.add(gridSpinner);
        gridSize.add(new JLabel("Panel border offset: "));
        JSpinner gapSpinner = new JSpinner(new SpinnerNumberModel(Main.config.borderOffset, 0,
                new DynamicInteger(() -> (Main.config.cellSize - BasePanel.imageSize)), 1));
        gapSpinner.addChangeListener((e) -> {
            Main.config.borderOffset = (int) gapSpinner.getValue();
            if (live) {
                Main.reconfigure();
            }
        });
        gridSize.add(gapSpinner);
        gridSpinner.addChangeListener((e) -> {
            Main.config.cellSize = (int) gridSpinner.getValue();
            if (Main.config.borderOffset > Main.config.cellSize - BasePanel.imageSize) {
                Main.config.borderOffset = Main.config.cellSize - BasePanel.imageSize;
                gapSpinner.setValue(Main.config.borderOffset);
            }
            if (live) {
                Main.reconfigure();
            }
        });

        form.add(gridSize, BorderLayout.PAGE_START);

        JScrollPane pane = new JScrollPane(view);
        pane.setBorder(BorderFactory.createTitledBorder("Panels"));
        pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        pane.setPreferredSize(new Dimension(600, 200));
        pane.getVerticalScrollBar().setUnitIncrement(16);

        form.add(pane, BorderLayout.CENTER);

        JPanel graphLayout = new JPanel(new GridLayout(5, 2, 0, 5));
        graphLayout.setBorder(BorderFactory.createTitledBorder("Graph"));
        graphLayout.add(new JLabel("Graph mode: "));
        JComboBox<Object> graphMode = new JComboBox<Object>(GraphMode.values());
        graphMode.setSelectedItem(Main.config.graphMode);
        graphLayout.add(graphMode);

        LayoutValidator validator = new LayoutValidator();

        graphLayout.add(new JLabel("Graph x position: "));
        JSpinner x = new JSpinner(new EndNumberModel(Main.config.graph_x, validator.getXField(), (val) -> {
            Main.config.graph_x = val;
            if (live) {
                Main.reconfigure();
            }
        }));
        x.setEnabled(Main.config.graphMode == GraphMode.INLINE);
        graphLayout.add(x);

        graphLayout.add(new JLabel("Graph y position: "));
        JSpinner y = new JSpinner(new EndNumberModel(Main.config.graph_y, validator.getYField(), (val) -> {
            Main.config.graph_y = val;
            if (live) {
                Main.reconfigure();
            }
        }));
        y.setEnabled(Main.config.graphMode == GraphMode.INLINE);
        graphLayout.add(y);

        graphLayout.add(new JLabel("Graph width: "));
        JSpinner w = new JSpinner(new MaxNumberModel(Main.config.graph_w, validator.getWidthField(), (val) -> {
            Main.config.graph_w = val;
            if (live) {
                Main.reconfigure();
            }
        }));
        graphLayout.add(w);

        graphLayout.add(new JLabel("Graph height: "));
        JSpinner h = new JSpinner(new MaxNumberModel(Main.config.graph_h, validator.getHeightField(), (val) -> {
            Main.config.graph_h = val;
            if (live) {
                Main.reconfigure();
            }
        }));
        graphLayout.add(h);

        graphMode.addActionListener((e) -> {
            Main.config.graphMode = (GraphMode) graphMode.getSelectedItem();
            if (graphMode.getSelectedItem() == GraphMode.INLINE) {
                x.setEnabled(true);
                y.setEnabled(true);
            } else {
                x.setEnabled(false);
                y.setEnabled(false);
            }
            if (live) {
                Main.reconfigure();
            }
        });

        form.add(graphLayout, BorderLayout.PAGE_END);

        dialog.setSize(new Dimension(900, 700));
        dialog.setPreferredSize(new Dimension(900, 700));
        dialog.setResizable(true);
        dialog.setIconImage(icon);
        dialog.setVisible(true);

        // for(int i = 0; i < options.length; i++){
        // if(options[i].equals(optionPane.getValue())){
        // }
        // }

        Main.content.hideGrid();
    }

    /**
     * Creates a editable list item for the
     * layout configuration dialog
     * 
     * @param info   The positionable that links the
     *               editor to the underlying data
     * @param fields The GUI panel that holds all the fields
     * @param modes  The GUI panel that holds all the modes
     * @param live   Whether or not edits should be displayed in real time
     */
    private static final void createListItem(JDialog parent, Positionable info, JPanel fields,
            ArrayList<JSpinner[]> borderSpinners, JPanel modes, ArrayList<JComboBox<RenderingMode>> modeComboBoxes,
            JPanel bindImgs, boolean live) {
        fields.add(new JLabel(info.getName(), SwingConstants.CENTER));

        LayoutValidator validator = new LayoutValidator();
        // JSpinner x = new JSpinner(new EndNumberModel(info.getX(),
        // validator.getXField(), (val)->{
        JSpinner x = new JSpinner(new EndNumberModel(info.getX(), validator.getXField(), (val) -> {
            info.setX(val);
            if (live) {
                Main.reconfigure();
            }
        }));
        ((DefaultEditor) x.getEditor()).getTextField().setEditable(true);
        fields.add(x);

        JSpinner y = new JSpinner(new EndNumberModel(info.getY(), validator.getYField(), (val) -> {
            info.setY(val);
            if (live) {
                Main.reconfigure();
            }
        }));
        ((DefaultEditor) y.getEditor()).getTextField().setEditable(true);
        fields.add(y);

        JSpinner[] borderSet = new JSpinner[2];
        JSpinner w = new JSpinner(new MaxNumberModel(info.getWidth(), validator.getWidthField(), (val) -> {
            info.setWidth(val);
            if (live) {
                Main.reconfigure();
            }
        }));
        ((DefaultEditor) w.getEditor()).getTextField().setEditable(true);
        fields.add(w);
        borderSet[0] = w;

        JSpinner h = new JSpinner(new MaxNumberModel(info.getHeight(), validator.getHeightField(), (val) -> {
            info.setHeight(val);
            if (live) {
                Main.reconfigure();
            }
        }));
        ((DefaultEditor) h.getEditor()).getTextField().setEditable(true);
        fields.add(h);
        borderSet[1] = h;
        borderSpinners.add(borderSet);

        JComboBox<RenderingMode> mode = new JComboBox<RenderingMode>(RenderingMode.values());
        mode.setSelectedItem(info.getRenderingMode());
        mode.addActionListener((e) -> {
            info.setRenderingMode((RenderingMode) mode.getSelectedItem());
            if (live) {
                Main.reconfigure();
            }
        });
        modes.add(mode);
        modeComboBoxes.add(mode);

        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(40, 28));
        btn.setSize(40, 28);
        int imgHeight = btn.getHeight();
        BindImgDialog.parent = parent;
        if (info.getBindingImg() != null) {
            try {
                // Image img = ImageIO.read(LayoutDialog.class.getResourceAsStream("/LS/" +
                // info.getBindingImg()));
                String fileName = info.getBindingImg();
                String filePath = "";
                if (BindImgDialog.class.getResource("").getProtocol() == "file") {
                    String projectPath = new File(".").getAbsoluteFile().toString().replace("\\", "/");
                    filePath = projectPath.substring(0, projectPath.lastIndexOf("KeysPerSecond")) + "images/"
                            + fileName;
                } else {
                    filePath = "";
                    String curPath = BindImgDialog.class.getProtectionDomain().getCodeSource().getLocation().getPath()
                            .toString();
                    filePath = curPath.substring(0, curPath.lastIndexOf("/")) + "/images/" + fileName;
                }

                Image img = ImageIO.read(new File(filePath));
                imgHeight -= btn.getInsets().top + btn.getInsets().bottom;
                img = img.getScaledInstance(imgHeight, imgHeight, Image.SCALE_SMOOTH);
                btn.setIcon(new ImageIcon(img));
            } catch (IOException e1) {
                btn.setText("X");
            } catch (IllegalArgumentException e) {
                btn.setText("X");
            }
        }
        btn.setSize(40, 28);
        btn.addActionListener(((e) -> {
            BindImgDialog.parentBtn = btn;
            BindImgDialog.info = info;
            BindImgDialog.bindingImg(live);
        }));
        bindImgs.add(btn);
    }

    public static class LayoutAdvSettingDialog {

        public static boolean live;
        public static ArrayList<JSpinner[]> parentSpinners;
        public static ArrayList<JComboBox<RenderingMode>> parentModes;

        public static final void configureLayoutAdvSetting(boolean live) {

            JPanel form = new JPanel(new BorderLayout());
            JPanel setGaps = new JPanel();
            setGaps.setLayout(new BoxLayout(setGaps, BoxLayout.Y_AXIS));
            JPanel gapsCHBPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 1, 0));
            JCheckBox isSetGapsCHB = new JCheckBox("activate");
            isSetGapsCHB.setAlignmentX(Component.CENTER_ALIGNMENT);
            JPanel setGapsSub = new JPanel();
            setGapsSub.setLayout(new BoxLayout(setGapsSub, BoxLayout.Y_AXIS));
            JPanel setGapsFields = new JPanel(new GridLayout(2, 2, 0, 0));
            JPanel setModes = new JPanel();
            setModes.setLayout(new BoxLayout(setModes, BoxLayout.Y_AXIS));
            JCheckBox isSetModesCHB = new JCheckBox("activate");
            isSetModesCHB.setAlignmentX(Component.CENTER_ALIGNMENT);
            JPanel setModesSub = new JPanel();
            setModesSub.setLayout(new BoxLayout(setModesSub, BoxLayout.Y_AXIS));
            JComboBox<RenderingMode> setModesComboBox = new JComboBox<RenderingMode>(RenderingMode.values());
            setModesComboBox.setSelectedItem(RenderingMode.ONLY_T);
            setModesComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);

            Main.content.showGrid();

            // setBorders layout
            setGaps.setBorder(BorderFactory.createTitledBorder("Set Gaps At Once"));

            gapsCHBPanel.add(isSetGapsCHB);

            CHBStateChangeEvent bordersEvent = new CHBStateChangeEvent(setGaps, setGapsSub);
            isSetGapsCHB.addItemListener(bordersEvent);

            // setBordersSub layout
            JLabel wLabel = new JLabel("Width Gap");
            wLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel hLabel = new JLabel("Height Gap");
            hLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            setGapsFields.add(wLabel);
            setGapsFields.add(hLabel);
            LayoutValidator validator = new LayoutValidator();

            new EndNumberModel(tmpItem.getX(), validator.getXField(), (val) -> {
                tmpItem.setX(val);
            });
            new EndNumberModel(tmpItem.getY(), validator.getYField(), (val) -> {
                tmpItem.setY(val);
            });

            JSpinner w = new JSpinner(new MaxNumberModel(tmpItem.getWidth(), validator.getWidthField(), (val) -> {
                tmpItem.setWidth(val);
                if (live) {
                    Main.reconfigure();
                }
            }));
            w.setAlignmentX(Component.CENTER_ALIGNMENT);
            setGapsFields.add(w);

            JSpinner h = new JSpinner(new MaxNumberModel(tmpItem.getHeight(), validator.getHeightField(), (val) -> {
                tmpItem.setHeight(val);
                if (live) {
                    Main.reconfigure();
                }
            }));
            h.setAlignmentX(Component.CENTER_ALIGNMENT);
            setGapsFields.add(h);

            setGapsSub.add(setGapsFields);

            setGaps.add(gapsCHBPanel);
            setGaps.add(setGapsSub);
            bordersEvent.setEnabled(false, setGapsSub);

            // setModes layout
            setModes.setBorder(BorderFactory.createTitledBorder("Set Modes At Once"));

            CHBStateChangeEvent modesEvent = new CHBStateChangeEvent(setModes, setModesSub);
            isSetModesCHB.addItemListener(modesEvent);

            // setModesSub layout

            JLabel modesLabel = new JLabel("Mode");
            modesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            setModesSub.add(modesLabel);
            setModesSub.add(setModesComboBox);

            setModes.add(isSetModesCHB);
            setModes.add(setModesSub);
            modesEvent.setEnabled(false, setModesSub);

            form.add(setGaps, BorderLayout.NORTH);
            form.add(setModes, BorderLayout.CENTER);

            // if (Dialog.showDialog(form, false, new String[] { "OK", "Cancel" }) == 0) {
            if (Dialog.showDialog(form, false, ModalityType.APPLICATION_MODAL).getDefaultCloseOperation() == 0) {
                if (isSetGapsCHB.isSelected()) {
                    allItem.setWidth(tmpItem.getWidth());
                    allItem.setHeight(tmpItem.getHeight());
                    for (JSpinner[] borderSet : parentSpinners) {
                        borderSet[0].setValue(Integer.toString(tmpItem.getWidth()));
                        borderSet[1].setValue(Integer.toString(tmpItem.getHeight()));
                    }
                }

                if (isSetModesCHB.isSelected()) {
                    allItem.setRenderingMode((RenderingMode) setModesComboBox.getSelectedItem());
                    for (JComboBox<RenderingMode> modes : parentModes) {
                        modes.setSelectedItem((RenderingMode) setModesComboBox.getSelectedItem());
                    }
                }
            }
            Main.content.hideGrid();
        }
    }

    public static class BindImgDialog {

        public static JDialog parent;
        public static JButton parentBtn;
        public static Positionable info;
        private static ArrayList<String> imgPathList = new ArrayList<String>();
        private static ArrayList<Image> imgFileList = new ArrayList<Image>();

        public static final void bindingImg(boolean live) {

            JScrollPane form = new JScrollPane();
            JPanel iconListPanel = new JPanel(new GridLayout(0, 5));

            JOptionPane optionPane = new JOptionPane(form, JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, new String[] {}, 0);
            optionPane.setActionMap(new ActionMap());
            JDialog dialog = optionPane.createDialog(parent, "Bind Image");

            form.setPreferredSize(new Dimension(800, 500));
            form.setViewportView(iconListPanel);
            form.getVerticalScrollBar().setUnitIncrement(16);

            // Image bIcon;
            Image bIcon;
            JButton iconBtn;
            iconBtn = new JButton("Select Image");
            iconBtn.setPreferredSize(new Dimension(110, 110));
            iconListPanel.add(iconBtn);

            /////////////////////////////
            String filePath = "";
            try {
                if (BindImgDialog.class.getResource("").getProtocol() == "file") {
                    // in IDE

                    File rw = new File("../images");
                    File[] fileList = rw.listFiles();
                    for (File file : fileList) {
                        if (file.isFile()) {
                            String projectPath = new File(".").getAbsoluteFile().toString().replace("\\", "/");
                            filePath = projectPath.substring(0, projectPath.lastIndexOf("KeysPerSecond")) + "images/"
                                    + file.getName();
                            if (!imgPathList.contains(filePath)) {
                                imgPathList.add(filePath);

                                imgFileList.add(ImageIO.read(new File(filePath)).getScaledInstance(100, 100,
                                        Image.SCALE_SMOOTH));

                            }
                        }
                    }

                } else {
                    // in .jar or .exe

                    String curPath = BindImgDialog.class.getProtectionDomain().getCodeSource().getLocation().getPath()
                            .toString();
                    String fileRoot = curPath.substring(0, curPath.lastIndexOf("/")) + "/images/";
                    System.out.println("root : " + fileRoot);
                    File rw = new File(fileRoot);
                    File[] fileList = rw.listFiles();

                    for (File file : fileList) {
                        System.out.println("file : " + file.getName());
                        if (file.isFile()) {
                            filePath = fileRoot + file.getName();

                            if (!imgPathList.contains(filePath)) {
                                imgPathList.add(filePath);

                                // imgFileList.add(ImageIO.read(BindImgDialog.class.getResourceAsStream(filePath)));
                                imgFileList.add(ImageIO.read(new File(filePath)).getScaledInstance(100, 100,
                                        Image.SCALE_SMOOTH));

                            }
                        }
                    }
                }

            } catch (IOException e) {
                parentBtn.setText("error");
                imgPathList.clear();
                imgFileList.clear();
            }

            int arrayLen = imgPathList.size();
            filePath = "";
            for (int n = 0; n < arrayLen; n++) {
                filePath = imgPathList.get(n);
                bIcon = imgFileList.get(n);
                if (bIcon != null) {
                    iconBtn = new JButton(new ImageIcon(bIcon));
                    iconBtn.setPreferredSize(new Dimension(110, 110));
                    iconBtn.setName(filePath);

                    iconBtn.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {

                            JButton selectedBtn = (JButton) e.getSource();

                            parentBtn.setPreferredSize(new Dimension(40, 28));
                            parentBtn.setSize(40, 28);

                            int imgHeight = parentBtn.getHeight();

                            String fileName = "";
                            String filePath = selectedBtn.getName();
                            if (BindImgDialog.class.getResource("").getProtocol() == "file") {
                                fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
                                String projectPath = new File(".").getAbsoluteFile().toString().replace("\\", "/");
                                filePath = projectPath.substring(0, projectPath.lastIndexOf("K")) + "images/"
                                        + fileName;
                            } else {
                                fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
                            }
                            if (fileName.equals("1_none.png")) {
                                parentBtn.setIcon(null);
                                parentBtn.setText("");
                                info.setBindingImg(null);
                            } else {
                                try {
                                    System.out.println("path : " + filePath);
                                    Image img = ImageIO.read(new File(filePath));
                                    imgHeight -= parentBtn.getInsets().top + parentBtn.getInsets().bottom;
                                    img = img.getScaledInstance(imgHeight, imgHeight, Image.SCALE_SMOOTH);
                                    parentBtn.setIcon(new ImageIcon(img));

                                } catch (IOException e1) {
                                    parentBtn.setText("X");
                                } catch (IllegalArgumentException e1) {
                                    parentBtn.setText("X");
                                }

                                parentBtn.setPreferredSize(new Dimension(40, 28));
                                parentBtn.setSize(40, 28);

                                info.setBindingImg(fileName);
                            }
                            dialog.dispose();

                        }
                    });
                    iconListPanel.add(iconBtn);
                } else {
                    iconListPanel.add(new JLabel("Can't load image resource"));
                }
            }

            /////////////////////////////

            Main.content.showGrid();

            Image icon;
            try {
                icon = ImageIO.read(ClassLoader.getSystemResource("kps_small.png"));
            } catch (IOException e) {
                icon = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
            }

            dialog.setLayout(new GridLayout(1, 0));
            dialog.setLocation(parent.getLocation());
            dialog.setSize(1200, 700);
            dialog.setResizable(true);
            dialog.setIconImage(icon);
            dialog.setVisible(true);
            dialog.setAlwaysOnTop(true);

            dialog.add(form);
            dialog.add(new JLabel("hello"));

            Main.content.hideGrid();

        }
    }

}
