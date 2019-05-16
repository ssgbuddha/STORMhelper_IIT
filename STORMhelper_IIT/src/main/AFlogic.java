/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import org.apache.commons.math.ArgumentOutsideDomainException;
import org.apache.commons.math.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math.analysis.polynomials.PolynomialSplineFunction;



/**
 *
 * @author jonat
 */
public class AFlogic {
    
    ArrayList<Double> z_pos_list  = new ArrayList<Double>();
    ArrayList<Double> rad_list  = new ArrayList<Double>();
    LinearInterpolator interp= new LinearInterpolator();
    
    
    
    public void read_file(String path) throws FileNotFoundException {
    Scanner sc=new Scanner(new FileReader(path));
    while (sc.hasNextLine()){
        String line = sc.nextLine();
        String[] values = line.split("\t");
        z_pos_list.add(Double.parseDouble(values[1]));
        rad_list.add(Double.parseDouble(values[0]));       
        //System.out.println(str);
        //System.out.println(sc.nextLine());
    }
 
}

    public void interpolate(ArrayList x, ArrayList y) throws ArgumentOutsideDomainException{        
        ArrayList intermediate_x  = new ArrayList();
        ArrayList intermediate_y  = new ArrayList();
        if(Math.round(100*Math.abs(Double.parseDouble(x.get(0).toString()) - Double.parseDouble(x.get(1).toString())))!=1){
        double increments = 100*Math.abs(Double.parseDouble(x.get(0).toString()) - Double.parseDouble(x.get(1).toString()));
        System.out.println("increment = " + increments);
        for(int i=0; i<=x.size()-2; i++){

            //System.out.println(x_array[i].getClass().getSimpleName());
            double first_x = Double.parseDouble(x.get(i).toString());
            double next_x = Double.parseDouble(x.get(i+1).toString());
            double first_y = Double.parseDouble(y.get(i).toString());
            double next_y = Double.parseDouble(y.get(i+1).toString());
            if(Math.signum(first_y)*Math.signum(next_y) >0){
            double[] interp_x_points  = {first_x, next_x};
            double[] interp_y_points = {first_y, next_y};
            double gradient = (next_y - first_y)/(next_x - first_x);
            double intercept = first_y - gradient * first_x;
            for(int j=0; j<=increments-1; j++){
                double x_new  = Math.round((first_x+(next_x - first_x)*(1/increments)*j)* 100.0) / 100.0;
                intermediate_x.add(x_new);          
                //double y_new = estimatefunc.value(x_new);
                double y_new = gradient*x_new + intercept;
                //System.out.println(y_new);
                //System.out.println(x_new);
                intermediate_y.add(y_new);
            
                }
            intermediate_x.add(next_x);
            intermediate_y.add(next_y);
            
            }
            else{
                double first_xx = Double.parseDouble(x.get(i-1).toString());
                double next_xx = Double.parseDouble(x.get(i).toString());
                double first_yy = Double.parseDouble(y.get(i-1).toString());
                double next_yy = Double.parseDouble(y.get(i).toString());
                double[] interp_xx_points  = {first_xx, next_xx};
                double[] interp_yy_points = {first_yy, next_yy};
                double gradient = (next_yy - first_yy)/(next_xx - first_xx);
                double intercept = first_yy - gradient * first_xx;
                double fake_next_x = first_x+(increments/100);
                for(int j=1; j<=increments; j++){
                double x_new  = Math.round((first_x+(fake_next_x - first_x)*(1/increments)*j)* 100.0) / 100.0;
                intermediate_x.add(x_new);          
                //double y_new = estimatefunc.value(x_new);
                double y_new = gradient*x_new + intercept;
                //System.out.println(y_new);
                //System.out.println(x_new);
                intermediate_y.add(y_new);
                }
                
                
                double first_xxx = Double.parseDouble(x.get(i+1).toString());
                double next_xxx = Double.parseDouble(x.get(i+2).toString());
                double first_yyy = Double.parseDouble(y.get(i+1).toString());
                double next_yyy = Double.parseDouble(y.get(i+2).toString());
                double[] interp_xxx_points  = {first_xxx, next_xxx};
                double[] interp_yyy_points = {first_yyy, next_yyy};
                double gradient2 = (next_yyy - first_yyy)/(next_xxx - first_xxx);
                double intercept2 = first_yyy - gradient2 * first_xxx;
                double fake_first_x = next_x-(increments/100);
                for(int k=0; k<=increments-1; k++){
                double x_new2  = Math.round((fake_first_x+(next_x - fake_first_x)*(1/increments)*k)* 100.0) / 100.0;
                intermediate_x.add(x_new2);          
                //double y_new = estimatefunc.value(x_new);
                double y_new2 = gradient2*x_new2 + intercept2;
                //System.out.println(y_new);
                //System.out.println(x_new);
                intermediate_y.add(y_new2);
                

            }
            }
                
                  
            
            
            
        //intermediate_x.add(next_x);
        //intermediate_y.add(next_y);
            
            
            
            }
        
        //System.out.println(intermediate_x);
        //System.out.println(intermediate_y);
        z_pos_list = intermediate_x;
        rad_list = intermediate_y;
        }
        else{
            z_pos_list = x;
            rad_list = y;
            
        }
        }
        //PolynomialSplineFunction estimatefunc = interp.interpolate(x, y);
        //System.out.println(estimatefunc.value());
      
    
    public void check_lists(){
    System.out.println("Z list = " + z_pos_list);
    System.out.println("Radius list = " + rad_list);  
}
    
    public String look_up_defocus(double target_value){
        double closest = Double.parseDouble(rad_list.get(0).toString());
        int closest_index = 0;
        //System.out.println("hi");
        //System.out.println(closest_index);
        
        for(int i = 1; i<=rad_list.size()-1; i++){
            //System.out.println("target value =" + target_value);
            //System.out.println("Radius = " +Double.parseDouble(rad_list.get(i).toString())); 
            //System.out.println("closest = " + Math.abs(closest));
            //System.out.println("closest difference = " + Math.abs(closest-target_value));
            //System.out.println("current difference = " + Math.abs(Double.parseDouble(rad_list.get(i).toString()) - target_value));
            if (Math.abs(closest - target_value) > Math.abs(Double.parseDouble(rad_list.get(i).toString()) - target_value)){
            closest_index = i;
            closest = Double.parseDouble(rad_list.get(i).toString());
            }
            
            //System.out.println(Double.parseDouble(rad_list.get(closest_index).toString())); 
        }
        //System.out.println(closest_index);
        String current_position = z_pos_list.get(closest_index).toString();
        //System.out.println(current_position);
        return current_position;

        //System.out.println(Double.parseDouble(rad_list.get(closest_index).toString())); 
}
}
