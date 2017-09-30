package com.cavellandcavell.leavethehouseoutofit.backend;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;
import java.util.ArrayList;

import com.google.appengine.api.utils.SystemProperty;

/** This is used to manage the active user. **/
public class Week
{
    private int number;
    private int season;
    private String short_name;
    private String long_name;
    private String next;
    private String previous;

    private ArrayList<Game> games = new ArrayList<>();
    private Date start;

    //This constructor is for testing purposes and just creates week 3 of the 2015 season.
    public Week()
    {
        final Logger log = Logger.getLogger(League_Season.class.getName());
        log.info("This is the blank constructor for Week... doing this to avoid a java error.");
    }

    public Week(int id, int lsid, int uid, Connection conn)
    {
        String strquery;
        int nextID;
        int prevID;
        final Logger log = Logger.getLogger(League_Season.class.getName());

        log.info("In the constructor for Week with the id passed.");
        log.info("ID passed: " + id);

        try
        {
            strquery = "Select * From weeks WHERE id = '" + id + "';";
            ResultSet rs = conn.createStatement().executeQuery(strquery);
            if (rs.next()) //Anything in the result set?
            {
                this.setLong_Name(rs.getString("name_long"));
                this.setShort_Name(rs.getString("name_short"));
                this.setNumber(rs.getInt("number"));
                this.setSeason(rs.getInt("season"));
                nextID = rs.getInt("id") + 1;
                prevID = rs.getInt("id") - 1;
            }
            else //Nothing in the result set.
            {
                log.severe("Nothing in the result set for query.");
                log.severe("Query Executed: " + strquery);
                return;
            }

            strquery = "Select name_long FROM weeks WHERE id = '" + nextID + "';";
            rs = conn.createStatement().executeQuery(strquery);
            if (rs.next()) //Anything in the result set?
            {
                this.setNext(rs.getString("name_long"));
            }
            else {} //Nothing in the result set.  In which case we don't want to set a value so that the UI can assume there is no next week.

            strquery = "Select name_long FROM weeks WHERE id = '" + prevID + "';";
            rs = conn.createStatement().executeQuery(strquery);
            if (rs.next()) //Anything in the result set?
            {
                this.setPrevious(rs.getString("name_long"));
            }
            else {} //Nothing in the result set.  In which case we don't want to set a value so that the UI can assume there is no next week.

            strquery = "Select game_id FROM games WHERE week_id = " + id + ";";
            rs = conn.createStatement().executeQuery(strquery);
            if (rs.next())
            {
                do
                {
                    this.games.add(new Game(rs.getInt("game_id"), lsid, uid, conn));
                }
                while (rs.next());
            }
            else
            {
                log.severe("No Games in the Week.");
            }


        }
        catch (SQLException e)
        {
            log.severe("SQL Exception processing!");
            log.info(e.getMessage());
        }
    }

    public String getPrevious()
    {
        return previous;
    }

    public void setPrevious(String update)
    {
        previous = update;
    }

    public String getNext()
    {
        return next;
    }

    public void setNext(String update)
    {
        next = update;
    }

    public void setNumber(int update)
    {
        this.number = update;
    }

    public int getSeason()
    {
        return season;
    }

    public void setSeason(int update)
    {
        season = update;
    }

    public Date getStart()
    {
        return start;
    }

    public void setStart(Date update)
    {
        start = update;
    }

    public String getShort_Name()
    {
        return short_name;
    }

    public void setShort_Name(String update)
    {
        short_name = update;
    }

    public String getLong_Name()
    {
        return long_name;
    }

    public void setLong_Name(String update)
    {
        long_name = update;
    }

    public void setGames(ArrayList<Game> update)
    {
        games = update;
    }

    public ArrayList<Game> getGames()
    {
        return games;
    }

}
