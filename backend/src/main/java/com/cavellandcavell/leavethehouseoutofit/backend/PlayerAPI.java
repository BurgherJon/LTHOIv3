/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.cavellandcavell.leavethehouseoutofit.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiIssuer;
import com.google.api.server.spi.config.ApiIssuerAudience;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.Api;

import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Nullable;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.utils.SystemProperty;

import java.util.Calendar;
import java.util.logging.Logger;
import com.google.api.server.spi.response.InternalServerErrorException;
import com.google.api.server.spi.response.UnauthorizedException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
//import javax.xml.bind.DatatypeConverter;
import org.apache.commons.codec.binary.Base64;

import javax.inject.Named;

import java.io.FileReader;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;
import java.util.Date;


import javax.inject.Named;

// This is the API for players to access.
@Api(
        name = "playerapi",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.leavethehouseoutofit.cavellandcavell.com",
                ownerName = "backend.leavethehouseoutofit.cavellandcavell.com",
                packagePath = ""
        )



        /* THIS NEEDS TO BE RENABLED FOR PRODUCTION.... I HAVE DISABLED SO WE CAN PLAY WITH THE API EXPLORER.
        apiKeyRequired = AnnotationBoolean.TRUE
        */
)
public class PlayerAPI {

    /**
     * A simple endpoint method that takes a name and says Hi back
     */
    @ApiMethod(name = "getMe")
    public Me getMe(@Named("firebase_uid") String user_uid) throws InternalServerErrorException {
        final Logger log = Logger.getLogger(PlayerAPI.class.getName());
        log.info("Running the GetMe function");
        Me response = new Me();

        Environment env = new Environment();
        try
        {
            Class.forName(env.db_driver);
        }
        catch (ClassNotFoundException e)
        {
            log.severe("Unable to load database driver.");
            log.severe(e.getMessage());
        }

        Connection conn;

        try
        {
            conn = DriverManager.getConnection(env.db_url, env.db_user, env.db_password);

            response = new Me(user_uid, conn);

            conn.close();
        }
        catch (InternalServerErrorException e)
        {
            throw e;
        }
        catch (SQLException e)
        {
            log.severe("SQL Exception processing!");
            log.severe("Connection String: " + env.db_url + "&" + env.db_user + "&" + env.db_password);
            log.severe(e.getMessage());
        }

        return response;
    }

    /**
     * Add a new user.
     */
    @ApiMethod(name = "newUser")
    public Me newUser(@Named("firebase_uid") String user_uid, @Named("fname") String fname, @Named("lname") String lname, @Named("linitial") String linitial, @Named("email") String email, @Named("invite") String invite) throws InternalServerErrorException
    {
        String strquery;
        final Logger log = Logger.getLogger(PlayerAPI.class.getName());
        int user_id;
        Me response = new Me();

        log.info("Adding a User with fname " + fname + " lname " + lname + " linitial " + linitial + " email " + email + " invite " + invite);

        if (invite.equals("password"))
        {
            Environment env = new Environment();
            try
            {
                Class.forName(env.db_driver);
            }
            catch (ClassNotFoundException e)
            {
                log.severe("Unable to load database driver.");
                log.severe(e.getMessage());
            }

            Connection conn = null;

            try
            {
                conn = DriverManager.getConnection(env.db_url, env.db_user, env.db_password);

                strquery = "INSERT INTO users (email, fname, linitial, lname) VALUES ('" + email + "', '" + fname + "', '" + linitial + "', '" + lname + "');";
                log.info(strquery);
                conn.createStatement().executeUpdate(strquery);

                strquery = "SELECT * FROM users WHERE email = '" + email + "';";
                ResultSet rs = conn.createStatement().executeQuery(strquery);

                if (rs.next())
                {
                    strquery = "INSERT INTO firebaseids (firebase_uid, user_id) VALUES ('" + user_uid + "', " + rs.getInt("user_id") + ");";
                    log.info(strquery);
                    conn.createStatement().executeUpdate(strquery);

                    strquery = "INSERT INTO league_season_user_map (user_id, league_season_id) VALUES (" + rs.getInt("user_id") + ", 8);";
                    log.info(strquery);
                    conn.createStatement().executeUpdate(strquery);
                }
                response = new Me(user_uid, conn);

                conn.close();
            }
            catch (SQLException e)
            {
                log.severe("SQL Exception processing!");
                log.severe("Connection String: " + env.db_url + "&" + env.db_user + "&" + env.db_password);
                log.severe(e.getMessage());
            }
        }

        return response;
    }



