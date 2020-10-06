import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

public class Main
{
    private static DiscoveryListener listener;
    private static HashMap<RemoteDevice, ServiceRecord> listDeviceDiscovered;

    public static void main(String[] args)
    {
        System.out.println("initialisation Prog...");
        Main.initListenerAndList();

        if (LocalDevice.isPowerOn()) try
        {
            LocalDevice device = LocalDevice.getLocalDevice();
            System.out.println(
                    "Infos:\nname: " + device.getFriendlyName() + "\n" + "adress: " + device.getBluetoothAddress() + "\ndeviceClass: " + device.getDeviceClass() + "\ndiscoverable: " + device.getDiscoverable());
            device.setDiscoverable(DiscoveryAgent.GIAC);

            DiscoveryAgent agent = device.getDiscoveryAgent();

            agent.startInquiry(DiscoveryAgent.GIAC, Main.listener);

            System.out.println("Attente de la découverte d'appareil...");
            while (Main.listDeviceDiscovered.size() == 0)
            {
                Thread.sleep(15000);
            }

            RemoteDevice hc05 = null;
            System.out.println("Recherche de HC-05 avec address \"98D331FC220D\"");
            for (RemoteDevice remoteDevice : listDeviceDiscovered.keySet())
                if (remoteDevice.getBluetoothAddress().equals("98D331FC220D")) hc05 = remoteDevice;

            if (hc05 == null)
            {
                System.out.println("HC-05 non trouver.");
            }
            else
            {
                System.out.println("HC-05 trouver !");
                System.out.println("isAuthenticated: " + hc05.isAuthenticated());
                System.out.println("isEncrypted: " + hc05.isEncrypted());
                System.out.println("isTrustedDevice: " + hc05.isTrustedDevice());

                String stringCon = "btspp://" + hc05.getBluetoothAddress() + ":1;authenticate=false;encrypt=false;master=true";


                System.out.println("StringConn: " + stringCon);

                System.out.println("Connection dans 2s...");
                Thread.sleep(2000);
                StreamConnection connection = (StreamConnection) Connector.open(stringCon);

                OutputStream outputStream = connection.openOutputStream();
                System.out.println("Connecter, ecriture dans 2s...");
                outputStream.write(Byte.parseByte("0"));

                System.out.println("envoi de la données dans 2s: 0");
                Thread.sleep(2000);
                outputStream.flush();

                System.out.println("Fermeture dans 2s...");
                Thread.sleep(2000);
                outputStream.close();
            }
        }
        catch (InterruptedException | IOException e)
        {
            e.printStackTrace();
        }
        else System.out.println("Please, active bluetooth.");
    }

    private static void initListenerAndList()
    {
        Main.listDeviceDiscovered = new HashMap<>();

        Main.listener = new DiscoveryListener()
        {
            @Override
            public void deviceDiscovered(RemoteDevice remoteDevice, DeviceClass deviceClass)
            {
                System.out.println("device added: " + remoteDevice.getBluetoothAddress());
                listDeviceDiscovered.put(remoteDevice, null);
            }

            @Override
            public void servicesDiscovered(int i, ServiceRecord[] serviceRecords)
            {
                System.out.println("Service record: " + serviceRecords.length);
                for (ServiceRecord record : serviceRecords)
                {
                    try
                    {
                        System.out.println("device: " + record.getHostDevice().getFriendlyName(false) +
                                "service: " + record.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false));
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    if( listDeviceDiscovered.containsKey(record.getHostDevice()) )
                        listDeviceDiscovered.replace(record.getHostDevice(), record);
                    else
                        listDeviceDiscovered.put(record.getHostDevice(), record);
                }
            }

            @Override
            public void serviceSearchCompleted(int i, int i1)
            {

            }

            @Override
            public void inquiryCompleted(int i)
            {
                System.out.println("Devices: (" + listDeviceDiscovered.size() + ")");

                for (RemoteDevice remoteDevice : listDeviceDiscovered.keySet())
                {
                    try
                    {
                        System.out.println(
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
}
