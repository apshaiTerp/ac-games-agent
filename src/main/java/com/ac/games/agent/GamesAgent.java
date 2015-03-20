package com.ac.games.agent;

import com.ac.games.agent.thread.BGGAutoReviewAgentThread;
import com.ac.games.agent.thread.BGGScheduledAgentThread;
import com.ac.games.agent.thread.BatchBGGGameAgentThread;
import com.ac.games.agent.thread.CSIScheduledAgentThread;
import com.ac.games.agent.thread.MMScheduledAgentThread;
import com.ac.games.agent.thread.SingleBGGGameAgentThread;
import com.ac.games.agent.thread.CSIDataAgentThread;
import com.ac.games.agent.thread.MMDataAgentThread;
import com.ac.games.agent.thread.StatsThread;

/**
 * This is the driving Agent class.
 * 
 * @author ac010168
 */
public class GamesAgent {

  public static String serverAddress = "http://localhost:8080/ac-games-restservice-spring-0.2.0-SNAPSHOT";
  
  public final static int FIRST_CSI_ENTRY = 75069;
  
  public final static int FIRST_MM_ENTRY  = 5136;
  /**
   * This class should kick off agent threads for {@link SingleBGGGameAgentThread},
   * {@link BatchBGGGameAgentThread}, {@link CSIDataAgentThread}, and {@link MMDataAgentThread} loads.
   * 
   * @param args
   */
  public static void main(String[] args) {
    serverAddress = "http://localhost:8080/ac-games-restservice-spring-0.2.0-SNAPSHOT";
    //serverAddress = "http://localhost:8080";
    
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
    /***************************************************************
    Thread thread2 = new CSIDataAgentThread(FIRST_CSI_ENTRY,250000);
    try { 
      thread2.start();
    } catch (Throwable t) {
      t.printStackTrace();
    }
    /***************************************************************
    Thread thread3 = new MMDataAgentThread(FIRST_MM_ENTRY,150000);
    try { 
      thread3.start();
    } catch (Throwable t) {
      t.printStackTrace();
    }
    /***************************************************************/
    
    Thread thread1 = new BGGScheduledAgentThread();
    //Thread thread1 = new BatchBGGGameAgentThread(40000, 180000);
    Thread thread2 = new CSIScheduledAgentThread();
    Thread thread3 = new MMScheduledAgentThread();
    
    thread1.start();
    thread2.start();
    thread3.start();
    try {
      thread1.join();
      thread2.join();
      thread3.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    
    Thread thread4 = new BGGAutoReviewAgentThread();
    thread4.start();
    
    try {
      thread4.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    /***************************************************************/

    StatsThread statThread = new StatsThread();
    statThread.start();
    try {
      statThread.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    
    System.out.println ("Processing Complete!");
  }

}
