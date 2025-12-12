package Util;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class ClickListener extends MouseAdapter {
    private Runnable onClick;

    public ClickListener(Runnable onClick) {
        this.onClick = onClick;
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        if (onClick != null) {
            onClick.run();
        }
    }
}