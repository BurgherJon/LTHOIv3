package com.cavellandcavell.leavethehouseoutofit.backend;

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
import com.google.appengine.api.users.User;

import javax.inject.Named;

/**
 * Created by Jonat on 6/24/2017.
 */

public class Environment
{
    public String db_url;
    public String db_user;
    public String db_password;
    public String db_driver;

    public Environment()
    {
        //If you change the local variable values you must update them here.
        String local_db_url = "jdbc:mysql://127.0.0.1:3306/lthoidb";
        String local_db_user = "root";
        String local_db_password = "vv";
        String local_db_driver = "com.mysql.jdbc.Driver";

        //If you change the test variable values you must update them here.
        String test_applicationId = "lthoi-test";
        String test_db_url = "jdbc:google:mysql://lthoi-test:us-central1:lthoidb/lthoidb";
        String test_db_user = "root";
        String test_db_password = "lthoi-test";
        String test_db_driver = "com.mysql.jdbc.GoogleDriver";

        //If you change the production values you must update them here.
        String prod_applicationId = "leavethehouseoutofit";
        String prod_db_url = "jdbc:google:mysql://leavethehouseoutofit:us-central1:lthoidb/lthoidb";
        String prod_db_user = "root";
        String prod_db_password = "lthoiprod";
        String prod_db_driver = "com.mysql.jdbc.GoogleDriver";

        final Logger log = Logger.getLogger(Environment.class.getName());

        //First to see if this is running locally (e.g. not running in an actual appengine environment.
        if (SystemProperty.environment.value() != SystemProperty.Environment.Value.Production)
        {
            this.db_url = local_db_url;
            this.db_user = local_db_user;
            this.db_password = local_db_password;
            this.db_driver = local_db_driver;
            log.info("Setup Local");
        }

        //If it's not local, see if the app engine id is for test.
        else if (SystemProperty.applicationId.get().equals(test_applicationId))
        {
            this.db_url = test_db_url;
            this.db_user = test_db_user;
            this.db_password = test_db_password;
            this.db_driver = test_db_driver;
            log.info("Setup Test");
        }

        //If it's not local and it's not test, then maybe it's Prod.
        else if (SystemProperty.applicationId.get().equals(prod_applicationId))
        {
            this.db_url = prod_db_url;
            this.db_user = prod_db_user;
            this.db_password = prod_db_password;
            this.db_driver = prod_db_driver;
            log.info("Setup Prod");
        }

        else
        {
            log.severe("Can't recognize environment!  AppId: " + SystemProperty.applicationId.get());
        }
    }
}
