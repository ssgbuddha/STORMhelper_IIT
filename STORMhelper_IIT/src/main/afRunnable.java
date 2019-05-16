/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.logging.Level;
import java.util.logging.Logger;
import mmcorej.CMMCore;

/**
 *
 * @author STORM
 */
public class afRunnable implements Runnable{
    private mainSTORM_frame parent_;
    private CMMCore core_;
    
    public afRunnable (mainSTORM_frame parent_ref){
        parent_ = parent_ref;
        core_ = parent_.gui_.getCMMCore();
    }
    
    @Override
    public void run() {    
        System.out.println("afRunnable");       
        try {
            parent_.af_.goToFocus();
        } catch (Exception ex) {
            Logger.getLogger(afRunnable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
