/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import static main.mainSTORM_frame.gui_;
import mmcorej.CMMCore;
import mmcorej.Configuration;
import mmcorej.PropertySetting;
import mmcorej.TaggedImage;
import org.micromanager.PositionList;
import org.micromanager.Studio;
import org.micromanager.acquisition.ChannelSpec;
import org.micromanager.acquisition.SequenceSettings;
import org.micromanager.data.Coords;
import org.micromanager.data.Datastore;
import org.micromanager.data.Image;

/**
 *
 * @author STORM
 */
public class twoColSTORM {
    private mainSTORM_frame parent_;
    private CMMCore core_;
    public static Studio gui_;
    
    public twoColSTORM(mainSTORM_frame aThis) {
        parent_ = aThis;
        gui_ = parent_.gui_;
        core_ = gui_.getCMMCore();
    }
    
    String xyPath = null;
    int numAF = 0;
    boolean afON = false;
    
    public void setFrameOfAF(int num){
        numAF = num;
    }
    
    public void setAF(boolean af){
        afON = af;
    }
    
    public void takeTimeSeries() {
        SequenceSettings acqSet = gui_.getAcquisitionManager().getAcquisitionSettings();
        int numFrames = acqSet.numFrames;
        String root = acqSet.root;
        String prefix = acqSet.prefix;
        String path = root+"\\"+prefix;
        String unique_name = gui_.data().getUniqueSaveDirectory(path);
        Datastore ds = null;
        try {
            double expTime = core_.getExposure();
            ds = gui_.data().createMultipageTIFFDatastore(unique_name, true, true);
            gui_.core().setExposure(expTime);
        } catch (IOException ex) {
            Logger.getLogger(twoColSTORM.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(twoColSTORM.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        for(int i=0; i<numFrames; i++){
            try {
                core_.snapImage();
                TaggedImage taggedImg = core_.getTaggedImage();
                Image img = gui_.data().convertTaggedImage(taggedImg);

                Coords.CoordsBuilder builder1 = gui_.data().getCoordsBuilder();
                Coords coords1 = builder1.channel(1).stagePosition(1).time(i).build();
                Image newImage = img.copyAtCoords(coords1);
                ds.putImage(newImage);
            } catch (Exception ex) {
                Logger.getLogger(twoColSTORM.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Did take " + numFrames+ " images and save under " + path);
    }
    
    public void takeTimeSeries(String path) {
        SequenceSettings acqSet = gui_.getAcquisitionManager().getAcquisitionSettings();
        int numFrames = acqSet.numFrames;
        String unique_name = gui_.data().getUniqueSaveDirectory(path);
        Datastore ds = null;
        try {
            double expTime = core_.getExposure();
            ds = gui_.data().createMultipageTIFFDatastore(unique_name, true, true);
            gui_.core().setExposure(expTime);
        } catch (IOException ex) {
            Logger.getLogger(twoColSTORM.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(twoColSTORM.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        for(int i=0; i<numFrames; i++){
            try {
                core_.snapImage();
                TaggedImage taggedImg = core_.getTaggedImage();
                Image img = gui_.data().convertTaggedImage(taggedImg);

                Coords.CoordsBuilder builder1 = gui_.data().getCoordsBuilder();
                Coords coords1 = builder1.channel(1).stagePosition(1).time(i).build();
                Image newImage = img.copyAtCoords(coords1);
                ds.putImage(newImage);
            } catch (Exception ex) {
                Logger.getLogger(twoColSTORM.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Did take " + numFrames+ " images and save under " + path);
    }
    
    public void takeTimeSeriesWithAF(String path) {
        SequenceSettings acqSet = gui_.getAcquisitionManager().getAcquisitionSettings();
        int numFrames = acqSet.numFrames;
        String unique_name = gui_.data().getUniqueSaveDirectory(path);
        Datastore ds = null;
        try {
            double expTime = core_.getExposure();
            ds = gui_.data().createMultipageTIFFDatastore(unique_name, true, true);
            gui_.core().setExposure(expTime);
        } catch (IOException ex) {
            Logger.getLogger(twoColSTORM.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(twoColSTORM.class.getName()).log(Level.SEVERE, null, ex);
        }
        for(int i=0; i<numFrames; i++){
            try {
                if(i == numAF){
                    parent_.af_.goToFocus();
                }
                core_.waitForDevice(core_.getFocusDevice());
                core_.snapImage();
                TaggedImage taggedImg = core_.getTaggedImage();
                Image img = gui_.data().convertTaggedImage(taggedImg);

                Coords.CoordsBuilder builder1 = gui_.data().getCoordsBuilder();
                Coords coords1 = builder1.channel(1).stagePosition(1).time(i).build();
                Image newImage = img.copyAtCoords(coords1);
                ds.putImage(newImage);
            } catch (Exception ex) {
                Logger.getLogger(twoColSTORM.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Did take " + numFrames+ " images and save under " + path);
    }
    
    public String[][] getMultiColSTORMdata(){
        try {
            SequenceSettings acqSet1 = gui_.acquisitions().getAcquisitionSettings();
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
            return resArr;
        } catch (Exception ex) {
            Logger.getLogger(mainSTORM_frame.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }        
    }
    
    public void stormIt(String[][] arr){
        double aP = 0;
        double eP = 0;
        long aT = 0;
        String laserN = null;
        String propN = null;
        String chN = null;
        String groupN = null;
        
        // get number of colors and basic path
        int numCol = arr.length;
        SequenceSettings acqSet = gui_.getAcquisitionManager().getAcquisitionSettings();
        String pathB = acqSet.root+"\\"+acqSet.prefix;
        
        //  channel loop
        parent_.af_.goToFocus();
        
        try {
            core_.waitForDevice(core_.getFocusDevice());
        } catch (Exception ex) {
            Logger.getLogger(twoColSTORM.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        for(int i=0; i<numCol; i++){
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
            
            String new_path = pathB + "\\" + chN + "\\pos";
            if(afON){
                takeTimeSeriesWithAF(new_path);
            } else{
                takeTimeSeries(new_path);
            }
            try {
                core_.setProperty(laserN, propN, 0);
            } catch (Exception ex) {
                Logger.getLogger(twoColSTORM.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Finito");
    }
    
    public void stormIt_Satya(String[][] arr){
        double aP = 0;
        double eP = 0;
        long aT = 0;
        String laserN = null;
        String propN = null;
        String chN = null;
        String groupN = null;
        
        // get number of colors and basic path
        int numCol = arr.length;
        SequenceSettings acqSet = gui_.getAcquisitionManager().getAcquisitionSettings();
        String pathB = acqSet.root+"\\"+acqSet.prefix;
        
        //  channel loop
        /*parent_.af_.goToFocus();
        
        try {
            core_.waitForDevice(core_.getFocusDevice());
        } catch (Exception ex) {
            Logger.getLogger(twoColSTORM.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        
        for(int i=0; i<numCol; i++){
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
            
            String new_path = pathB + "\\" + chN + "\\pos";
            
            takeTimeSeries(new_path);
            
            try {
                core_.setProperty(laserN, propN, 0);
            } catch (Exception ex) {
                Logger.getLogger(twoColSTORM.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Finito");
    }
    
    public void allLaserOff(String[][] arr){
        double aP = 0;

        String laserN = null;
        String propN = null;
        
        // get number of colors and basic path
        int numCol = arr.length;
        
        //  channel loop
        for(int i=0; i<numCol; i++){
            // get parameters for specific color
            aP = 0;
            laserN = arr[i][5];
            propN = arr[i][6];
            // set parameters
            try {
                //gui_.shutter().setShutter(false);
                System.out.println("Set: "+ laserN + " " + propN + " to " + aP);
                core_.setProperty(laserN, propN, aP);
            } catch (Exception ex) {
                Logger.getLogger(mainSTORM_frame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    public void stormItxy(String[][] arr){
        double aP = 0;
        double eP = 0;
        long aT = 0;
        String laserN = null;
        String propN = null;
        String chN = null;
        String groupN = null;
        
        // get number of colors and basic path
        int numCol = arr.length;
        SequenceSettings acqSet = gui_.getAcquisitionManager().getAcquisitionSettings();
        String pathB = acqSet.root+"\\"+acqSet.prefix+"\\"+xyPath;
        
        //  channel loop
        for(int i=0; i<numCol; i++){
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
            allLaserOff(arr);
        }
        System.out.println("Finito");
    }
    
    public void doXY(){ 
        PositionList pList = gui_.getPositionListManager().getPositionList();
        int numP = pList.getNumberOfPositions();
        String[][] arr = getMultiColSTORMdata();
        
        for(int i=0;i<numP;i++){
            double xP = pList.getPosition(i).getX();
            double yP = pList.getPosition(i).getY();
            try {
                core_.setXYPosition(xP, yP);
                core_.waitForDevice(core_.getXYStageDevice());
                stormIt(arr);
                allLaserOff(arr);
            } catch (Exception ex) {
                Logger.getLogger(twoColSTORM.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void doXYSTORM(){ 
        PositionList pList = gui_.getPositionListManager().getPositionList();
        int numP = pList.getNumberOfPositions();
        String[][] arr = getMultiColSTORMdata();
        if(numP == 0){
            stormIt(arr);
        } else {
            for(int i=0;i<numP;i++){
                double xP = pList.getPosition(1).getX();
                double yP = pList.getPosition(1).getY();
                xyPath = "xPos"+Double.toString(xP)+"_yPos"+Double.toString(xP);
                try {
                    core_.setXYPosition(xP, yP);
                    boolean checkSt = true;
                    while(checkSt){
                        Point2D.Double pos = core_.getXYStagePosition(core_.getXYStageDevice());
                        System.out.println("Stage is busy!");
                        checkSt = false;
                    } 
                    stormItxy(arr);
                } catch (Exception ex) {
                    Logger.getLogger(twoColSTORM.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
