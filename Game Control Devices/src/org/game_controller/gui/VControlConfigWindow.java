package org.game_controller.gui;

import java.awt.Font;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.game_controller.Configuration;
import org.game_controller.ControlButton;
import org.game_controller.ControlCoolieHat;
import org.game_controller.ControlDevice;
import org.game_controller.ControlIO;
import org.game_controller.ControlInput;
import org.game_controller.ControlSlider;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.event.MouseEvent;

public class VControlConfigWindow implements PConstants, VConstants {

	static int nbrWindows = 0;

	private final ControlDevice device;

	final ControlIO controlIO;

	final Configuration config;
	
	private boolean active = false;

	List<VBase> uiElements = new ArrayList<VBase>();
	List<VConnector> uiConnections = new ArrayList<VConnector>();

	VConnector start = null;
	VConnector end = null;
	VConnector current = null;

	float scale;
	final float input_UI_height;
	final float desc_UI_height;
	final float element_UI_gap;
	final float input_UI_length;
	final float desc_UI_length;
	final float textfield_gap;
	final float indicator_d;
	final float connector_size_r;  // radius
	final float connector_size_d;  // diameter
	final float fontSize;
	final Font font;

	private StringBuffer report;
	List<VConnector> configConnections = new ArrayList<VConnector>();
	private Set<String> keys = new TreeSet<String>();
	private int errCount = 0;

	private boolean dragging = false;

	private void addToReport(String line, boolean isError){
		report.append(line);
		if(isError) errCount++;
	}

	/**
	 * This method will check for any errors / omissions in all
	 * used descriptors.
	 */
	private void validateDescriptors(){
		// Create a list of used descriptors
		for(VConnector ui : uiConnections){
			if(ui.type == VConnector.DESC && ui.conTo != null){
				configConnections.add(ui);
			}
		}
		if(configConnections.size() == 0){
			addToReport("Empty configuration!\n", true);
			return;
		}
		// We only get here if we have some connections configured
		for(VConnector ui : configConnections){
			VDescriptor descUI = (VDescriptor)ui.owner;
			String inputName = ((VBaseInput)ui.conTo.owner).name;
			String desc = descUI.iconfig.description;
			if(desc.length() == 0)
				addToReport("No description for input: " + inputName + "\n", true);
		}
	}

	/**
	 * Verify the configuration
	 * @param chain
	 * @return
	 */
	private boolean verifyConfig(boolean chain){
		configConnections.clear();
		keys.clear();
		report = new StringBuffer();
		errCount = 0;

		validateDescriptors();

		if(errCount > 0)
			addToReport("VERIFY - " + errCount + " errors found\n", false);
		else
			addToReport("VERIFY - successful\n", false);
		if(!chain)
			txaStatus.setText(report.toString());
		return errCount == 0;
	}

	private void saveConfig(){
		if(!verifyConfig(true)){
			addToReport("SAVE - abandoned\n", false);
			txaStatus.setText(report.toString());
			return;
		}
		String filename = txfFilename.getText();
		if(filename.length() == 0){
			addToReport("Name for configuration file required\n", true);
		}
		else {
			if(!filename.endsWith(".c_config"))
				filename += ".c_config";
			//==================================================================================================
			// Will eventually need to use sketch data path
			//==================================================================================================
			File file = new File(filename);
			String[] lines = makeConfigLines();
			PApplet.saveStrings(file, lines);
		}
		if(errCount > 0)
			addToReport("SAVE - failed\n", false);
		else
			addToReport("SAVE - successful", false);
		txaStatus.setText(report.toString());
	}

	private String[] makeConfigLines() {
		String[] data = new String[configConnections.size()];
		int index = 0;
		for(VConnector ui : configConnections){
			VDescriptor descUI = (VDescriptor)ui.owner;
			VBaseInput inputUI = (VBaseInput)ui.conTo.owner;
			String desc = descUI.iconfig.description;
			String key = descUI.iconfig.key;
			String inputName = inputUI.name;
			int typeID = inputUI.uiType;
			float multiplier = inputUI.getMultiplier();
			float tolerance = inputUI.getTolerance();
			String type = inputUI.inputTypeName;
			data[index] = key + SEPARATOR + desc + SEPARATOR + typeID + SEPARATOR + type;
			data[index] += SEPARATOR + inputName + SEPARATOR + multiplier + SEPARATOR + tolerance;
			index++;
		}
		return data;
	}

	public void verify_click(MButton button, MEvent event) { 
		verifyConfig(false);
	}


	public void save_click(MButton button, MEvent event) { 
		saveConfig();
	}


