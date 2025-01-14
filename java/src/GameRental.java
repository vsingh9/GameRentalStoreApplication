/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;
import java.util.Scanner;
import java.io.IOException;
import java.sql.Timestamp;  
import java.util.Random;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class GameRental {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   // global variables (to use in different functions)
   static Scanner scanner = new Scanner(System.in);
   static ResultSet rsRole;
   static String inputLogin = "";
   static String inputPassword = "";
   static String favGames;
   static String inputRole = "";
   static String inputPhoneNumber= "";
   static int numOverdueGames;

   /**
    * Creates a new instance of GameRental store
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public GameRental(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end GameRental

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
     if(outputHeader){
      for(int i = 1; i <= numCol; i++){
      System.out.print(rsmd.getColumnName(i) + "\t");
      }
      System.out.println();
      outputHeader = false;
     }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
        List<String> record = new ArrayList<String>();
    for (int i=1; i<=numCol; ++i)
      record.add(rs.getString (i));
        result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       while (rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
  Statement stmt = this._connection.createStatement ();

  ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
  if (rs.next())
    return rs.getInt(1);
  return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            GameRental.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      GameRental esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the GameRental object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new GameRental (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. View Profile");
                System.out.println("2. Update Profile");
                System.out.println("3. View Catalog");
                System.out.println("4. Place Rental Order");
                System.out.println("5. View Full Rental Order History");
                System.out.println("6. View Past 5 Rental Orders");
                System.out.println("7. View Rental Order Information");
                System.out.println("8. View Tracking Information");

                //the following functionalities basically used by employees & managers
                System.out.println("9. Update Tracking Information");

                //the following functionalities basically used by managers
                System.out.println("10. Update Catalog");
                System.out.println("11. Update User");

                System.out.println(".........................");
                System.out.println("20. Log out");
                switch (readChoice()){
                   case 1: viewProfile(esql); break;
                   case 2: updateProfile(esql); break;
                   case 3: viewCatalog(esql, scanner); break;
                   case 4: placeOrder(esql); break;
                   case 5: viewAllOrders(esql); break;
                   case 6: viewRecentOrders(esql); break;
                   case 7: viewOrderInfo(esql); break;
                   case 8: viewTrackingInfo(esql); break;
                   case 9: updateTrackingInfo(esql); break;
                   case 10: updateCatalog(esql, scanner); break;
                   case 11: updateUser(esql); break;



                   case 20: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user
    **/

  //new user registration
  public static void CreateUser(GameRental esql){
   try{
       System.out.print("\tEnter login: ");
       inputLogin = in.readLine();

       System.out.print("\tEnter password: ");
       inputPassword = in.readLine();

       System.out.print("\tEnter role: ");
       inputRole = in.readLine();

       System.out.print("\tEnter phone number: ");
       inputPhoneNumber= in.readLine();

       favGames = "";
       numOverdueGames = 0;

       String query = "INSERT INTO Users (login, password, role, favGames, phoneNum, numOverDueGames) VALUES (\'" + inputLogin + "\', \'" + inputPassword + "\', \'" + inputRole + "\', \'" + favGames + "\', \'" + inputPhoneNumber + "\'," + numOverdueGames + ");";

       esql.executeUpdate(query);
       // System.out.println ("total row(s): " + rowCount); //debug statement
    }catch(Exception e){
       System.out.println ("error in insert");
       System.err.println (e.getMessage());
    }
   }//end CreateUser


   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   //user login/logout
 /**
 * Check log in credentials for an existing user
 * @return User login or null if the user does not exist
 **/
public static String LogIn(GameRental esql) {
    try {
        System.out.print("\tWhat is your login?: ");
        String userLogin = in.readLine();

        System.out.print("\tWhat is your password?: ");
        String userPassword = in.readLine();

        String query = "SELECT * FROM Users WHERE login = \'" + userLogin + "\' AND password = \'" + userPassword + "\';";
        List<List<String>> result = esql.executeQueryAndReturnResult(query);

        if (result.size() > 0) {
            inputLogin = userLogin;
            inputPassword = userPassword;
            favGames = result.get(0).get(3); // assuming favGames is the 4th column in the Users table
            inputPhoneNumber = result.get(0).get(4); // assuming phoneNum is the 5th column in the Users table
            numOverdueGames = Integer.parseInt(result.get(0).get(5)); // assuming numOverDueGames is the 6th column in the Users table
            inputRole = result.get(0).get(2); // assuming role is the 3rd column in the Users table
            System.out.println("Logged in as " + inputRole);
            return userLogin;
        } else {
            System.out.println("Invalid login or password!");
        }
    } catch (Exception e) {
        System.err.println(e.getMessage());
    }
    return null;
}

   //browse catalog games
public static void viewCatalog(GameRental esql, Scanner scanner) {
    try {
        int userInput = 0;

        while (userInput != 3) {
            System.out.print("\tChoose an option below:\n");
            System.out.print("\t1. Filter by genre\n");
            System.out.print("\t2. Filter by price\n");
            System.out.print("\t3. Exit\n");

            userInput = Integer.parseInt(in.readLine());

            // Sort by genre
            if (userInput == 1) {
                System.out.print("\tEnter the genre you want to see: ");
                String genre = in.readLine();

                String query = "SELECT * FROM Catalog C WHERE C.genre = \'" + genre + "\';";
                esql.executeQueryAndPrintResult(query);
            }
            // Sort by price
            else if (userInput == 2) {
                System.out.print("\tEnter the maximum price you want to see: ");
                double price = scanner.nextDouble();

                System.out.print("\tDo you want to sort by ascending (1) or descending (2) price?: ");
                int AscDescInput = Integer.parseInt(in.readLine());

                if (AscDescInput == 1) {
                    String query = "SELECT * FROM Catalog C WHERE C.price < " + price + " ORDER BY price ASC";
                    esql.executeQueryAndPrintResult(query);
                } else if (AscDescInput == 2) {
                    String query = "SELECT * FROM Catalog C WHERE C.price < " + price + " ORDER BY price DESC";
                    esql.executeQueryAndPrintResult(query);
                }
            }
        }
    } catch (Exception e) {
        System.err.println(e.getMessage());
    }
}

   //profile (any user can view)
   public static void viewProfile(GameRental esql) {
      try{        
        int userInput = 0;

        while (userInput != 4) {
          System.out.print("\tChoose an option below:");
          System.out.print("\t1. View Favorite Games");
          System.out.print("\t2. View Num Overdue Games");
          System.out.print("\t3. View Phone Number");
          System.out.print("\t4. Exit");

          userInput = Integer.parseInt(in.readLine());

          if (userInput == 1) {
            System.out.print("\tMy Favorite Games: ");
            System.out.print(favGames);
          }
          else if (userInput == 2) {
            System.out.print("\tMy Num Overdue Games: ");
            System.out.print(numOverdueGames);
          }
          else if (userInput == 3) {
            System.out.print("\tMy Phone Number: ");
            System.out.print(inputPhoneNumber);
          }
        }
     }catch(Exception e){
         System.err.println (e.getMessage());
     }
   }

   //profile (only things customer can update)
   public static void updateProfile(GameRental esql) {
      try{
        System.out.print("\tWhat is your login?: "); 
        String userLogin = in.readLine();

        int userInput = 0;

        while (userInput != 4) {
          System.out.print("\tChoose an option below:");
          System.out.print("\t1. Update Favorite Games");
          System.out.print("\t2. Change Password");
          System.out.print("\t3. Change Phone Number");
          System.out.print("\t4. Exit");

          userInput = Integer.parseInt(in.readLine());

          if (userInput == 1) {
            System.out.print("\tWhat would you like to add to your favorite games?: ");
            String newFavGame = in.readLine();
            favGames = favGames + ", " + newFavGame;

            String query = "UPDATE Users SET favGames = \'" + favGames + "\' WHERE login = \'" + userLogin + "\';";
            esql.executeUpdate(query);
          }
          else if (userInput == 2) {
            System.out.print("\tChange Password! What do you want your new password to be?: ");
            inputPassword = in.readLine();

            String query = "UPDATE Users SET password = \'" + inputPassword + "\' WHERE login = \'" + userLogin + "\';";
            esql.executeUpdate(query);
          }
          else if (userInput == 3) {
            System.out.print("\tChange Phone Number! What do you want your new phone number to be?: ");
            inputPhoneNumber = in.readLine();

            String query = "UPDATE Users SET phoneNum = \'" + inputPhoneNumber + "\' WHERE login = \'" + userLogin + "\';";
            esql.executeUpdate(query);
          }
       }
     }catch(Exception e){
         System.err.println (e.getMessage());
     }
   }

   //manager (user info only manager can update)
   public static void updateUser(GameRental esql) {
      try{ 
        System.out.print("\tWhat is your login?: "); 
        String userLogin = in.readLine();

        //manager capabilities
        if (inputRole.equals("manager")) {
          System.out.print("\tWhich user do you want to change (enter login)?: ");
          String changeUser = in.readLine();

          int userInput = 0;

          while (userInput != 4) {
            System.out.print("\tChoose an option below:");
            System.out.print("\t1. Edit User Login");
            System.out.print("\t2. Edit User Role");
            System.out.print("\t3. Edit User Num Overdue Games");
            System.out.print("\t4. Exit");

            userInput = Integer.parseInt(in.readLine());

            if (userInput == 1) {
              System.out.print("\tWhat would you like the new login for user " + changeUser + " to be?: ");
              String newLogin = in.readLine();

              String query = "UPDATE Users SET login = \'" + newLogin + "\' WHERE login = \'" + changeUser + "\';";
              esql.executeUpdate(query);
            }
            else if (userInput == 2) {
              System.out.print("\tWhat do you want the new role for user " + changeUser + " to be?: ");
              String newRole = in.readLine();

              String query = "UPDATE Users SET role = \'" + newRole + "\' WHERE login = \'" + changeUser + "\';";
              esql.executeUpdate(query);
            }
            else if (userInput == 3) {
               System.out.print("\tChange number of overdue games to: ");
               int newGames = Integer.parseInt(in.readLine());

               String query = "UPDATE Users SET numOverDueGames = " + newGames + " WHERE login = \'" + changeUser + "\';";
               esql.executeUpdate(query);
            }
          }
        }
     }catch(Exception e){
         System.err.println (e.getMessage());
     }
   }

   //place rental order
   public static void placeOrder(GameRental esql) {
     try{
       int userInput = 0;

       while (userInput != 2) {
         System.out.print("\tChoose an option below:");
         System.out.print("\t1. Rent A Game");
         System.out.print("\t2. Exit");

         userInput = Integer.parseInt(in.readLine());

         if (userInput == 1) {
           System.out.print("\tInput the game ID of the game you want to order: ");
           String gameID = in.readLine();

           System.out.print("\tInput how many units of " + gameID + " you want: ");
           int numUnits = Integer.parseInt(in.readLine());

           //get total price of rental order
           String pQuery = "SELECT price FROM Catalog WHERE gameID = \'" + gameID + "\';";
           List<List<String>> priceQuery = esql.executeQueryAndReturnResult(pQuery);
           
           double dPrice = Double.parseDouble(priceQuery.get(0).get(0));
           double totalPrice = numUnits * dPrice;
           System.out.print("\tTotal Price of Rental Order: " + totalPrice);
           
           //get rental order ID
           Random rand = new Random();
           int orderIDNum = rand.nextInt(1000);
					 String orderIDString = Integer.toString(orderIDNum);
           String finalOrderID = "gamerentalorder" + orderIDString;
           
           //get tracking ID
           int trackingIDNum = rand.nextInt(1000);
					 String trackerIDString = Integer.toString(trackingIDNum);
           String finalTrackingID = "trackingid" + trackerIDString;
          
           //get timestamp
           Timestamp instant = new Timestamp(System.currentTimeMillis());
           LocalDateTime dateNextWeek = LocalDateTime.now().plusWeeks(1);
           DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
           String formattedDateNextWeek = dateNextWeek.format(formatter);

           // insert into RentalOrderTable
           String query = "INSERT INTO RentalOrder (rentalOrderID, login, noOfGames, totalPrice, orderTimestamp, dueDate) VALUES (\'" + finalOrderID + "\', \'" + inputLogin + "\', " + numUnits + ", " + totalPrice + ", \'" + instant + "\', \'" + formattedDateNextWeek + "\');";
           esql.executeUpdate(query);
           
           // insert into GamesInOrder
           String query2 = "INSERT INTO GamesInOrder (rentalOrderID, gameID, unitsOrdered) VALUES (\'" + finalOrderID + "\', \'" + gameID + "\', " + numUnits + ");";
           esql.executeUpdate(query2);

           // insert into TrackingInfo
           String query3 = "INSERT INTO TrackingInfo (trackingID, rentalOrderID, status, currentLocation, courierName, lastUpdateDate) " + "VALUES ('" + finalTrackingID + "', '" + finalOrderID + "', 'Pending', 'Warehouse', 'FedEx', '" + instant + "')";
           esql.executeUpdate(query3);
         }
       }
     }catch(Exception e){
         System.err.println(e.getMessage());
     }
   }

  //update game information (manager only)
  public static void updateCatalog(GameRental esql, Scanner scanner) {
    try{
      if (inputRole.equals("manager")) {
        int userInput = 0;

        while (userInput != 5) {
          System.out.print("\tWhat is the game ID of the game you want to update?: ");
          int changedGameID = Integer.parseInt(in.readLine());

          System.out.print("\tChoose an option below:");
          System.out.print("\t1. Update Game Name");
          System.out.print("\t2. Update Game Genre");
          System.out.print("\t3. Update Game Price");
          System.out.print("\t4. Update Game Description");
          System.out.print("\t5. Exit");

          userInput = Integer.parseInt(in.readLine());

          if (userInput == 1) {
            System.out.print("\tWhat would you like to change the game name to?: ");
            String newGameName = in.readLine();

            String query = "UPDATE Catalog SET gameName = \'" + newGameName + "\' WHERE gameID = " + changedGameID + ";";
            esql.executeUpdate(query);
          }
          else if (userInput == 2) {
            System.out.print("\tWhat would you like to change the genre to?: ");
            String newGenre = in.readLine();

            String query = "UPDATE Catalog SET genre = \'" + newGenre + "\' WHERE gameID = " + changedGameID + ";";
            esql.executeUpdate(query);
          }
          else if (userInput == 3) {
            System.out.print("\tWhat would you like to change the price to?: ");
            double newPrice = scanner.nextDouble();

            String query = "UPDATE Catalog SET price = " + newPrice + " WHERE gameID = " + changedGameID + ";";
            esql.executeUpdate(query);
          }
          else if (userInput == 4) {
            System.out.print("\tWhat would you like to change the description to?: ");
            String newDescription = in.readLine();

            String query = "UPDATE Catalog SET description = \'" + newDescription + "\' WHERE gameID = " + changedGameID + ";";
            esql.executeUpdate(query);
          }
         }
      } 
     }catch(Exception e){
         System.err.println (e.getMessage());
     }  
  }

  // see rental history
  public static void viewAllOrders(GameRental esql) {
      try {
          System.out.print("\tWhat is your login?: ");
          String login = in.readLine();

          String query = "SELECT rentalOrderID FROM RentalOrder WHERE login = \'" + login + "\';";
          esql.executeQueryAndPrintResult(query);

      } catch (Exception e) {
          System.err.println(e.getMessage());
      }
  }


  // see recent 5 orders
  public static void viewRecentOrders(GameRental esql) {
      try {
          System.out.print("\tWhat is your login?: ");
          String login = in.readLine();

          String query = "SELECT rentalOrderID FROM RentalOrder WHERE login = \'" + login + "\' ORDER BY orderTimestamp DESC LIMIT 5;";
          esql.executeQueryAndPrintResult(query);

      } catch (Exception e) {
          System.err.println(e.getMessage());
      }
  }

  // lookup specific rental order
  public static void viewOrderInfo(GameRental esql) {
      try {
          System.out.print("\tWhat is your login?: ");
          String login = in.readLine();

          System.out.print("\tWhat is the rental order ID of the order you want to view?: ");
          String orderID = in.readLine();

          System.out.print("\tHere is the information for the corresponding rental order:\n");

          String query = "SELECT orderTimestamp, dueDate, totalPrice, rentalOrderID, noOfGames FROM RentalOrder WHERE login = \'" + login + "\' AND rentalOrderID = \'" + orderID + "\';";
          esql.executeQueryAndPrintResult(query);

      } catch (Exception e) {
          System.err.println(e.getMessage());
      }
  }

  // view tracking information
  public static void viewTrackingInfo(GameRental esql) {
      try {
          System.out.print("\tWhat is your login?: ");
          String login = in.readLine();

          System.out.print("\tWhat is the tracking ID you want to view?: ");
          String trackingID = in.readLine();

          System.out.print("\tHere is the information for the corresponding tracking info:\n");

          String query = "SELECT courierName, rentalOrderID, currentLocation, status, lastUpdateDate, additionalComments FROM TrackingInfo WHERE trackingID = \'" + trackingID + "\';";
          esql.executeQueryAndPrintResult(query);

      } catch (Exception e) {
          System.err.println(e.getMessage());
      }
  }

    //update tracking info
    public static void updateTrackingInfo(GameRental esql) {
      try{
       if (inputRole.equals("employee") || inputRole.equals("manager")) {
         int userInput = 0;

          while (userInput != 5) {
            System.out.print("\tWhat is the tracking ID of the tracking info you'd like to update?: ");
            String trackID = in.readLine();

            System.out.print("\tChoose an option below:");
            System.out.print("\t1. Update Status");
            System.out.print("\t2. Update Current Location");
            System.out.print("\t3. Update Courier Name");
            System.out.print("\t4. Update Additional Comments");
            System.out.print("\t5. Exit");

            userInput = Integer.parseInt(in.readLine());

            if (userInput == 1) {
              System.out.print("\tWhat would you like to update the status to?: ");
              String newStatus = in.readLine();

              String query = "UPDATE TrackingInfo SET status = \'" + newStatus + "\' WHERE trackingID = \'" + trackID + "\';";
              esql.executeUpdate(query);
            }
            else if (userInput == 2) {
              System.out.print("\tWhat would you like to update the current location to?: ");
              String newLoc = in.readLine();

              String query = "UPDATE TrackingInfo SET currentLocation = \'" + newLoc + "\' WHERE trackingID = \'" + trackID + "\';";
              esql.executeUpdate(query);
            }
            else if (userInput == 3) {
              System.out.print("\tWhat would you like to update the courier name to?: ");
              String cName = in.readLine();

              String query = "UPDATE TrackingInfo SET courierName = \'" + cName + "\' WHERE trackingID = \'" + trackID + "\';";
              esql.executeUpdate(query);
            }
            else if (userInput == 4) {
              System.out.print("\tWhat would you like to update the additional comments to?: ");
              String addComments = in.readLine();

              String query = "UPDATE TrackingInfo SET additionalComments = \'" + addComments + "\' WHERE trackingID = \'" + trackID + "\';";
              esql.executeUpdate(query);
            }
          }
       }
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
    }

}//end GameRental

