package main.ssalhub.kiv.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Dialog.ModalityType;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;

import main.ssalhub.kiv.CommandKeys;
import main.ssalhub.kiv.Configuration;
import main.ssalhub.kiv.Main;
import main.ssalhub.kiv.Main.KeyInformation;
import locallib.roanh.util.Dialog;

/**
 * Logic for the key setup dialog.
 * 
 * @author Roan
 */
public class KeysDialog {

    public static final void selectKeyMode() {
        Image allkeys, arrow, numpad;
        try {
            allkeys = ImageIO.read(ClassLoader.getSystemResource("keymode_allkeys.jpg"));
            // arrow = ImageIO.read(ClassLoader.getSystemResource("keymode_arrow.jpg"));
            // numpad = ImageIO.read(ClassLoader.getSystemResource("keymode_numpad.jpg"));
        } catch (IOException e) {
            allkeys = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
            // arrow = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
            // numpad = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
        }

        JPanel customPanel = new JPanel(new GridLayout(1, 2, -200, 0));
        customPanel.add(new JLabel("Custom setting : "));
        customPanel.add(
                new JLabel(String.format("<html><b>[Default option]</b> You can customize the key setting.</html>")));
        JPanel allkeyPanel = new JPanel(new GridLayout(1, 2, -300, 0));
        allkeyPanel.add(new JLabel("*All keys : "));
        allkeyPanel.add(new JLabel(new ImageIcon(allkeys)));
        // JPanel arrowPanel = new JPanel(new GridLayout(1, 2, -600, 0));
        // arrowPanel.add(new JLabel("*Basic+Arrow keys : "));
        // arrowPanel.add(new JLabel(new ImageIcon(arrow)));
        // JPanel numpadPanel = new JPanel(new GridLayout(1, 2, -600, 0));
        // numpadPanel.add(new JLabel("*Basic+Numpad keys : "));
        // numpadPanel.add(new JLabel(new ImageIcon(numpad)));

        // JPanel selectPanel = new JPanel(new GridLayout(4, 1, 0, 0));
        JPanel selectPanel = new JPanel(new GridLayout(2, 1, 0, 0));

        selectPanel.add(customPanel);
        selectPanel.add(allkeyPanel);
        // selectPanel.add(arrowPanel);
        // selectPanel.add(numpadPanel);

        JPanel basePanel = new JPanel(new BorderLayout());
        basePanel.add(new JLabel("If this is your first time adding keys, try the options below."), BorderLayout.NORTH);
        basePanel.add(selectPanel, BorderLayout.CENTER);

        int selection = 0;
        // String[] options = new String[] {"Cusom setting", "*All keys", "*Basic+Arrow
        // keys", "*Basic+Numpad keys"};
        String[] options = new String[] { "Cusom setting", "*All keys" };
        // selection = Dialog.showDialog(basePanel, false, options);
        selection = Dialog.showDialog(basePanel, false, ModalityType.APPLICATION_MODAL).getDefaultCloseOperation();
        switch (selection) {
            case 0:
                configureKeys();
                break;
            case 1:
                setAllKeys();
                break;
            // case 2:
            // setArrow();
            // break;
            // case 3:
            // setNumpad();
            // break;
        }

        return;
    }

    /**
     * Shows the key configuration dialog
     */
    public static final void configureKeys() {
        List<KeyInformation> copy = new ArrayList<KeyInformation>(Main.config.keyinfo);
        boolean[] visibleState = new boolean[copy.size()];
        String[] nameState = new String[copy.size()];
        for (int i = 0; i < copy.size(); i++) {
            visibleState[i] = copy.get(i).visible;
            nameState[i] = copy.get(i).name;
        }

        JPanel keyform = new JPanel(new BorderLayout());
        keyform.add(new JLabel("Currently added keys (you can edit these fields):"), BorderLayout.PAGE_START);
        JTable keys = new JTable();
        KeysModel model = new KeysModel();
        keys.setModel(model);
        keys.setDragEnabled(false);
        JScrollPane pane = new JScrollPane(keys);
        pane.setPreferredSize(new Dimension((int) keyform.getPreferredSize().getWidth() + 50, 120));
        keyform.add(pane, BorderLayout.CENTER);
        JButton newkey = new JButton("Add Key");
        newkey.addActionListener(e -> showAddKeyDialog(model));
        JButton newmouse = new JButton("Add Mouse Button");
        newmouse.addActionListener(e -> showAddMouseButtonDialog(model));
        JPanel nbuttons = new JPanel(new GridLayout(1, 2, 2, 0));
        nbuttons.add(newkey, BorderLayout.LINE_START);
        nbuttons.add(newmouse, BorderLayout.LINE_END);
        keyform.add(nbuttons, BorderLayout.PAGE_END);

        if (!Dialog.showSaveDialog(keyform, true, ModalityType.APPLICATION_MODAL)) {
            for (int i = 0; i < copy.size(); i++) {
                copy.get(i).visible = visibleState[i];
                copy.get(i).setName(nameState[i]);
            }
            Main.config.keyinfo = copy;
        }
    }