    /**
     * An endpoint that takes a user ID and adds a new authentication to it, then returns the user.
     */
    @ApiMethod(name = "newAuth")
    public Me newAuth(@Named("firebase_uid") String user_uid, @Named("user_id") int user_id) throws InternalServerErrorException {
        String strquery;
        final Logger log = Logger.getLogger(PlayerAPI.class.getName());
        Me response = new Me();

        log.info("Pairing Firebase_ID: " + user_uid + "with email address: " + user_id + ".");

        Environment env = new Environment();
        try
        {
            Class.forName(env.db_driver);
        }
        catch (ClassNotFoundException e)
        {
            log.severe("Unable to load database driver.");
            log.severe(e.getMessage());
        }

        Connection conn = null;

        try
        {
            conn = DriverManager.getConnection(env.db_url, env.db_user, env.db_password);

            strquery = "INSERT INTO firebaseids (firebase_uid, user_id) VALUES ('" + user_uid + "', " + user_id + ");";
            conn.createStatement().executeUpdate(strquery);

            response = new Me(user_uid, conn);

            conn.close();
        }
        catch (SQLException e)
        {
            log.severe("SQL Exception processing!");
            log.severe("Connection String: " + env.db_url + "&" + env.db_user + "&" + env.db_password);
            log.severe(e.getMessage());
        }

        return response;
    }

    @ApiMethod(name = "getWeek")
    public Week getWeek(@Named("firebase_uid") String fid, @Named("week") String week, @Named("league_season_id") int lsid)
    {
        String strquery;
        Week response = new Week();
        int user_id = 0;
        final Logger log = Logger.getLogger(PlayerAPI.class.getName());

        log.info("Retreiving Week.  league_season passed is " + lsid + ".  Week passed is " + week + ".");

        Environment env = new Environment();
        try
        {
            Class.forName(env.db_driver);
        }
        catch (ClassNotFoundException e)
        {
            log.severe("Unable to load database driver.");
            log.severe(e.getMessage());
        }

        Connection conn = null;

        try
        {
            conn = DriverManager.getConnection(env.db_url, env.db_user, env.db_password);

            strquery = "Select u.user_id AS uid FROM users u INNER JOIN firebaseids fids ON fids.user_id = u.user_id WHERE fids.firebase_uid = '" + fid + "';";
            ResultSet rs = conn.createStatement().executeQuery(strquery);
            if (rs.next())
            {
                user_id = rs.getInt("uid");
            }
            else
            {
                response.setShort_Name("USER AUTH FAILED");
                return response;
            }

            strquery = "Select w.id AS week_id FROM weeks w RIGHT OUTER JOIN sysinfo si ON w.season = si.CurrentSeason WHERE name_long = '" + week + "';";
            rs = conn.createStatement().executeQuery(strquery);
            if (rs.next()) //Anything in the result set?
            {
                response = new Week(rs.getInt("week_id"), lsid, user_id, conn);
            }
            else //Nothing in the result set.
            {
                log.severe("Nothing in the result set for query.");
                log.severe("Query Executed: " + strquery);
            }

            conn.close();
        }
        catch (SQLException e)
        {
            log.severe("SQL Exception processing!");
            log.severe("Connection String: " + env.db_url + "&" + env.db_user + "&" + env.db_password);
            log.severe(e.getMessage());
        }

        return response;

    }

