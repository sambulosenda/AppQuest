package ch.delavega_schumacher.schrittzaehler;

import org.json.JSONArray;
import org.json.JSONException;

public class StepManager {

	private JSONArray allStepInstructions; // für den Fall, dass es nochmals benotwendigt wird (Statistik oder dergleichen)
	private JSONArray leftStepInstructions;

	private int tempStepsLeft = 0;
	private String tempTurnDirection;

	public StepManager(JSONArray instructions)
	{
		this.allStepInstructions = instructions;
		this.leftStepInstructions = instructions;
	}

	public void setNextInstructions() throws JSONException
	{
		if(leftStepInstructions.length() > 0)
		{
			try
			{
				// Parsing => sollte es nicht klappen, dann war es eine Drehanweisung und keine Laufanweisung
				int tempStepsLeft = Integer.parseInt(String.valueOf(leftStepInstructions.get(0)));
				this.tempStepsLeft = tempStepsLeft;
			}
			catch(Exception ex)
			{
				String tempTurnDirection = String.valueOf(leftStepInstructions.get(0));
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

	public boolean isAboutToTurn()
	{
		return tempTurnDirection != "";
	}

	public boolean makeStep() throws JSONException
	{
		if(tempStepsLeft > 0)
		{
			tempStepsLeft--;
			this.setNextInstructions();
			return true;
		}
		return false;
	}

	public boolean makeTurn() throws JSONException
	{
		if(tempTurnDirection != "")
		{
			tempTurnDirection = "";
			this.setNextInstructions();
			return true;
		}
		return false;
	}

	public boolean isTourFinished()
	{
		return leftStepInstructions.length() == 0;
	}

}
