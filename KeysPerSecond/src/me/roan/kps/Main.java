package me.roan.kps;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.IllegalComponentStateException;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.print.Doc;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.NativeInputEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;

import me.roan.kps.CommandKeys.CMD;
import me.roan.kps.layout.GridPanel;
import me.roan.kps.layout.Layout;
import me.roan.kps.layout.Positionable;
import me.roan.kps.panels.AvgPanel;
import me.roan.kps.panels.GraphPanel;
import me.roan.kps.panels.KeyPanel;
import me.roan.kps.panels.MaxPanel;
import me.roan.kps.panels.NowPanel;
import me.roan.kps.panels.TotPanel;
import me.roan.kps.ui.dialog.KeysDialog;
import me.roan.kps.ui.dialog.LayoutDialog;
import me.roan.util.ClickableLink;
import me.roan.util.Dialog;
import me.roan.util.ExclamationMarkPath;
import me.roan.util.Util;

/**
 * This program can be used to display
 * information about how many times
 * certain keys are pressed and what the
 * average, maximum and current
 * number of keys pressed per second is.
 * <p>
 * Besides the tracking of the assigned keys
 * this program responds to 6 key events these are:
 * <ol>
 * <li><b>Ctrl + P</b>: Causes the program to reset the average and maximum
 * value
 * And to print the statistics to standard output
 * </li>
 * <li><b>Ctrl + O</b>: Terminates the program
 * </li>
 * <li><b>Ctrl + I</b>: Causes the program to reset the amount of times a key is
 * pressed
 * And to print the statistics to standard output
 * </li>
 * <li><b>Ctrl + Y</b>: Hides/shows the GUI
 * </li>
 * <li><b>Ctrl + T</b>: Pauses/resumes the counter
 * </li>
 * <li><b>Ctrl + R</b>: Reloads the configuration</li>
 * </ol>
 * The program also constantly prints the current keys per second to
 * the standard output.<br>
 * A key is only counted as being pressed if the key has been released before
 * this deals with the issue of holding a key firing multiple key press
 * events<br>
 * This program also has support for saving and loading configurations
 * 
 * @author Roan
 */
public class Main {
    protected static String version = "1.1";
    /**
     * The number of seconds the average has
     * been calculated for
     */
    protected static long n = 0;
    /**
     * The number of keys pressed in the
     * ongoing second
     */
    protected static AtomicInteger tmp = new AtomicInteger(0);
    /**
     * The average keys per second
     */
    public static double avg;
    /**
     * The maximum keys per second value reached so far
     */
    public static int max;
    /**
     * The keys per second of the previous second
     * used for displaying the current keys per second value
     */
    public static int prev;
    /**
     * HashMap containing all the tracked keys and their
     * virtual codes<br>
     * Used to increment the count for the
     * keys
     */
    public static Map<Integer, Key> keys = new HashMap<Integer, Key>();
    /**
     * The most recent key event, only
     * used during the initial setup
     */
    public static NativeKeyEvent lastevent;
    /**
     * Main panel used for showing all the sub panels that
     * display all the information
     */
    public static final GridPanel content = new GridPanel();
    /**
     * Graph panel
     */
    protected static GraphPanel graph = new GraphPanel();
    /**
     * Linked list containing all the past key counts per time frame
     */
    private static LinkedList<Integer> timepoints = new LinkedList<Integer>();
    /**
     * The program's main frame
     */
    protected static final JFrame frame = new JFrame("KeyboardInputViewer");
    /**
     * Whether or not the counter is paused
     */
    protected static boolean suspended = false;
    /**
     * The configuration
     */
    public static Configuration config = new Configuration(null);
    /**
     * The loop timer
     */
    protected static ScheduledExecutorService timer = null;
    /**
     * The loop timer task
     */
    protected static ScheduledFuture<?> future = null;
    /**
     * Frame for the graph
     */
    protected static JFrame graphFrame = new JFrame("KeyboardInputViewer");
    /**
     * The layout for the main panel of the program
     */
    protected static final Layout layout = new Layout(content);
    /**
     * Small icon for the program
     */
    private static final Image iconSmall;
    /**
     * Icon for the program
     */
    private static final Image icon;
    /**
     * Called when a frame is closed
     */
    private static final WindowListener onClose;
    /**
     * Dummy key for getOrDefault operations
     */
    private static final Key DUMMY_KEY;

    /**
     * Best text rendering hints.
     */
    public static Map<?, ?> desktopHints;

