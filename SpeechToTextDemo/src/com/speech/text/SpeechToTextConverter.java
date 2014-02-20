package com.speech.text;

import java.io.FileReader;
import java.util.Locale;

import javax.speech.Central;
import javax.speech.EngineException;
import javax.speech.EngineStateError;
import javax.speech.recognition.Recognizer;
import javax.speech.recognition.RecognizerModeDesc;
import javax.speech.recognition.RuleGrammar;

import com.text.speech.GreetUserIntractive;
import com.text.speech.ResponseSpeaker;

public class SpeechToTextConverter {
	static Recognizer recognizer;
	static ResponseSpeaker mResponseSpeaker;
	

	public static void exitApplication(){
		recognizer.forceFinalize(true);
		try {
			recognizer.deallocate();
		} catch (EngineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EngineStateError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}

	public static void main(String args[]) {
		GreetUserIntractive mGreetUserIntractive = new GreetUserIntractive();
		mGreetUserIntractive.startGreeting();
		mResponseSpeaker = new ResponseSpeaker();
		
		try {
			Central.registerEngineCentral("com.cloudgarden.speech.CGEngineCentral");
			RecognizerModeDesc desc = new RecognizerModeDesc(Locale.US,
					Boolean.TRUE);
			// Create a recognizer that supports US English.
			recognizer = Central.createRecognizer(desc);

			// Start up the recognizer
			recognizer.allocate();

			// Load the grammar from a file, and enable it
			FileReader fileReader = new FileReader(
					"C:\\Documents and Settings\\gur31265\\workspace\\SpeechToTextDemo\\src\\my_grammar.grammar");
			RuleGrammar grammar = recognizer.loadJSGF(fileReader);
			grammar.setEnabled(true);

			// initialize program list
			

			// Add the listener to get results
			recognizer.addResultListener(new TestResultListener(recognizer, 0, true));

			// Commit the grammar
			recognizer.commitChanges();
			recognizer.waitEngineState(Recognizer.LISTENING);

			// Request focus and start listening
			recognizer.requestFocus();
			recognizer.resume();

			recognizer.waitEngineState(Recognizer.FOCUS_ON);

			recognizer.forceFinalize(true);
			recognizer.waitEngineState(Recognizer.DEALLOCATED);

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
}
