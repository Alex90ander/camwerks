package com.shamwerks.camwerks.pojo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.Set;

import javax.swing.JOptionPane;

import com.shamwerks.camwerks.CamWerks;
import com.shamwerks.camwerks.config.Constants;
import com.shamwerks.camwerks.config.Toolbox;
import com.shamwerks.camwerks.config.Constants.CamDirection;
import com.shamwerks.camwerks.config.Constants.CamType;

public class Camshaft {

	private String name;
	private int nbSteps;

	private int nbCylinders;
	private int nbIntakeCamPerCylinder;
	private int nbExhaustCamPerCylinder;

	//private Cam[] cams;
	private LinkedHashMap<String, Cam> cams = new LinkedHashMap<String,Cam>();

	
	//not saved in .cam file:
	private int nbCycles;
	private CamDirection direction = CamDirection.CLOCKWISE;
	

	public Camshaft(String name, int nbCylinders, int nbIntakeCamPerCylinder, int nbExhaustCamPerCylinder, int nbValues){
		this.name = name;
		this.nbSteps = nbValues;
		this.nbCylinders = nbCylinders;
		this.nbIntakeCamPerCylinder = nbIntakeCamPerCylinder;
		this.nbExhaustCamPerCylinder = nbExhaustCamPerCylinder;
		
		for(int c=1 ; c<=nbCylinders ; c++){
			for(int i=1 ; i<=nbIntakeCamPerCylinder ; i++){
				addCam(new Cam(CamType.INTAKE, c, i, nbValues) );
			}
			for(int i=1 ; i<=nbExhaustCamPerCylinder ; i++){
				addCam( new Cam(CamType.EXHAUST, c, i, nbValues) );
			}
		}
		
	}
	public void addCam(Cam cam){
		cams.put(cam.getPrefix(), cam);
	}

	public static Camshaft parseCamFile(String camFilePath){
		Properties camFile = new Properties();
		try {
		    camFile.load( new FileInputStream( camFilePath ) );
		} catch (IOException e) {
			JOptionPane.showMessageDialog(CamWerks.getInstance().getFrame(), e.getStackTrace().toString() );
		}
		String name = camFile.getProperty(Constants.camFileName);
		int nbSteps = Integer.parseInt(camFile.getProperty(Constants.camFileNbSteps));
		int nbCyls  = Integer.parseInt(camFile.getProperty(Constants.camFileNbCylinders));
		int nbInt   = Integer.parseInt(camFile.getProperty(Constants.camFileNbIntakeCamsPerCylinder));
		int nbExh   = Integer.parseInt(camFile.getProperty(Constants.camFileNbExhaustCamsPerCylinder));
		
		Camshaft camshaft = new Camshaft(name, nbCyls, nbInt, nbExh, nbSteps);
		
		for(int cylId=1 ; cylId<=nbCyls ; cylId++){
			for(int camId=1 ; camId<=nbInt ; camId++){
				Cam cam = new Cam(CamType.INTAKE, cylId, camId, nbSteps);
				cam.parseCamStr( camFile.getProperty(cam.getPrefix()) );
				camshaft.addCam( cam );
			}
			for(int i=1 ; i<=nbExh ; i++){
				Cam cam = new Cam(CamType.EXHAUST, cylId, i, nbSteps);
				cam.parseCamStr( camFile.getProperty(cam.getPrefix()) );
				camshaft.addCam( cam );
			}
		}
		
		System.out.println("================\n" + camshaft + "\n================");
		return camshaft;
	}
	
	
	
	/*
	private void parseCamStr(int cylNumber, int camIdx, String strCam, CamType camType){
		getCam(camIdx).setCamType(camType);
		getCam(camIdx).setCylNumber(cylNumber);
		String values[] = strCam.split(";");
		for(int j=0 ; j<values.length ; j++){
			String[] value = values[j].split(" ");
			getCam(camIdx).setValue(Integer.parseInt(value[0]), Double.parseDouble(value[1]));
		}
		getCam(camIdx).normalizeValues();
	}*/
	
	public void saveAsCSV(String filePath){
		StringBuffer content = new StringBuffer();
		
		for(int j=0 ; j<nbSteps ; j++){
			content.append( j );
			for(String key : cams.keySet()){
				content.append( cams.get(key).getValue(j) + " " );
		    }
			content.append( "\r\n");
		}
		System.out.println(content);
		//TODO !!!!
	}
	