    @ApiMethod(name = "setBet")
    public Bet setBet(@Named("team_name") String team_name, @Named("league_season_id") int league_season_id, @Named("firebase_uid") String fid, @Named("week") String week)
    {
        Bet response = new Bet();
        String strquery;
        int user_id = 0;
        int home = -1;
        final Logger log = Logger.getLogger(PlayerAPI.class.getName());

        log.info("Setting bet on " + team_name + " for " + fid + " in week " + week + " in lsid: " + league_season_id + ".");

        Environment env = new Environment();
        try
        {
            Class.forName(env.db_driver);
        }
        catch (ClassNotFoundException e)
        {
            log.severe("Unable to load database driver.");
            log.severe(e.getMessage());
        }

        Connection conn = null;

        try
        {
            conn = DriverManager.getConnection(env.db_url, env.db_user, env.db_password);

            //find the user.
            strquery = "Select u.user_id AS uid FROM users u INNER JOIN firebaseids fids ON fids.user_id = u.user_id WHERE fids.firebase_uid = '" + fid + "';";
            ResultSet rs = conn.createStatement().executeQuery(strquery);
            if (rs.next())
            {
                user_id = rs.getInt("uid");
            }
            else
            {
                response.setWeek_Short("USER AUTH FAILED");
                return response;
            }

            //find the game and place the bet (the Bet constructor places the bet).
            strquery = "SELECT g.game_id, at.name AS at, ht.name AS ht FROM games g INNER JOIN weeks w ON w.id = g.week_id INNER JOIN teams ht ON ht.team_id = g.home_team INNER JOIN sysinfo si ON si.CurrentSeason = w.season INNER JOIN teams at ON at.team_id = g.away_team WHERE (ht.name = '" + team_name + "' OR at.name = '" + team_name + "') AND w.name_long = '" + week + "';";
            rs = conn.createStatement().executeQuery(strquery);
            if (rs.next()) //Anything in the result set?
            {
                if (team_name.equals(rs.getString("ht")))
                {
                    home = 1;
                }
                else if (team_name.equals(rs.getString("at")))
                {
                    home = 0;
                }
                response = new Bet(rs.getInt("game_id"), user_id, league_season_id, home, conn);
            }
            else //Nothing in the result set.
            {
                log.severe("Nothing in the result set for query.");
                log.severe("Query Executed: " + strquery);
            }

            conn.close();
        }
        catch (SQLException e)
        {
            log.severe("SQL Exception processing!");
            log.severe("Connection String: " + env.db_url + "&" + env.db_user + "&" + env.db_password);
            log.severe(e.getMessage());
        }

        return response;
    }

    @ApiMethod(name = "deleteBet")
    public void deleteBet (@Named("team_name") String team_name, @Named("league_season_id") int league_season_id, @Named("firebase_uid") String fid, @Named("week") String week)
    {
        String strquery = "";
        final Logger log = Logger.getLogger(PlayerAPI.class.getName());

        log.info("In the deleteBet method.");

        Environment env = new Environment();
        try
        {
            Class.forName(env.db_driver);
        }
        catch (ClassNotFoundException e)
        {
            log.severe("Unable to load database driver.");
            log.severe(e.getMessage());
        }

        Connection conn = null;

        try
        {
            conn = DriverManager.getConnection(env.db_url, env.db_user, env.db_password);

            strquery = "SELECT min(b.bet_id) AS bet_id from bets b INNER JOIN games g ON g.game_id = b.game_id INNER JOIN weeks w ON w.id = g.week_id INNER JOIN firebaseids fuid ON fuid.user_id = b.user_id INNER JOIN teams ht ON g.home_team = ht.team_id INNER JOIN teams at ON g.away_team = at.team_id WHERE fuid.firebase_uid = '" + fid + "' AND b.league_season_id = " + league_season_id + " AND ((ht.name = '" + team_name + "' AND b.home = 1) OR (at.name = '" + team_name + "' AND b.home = 0)) AND w.name_long = '" + week + "';";
            log.info("Finding the bet to remove: " + strquery);
            ResultSet rs = conn.createStatement().executeQuery(strquery);

            if (rs.next())
            {
                strquery = "DELETE FROM bets WHERE bet_id = " + rs.getInt("bet_id") + ";";
                log.info("Deleting Bet: " + strquery);
                conn.createStatement().executeUpdate(strquery);

                strquery = "DELETE FROM house_bets WHERE parent_bet_id = " + rs.getInt("bet_id") + ";";
                log.info("Deleting House Bets: " + strquery);
                conn.createStatement().executeUpdate(strquery);
            }

            conn.close();
        }
        catch (SQLException e)
        {
            log.severe("SQL Exception processing!");
            log.info("Connection String: " + env.db_url + "&" + env.db_user + "&" + env.db_password);
            log.info(e.getMessage());
        }
    }

