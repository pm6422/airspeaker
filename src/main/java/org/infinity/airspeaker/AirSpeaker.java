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

    private static final String HOST     = "192.168.50.230";
    private static final int    PORT     = 22;
    private static final String USERNAME = "pi";
    private static final String PASSWORD = "raspberry";

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

    private void createSystemTray() throws AWTException, IOException {
        // Create context menus
        PopupMenu popup = new PopupMenu();

        final MenuItem versionItem = new MenuItem("Air Speaker v1.00");
        versionItem.setEnabled(false);
        popup.add(versionItem);

        popup.addSeparator();

        final MenuItem airPlayItem = new MenuItem("AirPlay Speaker");
        final MenuItem shairPlayItem = new MenuItem("ShairPlay Speaker");

        popup.add(airPlayItem);
        popup.add(shairPlayItem);

        airPlayItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    airPlayItem.setEnabled(false);
                    if (startAirPlay(HOST, PORT, USERNAME, PASSWORD)) {
                        airPlayItem.setEnabled(false);
                        shairPlayItem.setEnabled(true);
                    } else {
                        airPlayItem.setEnabled(true);
                        showError("Failed to start!");
                    }
                } catch (IOException ex) {
                    airPlayItem.setEnabled(true);
                    showError(ex.toString());
                }
            }
        });

        shairPlayItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    shairPlayItem.setEnabled(false);
                    if (startShairPlay(HOST, PORT, USERNAME, PASSWORD)) {
                        airPlayItem.setEnabled(true);
                        shairPlayItem.setEnabled(false);
                    } else {
                        shairPlayItem.setEnabled(true);
                        showError("Failed to start!");
                    }
                } catch (IOException ex) {
                    shairPlayItem.setEnabled(true);
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

        disabledMenuItem(airPlayItem, shairPlayItem);
    }

    private boolean startAirPlay(String host, Integer port, String username, String password) throws IOException {
        new Shell.Plain(createShell(host, port, username, password)).exec("cd /usr/data/ && sudo bash ./airplay.sh");
        return isAirPlayRunning();
    }

    private boolean startShairPlay(String host, Integer port, String username, String password) throws IOException {
        new Shell.Plain(createShell(host, port, username, password)).exec("cd /usr/data/ && sudo bash ./shairplay.sh");
        return isShairPlayRunning();
    }

    private Shell createShell(String host, Integer port, String username, String password) throws UnknownHostException {
        return new SshByPassword(host, port, username, password);
    }

    private static void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void disabledMenuItem(MenuItem airPlayItem, MenuItem shairPlayItem) throws IOException {
        if (isAirPlayRunning()) {
            airPlayItem.setEnabled(false);
        }
        if (isShairPlayRunning()) {
            shairPlayItem.setEnabled(false);
        }
    }

    private boolean isAirPlayRunning() throws IOException {
        String output = new Shell.Plain(createShell(HOST, PORT, USERNAME, PASSWORD)).exec("docker ps");
        return output.contains("airplay");
    }

    private boolean isShairPlayRunning() throws IOException {
        String output = new Shell.Plain(createShell(HOST, PORT, USERNAME, PASSWORD)).exec("ps -ef | grep shairplay");
        return output.contains("shairplay -a ShairPlay Speaker");
    }
}
