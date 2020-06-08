package com.alberto.psedomo.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;

import com.alberto.psedomo.controller.RandomGenerateController;
import com.alberto.psedomo.model.NumeroRandom;

 
/**
 * @author Albert Sh@ady  
 * Version: 1.0.1
 * 
 */
 
public class Generate implements Callable<Boolean> {
 
	private int workerNumber;
	private long time;
	public NumeroRandom numero;
	
	public int getNumber() {
		return workerNumber;
	}
 
	public void setNumber(int workerNumber) {
		this.workerNumber = workerNumber;
	}
 
	public Generate(NumeroRandom numero,long time) {
		this.time = time;
		this.numero = numero;
		
	}
 
	SimpleDateFormat Formatter = new SimpleDateFormat("dd-MMMMM-yyyy hh:mm:ss");
 
	
	
	public Boolean call() throws InterruptedException {
		
		
			
		try {
			if(!Thread.currentThread().isInterrupted()) {
				//System.out.println("the states of the thread "+Thread.currentThread().);
				int index=	RandomGenerateController.pendinglist.indexOf(numero);
				int number=	RandomGenerateController.pendinglist.get(index).getNumber();
				RandomGenerateController.pendinglist.remove(index);
				System.out.println(" The thread reunnng "+numero.getNumber()+" the index is  "+index+" and the number is "+number +" the time beeing used is "+time);
				Thread.sleep(time);
				
				//System.out.println(String.format(" Seting  the process task thread %s the codigo "+numero.getId(), Thread.currentThread().getName()));
				long endTime=System.currentTimeMillis();
				long queueDuration = ( endTime- numero.getTimeArrived())/1000;
				numero.SetTimeToProcess(queueDuration);
				RandomGenerateController.finishedlist.add(numero);
				RandomGenerateController.contagem++;
			}
			
		} catch (InterruptedException ie) {
			log("\n" + Formatter.format(new Date()) + "  task " + getNumber() + " interrupted.");
			log("\n=> Basically once thread is timed out, it should be cancelled and interrupted. (timedout ==> cancelled ==> interrupted)");
		}
		return true;
	  
		
	}
 
	
	
	public void log(String info) {
		System.out.println(info);
 
	}
}