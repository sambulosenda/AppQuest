package ch.delavega_schumacher.schrittzaehler;

import org.json.JSONArray;
import org.json.JSONException;

import android.speech.tts.TextToSpeech;

public class StepManager {

	private JSONArray allStepInstructions; // für den Fall, dass es nochmals benotwendigt wird (Statistik oder dergleichen)
	private JSONArray leftStepInstructions;

	private int tempStepsLeft = 0;
	private String tempTurnDirection = "";
	private TextToSpeech ttsstepspeaker;

	public StepManager(JSONArray instructions)
	{
		this.allStepInstructions = instructions;
		this.leftStepInstructions = instructions;
	}
	
	public JSONArray getleftStepInstructions()
	{
		return leftStepInstructions;
	}
		
	public void setNextInstructions() throws JSONException
	{
		if(leftStepInstructions.length() > 0)
		{
			try
			{
				// Parsing => sollte es nicht klappen, dann war es eine Drehanweisung und keine Laufanweisung
				int tempStepsLeft = leftStepInstructions.getInt(0);
				this.tempStepsLeft = tempStepsLeft;
			}
			catch(Exception ex)
			{
				String tempTurnDirection = leftStepInstructions.getString(0);
				this.tempTurnDirection = tempTurnDirection;
			}

			// remove nicht möglich in API18, darum wird eine neue Liste gespeichert mit den Werten, die verbleiben
			JSONArray tempListSaving = new JSONArray();     
			int len = leftStepInstructions.length();
			if (leftStepInstructions != null) { 
				for (int i=0;i<len;i++)
				{ 
					//Excluding the item at position
					if (i != 0) 
					{
						tempListSaving.put(leftStepInstructions.get(i));
					}
				} 
			}

			leftStepInstructions = tempListSaving;


		}
	}

	public int getStepsLeft()
	{
		return tempStepsLeft;
	}
	
	public boolean hasStepsLeft()
	{
		return tempStepsLeft > 0;
	}

	public boolean isAboutToTurn()
	{
		return tempTurnDirection != "";
	}
	
	public String getTurningDirection()
	{
		String tempTurnDirectionE = "";
		
		if(tempTurnDirection.equals("links"))
		{
			tempTurnDirectionE = "left";
		}
		else
		{
			tempTurnDirectionE = "right";
		}
		
		return tempTurnDirectionE;
	}

	public boolean makeStep() throws JSONException
	{
		if(tempStepsLeft > 0)
		{
			tempStepsLeft--;
			
			if(tempStepsLeft == 0)
			{
				this.setNextInstructions();
			}
			talkToMe(tempStepsLeft + " steps left");
			return true;
		}
		return false;
	}

	public boolean makeTurn() throws JSONException
	{
		if(tempTurnDirection != "")
		{
			talkToMe("You've turned " + tempTurnDirection);
			tempTurnDirection = "";
			this.setNextInstructions();
			return true;
		}
		return false;
	}

	public boolean isTourFinished()
	{
		if(leftStepInstructions.length() == 0 && tempStepsLeft == 0 && isAboutToTurn() == false)
		{
			talkToMe("You've reached the endstation.");
			return true;
		}
		
		return false;
	}
	
	public void talkToMe(String text)
	{
		// text to speech
	}

}
