package main.java.acqexample.acqj;

import org.micromanager.acqj.api.DataSink;
import org.micromanager.acqj.main.Acquisition;

import mmcorej.TaggedImage;
import mmcorej.org.json.JSONObject;

public class SimpleSink implements DataSink{

	boolean finished, anything;
	int counter;
	
	@Override
	public void initialize(Acquisition acq, JSONObject summaryMetadata) {
		finished = false;
		counter = 0;
		anything = false;
	}

	@Override
	public void finished() {
		finished = true;
	}

	@Override
	public boolean isFinished() {
		System.out.println("[sink] total "+counter);
		return finished;
	}

	@Override
	public void putImage(TaggedImage image) {
		anything = true;
		
		if (image != null) {
			if (image.tags != null && image.pix != null) {
				anything = true;
				System.out.println("[sink] got image " + image.hashCode() +", " +counter);
				counter++;
			}
		}
	}

	@Override
	public boolean anythingAcquired() {
		return anything;
	}

}
