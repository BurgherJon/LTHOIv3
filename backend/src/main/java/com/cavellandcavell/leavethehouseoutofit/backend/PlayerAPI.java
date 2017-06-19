/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.cavellandcavell.leavethehouseoutofit.backend;

import com.google.api.server.spi.auth.EspAuthenticator;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiIssuer;
import com.google.api.server.spi.config.ApiIssuerAudience;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.response.UnauthorizedException;


import javax.inject.Named;

// This is the API for players to access.
@Api(
        name = "playerapi",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.leavethehouseoutofit.cavellandcavell.com",
                ownerName = "backend.leavethehouseoutofit.cavellandcavell.com",
                packagePath = ""
        ),
        authenticators = {EspAuthenticator.class},
        issuers = {
                @ApiIssuer(
                      name = "firebase",
                      issuer = "https://securetoken.google.com/lthoi-test",
                      jwksUri = "https://www.googleapis.com/service_accounts/v1/metadata/x509/securetoken@system.gserviceaccount.com"
                )
        },
        issuerAudiences = {
                @ApiIssuerAudience(name = "firebase", audiences = "lthoi-test")
        }
)
public class PlayerAPI {

    /**
     * A simple endpoint method that takes a name and says Hi back
     */
    @ApiMethod(name = "sayHi")
    public MyBean sayHi(@Named("name") String name, User u) throws UnauthorizedException{
        if (u == null)
        {
            throw new UnauthorizedException("Invalid Credentials");
        }

        MyBean response = new MyBean();
        response.setData("Hi, " + name + " " + u.getEmail());

        return response;
    }



}
