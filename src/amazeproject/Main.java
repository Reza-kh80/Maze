/*
 * Main.java
 *
 * Created on June 20, 2007, 7:08 PM
 *
 */

package amazeproject;

import gui.aMazeGUI;

/**
 * Maze Application 
 * @author Tomasz Gebarowski
 */
public class Main {
    
    /** Creates a new instance of Main */
    public Main() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new aMazeGUI().setVisible(true);
            }
        });
    }
    
}
