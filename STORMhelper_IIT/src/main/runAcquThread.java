/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import static main.mainSTORM_frame.gui_;

/**
 *
 * @author STORM
 */
public class runAcquThread implements Runnable {

    mainSTORM_frame msf;
    public runAcquThread(mainSTORM_frame aThis) {
        msf = aThis;
    }

    @Override
    public void run() {
        msf.startMDA();
        //gui_.acquisitions().runAcquisition();
    }
    
}
