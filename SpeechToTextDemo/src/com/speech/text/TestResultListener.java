package com.speech.text;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.speech.*;
import javax.speech.recognition.*;
import com.cloudgarden.speech.CGResult;
import com.write.text.Keyboard;

/**
 * Class to print out result events, and deallocate the recognizer after a given
 * number of accepted results. Also demonstrates playback of recorded speech and
 * use of tokenCorrection (except the actual tokenCorrection line has been
 * commented out).
 */
public class TestResultListener extends ResultAdapter {
	private int nRecs = 0;
	private Recognizer rec;
	private boolean playAudio;
	private static HashMap<String, String> progList;
	boolean flag = true;	

	private String[] key = { "OneNote", "Excel", "PowerPoint", "Outlook",
			"word", "VPN Client", "Skype", "VirusScan", "MySQL workbench",
			"reader", "Office Communicator", "Yammer", "Preforce",
			"Firefox", "notepad", "wordpad", "eclipse" };

	private String[] path = {
			"C:\\Program Files\\Microsoft Office\\Office14\\ONENOTE.exe",
			"C:\\Program Files\\Microsoft Office\\Office14\\EXCEL.exe",
			"C:\\Program Files\\Microsoft Office\\Office14\\POWERPNT.exe",
			"C:\\Program Files\\Microsoft Office\\Office14\\OUTLOOK.exe",
			"C:\\Program Files\\Microsoft Office\\Office14\\WINWORD.exe",
			"C:\\Program Files\\Cisco\\Cisco AnyConnect VPN Client\\vpnui.exe",
			"C:\\Program Files\\Skype\\phone\\skype.exe",
			"C:\\Program Files\\McAfee Security Scan\\3.0.207\\mcuicnt.exe",
			"C:\\Program Files\\MySQL\\MySQL Workbench CE 5.2.40\\MySQLWorkbench.exe",
			"C:\\Program Files\\Adobe\\Reader 10.0\\Reader\\AcroRd32.exe",
			"C:\\Program Files\\Microsoft Office Communicator\\communicator.exe",
			"c:\\program files\\Yammer\\yammer.exe",
			"C:\\Program Files\\Perforce\\p4v.exe",
			"C:\\Program Files\\Mozilla Firefox\\firefox.exe",
			"C:\\Windows\\notepad.exe", "C:\\Windows\\write.exe",
			"D:\\E & F Drive Back up\\eclipse\\eclipse.exe" };

	/**
	 * Creates a TestResultListener which will deallocate the given Recognizer
	 * after "nRecs" accepted recognitions, and will re-play the recorded audio
	 * if "playAudio" is true (and audio is being saved using
	 * RecognizerProperties.setResultAudioProvided(true))
	 */
	public TestResultListener(Recognizer rec, int nRecs, boolean playAudio) {
		this.rec = rec;
		this.nRecs = nRecs;
		this.playAudio = playAudio;
		progList = new HashMap<String, String>();
		for (int i = 0; i < key.length; i++) {
			progList.put(key[i], path[i]);
		}
	}

	public void resultRejected(ResultEvent e) {
		Result r = (Result) (e.getSource());
		System.out.println("Result Rejected " + r);
	}

	public void resultCreated(ResultEvent e) {
		// Result r = (Result)(e.getSource());
		System.out.println("Result Created ");
	}

	public void resultUpdated(ResultEvent e) {
		Result r = (Result) (e.getSource());
		System.out.println("Result Updated... " + r);
		ResultToken[] tokens = r.getBestTokens();
		if (tokens != null && tokens.length > 0) {
			displayTimes(tokens[0]);
		}
	}

	private void displayTimes(ResultToken token) {
		Date start = new Date(token.getStartTime());
		Date now = new Date(System.currentTimeMillis());
		System.out.println("Result start = " + start.getMinutes() + ":"
				+ start.getSeconds() + ", length = "
				+ ((token.getEndTime() - token.getStartTime()) / 1000.0)
				+ ", now=" + now.getMinutes() + ":" + now.getSeconds());
	}

