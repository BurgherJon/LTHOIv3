package com.cavellandcavell.leavethehouseoutofit.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.google.appengine.api.utils.SystemProperty;

/** This is used to manage the active user. **/
public class Record
{
    public int league_season_id;
    public int wins;
    public int losses;
    public int pushes;
    public double winnings;

    //This constructor can be used to generate a player's record and winnings in a particular league_season
    //?
    public Record(String uid, int league_season_id)
    {
        this.league_season_id = league_season_id;
        String strquery;
        double on;
        double opp;
        final Logger log = Logger.getLogger(Record.class.getName());

        log.info("Constructing Internal Record Entity.");
        log.info("league season passed: " + league_season_id);
        log.info("id passed: " + uid);


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

            strquery = "Select g.home_line AS home_line, b.home AS home, b.bet_amount AS bet_amount, g.home_score AS home_score, g.away_score AS away_score, g.isfinished AS isFinished FROM Bets b INNER JOIN Games g ON g.game_id = b.game_id INNER JOIN Users u ON u.user_id = b.user_id INNER JOIN firebaseids fid ON u.user_id = fid.user_id WHERE b.league_season_id = " + league_season_id + " AND fid.firebase_uid = '" + uid + "';";

            ResultSet rs = conn.createStatement().executeQuery(strquery);
            if (rs.next()) //Anything in the result set?
            {
                do
                {

                    if (rs.getInt("isFinished") > 0)
                    {

                        //figure out the score with line on each side.
                        if (rs.getInt("home") == 1)
                        {
                            on = rs.getInt("home_score") + rs.getDouble("home_line");
                            opp = rs.getInt("away_score");
                        }
                        else
                        {
                            on = rs.getInt("away_score");
                            opp = rs.getInt("home_score") + rs.getDouble("home_line");
                        }


                        if (on > opp)
                        {
                            this.wins++;
                            this.winnings += rs.getDouble("bet_amount");
                        }
                        else if (on < opp)
                        {
                            this.losses++;
                            this.winnings -= rs.getDouble("bet_amount");
                        }
                        else
                        {
                            this.pushes++;
                        }
                    }
                }
                while (rs.next());
            }
            else //Nothing in the result set.
            {
                log.severe("Nothing in the result set for query.");
                log.severe("Query Executed: " + strquery);
            }


            strquery = "SELECT g.home_line AS home_line, b.home AS home, hb.bet_amount AS bet_amount, g.home_score AS home_score, g.away_score AS away_score, g.isfinished AS isFinished FROM House_Bets hb INNER JOIN Users u ON u.user_id = hb.user_id INNER JOIN Bets b ON b.bet_id = hb.parent_bet_id INNER JOIN Games g ON g.game_id = b.game_id INNER JOIN firebaseids fids ON fids.user_id = u.user_id WHERE b.league_season_id = " + league_season_id + " AND fid.firebase_uid = '" + uid + "';";

            rs = conn.createStatement().executeQuery(strquery);
            if (rs.next()) //Anything in the result set?
            {
                do
                {

                    if (rs.getInt("isFinished") > 0)
                    {

                        //It's the opposite of the bet because it's the house bet.
                        if (rs.getInt("home") == 0)
                        {
                            on = rs.getInt("home_score") + rs.getDouble("home_line");
                            opp = rs.getInt("away_score");
                        }
                        else
                        {
                            on = rs.getInt("away_score");
                            opp = rs.getInt("home_score") + rs.getDouble("home_line");
                        }


                        if (on > opp)
                        {
                            this.winnings += rs.getDouble("bet_amount");
                        }
                        else if (on < opp)
                        {
                            this.winnings -= rs.getDouble("bet_amount");
                        }
                        else
                        {
                            this.pushes++;
                        }
                    }
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
    }
}