    /**
     * Shows the dialog for adding an new mouse button.
     * 
     * @param model The model for the table listing all the keys.
     */
    private static void showAddMouseButtonDialog(KeysModel model) {
        JPanel addform = new JPanel(new BorderLayout());
        addform.add(new JLabel("Select the mouse buttons to add:"), BorderLayout.PAGE_START);

        JPanel buttons = new JPanel(new GridLayout(5, 1, 2, 0));
        String[] names = new String[] { "M1", "M2", "M3", "M4", "M5" };
        JCheckBox[] boxes = new JCheckBox[] {
                new JCheckBox(names[0] + " (left click)"),
                new JCheckBox(names[1] + " (right click)"),
                new JCheckBox(names[2] + " (mouse wheel)"),
                new JCheckBox(names[3]),
                new JCheckBox(names[4])
        };

        for (JCheckBox box : boxes) {
            buttons.add(box);
        }

        addform.add(buttons, BorderLayout.CENTER);

        if (Dialog.showSaveDialog(addform)) {
            for (int i = 0; i < boxes.length; i++) {
                if (boxes[i].isSelected()) {
                    KeyInformation key = new KeyInformation(names[i], -(i + 1), 0, false, false, false, true);
                    if (Main.config.keyinfo.contains(key)) {
                        KeyInformation.autoIndex -= 2;
                        Dialog.showMessageDialog(
                                "The " + names[i] + " button was already added before.\nIt was not added again.");
                    } else {
                        Main.config.keyinfo.add(key);
                    }
                } // new KeyInformation(names[i], -(i + 1), false, false, false, true)
            }
            model.fireTableDataChanged();
        }
    }

    /**
     * Shows the dialog for adding an key.
     * 
     * @param model The model for the table listing all the keys.
     */
    private static void showAddKeyDialog(KeysModel model) {
        Main.lastevent = null;
        JPanel form = new JPanel(new GridLayout(Main.config.enableModifiers ? 4 : 1, 1));
        JLabel txt = new JLabel("Press a key and click 'Save' to add it.");
        form.add(txt);
        JCheckBox ctrl = new JCheckBox();
        JCheckBox alt = new JCheckBox();
        JCheckBox shift = new JCheckBox();
        if (Main.config.enableModifiers) {
            JPanel a = new JPanel(new BorderLayout());
            JPanel c = new JPanel(new BorderLayout());
            JPanel s = new JPanel(new BorderLayout());
            c.add(ctrl, BorderLayout.LINE_START);
            c.add(new JLabel("Ctrl"), BorderLayout.CENTER);
            a.add(alt, BorderLayout.LINE_START);
            a.add(new JLabel("Alt"), BorderLayout.CENTER);
            s.add(shift, BorderLayout.LINE_START);
            s.add(new JLabel("Shift"), BorderLayout.CENTER);
            form.add(c);
            form.add(a);
            form.add(s);
        }
        if (Dialog.showSaveDialog(form)) {
            if (Main.lastevent == null) {
                Dialog.showMessageDialog("No key pressed!");
                return;
            }
            KeyInformation info = new KeyInformation(
                    NativeKeyEvent.getKeyText(Main.lastevent.getKeyCode()),
                    Main.lastevent.getKeyCode(), Main.lastevent.getKeyLocation(),
                    (alt.isSelected() || CommandKeys.isAltDown) && Main.config.enableModifiers,
                    (ctrl.isSelected() || CommandKeys.isCtrlDown) && Main.config.enableModifiers,
                    (shift.isSelected() || CommandKeys.isShiftDown) && Main.config.enableModifiers, false);
            int n = (CommandKeys.hasAlt(info.keycode) ? 1 : 0) + (CommandKeys.hasCtrl(info.keycode) ? 1 : 0)
                    + (CommandKeys.hasShift(info.keycode) ? 1 : 0);
            if (Dialog.showConfirmDialog("Add the " + info.getModifierString() + info.name.substring(n) + " key?")) {
                if (isAddedNumpadKey(info, alt.isSelected(), ctrl.isSelected(), shift.isSelected())
                        || Main.config.keyinfo.contains(info)) {
                    KeyInformation.autoIndex -= 2;
                    Dialog.showMessageDialog("That key was already added before.\nIt was not added again.");
                } else {
                    Main.config.keyinfo.add(info);
                }
            }
            model.fireTableDataChanged();
        }
    }

