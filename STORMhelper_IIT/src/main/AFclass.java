/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import static com.google.common.collect.Iterables.size;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import static java.lang.Math.round;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import mmcorej.CMMCore;
import org.apache.commons.math.ArgumentOutsideDomainException;
import org.jfree.ui.RefineryUtilities;
import org.scijava.util.FileUtils;
/**
 *
 * @author STORM
 */


public class AFclass {
    AFlogic aflog_ = new AFlogic();
    private mainSTORM_frame parent_;
    private CMMCore core_;
    Scanner sc; 
    double afFoc = 65;
    double realFoc = 0;
    String zDev = null;
    long mody = 0;
    ArrayList<Double> afList = new ArrayList<Double>();
    ArrayList<Double> reList = new ArrayList<Double>();
    ArrayList<Double> xList = new ArrayList<Double>();
    double rangeZ = 15; // umeter
    double incZ = 1;    // umater
    double lowerLim = 0;
    double upperLim = 0;
    double linear_range = 20;
    double currZ = 0;
    double current_z = 9000;
    double new_z_pos = 9000;
    double current_rad = 0;
    int ccc =0;
    
    static Thread sent;
    static Thread receive;
    static Socket socket;
    static Thread buttonListner;
    
    static Thread sentSerial;
    static Thread receiveSerial;
    
    private DataInputStream din = null;
    private PrintStream pout = null;
    private Scanner scan = null;
    private String path = "C:\\Program Files\\Micro-Manager-2.0beta\\zPosSTORM.txt";
    private File repCalib;

