package main.java.test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.micromanager.Studio;
import org.micromanager.acquisition.AcquisitionManager;
import org.micromanager.acquisition.SequenceSettings;
//import org.micromanager.acquisition.SequenceSettings.Builder;
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
		/*Builder seqBuilder = new SequenceSettings.Builder();
		seqBuilder.save(true);
		seqBuilder.timeFirst(true);
		seqBuilder.usePositionList(false);
		seqBuilder.root(path);
		seqBuilder.prefix(NAME+timeSuffix+SNAP);
		seqBuilder.numFrames(1);
		seqBuilder.intervalMs(0);
		seqBuilder.shouldDisplayImages(true);
		seqBuilder.useFrames(true);
		*/
		
		SequenceSettings seq = new SequenceSettings();
		seq.save = true;
		seq.timeFirst = true;
		seq.usePositionList = false;
		seq.root = path;
		seq.prefix = NAME+timeSuffix+SNAP;
		seq.numFrames = 1;
		seq.intervalMs = 0;
		seq.shouldDisplayImages = true;
		
		// run acquisition
		AcquisitionManager acqManager = studio.acquisitions();
		acqManager.setAcquisitionSettings(seq);
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
		/*Builder seqBuilder = new SequenceSettings.Builder();
		seqBuilder.save(true);
		seqBuilder.timeFirst(true);
		seqBuilder.usePositionList(false);
		seqBuilder.root(path);
		seqBuilder.prefix(NAME+timeSuffix+TIME);
		seqBuilder.numFrames(150);
		seqBuilder.intervalMs(0);
		seqBuilder.shouldDisplayImages(true);
		seqBuilder.useFrames(true);*/
		
		SequenceSettings seq = new SequenceSettings();
		seq.save = true;
		seq.timeFirst = true;
		seq.usePositionList = false;
		seq.root = path;
		seq.prefix = NAME+timeSuffix+TIME;
		seq.numFrames = 150;
		seq.intervalMs = 0;
		seq.shouldDisplayImages = true;
		
		// runs acquisition
		AcquisitionManager acqManager = studio.acquisitions();		
		acqManager.setAcquisitionSettings(seq);
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
		/*Builder seqBuilder = new SequenceSettings.Builder();
		seqBuilder.save(true);
		seqBuilder.slicesFirst(true);
		seqBuilder.usePositionList(false);
		seqBuilder.root(path);
		seqBuilder.prefix(NAME+timeSuffix+ZSTACK);
		seqBuilder.numFrames(1);
		seqBuilder.intervalMs(0);
		seqBuilder.shouldDisplayImages(true);
		seqBuilder.useSlices(true);
		seqBuilder.relativeZSlice(true);
		seqBuilder.sliceZBottomUm(-1.);
		seqBuilder.sliceZStepUm(0.1);
		seqBuilder.sliceZTopUm(1.);*/
		
		SequenceSettings seq = new SequenceSettings();
		seq.save = true;
		seq.timeFirst = false;
		seq.slicesFirst = true;
		seq.usePositionList = false;
		seq.root = path;
		seq.prefix = NAME+timeSuffix+ZSTACK;
		seq.numFrames = 1;
		seq.intervalMs = 0;
		seq.shouldDisplayImages = true;
		
		ArrayList<Double> slices = new ArrayList<Double>();
		for(int i=-2; i<=2; i++) {
			slices.add(new Double(i));
		}
		
		seq.slices = slices;
		
		
		// run acquisition
		AcquisitionManager acqManager = studio.acquisitions();
		acqManager.setAcquisitionSettings(seq);
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
