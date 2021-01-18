package main.java.test;


import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import org.micromanager.MenuPlugin;
import org.micromanager.Studio;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.SciJavaPlugin;

@Plugin(type = MenuPlugin.class)
public class AcqTest implements MenuPlugin, SciJavaPlugin {

	private static Studio studio_;
	
	private static String name = "AcqTest";
	private static String description = "";
	private static String copyright = "";
	private static String version = "";

	@Override
	public String getCopyright() {
		return copyright;
	}

	@Override
	public String getHelpText() {
		return description;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setContext(Studio mmAPI) {
		studio_ = mmAPI;
	}

	@Override
	public String getSubMenu() {
		return "";
	}

	@Override
	public void onPluginSelected() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
			    JFileChooser chooser = new JFileChooser();
			    chooser.setCurrentDirectory(new java.io.File("."));
			    chooser.setDialogTitle("choosertitle");
			    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			    chooser.setAcceptAllFileFilterUsed(false);

			    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
		            File selectedFile = chooser.getSelectedFile();
		            AcqRun r = new AcqRun(studio_, selectedFile.getAbsolutePath());
		            
		            Thread thread = new Thread(r);
		            thread.start();
			    }
			}
		});
	}

	@Override
	public String getVersion() {
		return version;
	}

}