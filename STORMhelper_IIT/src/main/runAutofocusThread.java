/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.logging.Level;
import java.util.logging.Logger;
import static main.mainSTORM_frame.gui_;

/**
 *
 * @author STORM
 */
public class runAutofocusThread implements Runnable {

    public runAutofocusThread(mainSTORM_frame aThis) {
    }

    @Override
    public void run() {
        try {
            mainSTORM_frame.autoFocusGo();
        } catch (InterruptedException ex) {
            Logger.getLogger(runAutofocusThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