	public void clear_click(MButton button, MEvent event) {
		txaStatus.setText("");
	}

	synchronized public void pre(MWinApplet appc, MWinData data) {
		current = null;
		for(VBase ui : uiElements){
			ui.update();
			ui.overWhat(appc.mouseX, appc.mouseY);
		}
		if(!dragging && current != null && current.conTo != null){
			current.conTo.isOver = true;
		}
	}

	synchronized public void mouse(MWinApplet appc, MWinData data, MouseEvent mevent) {
		switch(mevent.getAction()){
		case MouseEvent.PRESS:
			if(current != null){
				start = current;
				dragging = true;
			}
			break;
		case MouseEvent.RELEASE:
			if(current != null && start != null && current.type != start.type){
				end = current;
				current = null;
				dragging = false;
				if(start.conTo != null)
					start.conTo.conTo = null;
				if(end.conTo != null)
					end.conTo.conTo = null;
				start.conTo = end;
				end.conTo = start;			
			}
			break;
		case MouseEvent.DRAG:

			break;
		}
	}

	synchronized public void draw(MWinApplet appc, MWinData data) {
		appc.background(BACKGROUND);
		if(!active) return;
		// Draw control panel at bottom
		appc.noStroke();
		appc.fill(PANEL);;
		appc.rect(appc.width - PANEL_WIDTH, 0, PANEL_WIDTH, appc.height);
		// Draw connections
		appc.strokeWeight(3.5f);
		for(VConnector c : uiConnections){
			if(c.conTo != null && c.type == VConnector.DESC){
				appc.stroke(c.isOver ? HIGHLIGHT : CONNECTION);
				appc.line(c.px,  c.py,  c.conTo.px,  c.conTo.py);
			}
		}
		// Connection in the making
		if(dragging && start != null){
			appc.stroke(CONNECTION);
			appc.line(start.px, start.py, appc.mouseX, appc.mouseY);
		}
		// Draw descriptors and inputs
		for(VBase ui : uiElements)
			ui.draw();
	}

	public void printDevice(int id,  ControlDevice device){
		System.out.println("========================================================================");
		System.out.println("Device number  " + id + " is called '" + device.getName() + "' and has");
		System.out.println("\t" + device.getNumberOfButtons() + " buttons");
		System.out.println("\t" + device.getNumberOfSliders() + " sliders");
		System.out.println("\t" + device.getNumberOfRumblers() + " rumblers");
		device.printButtons();
		device.printSliders();
		System.out.println("------------------------------------------------------------------------\n\n");		
	}

	// Widow GUI stuff
	MWindow window;
	MTabManager tabManager = new MTabManager();
	MTextField txfFilename;
	MTextArea txaStatus;

