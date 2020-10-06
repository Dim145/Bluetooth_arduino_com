package BluetoothLedApp;

import BluetoothLedApp.ihm.JFrameBluettoh;
import BluetoothLedApp.metier.BluetoothConnection;

import javax.bluetooth.RemoteDevice;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Controlleur
{
    private final BluetoothConnection metier;
    private final JFrameBluettoh ihm;

    private final File   bluetoothAdressSaved;
    private String savedAdress;

    public Controlleur()
    {
        this.bluetoothAdressSaved = new File(System.getProperty("user.home") + "/.BluetoothAppLed.config");

        if( !bluetoothAdressSaved.exists() )
        {
            try
            {
                boolean created = bluetoothAdressSaved.createNewFile();
                System.out.println("Fichier: " + bluetoothAdressSaved.getName() + "created: " + created);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        if( bluetoothAdressSaved.exists() )
        {
            try(Scanner scanner = new Scanner(bluetoothAdressSaved))
            {
                // si plusieurs adresse enregistrer, prend la dernière. (historique)
                while (scanner.hasNextLine() )
                    this.savedAdress = scanner.nextLine();
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }

        this.metier = new BluetoothConnection(this);
        this.ihm    = new JFrameBluettoh(this);

        if( this.savedAdress != null )
            this.ihm.setConnexionTo( "saved -> " + this.savedAdress, false);

        this.ihm.repaint();
    }

    public static void main(String[] args)
    {
        new Controlleur();
    }

    public boolean writeNewAdressInSavedFile( boolean eraseAllAdresseBefore, String adress )
    {
        try(FileWriter writer = new FileWriter(this.bluetoothAdressSaved) )
        {
            if( eraseAllAdresseBefore ) writer.write("\n" + adress);
            else                        writer.append("\n").append(adress);

            this.savedAdress = adress;

            writer.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public String getSavedAdress()
    {
        return this.savedAdress;
    }

    public void print(String label, String s)
    {
        switch (label)
        {
            case "ConsoleLabel" -> this.ihm.addConsoleText(s);
            case "LogLabel" ->
            {
                String[] t = s.split(":");

                if( t.length >= 2 ) this.ihm.setLogText(t[0], t[1]);
                else                this.ihm.setLogText("", t[0]);
            }
        }
    }

    public void launchSearch()
    {
        this.metier.launchSearch();
    }

    public void addDeviceToList(RemoteDevice remoteDevice)
    {
        this.ihm.addDeviceToList(remoteDevice);
    }

    public boolean setMainDevice(String selected, boolean useMainDevice )
    {
        if (useMainDevice) return this.metier.setMainDevice(selected);
        else               return this.metier.setConnectionToAdress(selected);
    }

    public void sendValue(int i)
    {
        this.metier.sendValue(i);
    }

    public void deconnexion()
    {
        this.metier.deconnexion();
    }

    public void flush()
    {
        this.metier.flush();
    }

    public void clearList()
    {
        this.metier.clearListDevice();
    }
}
