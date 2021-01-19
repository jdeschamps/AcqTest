package main.java.test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.micromanager.Studio;
import org.micromanager.acquisition.AcquisitionManager;
import org.micromanager.acquisition.SequenceSettings;
import org.micromanager.acquisition.SequenceSettings.Builder;
import org.micromanager.data.Datastore;


public class AcqRun implements Runnable{

	public static String TIME = "_time";
	public static String SNAP = "_snap";
	public static String ZSTACK = "_zstack";
	public static String NAME = "exp_test_";
	
	private String path;
	private Studio studio;
	
	public AcqRun(Studio studio, String path) {
		this.path = path;
		this.studio = studio;
	}
	
	@Override
	public void run() {
		String timeSuffix = new SimpleDateFormat("mmss").format(new Date());
		
		runTime(timeSuffix);
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		runZStack(timeSuffix);
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		runSnap(timeSuffix);
	}

	
	private void runSnap(String timeSuffix) {
		Builder seqBuilder = new SequenceSettings.Builder();
		seqBuilder.save(true);
		seqBuilder.timeFirst(true);
		seqBuilder.root(path);
		seqBuilder.prefix(NAME+timeSuffix+SNAP);
		seqBuilder.numFrames(1);
		seqBuilder.intervalMs(0);
		seqBuilder.shouldDisplayImages(true);
		seqBuilder.useAutofocus(false);
		seqBuilder.useChannels(false);
		seqBuilder.useCustomIntervals(false);
		seqBuilder.useFrames(true);
		seqBuilder.usePositionList(false);
		seqBuilder.useSlices(false);
		
		// run acquisition
		AcquisitionManager acqManager = studio.acquisitions();
		acqManager.setAcquisitionSettings(seqBuilder.build());
		Datastore store = acqManager.runAcquisition();
		
		// to mimic what we have in the other plugin for user interruption
		while(studio.acquisitions().isAcquisitionRunning()) {		
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		studio.displays().closeDisplaysFor(store);

		try {
			store.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	private void runTime(String timeSuffix) {
		Builder seqBuilder = new SequenceSettings.Builder();
		seqBuilder.save(true);
		seqBuilder.timeFirst(true);
		seqBuilder.root(path);
		seqBuilder.prefix(NAME+timeSuffix+TIME);
		seqBuilder.numFrames(150);
		seqBuilder.intervalMs(0);
		seqBuilder.shouldDisplayImages(true);
		seqBuilder.useAutofocus(false);
		seqBuilder.useChannels(false);
		seqBuilder.useCustomIntervals(false);
		seqBuilder.useFrames(true);
		seqBuilder.usePositionList(false);
		seqBuilder.useSlices(false);
		
		// runs acquisition
		AcquisitionManager acqManager = studio.acquisitions();		
		acqManager.setAcquisitionSettings(seqBuilder.build());
		Datastore store = acqManager.runAcquisitionNonblocking();

		// to mimic what we have in the other plugin for user interruption
		while(studio.acquisitions().isAcquisitionRunning()) {		
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		studio.displays().closeDisplaysFor(store);

		try {
			store.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	private void runZStack(String timeSuffix) {
		Builder seqBuilder = new SequenceSettings.Builder();
		seqBuilder.save(true);
		seqBuilder.slicesFirst(true);
		seqBuilder.root(path);
		seqBuilder.prefix(NAME+timeSuffix+ZSTACK);
		seqBuilder.numFrames(1);
		seqBuilder.intervalMs(0);
		seqBuilder.shouldDisplayImages(true);
		seqBuilder.sliceZBottomUm(-2.);
		seqBuilder.sliceZStepUm(1.);
		seqBuilder.sliceZTopUm(2.);
		seqBuilder.relativeZSlice(true);
		seqBuilder.useAutofocus(false);
		seqBuilder.useChannels(false);
		seqBuilder.useCustomIntervals(false);
		seqBuilder.useFrames(false);
		seqBuilder.usePositionList(false);
		seqBuilder.useSlices(true);
		
		// run acquisition
		AcquisitionManager acqManager = studio.acquisitions();
		acqManager.setAcquisitionSettings(seqBuilder.build());
		Datastore store = acqManager.runAcquisitionNonblocking();

		
		// to mimic what we have in the other plugin for user interruption
		while(studio.acquisitions().isAcquisitionRunning()) {		
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		studio.displays().closeDisplaysFor(store);
		
		try {
			store.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
