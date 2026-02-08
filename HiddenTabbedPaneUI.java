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



public class HiddenTabbedPaneUI extends BasicTabbedPaneUI {
    // Method to calculate the height of the tab area
    @Override
    protected int calculateTabAreaHeight(int tabPlacement, int runCount, int maxTabHeight) {
        return 0; // Hide the tab area
    }

    // Method to paint the tab area
    @Override
    protected void paintTabArea(Graphics g, int tabPlacement, int selectedIndex) {
        // Do nothing to hide tab headers
    }
}
