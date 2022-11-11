package main.java.acqexample.acqj;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

import org.micromanager.acqj.api.TaggedImageProcessor;

import mmcorej.TaggedImage;

public class SimpleProcessor implements TaggedImageProcessor {
		
	@Override
	public void setDequeues(LinkedBlockingDeque<TaggedImage> source, LinkedBlockingDeque<TaggedImage> sink) {		
		ExecutorService executor = Executors.newSingleThreadExecutor((Runnable r) -> new Thread(r, "ImageProcessorThread"));

		executor.submit(() -> {
			int counter = 0;
			while (true) {
				TaggedImage newImage = source.pollFirst();
				if (newImage != null) {
					if (newImage.tags == null && newImage.pix == null) {
						System.out.println("[proc] done");
						executor.shutdown();
						break;
					}

					sink.add(newImage);
					System.out.println("[proc] pass on image " + counter);
					counter++;
				}
			}
			System.out.println("[proc] total " + counter);

		});
	}
}