	public void saveAsCAM(String filePath){
		//StringBuffer content = new StringBuffer();
		/*
		for(int j=0 ; j<nbSteps ; j++){
			content.append( j );
			for(int i=0 ; i<cams.length ; i++){
				content.append( cams[i].getValue(j) + " " );
		    }
			content.append( "\r\n");
		}
		*/
        Properties props = new Properties();
        
        props.setProperty(Constants.camFileName, name);
        props.setProperty(Constants.camFileNbSteps, ""+nbSteps);
        props.setProperty(Constants.camFileNbCylinders, ""+nbCylinders);
        props.setProperty(Constants.camFileNbIntakeCamsPerCylinder, ""+nbIntakeCamPerCylinder);
        props.setProperty(Constants.camFileNbExhaustCamsPerCylinder, ""+nbExhaustCamPerCylinder);

        for(String key : cams.keySet()){
        	Cam cam = cams.get(key);
        	String values = "";
        	for (int j=0; j<cam.getValues().length ; j++){
        		values += ";" + j + " " + cam.getValue(j);
        	}
        	values = values.substring(1);
            props.setProperty( cam.getPrefix(), values );
        }
        
        File f = new File(filePath);
        try {
	        OutputStream out = new FileOutputStream( f );
	        props.store(out, "This is an optional header comment string");
	    }
	    catch (Exception e ) {
	        e.printStackTrace();
	    }		
	}
	
	public int getNbCams() {return cams.size(); }

	public int getNbDisplayedCams() {
		int i=0;
        for(String key : cams.keySet()){
        	if(cams.get(key).isDisplay())i++;
        }
		return i;
	}
	
    public Cam getCam(String key){
		return cams.get(key);
		
	}
	
    public Cam getCam(int index){
    	//TODO : well, that ain't clean...
		return (Cam)cams.values().toArray()[index];
		
	}
	
    public Set<String> getKeys(){
		return cams.keySet();
	}
	
	public String getName() {
		return name;
	}

	public int getNbSteps() {
		return nbSteps;
	}

	public void setNbSteps(int nbSteps) {
		this.nbSteps = nbSteps;
	}

	public int getNbIntakeCamPerCylinder() {
		return nbIntakeCamPerCylinder;
	}

	public int getNbExhaustCamPerCylinder() {
		return nbExhaustCamPerCylinder;
	}

	public int getNbCylinders() {
		return nbCylinders;
	}

	public CamDirection getDirection() {
		return direction;
	}

	public void setDirection(CamDirection direction) {
		this.direction = direction;
	}

	public int getNbCycles() {
		return nbCycles;
	}

	public void setNbCycles(int nbCycles) {
		this.nbCycles = nbCycles;
	}

	public double getOverlap(int cylinder, double lift){
		int exhClose = nbSteps;
		int intOpen = 0;
			
        for(String key : cams.keySet()){
        	Cam cam = cams.get(key);

        	if(cam.getCylNumber() == cylinder){

        		//first loop to find peak
        		int peak = 0;
        		double max = 0;
       		    for (int j=0; j<cam.getValues().length ; j++){
        			if(cam.getValue(j) > max){
        				max =cam.getValue(j);
        				peak = j;
        			}
        		}

        		if(cam.getCamType() == CamType.EXHAUST){
   	        		for (int j=peak; j<cam.getValues().length ; j++){
   	        			//we're on the descending slope as we're post-peak
   	        			if( cam.getValue(j)<lift){
   	        				exhClose = Math.min(exhClose, j); 
   	        				break;
   	        			}
   	        		}//end for cam values
       			}
       			else{ //INTAKE
   	        		for (int j=0; j<peak ; j++){
   	        			//we're on the ascending slope as we're pre-peak
   	        			if( cam.getValue(j)>lift){
   	        				intOpen = Math.max(intOpen, j); 
   	        				break;
   	        			}
   	        		}//end for cam values
       			}
        	}//end for cyl number
        }//end for cams
		double overlap = (exhClose-intOpen)*(360.0F/nbSteps) * 2; //because crankshaft does 2 turns for 1 turn of camshaft 
		return Toolbox.round(overlap,2);
	}
	
	@Override
	public String toString(){
		String out = "Name=" + name + "\n";
		out += "nbSteps=" + nbSteps + "\n";
		out += "nbCylinders=" + nbCylinders + "\n";
		out += "nbIntakeCamPerCylinder=" + nbIntakeCamPerCylinder + "\n";
		out += "nbExhaustCamPerCylinder=" + nbExhaustCamPerCylinder;
		return out;
	}
}