	public void resultAccepted(ResultEvent e) {
		final FinalResult r = (FinalResult) (e.getSource());
		Runnable lt = new Runnable() {
			public void run() {
				try {
					System.out.print("Result Accepted: " + r);
					ResultToken tokens[] = null;
					// only way to find out if it's a FinalRuleResult or a
					// FinalDictationResult is to see whether
					// it's grammar is a Rule or Dictation Grammar, since all
					// Results obtained from a ResultEvent
					// implement both FinalRuleResult and FinalDictationResult
					// interfaces (see JSAPI documentation)
					if (r.getGrammar() instanceof RuleGrammar) {
						System.out.println("\nRuleGrammar name="
								+ ((FinalRuleResult) r).getRuleGrammar(0)
										.getName());
						System.out.println("Rule name="
								+ ((FinalRuleResult) r).getRuleName(0));
						tokens = ((FinalRuleResult) r).getAlternativeTokens(0);
					} else {
						System.out.println("\nGrammar name="
								+ r.getGrammar().getName());
						tokens = r.getBestTokens();
					}

					if (tokens != null && tokens.length > 0) {
						displayTimes(tokens[0]);
					}

					if (playAudio) {
						try {
							System.out
									.println("Speaking all tokens of recorded speech");
							java.applet.AudioClip clip = null;
							if (tokens != null && tokens.length > 0)
								clip = r.getAudio(tokens[0],
										tokens[tokens.length - 1]);
							if (clip != null) {
								rec.pause();
								clip.play();
								rec.resume();
							} else
								System.out
										.println("Audio clip is null - can't play");
							// release it so it doesn't hang about wasting space
							r.releaseAudio();
						} catch (EngineStateError e) {
							System.out.println("Non-fatal error: " + e);
						}
					}

					// Test out token correction - here we just get the "nAlt"th
					// alternative
					// and "correct" the result using it
					// (except here we've commented out the tokenCorrection call
					// ...include that line if you actually want to update your
					// profile randomly!

					try {
						ResultToken[] toks = null;
						int nAlt = 3;
						if (r.getGrammar() instanceof DictationGrammar) {

							// Print out first three alternatives
							String str = "";
							FinalDictationResult fdr = (FinalDictationResult) r;
							ResultToken start = fdr.getBestToken(0);
							ResultToken end = fdr
									.getBestToken(fdr.numTokens() - 1);
							ResultToken[][] tokenArray = fdr
									.getAlternativeTokens(start, end, 3);							
							if (tokenArray != null) {
								for (int i = 0; i < tokenArray.length; i++) {
									str += "\nAlternative (engineConf="
											+ ((CGResult) fdr)
													.getEngineConfidence(i)
											+ ") " + i + " =";
									for (int j = 0; j < tokenArray[i].length; j++) {
										str += " "
												+ tokenArray[i][j]
														.getSpokenText();
										
									}
								}
							}
							System.out.println("1:" + str);
							performAction(str);
							tokenArray = fdr
									.getAlternativeTokens(start, end, 3);
							if (tokenArray != null && tokenArray.length > nAlt)
								toks = tokenArray[nAlt];

						} else {
							// Print out first three alternatives
							String str = "";
							FinalRuleResult frr = (FinalRuleResult) r;
							for (int i = 0; i < 3; i++) {
								ResultToken[] tokenArray = frr
										.getAlternativeTokens(i);
								if (tokenArray != null) {
									str += "\nAlternative (engineConf="
											+ ((CGResult) frr)
													.getEngineConfidence(i)
											+ ") " + i + " =";
									for (int j = 0; j < tokenArray.length; j++) {
										str += " "
												+ tokenArray[j].getSpokenText();										
									}
								}
							}
							System.out.println("2:" + str);
							performAction(str);
							toks = ((FinalRuleResult) r)
									.getAlternativeTokens(nAlt);
						}
						if (toks != null) {
							String[] stoks = new String[toks.length];
							for (int i = 0; i < stoks.length; i++)
								stoks[i] = toks[i].getSpokenText();
							System.out.print("Could correct ... '");
							for (int i = 0; i < tokens.length; i++)
								System.out.print(tokens[i].getSpokenText()
										+ " ");
							System.out.print("' ...with... '");
							for (int i = 0; i < stoks.length; i++)
								System.out.print(stoks[i] + " ");
							System.out
									.println("' ...(except the tokenCorrection call is commented out)");
							// r.tokenCorrection(stoks,tokens[0],tokens[tokens.length-1],
							// 0);

							// NOTE: for SAPI4 engines, after correction, the
							// alternatives are deleted,
							// but for SAPI5 engines the alternatives are
							// rearranged to be consistent
							// with the correction - you can observe this by
							// looking at the output of the
							// System.out.println("RESULT is "+r); line below

						}
					} catch (ResultStateError er) {
						er.printStackTrace(System.out);
						// Result is not a DictationResult
					}
				} catch (Exception e1) {
					e1.printStackTrace(System.out);
				} catch (ResultStateError er) {
					er.printStackTrace(System.out);
				}
				nRecs--;
				if (nRecs == 0
						&& rec.testEngineState(Recognizer.ALLOCATED)
						&& !rec.testEngineState(Recognizer.DEALLOCATING_RESOURCES)) {
					try {
						System.out.println("forcing finalize");
						rec.forceFinalize(true);
						System.out.println("deallocating");
						rec.deallocate();
					} catch (Exception e2) {
						e2.printStackTrace(System.out);
					}
				}
			}
		};
		(new Thread(lt)).start();

	}