    public void saveCalib() {
        BufferedWriter bw = null;
        int c = 0;
        Double helpy = 0.0;
        try {
            bw = new BufferedWriter (new FileWriter (path));
            System.out.println("save: "+afList);
            int l = size(afList);
            System.out.println("length of list:  "+ l);
            for(Double line:afList){
                System.out.println(c);
                System.out.println(line);
                helpy = reList.get(c);
                bw.write(line + "\t" + helpy + "\r\n");
                c = c+1;
            }
            bw.close ();
        } catch (IOException ex) {
            Logger.getLogger(AFclass.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void saveCalibLoop(File repCalib, int num) {
        BufferedWriter bw = null;
        int c = 0;
        Double helpy = 0.0;
        String newPath = FileUtils.getPath(repCalib) + "/calib" + num + ".txt";
        try {
            bw = new BufferedWriter (new FileWriter (newPath));
            for(Double line:afList){
                helpy = reList.get(c);
                bw.write(line + "\t" + helpy + "\r\n");
                c = c+1;
            }
            bw.close ();
        } catch (IOException ex) {
            Logger.getLogger(AFclass.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void changeAFpath(String text) {
        path = text;
    }

    public void goToFocus(){
        
        try {
            aflog_.read_file(path);
            //aflog_.check_lists();
            aflog_.interpolate(aflog_.z_pos_list, aflog_.rad_list);
            //aflog_.check_lists();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AFclass.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ArgumentOutsideDomainException ex) {
            Logger.getLogger(AFclass.class.getName()).log(Level.SEVERE, null, ex);
        }
            
            
            String current_pos;
            String hh = "0";
            synchronized(control){
                control.flagSent=true;
                boolean cc = true;
                while(cc){
                    if(control.flagRead){
                        hh = control.pyZ;
                        //current_rad = Double.parseDouble(control.pyZ);
                        cc = false;
                        control.flagRead = false;
                    }
                }
            }
            boolean co = true;
            while(co){
                if(hh!=control.pyZ){
                    current_rad = Double.parseDouble(control.pyZ);
                    co=false;
                }
            }
            current_rad = Double.parseDouble(control.pyZ);
            //synchronized(control){
            //System.out.println("current radius =" + control.pyZ);
            if(current_rad < upperLim && current_rad > lowerLim){
                System.out.println("current_radius = " + current_rad);
                current_pos = aflog_.look_up_defocus(current_rad);

                System.out.println("focus radius = " + afFoc);

                String Focus_pos = aflog_.look_up_defocus(afFoc);
                double defocus = (Math.round(100*((Double.parseDouble(Focus_pos) - Double.parseDouble(current_pos)))))/100.0;
                System.out.println("defocus = " + defocus);
            try {
                current_z = core_.getPosition(zDev);
            } catch (Exception ex) {
                Logger.getLogger(AFclass.class.getName()).log(Level.SEVERE, null, ex);
            }

                new_z_pos = current_z + defocus;
                //System.out.println(new_z_pos);
                current_z = Math.round(new_z_pos*100.0)/100.0;

                System.out.println(current_z);
            try {
                core_.setPosition(zDev, current_z);
                core_.waitForDevice(zDev);
            } catch (Exception ex) {
                Logger.getLogger(AFclass.class.getName()).log(Level.SEVERE, null, ex);
            }
                
            }
            else if(current_rad >= upperLim){
                String upperlim_pos = aflog_.look_up_defocus(upperLim);
                String lowerlim_pos = aflog_.look_up_defocus(lowerLim);
                linear_range = Double.parseDouble(upperlim_pos)-Double.parseDouble(lowerlim_pos);
                current_z = Math.round((current_z - linear_range)*100.0)/100.0;
                //core_.setPosition(zDev, current_z);
                //core_.waitForDevice(zDev);
                        }
            else if(current_rad <= lowerLim){
                String upperlim_pos = aflog_.look_up_defocus(upperLim);
                String lowerlim_pos = aflog_.look_up_defocus(lowerLim);
                linear_range = Double.parseDouble(upperlim_pos)-Double.parseDouble(lowerlim_pos);
                //System.out.println(linear_range);
                current_z = Math.round((current_z + linear_range)*100.0)/100.0;
                //System.out.println(current_z);
                //core_.setPosition(zDev, current_z);
                //core_.waitForDevice(zDev);
                        }
            
    }        
    
    public File chooseFolder(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        int result = fileChooser.showOpenDialog(parent_);
        if (result == JFileChooser.APPROVE_OPTION) {
            repCalib = fileChooser.getSelectedFile();
        } 
        return repCalib;
    }

    public void repCalib(double reps, int slp) {
        // choose folder to save
        repCalib = chooseFolder();
        
        // start loop of calibs
        for(int i=0; i<reps; i++){
           goToFocus();
           calib(false);
           saveCalibLoop(repCalib, i);
            try {
                Thread.sleep(slp*1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(AFclass.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    class Control {
        public volatile boolean flagReceive = false;
        public volatile boolean flagSent = false;
        public volatile boolean flagRead = false;
        public volatile String pyZ = "-600";
        
        
        
    }
    
    final Control control = new Control();
    
    final Control conSerial = new Control();
    
    public void set_parent(Object parentframe){
        parent_ = (mainSTORM_frame) parentframe;
        core_ = mainSTORM_frame.gui_.getCMMCore();
        zDev = core_.getFocusDevice();
    }
    
    public double checkAF(String zpos){
        try{
            double ret = Double.parseDouble(zpos);
            return ret;
        }catch(NumberFormatException e){
            System.out.println("z-position in .txt file not convertabel to double");
            return 0;
        } 
    }
    
    public void iniSocket(){
        try {
                socket = new Socket("localhost",9999);
            } catch (UnknownHostException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        
            sent = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        //control.flagBL = false;
                        //int c = 0;
                        while(true){
                            synchronized(control){
                                
                               
                                //Thread.sleep(20);
                                if(control.flagSent){
                                    //c = c+1;
                                    //System.out.println("Loop Sent, counter = " + c);
                                    out.write("call"+"\r\n");
                                    out.flush();
                                    control.flagSent = false;
                                    control.flagReceive = true;
                                }
                            }
                        }    
                    } catch (IOException e) {
                        System.out.println("Could not send to socket");
                        //e.printStackTrace();
                    }
                }            
            });
            
            receive = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        InputStreamReader stdIn =new InputStreamReader(socket.getInputStream());
                        BufferedReader in =new BufferedReader(stdIn);
                        //control.flag = false;
                        //int c = 0;
                        String hh = "0";
                        boolean co = true;
                        while(true){
                           
                            synchronized(control){
                                
                                //Thread.sleep(20);
                                if(control.flagReceive){ 
                                    //c = c+1;
                                    //System.out.println("Loop receive, counter = " + c);
                                    String inni = in.readLine();
                                    hh = control.pyZ;
                                    control.pyZ = inni;
                                    System.out.println(inni);
                                    control.flagReceive = false;                            
                                    control.flagRead = true;
                                }
                            }

                        }
                    } catch (IOException e) {
                        System.out.println("Could not read socket");
                        //e.printStackTrace();
                    }        

                }
            });
            
        sent.start();
//        try {
//            sent.join();
//        } catch (InterruptedException e) {
//            System.out.println("cannot sent in socketSentRec");
//        }        
        receive.start();        
//        try {
//            receive.join();
//        } catch (InterruptedException e) {
//            System.out.println("cannot receive in socketSentRec");
//        }
    }
    
    public void socketSent(){
        synchronized(control){control.flagSent=true;}
    }
 
    public double defineAFFocus() throws InterruptedException {
        String hh = "0";
        synchronized(control){
            control.flagSent=true;
            boolean cc = true;
            
            while(cc){
                if(control.flagRead){
                    hh = control.pyZ;
                    //afFoc = Double.parseDouble(control.pyZ);
                    cc = false;
                    control.flagRead = false;
                }
            }
            
            
        }
        boolean co = true;
            while(co){
                if(hh!=control.pyZ){
                    afFoc = Double.parseDouble(control.pyZ);
                    co=false;
                }
            }
        afFoc = Double.parseDouble(control.pyZ);
        return afFoc; 
    }

    public double defineRealFocus() {
        try {
            realFoc = core_.getPosition(zDev);
            return realFoc;
        } catch (Exception ex) {
            System.out.println("Cannot get z position from focusing device");
            return 0;
        }
    }

    public void calib(boolean dia){
        // get some variable and clear old ones
        reList.clear();
        afList.clear();
        long stepsZ = round(2*rangeZ/incZ);
        double af1 = 0;
        double re1 = 0; 
        double currZpreCalib = 9000;
        // set current position to start of scan range
        try{
            currZ = core_.getPosition(zDev);
            currZpreCalib = currZ;
            re1 = currZ-rangeZ-incZ;
            core_.setPosition(zDev, re1);
            //currZ = core_.getPosition(zDev);
        }catch(Exception ex){
                System.out.println("Skipped z device outside loop");
            }
        //swipe thorgh z planes and read txt file
        synchronized(control){control.flagRead=true;}
        for (int i = 0; i <= stepsZ; i++) {

            re1 = re1+incZ;
            reList.add(re1);
            try{
                core_.setPosition(zDev, re1);
                core_.waitForDevice(zDev);
                //currZ = core_.getPosition(zDev);
                currZ = re1;
            }catch(Exception ex){
                System.out.println("Skipped z device in loop");
            }

            boolean cc = true;
            String hh = "0";
            synchronized(control){
                while(cc){
                    if(control.flagRead){
                        control.flagSent = true;
                        control.flagRead = false;
                        cc = false;
                        hh = control.pyZ;
                    }else{
                        System.out.println("wait for message");
                    }    
                }
            }
            boolean co = true;
            while(co){
                if(hh!=control.pyZ){
                    af1 = Double.parseDouble(control.pyZ);
                    co=false;
                }
            }
            afList.add(af1);
        }
        
        //cosmetic remove first point
//        afList.remove(0);
//        reList.remove(0);
        
        System.out.println(afList);
        System.out.println(reList);
        
        // prepare data for diagram
        int l = afList.size();
        double[][] a = new double[l][2];
        for (int ii = 0; ii < l-1; ii++) {  
            a[ii][0] = reList.get(ii);
            a[ii][1] = afList.get(ii);
        }
        
        // show diagram
        if(dia){
            final diagram demo = new diagram(a);
            demo.pack();
            RefineryUtilities.centerFrameOnScreen(demo);
            demo.setVisible(true);
        }
        // get lower and upper limit
//        ArrayList<Double> maxList = new ArrayList<Double>();
//        ArrayList<Double> minList = new ArrayList<Double>();
//        for (int ii = 0; ii<6; ii++){
//            double maxVal = max(afList);
//            afList.remove(maxVal);
//            maxList.add(maxVal);
//            double minVal = min(afList);
//            afList.remove(minVal);
//            minList.add(minVal);
//        }
//        
//        // calculate upper and lower limit -> not useful
//        upperLim = round(mean(maxList)*10)/10;
//        lowerLim = round(mean(minList)*10)/10;
//        parent_.setUpperLim(upperLim);
//        parent_.setLowerLim(lowerLim);
//        
        // move to starting position
        try {
            core_.setPosition(zDev, currZpreCalib);
        } catch (Exception ex) {
            Logger.getLogger(AFclass.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void setLowLim(Double text) {
        lowerLim = text;
    }

    void setZrange(Double text) {
        rangeZ = text/1000;
    }

    void setZInc(Double text) {
        incZ = text/1000;
    }

    void setUpLim(Double text) {
        upperLim = text;
    }

    void test() {
        System.out.println(rangeZ);
    }

}
