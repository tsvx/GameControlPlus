package org.game_controller.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.game_controller.Configuration;
import org.game_controller.ControlDevice;
import org.game_controller.ControlIO;

import processing.core.PApplet;
import processing.event.MouseEvent;


/**
 * This creates the device select entry window.
 * @author peter
 *
 */
public class LSelectDeviceWindow {

	public static Configuration config;
	public static String filename;
	
	MWindow window;
	
	PApplet app;
	ControlIO controlIO;

	List<LDeviceSelectEntry> deviceEntries =  new ArrayList<LDeviceSelectEntry>();
	
	
	public LSelectDeviceWindow(PApplet app, String configFilename){
		M4P.messagesEnabled(false);
		this.app = app;
		filename = configFilename;
		LSelectDeviceWindow.config = Configuration.makeConfiguration(app, filename);
		String title = "Select device for " + LSelectDeviceWindow.config.usage;
		this.controlIO = ControlIO.getInstance(app);
		window = new MWindow(app, title, 80, 40, 500, 400, false, PApplet.JAVA2D);
		window.setResizable(false);
		window.addDrawHandler(this, "draw");
		window.addMouseHandler(this, "mouse");
		
		createSelectionInterface(window.papplet);

		List<ControlDevice> devices = controlIO.getDevices();
		// Add entries for devices added
		for(ControlDevice d : devices){
			if(d.available && !d.getTypeName().equalsIgnoreCase("keyboard"))
				deviceEntries.add(new LDeviceSelectEntry(window.papplet, controlIO, d));
		}
		// Sort entries and reposition on screen
		Collections.sort(deviceEntries);
		for(int i = 0; i < deviceEntries.size(); i++)
			deviceEntries.get(i).setIndex(i);

	}
	public void createSelectionInterface(PApplet wapp){
		MLabel lblControls = new MLabel(wapp, 0, 0, wapp.width, 20);
		lblControls.setText("Control devices");
		lblControls.setOpaque(true);
		lblControls.setTextBold();
	}
	
	synchronized public void mouse(MWinApplet appc, MWinData data, MouseEvent mevent) {
	}

	synchronized public void draw(MWinApplet appc, MWinData data) {
		appc.background(255, 255, 220);
		appc.stroke(230, 230, 200);
		appc.fill(240, 240, 210);
		int y =0;
		while(y < appc.height){
			appc.rect(0,y,appc.width,20);
			y += 40;
		}
	}
}
