package com.cavellandcavell.leavethehouseoutofit.backend;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

/** This is used to manage the active user. **/
public class Bet
{
    private int id;
    private int user_id;
    private String picked_team;
    private String picked_city;
    private String against_team;
    private String against_city;
    private int week_number;
    private String week_short;
    private String week_long;
    private int league_season_id;
    private double line;
    private int isHouseBet;
    private String result;
    private Date start;

    //Blank constructor used for building bets iteratively.
    public Bet()
    {

    }


    //This constructor is for creating an entirely new bet.  It will add to the database.
    public Bet(int game_id, int user_id, int lsid, int home, Connection conn)
    {
        //Variables will be used to build the insert query.
        double bet_amount = 0;
        String strquery;
        Date freeze;
        int bets_per_game = 0;
        final Logger log = Logger.getLogger(Bet.class.getName());

        log.info("Creating the bet.");
        log.info("league season passed: " + lsid);
        log.info("user passed: " + user_id);
        log.info("home team: " + home);

        try
        {
            //Ensure that that it isn't too late to bet in the game.
            strquery = "SELECT g.start, ls.freeze_minutes, ls.bet_amount AS ba, ls.bets_per_game FROM games g, league_seasons ls WHERE g.game_id = " + game_id + " AND ls.league_season_id = " + lsid + ";";

            log.info("Getting bet and league info: " + strquery);
            ResultSet rs = conn.createStatement().executeQuery(strquery);
            if (rs.next())
            {
                freeze = rs.getTimestamp("start");
                freeze = new Date(freeze.getTime() - rs.getInt("freeze_minutes") * 1000);
                log.info("Freeze Date: " + freeze.toString());

                if (freeze.before(new Date()))
                {
                    log.severe("The game was frozen.");
                    this.setWeek_Short("The game was frozen!");
                    return;
                }

                //Putting away league data for later.
                bets_per_game = rs.getInt("bets_per_game");
                bet_amount = rs.getDouble("ba");
            }
            else //Nothing in the result set.
            {
                log.severe("Nothing in the result set for query.");
                log.severe("Query Executed: " + strquery);
            }



            //Check if the player already has a bet on the other team, if so... get rid of it.
            strquery = "SELECT b.bet_id FROM bets b WHERE b.game_id = " + game_id + " AND b.home <> " + home + " AND b.user_id = " + user_id + " AND b.league_season_id = " + lsid + ";";
            log.info("Getting bets on the other side: " + strquery);
            rs = conn.createStatement().executeQuery(strquery);
            if (rs.next())
            {
                log.info("Found bet on the other team, removing it/them.");

                do
                {
                    strquery = "DELETE FROM bets WHERE bet_id = " + rs.getInt("bet_id") + ";";
                    log.info("Deleting Bet: " + strquery);
                    conn.createStatement().executeUpdate(strquery);
                }
                while (rs.next());
            }
            else //Nothing in the result set.
            {
                log.info("No bet on the other team.");
            }


            //Check if the player already has too many bets on that side of the game.
            strquery = "SELECT b.bet_id FROM bets b WHERE b.game_id = " + game_id + " AND b.home = " + home + " AND b.user_id = " + user_id + " AND b.league_season_id = " + lsid + ";";
            log.info("Getting bets already placed: " + strquery);
            rs = conn.createStatement().executeQuery(strquery);
            int betsmade = 0;
            if (rs.next())
            {
                do
                {
                    betsmade++;
                }
                while (rs.next());
            }
            else //Nothing in the result set.
            {
                log.info("No bets yet on this team.");
            }

            if (betsmade>=bets_per_game)
            {
                log.info("Too many bets on this game, no bet placed.");

                return;
            }




            //Check if the player already has too many bets for the week.
            strquery = "SELECT ls.bets_per_week FROM lthoidb.league_seasons ls WHERE ls.league_season_id = " + lsid + ";";
            log.info("Getting nummer of bets allowed in week: " + strquery);
            rs = conn.createStatement().executeQuery(strquery);
            int betsallowed = 0;
            if (rs.next())
            {
                betsallowed = rs.getInt("bets_per_week");
            }
            else //Nothing in the result set.
            {
                log.info("Couldn't find the league_season");
                return;
            }
            if (betsallowed != 0) {
                strquery = "SELECT COUNT(*) AS bets FROM lthoidb.bets b WHERE b.game_id IN (SELECT g.game_id FROM lthoidb.games g WHERE g.week_id IN (SELECT g.week_id FROM lthoidb.games g WHERE g.game_id = " + game_id + ")) AND b.league_season_id = " + lsid + " AND b.user_id = " + user_id + ";";
                log.info("Getting number of bets made this week: " + strquery);
                rs = conn.createStatement().executeQuery(strquery);
                if (rs.next())
                {
                    betsmade = rs.getInt("bets");
                    if (betsmade >= betsallowed)
                    {
                        log.info("Too many bets made this week to place another one.");
                        return;
                    }
                }
                else //Nothing in the result set.
                {
                    log.info("Couldn't find the league_season or user or week");
                    return;
                }
            }




            //Place the bet in the database.
            strquery = "INSERT INTO lthoidb.bets(user_id, game_id, league_season_id, home, bet_amount, hbprocessed) VALUES (" + user_id + ", " + game_id + ", " + lsid + ", " + home + ", " + bet_amount + ", 0);";
            log.info("Placing Bet: " + strquery);
            conn.createStatement().executeUpdate(strquery);



            //Setup the bet object
            strquery = "SELECT max(b.bet_id) AS bet_id, b.league_season_id AS league_season_id, g.start AS start, w.number AS week_number, w.name_short AS week_short, w.name_long AS week_long, ht.name AS home_team, at.name AS away_team, ht.city AS home_city, at.city AS away_city, g.home_line AS home_line FROM bets b INNER JOIN games g ON b.game_id = g.game_id INNER JOIN weeks w ON g.week_id = w.id INNER JOIN teams ht ON ht.team_id = g.home_team INNER JOIN teams at ON at.team_id = g.away_team WHERE b.game_id = " + game_id + " AND b.user_id = " + user_id + " AND b.league_season_id = " + lsid + ";";
            log.info("Finding Data: " + strquery);
            rs = conn.createStatement().executeQuery(strquery);
            if (rs.next()) //Anything in the result set?
            {
                this.id = rs.getInt("bet_id");
                this.isHouseBet = 0;
                this.league_season_id = rs.getInt("league_season_id");
                this.result = "Good Luck!";
                this.start = rs.getDate("start");
                this.week_long = rs.getString("week_long");
                this.week_short = rs.getString("week_short");
                this.week_number = rs.getInt("week_number");
                if (home == 1)
                {
                    this.picked_team = rs.getString("home_team");
                    this.picked_city = rs.getString("home_city");
                    this.against_city = rs.getString("away_city");
                    this.against_team = rs.getString("away_team");
                    this.line = rs.getDouble("home_line");
                }
                else
                {
                    this.picked_team = rs.getString("away_team");
                    this.picked_city = rs.getString("away_city");
                    this.against_city = rs.getString("home_city");
                    this.against_team = rs.getString("home_team");
                    this.line = -1 * rs.getDouble("home_line");
                }
            }
            else //Nothing in the result set.
            {
                log.severe("Nothing in the result set for query.");
                log.severe("Query Executed: " + strquery);
            }

        }
        catch (SQLException e)
        {
            log.severe("SQL Exception processing!");
            log.info(e.getMessage());
        }

    }

