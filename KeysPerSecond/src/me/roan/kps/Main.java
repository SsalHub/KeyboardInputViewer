package me.roan.kps;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableModel;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

/**
 * This program can be used to display
 * information about how many times
 * certain keys are pressed and what
 * average, maximum and current
 * amount of keys pressed per second is
 * <pre>
 * Besides the tracking of the assigned keys
 * this program responds to 5 key events these are:
 * <ol><li><b>Ctrl + P</b>: Causes the program to reset the average and maximum value
 * And to print the statistics to standard output
 * </li><li><b>Ctrl + O</b>: Terminates the program
 * </li><li><b>Ctrl + I</b>: Causes the program to reset the amount of times a key is pressed
 * And to print the statistics to standard output
 * </li><li><b>Ctrl + Y</b>: Hides/shows the GUI
 * </li><li><b>Ctrl + T</b>: Pauses/resumes the counter</li></ol></pre>
 * The program also constantly prints the current keys per second to
 * the standard output.<br>
 * And key is only counted as being pressed if the key has been released before
 * this deals with the issue of holding a key firing multiple key press events<br>
 * This program also has support for saving and loading configurations
 * @author Roan
 */
public class Main {
	/**
	 * The number of seconds the average has
	 * been calculated for
	 */
	private static long n = 0;
	/**
	 * The number of keys pressed in the
	 * ongoing second
	 */
	private static int tmp = 0;
	/**
	 * The average keys per second
	 */
	protected static double avg;
	/**
	 * The maximum keys per second value reached so far
	 */
	protected static int max;
	/**
	 * The keys per second of the previous second
	 * used for displaying the current keys per second value
	 */
	protected static int prev;
	/**
	 * HashMap containing all the tracked keys and their
	 * virtual codes<br>Used to increment the count for the
	 * keys
	 */
	private static Map<Integer, Key> keys = new HashMap<Integer, Key>();
	/**
	 * The most recent key event, only
	 * used during the initial setup
	 */
	private static NativeKeyEvent lastevent;
	/**
	 * Key configuration data, can be serialised
	 */
	protected static List<KeyInformation> keyinfo = new ArrayList<KeyInformation>();
	/**
	 * Main panel used for showing all the sub panels that
	 * display all the information
	 */
	private static JPanel content = new JPanel(new GridLayout(1, 0, 2, 0));
	/**
	 * Graph panel
	 */
	protected static GraphPanel graph = new GraphPanel();
	/**
	 * Whether of not the frame forces itself to be the top window
	 */
	private static boolean alwaysOnTop = false;
	/**
	 * Linked list containing all the past key counts per time frame
	 */
	private static LinkedList<Integer> timepoints = new LinkedList<Integer>();
	/**
	 * The amount of milliseconds a single time frame takes
	 */
	protected static int timeframe = 1000;
	/**
	 * Whether or not to track all key presses
	 */
	private static boolean trackAll = false;
	/**
	 * How many digits to display for avg
	 */
	protected static int precision = 0;
	/**
	 * The program's main frame
	 */
	protected static final JFrame frame = new JFrame("Keys per second");
	/**
	 * The factor to multiply the frame size with
	 */
	protected static double sizeFactor = 1.0D;
	/**
	 * The right click menu
	 */
	protected static final JPopupMenu menu = new JPopupMenu();
	/**
	 * Whether or not the counter is paused
	 */
	private static boolean suspended = false;

	/**
	 * Main method
	 * @param args - configuration file path
	 */
	public static void main(String[] args) {
		String config = null;
		if(args.length >= 1){
			config = args[0];
			for(int i = 1; i < args.length; i++){
				config += args[i];
			}
			System.out.println("Attempting to load config: " + config);
		}
		relaunchFromTemp(config);
		System.out.println("Control keys:");
		System.out.println("Ctrl + P: Causes the program to reset and print the average and maximum value");
		System.out.println("Ctrl + U: Terminates the program");
		System.out.println("Ctrl + I: Causes the program to reset and print the key press statistics");
		System.out.println("Ctrl + Y: Hides/shows the GUI");
		System.out.println("Ctrl + T: Pauses/resumes the counter");
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e1) {
		}
		
