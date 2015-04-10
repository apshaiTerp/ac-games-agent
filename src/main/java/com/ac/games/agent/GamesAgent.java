package com.ac.games.agent;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.ac.games.agent.thread.BGGAutoReviewAgentThread;
import com.ac.games.agent.thread.BGGScheduledAgentThread;
import com.ac.games.agent.thread.BatchBGGGameAgentThread;
import com.ac.games.agent.thread.BatchBGGGameUpdateAgentThread;
import com.ac.games.agent.thread.CSIAutoReviewAgentThread;
import com.ac.games.agent.thread.CSIDataUpdateAgentThread;
import com.ac.games.agent.thread.CSIScheduledAgentThread;
import com.ac.games.agent.thread.MMAutoReviewAgentThread;
import com.ac.games.agent.thread.MMDataUpdateAgentThread;
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
    serverAddress = "http://localhost:8080/ac-games-restservice-spring-0.3.0-SNAPSHOT";
    //serverAddress = "http://localhost:8080";
    
    ScheduledThreadPoolExecutor mainTaskPool = new ScheduledThreadPoolExecutor(1);
    ScheduledThreadPoolExecutor subTaskPool  = new ScheduledThreadPoolExecutor(1);
    
    //Set the Check for new stuff tasks to run every three hours
    mainTaskPool.scheduleAtFixedRate(new BGGScheduledAgentThread(), 0, 3, TimeUnit.HOURS);
    mainTaskPool.scheduleAtFixedRate(new CSIScheduledAgentThread(), 0, 3, TimeUnit.HOURS);
    mainTaskPool.scheduleAtFixedRate(new MMScheduledAgentThread(), 0, 3, TimeUnit.HOURS);
    
    //These guys are a little more expensive, so only run them every 48 hours
    //mainTaskPool.scheduleAtFixedRate(new BatchBGGGameUpdateAgentThread(), 1, 48, TimeUnit.HOURS);
    //mainTaskPool.scheduleAtFixedRate(new CSIDataUpdateAgentThread(), 1, 48, TimeUnit.HOURS);
    //mainTaskPool.scheduleAtFixedRate(new MMDataUpdateAgentThread(), 1, 48, TimeUnit.HOURS);
    
    //Schedule the stats thread to collect stats every 30 minutes or so
    subTaskPool.scheduleAtFixedRate(new StatsThread(), 0, 30, TimeUnit.MINUTES);
    //Schedule the Auto-Review jobs to run every 6 hours
    subTaskPool.scheduleAtFixedRate(new CSIAutoReviewAgentThread(), 1, 360, TimeUnit.MINUTES);
    subTaskPool.scheduleAtFixedRate(new MMAutoReviewAgentThread(),  1, 360, TimeUnit.MINUTES);
    subTaskPool.scheduleAtFixedRate(new BGGAutoReviewAgentThread(), 1, 360, TimeUnit.MINUTES);
    
    while (true) {
      try {
        Thread.sleep(10000);
      } catch (Throwable t) { break; }
    }
    System.out.println ("Processing Complete!");
  }

}
