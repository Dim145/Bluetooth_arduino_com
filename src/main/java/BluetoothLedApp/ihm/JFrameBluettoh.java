package BluetoothLedApp.ihm;

import BluetoothLedApp.Controlleur;

import javax.bluetooth.RemoteDevice;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class JFrameBluettoh extends JFrame
{
    private final Controlleur controlleur;
    private final PanelControl panelPrincipal;
    private final JPanel       panelWest;

    private final JButton connexion;
    private final JButton deconnexion;

    private final JLabel consoleLabel;
    private final JLabel logLabel;

    private final List listDevice;

    public JFrameBluettoh(Controlleur controlleur )
    {
        this.controlleur = controlleur;

        this.setTitle("Bluetooth LedApp Controller");
        this.setSize(500, 500);
        this.setLocationRelativeTo(null);

        this.connexion      = new JButton("Connexion");
        this.deconnexion    = new JButton("Deconnexion");
        this.consoleLabel   = new JLabel("<html> console:<br/>");
        this.panelPrincipal = new PanelControl(this);
        this.panelWest      = new JPanel();
        this.logLabel       = new JLabel("log: ");
        this.listDevice     = new List();
        this.listDevice.setMultipleMode(false);

        this.panelWest.setLayout(new GridLayout(10, 1));
        this.panelWest.add(this.connexion);

        this.add(this.panelWest, BorderLayout.WEST);
        this.add(this.logLabel    , BorderLayout.NORTH);
        this.add(this.consoleLabel, BorderLayout.SOUTH);

        this.connexion.addActionListener(e ->
        {
            if( !this.listDevice.isShowing() )
                this.add(this.listDevice, BorderLayout.CENTER);

            this.listDevice.removeAll();
            this.controlleur.clearList();

            this.setLogText("", "Searching...");

            this.controlleur.launchSearch();
        });

        this.listDevice.addActionListener(e ->
        {
            String selected = this.listDevice.getSelectedItem();

            this.setConnexionTo(selected);
            this.controlleur.writeNewAdressInSavedFile(false, selected.split(" -> ")[1]);
        });

        this.deconnexion.addActionListener(e ->
        {
            this.panelWest.add(this.connexion, 0);
            this.panelWest.remove(this.deconnexion);
            this.remove(this.panelPrincipal);

            this.controlleur.deconnexion();
            this.repaint();
        });

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public  void setConnexionTo(String selected )
    {
        this.setConnexionTo(selected, true);
    }

    public void setConnexionTo(String selected, boolean useMainDevice)
    {
        if( this.controlleur.setMainDevice(selected, useMainDevice) )
        {
            this.panelWest.add(this.deconnexion, 0);

            this.remove(this.listDevice);
            this.add(this.panelPrincipal, BorderLayout.CENTER);

            this.panelWest.remove(this.connexion);
        }
    }

    public void setLogText(String type, String text)
    {
        this.logLabel.setText(type + " -> " + text);
    }

    public void addConsoleText(String text)
    {
        String[] consoleText = this.consoleLabel.getText().split("<br/>");

        if( consoleText.length > 7 )
        {
            this.consoleLabel.setText("<html> ");

            for (int cpt = 1; cpt < consoleText.length; cpt++ )
                this.consoleLabel.setText(this.consoleLabel.getText() + consoleText[cpt] + "<br/>");
        }

        this.consoleLabel.setText(this.consoleLabel.getText() + text + "<br/>");
    }

    public void addDeviceToList(RemoteDevice device )
    {
        this.setLogText("", "Appareil trouver");

        try
        {
            this.listDevice.add(device.getFriendlyName(false) + " -> " + device.getBluetoothAddress());
        }
        catch (IOException e)
        {
            this.listDevice.add(device.getBluetoothAddress() + " -> error");
        }
    }

    public void sendValue(int i)
    {
        this.controlleur.sendValue(i);
    }
}