    //Note that this constructor will NOT fetch information from the database... you must populate it.
    public Bet(int id)
    {
        this.id = id;
    }

    public void generatehousebets(int usernum, Connection conn)
    {
        final Logger log = Logger.getLogger(Bet.class.getName());

        String strurl = "";
        String struser = "";
        String strpass = "";
        String strquery;
        int index = 0;
        double bet_amount = 0.0;
        ArrayList<Integer> players = new ArrayList<Integer>();

        log.info("Generating House Bets for " + this.id);

        try
        {

            //Going to require a select query to get all of the users.
            strquery = "SELECT lsum.user_id, ls.bet_amount FROM league_season_user_map lsum INNER JOIN league_seasons ls ON ls.league_season_id = lsum.league_season_id  WHERE lsum.league_season_id = " + this.league_season_id + ";";

            ResultSet rs = conn.createStatement().executeQuery(strquery);
            if (rs.next()) //Anything in the result set?
            {

                log.info("Getting the bet amount.");
                bet_amount = rs.getDouble("bet_amount");

                do
                {
                    if (rs.getInt("user_id") != usernum)
                    {
                        log.info("Adding a user");
                        players.add(rs.getInt("user_id"));
                    }
                }
                while (rs.next());

                bet_amount = bet_amount / (players.size());

            }
            else //Nothing in the result set.
            {
                log.severe("Nothing in the result set for query.");
                log.severe("Query Executed: " + strquery);
            }

            log.info("To the inserts.");
            for (index = 0; index < players.size(); index++)
            {
                strquery = "INSERT INTO house_bets (parent_bet_id, user_id, bet_amount) VALUES (" + this.id + ", " + players.get(index) + ", " + bet_amount + ");";
                log.info("Adding one to DB: " + strquery);
                conn.createStatement().executeUpdate(strquery);
            }

        }
        catch (SQLException e)
        {
            log.severe("SQL Exception processing!");
            log.info("Connection String: " + strurl + "&" + struser + "&" + strpass);
            log.info(e.getMessage());
        }
    }


    public int getId()
    {
        return id;
    }

    public void setId(int update)
    {
        this.id = update;
    }

    public int getUser_id()
    {
        return user_id;
    }

    public void setUser_id(int update)
    {
        user_id = update;
    }

    public Date getStart()
    {
        return start;
    }

    public void setStart(Date update)
    {
        start = update;
    }

    public String getPicked_City()
    {
        return picked_city;
    }

    public void setPicked_City(String update)
    {
        picked_city = update;
    }

    public String getAgainst_Team()
    {
        return against_team;
    }

    public void setAgainst_Team(String update)
    {
        against_team = update;
    }

    public String getWeek_Short()
    {
        return week_short;
    }

    public void setWeek_Short(String update)
    {
        week_short = update;
    }

    public String getWeek_Long()
    {
        return week_long;
    }

    public void setWeek_Long(String update)
    {
        week_long = update;
    }

    public double getLine()
    {
        return line;
    }

    public void setLine(double update)
    {
        line = update;
    }

    public int getIsHouseBet()
    {
        return isHouseBet;
    }

    public void setIsHouseBet(int update)
    {
        isHouseBet = update;
    }

    public int getLeague_Season_ID()
    {
        return this.league_season_id;
    }

    public void setLeague_Season_ID(int update)
    {
        this.league_season_id = update;
    }

    public String getResult()
    {
        return result;
    }

    public void setResult(String update)
    {
        result = update;
    }

    public String getPicked_Team()
    {
        return picked_team;
    }

    public void setPicked_Team(String update)
    {
        picked_team = update;
    }

    public String getAgainst_City()
    {
        return against_city;
    }

    public void setAgainst_City(String update)
    {
        against_city = update;
    }

    public int getWeek_Number()
    {
        return week_number;
    }

    public void setWeek_Number(int update)
    {
        week_number = update;
    }


}