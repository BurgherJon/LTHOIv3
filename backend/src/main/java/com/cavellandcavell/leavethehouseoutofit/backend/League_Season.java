package com.cavellandcavell.leavethehouseoutofit.backend;

import java.io.*;
import java.sql.*;
import java.util.logging.Logger;

import com.google.appengine.api.utils.SystemProperty;


public class League_Season
{
    private int league_season_id;
    private int season;
    private String league_name;
    private int num_players;
    private int position;
    private int wins;
    private int losses;
    private int pushes;
    private double winnings;

    public League_Season(int user_id, int league_season_id)
    {
        String strquery;
        final Logger log = Logger.getLogger(League_Season.class.getName());

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

            strquery = "SELECT ls.league_name, ls.season, ls.league_season_id From lthoidb.league_seasons ls WHERE ls.league_season_id = " + league_season_id + ";";
            ResultSet rs = conn.createStatement().executeQuery(strquery);
            if (rs.next()) //Anything in the result set?
            {
                this.setLeague_Season_ID(rs.getInt("league_season_id"));
                this.league_name = rs.getString("league_name");
                this.season = rs.getInt("season");

                //For Right Now, I'm only getting the league name and season... not anything else... so filling in obvious blanks for the rest
                this.num_players = -1;
                this.position = -1;
                this.wins = -1;
                this.losses = -1;
                this.pushes = -1;
                this.winnings = -1;
            }
            else //Nothing in the result set.
            {
                log.warning("Nothing in the result set for query.  For League_Seasons... Executed: " + strquery);
            }

            conn.close();
        }
        catch (SQLException e)
        {
            log.severe("SQL Exception on connection!");
            log.severe("Connection String: " + env.db_url + "&" + env.db_user + "&" + env.db_password);
            log.severe(e.getMessage());
        }
    }

    public int getLeague_Season_ID()
    {
        return league_season_id;
    }

    public void setLeague_Season_ID(int update)
    {
        league_season_id = update;
    }

    public int getPosition()
    {
        return position;
    }

    public void setPosition(int update)
    {
        position = update;
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

    public String getLeague_Name()
    {
        return league_name;
    }

    public void setLeague_Name(String update)
    {
        league_name = update;
    }

    public double getWinnings()
    {
        return winnings;
    }

    public void setWinnings(double update)
    {
        winnings = update;
    }

    public int getSeason()
    {
        return season;
    }

    public void setSeason(int update)
    {
        season = update;
    }

    public int getNum_Players()
    {
        return num_players;
    }

    public void setNum_Players(int update)
    {
        season = num_players;
    }
}