    private static boolean isAddedNumpadKey(KeyInformation info, boolean alt, boolean ctrl, boolean shift) {
        int corrCode;
        switch (info.keycode) {
            // NUMPAD_.
            case 9437267:
                corrCode = 1052243;
                break;
            case 1052243:
                corrCode = 9437267;
                break;
            // NUMPAD_0
            case 9437195:
                corrCode = 1052242;
                break;
            case 1052242:
                corrCode = 9437195;
                break;
            // NUMPAD_1
            case 9437186:
                corrCode = 1052239;
                break;
            case 1052239:
                corrCode = 9437186;
                break;
            // NUMPAD_2
            case 9437187:
                corrCode = 1106000;
                break;
            case 1106000:
                corrCode = 9437187;
                break;
            // NUMPAD_3
            case 9437188:
                corrCode = 1052241;
                break;
            case 1052241:
                corrCode = 9437188;
                break;
            // NUMPAD_4
            case 9437189:
                corrCode = 1105995;
                break;
            case 1105995:
                corrCode = 9437189;
                break;
            // NUMPAD_5
            case 9437190:
                corrCode = 1105996;
                break;
            case 1105996:
                corrCode = 9437190;
                break;
            // NUMPAD_6
            case 9437191:
                corrCode = 1105997;
                break;
            case 1105997:
                corrCode = 9437191;
                break;
            // NUMPAD_7
            case 9437192:
                corrCode = 1052231;
                break;
            case 1052231:
                corrCode = 9437192;
                break;
            // NUMPAD_8
            case 9437193:
                corrCode = 1105992;
                break;
            case 1105992:
                corrCode = 9437193;
                break;
            // NUMPAD_9
            case 9437194:
                corrCode = 1052233;
                break;
            case 1052233:
                corrCode = 9437194;
                break;
            default:
                return false;
        }
        for (int i = 0; i < Main.config.keyinfo.size(); i++) {
            if (Main.config.keyinfo.get(i).keycode == corrCode)
                return true;
        }
        return false;
    }

