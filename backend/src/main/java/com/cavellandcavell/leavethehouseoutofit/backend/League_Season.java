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

    public League_Season(String user_id, int league_season_id, Connection conn)
    {
        String strquery;
        final Logger log = Logger.getLogger(League_Season.class.getName());
        log.info("In the league Season constructor for " + league_season_id);

        try {
            strquery = "SELECT ls.league_name, ls.season, ls.league_season_id From league_seasons ls WHERE ls.league_season_id = " + league_season_id + ";";
            ResultSet rs = conn.createStatement().executeQuery(strquery);
            if (rs.next()) //Anything in the result set?
            {
                this.setLeague_Season_ID(rs.getInt("league_season_id"));
                this.league_name = rs.getString("league_name");
                this.season = rs.getInt("season");

                //This method retreives the number of players from the database based on an already set league_season_id.
                retrieveNum_Players(conn);

                //retrieve a record for the league_season and store it.
                Record tempRecord = new Record(user_id, this.getLeague_Season_ID(), conn);
                this.wins = tempRecord.wins;
                this.losses = tempRecord.losses;
                this.pushes = tempRecord.pushes;
                this.winnings = tempRecord.winnings;

                //This still doesn't work.
                this.position = -1;
            } else //Nothing in the result set.
            {
                log.warning("Nothing in the result set for query.  For League_Seasons... Executed: " + strquery);
            }

        }
        catch (SQLException e)
        {
            log.severe("SQL Exception on connection!");
            log.severe(e.getMessage());
        }
        log.info("Finished setting up League Season: " + this.getLeague_Name());
    }

    public void retrieveNum_Players(Connection conn)
    {
        String strquery;
        final Logger log = Logger.getLogger(League_Season.class.getName());

        try
        {
            strquery = "SELECT COUNT(*) as num_players FROM league_season_user_map lsum WHERE lsum.league_season_id = " + league_season_id + ";";
            log.info("Counting players: " + strquery);
            ResultSet rs = conn.createStatement().executeQuery(strquery);
            if (rs.next()) //Anything in the result set?
            {
                this.num_players = rs.getInt("num_players");
            }
            else //Nothing in the result set.
            {
                log.warning("Nothing in the result set for query.  For League_Seasons... Executed: " + strquery);
            }

        }
        catch (SQLException e)
        {
            log.severe("SQL Exception on connection!");
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
