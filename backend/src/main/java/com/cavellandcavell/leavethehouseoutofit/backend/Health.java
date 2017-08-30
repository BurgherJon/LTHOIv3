package com.cavellandcavell.leavethehouseoutofit.backend;

/**
 * Created by Jonat on 8/27/2017.
 */

public class Health
{
    private String status;

    public Health()
    {
        this.status = "healthy";
    }

    public String getStatus()
    {
        return this.status;
    }

    public void setStatus(String update)
    {
        this.status = update;
    }
}