    /**
     * Main method
     * 
     * @param args - configuration file path
     */
    public static void main(String[] args) {
        // Work around for a JDK bug
        ExclamationMarkPath.check(args);

        // Basic setup and info
        String config = null;
        if (args.length >= 1 && !args[0].equalsIgnoreCase("-relaunch")) {
            config = args[0];
            System.out.println("Attempting to load config: " + config);
        }
        System.out.println("Control keys:");
        System.out.println("Ctrl + P: Causes the program to reset and print the average and maximum value");
        System.out.println("Ctrl + U: Terminates the program");
        System.out.println("Ctrl + I: Causes the program to reset and print the key press statistics");
        System.out.println("Ctrl + Y: Hides/shows the GUI");
        System.out.println("Ctrl + T: Pauses/resumes the counter");
        System.out.println("Ctrl + R: Reloads the configuration");
        Util.installUI();

        // Set dialog defaults
        Dialog.setDialogIcon(iconSmall);
        Dialog.setParentFrame(frame);
        Dialog.setDialogTitle("KeyboardInputViewer");
        VersionControl.setParentFrame(frame);

        // Make sure the native hook is always unregistered
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    GlobalScreen.unregisterNativeHook();
                } catch (NativeHookException e1) {
                    e1.printStackTrace();
                }
            }
        });

        // Initialise native library and register event handlers
        setupNativeHook();

        // Set configuration for the keys
        if (config != null) {
            Configuration toLoad = new Configuration(new File(config));
            int index = config.lastIndexOf(File.separatorChar);
            File dir = new File(config.substring(0, index));
            final String name = config.substring(index + 1);
            File[] files = null;
            if (dir.exists()) {
                files = dir.listFiles((FilenameFilter) (f, n) -> {
                    for (int i = 0; i < name.length(); i++) {
                        char ch = name.charAt(i);
                        if (ch == '?') {
                            continue;
                        }
                        if (i >= n.length() || ch != n.charAt(i)) {
                            return false;
                        }
                    }
                    return true;
                });
            }
            if (files != null && files.length > 0 && files[0].exists()) {
                toLoad.loadConfig(files[0]);
                Main.config = toLoad;
                System.out.println("Loaded config file: " + files[0].getName());
            } else {
                System.out.println("Provided config file does not exist.");
            }
        } else {
            try {
                configure();
            } catch (NullPointerException e) {
                e.printStackTrace();
                try {
                    Dialog.showErrorDialog(
                            "Failed to load the configuration menu, however you can use the live menu instead");
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                System.err.println("Failed to load the configuration menu, however you can use the live menu instead");
            }
        }

        // Build GUI
        try {
            buildGUI();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Start stats saving
        if (Main.config.autoSaveStats) {
            Statistics.saveStatsTask();
        }

        // Enter the main loop
        mainLoop();
    }

    /**
     * Main loop of the program
     * this loop updates the
     * average, current and
     * maximum keys per second
     */
    protected static final void mainLoop() {
        if (timer == null) {
            timer = Executors.newSingleThreadScheduledExecutor();
        } else {
            future.cancel(false);
        }
        future = timer.scheduleAtFixedRate(() -> {
            if (!suspended) {
                int currentTmp = tmp.getAndSet(0);
                int totaltmp = currentTmp;
                for (int i : timepoints) {
                    totaltmp += i;
                }
                if (totaltmp > max) {
                    max = totaltmp;
                }
                if (totaltmp != 0) {
                    avg = (avg * n + totaltmp) / (n + 1.0D);
                    n++;
                    TotPanel.hits += currentTmp;
                    System.out.println("Current keys per second: " + totaltmp + " time frame: " + tmp);
                }
                graph.addPoint(totaltmp);
                graph.repaint();
                content.repaint();
                prev = totaltmp;
                timepoints.addFirst(currentTmp);
                if (timepoints.size() >= 1000 / config.updateRate) {
                    timepoints.removeLast();
                }
            }
        }, 0, config.updateRate, TimeUnit.MILLISECONDS);
    }

    /**
     * Registers the native libraries and
     * registers event handlers for key
     * press events
     */
    private static final void setupNativeHook() {
        try {
            Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
            logger.setLevel(Level.WARNING);
            logger.setUseParentHandlers(false);
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());
            Dialog.showErrorDialog("There was a problem registering the native hook: " + ex.getMessage());
            try {
                GlobalScreen.unregisterNativeHook();
            } catch (NativeHookException e1) {
                e1.printStackTrace();
            }
            System.exit(1);
        }
        GlobalScreen.addNativeKeyListener(new NativeKeyListener() {

            @Override
            public void nativeKeyPressed(NativeKeyEvent event) {
                pressEvent(event);
            }

            @Override
            public void nativeKeyReleased(NativeKeyEvent event) {
                releaseEvent(event);
            }

            @Override
            public void nativeKeyTyped(NativeKeyEvent event) {
            }
        });
        GlobalScreen.addNativeMouseListener(new NativeMouseListener() {

            @Override
            public void nativeMouseClicked(NativeMouseEvent event) {
            }

            @Override
            public void nativeMousePressed(NativeMouseEvent event) {
                pressEvent(event);
            }

            @Override
            public void nativeMouseReleased(NativeMouseEvent event) {
                releaseEvent(event);
            }
        });
    }

    /**
     * Called when a key is released
     * 
     * @param event The event that occurred
     */
    private static final void releaseEvent(NativeInputEvent event) {
        int code = getExtendedKeyCode(event);
        if (code == CommandKeys.ALT) {
            CommandKeys.isAltDown = false;
        } else if (code == CommandKeys.CTRL) {
            CommandKeys.isCtrlDown = false;
        } else if (CommandKeys.isShift(code)) {
            CommandKeys.isShiftDown = false;
        }
        if (config.enableModifiers) {
            if (code == CommandKeys.ALT) {
                for (Key k : keys.values()) {
                    if (k.alt) {
                        k.keyReleased();
                    }
                }
            } else if (code == CommandKeys.CTRL) {
                for (Key k : keys.values()) {
                    if (k.ctrl) {
                        k.keyReleased();
                    }
                }
            } else if (CommandKeys.isShift(code)) {
                for (Key k : keys.values()) {
                    if (k.shift) {
                        k.keyReleased();
                    }
                }
            }
            for (Entry<Integer, Key> k : keys.entrySet()) {
                if (getBaseKeyCode(code) == getBaseKeyCode(k.getKey())) {
                    k.getValue().keyReleased();
                }
            }
        } else {
            keys.getOrDefault(code, DUMMY_KEY).keyReleased();
        }
    }

    /**
     * Called when a key is pressed
     * 
     * @param nevent The event that occurred
     */
    private static final void pressEvent(NativeInputEvent nevent) {
        Integer code = getExtendedKeyCode(nevent);
        Integer keyLocation;
        try {
            keyLocation = ((NativeKeyEvent) nevent).getKeyLocation();
        } catch (java.lang.ClassCastException e) {
            keyLocation = 0;
        }
        if (!keys.containsKey(code)) {
            if (config.trackAllKeys && nevent instanceof NativeKeyEvent) {
                keys.put(code,
                        new Key(KeyInformation.getKeyName(
                                NativeKeyEvent.getKeyText(((NativeKeyEvent) nevent).getKeyCode(), keyLocation), code,
                                keyLocation)));
            } else if (config.trackAllButtons && nevent instanceof NativeMouseEvent) {
                keys.put(code, new Key("M" + ((NativeMouseEvent) nevent).getButton()));
            }
        }

        if (!suspended && keys.containsKey(code)) {
            Key key = keys.get(code);
            key.keyPressed();
            if (config.enableModifiers) {
                if (key.alt) {
                    keys.getOrDefault(CommandKeys.ALT, DUMMY_KEY).keyReleased();
                }
                if (key.ctrl) {
                    keys.getOrDefault(CommandKeys.CTRL, DUMMY_KEY).keyReleased();
                }
                if (key.shift) {
                    keys.getOrDefault(CommandKeys.RSHIFT, DUMMY_KEY).keyReleased();
                    keys.getOrDefault(CommandKeys.LSHIFT, DUMMY_KEY).keyReleased();
                }
            }
        }
        if (nevent instanceof NativeKeyEvent) {
            NativeKeyEvent event = (NativeKeyEvent) nevent;
            if (!CommandKeys.isAltDown) {
                CommandKeys.isAltDown = code == CommandKeys.ALT;
            }
            if (!CommandKeys.isCtrlDown) {
                CommandKeys.isCtrlDown = code == CommandKeys.CTRL;
            }
            if (!CommandKeys.isShiftDown) {
                CommandKeys.isShiftDown = CommandKeys.isShift(code);
            }
            lastevent = event;
            if (config.CP.matches(event.getKeyCode())) {
                resetStats();
            } else if (config.CU.matches(event.getKeyCode())) {
                exit();
            } else if (config.CI.matches(event.getKeyCode())) {
                resetTotals();
            } else if (config.CY.matches(event.getKeyCode())) {
                if (frame.getContentPane().getComponentCount() != 0) {
                    frame.setVisible(!frame.isVisible());
                }
            } else if (config.CT.matches(event.getKeyCode())) {
                suspended = !suspended;
                Menu.pause.setSelected(suspended);
            } else if (config.CR.matches(event.getKeyCode())) {
                config.reloadConfig();
                Menu.resetData();
            }
        }
    }

    /**
     * Gets the extended key code for this event, this key code
     * includes modifiers
     * 
     * @param event The event that occurred
     * @return The extended key code for this event
     */
    private static final int getExtendedKeyCode(NativeInputEvent event) {
        if (event instanceof NativeKeyEvent) {
            NativeKeyEvent key = (NativeKeyEvent) event;
            if (!config.enableModifiers) {
                return CommandKeys.getExtendedKeyCode(key.getKeyCode(), key.getKeyLocation(), false, false, false);
            } else {
                return CommandKeys.getExtendedKeyCode(key.getKeyCode(), key.getKeyLocation());
            }
        } else {
            return -((NativeMouseEvent) event).getButton();
        }
    }

    /**
     * Gets the base key code for the extended key code,
     * this is the key code without modifiers
     * 
     * @param code The extended key code
     * @return The base key code
     */
    private static final int getBaseKeyCode(int code) {
        return code & CommandKeys.KEYCODE_MASK;
    }

    /**
     * Asks the user for a configuration
     * though a series of dialogs
     * These dialogs also provide the
     * option of saving or loading an
     * existing configuration
     */
    private static final void configure() {
        JPanel form = new JPanel(new BorderLayout());
        JPanel boxes = new JPanel(new GridLayout(11, 0));
        JPanel labels = new JPanel(new GridLayout(11, 0));
        JCheckBox cmax = new JCheckBox();
        JCheckBox cavg = new JCheckBox();
        JCheckBox ccur = new JCheckBox();
        JCheckBox ckey = new JCheckBox();
        JCheckBox cgra = new JCheckBox();
        JCheckBox ctop = new JCheckBox();
        JCheckBox ccol = new JCheckBox();
        JCheckBox callKeys = new JCheckBox();
        JCheckBox callButtons = new JCheckBox();
        JCheckBox ctot = new JCheckBox();
        JCheckBox cmod = new JCheckBox();
        // cmax.setSelected(true);
        // cavg.setSelected(true);
        // ccur.setSelected(true);
        ckey.setSelected(true);
        JLabel lmax = new JLabel("Show maximum: ");
        JLabel lavg = new JLabel("Show average: ");
        JLabel lcur = new JLabel("Show current: ");
        JLabel lkey = new JLabel("Show keys");
        JLabel lgra = new JLabel("Show graph: ");
        JLabel ltop = new JLabel("Overlay mode: ");
        JLabel lcol = new JLabel("Custom colours: ");
        JLabel lallKeys = new JLabel("Track all keys");
        JLabel lallButtons = new JLabel("Track all buttons");
        JLabel ltot = new JLabel("Show total");
        JLabel lmod = new JLabel("Key-modifier tracking");
        ltop.setToolTipText(
                "Requires you to run osu! out of full screen mode, known to not (always) work with the wine version of osu!");
        boxes.add(cmax);
        boxes.add(cavg);
        boxes.add(ccur);
        boxes.add(ctot);
        boxes.add(ckey);
        boxes.add(cgra);
        boxes.add(ctop);
        boxes.add(ccol);
        boxes.add(callKeys);
        boxes.add(callButtons);
        boxes.add(cmod);
        labels.add(lmax);
        labels.add(lavg);
        labels.add(lcur);
        labels.add(ltot);
        labels.add(lkey);
        labels.add(lgra);
        labels.add(ltop);
        labels.add(lcol);
        labels.add(lallKeys);
        labels.add(lallButtons);
        labels.add(lmod);
        ctop.addActionListener((e) -> {
            config.overlay = ctop.isSelected();
        });
        callKeys.addActionListener((e) -> {
            config.trackAllKeys = callKeys.isSelected();
        });
        callButtons.addActionListener((e) -> {
            config.trackAllButtons = callButtons.isSelected();
        });
        cmax.addActionListener((e) -> {
            config.showMax = cmax.isSelected();
        });
        cavg.addActionListener((e) -> {
            config.showAvg = cavg.isSelected();
        });
        ccur.addActionListener((e) -> {
            config.showCur = ccur.isSelected();
        });
        cgra.addActionListener((e) -> {
            config.showGraph = cgra.isSelected();
        });
        ccol.addActionListener((e) -> {
            config.customColors = ccol.isSelected();
        });
        ckey.addActionListener((e) -> {
            config.showKeys = ckey.isSelected();
        });
        ctot.addActionListener((e) -> {
            config.showTotal = ctot.isSelected();
        });
        cmod.addActionListener((e) -> {
            config.enableModifiers = cmod.isSelected();
        });
        JPanel options = new JPanel();
        labels.setPreferredSize(
                new Dimension((int) labels.getPreferredSize().getWidth(), (int) boxes.getPreferredSize().getHeight()));
        options.add(labels);
        options.add(boxes);
        JPanel buttons = new JPanel(new GridLayout(10, 1));
        JButton addkey = new JButton("Add key");
        JButton load = new JButton("Load config");
        JButton updaterate = new JButton("Update rate");
        JButton cmdkeys = new JButton("Commands");
        JButton save = new JButton("Save config");
        JButton graph = new JButton("Graph");
        graph.setEnabled(false);
        cgra.addActionListener((e) -> {
            graph.setEnabled(cgra.isSelected());
            graph.repaint();
        });
        JButton color = new JButton("Colours");
        color.setEnabled(false);
        ccol.addActionListener((e) -> {
            color.setEnabled(ccol.isSelected());
            color.repaint();
        });
        JButton precision = new JButton("Precision");
        JButton layout = new JButton("Layout");
        JButton autoSave = new JButton("Stats saving");
        buttons.add(addkey);
        buttons.add(load);
        buttons.add(save);
        buttons.add(graph);
        buttons.add(updaterate);
        buttons.add(color);
        buttons.add(precision);
        buttons.add(autoSave);
        buttons.add(cmdkeys);
        buttons.add(layout);
        form.add(options, BorderLayout.CENTER);
        options.setBorder(BorderFactory.createTitledBorder("General"));
        buttons.setBorder(BorderFactory.createTitledBorder("Configuration"));
        JPanel all = new JPanel(new BorderLayout());
        all.add(options, BorderLayout.LINE_START);
        all.add(buttons, BorderLayout.LINE_END);
        form.add(all, BorderLayout.CENTER);
        layout.addActionListener((e) -> {
            LayoutDialog.configureLayout(false);
            LayoutDialog.parent = frame;
            LayoutDialog.live = false;
        });
        cmdkeys.addActionListener((e) -> {
            configureCommandKeys();
        });
        precision.addActionListener((e) -> {
            JPanel pconfig = new JPanel(new BorderLayout());
            JLabel info1 = new JLabel("Specify how many digits should be displayed");
            JLabel info2 = new JLabel("beyond the decimal point for the average.");
            JPanel plabels = new JPanel(new GridLayout(2, 1, 0, 0));
            plabels.add(info1);
            plabels.add(info2);
            JComboBox<String> values = new JComboBox<String>(
                    new String[] { "No digits beyond the decimal point", "1 digit beyond the decimal point",
                            "2 digits beyond the decimal point", "3 digits beyond the decimal point" });
            values.setSelectedIndex(config.precision);
            JLabel vlabel = new JLabel("Precision: ");
            JPanel pvalue = new JPanel(new BorderLayout());
            pvalue.add(vlabel, BorderLayout.LINE_START);
            pvalue.add(values, BorderLayout.CENTER);
            pconfig.add(plabels, BorderLayout.CENTER);
            pconfig.add(pvalue, BorderLayout.PAGE_END);
            if (Dialog.showSaveDialog(pconfig)) {
                config.precision = values.getSelectedIndex();
            }
        });
        graph.addActionListener((e) -> {
            JPanel pconfig = new JPanel();
            JSpinner backlog = new JSpinner(new SpinnerNumberModel(Main.config.backlog, 1, Integer.MAX_VALUE, 1));
            JCheckBox showavg = new JCheckBox();
            showavg.setSelected(Main.config.graphAvg);
            JLabel lbacklog;
            if (config.updateRate != 1000) {
                lbacklog = new JLabel("Backlog (seconds / " + (1000 / config.updateRate) + "): ");
            } else {
                lbacklog = new JLabel("Backlog (seconds): ");
            }
            JLabel lshowavg = new JLabel("Show average: ");
            JPanel glabels = new JPanel(new GridLayout(2, 1));
            JPanel gcomponents = new JPanel(new GridLayout(2, 1));
            glabels.add(lbacklog);
            glabels.add(lshowavg);
            gcomponents.add(backlog);
            gcomponents.add(showavg);
            glabels.setPreferredSize(new Dimension((int) glabels.getPreferredSize().getWidth(),
                    (int) gcomponents.getPreferredSize().getHeight()));
            gcomponents.setPreferredSize(new Dimension(50, (int) gcomponents.getPreferredSize().getHeight()));
            pconfig.add(glabels);
            pconfig.add(gcomponents);
            if (Dialog.showSaveDialog(pconfig)) {
                Main.config.graphAvg = showavg.isSelected();
                Main.config.backlog = (int) backlog.getValue();
            }
        });
        addkey.addActionListener((e) -> {
            // KeysDialog.configureKeys();
            if (config.keyinfo.isEmpty())
                KeysDialog.selectKeyMode();
            else
                KeysDialog.configureKeys();
        });
        color.addActionListener((e) -> {
            configureColors();
        });
        save.addActionListener((e) -> {
            config.saveConfig(false);
        });
        load.addActionListener((e) -> {
            if (!Configuration.loadConfiguration()) {
                return;
            }

            cmax.setSelected(config.showMax);
            ccur.setSelected(config.showCur);
            cavg.setSelected(config.showAvg);
            cgra.setSelected(config.showGraph);
            if (config.showGraph) {
                graph.setEnabled(true);
            }
            ccol.setSelected(config.customColors);
            if (config.customColors) {
                color.setEnabled(true);
            }
            callKeys.setSelected(config.trackAllKeys);
            callButtons.setSelected(config.trackAllButtons);
            ckey.setSelected(config.showKeys);
            ctop.setSelected(config.overlay);
            ctot.setSelected(config.showTotal);
            cmod.setSelected(config.enableModifiers);
        });
        updaterate.addActionListener((e) -> {
            JPanel info = new JPanel(new GridLayout(2, 1, 0, 0));
            info.add(new JLabel("Here you can change the rate at which"));
            info.add(new JLabel("the graph, max, avg & cur are updated."));
            JPanel pconfig = new JPanel(new BorderLayout());
            JComboBox<String> update = new JComboBox<String>(new String[] { "1000ms", "500ms", "250ms", "200ms",
                    "125ms", "100ms", "50ms", "25ms", "20ms", "10ms", "5ms", "1ms" });
            update.setSelectedItem(config.updateRate + "ms");
            update.setRenderer(new DefaultListCellRenderer() {
                /**
                 * Serial ID
                 */
                private static final long serialVersionUID = 1L;

                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                        boolean isSelected, boolean cellHasFocus) {
                    Component item = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (((String) value).length() < 5 || ((String) value).equals("100ms")) {
                        item.setForeground(Color.RED);
                    }
                    if (((String) value).length() < 4) {
                        item.setForeground(Color.MAGENTA);
                    }
                    return item;
                }
            });
            JLabel lupdate = new JLabel("Update rate: ");
            pconfig.add(info, BorderLayout.PAGE_START);
            pconfig.add(lupdate, BorderLayout.WEST);
            pconfig.add(update, BorderLayout.CENTER);
            if (Dialog.showSaveDialog(pconfig)) {
                config.updateRate = Integer.parseInt(((String) update.getSelectedItem()).substring(0,
                        ((String) update.getSelectedItem()).length() - 2));
            }
        });
        autoSave.addActionListener((e) -> {
            Statistics.configureAutoSave(false);
        });
        // JPanel info = new JPanel(new GridLayout(2, 1, -2, 2));
        JPanel info = new JPanel(new BorderLayout());
        String repos = "KeyboardInputViewer";
        String currentVer = version;
        // info.add(Util.getVersionLabel("KeysPerSecond", "v1.0"));//XXX the version
        // number - don't forget build.gradle
        // JLabel version = new JLabel("<html><center><i>Version: " + nowVersion +
        // "</i></center></html>");
        VersionControl.setDialogIcon(iconSmall);
        info.add(VersionControl.getVersionLabel(repos, currentVer), BorderLayout.PAGE_START);

        JButton thxBtn = new JButton("Special Thanks");
        thxBtn.addActionListener((e) -> {
            String RoanH = new String("<p>Original Program Developer :<br><b>RoanH</b></p>");
            // String jegalryang = new String("<p>Resource Provided :<br><b>제갈량</b></p>");
            // String doctor48 = new String("<p>Resource Edited :<br><b>박사48</b></p>");
            String s = String.format("<html><left>%s</left></html>", RoanH);
            JLabel thxList = new JLabel(s);

            JDialog thxDialog = new JOptionPane(thxList, JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, new String[] {}, 0).createDialog("Special Thanks");

            thxDialog.setIconImage(iconSmall);
            thxDialog.setVisible(true);
        });
        info.add(thxBtn, BorderLayout.CENTER);

        // info.add(version, BorderLayout.PAGE_START);
        JPanel links = new JPanel(new GridLayout(2, 1, -2, 0));
        // JLabel forum = new JLabel("<html><font
        // color=blue><u>Forums</u></font></html>", SwingConstants.CENTER);
        JLabel originalGit = new JLabel("<html><font color=blue><u>Original_GitHub</u></font></html>",
                SwingConstants.CENTER);
        JLabel modifierGit = new JLabel("<html><font color=blue><u>KIV_GitHub</u></font></html>",
                SwingConstants.CENTER);
        // forum.addMouseListener(new
        // ClickableLink("https://osu.ppy.sh/community/forums/topics/552405"));
        originalGit.addMouseListener(new ClickableLink("https://github.com/RoanH/KeysPerSecond"));
        modifierGit.addMouseListener(new ClickableLink("https://github.com/SsalHub/KeyboardInputViewer"));
        // links.add(forum);
        links.add(originalGit);
        links.add(modifierGit);
        info.add(links, BorderLayout.PAGE_END);
        form.add(info, BorderLayout.PAGE_END);

        JButton ok = new JButton("OK");
        JButton exit = new JButton("Exit");
        exit.addActionListener(e -> exit());

        CountDownLatch latch = new CountDownLatch(1);
        ok.addActionListener(e -> latch.countDown());

        JPanel bottomButtons = new JPanel();
        bottomButtons.add(ok);
        bottomButtons.add(exit);

        JPanel dialog = new JPanel(new BorderLayout());
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(bottomButtons, BorderLayout.PAGE_END);

        dialog.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JFrame conf = new JFrame("Keyboard Input Viewer");
        conf.add(dialog);
        conf.pack();
        conf.setResizable(false);
        conf.setLocationRelativeTo(null);
        List<Image> icons = new ArrayList<Image>();
        icons.add(icon);
        icons.add(iconSmall);
        conf.setIconImages(icons);
        conf.addWindowListener(onClose);
        conf.setVisible(true);

        try {
            latch.await();
        } catch (InterruptedException e1) {
        }
        conf.setVisible(false);
        conf.dispose();
        frame.setAlwaysOnTop(config.overlay);
        graphFrame.setAlwaysOnTop(config.overlay);
    }

    /**
     * Shows the color configuration dialog
     */
    protected static final void configureColors() {
        JPanel cfg = new JPanel();
        JPanel cbg = new JPanel();
        MouseListener clistener = new MouseListener() {
            /**
             * Whether or not the color chooser is open
             */
            private boolean open = false;
            /**
             * Color chooser instance
             */
            private final JColorChooser chooser = new JColorChooser();

            @Override
            public void mouseClicked(MouseEvent e) {
                if (!open) {
                    open = true;
                    chooser.setColor(e.getComponent().getBackground());
                    if (Dialog.showSaveDialog(chooser)) {
                        e.getComponent().setBackground(chooser.getColor());
                    }
                    open = false;
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        };
        cbg.addMouseListener(clistener);
        cfg.addMouseListener(clistener);
        cfg.setBackground(Main.config.foreground);
        cbg.setBackground(Main.config.background);
        Color prevfg = cfg.getForeground();
        Color prevbg = cbg.getForeground();
        JPanel cform = new JPanel(new GridLayout(2, 3, 4, 2));
        JLabel lfg = new JLabel("Foreground colour: ");
        JLabel lbg = new JLabel("Background colour: ");
        JSpinner sbg = new JSpinner(new SpinnerNumberModel((int) (config.opacitybg * 100), 0, 100, 5));
        JSpinner sfg = new JSpinner(new SpinnerNumberModel((int) (config.opacityfg * 100), 0, 100, 5));
        sbg.setPreferredSize(new Dimension(sbg.getPreferredSize().width + 15, sbg.getPreferredSize().height));
        sfg.setPreferredSize(new Dimension(sfg.getPreferredSize().width + 15, sbg.getPreferredSize().height));
        JPanel spanelfg = new JPanel(new BorderLayout());
        JPanel spanelbg = new JPanel(new BorderLayout());
        spanelfg.add(new JLabel("Opacity (%): "), BorderLayout.LINE_START);
        spanelbg.add(new JLabel("Opacity (%): "), BorderLayout.LINE_START);
        spanelfg.add(sfg, BorderLayout.CENTER);
        spanelbg.add(sbg, BorderLayout.CENTER);
        cform.add(lfg);
        cform.add(cfg);
        cform.add(spanelfg);
        cform.add(lbg);
        cform.add(cbg);
        cform.add(spanelbg);
        if (Dialog.showSaveDialog(cform, false)) {
            config.foreground = cfg.getBackground();
            config.background = cbg.getBackground();
            config.opacitybg = (float) ((int) sbg.getValue() / 100.0D);
            config.opacityfg = (float) ((int) sfg.getValue() / 100.0D);
        } else {
            cfg.setForeground(prevfg);
            cbg.setForeground(prevbg);
        }
        frame.repaint();
    }

    /**
     * Show the command key configuration dialog
     */
    protected static final void configureCommandKeys() {
        JPanel content = new JPanel(new GridLayout(6, 2, 10, 2));

        JLabel lcp = new JLabel("Reset stats:");
        JLabel lcu = new JLabel("Exit the program:");
        JLabel lci = new JLabel("Reset totals:");
        JLabel lcy = new JLabel("Show/hide GUI:");
        JLabel lct = new JLabel("Pause/Resume:");
        JLabel lcr = new JLabel("Reload config:");

        JButton bcp = new JButton(config.CP.toString());
        JButton bcu = new JButton(config.CU.toString());
        JButton bci = new JButton(config.CI.toString());
        JButton bcy = new JButton(config.CY.toString());
        JButton bct = new JButton(config.CT.toString());
        JButton bcr = new JButton(config.CR.toString());

        content.add(lcp);
        content.add(bcp);

        content.add(lcu);
        content.add(bcu);

        content.add(lci);
        content.add(bci);

        content.add(lcy);
        content.add(bcy);

        content.add(lct);
        content.add(bct);

        content.add(lcr);
        content.add(bcr);

        bcp.addActionListener((e) -> {
            CMD cmd = CommandKeys.askForNewKey();
            if (cmd != null) {
                config.CP = cmd;
                bcp.setText(cmd.toString());
            }
        });
        bci.addActionListener((e) -> {
            CMD cmd = CommandKeys.askForNewKey();
            if (cmd != null) {
                config.CI = cmd;
                bci.setText(cmd.toString());
            }
        });
        bcu.addActionListener((e) -> {
            CMD cmd = CommandKeys.askForNewKey();
            if (cmd != null) {
                config.CU = cmd;
                bcu.setText(cmd.toString());
            }
        });
        bcy.addActionListener((e) -> {
            CMD cmd = CommandKeys.askForNewKey();
            if (cmd != null) {
                config.CY = cmd;
                bcy.setText(cmd.toString());
            }
        });
        bct.addActionListener((e) -> {
            CMD cmd = CommandKeys.askForNewKey();
            if (cmd != null) {
                config.CT = cmd;
                bct.setText(cmd.toString());
            }
        });
        bcr.addActionListener((e) -> {
            CMD cmd = CommandKeys.askForNewKey();
            if (cmd != null) {
                config.CR = cmd;
                bcr.setText(cmd.toString());
            }
        });

        Dialog.showMessageDialog(content);
    }

    /**
     * Changes the update rate
     * 
     * @param newRate The new update rate
     */
    protected static final void changeUpdateRate(int newRate) {
        n *= (double) config.updateRate / (double) newRate;
        tmp.set(0);
        timepoints.clear();
        config.updateRate = newRate;
        mainLoop();
    }

    /**
     * Builds the main GUI of the program
     * 
     * @throws IOException When an IO Exception occurs, this can be thrown
     *                     when the program fails to load its resources
     */
    protected static final void buildGUI() throws IOException {
        Menu.createMenu();
        frame.setResizable(false);
        frame.setIconImage(icon);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            frame.setUndecorated(true);
        } catch (IllegalComponentStateException e) {
            frame.dispose();
            frame.setUndecorated(true);
        }
        new Listener(frame);
        graphFrame.setResizable(false);
        graphFrame.setIconImage(icon);
        graphFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        graphFrame.setUndecorated(true);
        frame.addWindowListener(onClose);
        graphFrame.addWindowListener(onClose);
        new Listener(graphFrame);
        reconfigure();
    }

    /**
     * Reconfigures the layout of the program
     */
    public static final void reconfigure() {
        SwingUtilities.invokeLater(() -> {
            frame.getContentPane().removeAll();
            layout.removeAll();
            try {
                ColorManager.prepareImages(config.customColors);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Key k;
            int panels = 0;
            for (KeyInformation i : config.keyinfo) {
                if (!keys.containsKey(i.keycode)) {
                    k = new Key(i.name);
                    if (isNumpadKey(i.keycode)) {
                        // System.out.println("code : " + i.keycode);
                        setNumpadKey(i.keycode, k);
                    }
                    keys.put(i.keycode, k);
                    k.alt = CommandKeys.hasAlt(i.keycode);
                    k.ctrl = CommandKeys.hasCtrl(i.keycode);
                    k.shift = CommandKeys.hasShift(i.keycode);
                } else {
                    k = keys.get(i.keycode);
                }
                if (config.showKeys && i.visible) {
                    content.add(k.getPanel(i));
                    k.getPanel(i).sizeChanged();
                    panels++;
                }
            }
            if (config.showMax) {
                content.add(MaxPanel.INSTANCE);
                MaxPanel.INSTANCE.sizeChanged();
                panels++;
            }
            if (config.showAvg) {
                content.add(AvgPanel.INSTANCE);
                AvgPanel.INSTANCE.sizeChanged();
                panels++;
            }
            if (config.showCur) {
                content.add(NowPanel.INSTANCE);
                NowPanel.INSTANCE.sizeChanged();
                panels++;
            }
            if (config.showTotal) {
                content.add(TotPanel.INSTANCE);
                TotPanel.INSTANCE.sizeChanged();
                panels++;
            }
            if (panels == 0 && !config.showGraph) {
                frame.setVisible(false);
                return;// don't create a GUI if there's nothing to display
            }

            Menu.repaint();

            JPanel all = new JPanel(new BorderLayout());
            all.add(content, BorderLayout.CENTER);
            all.setOpaque(config.getBackgroundOpacity() != 1.0F ? !ColorManager.transparency : true);
            if (config.showGraph) {
                if (config.graphMode == GraphMode.INLINE) {
                    content.add(graph);
                    graphFrame.setVisible(false);
                } else {
                    graph.setOpaque(config.getBackgroundOpacity() != 1.0F ? !ColorManager.transparency : true);
                    graphFrame.add(graph);
                    graphFrame.setSize(config.graph_w * config.cellSize, config.graph_h * config.cellSize);
                    graphFrame.setVisible(true);
                }
            } else {
                graphFrame.setVisible(false);
            }
            frame.setSize(layout.getWidth(), layout.getHeight());
            if (config.getBackgroundOpacity() != 1.0F) {
                frame.setBackground(ColorManager.transparent);
                content.setOpaque(false);
                content.setBackground(ColorManager.transparent);
            } else {
                content.setOpaque(true);
                content.setBackground(config.background);
            }
            frame.add(all);
            if (panels > 0) {
                frame.setVisible(true);
            } else {
                frame.setVisible(false);
            }
        });
    }

    private static final boolean isNumpadKey(int keycode) {
        switch (keycode) {
            // NUMPAD_.
            case 9437267:
            case 1052243:
                // NUMPAD_0
            case 9437195:
            case 1052242:
                // NUMPAD_1
            case 9437186:
            case 1052239:
                // NUMPAD_2
            case 9437187:
            case 1106000:
                // NUMPAD_3
            case 9437188:
            case 1052241:
                // NUMPAD_4
            case 9437189:
            case 1105995:
                // NUMPAD_5
            case 9437190:
            case 1105996:
                // NUMPAD_6
            case 9437191:
            case 1105997:
                // NUMPAD_7
            case 9437192:
            case 1052231:
                // NUMPAD_8
            case 9437193:
            case 1105992:
                // NUMPAD_9
            case 9437194:
            case 1052233:
                return true;
            default:
                return false;
        }
    }

    private static void setNumpadKey(int keycode, Key k) {
        switch (keycode) {
            // NUMPAD_.
            case 9437267:
                keys.put(1052243, k);
                break;
            case 1052243:
                keys.put(9437267, k);
                break;
            // NUMPAD_0
            case 9437195:
                keys.put(1052242, k);
                break;
            case 1052242:
                keys.put(9437195, k);
                break;
            // NUMPAD_1
            case 9437186:
                keys.put(1052239, k);
                break;
            case 1052239:
                keys.put(9437186, k);
                break;
            // NUMPAD_2
            case 9437187:
                keys.put(1106000, k);
                break;
            case 1106000:
                keys.put(9437187, k);
                break;
            // NUMPAD_3
            case 9437188:
                keys.put(1052241, k);
                break;
            case 1052241:
                keys.put(9437188, k);
                break;
            // NUMPAD_4
            case 9437189:
                keys.put(1105995, k);
                break;
            case 1105995:
                keys.put(9437189, k);
                break;
            // NUMPAD_5
            case 9437190:
                keys.put(1105996, k);
                break;
            case 1105996:
                keys.put(9437190, k);
                break;
            // NUMPAD_6
            case 9437191:
                keys.put(1105997, k);
                break;
            case 1105997:
                keys.put(9437191, k);
                break;
            // NUMPAD_7
            case 9437192:
                keys.put(1052231, k);
                break;
            case 1052231:
                keys.put(9437192, k);
                break;
            // NUMPAD_8
            case 9437193:
                keys.put(1105992, k);
                break;
            case 1105992:
                keys.put(9437193, k);
                break;
            // NUMPAD_9
            case 9437194:
                keys.put(1052233, k);
                break;
            case 1052233:
                keys.put(9437194, k);
                break;
        }
    }

    /**
     * Shuts down the program
     */
    protected static final void exit() {
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException e1) {
            e1.printStackTrace();
        }
        Statistics.saveStatsOnExit();
        System.exit(0);
    }

    /**
     * Resets avg, max &amp; cur
     */
    protected static final void resetStats() {
        System.out.println("Reset max & avg | max: " + max + " avg: " + avg);
        n = 0;
        avg = 0;
        max = 0;
        tmp.set(0);
        graph.reset();
        frame.repaint();
        graphFrame.repaint();
    }

    /**
     * Resets key count totals
     */
    protected static final void resetTotals() {
        System.out.print("Reset key counts | ");
        for (Key k : keys.values()) {
            System.out.print(k.name + ":" + k.count + " ");
            k.count = 0;
        }
        System.out.println();
        frame.repaint();
    }

    // =================================================================================================
    // ================== NESTED CLASSES
    // ===============================================================
    // =================================================================================================

    /**
     * This class is used to keep track
     * of how many times a specific key
     * is pressed
     * 
     * @author Roan
     */
    public static class Key implements Serializable {
        /**
         * Serial ID
         */
        private static final long serialVersionUID = 1263090697516120354L;
        /**
         * Whether or not this key is currently pressed
         */
        public transient boolean down = false;
        /**
         * The total number of times this key has been pressed
         */
        public int count = 0;
        /**
         * The key in string form<br>
         * For example: X
         */
        public String name;
        /**
         * The graphical display for this key
         */
        private transient KeyPanel panel = null;
        /**
         * Whether or not alt has to be down
         */
        protected boolean alt;
        /**
         * Whether or not ctrl has to be down
         */
        protected boolean ctrl;
        /**
         * Whether or not shift has to be down
         */
        protected boolean shift;

        /**
         * Constructs a new Key object
         * for the key with the given
         * name
         * 
         * @param name The name of the key
         * @see #name
         */
        private Key(String name) {
            this.name = name;
        }

        /**
         * Creates a new KeyPanel with this
         * objects as its data source
         * 
         * @param i The information object for this key
         * @return A new KeyPanel
         */
        private KeyPanel getPanel(KeyInformation i) {
            return panel != null ? panel : (panel = new KeyPanel(this, i));
        }

        /**
         * Called when a key is pressed
         */
        protected void keyPressed() {
            if (!down && !name.equals("Right Alt") && !name.equals("Right Ctrl")) {
                count++;
                down = true;
                tmp.incrementAndGet();
                if (panel != null) {
                    panel.repaint();
                }
            }
        }

        /**
         * Called when a key is released
         */
        protected void keyReleased() {
            if (down) {
                down = false;
                if (panel != null) {
                    panel.repaint();
                }
            }
        }
    }

    /**
     * Simple class that holds all
     * the essential information
     * about a key.
     * 
     * @author Roan
     */
    public static final class KeyInformation implements Serializable, Positionable {
        /**
         * Serial ID
         */
        private static final long serialVersionUID = -8669938978334898443L;
        /**
         * The name of this key
         * 
         * @see Key#name
         */

        public String name;
        /**
         * The virtual key code of this key<br>
         * This code represents the key
         */
        public int keycode;

        public int keyLocation;
        /**
         * Whether or not this key is displayed
         */
        public boolean visible = true;
        /**
         * Auto-increment for #x
         */
        public static transient volatile int autoIndex = -2;
        /**
         * The x position of this panel in the layout
         */
        public int x = autoIndex += 2;
        /**
         * The y postion of this panel in the layout
         */
        public int y = 0;
        /**
         * The width of this panel in the layout
         */
        public int width = 2;
        /**
         * The height of this panel in the layout
         */
        public int height = 2;
        /**
         * The text rendering mode for this panel
         */
        protected RenderingMode mode = RenderingMode.VERTICAL;

        /**
         * Constructs a new KeyInformation
         * object with the given information
         * 
         * @param name  The name of the key
         * @param code  The virtual key code of the key
         * @param alt   Whether or not alt is down
         * @param ctrl  Whether or not ctrl is down
         * @param shift Whether or not shift is down
         * @param mouse Whether or not this is a mouse button
         * @see #name
         * @see #keycode
         */

        protected String bImg = null;

        public KeyInformation(String name, int code, int location, boolean alt, boolean ctrl, boolean shift,
                boolean mouse) {
            this.keycode = mouse ? code : CommandKeys.getExtendedKeyCode(code, location, shift, ctrl, alt);
            this.keyLocation = location;
            this.name = mouse ? name : getKeyName(name, keycode, keyLocation);
        }

        /**
         * Constructs the key name from the key
         * and modifiers
         * 
         * @param name The name of the key
         * @param code The virtual key code of the key
         * @return The full name of this given key
         */
        private static final String getKeyName(String name, int code, int location) {
            return ((CommandKeys.hasAlt(code) ? "a" : "") + (CommandKeys.hasCtrl(code) ? "c" : "")
                    + (CommandKeys.hasShift(code) ? "s" : ""))
                    + (name.length() == 1 ? name.toUpperCase(Locale.ROOT)
                            : getKeyText(code & CommandKeys.KEYCODE_MASK, location));
        }

        /**
         * Constructs a new KeyInformation
         * object with the given information
         * 
         * @param name    The name of the key
         * @param code    The virtual key code of the key
         * @param visible Whether or not the key is visible
         * @see #name
         * @see #keycode
         */
        protected KeyInformation(String name, int code, int location, boolean visible) {
            this.name = name;
            this.keycode = code;
            this.keyLocation = location;
            this.visible = visible;
        }

        /**
         * Changes the display name of this
         * key to the given string. If {@link Key}
         * panels are active with the same key code
         * as this {@link KeyInformation} object
         * then their display name is also updated.
         * 
         * @param name The new display name
         */
        public void setName(String name) {
            this.name = name;
            Integer code = keycode + keyLocation * 10000000;
            keys.getOrDefault(code, DUMMY_KEY).name = name;
        }

        /**
         * Gets a string containing all
         * the modifiers for this key
         * 
         * @return The modifier string
         */
        public String getModifierString() {
            return (CommandKeys.hasCtrl(keycode) ? "Ctrl + " : "") + (CommandKeys.hasAlt(keycode) ? "Alt + " : "")
                    + (CommandKeys.hasShift(keycode) ? "Shift + " : "");
        }

        @Override
        public String toString() {
            return "[keycode=" + keycode + ",keyLocation=" + keyLocation + ",x=" + x + ",y=" + y + ",width=" + width
                    + ",height=" + height + ",mode=" + mode.name() + ",bImg=" + bImg + ",visible=" + visible
                    + ",name=\"" + name + "\"]";
        }

        @Override
        public int hashCode() {
            return keycode;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof KeyInformation && keycode == ((KeyInformation) other).keycode
                    && keyLocation == ((KeyInformation) other).keyLocation;
        }

        /**
         * Legacy object initialisation
         * 
         * @see ObjectInputStream#defaultReadObject()
         * @param stream The object input stream
         * @throws IOException            When an IOException occurs
         * @throws ClassNotFoundException When this class cannot be found
         */
        private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
            stream.defaultReadObject();
            x = -1;
            y = 0;
            width = 2;
            height = 3;
            mode = RenderingMode.VERTICAL;
            keycode = CommandKeys.getExtendedKeyCode(keycode, keyLocation, false, false, false);
        }

        /**
         * Gets the key name for a key code
         * 
         * @param keyCode The key code
         * @return The key name
         */
        public static String getKeyText(int keyCode, int keyLocation) {
            switch (keyLocation) {
                case NativeKeyEvent.KEY_LOCATION_STANDARD:
                    switch (keyCode) {
                        case NativeKeyEvent.VC_ESCAPE:
                            return "Esc";
                        // Begin Function Keys
                        case NativeKeyEvent.VC_F1:
                            return "F1";
                        case NativeKeyEvent.VC_F2:
                            return "F2";
                        case NativeKeyEvent.VC_F3:
                            return "F3";
                        case NativeKeyEvent.VC_F4:
                            return "F4";
                        case NativeKeyEvent.VC_F5:
                            return "F5";
                        case NativeKeyEvent.VC_F6:
                            return "F6";
                        case NativeKeyEvent.VC_F7:
                            return "F7";
                        case NativeKeyEvent.VC_F8:
                            return "F8";
                        case NativeKeyEvent.VC_F9:
                            return "F9";
                        case NativeKeyEvent.VC_F10:
                            return "F10";
                        case NativeKeyEvent.VC_F11:
                            return "F11";
                        case NativeKeyEvent.VC_F12:
                            return "F12";
                        case NativeKeyEvent.VC_F13:
                            return "F13";
                        case NativeKeyEvent.VC_F14:
                            return "F14";
                        case NativeKeyEvent.VC_F15:
                            return "F15";
                        case NativeKeyEvent.VC_F16:
                            return "F16";
                        case NativeKeyEvent.VC_F17:
                            return "F17";
                        case NativeKeyEvent.VC_F18:
                            return "F18";
                        case NativeKeyEvent.VC_F19:
                            return "F19";
                        case NativeKeyEvent.VC_F20:
                            return "F20";
                        case NativeKeyEvent.VC_F21:
                            return "F21";
                        case NativeKeyEvent.VC_F22:
                            return "F22";
                        case NativeKeyEvent.VC_F23:
                            return "F23";
                        case NativeKeyEvent.VC_F24:
                            return "F24";
                        case 3663:
                            return "NUMPAD_1";
                        case 57424:
                            return "NUMPAD_2";
                        case 3665:
                            return "NUMPAD_3";
                        case 57419:
                            return "NUMPAD_4";
                        case 57420:
                            return "NUMPAD_5";
                        case 57421:
                            return "NUMPAD_6";
                        case 3655:
                            return "NUMPAD_7";
                        case 57416:
                            return "NUMPAD_8";
                        case 3657:
                            return "NUMPAD_9";
                        case 3666:
                            return "NUMPAD_0";
                        case 3667:
                            return "NUMPAD_.";
                        case 3639:
                            return "Print Screenshot";
                        // Begin Alphanumeric Zone
                        case NativeKeyEvent.VC_BACKQUOTE:
                            return "'";
                        case NativeKeyEvent.VC_MINUS:
                            return "-";
                        case NativeKeyEvent.VC_EQUALS:
                            return "=";
                        case NativeKeyEvent.VC_BACKSPACE:
                            return "BKSP";
                        case NativeKeyEvent.VC_TAB:
                            return "Tab";
                        case NativeKeyEvent.VC_CAPS_LOCK:
                            return "Cap";
                        case NativeKeyEvent.VC_OPEN_BRACKET:
                            return "(";
                        case NativeKeyEvent.VC_CLOSE_BRACKET:
                            return ")";
                        case NativeKeyEvent.VC_BACK_SLASH:
                            return "\\";
                        case NativeKeyEvent.VC_SEMICOLON:
                            return ";";
                        case NativeKeyEvent.VC_QUOTE:
                            return "\"";
                        case NativeKeyEvent.VC_ENTER:
                            return "ENTER";
                        case NativeKeyEvent.VC_COMMA:
                            return "comma";
                        case NativeKeyEvent.VC_PERIOD:
                            return ".";
                        case NativeKeyEvent.VC_SLASH:
                            return "/";
                        case NativeKeyEvent.VC_SPACE:
                            return "SP";
                        case NativeKeyEvent.VC_KATAKANA:
                            return "Right Alt";
                        case NativeKeyEvent.VC_KANJI:
                            return "Right Ctrl";
                        // Begin Modifier and Control Keys
                        case NativeKeyEvent.VC_SHIFT:
                        case CommandKeys.VC_RSHIFT:
                            return "\u21D1";
                        case NativeKeyEvent.VC_CONTROL:
                            return "CTRL";
                        case NativeKeyEvent.VC_ALT:
                            return "ALT";
                        case NativeKeyEvent.VC_META:
                            return "\u2318";
                        case NativeKeyEvent.VC_0:
                            return "0";
                        case NativeKeyEvent.VC_1:
                            return "1";
                        case NativeKeyEvent.VC_2:
                            return "2";
                        case NativeKeyEvent.VC_3:
                            return "3";
                        case NativeKeyEvent.VC_4:
                            return "4";
                        case NativeKeyEvent.VC_5:
                            return "5";
                        case NativeKeyEvent.VC_6:
                            return "6";
                        case NativeKeyEvent.VC_7:
                            return "7";
                        case NativeKeyEvent.VC_8:
                            return "8";
                        case NativeKeyEvent.VC_9:
                            return "9";

                    }
                case NativeKeyEvent.KEY_LOCATION_NUMPAD:
                    switch (keyCode) {
                        case 69:
                            return "Num Lock";
                        case 53:
                            return "NUMPAD_/";
                        case 3639:
                            return "NUMPAD_*";
                        case 3658:
                            return "NUMPAD_-";
                        case 3662:
                            return "NUMPAD_+";
                        case 28:
                            return "NUMPAD_Enter";
                        case 83:
                            return "NUMPAD_.";
                        case 3655:
                            return "Home";
                        case 3657:
                            return "Page Up";
                        case 3663:
                            return "End";
                        case 3665:
                            return "Page Down";
                        case 3666:
                            return "Insert";
                        case 3667:
                            return "Delete";
                        case 57416:
                            return Toolkit.getProperty("AWT.up", "Up");
                        case 57419:
                            return Toolkit.getProperty("AWT.left", "Left");
                        case 57420:
                            return Toolkit.getProperty("AWT.clear", "Clear");
                        case 57421:
                            return Toolkit.getProperty("AWT.right", "Right");
                        case 57424:
                            return Toolkit.getProperty("AWT.down", "Down");
                    }

                case NativeKeyEvent.KEY_LOCATION_LEFT:
                    switch (keyCode) {
                        case 29:
                            return "Ctrl";
                        case 42:
                            return "Shift";
                        case 56:
                            return "Alt";
                        case 3675:
                            return "Windows";
                    }
                case NativeKeyEvent.KEY_LOCATION_RIGHT:
                    switch (keyCode) {
                        case 3638:
                            return "Right Shift";
                    }
                default:
                    return NativeKeyEvent.getKeyText(keyCode, keyLocation);
            }
        }

        @Override
        public void setX(int x) {
            this.x = x;
        }

        @Override
        public void setY(int y) {
            this.y = y;
        }

        @Override
        public void setWidth(int w) {
            width = w;
        }

        @Override
        public void setHeight(int h) {
            height = h;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getX() {
            return x;
        }

        @Override
        public int getY() {
            return y;
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
            return mode;
        }

        @Override
        public void setRenderingMode(RenderingMode mode) {
            this.mode = mode;
        }

        @Override
        public String getBindingImg() {
            return bImg;
        }

        @Override
        public void setBindingImg(String bImg) {
            this.bImg = bImg;
        }
    }

    static {
        Image img;
        try {
            img = ImageIO.read(ClassLoader.getSystemResource("kiv_small.png"));
        } catch (IOException e) {
            img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
        }
        iconSmall = img;
        try {
            img = ImageIO.read(ClassLoader.getSystemResource("kiv.png"));
        } catch (IOException e) {
            img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
        }
        icon = img;
        onClose = new WindowListener() {

            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            }

            @Override
            public void windowClosed(WindowEvent e) {
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        };
        DUMMY_KEY = new Key(null) {
            /**
             * Serial ID
             */
            private static final long serialVersionUID = 5918421427659740215L;

            @Override
            public void keyPressed() {
            }

            @Override
            public void keyReleased() {
            }
        };

        // Request best text anti-aliasing settings
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        desktopHints = (Map<?, ?>) Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints");
        if (desktopHints == null) {
            Map<Object, Object> map = new HashMap<Object, Object>();
            map.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            desktopHints = map;
        } else {
            toolkit.addPropertyChangeListener("awt.font.desktophints",
                    event -> desktopHints = (Map<?, ?>) event.getNewValue());
        }
    }
}
