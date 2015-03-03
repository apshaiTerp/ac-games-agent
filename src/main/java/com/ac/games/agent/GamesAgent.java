package com.ac.games.agent;

import com.ac.games.agent.thread.BatchBGGGameAgentThread;
import com.ac.games.agent.thread.SingleBGGGameAgentThread;
import com.ac.games.agent.thread.CSIDataAgentThread;
import com.ac.games.agent.thread.MMDataAgentThread;

/**
 * This is the driving Agent class.
 * 
 * @author ac010168
 */
public class GamesAgent {

  public static String serverAddress = "http://localhost:8080/ac-games-restservice-spring-0.1.0-SNAPSHOT";
  
  public final static int FIRST_CSI_ENTRY = 75069;
  
  /**
   * This class should kick off agent threads for {@link SingleBGGGameAgentThread},
   * {@link BatchBGGGameAgentThread}, {@link CSIDataAgentThread}, and {@link MMDataAgentThread} loads.
   * 
   * @param args
   */
  public static void main(String[] args) {
    serverAddress = "http://localhost:8080/ac-games-restservice-spring-0.1.0-SNAPSHOT";
    
    /***************************************************************
    Thread thread1 = new BatchBGGGameAgentThread(501,200000);
    try { 
      thread1.start();
    } catch (Throwable t) {
      t.printStackTrace();
    }
    /***************************************************************
    Thread thread2 = new SingleBGGGameAgentThread(6542,6542);
    try { 
      thread2.start();
    } catch (Throwable t) {
      t.printStackTrace();
    }
    /***************************************************************/
    Thread thread2 = new CSIDataAgentThread(169580,250000);
    try { 
      thread2.start();
    } catch (Throwable t) {
      t.printStackTrace();
    }
    /***************************************************************
    Thread thread3 = new MMDataAgentThread(5500,6000);
    try { 
      thread3.start();
    } catch (Throwable t) {
      t.printStackTrace();
    }
    /***************************************************************/
    
    try {
      //thread3.join();
      thread2.join();
      //thread1.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    System.out.println ("Processing Complete!");
  }

}