    private static void setAllKeys() {
        int[][] allKeyInfoList = new int[][] {
                { 3145757, 2, 0, 0, 3, 2 },
                { 3149403, 2, 3, 0, 3, 2 },
                { 3145784, 2, 6, 0, 3, 2 },
                { 1048633, 1, 9, 0, 11, 2 },
                { 1048688, 1, 20, 0, 3, 2 },
                { 1052253, 1, 23, 0, 3, 2 },
                { 1048697, 1, 26, 0, 3, 2 },
                { 9494603, 4, 30, 0, 2, 2 },
                { 9494608, 4, 32, 0, 2, 2 },
                { 9494605, 4, 34, 0, 2, 2 },
                { 1052242, 1, 37, 0, 4, 2 },
                { 1052243, 1, 41, 0, 2, 2 },
                { 9437212, 4, 43, 0, 2, 4 },
                { 3145770, 2, 0, 2, 5, 2 },
                { 1048620, 1, 5, 2, 2, 2 },
                { 1048621, 1, 7, 2, 2, 2 },
                { 1048622, 1, 9, 2, 2, 2 },
                { 1048623, 1, 11, 2, 2, 2 },
                { 1048624, 1, 13, 2, 2, 2 },
                { 1048625, 1, 15, 2, 2, 2 },
                { 1048626, 1, 17, 2, 2, 2 },
                { 1048627, 1, 19, 2, 2, 2 },
                { 1048628, 1, 21, 2, 2, 2 },
                { 1048629, 1, 23, 2, 2, 2 },
                { 5770806, 3, 25, 2, 4, 2 },
                { 9494600, 4, 32, 2, 2, 2 },
                { 1052239, 1, 37, 2, 2, 2 },
                { 1106000, 1, 39, 2, 2, 2 },
                { 1052241, 1, 41, 2, 2, 2 },
                { 1048634, 1, 0, 4, 4, 2 },
                { 1048606, 1, 4, 4, 2, 2 },
                { 1048607, 1, 6, 4, 2, 2 },
                { 1048608, 1, 8, 4, 2, 2 },
                { 1048609, 1, 10, 4, 2, 2 },
                { 1048610, 1, 12, 4, 2, 2 },
                { 1048611, 1, 14, 4, 2, 2 },
                { 1048612, 1, 16, 4, 2, 2 },
                { 1048613, 1, 18, 4, 2, 2 },
                { 1048614, 1, 20, 4, 2, 2 },
                { 1048615, 1, 22, 4, 2, 2 },
                { 1048616, 1, 24, 4, 2, 2 },
                { 1048604, 1, 26, 4, 3, 2 },
                { 1105995, 1, 37, 4, 2, 2 },
                { 1105996, 1, 39, 4, 2, 2 },
                { 1105997, 1, 41, 4, 2, 2 },
                { 9440846, 4, 43, 4, 2, 4 },
                { 1048591, 1, 0, 6, 3, 2 },
                { 1048592, 1, 3, 6, 2, 2 },
                { 1048593, 1, 5, 6, 2, 2 },
                { 1048594, 1, 7, 6, 2, 2 },
                { 1048595, 1, 9, 6, 2, 2 },
                { 1048596, 1, 11, 6, 2, 2 },
                { 1048597, 1, 13, 6, 2, 2 },
                { 1048598, 1, 15, 6, 2, 2 },
                { 1048599, 1, 17, 6, 2, 2 },
                { 1048600, 1, 19, 6, 2, 2 },
                { 1048601, 1, 21, 6, 2, 2 },
                { 1048602, 1, 23, 6, 2, 2 },
                { 1048603, 1, 25, 6, 2, 2 },
                { 1048619, 1, 27, 6, 2, 2 },
                { 9440851, 4, 30, 6, 2, 2 },
                { 9440847, 4, 32, 6, 2, 2 },
                { 9440849, 4, 34, 6, 2, 2 },
                { 1052231, 1, 37, 6, 2, 2 },
                { 1105992, 1, 39, 6, 2, 2 },
                { 1052233, 1, 41, 6, 2, 2 },
                { 1048617, 1, 0, 8, 2, 2 },
                { 1048578, 1, 2, 8, 2, 2 },
                { 1048579, 1, 4, 8, 2, 2 },
                { 1048580, 1, 6, 8, 2, 2 },
                { 1048581, 1, 8, 8, 2, 2 },
                { 1048582, 1, 10, 8, 2, 2 },
                { 1048583, 1, 12, 8, 2, 2 },
                { 1048584, 1, 14, 8, 2, 2 },
                { 1048585, 1, 16, 8, 2, 2 },
                { 1048586, 1, 18, 8, 2, 2 },
                { 1048587, 1, 20, 8, 2, 2 },
                { 1048588, 1, 22, 8, 2, 2 },
                { 1048589, 1, 24, 8, 2, 2 },
                { 1048590, 1, 26, 8, 3, 2 },
                { 9440850, 4, 30, 8, 2, 2 },
                { 9440839, 4, 32, 8, 2, 2 },
                { 9440841, 4, 34, 8, 2, 2 },
                { 9437253, 4, 37, 8, 2, 2 },
                { 9437237, 4, 39, 8, 2, 2 },
                { 9440823, 4, 41, 8, 2, 2 },
                { 9440842, 4, 43, 8, 2, 2 },
                { 1048577, 1, 0, 11, 2, 2 },
                { 1048635, 1, 3, 11, 2, 2 },
                { 1048636, 1, 5, 11, 2, 2 },
                { 1048637, 1, 7, 11, 2, 2 },
                { 1048638, 1, 9, 11, 2, 2 },
                { 1048639, 1, 12, 11, 2, 2 },
                { 1048640, 1, 14, 11, 2, 2 },
                { 1048641, 1, 16, 11, 2, 2 },
                { 1048642, 1, 18, 11, 2, 2 },
                { 1048643, 1, 21, 11, 2, 2 },
                { 1048644, 1, 23, 11, 2, 2 },
                { 1048663, 1, 25, 11, 2, 2 },
                { 1048664, 1, 27, 11, 2, 2 },
                { 1052215, 1, 30, 11, 2, 2 },
                { 1048646, 1, 32, 11, 2, 2 },
                { 1052229, 1, 34, 11, 2, 2 }
        };

        for (int[] infoList : allKeyInfoList) {
            KeyInformation info = new KeyInformation(NativeKeyEvent.getKeyText(infoList[0]), infoList[0],
                    infoList[1], (false || CommandKeys.isAltDown) && Main.config.enableModifiers,
                    (false || CommandKeys.isCtrlDown) && Main.config.enableModifiers,
                    (false || CommandKeys.isShiftDown) && Main.config.enableModifiers, false);

            info.x = infoList[2];
            info.y = infoList[3];
            info.width = infoList[4];
            info.height = infoList[5];
            Main.config.keyinfo.add(info);
        }
    }

