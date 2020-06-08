package com.alberto.psedomo.controller;
import java.lang.reflect.Method;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import com.alberto.psedomo.model.DelayMe;
import com.alberto.psedomo.model.NumeroRandom;
import com.alberto.psedomo.service.Generate;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * @author
 * Version: 1.0.2
 * 
 */


@Path("/")
public class RandomGenerateController {
	static int sizepoll=1;
	public  static int contagem=0;
	SimpleDateFormat Formatter = new SimpleDateFormat("dd-MMMMM-yyyy hh:mm:ss");
    
    ExecutorService executorService = Executors.newFixedThreadPool(sizepoll);
    public static  ArrayList<NumeroRandom> treadlist = new ArrayList<>();
    public static  ArrayList<NumeroRandom> pendinglist = new ArrayList<>();
    public static  ArrayList<NumeroRandom> finishedlist = new ArrayList<>();
    public static  List<Future> Futurerequests = new ArrayList<Future>();
    
    
    
    
   
    @POST
	@Path("/random")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
    public String send(@HeaderParam("X-Max-Wait") String dadoTempo) {

    	//String dadoTempo="";
    	long Tempo;
    	
    	
    	if (dadoTempo!=null) {
    		Tempo=1000*Long.parseLong(dadoTempo);
    		
    		if(Tempo<30000) {
        		return "The request can not be processed, the time give should be at least 31 s";
        	}
    		
    	}else{
    		Tempo=30000;
    	}
    	
    	String condigoUniversal=GetID();
		long startTime = System.currentTimeMillis();
		Random rand = new Random(); 
		int numb = rand.nextInt(10000)+3;
		int numeroGerado=Math.abs(numb);
		NumeroRandom novoPendente=new NumeroRandom();
		novoPendente.setId(condigoUniversal);
		novoPendente.setNumber(numeroGerado);
		novoPendente.setTimeArrived(startTime);
		novoPendente.SetTimeToProcess(Tempo);
		
		pendinglist.add(novoPendente);
		treadlist.add(novoPendente);
		Futurerequests.add(executorService.submit(new Generate(novoPendente,Tempo)));
		
		executorService.execute(new VerificaRequisicao(Futurerequests));
		//TrataRequisicao(1);
		return "The request has been sent to generate the random number "+numeroGerado;
		
	}
	 
 
    @GET
	@Path("/history")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<NumeroRandom> index() {	
    	
	    ArrayList<NumeroRandom> finishedlistup = new ArrayList<>();
    	DateFormat simple = new SimpleDateFormat("dd MM yyyy HH:mm:ss:SSS z");
    	
	    for (NumeroRandom novo : finishedlist) {
				
	    	    Date result=new Date(novo.getTimeArrived());
				
	    	    simple.format(result);
	    		
	    		 NumeroRandom newNumber=new NumeroRandom();
	    		 newNumber.setId(novo.getId());
	    		 newNumber.setNumber(novo.getNumber());
	    		 newNumber.setTimeArrived(novo.getTimeArrived());
	    		 newNumber.SetTimeToProcess(novo.getTimeToProcess()/1000);
	    		 finishedlistup.add(newNumber);
	    	
		}
	     
	    return finishedlistup;
	     
    }
	
    @PUT
	@Path("{requestId}/cancel")
	@Produces(MediaType.APPLICATION_JSON)
	 public String remove(@PathParam("id") String requestId){	
    	 int count=0;
    	 String request=requestId;
    	
    	 String respost= "The request ID does not existes";
    	
		 for (NumeroRandom listathread : treadlist) {
			 	if (listathread.getId().equals(respost)) {
			 		
			 		if(! Futurerequests.get(count).isCancelled() | ! Futurerequests.get(count).isDone()) {
						//canceling the request
						Futurerequests.get(count).cancel(false);
						
						   respost= "The request has been cancelled";
					}else {
						
							if(Futurerequests.get(count).isCancelled()) {
								respost= "The request has already been cancelled";
							}else {
								respost= "The request ID has been done can not be cancelled";
							}
							
						
					}
			 		break;
    		    }
			 count++;
			 
			 
    	}		 
		 return respost;
		 
	 }
	 
    
    @GET
	@Path("/stats")
	@Produces(MediaType.APPLICATION_JSON)
	public String  status() {
    	 int contador = 0;
         
         ArrayList<NumeroRandom> finishedlistuppendig = new ArrayList<>();
         
         int totalpeding=pendinglist.size();
    	 long Maximum_waiting=0;
    	 long Minimum_waiting=0; 
    	 
    	 for (NumeroRandom novo : finishedlist) {
    		 
    		 if(contador==0) {
    			 Minimum_waiting=(System.currentTimeMillis()-novo.getTimeArrived())/1000;
    			 Maximum_waiting=(System.currentTimeMillis()-novo.getTimeArrived())/1000;
    		 }
    		 
    		  long min=(System.currentTimeMillis()-novo.getTimeArrived())/1000;
    		  long max=(System.currentTimeMillis()-novo.getTimeArrived())/1000;
    		 
    		 if(min<Minimum_waiting) {
    			 Minimum_waiting=min; 
    		 }
    		 
    		 if(max<Maximum_waiting) {
    			 Maximum_waiting=max; 
    		 }
    		 
    		 contador++;
		 }
    	  System.out.println("Maximum waiting time-: " + Maximum_waiting);
         System.out.println("Minimum waiting time" + Minimum_waiting);
         System.out.println("Total pending Requests : " + totalpeding);
        
         return "Maximum waiting time-  "+Maximum_waiting+" Minimum waiting time "+Minimum_waiting+" Total pending Requests "+totalpeding;
    	
    }
	   
   
   
   
    @GET
	@Path("/pending")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<NumeroRandom> list() {
    	
    	
    	 ArrayList<NumeroRandom> finishedlistuppendig = new ArrayList<>();
    	 
    	 for (NumeroRandom novo : pendinglist) {
    		 NumeroRandom newNumber=new NumeroRandom();
    		 
        	 newNumber.setId(novo.getId());
    		 newNumber.setNumber(novo.getNumber());
    		 newNumber.setTimeArrived(novo.getTimeArrived());
    		 newNumber.SetTimeToProcess(novo.getTimeToProcess()/1000);
    		 newNumber.setTotalWaitTime((System.currentTimeMillis()-novo.getTimeArrived())/1000);
    		 finishedlistuppendig.add(newNumber);
		 }
    	 
		 
		return finishedlistuppendig;
	}
   
   
    
