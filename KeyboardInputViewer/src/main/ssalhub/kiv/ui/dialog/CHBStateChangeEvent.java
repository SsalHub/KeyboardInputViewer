package main.ssalhub.kiv.ui.dialog;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JPanel;

public class CHBStateChangeEvent implements ItemListener {
    private JPanel mainPanel;
    private JPanel subPanel;

    public CHBStateChangeEvent(JPanel mainPanel, JPanel subPanel) {
        // TODO Auto-generated constructor stub
        this.mainPanel = mainPanel;
        this.subPanel = subPanel;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        // TODO Auto-generated method stub

        if (e.getStateChange() == ItemEvent.SELECTED) {
            setEnabled(true, subPanel);
        } else if (e.getStateChange() == ItemEvent.DESELECTED) {
            setEnabled(false, subPanel);
        }

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private static Component[] getComponents(Component container) {
        ArrayList<Component> list = null;

        try {
            list = new ArrayList<Component>(Arrays.asList(((Container) container).getComponents()));
            for (int index = 0; index < list.size(); index++) {
                for (Component currentComponent : getComponents(list.get(index))) {
                    list.add(currentComponent);
                }
            }
        } catch (ClassCastException e) {
            list = new ArrayList<Component>();
        }

        return list.toArray(new Component[list.size()]);
    }

    public void setEnabled(boolean b, Component container) {
        for (Component component : getComponents(container)) {
            component.setEnabled(b);
        }
    }

    public void setMainPanel(JPanel mainPanel) {
        this.mainPanel = mainPanel;
    }

    public void setSubPanel(JPanel subPanel) {
        this.subPanel = subPanel;
    }

}
