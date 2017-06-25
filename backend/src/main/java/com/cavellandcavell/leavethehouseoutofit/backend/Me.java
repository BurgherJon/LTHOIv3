package com.cavellandcavell.leavethehouseoutofit.backend;

/**
 * Created by Jonat on 6/24/2017.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

import com.google.api.server.spi.response.InternalServerErrorException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.utils.SystemProperty;

public class Me
{
    private String email;
    private String fname;
    private String lname;
    private String linitial;
    private int wins;
    private int losses;
    private int pushes;
    private double winnings;
    private ArrayList<League_Season> leagues;

    //This constructor is for testing purposes and just creates the user with the agreed to test data (see TestHarnessScenario.doc)
    public Me(String firebase_uid) throws InternalServerErrorException
    {
        String strquery;
        final Logger log = Logger.getLogger(Me.class.getName());

        log.info("In the constructor for Me with the firebase_uid passed.");
        log.info("Firebase ID: " + firebase_uid);

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

            strquery = "Select u.user_id AS user_id, u.email AS email, u.fname AS fname, u.linitial AS linitial, u.lname AS lname, lsum.league_season_id AS league_season_id FROM lthoidb.Users u INNER JOIN lthoidb.League_Season_User_Map lsum ON u.user_id = lsum.user_id INNER JOIN lthoidb.firebaseids fb ON u.user_id = fb.user_id INNER JOIN league_seasons ls ON ls.league_season_id = lsum.league_season_id INNER JOIN lthoidb.sysinfo si ON ls.season = si.CurrentSeason WHERE fb.firebase_uid = '" + firebase_uid + "';";
            ResultSet rs = conn.createStatement().executeQuery(strquery);
            if (rs.next()) //Anything in the result set?
            {
                this.setFname(rs.getString("fname"));
                this.setLname(rs.getString("lname"));
                this.setLinitial(rs.getString("Linitial"));
                this.setEmail(rs.getString("email"));

                leagues = new ArrayList<League_Season>();
                do
                {
                    leagues.add(new League_Season(rs.getInt("user_id"), rs.getInt("league_season_id")));


                    //Eventually this should be adding up wins and losses as we go.
                    this.losses = -2;
                    this.wins = -1;
                    this.pushes = -1;
                    this.winnings = -1.0;

                } while (rs.next());

            }
            else //Nothing in the result set.
            {
                log.severe("We didn't find the firebaseid: " + firebase_uid + " throwing exception!");
                throw new InternalServerErrorException("Firebase UID not recognized.");
            }

            conn.close();
        }
        catch (SQLException e)
        {
            log.severe("SQL Exception processing!");
            log.severe("Connection String: " + env.db_url + "&" + env.db_user + "&" + env.db_password);
            log.severe(e.getMessage());
        }

    }

    public ArrayList<League_Season> getLeagues() {
        return leagues;
    }

    public void setLeagues(ArrayList<League_Season> leagues) {
        this.leagues = leagues;
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

    public String getLname()
    {
        return lname;
    }

    public void setLname(String update)
    {
        lname = update;
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
        winnings = update;
    }

}
