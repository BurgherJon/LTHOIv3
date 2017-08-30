package com.cavellandcavell.leavethehouseoutofit.backend;
import javax.servlet.http.*;
import java.io.*;


public class CronServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        PlayerAPI playerAPI = new PlayerAPI();
        playerAPI.cronJob();
    }
}
