/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package SchoolManagementSystem;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;

/**
 *
 * @author hp
 */

class HiddenTabbedPaneUI extends BasicTabbedPaneUI {
    @Override
    protected int calculateTabAreaHeight(int tabPlacement, int runCount, int maxTabHeight) {
        return 0; // Hide the tab area
    }

    @Override
    protected void paintTabArea(Graphics g, int tabPlacement, int selectedIndex) {
        // Do nothing to hide tab headers
    }
}
