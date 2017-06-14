/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.cavellandcavell.leavethehouseoutofit.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;


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
)
public class PlayerAPI {

    /**
     * A simple endpoint method that takes a name and says Hi back
     */
    @ApiMethod(name = "sayHi")
    public MyBean sayHi(@Named("name") String name) {
        MyBean response = new MyBean();
        response.setData("Hi, " + name);

        return response;
    }

}
