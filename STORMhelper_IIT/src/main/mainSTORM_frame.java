/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.concurrent.TimeUnit;
import mmcorej.CMMCore;
import org.micromanager.Studio;
import org.micromanager.acquisition.SequenceSettings;
import java.util.logging.Level;
import java.util.logging.Logger;
import mmcorej.Configuration;
import mmcorej.PropertySetting;
import mmcorej.StrVector;
import mmcorej.TaggedImage;
import org.micromanager.data.Coords;
import org.micromanager.data.Datastore;
import org.micromanager.data.Image;

/**
 *
 * @author STORM
 */

public class mainSTORM_frame extends javax.swing.JFrame {
    static mainSTORM_frame frame_;          
    public static Studio gui_ = null;
    private static CMMCore core_ = null;
    public static Thread satya2colThread;
    
    /**
     * Creates new form mainSTORM_frame
     */

    mainSTORM_frame(Studio gui_ref) {
        frame_ = this;  
        gui_ = gui_ref;
        core_ = gui_.getCMMCore();
        satya2colThread = new Thread(new satyaThread(this));
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        activationTime_label = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        satyaStart = new javax.swing.JButton();
        frameNumberField = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        savePathField = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        expFolderField = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        activationTime_label.setText("Excitation power defined in configuration group ");

        jLabel4.setText("STORM:");

        jLabel2.setText("'activationPower' and 'activationTime' define as dummy device names ");

        jLabel3.setText("use for example DemoCamera -> DHub -> D-DA and D-DA2");

        jLabel13.setText("IIT Guwahati 2 color STORM");

        satyaStart.setText("Go");
        satyaStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                satyaStartActionPerformed(evt);
            }
        });

        frameNumberField.setText("20");
        frameNumberField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                frameNumberFieldActionPerformed(evt);
            }
        });

        jLabel14.setText("Frame number");

        savePathField.setText("D:\\STORM");
        savePathField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                savePathFieldActionPerformed(evt);
            }
        });

        jLabel15.setText("SavePath");

        expFolderField.setText("exp1");

        jLabel16.setText("experiment folder");

        jLabel17.setText("Presets activation via 'AutoShutter' set to 0 (inactive) and 1 (active)");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(activationTime_label)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel17))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(expFolderField)
                                    .addComponent(savePathField, javax.swing.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
                                    .addComponent(frameNumberField, javax.swing.GroupLayout.Alignment.LEADING))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel14)
                                            .addComponent(jLabel15)))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addGap(1, 1, 1)
                                        .addComponent(jLabel16)))))
                        .addGap(0, 0, Short.MAX_VALUE))))
            .addGroup(layout.createSequentialGroup()
                .addGap(265, 265, 265)
                .addComponent(satyaStart)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addComponent(jLabel4)
                .addGap(14, 14, 14)
                .addComponent(activationTime_label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(frameNumberField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(savePathField, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(expFolderField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addGap(9, 9, 9)
                .addComponent(satyaStart)
                .addContainerGap(34, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void startSatya(){
        System.out.println("Thread Satya successful");
    }
 
    public String[][] satyaGetSetings() {
        
        String groupName = core_.getChannelGroup();
        System.out.println("Channel groupe: " + groupName);
        StrVector presetList = core_.getAvailableConfigs(groupName);
        String currPreset = presetList.get(0);
        Configuration configData;
        try {
            configData = core_.getConfigData(groupName, currPreset);
        } catch (Exception ex) {
            configData = null;
            Logger.getLogger(mainSTORM_frame.class.getName()).log(Level.SEVERE, null, ex);
        }
        int s = (int) configData.size();
        String[][] arr = new String[s][8];
        
        for (int i=0; i<presetList.size(); i++){
            try {
                currPreset = presetList.get(i);
                System.out.println("Configuration " + presetList.get(i));
                configData = core_.getConfigData(groupName, currPreset);
                String dName = "null";
                String pName = "null";
                String pValue = "null";
                for (int ii=0; ii<=s-1; ii++){
                    PropertySetting set = configData.getSetting(ii);
                    dName = set.getDeviceLabel();
                    pName = set.getPropertyName();
                    pValue = set.getPropertyValue();
                    System.out.println("device name: " + dName);
                    System.out.println("property name: " + pName);
                    System.out.println("property value: " + pValue);
                
                    arr[i][0] = groupName;
                    arr[i][1] = currPreset;
                    if("activationTime".equals(dName)){
                            arr[i][2] = pValue;
                        }else if("activationPower".equals(dName)){
                            arr[i][3] = pValue;
                        }else if("Core".equals(dName)){
                            arr[i][7] = pValue;
                        }else{
                            if(Double.parseDouble(pValue)!=0){
                                arr[i][4] = pValue;
                                arr[i][5] = dName;
                                arr[i][6] = pName;
                            }
                        }
                }
                
            } catch (Exception ex) {
                
                Logger.getLogger(mainSTORM_frame.class.getName()).log(Level.SEVERE, null, ex);
            }      
        }
        return arr;
        /*try {
            //Configuration configData = core_.getConfigGroupState("STORM");
            Configuration configData = core_.getConfigData("STORM", "STORM 462");
            System.out.println("STORM data 462");
            long s = configData.size();
            for (int i=0; i<=s-1; i++){
                PropertySetting set = configData.getSetting(i);
                String pName = set.getPropertyName();
                String pValue = set.getPropertyValue();
                System.out.println("property name: " + pName);
                System.out.println("property value: " + pValue);
            }
            configData = core_.getConfigData("STORM", "STORM 405");
            System.out.println("STORM data 405");
            s = configData.size();
            for (int i=0; i<=s-1; i++){
                PropertySetting set = configData.getSetting(i);
                String pName = set.getPropertyName();
                String pValue = set.getPropertyValue();
                System.out.println("property name: " + pName);
                System.out.println("property value: " + pValue);
            }
            configData = core_.getConfigData("STORM", "STORM 638");
            System.out.println("STORM data 638");
            s = configData.size();
            for (int i=0; i<=s-1; i++){
                PropertySetting set = configData.getSetting(i);
                String pName = set.getPropertyName();
                String pValue = set.getPropertyValue();
                System.out.println("property name: " + pName);
                System.out.println("property value: " + pValue);
            }
                    
            /*SequenceSettings acqSet1 = gui_.acquisitions().getAcquisitionSettings();
            ArrayList<ChannelSpec> channels = acqSet1.channels;
            int s = channels.size();
            String[][] resArr = new String[s][7];
            for (int i=0; i<=s-1; i++){
                ChannelSpec ch1 = channels.get(i);
                String chh1 = ch1.config;
                String group = core_.getChannelGroup();
                Configuration configData = core_.getConfigData(group, chh1);
                long si = configData.size();
                //double roundOff = Math.round(((double) i+1.0)/(double) s * 100.0) / 100.0;
                resArr[i][0] = group;
                resArr[i][1] = chh1;
                for(int ii=0; ii<=si-1; ii++){
                    PropertySetting setting = configData.getSetting(ii);
                    String propertyValue = setting.getPropertyValue();
                    String propertyName = setting.getPropertyName();
                    String deviceName = setting.getDeviceLabel();
                    //System.out.println(deviceName+" "+propertyName+" "+propertyValue+" -> ??? STORM");
                    if("activationTime".equals(deviceName)){
                        resArr[i][2] = propertyValue;
                    } else if("activationPower".equals(deviceName)){
                        resArr[i][3] = propertyValue;
                    }else if(deviceName.contains("aser")){
                        if(Double.parseDouble(propertyValue)!=0){
                            resArr[i][4] = propertyValue;
                            resArr[i][5] = deviceName;
                            resArr[i][6] = propertyName;
                        } else {
                            //System.out.println("no STORM");
                        }
                    }else{
                        System.out.println("maybe: Could not find device name 'activationTime'"+deviceName+" "+propertyName+" "+propertyValue);
                        System.out.println("or maybe: Could not find device name 'activationPower'"+deviceName+" "+propertyName+" "+propertyValue);
                        break;
                    }
                }  
            }
            return arr;
        } catch (Exception ex) {
            Logger.getLogger(mainSTORM_frame.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }*/ 
        
    }
    
    public void takeTimeSeries(String path) {
        
        try {
            int numFrames = (int) Double.parseDouble(frameNumberField.getText());
            String unique_name = gui_.data().getUniqueSaveDirectory(path);
            Datastore ds = null;
            
            double expTime = core_.getExposure();
            ds = gui_.data().createMultipageTIFFDatastore(unique_name, true, true);
            gui_.core().setExposure(expTime);
            
            
            for(int i=0; i<numFrames; i++){

                core_.snapImage();
                TaggedImage taggedImg = core_.getTaggedImage();
                Image img = gui_.data().convertTaggedImage(taggedImg);

                Coords.CoordsBuilder builder1 = gui_.data().getCoordsBuilder();
                Coords coords1 = builder1.channel(1).stagePosition(1).time(i).build();
                Image newImage = img.copyAtCoords(coords1);
                ds.putImage(newImage);
                
            }
            System.out.println("Did take " + numFrames+ " images and save under " + path);
        } catch (Exception ex) {
            Logger.getLogger(mainSTORM_frame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void satyaStartSTORM(String[][] arr) {
        double aP = 0;
        double eP = 0;
        long aT = 0;
        int active = 0;
        String laserN = null;
        String propN = null;
        String chN = null;
        String groupN = null;
        
        // get number of colors and basic path
        int numCol = arr.length;
        SequenceSettings acqSet = gui_.getAcquisitionManager().getAcquisitionSettings();
        String mainPath = savePathField.getText();
        String expName = expFolderField.getText();
        String pathB = mainPath+"\\"+expName;
        
        for(int i=0; i<numCol; i++){
            active =(int) Double.parseDouble(arr[i][7]);
            if(active==1){
                // get parameters for specific color
                aP = Double.parseDouble(arr[i][3]);
                eP = Double.parseDouble(arr[i][4]);
                aT = (long) Double.parseDouble(arr[i][2]);
                laserN = arr[i][5];
                propN = arr[i][6];
                chN = arr[i][1];
                groupN = arr[i][0];
                System.out.println("Group name: " + groupN + " channel name: " + chN + " ch count: " + i);
                System.out.println("Laser name: " + laserN + " property name: " + propN + " excPow: " + eP + " actPow: " + aP + " actTimePow: " + aT);

                // set parameters
                try {
                    //gui_.shutter().setShutter(false);
                    System.out.println("Set: "+ laserN + " " + propN + " to " + aP);
                    core_.setProperty(laserN, propN, aP);
                    System.out.println("sleep for "+  aT);
                    TimeUnit.SECONDS.sleep(aT);
                    System.out.println("Set: "+ laserN + " " + propN + " to " + eP);
                    core_.setProperty(laserN, propN, eP);
                } catch (Exception ex) {
                    Logger.getLogger(mainSTORM_frame.class.getName()).log(Level.SEVERE, null, ex);
                }

                String new_path = pathB + "\\" + chN;

                takeTimeSeries(new_path);

                try {
                    core_.setProperty(laserN, propN, 0);
                } catch (Exception ex) {
                    Logger.getLogger(mainSTORM_frame.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
        System.out.println("Finito");
    }
    
    public static void startMDA(){
        gui_.acquisitions().runAcquisition();
        gui_.acquisitions().clearRunnables();
    }
    
    private void frameNumberFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_frameNumberFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_frameNumberFieldActionPerformed

    private void savePathFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_savePathFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_savePathFieldActionPerformed

    public void satyaSetLasersOff(String[][] arr){
        double p = 0;
        String laserN = null;
        String propN = null;
        int s = arr.length;
        for(int i=0; i<s-1; i++){
            laserN = arr[i][5];
            propN = arr[i][6];
            try {
                core_.setProperty(laserN, propN, p);
            } catch (Exception ex) {
                Logger.getLogger(mainSTORM_frame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void satyaStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_satyaStartActionPerformed
        System.out.println("Satya start STORM");
        satyaSetLasersOff(satyaGetSetings()); 
        satya2colThread = new Thread(new satyaThread(this));
        satya2colThread.start();
        while(satya2colThread.isAlive()){};
        System.out.println("Satya end STORM");
        satyaSetLasersOff(satyaGetSetings());        
    }//GEN-LAST:event_satyaStartActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(mainSTORM_frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(mainSTORM_frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(mainSTORM_frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(mainSTORM_frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new mainSTORM_frame(gui_).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel activationTime_label;
    private javax.swing.JTextField expFolderField;
    public javax.swing.JTextField frameNumberField;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JButton satyaStart;
    private javax.swing.JTextField savePathField;
    // End of variables declaration//GEN-END:variables


}
