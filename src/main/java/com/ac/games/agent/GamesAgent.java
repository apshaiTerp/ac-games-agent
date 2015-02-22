package com.ac.games.agent;

import com.ac.games.agent.thread.BGGGameAgentThread;

/**
 * This is the driving Agent class.
 * 
 * @author ac010168
 */
public class GamesAgent {

  public static String serverAddress = "http://localhost:8080";
  
  /**
   * @param args
   */
  public static void main(String[] args) {
    serverAddress = "http://localhost:8080";
    
    Thread thread = new BGGGameAgentThread(1,100);
    try { 
      thread.run();
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

}