    @ApiMethod(name = "getPlayers")
    public ArrayList<Player> getPlayers (@Named("league_season_id") int league_season_id)
    {

        ArrayList<Player> response = new ArrayList<Player>();
        String strquery;
        final Logger log = Logger.getLogger(PlayerAPI.class.getName());

        log.info("In the getUsers method.");
        log.info("League Season passed: " + league_season_id);

        Environment env = new Environment();
        try
        {
            Class.forName(env.db_driver);
        }
        catch (ClassNotFoundException e)
        {
            log.severe("Unable to load database driver.");
            log.severe(e.getMessage());
        }

        Connection conn = null;

        try
        {
            conn = DriverManager.getConnection(env.db_url, env.db_user, env.db_password);

            strquery = "Select u.email from users u INNER JOIN league_season_user_map lsum ON lsum.user_id = u.user_id WHERE lsum.league_season_id = " + league_season_id + ";";
            ResultSet rs = conn.createStatement().executeQuery(strquery);
            if (rs.next()) //Anything in the result set?
            {
                do
                {
                    response.add(new Player(rs.getString("email"), league_season_id, conn));
                }
                while (rs.next());
            }
            else //Nothing in the result set.
            {
                log.severe("Nothing in the result set for query.");
                log.severe("Query Executed: " + strquery);
            }

            conn.close();
        }
        catch (SQLException e)
        {
            log.severe("SQL Exception processing!");
            log.info("Connection String: " + env.db_url + "&" + env.db_user + "&" + env.db_password);
            log.info(e.getMessage());
        }


        return response;
    }