	public VControlConfigWindow(PApplet papp, VDeviceSelectEntry entry){
		float px, py, pw;
		device = entry.device;
		entry.device.open();
		controlIO = entry.controlIO;
		this.config = entry.config;
		float spaceForInputs = ELEMENT_UI_GAP;

		// Scan through controls to calculate the window height needed
		for(ControlInput input : device.getInputs()){
			if(input instanceof ControlCoolieHat){
				spaceForInputs += 5 * INPUT_UI_HEIGHT + ELEMENT_UI_GAP + 2;
			}
			else  if(input instanceof ControlButton){
				spaceForInputs += INPUT_UI_HEIGHT + ELEMENT_UI_GAP + 2;
			}
			else  if(input instanceof ControlSlider){
				spaceForInputs += 4 * INPUT_UI_HEIGHT + ELEMENT_UI_GAP + 2;
			}
			else
				System.out.println("Unknown input " + input);	
		}
		float spaceForDescs = config.nbrInputs() * (DESC_UI_HEIGHT + ELEMENT_UI_GAP + 2);
		float spaceNeeded = Math.max(spaceForInputs, spaceForDescs);
		spaceNeeded = Math.max(spaceNeeded, PANEL_HEIGHT);
		// Now calculate window scaling and height
		if(papp.displayHeight < spaceNeeded + 40)
			scale = papp.displayHeight / (spaceNeeded + 40);	
		else
			scale = 1.0f;
		int winHeight = Math.round(spaceNeeded  * scale);

		// Apply scaling
		input_UI_height = INPUT_UI_HEIGHT * scale;
		desc_UI_height = DESC_UI_HEIGHT * scale;
		element_UI_gap  = ELEMENT_UI_GAP * scale;
		input_UI_length = INPUT_UI_LENGTH;
		desc_UI_length = DESC_UI_LENGTH;
		textfield_gap = TEXTFIELD_GAP * scale;
		indicator_d = INICATOR_D * scale;
		connector_size_r = CONNECTOR_SIZE_R * scale;		
		connector_size_d = 2 * connector_size_r;
		fontSize = FONT_SIZE * scale;
		font = new Font("Dialog", Font.PLAIN, (int)fontSize);

		// CREATE THE WINDOW
		String title = "'" + device.getName() + "'  [" + device.getTypeName() + " on " + device.getPortTypeName() + "]"; 
		window = new MWindow(papp, title, 80 + nbrWindows * 40, 100 + nbrWindows * 30, 1020, winHeight, false, M4P.JAVA2D);
		window.setResizable(false);
		window.addDrawHandler(this, "draw");
		window.addMouseHandler(this, "mouse");
		window.addPreHandler(this, "pre");
		window.papplet.noLoop();
		tabManager = new MTabManager();
		M4P.setCursor(CROSS, window);	
		nbrWindows++;

		// Create the control panel
		px = window.papplet.width - PANEL_WIDTH + 10;
		pw = PANEL_WIDTH - 20;
		py = 10;
		MLabel lblFilenamePrompt = new MLabel(window.papplet, px, py, pw, 20, "Filename for this configuration");
		lblFilenamePrompt.setTextAlign(MAlign.LEFT, null);
		lblFilenamePrompt.setLocalColorScheme(M4P.GREEN_SCHEME);
		lblFilenamePrompt.setTextBold();
		lblFilenamePrompt.setOpaque(true);
		py += 22;
		txfFilename = new MTextField(window.papplet, px, py, pw, 20);
		txfFilename.setLocalColorScheme(M4P.GREEN_SCHEME);
		txfFilename.setDefaultText("Enter a filename for this configuration");
		py += 22;
		MButton btnVerify = new MButton(window.papplet, px, py, (pw-10)/2, 20);
		btnVerify.setLocalColorScheme(M4P.GREEN_SCHEME);
		btnVerify.setText("Verify");
		btnVerify.addEventHandler(this, "verify_click");
		MButton btnSave = new MButton(window.papplet, px + pw / 2 + 5, py, (pw-10)/2, 20);
		btnSave.setLocalColorScheme(M4P.GREEN_SCHEME);
		btnSave.setText("Save");
		btnSave.addEventHandler(this, "save_click");
		py += 26;
		MLabel lblStatus = new MLabel(window.papplet, px, py, pw, 20, "VERIFY / SAVE STUS REPORT");
		lblStatus.setLocalColorScheme(M4P.GREEN_SCHEME);
		lblStatus.setTextBold();
		lblStatus.setOpaque(true);
		py += 22;
		txaStatus = new MTextArea(window.papplet, px, py, pw, 140, M4P.SCROLLBARS_VERTICAL_ONLY);
		txaStatus.setLocalColorScheme(M4P.GREEN_SCHEME);
		txaStatus.setDefaultText("Verify / save status report");
		py += txaStatus.getHeight() + 2;
		MButton btnClearStatus = new MButton(window.papplet, px, py, (pw-10)/2, 20);
		btnClearStatus.setLocalColorScheme(M4P.GREEN_SCHEME);
		btnClearStatus.setText("Clear Status");
		btnClearStatus.addEventHandler(this, "clear_click");

		nbrWindows++;
		// Create and add inputs to UI 
		window.papplet.textSize(fontSize);
		px = window.papplet.width - 10 - INPUT_UI_LENGTH - PANEL_WIDTH;
		py = ELEMENT_UI_GAP + (spaceNeeded - spaceForInputs) / 2; 
		for(ControlInput input : device.getInputs()){
			VBaseInput ui = VBaseInput.makeInputUI(this, input, px, py);
			if(ui != null){
				uiElements.add(ui);
				py += ui.UI_HEIGHT + ELEMENT_UI_GAP;
			}
		}
		// Create and add descriptors to UI 
		px = 10;
		py = ELEMENT_UI_GAP + (spaceNeeded - spaceForDescs) / 2; 
		for(Configuration.InputConfig iconfig : config.gameInputs){
			VDescriptor ui = new VDescriptor(this, px, py, iconfig);
			uiElements.add(ui);
			py += ui.UI_HEIGHT + ELEMENT_UI_GAP;
		}
		// Now create list of connectors
		for(VBase ui : uiElements)
			for(VConnector c : ui.connectors)
				uiConnections.add(c);
		active = true;
		window.papplet.loop();
	}

	void close(){
		window.forceClose();
	}
}