		//Make sure the native hook is always unregistered
		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override
			public void run(){
				try {
					GlobalScreen.unregisterNativeHook();
				} catch (NativeHookException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		//Initialize native library and register event handlers
		setupKeyboardHook();
		
		//Set configuration for the keys
		configure(config == null ? null : new File(config));
		
		//Enter the main loop
		mainLoop();
	}
	
	/**
	 * Main loop of the program
	 * this loop updates the
	 * average, current and 
	 * maximum keys per second
	 */
	private static final void mainLoop(){
		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new Runnable() {
	        @Override
	        public void run() {
	        	if(!suspended){
	        		int totaltmp = tmp;
					for(int i : timepoints){
						totaltmp += i;
					}
					if(totaltmp > max){
						max = totaltmp;
					}
					if(totaltmp != 0){
						avg = (avg * (double)n + (double)totaltmp) / ((double)n + 1.0D);
						n++;
						System.out.println("Current keys per second: " + totaltmp + " time frame: " + tmp);
					}
					graph.addPoint(totaltmp);
					graph.repaint();
					content.repaint();
					prev = totaltmp;
					timepoints.addFirst(tmp);
					if(timepoints.size() >= 1000 / timeframe){
						timepoints.removeLast();
					}
					tmp = 0;
	        	}else{
	        		tmp = 0;
	        	}
	        }
	    }, 0, timeframe, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * Registers the native libraries and
	 * registers event handlers for key
	 * press events
	 */
	private static final void setupKeyboardHook(){
		try {
        	Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        	logger.setLevel(Level.WARNING);
        	logger.setUseParentHandlers(false);
            GlobalScreen.registerNativeHook();
        }
        catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());
            JOptionPane.showMessageDialog(null, "There was a problem registering the native hook: " + ex.getMessage(), "Keys per second", JOptionPane.ERROR_MESSAGE);
            try {
				GlobalScreen.unregisterNativeHook();
			} catch (NativeHookException e1) {
				e1.printStackTrace();
			}
            System.exit(1);
        }
		GlobalScreen.addNativeKeyListener(new NativeKeyListener(){

			@Override
			public void nativeKeyPressed(NativeKeyEvent event) {
				lastevent = event;
				if(trackAll && !keys.containsKey(event.getKeyCode())){
					keys.put(event.getKeyCode(), new Key(NativeKeyEvent.getKeyText(lastevent.getKeyCode())));
				}
				if(keys.containsKey(event.getKeyCode()) && !suspended){
					keys.get(event.getKeyCode()).keyPressed();	
				}
				if(event.getKeyCode() == NativeKeyEvent.VC_P && (event.getModifiers() & (NativeKeyEvent.CTRL_MASK | NativeKeyEvent.CTRL_L_MASK | NativeKeyEvent.CTRL_R_MASK)) != 0){
					resetStats();
				}else if(event.getKeyCode() == NativeKeyEvent.VC_U && (event.getModifiers() & (NativeKeyEvent.CTRL_MASK | NativeKeyEvent.CTRL_L_MASK | NativeKeyEvent.CTRL_R_MASK)) != 0){
					exit();
				}else if(event.getKeyCode() == NativeKeyEvent.VC_I && (event.getModifiers() & (NativeKeyEvent.CTRL_MASK | NativeKeyEvent.CTRL_L_MASK | NativeKeyEvent.CTRL_R_MASK)) != 0){
					resetTotals();
				}else if(event.getKeyCode() == NativeKeyEvent.VC_Y && (event.getModifiers() & (NativeKeyEvent.CTRL_MASK | NativeKeyEvent.CTRL_L_MASK | NativeKeyEvent.CTRL_R_MASK)) != 0){
					frame.setVisible(!frame.isVisible());
				}else if(event.getKeyCode() == NativeKeyEvent.VC_T && (event.getModifiers() & (NativeKeyEvent.CTRL_MASK | NativeKeyEvent.CTRL_L_MASK | NativeKeyEvent.CTRL_R_MASK)) != 0){
					suspended = !suspended;
				}
			}

			@Override
			public void nativeKeyReleased(NativeKeyEvent event) {
				if(keys.containsKey(event.getKeyCode())){
					keys.get(event.getKeyCode()).keyReleased();
				}
			}

			@Override
			public void nativeKeyTyped(NativeKeyEvent arg0) {
			}
		});
	}

