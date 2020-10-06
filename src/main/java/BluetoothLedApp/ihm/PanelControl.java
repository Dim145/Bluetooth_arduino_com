package BluetoothLedApp.ihm;

import javax.swing.*;
import java.awt.*;

public class PanelControl extends JPanel
{
    private final JFrameBluettoh ihm;

    private final JButton on;
    private final JButton off;
    private final JButton clignoter;

    public PanelControl(JFrameBluettoh jFrameBluettoh)
    {
        this.ihm = jFrameBluettoh;

        this.on        = new JButton("On");
        this.off       = new JButton("Off");
        this.clignoter = new JButton("Clignoter");

        this.on .addActionListener(e -> this.ihm.sendValue(2));
        this.off.addActionListener(e -> this.ihm.sendValue(1));
        this.clignoter.addActionListener(e -> this.ihm.sendValue(3));

        this.setLayout(new BorderLayout());

        JPanel tmp = new JPanel();
        tmp.add(this.off);
        tmp.add(this.on);

        this.add(tmp, BorderLayout.NORTH);

        tmp = new JPanel();
        tmp.add(this.clignoter);

        this.add(tmp, BorderLayout.CENTER);
    }
}
