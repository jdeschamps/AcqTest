package main.java.acqexample;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import mmcorej.Configuration;
import mmcorej.PropertySetting;
import org.micromanager.PropertyMap;
import org.micromanager.PropertyMaps;
import org.micromanager.Studio;
import org.micromanager.acqj.internal.Engine;
import org.micromanager.acqj.main.Acquisition;
import org.micromanager.acqj.main.AcquisitionEvent;
import org.micromanager.acquisition.AcquisitionManager;
import org.micromanager.acquisition.SequenceSettings;
import org.micromanager.acquisition.SequenceSettings.Builder;
import org.micromanager.data.*;
import org.micromanager.display.DisplayWindow;

import mmcorej.CMMCore;
import mmcorej.TaggedImage;
import org.micromanager.internal.MMStudio;

import main.java.acqexample.acqj.SimpleProcessor;
import main.java.acqexample.acqj.SimpleSink;
import main.java.acqexample.acqj.StoreSink;

public class AcqRun implements Runnable
{
	private String path;

	private Studio studio;

	public AcqRun( Studio studio, String path )
	{
		this.path = path;
		this.studio = studio;
	}

	@Override
	public void run()
	{
		runAcqJ();
	}
	
	private void runAcqJ() {
		Engine engine = new Engine(studio.core());
		
		// create sink and acquisition
		SimpleSink sink = new SimpleSink();
		Acquisition acq = new Acquisition(sink);
		
		// create processor
		SimpleProcessor q = new SimpleProcessor();
		acq.addImageProcessor(q);
		
		// create acquisition and start it
		ArrayList<AcquisitionEvent> list = new ArrayList<AcquisitionEvent>();
		for(int i=0; i<15; i++) {
			AcquisitionEvent a = new AcquisitionEvent(acq);
			a.setExposure(50);
			a.setTimeIndex(i);
			list.add(a);
		}
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		acq.submitEventIterator(list.iterator());

		// finish
		acq.finish();
			
		while(!acq.areEventsFinished()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		sink.isFinished();		
	}
}
