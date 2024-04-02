package main.ssalhub.kiv;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Window;
import java.awt.Dialog.ModalityType;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import locallib.roanh.util.Dialog;

public class VersionControl {
    /**
     * Version label format in italics.
     * 
     * @see #VERSION_FORMAT
     */
    private static final String VERSION_FORMAT_ITALICS = "<html><center><i>Version: %1$s, latest version: %2$s</i></center></html>";

    private static String latestVer = null;

    private static Image icon = null;

    private static JFrame parentFrame = null;

    private static String fileName = "", fileUrl = "";

    public static final void setDialogIcon(Image icon) {
        VersionControl.icon = icon;
    }

    public static final void setParentFrame(JFrame parent) {
        parentFrame = parent;
    }

    public static final String getLatestVersion(String repository) {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(
                    "https://api.github.com/repos/SsalHub/" + repository + "/tags").openConnection();
            con.setRequestMethod("GET");
            con.addRequestProperty("Accept", "application/vnd.github.v3+json");
            con.setConnectTimeout(10000);
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line = reader.readLine();
            reader.close();
            String[] versions = line.split("\"name\":\"");
            int max_main = 1;
            int max_sub = 0;
            String[] tmp;
            for (int i = 1; i < versions.length; i++) {
                try {
                    tmp = versions[i].split("\",\"")[0].split("\\.");
                    if (Integer.parseInt(tmp[0]) > max_main) {
                        max_main = Integer.parseInt(tmp[0]);
                        max_sub = Integer.parseInt(tmp[1]);
                    } else if (Integer.parseInt(tmp[0]) < max_main) {
                        continue;
                    } else {
                        if (Integer.parseInt(tmp[1]) > max_sub) {
                            max_sub = Integer.parseInt(tmp[1]);
                        }
                    }
                } catch (Exception e) {
                    continue;
                }
            }
            return max_main + "." + max_sub;
        } catch (IOException e) {
            // critical error
            return null;
        }

    }

    public static final JLabel getVersionLabel(String repos, String currentVer) {
        JLabel ver = new JLabel(
                String.format(VERSION_FORMAT_ITALICS, currentVer, "<i><font color=gray>loading</font></i>"),
                SwingConstants.CENTER);

        new Thread(() -> {
            latestVer = getLatestVersion(repos);
            ver.setText(
                    String.format(VERSION_FORMAT_ITALICS, currentVer, latestVer == null ? "unknown :(" : latestVer));
            VersionControl.autoUpdate(repos, currentVer);
        }, "Version Checker").start();
        return ver;
    }

    public static final void autoUpdate(String repos, String currentVer) {

        // return func if this version is latest version
        int cur_main = Integer.parseInt(currentVer.split("\\.")[0]);
        int max_main = Integer.parseInt(latestVer.split("\\.")[0]);
        int cur_sub = Integer.parseInt(currentVer.split("\\.")[1]);
        int max_sub = Integer.parseInt(latestVer.split("\\.")[1]);
        if (cur_main > max_main || cur_sub >= max_sub)
            return;

        if (Dialog.showDialog(new JLabel("You can update to " + latestVer + "version."), false,
                ModalityType.APPLICATION_MODAL).getDefaultCloseOperation() == 0) {
            // run auto update
            fileName = "KeyboardInputViewer_" + latestVer + ".exe";
            fileUrl = "https://github.com/SsalHub/" + repos + "/releases/download/" + latestVer + "/" + fileName;

            new Thread(() -> {
                JDialog downloadDialog = new JOptionPane(
                        new JLabel("Downloading " + fileName + " ..."),
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        new String[] {},
                        0)
                        .createDialog("Download latest version");
                JPanel downloadPanel = new JPanel(new BorderLayout());
                JLabel downloadLabel = new JLabel("Downloading " + fileName + " ...");
                JProgressBar downloadProg = new JProgressBar(JProgressBar.HORIZONTAL, 0, 1);

                downloadProg.setSize(5, 5);

                downloadPanel.add(downloadLabel, BorderLayout.NORTH);
                downloadPanel.add(downloadProg, BorderLayout.SOUTH);

                downloadDialog.add(downloadPanel);

                downloadDialog.setIconImage(VersionControl.icon);

                // JOptionPane.showMessageDialog(null, downloadLabel);

                new Thread(() -> {
                    downloadDialog.setVisible(true);
                }, "Download dialog").start();

                InputStream inStream = null;
                OutputStream outStream = null;

                try {
                    URL url = new URL(fileUrl);
                    URLConnection conn = url.openConnection();
                    int fileSize = conn.getContentLength();
                    downloadProg.setMaximum(fileSize);
                    downloadProg.setValue(0);

                    inStream = url.openStream();
                    outStream = new FileOutputStream(fileName);

                    // download latest file
                    int data = 0;
                    while (downloadDialog.isVisible()) {
                        data = inStream.read();
                        if (data == -1) {
                            break;
                        }
                        outStream.write(data);
                        downloadProg.setValue(downloadProg.getValue() + 1);
                    }

                    inStream.close();
                    outStream.close();

                    if (downloadDialog.isVisible()) {
                        // download successed
                        downloadDialog.dispose();

                        JOptionPane.showMessageDialog(null, new JLabel("Successfully downloaded latest version."));

                        Runtime.getRuntime();
                        System.exit(0);
                    } else {
                        // download canceled
                        JOptionPane.showMessageDialog(null, new JLabel("Download canceled."));
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (inStream != null)
                            inStream.close();
                        if (outStream != null)
                            outStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error occurred.");
                    }
                    new File(fileName).delete();

                    downloadDialog.dispose();
                    JOptionPane.showMessageDialog(null, new JLabel("Update failed."));
                }
            }, "Lastest version downloader").start();
        }
        ;
    }
}