	public void performAction(String result) {
		try {
			System.out.println("Text: " + result);
			result= result.substring(result.lastIndexOf('=')+1, result.length());
			System.out.println("Text: " + result);
			String[] resultToken = processToken(result);
			for (int nIndex = 0; nIndex < resultToken.length; nIndex++) {

				/*
				 * mResponseSpeaker.startSpeaking(resultToken[nIndex] ) + " ");
				 */
				if (resultToken[nIndex].equalsIgnoreCase("exit")) {
					// Deallocate the recognizer
					// 
					System.out.println("Exiting program....");
					SpeechToTextConverter.exitApplication();
				}
				if (resultToken[nIndex].equalsIgnoreCase("open")
						&& (!resultToken[nIndex + 1].equals(null))
						&& flag == true) {
					startProcess(resultToken[nIndex + 1]);
				}
				if (resultToken[nIndex].equalsIgnoreCase("browse")
						&& (!resultToken[nIndex + 1].equals(null))
						&& flag == true) {
					openBrowser(resultToken[nIndex + 1]);
				}
				if (resultToken[nIndex].equalsIgnoreCase("type")) {
					flag = false;
				}
				if (resultToken[nIndex].equalsIgnoreCase("finish")
						&& flag == false) {
					flag = true;
				}
				if (flag == false) {
					Keyboard keyboard = new Keyboard();
					keyboard.init(resultToken[nIndex] + " ");
				}

			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return;
	}

	private String[] processToken(String result) {
		// TODO Auto-generated method stub
		StringTokenizer tokenizer = new StringTokenizer(result, " ");
        List<Object> tempList = new ArrayList<Object>();
        while (tokenizer.hasMoreElements()) {
            tempList.add(tokenizer.nextElement());
        }
        String[] ret = new String[tempList.size()];
        System.out.println("array:"+tempList.toString());
        tempList.toArray(ret);
        return ret;
	}

	private void openBrowser(String spokenText) {
		// TODO Auto-generated method stub
		try {
			System.out.println("testing:" + spokenText);
			spokenText.substring(spokenText.indexOf(" ") + 1,
					spokenText.length());
			Runtime.getRuntime().exec(
					new String[] {
							"cmd.exe",
							"/c",
							"C:\\Users\\gur31265\\workspace\\SpeechToTextDemo\\bin\\com\\speech\\text\\"
									+ spokenText + ".bat" });

		} catch (Exception hj) {
			// mResponseSpeaker.startSpeaking("Error not der" + hj);
		}
	}

	private void startProcess(String spokenText) {
		// TODO Auto-generated method stub
		try {
			spokenText.substring(spokenText.indexOf(" ") + 1,
					spokenText.length());
			System.out.println("final: " + spokenText);
			System.out.println(progList.get(spokenText));
			ProcessBuilder proc = new ProcessBuilder(progList.get(spokenText));
			proc.start();
		} catch (Exception hj) {
			System.out.println("Error not der" + hj);
		}
	}
}