    @GET
	@Path("/list")
	@Produces(MediaType.APPLICATION_JSON)
	 public List<Future>  listagem(){	
		
		return  Futurerequests;
		
	 }
	 
	
	
    
	 @PUT
	 @Path("threads")
	 @Produces(MediaType.APPLICATION_JSON)
	 public String sizethread(int size){	
		 
		 if(size>0 && size<11) {
			 sizepoll=size;
			 
			 return "The Thread-pool size has been set to "+size;
			
		 }else {
			 
			 return "Thread-pool size Maximum 10 and Minimum value 1 ,please set correctrly";
		 }
		 
		 
	 }
	 
	 @DelayMe(time = 0)
	  private void TestingDelay(int i) {
	
		 System.out.println("executing the dalay anotation: ");
		 
		
	 }
	 
	 
	 

	class VerificaRequisicao implements Runnable {
	      private final List<Future> requisicoes;
	  
	      VerificaRequisicao(List<Future> requisicoes) {
	            this.requisicoes = requisicoes;
	      }
	  
	      public void run() {
	        //while (true) {
	              int somaTerminadas = 0;
	              int somaCanceladas = 0;
	              int somaEmExecucao = 0;
	              int posicao=0;
	              
	              try {
	                    for (Future f : requisicoes) {
	                    	
	                    	Thread.sleep(10000);
	                    	
	                    	if(!f.isDone() || !f.isCancelled()) {
	                    		//posicao;
	                    	  long startime=treadlist.get(posicao).getTimeArrived();
	                    	  long timeToprocess=treadlist.get(posicao).getTimeToProcess();
	                    	  long now=System.currentTimeMillis();
	                    	  long duracao=now-startime;
	                    		
	                    		if(duracao>timeToprocess) {
	                    			
	                    			f.get(3,TimeUnit.SECONDS);
	                    			//cancel the task th 
	                    			f.cancel(false);
	                    				
	                    		}
	                    		
	                    	}
	                    		
	                    	
	                          if (f.isDone()) {
	                                somaTerminadas++;
	                          } else if (f.isCancelled()) {
	                                somaCanceladas++;
	                          } else if (!f.isDone()) {
	                                somaEmExecucao++;
	                          }
	                          posicao++;
	                          
		                    
	                    }
	                      System.out.println("Terminadas: " + somaTerminadas);
	                       System.out.println("Canceladas: " + somaCanceladas);
	                       System.out.println("Execução: " + somaEmExecucao);
	                       
	                       
	                    } catch (InterruptedException | ExecutionException | TimeoutException  e) {
		                    e.printStackTrace();
		                    log(e.getMessage());
		                    
		              } 
	          }
	}
	
	
	

	
 // method to teste dalay	
 public void delayMe() {
	 StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

		 StackTraceElement element = stackTrace[2];

			 for (Method method : this.getClass().getDeclaredMethods()) {
				 if (method.getName().equals(element.getMethodName())) {
		
				 if (method.isAnnotationPresent(DelayMe.class)) {
					 DelayMe delayMe = method.getAnnotation(DelayMe.class);
					 try {
						 	Thread.sleep(delayMe.time() * 1000);
					 } catch (InterruptedException e) {
						 // TODO Auto-generated catch block
						 e.printStackTrace();
					 }
	
			 }
			 }

		 }

	 }
	
	private static void log(String string) {
		System.out.println(string);
 
	}
	 private static String GetID() {
	    	String condigoUniversal;
	    	UUID uuid = UUID.randomUUID();
			condigoUniversal=uuid.toString();
			return condigoUniversal;
	}
	 
  
	 
}
