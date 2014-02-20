package com.text.speech;

import java.util.Locale;

import javax.speech.Central;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.speech.synthesis.SynthesizerProperties;
import javax.speech.synthesis.Voice;


public class ResponseSpeaker {
	public void startSpeaking(String texttoSpeak) {
		Synthesizer synth = null;
		try {

			SynthesizerModeDesc desc = new SynthesizerModeDesc(Locale.ENGLISH);
			synth = Central.createSynthesizer(desc);
			((com.cloudgarden.speech.CGEngineProperties) synth
					.getSynthesizerProperties()).setEventsInNewThread(false);
			synth.addEngineListener(new TestEngineListener());
			

			synth.allocate();
			synth.resume();
			synth.waitEngineState(Synthesizer.ALLOCATED);

			Voice v = new Voice("Microsoft Anna, SAPI5, Microsoft", Voice.GENDER_FEMALE, Voice.AGE_MIDDLE_ADULT, null);
			
			System.out.println("Using voice " + v);
			SynthesizerProperties props = synth.getSynthesizerProperties();
			props.setVoice(v);
			props.setVolume(1.0f);
			props.setSpeakingRate(150.0f);

			synth.speak(texttoSpeak, null);
			synth.waitEngineState(Synthesizer.QUEUE_EMPTY);
			
		} catch (Exception e) {
			e.printStackTrace(System.out);
		} catch (Error e1) {
			e1.printStackTrace(System.out);
		} finally {
			try {
				synth.deallocate();
				synth.waitEngineState(Synthesizer.DEALLOCATED);
			} catch (Exception e2) {
			}
			
		}
	}
}
