/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import static main.mainSTORM_frame.runAcquThread;
import mmcorej.CMMCore;

/**
 *
 * @author STORM
 */
public class hcaSTORM implements Runnable{
    private mainSTORM_frame parent_;
    private CMMCore core_;
    
    public hcaSTORM (mainSTORM_frame parent_ref){
        parent_ = parent_ref;
        core_ = parent_.gui_.getCMMCore();
    }
    
    @Override
    public void run() {
         //parent_.stormIt2(parent_.ar);
        String[][] para = parent_.tcs_.getMultiColSTORMdata();
//        parent_.setAllLasersOff(para);     
        parent_.tcs_.stormIt_Satya(para); 
    }
    
}
