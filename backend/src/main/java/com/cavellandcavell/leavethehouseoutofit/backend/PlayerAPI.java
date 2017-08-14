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
import java.util.logging.Logger;
import com.google.api.server.spi.response.InternalServerErrorException;
import com.google.api.server.spi.response.UnauthorizedException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.inject.Named;


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

        Me response = new Me(user_uid);
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

}
