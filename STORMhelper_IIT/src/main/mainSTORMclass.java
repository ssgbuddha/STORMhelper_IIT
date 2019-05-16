/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import javax.swing.JFrame;
import org.micromanager.MenuPlugin;
import org.micromanager.Studio;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.SciJavaPlugin;


/**
 *
 * @author STORM
 */
@Plugin(type = MenuPlugin.class)
public class mainSTORMclass implements MenuPlugin, SciJavaPlugin{
    //Name for the plugin
    public static final String menuName = "STORMhelper";
    private Studio gui_;    
    public static JFrame frame_;
    
       @Override
    public String getSubMenu() {
        //Think this lets you determine which plugin submenu this ends up in
        return("Acquisition Tools");
    }

    @Override
    public void onPluginSelected() {
        frame_ = new mainSTORM_frame(gui_);
        frame_.setVisible(true);
    }

    @Override
    public void setContext(Studio studio) {
        gui_ = studio;
    }

    @Override
    public String getName() {
        return "STORMhelper";
    }

    @Override
    public String getHelpText() {
        return("Sorry, not much help to be had here yet...");
    }

    @Override
    public String getVersion() {
        return("0.0.1");
    }

    @Override
    public String getCopyright() {
        return("Copyright Imperial College London [2018]");
    }
    
}
