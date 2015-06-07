package com.musicPlayer.JML;

import java.io.IOException;

import javafx.scene.media.AudioSpectrumListener;
import javafx.scene.media.MediaPlayer;

public class SpectrumListener implements AudioSpectrumListener {
	private final SpectrumBar[] bars;
	  private double minValue;
	  private double[] norms;
	  private int[] spectrumBucketCounts;
	  private int index=0;

	  SpectrumListener(double startFreq, MediaPlayer mp, SpectrumBar[] bars) {
	    this.bars = bars;
	    this.minValue = mp.getAudioSpectrumThreshold();
	    this.norms = createNormArray();
	    
	    int bandCount = mp.getAudioSpectrumNumBands();
	    this.spectrumBucketCounts = createBucketCounts(startFreq, bandCount);
	  }

	  @Override
	  public void spectrumDataUpdate(double timestamp, double duration,  float[] magnitudes, float[] phases) {
	    int index = 0;
	    int bucketIndex = 0;
	    int currentBucketCount = 0;
	    double sum = 0.0;
	    
	    while (index < magnitudes.length) {
	      sum += magnitudes[index] - minValue;
	      ++currentBucketCount;
	      
	      if (currentBucketCount >= spectrumBucketCounts[bucketIndex]) {
	        bars[bucketIndex].setValue(sum / norms[bucketIndex]);
	        EqualizerView.printStream.print(("I"+bucketIndex+"B"+bars[bucketIndex].getBarCount()));
	        //System.out.println(("I"+bucketIndex+"B"+bars[bucketIndex].getBarCount()));
	        try {
				System.out.println(EqualizerView.serialReader.readLine());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        currentBucketCount = 0;
	        sum = 0.0;
	        ++bucketIndex;
	      }
	      
	      ++index;
	    }
	  }

	  private double[] createNormArray() {
	    double[] normArray = new double[bars.length];
	    double currentNorm = 0.05;
	    for (int i = 0; i < normArray.length; i++) {
	      normArray[i] = 1 + currentNorm;
	      currentNorm *= 2;
	    }
	    return normArray;
	  }

	  private int[] createBucketCounts(double startFreq, int bandCount) {
	    int[] bucketCounts = new int[bars.length];
	    
	    double bandwidth = 22050.0 / bandCount;
	    double centerFreq = bandwidth / 2;
	    double currentSpectrumFreq = centerFreq;
	    double currentEQFreq = startFreq / 2;
	    double currentCutoff = 0;
	    int currentBucketIndex = -1;
	    
	    for (int i = 0; i < bandCount; i++) {
	      if (currentSpectrumFreq > currentCutoff) {
	        currentEQFreq *= 2;
	        currentCutoff = currentEQFreq + currentEQFreq / 2;
	        ++currentBucketIndex;
	        if (currentBucketIndex == bucketCounts.length) {
	          break;
	        }
	      }
	      
	      ++bucketCounts[currentBucketIndex];
	      currentSpectrumFreq += bandwidth;
	    }
	    
	    return bucketCounts;
	  }
	}