    //The sloppy part about this is that there is no transactionality.  If it fails after processing x house bets
    //but before the last one, then the next run of the cronjub will process duplicate house bets.
    @ApiMethod(name = "cronJob")
    public void cronJob ()
    {
        String strquery;
        int updates = 0;
        Bet workingbet;
        final Logger log = Logger.getLogger(PlayerAPI.class.getName());

        log.info("In the Cron Job.");

        Environment env = new Environment();
        try
        {
            Class.forName(env.db_driver);
        }
        catch (ClassNotFoundException e)
        {
            log.severe("Unable to load database driver.");
            log.severe(e.getMessage());
        }

        Connection conn = null;

        try
        {
            conn = DriverManager.getConnection(env.db_url, env.db_user, env.db_password);

            //Query will be updated to determine if there are any games that need to be frozen.
            strquery = "SELECT b.bet_id, b.league_season_id, b.user_id FROM bets b WHERE b.game_id IN (SELECT g.game_id FROM games g WHERE g.START <= NOW() - INTERVAL (SELECT ls.freeze_minutes FROM league_seasons ls WHERE ls.league_season_id = b.league_season_id) MINUTE) AND (b.hbprocessed <> 1 OR b.hbprocessed IS NULL);";
            log.info("Made connection, going to run: " + strquery);
            ResultSet rs = conn.createStatement().executeQuery(strquery);
            if (rs.next()) //Anything in the result set?
            {
                strquery = "UPDATE bets SET hbprocessed = 1 WHERE bet_id IN (";
                log.info("found games to update, stepping through them.");
                do
                {
                    //Generate the house bets
                    log.info("Working with bet: " + rs.getInt("bet_id"));
                    workingbet = new Bet(rs.getInt("bet_id"));
                    workingbet.setLeague_Season_ID(rs.getInt("league_season_id"));
                    workingbet.generatehousebets(rs.getInt("user_id"), conn);

                    //Update the query to mark the house bets processed.
                    if (updates == 0)
                    {
                        strquery = strquery + workingbet.getId();
                    }
                    else
                    {
                        strquery = strquery + ", " + workingbet.getId();
                    }

                    //Note that there is at least one that needs to be updated.
                    updates = 1;
                }
                while (rs.next());

                //Finish the query and run it if there were updates.
                if (updates == 1)
                {
                    strquery = strquery + ");";

                    //Run the update query.
                    conn.createStatement().executeUpdate(strquery);
                }
            }
            else //Nothing in the result set.
            {
                log.info("No house bets necessary.");
                log.info("Query Executed: " + strquery);
            }


            //Ping the service about scores.
            try
            {
                //Figure out the current date in the right format.
                int year = Calendar.getInstance().get(Calendar.YEAR);
                int monthint = Calendar.getInstance().get(Calendar.MONTH) + 1;
                String month = "" + monthint;
                if (monthint < 10)
                {
                    month = "0" + monthint;
                }
                int todayint = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                String day;
                int dayint;



                for (int counter=-1;counter < 2; counter++)
                {
                    //Each time we run, we run for yesterday, today, and tomorrow... just to cover all the bets possibly spilling over.
                    dayint = todayint + counter;
                    day = "" + dayint;
                    if (dayint < 10)
                    {
                        day = "0" + day;
                    }
                    log.info("Running for: " + year + month + day);


                    //Setup the URL Fetch
                    try
                    {
                        URL url = new URL("https://api.mysportsfeeds.com/v1.1/pull/nfl/2017-regular/scoreboard.json?fordate=" + year + month + day /* + "&force=false" */);
                        String encoding = new String(Base64.encodeBase64("BurgherJon:VegasVaca".getBytes()));
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setDoOutput(true);
                        connection.setRequestProperty("Authorization", "Basic " + encoding);
                        InputStream content = (InputStream) connection.getInputStream();
                        BufferedReader in =
                                new BufferedReader(new InputStreamReader(content));
                        String line;


                        //Setup to parse the data.
                        Object obj = new JSONParser().parse(in);
                        JSONObject job = (JSONObject) obj;
                        JSONObject scoreboard = (JSONObject) job.get("scoreboard");
                        JSONArray gamescores = (JSONArray) scoreboard.get("gameScore");
                        JSONObject gamescore;
                        JSONObject hometeam;
                        JSONObject game;
                        String isInProgress;
                        String isCompleted;
                        String homeScore;
                        String awayScore;
                        String msf_id;
                        String homemsfID;
                        int minutes_remaining;
                        int isFinished;
                        int home_id;
                        for (int index = 0; index < gamescores.size(); index++) {
                            gamescore = (JSONObject) gamescores.get(index);
                            game = (JSONObject) gamescore.get("game");

                            isInProgress = (String)gamescore.get("isInProgress");
                            isCompleted = (String)gamescore.get("isCompleted");
                            homeScore = (String)gamescore.get("homeScore");
                            awayScore = (String)gamescore.get("awayScore");
                            hometeam = (JSONObject) game.get("homeTeam");
                            homemsfID = (String)hometeam.get("ID");

                            log.info("isInProgress = " + isInProgress + " ||| isCompleted = " + isCompleted + " ||| homeScore = " + homeScore + " ||| awayScore = " + awayScore + " ||| homemsfID = " + homemsfID);


                            //check if the game is in progress and, if it is, stipulate that the minutes remaining in the game will be set to 1.
                            if (isInProgress.equals("true") || ((!(homeScore.equals("0"))) || (!(awayScore.equals("0"))))) {
                                minutes_remaining = 1;
                            } else {
                                minutes_remaining = 0;
                            }

                            //check if the game has ended and, if it has, stipulate that the isFinished will be 1.
                            if (isCompleted.equals("true")) {
                                isFinished = 1;
                            } else {
                                isFinished = 0;
                            }

                            //Retrieve my id value for the home team (as a way to verify that the games haven't gotten messed up on their system.

                            strquery = "SELECT team_id FROM teams WHERE msf_id = " + homemsfID + ";";
                            rs = conn.createStatement().executeQuery(strquery);
                            if (rs.next()) {
                                home_id = rs.getInt("team_id");
                            } else {
                                log.severe("Something isn't right... the homeTeam in a retreived score is not found!!!");
                                throw new Exception("Error trying to update scores!");
                            }


                            //Query for updating the score in the system.
                            strquery = "UPDATE games SET home_score = " + homeScore + ", away_score = " + awayScore + ", isFinished = " + isFinished + ", mins_remaining = " + minutes_remaining + " WHERE (msf_id = " + game.get("ID") + " AND home_team = " + home_id + ");";
                            log.info("Updating a score: " + strquery);
                            conn.createStatement().executeUpdate(strquery);
                        }
                    }
                    catch(Exception e)
                    {
                        log.info("Exception with the call, was probably just a lack of an update.  Continuing in case the next one works.");
                        log.info(e.getMessage());
                    }
                }



            }
            catch(Exception e)
            {
                e.printStackTrace();
            }







            conn.close();
        }
        catch (SQLException e)
        {
            log.severe("SQL Exception processing!");
            log.info("Connection String: " + env.db_url + "&" + env.db_user + "&" + env.db_password);
            log.severe(e.getMessage());
        }

    }

    /**
     * A simple method that can be hit to test the system is live.
     */
    @ApiMethod(name = "healthcheck")
    public Health getStatus()
    {
        return new Health();
    }

}
