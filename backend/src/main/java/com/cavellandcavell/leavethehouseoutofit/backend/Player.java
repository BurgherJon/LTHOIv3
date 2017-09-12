package com.cavellandcavell.leavethehouseoutofit.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.google.appengine.api.utils.SystemProperty;

/** This is used to manage the active user. **/
public class Player
{
    private int league_season_id;
    private String email;
    private String fname;
    private String linitial;
    private int wins;
    private int losses;
    private int pushes;
    private double winnings;


    //This constructor takes the email and the league_season_id and returns relevant information.
    public Player(String email, int league_season_id, Connection conn)
    {
        String strquery;
        final Logger log = Logger.getLogger(Player.class.getName());

        log.info("In the constructor for User.");
        log.info("Email passed: " + email);
        log.info("League Season passed: " + league_season_id);

        this.setEmail(email);
        this.setLeague_season_id(league_season_id);

        try
        {
            strquery = "Select * From users WHERE email = '" + email + "';";
            ResultSet rs = conn.createStatement().executeQuery(strquery);
            if (rs.next()) //Anything in the result set?
            {
                this.setFname(rs.getString("fname"));
                this.setLinitial(rs.getString("linitial"));
            }
            else //Nothing in the result set.
            {
                log.severe("Nothing in the result set for query.");
                log.severe("Query Executed: " + strquery);
            }

            Record load = new Record(email, league_season_id, conn);
            this.wins = load.wins;
            this.losses = load.losses;
            this.pushes = load.pushes;
            this.winnings = load.winnings;
        }
        catch (SQLException e)
        {
            log.severe("SQL Exception processing!");
            log.info(e.getMessage());
        }
    }

    public Player(int user_id, int league_season_id, Connection conn)
    {
        String strquery;
        String fid = "";
        final Logger log = Logger.getLogger(Player.class.getName());

        log.info("In the constructor for User that's based on user_id.");
        log.info("User ID passed: " + user_id);
        log.info("League Season passed: " + league_season_id);

        this.setLeague_season_id(league_season_id);

        try
        {
            strquery = "SELECT * FROM users u INNER JOIN firebaseids fid ON fid.user_id = u.user_id WHERE u.user_id =" + user_id + ";";
            ResultSet rs = conn.createStatement().executeQuery(strquery);
            if (rs.next()) //Anything in the result set?
            {
                this.setFname(rs.getString("fname"));
                this.setLinitial(rs.getString("linitial"));
                this.setEmail(rs.getString("email"));
                //TODO: I need to add optionality to not have to pass the firebase id... It's sloppy, but I don't want have to build a new record constructor.
                fid = rs.getString("firebase_uid");
            }
            else //Nothing in the result set.
            {
                log.severe("Nothing in the result set for query.");
                log.severe("Query Executed: " + strquery);
            }

            Record load = new Record(fid, league_season_id, conn);
            this.wins = load.wins;
            this.losses = load.losses;
            this.pushes = load.pushes;
            this.winnings = load.winnings;
        }
        catch (SQLException e)
        {
            log.severe("SQL Exception processing!");
            log.info(e.getMessage());
        }
    }

    public int getLeague_season_id()
    {
        return league_season_id;
    }

    public void setLeague_season_id(int update)
    {
        this.league_season_id = update;
    }

    public int getPushes()
    {
        return pushes;
    }

    public void setPushes(int update)
    {
        pushes = update;
    }

    public int getLosses()
    {
        return losses;
    }

    public void setLosses(int update)
    {
        losses = update;
    }

    public int getWins()
    {
        return wins;
    }

    public void setWins(int update)
    {
        wins = update;
    }

    public String getLinitial()
    {
        return linitial;
    }

    public void setLinitial(String update)
    {
        linitial = update;
    }

    public String getFname()
    {
        return fname;
    }

    public void setFname(String update)
    {
        fname = update;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String update)
    {
        email = update;
    }

    public double getWinnings()
    {
        return winnings;
    }

    public void setWinnings(double update)
    {
        this.winnings = update;
    }
}