	/**
	 * Asks the user for a configuration
	 * though a series of dialogs
	 * These dialogs also provide the
	 * option of saving or loading an
	 * existing configuration
	 */
	@SuppressWarnings("unchecked")
	private static final void configure(File cf){
		JPanel form = new JPanel(new BorderLayout());
		JPanel boxes = new JPanel(new GridLayout(8, 0));
		JPanel labels = new JPanel(new GridLayout(8, 0));
		JCheckBox cmax = new JCheckBox();
		JCheckBox cavg = new JCheckBox();
		JCheckBox ccur = new JCheckBox();
		JCheckBox ckey = new JCheckBox();
		JCheckBox cgra = new JCheckBox();
		JCheckBox ctop = new JCheckBox();
		JCheckBox ccol = new JCheckBox();
		JCheckBox call = new JCheckBox();
		cmax.setSelected(true);
		cavg.setSelected(true);
		ccur.setSelected(true);
		ckey.setSelected(true);
		JLabel lmax = new JLabel("Show maximum: ");
		JLabel lavg = new JLabel("Show average: ");
		JLabel lcur = new JLabel("Show current: ");
		JLabel lkey = new JLabel("Show keys");
		JLabel lgra = new JLabel("Show graph: ");
		JLabel ltop = new JLabel("Overlay osu!: ");
		JLabel lcol = new JLabel("Custom colours: ");
		JLabel lall = new JLabel("Track all keys");
		ltop.setToolTipText("Requires you to run osu! out of full screen mode, known to not (always) work with the wine version of osu!");
		boxes.add(cmax);
		boxes.add(cavg);
		boxes.add(ccur);
		boxes.add(ckey);
		boxes.add(cgra);
		boxes.add(ctop);
		boxes.add(ccol);
		boxes.add(call);
		labels.add(lmax);
		labels.add(lavg);
		labels.add(lcur);
		labels.add(lkey);
		labels.add(lgra);
		labels.add(ltop);
		labels.add(lcol);
		labels.add(lall);
		JPanel options = new JPanel();
		labels.setPreferredSize(new Dimension((int)labels.getPreferredSize().getWidth(), (int)boxes.getPreferredSize().getHeight()));
		options.add(labels);
		options.add(boxes);
		JPanel buttons = new JPanel(new GridLayout(8, 0));
		JButton addkey = new JButton("Add key");
		JButton load = new JButton("Load config");
		JButton save = new JButton("Save config");
		JButton updaterate = new JButton("Update rate");
		save.setEnabled(false);
		JButton graph = new JButton("Graph");
		graph.setEnabled(false);
		cgra.addActionListener((e)->{
			graph.setEnabled(cgra.isSelected());
			graph.repaint();
		});
		JButton color = new JButton("Colours");
		color.setEnabled(false);
		ccol.addActionListener((e)->{
			color.setEnabled(ccol.isSelected());
			color.repaint();
		});
		JButton precision = new JButton("Precision");
		JButton size = new JButton("Size");
		buttons.add(addkey);
		buttons.add(load);
		buttons.add(save);
		buttons.add(graph);
		buttons.add(updaterate);
		buttons.add(color);
		buttons.add(precision);
		buttons.add(size);
		form.add(options, BorderLayout.CENTER);
		options.setBorder(BorderFactory.createTitledBorder("General"));
		buttons.setBorder(BorderFactory.createTitledBorder("Config"));
		JPanel all = new JPanel(new BorderLayout());
		all.add(options, BorderLayout.LINE_START);
		all.add(buttons, BorderLayout.LINE_END);
		form.add(all, BorderLayout.CENTER);
		size.addActionListener((e)->{
			JPanel config = new JPanel(new BorderLayout());
			JSpinner s = new JSpinner(new SpinnerNumberModel(sizeFactor * 100, 50, 200, 1));
			JLabel info = new JLabel("<html>Change how big the displayed window is.<br>"
								   + "The precentage specifies how big the window is in<br>"
								   + "comparison to the default size of the window.<html>");
			config.add(info, BorderLayout.PAGE_START);
			config.add(new JSeparator(), BorderLayout.CENTER);
			JPanel line = new JPanel();
			line.add(new JLabel("Size: "));
			line.add(s);
			line.add(new JLabel("%"));
			config.add(line, BorderLayout.PAGE_END);
			JOptionPane.showMessageDialog(null, config, "Keys per second", JOptionPane.QUESTION_MESSAGE, null);
			sizeFactor = ((double)s.getValue()) / 100.0D;
		});
		precision.addActionListener((e)->{
			JPanel config = new JPanel(new BorderLayout());
			JLabel info1 = new JLabel("Specify how many digits should be displayed");
			JLabel info2 = new JLabel("beyond the decimal point for the average.");
			JPanel plabels = new JPanel(new GridLayout(2, 1, 0, 0));
			plabels.add(info1);
			plabels.add(info2);
			JComboBox<String> values = new JComboBox<String>(new String[]{"No digits beyond the decimal point", "1 digit beyond the decimal point", "2 digits beyond the decimal point", "3 digits beyond the decimal point"});
			values.setSelectedIndex(Main.precision);
			JLabel vlabel = new JLabel("Precision: ");
			JPanel pvalue = new JPanel(new BorderLayout());
			pvalue.add(vlabel, BorderLayout.LINE_START);
			pvalue.add(values, BorderLayout.CENTER);
			config.add(plabels, BorderLayout.CENTER);
			config.add(pvalue, BorderLayout.PAGE_END);
			JOptionPane.showMessageDialog(null, config, "Keys per second", JOptionPane.QUESTION_MESSAGE, null);
			Main.precision = values.getSelectedIndex();
			save.setEnabled(true);
		});
		graph.addActionListener((e)->{
			JPanel config = new JPanel();
			JSpinner backlog = new JSpinner(new SpinnerNumberModel(GraphPanel.MAX, 1, Integer.MAX_VALUE, 1));
			JCheckBox showavg = new JCheckBox();
			showavg.setSelected(GraphPanel.showAverage);
			JLabel lbacklog;
			if(timeframe != 1000){
				lbacklog = new JLabel("Backlog (seconds / " + (1000 / timeframe) + "): ");
			}else{
				lbacklog = new JLabel("Backlog (seconds): ");
			}
			JLabel lshowavg = new JLabel("Show average: ");
			JPanel glabels = new JPanel(new GridLayout(2, 1));
			JPanel gcomponents = new JPanel(new GridLayout(2, 1));
			glabels.add(lbacklog);
			glabels.add(lshowavg);
			gcomponents.add(backlog);
			gcomponents.add(showavg);
			glabels.setPreferredSize(new Dimension((int)glabels.getPreferredSize().getWidth(), (int)gcomponents.getPreferredSize().getHeight()));
			gcomponents.setPreferredSize(new Dimension(50, (int)gcomponents.getPreferredSize().getHeight()));
			config.add(glabels);
			config.add(gcomponents);
			JOptionPane.showMessageDialog(null, config, "Keys per second", JOptionPane.QUESTION_MESSAGE, null);
			GraphPanel.showAverage = showavg.isSelected();
			GraphPanel.MAX = (int)backlog.getValue();
			save.setEnabled(true);
		});
		addkey.addActionListener((e)->{
			JPanel keyform = new JPanel(new BorderLayout());
			keyform.add(new JLabel("Currently added keys (you can edit the position & visible or remove it):"), BorderLayout.PAGE_START);
			JTable keys = new JTable();
			DefaultTableModel model = new DefaultTableModel(){
				/**
				 * Serial ID
				 */
				private static final long serialVersionUID = -5510962859479828507L;				
				
				@Override
				public int getRowCount() {
					return keyinfo.size();
				}

				@Override
				public int getColumnCount() {
					return 4;
				}

				@Override
				public Object getValueAt(int rowIndex, int columnIndex) {
					switch(columnIndex){
					case 0:
						return keyinfo.get(rowIndex).index;
					case 1:
						return keyinfo.get(rowIndex).name;
					case 2:
						return keyinfo.get(rowIndex).visible;
					case 3:
						return false;
					}
					return null;
				}
				
				@Override
				public String getColumnName(int col) {
					switch(col){
					case 0:
						return "Position";
					case 1:
						return "Key";
					case 2:
						return "Visible";
					case 3:
						return "Remove";
					}
					return null;
				}

				@Override
				public Class<?> getColumnClass(int columnIndex) {
				    if (columnIndex == 2 || columnIndex ==3){
				    	return Boolean.class;
				    }
				    return super.getColumnClass(columnIndex);
				}
				
				@Override
				public boolean isCellEditable(int row, int col){
					return col != 1;
				}
				
				@Override
				public void setValueAt(Object value, int row, int col){
					if(col == 0){
						try{
							keyinfo.get(row).index = Integer.parseInt((String)value);
						}catch(NumberFormatException | NullPointerException e){
							JOptionPane.showMessageDialog(null, "Entered position not a (whole) number!", "Keys per second", JOptionPane.ERROR_MESSAGE);
						}
					}else if(col == 2){
						keyinfo.get(row).visible = (boolean)value;
					}else{
						if((boolean)value == true){
							keyinfo.remove(row);
							keys.repaint();
						}
					}
				}
			};
			keys.setModel(model);
			keys.setDragEnabled(false);
			JScrollPane pane = new JScrollPane(keys);
			pane.setPreferredSize(new Dimension((int)keys.getPreferredSize().getWidth(), 120));
			keyform.add(pane, BorderLayout.CENTER);
			JButton newkey = new JButton("Add Key");
			newkey.addActionListener((evt)->{
				if(JOptionPane.showOptionDialog(null, "Press a key and press 'OK' to add it.", "Keys per second", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"OK", "Cancel"}, 0) == 0){
					if(lastevent == null){
						JOptionPane.showMessageDialog(null, "No key pressed!", "Keys per second", JOptionPane.ERROR_MESSAGE);
						return;
					}
					KeyInformation info = new KeyInformation(NativeKeyEvent.getKeyText(lastevent.getKeyCode()), lastevent.getKeyCode());
					if(JOptionPane.showConfirmDialog(null, "Add the " + info.name + " key?", "Keys per second", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
						keyinfo.add(info);
						save.setEnabled(true);
					}
					model.fireTableDataChanged();
				}
			});
			keyform.add(newkey, BorderLayout.PAGE_END);
			JOptionPane.showOptionDialog(null, keyform, "Keys per second", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[]{"Back"}, 0);
		});
		JPanel cfg = new JPanel();
		JPanel cbg = new JPanel();
		cbg.setBackground(Color.BLACK);
		cfg.setBackground(Color.CYAN);
		MouseListener clistener = new MouseListener(){
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
				if(!open){
					open = true;
					chooser.setColor(e.getComponent().getBackground());
					if(0 == JOptionPane.showOptionDialog(null, chooser, "Keys per second", 0, JOptionPane.QUESTION_MESSAGE, null, new String[]{"OK", "Cancel"}, 0)){
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
		color.addActionListener((e)->{
			Color prevfg = cfg.getForeground();
			Color prevbg = cbg.getForeground();
			JPanel cform = new JPanel(new GridLayout(2, 3, 4, 2));	
			JLabel lfg = new JLabel("Foreground colour: ");
			JLabel lbg = new JLabel("Background colour: ");
			JSpinner sbg = new JSpinner(new SpinnerNumberModel((int)(ColorManager.opacitybg * 100), 0, 100, 5));
			JSpinner sfg = new JSpinner(new SpinnerNumberModel((int)(ColorManager.opacityfg * 100), 0, 100, 5));
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
			if(1 == JOptionPane.showOptionDialog(null, cform, "Keys per second", 0, JOptionPane.QUESTION_MESSAGE, null, new String[]{"OK", "Cancel"}, 0)){
				cfg.setForeground(prevfg);
				cbg.setForeground(prevbg);
			}else{
				ColorManager.opacitybg = (float)(double)((int)sbg.getValue() / 100.0D);
				ColorManager.opacityfg = (float)(double)((int)sfg.getValue() / 100.0D);
				save.setEnabled(true);
			}
		});
		save.addActionListener((e)->{
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			if(chooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION){
				return;
			};
			File saveloc = new File(chooser.getSelectedFile().getAbsolutePath().endsWith(".kpsconf") ? chooser.getSelectedFile().getAbsolutePath() : (chooser.getSelectedFile().getAbsolutePath() + ".kpsconf"));
			if(!saveloc.exists() || (saveloc.exists() && JOptionPane.showConfirmDialog(null, "File already exists, overwrite?", "Keys per second", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)){
				try {
					ObjectOutputStream objout = new ObjectOutputStream(new FileOutputStream(saveloc));
					objout.writeObject(keyinfo);
					objout.writeBoolean(cmax.isSelected());
					objout.writeBoolean(ccur.isSelected());
					objout.writeBoolean(cavg.isSelected());
					objout.writeBoolean(cgra.isSelected());
					objout.writeBoolean(GraphPanel.showAverage);
					objout.writeInt(GraphPanel.MAX);
					objout.writeInt(timeframe);
					objout.writeBoolean(ccol.isSelected());
					objout.writeObject(cbg.getBackground());
					objout.writeObject(cfg.getBackground());
					objout.writeBoolean(call.isSelected());
					objout.writeBoolean(ckey.isSelected());
					objout.writeDouble(4.2D);//XXX config version
					objout.writeInt(Main.precision);//since 3.9
					objout.writeFloat(ColorManager.opacitybg);//since 3.10
					objout.writeFloat(ColorManager.opacityfg);//since 3.10
					objout.writeDouble(sizeFactor);//since 3.12 / 4.0D
					objout.writeBoolean(ctop.isSelected());//since 4.2
					objout.flush();
					objout.close();
					JOptionPane.showMessageDialog(null, "Config succesfully saved", "Keys per second", JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, "Failed to save the config!", "Keys per second", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		Callable<Boolean> loadAction = new Callable<Boolean>(){

			@Override
			public Boolean call() throws Exception {
				File saveloc = cf;
				if(cf == null){
					JFileChooser chooser = new JFileChooser();
					chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					if(chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION){
						return false;
					}
					saveloc = chooser.getSelectedFile();
				}
				try {
					ObjectInputStream objin = new ObjectInputStream(new FileInputStream(saveloc));
					keyinfo = (List<KeyInformation>) objin.readObject();
					cmax.setSelected(objin.readBoolean());
					ccur.setSelected(objin.readBoolean());
					cavg.setSelected(objin.readBoolean());
					cgra.setSelected(objin.readBoolean());
					if(cgra.isSelected()){
						graph.setEnabled(true);
					}
					GraphPanel.showAverage = objin.readBoolean();
					GraphPanel.MAX = objin.readInt();
					timeframe = objin.readInt();
					double version = 3.0D;
					if(objin.available() > 0){
						ccol.setSelected(objin.readBoolean());
						if(ccol.isSelected()){
							color.setEnabled(true);
						}
						cbg.setBackground((Color)objin.readObject());
						cfg.setBackground((Color)objin.readObject());
						if(objin.available() > 0){
							call.setSelected(objin.readBoolean());
							ckey.setSelected(objin.readBoolean());
							if(objin.available() > 0){
								version = objin.readDouble();
							}
						}
					}
					if(version >= 3.9){
						Main.precision = objin.readInt();
					}
					if(version >= 3.10){
						ColorManager.opacitybg = objin.readFloat();
						ColorManager.opacityfg = objin.readFloat();
					}
					if(version >= 4.0D){
						sizeFactor = objin.readDouble();
					}
					if(version >= 4.2D){
						ctop.setSelected(objin.readBoolean());
					}
					objin.close();
					save.setEnabled(true);
					for(KeyInformation info : keyinfo){
						if(version < 3.7D){
							info.visible = true;
						}
						if(info.index > KeyInformation.autoIndex){
							KeyInformation.autoIndex = info.index + 1;
						}
					}
					if(cf == null){
						JOptionPane.showMessageDialog(null, "Config succesfully loaded", "Keys per second", JOptionPane.INFORMATION_MESSAGE);
					}
					return true;
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, "Failed to load the config!", "Keys per second", JOptionPane.ERROR_MESSAGE);
					return false;
				}
			}
		};
		load.addActionListener((e)->{
			try {
				loadAction.call();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		updaterate.addActionListener((e)->{
			JPanel info = new JPanel(new GridLayout(2, 1, 0, 0));
			info.add(new JLabel("Here you can change the rate at which"));
			info.add(new JLabel("the graph, max, avg & cur are updated."));
			JPanel config = new JPanel(new BorderLayout());
			JComboBox<String> update = new JComboBox<String>(new String[]{"1000ms", "500ms", "250ms", "200ms", "125ms", "100ms", "50ms", "25ms", "20ms", "10ms"});
			update.setSelectedItem(timeframe + "ms");
			update.setRenderer(new DefaultListCellRenderer(){
				/**
				 * Serial ID
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
					Component item = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
					if(((String)value).length() < 5 || ((String)value).equals("100ms")){
						item.setForeground(Color.RED);
					}
					return item;
				}
			});
			JLabel lupdate = new JLabel("Update rate: ");
			config.add(info, BorderLayout.PAGE_START);
			config.add(lupdate, BorderLayout.WEST);
			config.add(update, BorderLayout.CENTER);
			JOptionPane.showMessageDialog(null, config, "Keys per second", JOptionPane.QUESTION_MESSAGE, null);
			timeframe = Integer.parseInt(((String)update.getSelectedItem()).substring(0, ((String)update.getSelectedItem()).length() - 2));
			save.setEnabled(true);
		});
		String version = checkVersion();//XXX the version number 
		JLabel ver = new JLabel("<html><center><i>Version: v4.4, latest version: " + (version == null ? "unknown :(" : version) + "<br>"
				              + "<u><font color=blue>https://osu.ppy.sh/forum/t/552405</font></u></i></center></html>", SwingConstants.CENTER);
		ver.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				if(Desktop.isDesktopSupported()){
					try {
						Desktop.getDesktop().browse(new URL("https://osu.ppy.sh/forum/t/552405").toURI());
					} catch (IOException | URISyntaxException e1) {
						//pity
					}
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
		});
		form.add(ver, BorderLayout.PAGE_END);
		int option = 0;
		if(cf == null){
			option = JOptionPane.showOptionDialog(null, form, "Keys per second", 0, JOptionPane.QUESTION_MESSAGE, null, new String[]{"OK", "Exit"}, 0);
		}else{
			try {
				option = loadAction.call() ? 0 : 1;
			} catch (Exception e1) {
				option = 1;
				e1.printStackTrace();
			}
		}
		if(1 == option || option == JOptionPane.CLOSED_OPTION){
			try {
				GlobalScreen.unregisterNativeHook();
			} catch (NativeHookException e1) {
				e1.printStackTrace();
			}
			System.exit(0);
		}
		alwaysOnTop = ctop.isSelected();
		trackAll = call.isSelected();

		//Build GUI
		try {
			buildGUI(cmax.isSelected(), cavg.isSelected(), ccur.isSelected(), cgra.isSelected(), ccol.isSelected() ? cfg.getBackground() : null, ccol.isSelected() ? cbg.getBackground() : null, ckey.isSelected());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Builds the main GUI of the program
	 * @param max Whether or not to display the maximum keys per second
	 * @param avg Whether or not to display the average keys per second
	 * @param cur Whether or not to display the current keys per second
	 * @param cgraph Whether or not to display the graph
	 * @param fg Foreground color
	 * @param bg Background color
	 * @param showKeys Whether or not to display the tracked keys
	 * @throws IOException When an IO Exception occurs, this can be thrown
	 *         when the program fails the load its resources
	 */
	protected static final void buildGUI(boolean max, boolean avg, boolean cur, boolean cgraph, Color fg, Color bg, boolean showKeys) throws IOException {
		ColorManager.prepareImages(fg, bg, cgraph, fg != null && bg != null);
		SizeManager.scale(sizeFactor);
		if(ColorManager.transparency){
			content.setOpaque(false);
		}else{
			content.setBackground(bg == null ? Color.BLACK : bg);
		}
		content.setComponentPopupMenu(menu);
		keyinfo.sort((KeyInformation left, KeyInformation right) -> (left.index > right.index ? 1 : -1));
		Key k;
		int panels = 0;
		for(KeyInformation i : keyinfo){
			keys.put(i.keycode, k = new Key(i.name));
			if(showKeys && i.visible){
				content.add(k.getPanel());
				panels++;
			}
		}
		if(max){
			content.add(new MaxPanel());
			panels++;
		}
		if(avg){
			content.add(new AvgPanel());
			panels++;
		}
		if(cur){
			content.add(new NowPanel());
			panels++;
		}
		if(panels == 0 && !cgraph){
			return;//don't create a GUI if there's nothing to display
		}
		
		JMenuItem snap = new JMenuItem("Snap to edges");
		snap.setForeground(ColorManager.foreground);
		snap.addActionListener((e)->{
			Point loc = frame.getLocationOnScreen();
			Rectangle bounds = frame.getGraphicsConfiguration().getBounds();	
			frame.setLocation(Math.abs(loc.x - bounds.x) < 100 ? bounds.x : 
							  Math.abs((loc.x + frame.getWidth()) - (bounds.x + bounds.width)) < 100 ? bounds.x + bounds.width - frame.getWidth() : loc.x, 
							  Math.abs(loc.y - bounds.y) < 100 ? bounds.y : 
							  Math.abs((loc.y + frame.getHeight()) - (bounds.y + bounds.height)) < 100 ? bounds.y + bounds.height - frame.getHeight() : loc.y);
		});
		
		JMenuItem exit = new JMenuItem("Exit");
		exit.setForeground(ColorManager.foreground);
		exit.addActionListener((e)->{
			exit();
		});
		
		JMenuItem pause = new JMenuItem("Pause/resume");
		pause.setForeground(ColorManager.foreground);
		pause.addActionListener((e)->{
			suspended = !suspended;
		});
		
		JMenuItem sreset = new JMenuItem("Reset stats");
		sreset.setForeground(ColorManager.foreground);
		sreset.addActionListener((e)->{
			resetStats();
		});
		
		JMenuItem treset = new JMenuItem("Reset totals");
		treset.setForeground(ColorManager.foreground);
		treset.addActionListener((e)->{
			resetTotals();
		});
		
		menu.add(snap);
		menu.add(treset);
		menu.add(sreset);
		menu.add(pause);
		menu.add(exit);
		menu.setForeground(ColorManager.foreground);
		menu.setBackground(ColorManager.background);
		menu.setBorder(BorderFactory.createLineBorder(ColorManager.foreground));
		
		JPanel allcontent = new JPanel(new GridLayout((cgraph ? 1 : 0) + (panels > 0 ? 1 : 0), 1, 0, 0));
		allcontent.setOpaque(!ColorManager.transparency);
		if(panels > 0){
			allcontent.add(content);
		}
		if(cgraph){
			allcontent.add(graph);
			GraphPanel.frames = panels > 0 ? panels : 5;
		}
		frame.setSize((panels == 0 && cgraph) ? SizeManager.defaultGraphWidth : (panels * SizeManager.keyPanelWidth + (panels - 1) * 2), (panels > 0 ? SizeManager.subComponentHeight : 0) + (cgraph ? SizeManager.subComponentHeight : 0));
		frame.setResizable(false);
		frame.setIconImage(ImageIO.read(ClassLoader.getSystemResource("kps.png")));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(allcontent);
		frame.setUndecorated(true);
		frame.setBackground(ColorManager.opacitybg != 1.0F ? new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), ColorManager.opacitybg) : bg);
		frame.addWindowListener(new WindowListener(){

			@Override
			public void windowOpened(WindowEvent e) {				
			}

			@Override
			public void windowClosing(WindowEvent e) {				
			}

			@Override
			public void windowClosed(WindowEvent e) {
				try {
					GlobalScreen.unregisterNativeHook();
				} catch (NativeHookException e1) {
					e1.printStackTrace();
				}
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
		});
		if(alwaysOnTop){
			frame.setAlwaysOnTop(true);
		}
		frame.addMouseMotionListener(Listener.INSTANCE);
		frame.setVisible(true);
	}
	
	/**
	 * Check the KeysPerSecond version to see
	 * if we are running the latest version
	 * @return The latest version
	 */
	private static final String checkVersion(){
		try{ 			
			HttpURLConnection con = (HttpURLConnection) new URL("https://api.github.com/repos/RoanH/KeysPerSecond/tags").openConnection(); 			
			con.setRequestMethod("GET"); 		
			con.setConnectTimeout(10000); 					   
			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream())); 	
			String line = reader.readLine(); 		
			reader.close(); 	
			String[] versions = line.split("\"name\":\"v");
			int max_main = 3;
			int max_sub = 0;
			String[] tmp;
			for(int i = 1; i < versions.length; i++){
				tmp = versions[i].split("\",\"")[0].split("\\.");
				if(Integer.parseInt(tmp[0]) > max_main){
					max_main = Integer.parseInt(tmp[0]);
					max_sub = Integer.parseInt(tmp[1]);
				}else if(Integer.parseInt(tmp[0]) < max_main){
					continue;
				}else{
					if(Integer.parseInt(tmp[1]) > max_sub){
						max_sub = Integer.parseInt(tmp[1]);
					}
				}
			}
			return "v" + max_main + "." + max_sub;
		}catch(Exception e){ 	
			return null;
			//No Internet access or something else is wrong,
			//No problem though since this isn't a critical function
		}
	}
	
	/**
	 * Shuts down the program
	 */
	private static final void exit(){
		try {
			GlobalScreen.unregisterNativeHook();
		} catch (NativeHookException e1) {
			e1.printStackTrace();
		}
		System.exit(0);
	}
	
	/**
	 * Reset avg, max & cur
	 */
	private static final void resetStats(){
		System.out.println("Reset max & avg | max: " + max + " avg: " + avg);
		n = 0;
		avg = 0;
		max = 0;
		tmp = 0;
		graph.reset();
	}
	
	/**
	 * Rest key count totals
	 */
	private static final void resetTotals(){
		System.out.print("Reset key counts | ");
		for(Key k : keys.values()){
			System.out.print(k.name + ":" + k.count + " ");
			k.count = 0;
		}
		System.out.println();
	}
	
	/**
	 * Re-launches the program from the temp directory
	 * if the program path contains a ! this fixes a
	 * bug in the native library loading
	 * @param args
	 */
	private static final void relaunchFromTemp(String args){
		URL url = Main.class.getProtectionDomain().getCodeSource().getLocation(); 	
		File exe; 	
		try { 		 
			exe = new File(url.toURI()); 	
		} catch(URISyntaxException e) { 	
			exe = new File(url.getPath()); 	
		} 	
		if(!exe.getAbsolutePath().contains("!")){
			return;
		}
		File jvm = new File(System.getProperty("java.home") +  File.separator + "bin" + File.separator + "java.exe");
		if(!jvm.exists() || !exe.exists()){
			System.out.println("JVM exists: " + jvm.exists() + " Executable exists: " + exe.exists());
			JOptionPane.showMessageDialog(null, "An error occured whilst trying to launch the program >.<");
			System.exit(0);
		}
		File tmp = null;
		try {
			tmp = File.createTempFile("kps", null);
			Files.copy(exe.toPath(), tmp.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "An error occured whilst trying to launch the program >.<");
			tmp.deleteOnExit();
			tmp.delete();
			System.exit(0);
		}
		ProcessBuilder builder = new ProcessBuilder();
		if(args != null){
			builder.command(jvm.getAbsolutePath(), "-jar", tmp.getAbsolutePath(), args);
		}else{
			builder.command(jvm.getAbsolutePath(), "-jar", tmp.getAbsolutePath());
		}
		Process proc = null;
		try {
			proc = builder.start();
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "An error occured whilst trying to launch the program >.<");
			tmp.deleteOnExit();
			tmp.delete();
			System.exit(0);
		}		
		BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		String line;
		try {
			while(proc.isAlive()){
				while((line = in.readLine()) != null){
					System.out.println(line);
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
		} catch (IOException e) {
			System.err.print("Output stream chrashed :/");
		}
		tmp.deleteOnExit();
		tmp.delete();
		System.exit(0);
	}

//=================================================================================================
//================== NESTED CLASSES ===============================================================
//=================================================================================================

	/**
	 * This class is used to keep track
	 * of how many times a specific key
	 * is pressed
	 * @author Roan
	 */
	protected static final class Key {
		/**
		 * Whether or not this key is currently pressed
		 */
		protected boolean down = false;
		/**
		 * The total number of times this key has been pressed
		 */
		protected int count = 0;
		/**
		 * The key in string form<br>
		 * For example: X
		 */
		protected final String name;
		/**
		 * The graphical display for this key
		 */
		private KeyPanel panel = null;

		/**
		 * Constructs a new Key object
		 * for the key with the given
		 * name
		 * @param name The name of the key
		 * @see #name
		 */
		private Key(String name) {
			this.name = name;
		}

		/**
		 * Creates a new KeyPanel with this
		 * objects as its data source
		 * @return A new KeyPanel
		 */
		private KeyPanel getPanel() {
			return panel = new KeyPanel(this);
		}

		/**
		 * Called when a key is pressed
		 * @return Whether or not this was a key press
		 *         to register
		 */
		private void keyPressed() {
			if (!down) {
				count++;
				down = true;
				tmp++;
				if(panel != null){
					panel.repaint();
				}
			}
		}

		/**
		 * Called when a key is released
		 */
		private void keyReleased() {
			down = false;
			if(panel != null){
				panel.repaint();
			}
		}
	}

	/**
	 * Simple class that holds all
	 * the essential information 
	 * about a key<br>This class
	 * is mainly used for serialisation
	 * which allows for easy saving and
	 * loading of configurations
	 * @author Roan
	 */
	protected static final class KeyInformation implements Serializable{
		/**
		 * Serial ID
		 */
		private static final long serialVersionUID = -8669938978334898443L;
		/**
		 * The name of this key
		 * @see Key#name
		 */
		private String name;
		/**
		 * The virtual key code of this key<br>
		 * This code represents the key
		 */
		private int keycode;
		/**
		 * Index of the key
		 */
		private int index = autoIndex++;
		/**
		 * Auto-increment for #index
		 */
		private static transient int autoIndex = 0; 
		/**
		 * Whether or not this key is displayed
		 */
		private boolean visible = true;
		
		/**
		 * Constructs a new KeyInformation
		 * object with the given information
		 * @param name The name of the key
		 * @param code The virtual key code of the key
		 * @see #name
		 * @see #keycode
		 */
		private KeyInformation(String name, int code){
			this.name = name.length() == 1 ? name.toUpperCase() : parseKeyString(name);
			this.keycode = code;
		}
		
		/**
		 * Converts a long key name to
		 * something a bit shorter
		 * @param key The key to convert
		 * @return The shorter key name
		 */
		private static final String parseKeyString(String key){
			switch(key){
			case "Back Quote":
				return "'";
			case "Minus":
				return "-";
			case "Equals":
				return "=";
			case "Backspace":
				return "\u2190";
			case "Caps Lock":
				return "Cap";
			case "Open Bracket":
				return "(";
			case "Close Bracket":
				return ")";
			case "Back Slash":
				return "\\";
			case "Semicolon":
				return ";";
			case "Quote":
				return "\"";
			case "Enter":
				return "\u21B5";
			case "Comma":
				return ",";
			case "Period":
				return ".";
			case "Slash":
				return "/";
			case "Space":
				return " ";
			case "Insert":
				return "Ins";
			case "Delete":
				return "Del";
			case "Home":
				return "\u2302";
			case "Page Up":
				return "\u2191";
			case "Page Down":
				return "\u2193";
			case "Up":
				return "\u25B2";
			case "Left":
				return "\u25B0";
			case "Right":
				return "\u25B6";
			case "Down":
				return "\u25BC";
			case "Shift":
				return "\u21D1";
				default:
					return "?";
			}
		}
	}
}
