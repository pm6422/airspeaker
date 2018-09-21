package org.infinity.airspeaker;


import com.jcabi.ssh.Shell;
import com.jcabi.ssh.SshByPassword;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;

/**
 * AirSpeaker use to start AirPlay or ShairPlay speaker.
 */
public class AirSpeaker {

    private String HOST     = "192.168.50.230";
    private int    PORT     = 22;
    private String USERNAME = "pi";
    private String PASSWORD = "raspberry";

    public static void main(String[] args) {
        if (!SystemTray.isSupported()) {
            showError("System tray not supported!");
            return;
        }

        AirSpeaker speaker = new AirSpeaker();
        try {
            speaker.createSystemTray();
        } catch (Exception e) {
            showError(e.toString());
        }
    }

    private void createSystemTray() throws AWTException {
        // Create context menus
        PopupMenu popup = new PopupMenu();

        final MenuItem airPlayItem = new MenuItem("AirPlay Speaker");
        final MenuItem shairPlayItem = new MenuItem("ShairPlay Speaker");

        popup.add(airPlayItem);
        popup.add(shairPlayItem);

        airPlayItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    if (startAirPlay(HOST, PORT, USERNAME, PASSWORD)) {
                        airPlayItem.setEnabled(false);
                        shairPlayItem.setEnabled(true);
                    } else {
                        showError("Failed to start!");
                    }
                } catch (IOException ex) {
                    showError(ex.toString());
                }
            }
        });

        shairPlayItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    if (startShairPlay(HOST, PORT, USERNAME, PASSWORD)) {
                        airPlayItem.setEnabled(true);
                        shairPlayItem.setEnabled(false);
                    } else {
                        showError("Failed to start!");
                    }
                } catch (IOException ex) {
                    showError(ex.toString());
                }
            }
        });

        popup.addSeparator();

        MenuItem exitItem = new MenuItem("Quit");
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        popup.add(exitItem);

        // Create the app tray
        Image logo = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("speaker.png")).getImage();
        TrayIcon trayIcon = new TrayIcon(logo, "Switch speaker", popup);
        trayIcon.setImageAutoSize(true);

        // Add to system tray
        SystemTray tray = SystemTray.getSystemTray();
        tray.add(trayIcon);
    }

    private boolean startAirPlay(String host, Integer port, String username, String password) throws IOException {
        String output = new Shell.Plain(createShell(host, port, username, password)).exec("cd /usr/data/ && sudo bash ./airplay.sh");
        if (output.contains("Stopping shairplay")) {
            return true;
        }
        return false;
    }

    private boolean startShairPlay(String host, Integer port, String username, String password) throws IOException {
        String output = new Shell.Plain(createShell(host, port, username, password)).exec("cd /usr/data/ && sudo bash ./shairplay.sh");
        if (output.contains("Restarting shairplay")) {
            return true;
        }
        return false;
    }

    private Shell createShell(String host, Integer port, String username, String password) throws UnknownHostException {
        return new SshByPassword(host, port, username, password);
    }

    private static void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
