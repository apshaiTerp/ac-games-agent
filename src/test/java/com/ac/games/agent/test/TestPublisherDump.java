package com.ac.games.agent.test;

import static org.junit.Assert.assertTrue;

import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.ac.games.agent.data.PreferredPublisherList;
import com.ac.games.data.BGGGame;
import com.ac.games.data.ReviewState;
import com.ac.games.db.mongo.BGGGameConverter;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

/**
 * @author ac010168
 *
 */
public class TestPublisherDump {

  @Test
  public void testPubDump() {
    System.out.println ("Starting the pub dump test");
    
    MongoClient client = null;
    try {
      client = new MongoClient("localhost", 27017);
    } catch (UnknownHostException e) {
      e.printStackTrace();
      return;
    }
    
    DB mongoDB = client.getDB("livedb");
    DBCollection bggCollection   = mongoDB.getCollection("bgggame");
    
    //Categories of invalid of junk criteria
    //Base game with no player data
    //db.bgggame.find( { reviewState: 0, minPlayers: {$exists:false}} )
    BasicDBObject queryObject = new BasicDBObject();
    queryObject.append("reviewState", 0);

    BasicDBList parentList = new BasicDBList();
    parentList.add(0);
    parentList.add(2);
    
    queryObject = new BasicDBObject();
    queryObject.append("gameType", new BasicDBObject("$in", parentList));
    queryObject.append("publishers", new BasicDBObject("$exists", true));
    queryObject.append("publishers.1", new BasicDBObject("$exists", true));
    
    System.out.println ("Running the following Query:\n  db.bgggame.find( "  + queryObject.toString() + " )");
     
    DBCursor cursor = bggCollection.find(queryObject);
    List<BGGGame> games = new LinkedList<BGGGame>();
    while (cursor.hasNext()) {
      DBObject object = cursor.next();
      BGGGame game = BGGGameConverter.convertMongoToGame(object);
      games.add(game);
    }
    try { cursor.close(); } catch (Throwable t) { /** Ignore Errors */ }
    
    //At this point, we need to start figuring out what more we need to figure it out.
    int noPreferredHits      = 0;
    int onePreferredHit      = 0;
    int twoPlusPreferredHits = 0;
    int onlyAvoidPublished   = 0;
    int solvedWithAvoidHits  = 0;
    
    List<String> preferredList = PreferredPublisherList.getTrustedPublishers();
    
    for (BGGGame game : games) {
      List<String> publishers = game.getPublishers();
      
      System.out.println ("Processing " + game.getName() + " (" + game.getBggID() + ")");
      System.out.println ("  Size of publishers list before scanning for avoided publishers: " + publishers.size());
      
      boolean isSelfPublished = false;
      boolean isWebPublished  = false;
      boolean isUnpublished   = false;
      boolean isUnknown       = false;
      
      if (publishers.contains("(Self-published)")) {
        isSelfPublished = true;
        publishers.remove("(Self-published)");
      }
      if (publishers.contains("(Web published)")) {
        isWebPublished = true;
        publishers.remove("(Web published)");
      }
      if (publishers.contains("(Unpublished)")) {
        isUnpublished = true;
        publishers.remove("(Unpublished)");
      }
      if (publishers.contains("(Unknown)")) {
        isUnknown = true;
        publishers.remove("(Unknown)");
      }

      System.out.println ("  Size of publishers list after scanning for avoided publishers: " + publishers.size());
      if (isSelfPublished) System.out.println ("    This game is self published!");
      if (isWebPublished)  System.out.println ("    This game is web published!");
      if (isUnpublished)   System.out.println ("    This game is unpublished!");
      if (isUnknown)       System.out.println ("    This game is unknown!");

      if (publishers.size() == 0) {
        System.out.println ("!" + game.getName() + " has no only avoided publishers");
        onlyAvoidPublished++;
        continue;
      }
      if (publishers.size() == 1) {
        System.out.println ("#" + game.getName() + " has just one publisher left after removing avoided ones!");
        solvedWithAvoidHits++;
        continue;
      }
      
      int foundHits = 0;
      for (String publisher : publishers) {
        if (preferredList.contains(publisher)) {
          foundHits++;
        }
      }
      
      if ((foundHits == 0) && (publishers.size() > 1))
        noPreferredHits++;
      if ((foundHits == 1) && (publishers.size() > 1))
        onePreferredHit++;
      if (foundHits >= 2)
        twoPlusPreferredHits++;
      
      if (foundHits == 0) {
        System.out.println ("-" + game.getName() + " has no preferred publishers");
        System.out.print ("|");
        for (String publisher : publishers) {
          System.out.print (" " + publisher + " | ");
        }
        System.out.println ();
      }
      
      if (foundHits == 1) {
        System.out.println ("*" + game.getName() + " found one preferred publisher in the list of publishers!");
      }
      
      if (foundHits >= 2) {
        System.out.println ("+" + game.getName() + " has " + foundHits + " preferred publishers");
        for (String publisher : publishers) {
          if (preferredList.contains(publisher)) {
            System.out.println ("  " + publisher);
          }
        }
      }
    }
    
    System.out.println ("Total Number of Games Found: " + games.size());
    System.out.println ("  Total with Only Avoided Publishers:    " + onlyAvoidPublished);
    System.out.println ("  Total solved by removing Avoided Pubs: " + solvedWithAvoidHits);
    System.out.println ("  Total with One Preferred Hit:          " + onePreferredHit);
    System.out.println ("  Total with No Preferred Hits:          " + noPreferredHits);
    System.out.println ("  Total with Two Or More Preferred Hits: " + twoPlusPreferredHits);

    client.close();
    
    assertTrue("The world did not end", true);
  }
}

class PublisherData implements Comparable<PublisherData> {

  private String publisherName;
  private int    gameHits;
  
  /**
   * @return the publisherName
   */
  public String getPublisherName() {
    return publisherName;
  }

  /**
   * @param publisherName the publisherName to set
   */
  public void setPublisherName(String publisherName) {
    this.publisherName = publisherName;
  }

  /**
   * @return the gameHits
   */
  public int getGameHits() {
    return gameHits;
  }

  /**
   * @param gameHits the gameHits to set
   */
  public void setGameHits(int gameHits) {
    this.gameHits = gameHits;
  }

  @Override
  public int compareTo(PublisherData data) {
    return gameHits - data.getGameHits();
  }
}
