package com.shamwerks.camwerks.pojo;

import com.shamwerks.camwerks.config.Constants;
import com.shamwerks.camwerks.config.Constants.CamType;
import com.shamwerks.camwerks.config.Lang;
import com.shamwerks.camwerks.config.LangEntry;
import com.shamwerks.camwerks.config.Toolbox;

public class Cam {

	private CamType  camType;
	private int      camNumber;
	private int      cylNumber;
	private double[]  values;
	private boolean   display = true;
	
	Cam(CamType camType, int cylNumber, int camNumber, int numberOfValues){
		this.camType   = camType;
		this.cylNumber = cylNumber;
		this.camNumber = camNumber;
		values = new double[numberOfValues];
	}

	public double[] getValues() {
		return values;
	}

	public double getValue(int idx) {
		return values[idx];
	}

	public void setValue(int idx, double value) {
		values[idx] = value;
	}

	public int getCamNumber() {
		return camNumber;
	}

	public CamType getCamType() {
		return camType;
	}
	
	public boolean isExhaust() {
		return camType==CamType.EXHAUST;
	}
	
	public boolean isIntake() {
		return camType==CamType.INTAKE;
	}
	
	public String getColumnName(){
		return Lang.getText(LangEntry.TABLE_COLUMN_CYLINDER) + cylNumber + "-" +
			   Lang.getText(isIntake()?LangEntry.TABLE_COLUMN_INTAKE:LangEntry.TABLE_COLUMN_EXHAUST) + camNumber;
	}
	
    public void parseCamStr(String strCam){
		String values[] = strCam.split(";");
		for(int j=0 ; j<values.length ; j++){
			String[] value = values[j].split(" ");
			setValue(Integer.parseInt(value[0]), Double.parseDouble(value[1]));
		}
		normalizeValues();
	}

	public double normalizeValues(){
		//first loop to find the min value...
		double min = values[0];
		for (int i=0 ; i<values.length ; i++){
			min=Math.min(min, values[i]);
		}
		//second loop to actually reset the min...
		for (int i=0 ; i<values.length ; i++){
			values[i] = Toolbox.round(values[i] - min, 2);
		}
		return min;
	}
	
	public double getMaxLift(){
		double max = 0;
		for (int i=0 ; i<values.length ; i++){
			max=Math.max(max, values[i]);
		}
		return max;
	}
	
	public double getDuration(double lift){
		int startIdx = -1, endIdx = -1;
		for (int i=0 ; i<values.length ; i++){
			if(startIdx == -1 && values[i] > lift) startIdx = i;
			if(values[i] > lift) endIdx = i;
		}
		double duration = (endIdx-startIdx)*(360.0F/values.length) * 2; //because crankshaft does 2 turns for 1 turn of camshaft 
		return Toolbox.round(duration,2);
	}

	
	public int getCylNumber() {
		return cylNumber;
	}

	public String getPrefix(){
		return getPrefix( cylNumber, camType, camNumber );
	}
	
	public static String getPrefix(int cylIdx, CamType type, int camIdx){
        return Constants.camFileCylPrefix + cylIdx + "-" +   
	                 (type==CamType.INTAKE?Constants.camFileIntPrefix:Constants.camFileExhPrefix) + 
	                 camIdx;
	}
	
	public String getDescription(){
		String descr = Lang.getText( LangEntry.CAM_DESCRIPTION );
		//descr  = descr.replaceAll("[INTEXH]", (camType==CamType.INTAKE ? Lang.getText(Lang.CAM_INTAKE) : Lang.getText(Lang.CAM_EXHAUST)) );
		//descr  = descr.replaceAll("[NBCAM]", Integer.toString(camNumber));
		//descr  = descr.replaceAll("[NBCYL]", Integer.toString(cylNumber));
		
		return String.format(descr, 
								(camType==CamType.INTAKE ? Lang.getText(LangEntry.CAM_INTAKE) : Lang.getText(LangEntry.CAM_EXHAUST)),
								Integer.toString(camNumber), 
								Integer.toString(cylNumber));
		//return descr;
	}

	public boolean isDisplay() {
		return display;
	}

	public void setDisplay(boolean display) {
		this.display = display;
	}
}


