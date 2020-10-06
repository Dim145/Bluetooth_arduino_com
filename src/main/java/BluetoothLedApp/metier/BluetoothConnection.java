package BluetoothLedApp.metier;

import BluetoothLedApp.Controlleur;

import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import java.io.*;
import java.util.ArrayList;

public class BluetoothConnection
{
    private final DiscoveryListener       discoveryListener;
    private final ArrayList<RemoteDevice> listDeviceDiscovered;
    private final Controlleur             controlleur;

    private RemoteDevice deviceSelected;

    private StreamConnection streamConnection;
    private DataOutputStream outputStream;
    private DataInputStream  inputStream;

    private LocalDevice localDevice;

    public BluetoothConnection(Controlleur controlleur)
    {
        this.controlleur          = controlleur;
        this.listDeviceDiscovered = new ArrayList<>();

        this.discoveryListener = new DiscoveryListener()
        {
            @Override
            public void deviceDiscovered(RemoteDevice remoteDevice, DeviceClass deviceClass)
            {
                controlleur.print("ConsoleLabel", "device added: " + remoteDevice.getBluetoothAddress());
                controlleur.addDeviceToList(remoteDevice);
                listDeviceDiscovered.add(remoteDevice);
            }

            @Override
            public void servicesDiscovered(int i, ServiceRecord[] serviceRecords)
            {
                controlleur.print("ConsoleLabel", "Service record: " + serviceRecords.length);
            }

            @Override
            public void serviceSearchCompleted(int i, int i1)
            {

            }

            @Override
            public void inquiryCompleted(int i)
            {
                controlleur.print( "ConsoleLabel", "Devices: (" + listDeviceDiscovered.size() + ")");

                for (RemoteDevice remoteDevice : listDeviceDiscovered )
                {
                    try
                    {
                        controlleur.print( "ConsoleLabel",
                                remoteDevice.getFriendlyName(false) + " => " + remoteDevice.getBluetoothAddress());
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    /*public RemoteDevice[] getAllRemoteDevices()
    {
        return listDeviceDiscovered.toArray(RemoteDevice[]::new);
    }*/

    public void clearListDevice()
    {
        this.listDeviceDiscovered.clear();
    }

    public void launchSearch()
    {
        if( LocalDevice.isPowerOn() )
        {
            if( this.localDevice == null ) try
            {
                this.localDevice    = LocalDevice.getLocalDevice();

            }
            catch (BluetoothStateException e)
            {
                e.printStackTrace();
                this.controlleur.print("ConsoleLabel", e.getLocalizedMessage());
            }

            try
            {
                try
                {
                    this.localDevice.setDiscoverable(DiscoveryAgent.GIAC);
                }
                catch (Exception ignored)
                {

                }

                DiscoveryAgent discoveryAgent = this.localDevice.getDiscoveryAgent();
                discoveryAgent.startInquiry(DiscoveryAgent.GIAC, this.discoveryListener);
            }
            catch (BluetoothStateException e)
            {
                e.printStackTrace();
                this.controlleur.print("ConsoleLabel", e.getLocalizedMessage());
            }
        }
        else
        {
            this.controlleur.print("LogLabel", "error: Bluetooth is off, please activate.");
        }
    }

    public boolean setMainDevice(String selected)
    {
        String[] infos = selected.split(" -> ");

        for (RemoteDevice device : this.listDeviceDiscovered)
        {
            if (infos[1].equals(device.getBluetoothAddress()))
            {
                this.deviceSelected = device;
                break;
            }
        }

        if( this.deviceSelected == null )
        {
            this.controlleur.print("LogLabel", "error: device inconnue");
            this.controlleur.print("ConsoleLabel", "l'appareil avec l'adresse \"" + selected + "\" est inconnue");

            return false;
        }

        return this.setConnectionToAdress(this.deviceSelected.getBluetoothAddress());
    }

    public boolean setConnectionToAdress( String adresse )
    {
        String[] infos = adresse.split(" -> ");

        String stringConn = "btspp://" + infos[infos.length > 1 ? 1 : 0] + ":1;authenticate=false;encrypt=false;master=true";

        try
        {
            this.streamConnection = (StreamConnection) Connector.open(stringConn);

            this.outputStream = this.streamConnection.openDataOutputStream();
            this.inputStream  = this.streamConnection.openDataInputStream();

            //outputStream.write(0);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            this.controlleur.print("ConsoleLabel", "Erreur de connexion: \n" + e.getLocalizedMessage());
            return false;
        }

        this.controlleur.print("LogLabel", " : Connecter");

        return true;
    }

    public void sendValue(int i)
    {
        try
        {
            this.outputStream.writeByte(i);

            this.controlleur.print("ConsoleLabel", "value \"" + i + "\" sended");
        }
        catch (IOException e)
        {
            e.printStackTrace();
            this.controlleur.print("ConsoleLabel", e.getLocalizedMessage());
        }
    }

    public void flush()
    {
        try
        {
            this.outputStream.flush();
            this.controlleur.print("ConsoleLabel", "donnée en attente envoyer");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void deconnexion()
    {
        try
        {
            this.outputStream.close();
            this.outputStream = null;
        }
        catch (IOException ignored)
        {

        }

        try
        {
            this.inputStream.close();
            this.inputStream = null;
        }
        catch (IOException ignored)
        {

        }

        try
        {
            this.streamConnection.close();
            this.streamConnection = null;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        this.deviceSelected = null;
        this.listDeviceDiscovered.clear();
    }
}
