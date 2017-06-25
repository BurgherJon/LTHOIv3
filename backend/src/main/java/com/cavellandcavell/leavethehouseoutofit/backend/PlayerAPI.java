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



}