    /**
     * private static void setArrow() {
     * int[][] arrowKeyInfoList = new int[][] {
     * {1048606, 1, 1, 0, 2, 2},
     * {1048607, 1, 3, 0, 2, 2},
     * {1048608, 1, 5, 0, 2, 2},
     * {1048592, 1, 0, 2, 2, 2},
     * {1048593, 1, 2, 2, 2, 2},
     * {1048594, 1, 4, 2, 2, 2},
     * {1048595, 1, 6, 2, 2, 2},
     * {9494603, 4, 8, 0, 2, 2},
     * {9494608, 4, 10, 0, 2, 2},
     * {9494605, 4, 12, 0, 2, 2},
     * {9494600, 4, 10, 2, 2, 2}
     * };
     * 
     * for(int[] infoList : arrowKeyInfoList) {
     * KeyInformation info = new
     * KeyInformation(NativeKeyEvent.getKeyText(infoList[0], infoList[1]),
     * infoList[0], infoList[1], (false || CommandKeys.isAltDown) &&
     * Main.config.enableModifiers, (false || CommandKeys.isCtrlDown) &&
     * Main.config.enableModifiers, (false || CommandKeys.isShiftDown) &&
     * Main.config.enableModifiers, false);
     * 
     * info.x = infoList[2];
     * info.y = infoList[3];
     * info.width = infoList[4];
     * info.height = infoList[5];
     * Main.config.keyinfo.add(info);}
     * }
     * 
     * private static void setNumpad() {
     * int[][] numpadKeyInfoList = new int[][] {
     * {1048606, 1, 1, 1, 2, 2},
     * {1048607, 1, 3, 1, 2, 2},
     * {1048608, 1, 5, 1, 2, 2},
     * {1048592, 1, 0, 3, 2, 2},
     * {1048593, 1, 2, 3, 2, 2},
     * {1048594, 1, 4, 3, 2, 2},
     * {1048595, 1, 6, 3, 2, 2},
     * {9437186, 4, 8, 0, 2, 2},
     * {9437187, 4, 10, 0, 2, 2},
     * {9437188, 4, 12, 0, 2, 2},
     * {9437189, 4, 8, 2, 2, 2},
     * {9437190, 4, 10, 2, 2, 2},
     * {9437191, 4, 12, 2, 2, 2},
     * {9437192, 4, 8, 4, 2, 2},
     * {9437193, 4, 10, 4, 2, 2},
     * {9437194, 4, 12, 4, 2, 2}
     * };
     * 
     * for(int[] infoList : numpadKeyInfoList) {
     * KeyInformation info = new
     * KeyInformation(NativeKeyEvent.getKeyText(infoList[0], infoList[1]),
     * infoList[0], infoList[1], (false || CommandKeys.isAltDown) &&
     * Main.config.enableModifiers, (false || CommandKeys.isCtrlDown) &&
     * Main.config.enableModifiers, (false || CommandKeys.isShiftDown) &&
     * Main.config.enableModifiers, false);
     * 
     * info.x = infoList[2];
     * info.y = infoList[3];
     * info.width = infoList[4];
     * info.height = infoList[5];
     * Main.config.keyinfo.add(info);
     * }
     * }
     * 
     */

    /**
     * Table model that displays all configured keys.
     * 
     * @author Roan
     */
    private static class KeysModel extends DefaultTableModel {
        /**
         * Serial ID
         */
        private static final long serialVersionUID = -5510962859479828507L;

        @Override
        public int getRowCount() {
            return Main.config.keyinfo.size();
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return Main.config.keyinfo.get(rowIndex).name;
                case 1:
                    return Main.config.keyinfo.get(rowIndex).visible;
                case 2:
                    return false;
                default:
                    return null;
            }
        }

        @Override
        public String getColumnName(int col) {
            switch (col) {
                case 0:
                    return "Key";
                case 1:
                    return "Visible";
                case 2:
                    return "Remove";
                default:
                    return null;
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 1 || columnIndex == 2) {
                return Boolean.class;
            }
            return super.getColumnClass(columnIndex);
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return true;
        }

        @Override
        public void setValueAt(Object value, int row, int col) {
            switch (col) {
                case 0:
                    Main.config.keyinfo.get(row).setName((String) value);
                    break;
                case 1:
                    Main.config.keyinfo.get(row).visible = (boolean) value;
                    break;
                case 2:
                    if ((boolean) value == true) {
                        Integer code = Main.config.keyinfo.get(row).keycode
                                + Main.config.keyinfo.get(row).keyLocation * 10000000;
                        Main.keys.remove(code);
                        Main.config.keyinfo.remove(row);
                        this.fireTableDataChanged();
                    }
                    break;
            }
        }
    }
}
