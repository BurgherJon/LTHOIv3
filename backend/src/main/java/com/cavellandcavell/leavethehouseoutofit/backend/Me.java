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
    private String wlong;
    private String wshort;
    private String email;
    private String fname;
    private String lname;
    private String linitial;
    private int wins;
    private int losses;
    private int pushes;
    private double winnings;
    private ArrayList<League_Season> leagues;

    public Me()
    {

    }


    public Me(String firebase_uid, Connection conn) throws InternalServerErrorException
    {
        String strquery;
        final Logger log = Logger.getLogger(Me.class.getName());

        log.info("In the constructor for Me with the firebase_uid passed.");
        log.info("Firebase ID: " + firebase_uid);

        try
        {
            this.winnings = 0;
            this.losses = 0;
            this.wins = 0;
            this.pushes = 0;


            strquery = "Select u.user_id AS user_id, u.email AS email, u.fname AS fname, u.linitial AS linitial, u.lname AS lname, lsum.league_season_id AS league_season_id FROM lthoidb.users u INNER JOIN lthoidb.league_season_user_map lsum ON u.user_id = lsum.user_id INNER JOIN lthoidb.firebaseids fb ON u.user_id = fb.user_id INNER JOIN league_seasons ls ON ls.league_season_id = lsum.league_season_id INNER JOIN lthoidb.sysinfo si ON ls.season = si.CurrentSeason WHERE fb.firebase_uid = '" + firebase_uid + "';";
            ResultSet rs = conn.createStatement().executeQuery(strquery);
            if (rs.next()) //Anything in the result set?
            {
                this.setFname(rs.getString("fname"));
                this.setLname(rs.getString("lname"));
                this.setLinitial(rs.getString("Linitial"));
                this.setEmail(rs.getString("email"));


                leagues = new ArrayList<League_Season>();
                League_Season thisls;
                do
                {
                    thisls = new League_Season(firebase_uid, rs.getInt("league_season_id"), conn);
                    leagues.add(thisls);

                    //Eventually this should be adding up wins and losses as we go.
                    this.losses += thisls.getLosses();
                    this.wins += thisls.getWins();
                    this.pushes += thisls.getPushes();
                    this.winnings += thisls.getWinnings();

                } while (rs.next());


            }
            else //Nothing in the result set.
            {
                strquery = "Select * from firebaseids fb WHERE fb.firebase_uid = '" + firebase_uid + "';";
                rs = conn.createStatement().executeQuery(strquery);

                if (rs.next())
                {
                    log.severe("The FirebaseID does exist, but isn't any leagues: " + firebase_uid + " throwing exception!");
                    throw new InternalServerErrorException("NO LEAGUES");
                }
                else
                {
                    log.severe("The FirebaseID does not exist: " + firebase_uid + " throwing exception!");
                    throw new InternalServerErrorException("NO USER");
                }
            }

            strquery = "Select w.name_short as name_short, w.name_long as name_long FROM weeks w INNER JOIN sysinfo si ON si.CurrentWeek = w.id;";
            rs = conn.createStatement().executeQuery(strquery);
            if (rs.next()) //Anything in the result set?
            {
                this.wlong = rs.getString("name_short");
                this.wshort = rs.getString("name_long");
            }
            else //Nothing in the result set.
            {
                log.severe("WTF!!! We didn't find the current week?");
            }

            conn.close();
        }
        catch (InternalServerErrorException e)
        {
            throw e;
        }
        catch (SQLException e)
        {
            log.severe("SQL Exception processing!");
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

    public String getWlong()
    {
        return wlong;
    }

    public void setWlong(String update)
    {
        wlong = update;
    }

    public String getWshort()
    {
        return wshort;
    }

    public void setWshort(String update)
    {
        wshort = update;
    }

}
