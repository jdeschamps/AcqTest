package main.java.acqexample.acqj;

import java.io.File;
import java.io.IOException;

import org.micromanager.Studio;
import org.micromanager.acqj.api.DataSink;
import org.micromanager.acqj.main.Acquisition;
import org.micromanager.data.Coords;
import org.micromanager.data.Datastore;
import org.micromanager.data.Image;
import org.micromanager.data.Metadata;
import org.micromanager.data.SummaryMetadata;
import org.micromanager.display.DisplayWindow;

import mmcorej.TaggedImage;
import mmcorej.org.json.JSONObject;

public class StoreSink implements DataSink {
	
	private boolean finished = false;
	private Datastore store;
	private Studio studio;
	private Metadata metadata;
	private int counter = 0;
	private Coords.Builder cb;
	private DisplayWindow display;
	boolean anything = false;
	String path, name;
	
	public StoreSink(Studio studio, String path, String name)  throws IOException {
		this.studio = studio;
		this.path = path;
		this.name = name;
	}

	@Override
	public boolean anythingAcquired() {
		return anything;
	}

	@Override
	public void finished() {
		display.close();
		try {
			if(store != null) {
				store.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finished = true;
	}

	@Override
	public void initialize(Acquisition arg0, JSONObject arg1) {		
		String savePath = path + File.separator + name;

		metadata = studio.data().metadataBuilder().build();

		try {
			store = studio.data().createMultipageTIFFDatastore( savePath, true, false );
			
			// summary metadata
			int numImages = 50;
			store.setSummaryMetadata( generateSummaryMetadata( studio, path, name, numImages ) );

			// display and coordinate builder
			display = studio.displays().createDisplay( store );
			cb = studio.data().coordsBuilder().z( 0 ).c( 0 ).p( 0 ).t( 0 );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isFinished() {
		return finished;
	}

	@Override
	public void putImage(TaggedImage taggedImage) {
		try {
			System.out.println("[store] added a frame "+counter++);
			Image image = studio.data().convertTaggedImage(taggedImage, cb.t( counter++ ).build(), metadata);
			store.putImage( image );
			anything = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private SummaryMetadata generateSummaryMetadata( Studio studio, String path, String name, int n )
	{//Map<String, Object> properties){

		SummaryMetadata defaultSM = studio.acquisitions().generateSummaryMetadata();
		SummaryMetadata.Builder smBuilder = defaultSM.copyBuilder();

		// stacks dimensions
		Coords coords = defaultSM.getIntendedDimensions();
		smBuilder.intendedDimensions( coords.copyBuilder().t( n ).c( 1 ).p( 1 ).z( 1 ).build() );

		// others
		smBuilder.prefix( name );
		smBuilder.directory( path );

		return smBuilder.build();
	}
}
