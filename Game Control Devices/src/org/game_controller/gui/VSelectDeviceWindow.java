package org.game_controller.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.game_controller.Configuration;
import org.game_controller.ControlDevice;
import org.game_controller.ControlIO;

import processing.core.PApplet;
import processing.event.MouseEvent;

public class VSelectDeviceWindow {

	
	MWindow window;
	
	PApplet app;
	ControlIO controlIO;

	String filename;
	Configuration config;
	List<VDeviceSelectEntry> deviceEntries =  new ArrayList<VDeviceSelectEntry>();
	
	
	public VSelectDeviceWindow(PApplet app, String configFilename){
		M4P.messagesEnabled(false);
		this.app = app;
		filename = configFilename;
		config = Configuration.makeConfiguration(app, filename);
		this.controlIO = ControlIO.getInstance(app);
		window = new MWindow(app, "Select device for " + config.usage, 80, 40, 500, 400, false, PApplet.JAVA2D);
		window.setResizable(false);
		window.addDrawHandler(this, "draw");
		window.addMouseHandler(this, "mouse");
		
		createSelectionInterface(window.papplet);

		List<ControlDevice> devices = controlIO.getDevices();
		// Add entries for devices added
		for(ControlDevice d : devices){
			if(d.available && !d.getTypeName().equalsIgnoreCase("keyboard"))
				deviceEntries.add(new VDeviceSelectEntry(window.papplet, controlIO, d, config));
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
