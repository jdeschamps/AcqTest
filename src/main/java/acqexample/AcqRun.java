package main.java.acqexample;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import mmcorej.Configuration;
import mmcorej.PropertySetting;
import org.micromanager.PropertyMap;
import org.micromanager.PropertyMaps;
import org.micromanager.Studio;
import org.micromanager.acquisition.AcquisitionManager;
import org.micromanager.acquisition.SequenceSettings;
import org.micromanager.acquisition.SequenceSettings.Builder;
import org.micromanager.data.*;
import org.micromanager.display.DisplayWindow;

import mmcorej.CMMCore;
import mmcorej.TaggedImage;
import org.micromanager.internal.MMStudio;


public class AcqRun implements Runnable{

	public static String TIME = "_time";
	public static String SNAP = "_snap";
	public static String ZSTACK = "_zstack";
	public static String STREAM = "_stream";
	public static String NAME = "Acq_test_";
	
	private String path;
	private Studio studio;
	
	public AcqRun(Studio studio, String path) {
		this.path = path;
		this.studio = studio;
	}
	
	@Override
	public void run() {
		//runMultipleAcq();

		runMultipleMetadata();
	}

	private void runMultipleMetadata() {
		String timeSuffix = new SimpleDateFormat("mmss").format(new Date());

		//runStreamMode(timeSuffix, MetadataApproach.SIMPLE);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		runStreamMode(timeSuffix, MetadataApproach.MANUAL);
	}

	private void runMultipleAcq(){
		String timeSuffix = new SimpleDateFormat("mmss").format(new Date());

		runTime(timeSuffix);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		runStreamMode(timeSuffix, MetadataApproach.MANUAL);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		runZStack(timeSuffix);

		try {
			Thread.sleep(1000);
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
		Datastore store = acqManager.runAcquisitionWithSettings(seqBuilder.build(), false);
		
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
		seqBuilder.numFrames(50);
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
		Datastore store = acqManager.runAcquisitionWithSettings(seqBuilder.build(), false);

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
		seqBuilder.sliceZStepUm(0.5);
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
		Datastore store = acqManager.runAcquisitionWithSettings(seqBuilder.build(), false);

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

	private void runStreamMode(String timeSuffix, MetadataApproach metadataMethod) {
		// creates store
		Datastore store;
		try {
			Map<String, String> propertyMap = new HashMap<>();

			String name = NAME+timeSuffix+STREAM+"_"+metadataMethod.n;
			String savePath  = path+File.separator+name;
			store = studio.data().createMultipageTIFFDatastore(savePath, true, false);

			// system metadata
			int numImages = 50;
			store.setSummaryMetadata(generateSummaryMetadata(studio, path, name, numImages));

			// display and coordinate builder
			DisplayWindow display = studio.displays().createDisplay(store);
			Coords.Builder cb = studio.data().getCoordsBuilder().z(0).c(0).p(0).t(0);

			CMMCore core = studio.core();
			AcquisitionManager acqManager = studio.getAcquisitionManager();
			int curFrame = 0;
			try {
				core.startSequenceAcquisition(numImages, 0, true);
				Metadata metadata = studio.data().getMetadataBuilder().build();

				try {
					while ((core.getRemainingImageCount() > 0 || core.isSequenceRunning(core.getCameraDevice()))) {
						if (core.getRemainingImageCount() > 0) {
							TaggedImage tagged = core.popNextTaggedImage();

							// Convert to an Image at the desired time point
							Image image;
							switch(metadataMethod){
								case DEFAULT:
									image = studio.data().convertTaggedImage(tagged);
									store.putImage(image.copyAtCoords(cb.time(curFrame).build()));
									store.putImage(image);

								case MANUAL:
									image = studio.data().convertTaggedImage(tagged, cb.time(curFrame).build(), generateMetadata(studio, metadata));
									store.putImage(image);
							}

							curFrame++;
						} else {
							core.sleep(5);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			// close store
			store.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private enum MetadataApproach{
		DEFAULT(0),
		MANUAL(1);

		private int n;

		MetadataApproach(int n){
			this.n = n;
		}
	}

	private SummaryMetadata generateSummaryMetadata(Studio studio, String path, String name, int n){//Map<String, Object> properties){

		SummaryMetadata defaultSM = studio.acquisitions().generateSummaryMetadata();
		SummaryMetadata.Builder smBuilder = defaultSM.copyBuilder();

		// stacks dimensions
		Coords coords = defaultSM.getIntendedDimensions();
		smBuilder.intendedDimensions(coords.copyBuilder().t(n).c(1).p(1).z(1).build());

		// others
		smBuilder.prefix(name);
		smBuilder.directory(path);

		return smBuilder.build();
	}

	/*
	 * adapted from DefaultAcquisitionManager
	 */
	private Metadata generateMetadata(Studio studio, Metadata metadata) throws Exception {
		String camera = studio.core().getCameraDevice();

		MMStudio mmstudio = (MMStudio) studio;
		Metadata.Builder result = metadata.copyBuilderWithNewUUID()
				.camera(camera)
				.receivedTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z").format(new Date()))
				.pixelSizeUm(mmstudio.cache().getPixelSizeUm())
				.pixelSizeAffine(mmstudio.cache().getPixelSizeAffine())
				.xPositionUm(mmstudio.cache().getStageX())
				.yPositionUm(mmstudio.cache().getStageY())
				.zPositionUm(mmstudio.cache().getStageZ())
				.bitDepth(mmstudio.cache().getImageBitDepth());

		try {
			String binning = studio.core().getPropertyFromCache(camera, "Binning");
			if (binning.contains("x")) {
				// HACK: assume the binning parameter is e.g. "1x1" or "2x2" and
				// just take the first number.
				try {
					result.binning(Integer.parseInt(binning.split("x", 2)[0]));
				}
				catch (NumberFormatException e) {
					studio.logs().logError("Unable to determine binning from " + binning);
				}
			}
			else {
				try {
					result.binning(Integer.parseInt(binning));
				}
				catch (NumberFormatException e) {
					studio.logs().logError("Unable to determine binning from " + binning);
				}
			}
		}
		catch (Exception ignored) {
			// Again, this can fail if there is no camera.
		}

		PropertyMap.Builder scopeBuilder = PropertyMaps.builder();
		Configuration config = studio.core().getSystemStateCache();
		for (long i = 0; i < config.size(); ++i) {
			PropertySetting setting = config.getSetting(i);
			// NOTE: this key format chosen to match that used by the current
			// acquisition engine.
			scopeBuilder.putString(setting.getDeviceLabel() + "-" + setting.getPropertyName(),
					setting.getPropertyValue());
		}
		result.scopeData(scopeBuilder.build());

		return result.build();
	}
}
