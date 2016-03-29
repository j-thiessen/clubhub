package utilities;
import java.awt.Component;
/****************************************************************************************************
* Project: ClubHub
* Author(s): A. Dicks-Stephen, B. Lamaa, J. Thiessen
* Student Number: 100563954, 100911472, 100898311
* Date: Feb 07, 2016
* Description: PostDao - prepares a database access object for the game model
****************************************************************************************************/
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.JOptionPane;

import model.Game;
import model.Post;
import model.Season;
import model.Slot;
import model.User;
import utilities.DatabaseAccess;
import utilities.ValidationUtilities;

public class GameDao {
	private Connection connect = null;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;
	
	
	public GameDao() {
		try {
			connect = DatabaseAccess.connectDataBase();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean isInDatabase(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    try {
			String id = request.getParameter("id");
			statement = connect.createStatement();
			resultSet = statement.executeQuery("select * from ch_season where id = \"" + id + "\""); 
			// if there result set is before the first item, there are entries
			// if it is not, there are not
			if (!resultSet.isBeforeFirst() ) {    
				return false;
			} else {
				return true;
			}
	    } catch (Exception e) {
	    	throw e;
	    } 
	}
	
	public void addToDatabase(HttpServletRequest request, HttpServletResponse response, String seasonID) throws Exception {
		try {
			HttpSession session = request.getSession();
			
			
			System.out.println("I'm in addToDatabase in GameDao");
			
			System.out.println("The season Id is: " + seasonID);
			
			// First we need to check how many games are in the current season we have passed in. 
			// We also need to get the start date because we have to add 7 days to it everytime we go through the loop
			
			statement = connect.createStatement();
			ResultSet results = statement.executeQuery("select * from ch_season where id = " + seasonID); 
			//ResultSet startDate  = sttmnt.executeQuery("select startDate from ch_season where id = " + seasonID);
			
			while(results.next()){
				String str = results.getString("duration");
				int games = Integer.parseInt(str);
					
				System.out.println("Number of games: " + games);
				
				String date = results.getString("startDate");
				
				String str1 = results.getString("dayOfWeek");
				int dayOfWeek = Integer.parseInt(str1);
				
				String str2 = results.getString("startTime");
				int time = Integer.parseInt(str2);
				
				String gender = results.getString("gender");
				
			
			
			// Next we need to create a loop in order to create as many games as we need for the current season 
			System.out.println("The current number of games in this season is " + games);
			
			int cnt=0;
			int week = 0;
			String scheduledDate;
			
			Date theDate;
			
			
			
			do {
				cnt++;
				
				week++;
				
				PreparedStatement preparedStatement = connect.prepareStatement("insert into ch_game values (default, ?, ?, ?)");
				preparedStatement.setInt(1, week);	//week of game
				preparedStatement.setString(2, date); // date of game
				preparedStatement.setString(3, seasonID);
				preparedStatement.executeUpdate();
				
				statement = connect.createStatement();
				ResultSet rs = statement.executeQuery("SELECT LAST_INSERT_ID();");
			     
				String gameID = null;
			      
			      while(rs.next()){
			    	  gameID = rs.getString("LAST_INSERT_ID()");
			    	  System.out.println("The current game ID is: " + gameID);
			      }
				
				PreparedStatement preparedStatement2 = connect.prepareStatement("insert into ch_slot values (default, ?, ?, ?, ?, ?, ?, null, ?)");
				preparedStatement2.setInt(1, dayOfWeek );	//week of game
				preparedStatement2.setInt(2, time); // date of game
				preparedStatement2.setInt(3, week);
				preparedStatement2.setString(4, gender);
				preparedStatement2.setInt(5, 1);
				preparedStatement2.setString(6, date);
				preparedStatement2.setString(7, gameID);
				preparedStatement2.executeUpdate();
				
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Calendar c = Calendar.getInstance();
				c.setTime(sdf.parse(date));
				c.add(Calendar.DATE, 7);  // number of days to add
				date = sdf.format(c.getTime()); 
				
				
			}while (cnt < games);
			}
		      
		      
		      
		    }catch (Exception e) {
			      throw e;
		    }

	}

	/*public void listAll(HttpServletRequest request) throws Exception {
		  List<Game> games = new ArrayList<Game>();
		  
		  	try{  		
		  		statement = connect.createStatement();
			    resultSet = statement.executeQuery("SELECT year, season, "
			    		+ "gender, startDate, startTime, "
			    		+ "dayOfWeek, duration" 
				+ "FROM clubhub.ch_season");
			      
			    while (resultSet.next()) {
			    	  Season season = new Season();
			    	  season.setYear(resultSet.getString("year"));
			    	  season.setSeason(resultSet.getString("season"));
			    	  season.setId(resultSet.getString("id"));
			    	  season.setGender(resultSet.getString("gender"));
			    	  season.setStartDate(resultSet.getString("startDate"));
			    	  season.setStartTime(resultSet.getString("startTime"));
			    	  season.setDayOfWeek(resultSet.getString("dayOfWeek"));
			    	  season.setDuration(resultSet.getString("duration"));
			    	  
			    	  request.setAttribute("seasonID", season.getId());
			    	  seasons.add(season);
			    }
		    } catch (SQLException e) {
			      throw e;
			}
		  	request.setAttribute("seasons", seasons);
	} 
	/*
	public void deleteSeason(HttpServletRequest request, HttpServletResponse response, String seasonID) throws Exception {
		  try {
			  statement = connect.createStatement();
			  statement.executeUpdate("delete from ch_season where id =" + seasonID); 
			  
		  } catch (SQLException e) {
		      throw e;
		  }
	}
	
	public void batchDelete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String [] markedForDeletion = request.getParameterValues("seasonsSelected");
		for (String x : markedForDeletion) {
			deleteSeason(request, response, x);
		}		
	}
	*/
	
	public int getDuration(HttpServletRequest request, String seasonID) throws Exception {
		statement = connect.createStatement();
	    resultSet = statement.executeQuery("SELECT * FROM ch_season WHERE id= " + seasonID );
	    
	    String str = resultSet.getString("duration");
	    int duration = Integer.parseInt(str);
	    
	    System.out.println("I'm in getDuration, the current duration is: "+ duration);
	    
	    return duration;
	}
	
	
	public void findGameSet(HttpServletRequest request, int seasonID) throws Exception{
		 List<Game> games = new ArrayList<Game>();
		  	try{  		
		  		statement = connect.createStatement();
			    resultSet = statement.executeQuery("SELECT week, scheduledDate, seasonID "
				+ "FROM ch_game WHERE seasonID= " + seasonID);
			      
			    while (resultSet.next()) {
			    	
			    	  Game game = new Game();
			    	  game.setWeek(resultSet.getString("week"));
			    	  game.setScheduledDate(resultSet.getString("scheduledDate"));
			    	  game.setSeasonId(resultSet.getString("seasonID"));
			    	  
			    	  request.setAttribute("gameID", game.getId());
			    	  games.add(game);
			    }
		    } catch (SQLException e) {
			      throw e;
			}
		  	request.setAttribute("games", games);
	}
	
	
	public void findGames(HttpServletRequest request, String gameID) throws Exception {
		  Game game = new Game();
		  	try{
			    statement = connect.createStatement();
			    resultSet = statement.executeQuery("SELECT * FROM ch_game WHERE id= " + gameID );
			    
			    
			    while (resultSet.next()) {
			    	  game.setWeek(resultSet.getString("week"));
			    	  game.setScheduledDate(resultSet.getString("scheduledDate"));
			    	  game.setSeasonId(resultSet.getString("seasonId"));
			    	  game.setId(resultSet.getString("id"));
			    	  
			    	  
			    	  
			}} catch (SQLException e) {
			      throw e;
			}
		  	request.setAttribute("game", game);
	} 
	
	public void playersAvailable(HttpServletRequest request, String userID, String slotIDs) throws Exception{
		
		String [] slots = slotIDs.split(",");
		try{
			statement = connect.createStatement();
			
			for (int i = 0; i < slots.length; i++) { 
				String slotID = slots[i];
				ResultSet results = statement.executeQuery("select * from ch_slot where id = " + slotID); 
				while(results.next()){
					String availablePlayers = results.getString("availablePlayers");
					if(availablePlayers != null && availablePlayers.contains(userID)){
						System.out.println("User already exists in current slot");
						}else {
							
							if(availablePlayers != null){
								String theIDs = availablePlayers +", "+ userID;
								System.out.println(userID);
								PreparedStatement preparedStatement = connect.prepareStatement("UPDATE ch_slot SET availablePlayers = ? WHERE id= " + slotID);
								preparedStatement.setString(1, theIDs);
								preparedStatement.executeUpdate();
							}else{
								PreparedStatement preparedStatement = connect.prepareStatement("UPDATE ch_slot SET availablePlayers = ? WHERE id= " + slotID);
								System.out.println(userID);
								preparedStatement.setString(1, userID);
								preparedStatement.executeUpdate();
							}
							
						}
					}
				}
			
		}catch(SQLException e) {
		      throw e;
		}
	}
	
public void closeSlot(HttpServletRequest request, String playerIDs) throws Exception{
		
		String [] players = playerIDs.split(", ");
		List<Integer> indexes = new ArrayList<Integer>();
		//String [] indexes = new String[4];
		int hold1,hold2,hold3,hold4;
		hold1 = new Random().nextInt(players.length);
		hold2 = new Random().nextInt(players.length);
		hold3 = new Random().nextInt(players.length);
		hold4 = new Random().nextInt(players.length);
		
		do{
			if(hold1==hold2 ||hold1 == hold3 || hold1 == hold4){
				hold1 = new Random().nextInt(players.length);
			}else{
				indexes.add(0,hold1);
				if(hold2==hold3||hold2==hold4){
					hold2 = new Random().nextInt(players.length);
				}
				else{
					indexes.add(1,hold2);
					if(hold3==hold4){
						hold3 = new Random().nextInt(players.length);
					}else{
						indexes.add(2,hold3);
						indexes.add(3,hold4);
					}
				}
			}
		}while(indexes.size()<4);
		
		
		String [] playingPlayers = new String[4];
		playingPlayers[0] = players[indexes.get(0)];
		playingPlayers[1] = players[indexes.get(1)];
		playingPlayers[2] = players[indexes.get(2)];
		playingPlayers[3] = players[indexes.get(3)];
		
		StringBuilder builder = new StringBuilder();
		
		if (playingPlayers.length >= 1) {
			builder.append(playingPlayers[0]);
		}

		for (int i = 1; i < playingPlayers.length; i++) { 
			builder.append(",");
			builder.append(playingPlayers[i]);
		}
		
		String thePlayers = builder.toString();
		System.out.println(thePlayers);
		
		
	}
	
	
	public void findOpenGameSlots(HttpServletRequest request, int userID) throws Exception {
		  //Game game = new Game();
		List<Slot> slots = new ArrayList<Slot>();
		  	try{
		  		
			    statement = connect.createStatement();
			    resultSet = statement.executeQuery("SELECT * FROM ch_user WHERE id= " + userID );
			    //String userGender = null;
			    resultSet.next();
			    String userGender = resultSet.getString("gender");
			    
			    ResultSet resultSet2;
			    resultSet2 = statement.executeQuery("Select * from ch_slot where gender= \""+ userGender +"\" And status= 1");
			    
			    while (resultSet2.next()) {
			    	int num = resultSet2.getInt("dayOfWeek");
			    	int givenTime = resultSet2.getInt("time");
			    	String dayOfWeek = utilities.ValidationUtilities.numberToDay(request,num);
			    	String time = utilities.ValidationUtilities.toTime(request,givenTime);
			    	
			    	
			    	//System.out.println("The day of week is: "+ dayOfWeek);
			    	Slot slot = new Slot();
			    	slot.setDayOfWeek(dayOfWeek);
			    	slot.setTime(time);
			    	slot.setScheduledDate(resultSet2.getString("scheduledDate"));
			    	slot.setId(resultSet2.getString("id"));    	  
			    	request.setAttribute("slotID", slot.getId());
			    	  slots.add(slot);
			    	
			}} catch (SQLException e) {
			      throw e;
			}
		  	request.setAttribute("slots", slots);
	} 
	/*
	public void editSeason(HttpServletRequest request, HttpServletResponse response, String _seasonID) throws Exception {
	    try {			
			String seasonID = request.getParameter("seasonID");
		    String year = request.getParameter("year");	// year
		    String season = request.getParameter("season"); // season
		    String gender = request.getParameter("gender"); // gender
		    String startDate = request.getParameter("startDate"); // StartDate
		    String startTime = request.getParameter("startTime"); // startTime
		    String dayOfWeek = request.getParameter("dayOfWeek"); // DOW
		    String duration = request.getParameter("duration"); // duration
		    
		    
	      
			statement = connect.createStatement();
			statement.executeUpdate("UPDATE ch_season SET year='" + year + "', season='" + season + "', gender='" + gender + 
					"', startDate='" + startDate + "', startTime='" + startTime + "' dayOfWeek='" + dayOfWeek + "'duration='" 
					+ duration + "' WHERE id='" + seasonID + "'");
	      
	      //preparedStatement.executeUpdate();
	    } catch (Exception e) {
	      throw e;
	    }
	}*/

}