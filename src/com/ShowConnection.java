package com;

import ist.csf.snifftorrent.RMIServer.Server;
import ist.csf.snifftorrent.RMIServer.ServerInterface;
import ist.csf.snifftorrent.classes.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Servlet implementation class FirstServlet
 */
@WebServlet(description = "ShowConnection", urlPatterns = { "/ShowConnection" })
public class ShowConnection extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public ShowConnection() {
        super();
        // TODO Auto-generated constructor stub
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { // TODO: ALL SHOW CONNECTION PAGE
        int onList;
        ServerInterface server = HTML_Templates.server;

        if(Integer.parseInt(request.getParameter("list")) == Server.LIVE) {
            onList = Server.LIVE;
        } else {
            onList = Server.SAVED;
        }

        String contextPath = request.getContextPath();

        PrintWriter out = response.getWriter();

        if (server == null) {
            server = HTML_Templates.connectToServer();
        }

        // MAKE CONTENT
        int con = Integer.parseInt(request.getParameter("connection"));
        Connection conInfo;

        try {
            conInfo = server.getConnection(onList, con);
        } catch (Exception e) {
            e.printStackTrace();
            conInfo = null;
        }

        String content = HTML_Templates.htmlNavBar(contextPath, onList, conInfo);

        String timeline;

        if(conInfo.getType() == Connection.BITTORRENT_TRAFFIC) {
            timeline = HTML_Templates.htmlConnectionTimeLineToHtmlTimeLine(contextPath, onList, con, request.getRequestURI()+ "?" + request.getQueryString(), conInfo, conInfo.getTimeline());
        } else {
            Date first = conInfo.getTimeline().get(0).getTimeStamp();
            Date last = conInfo.getTimeline().get(1).getTimeStamp();
            long diff = TimeUnit.MILLISECONDS.toSeconds(last.getTime() - first.getTime());

            if (diff > 60) {
                diff = TimeUnit.MILLISECONDS.toMinutes(last.getTime() - first.getTime());
            }

            timeline = "<p class=\"lead\"><b>Number of UDP Packets Exchanged: </b>" + conInfo.getUDPPacketCounter() + "</p>" +
                        "<p class=\"lead\"><b>Time Between the First and the Last One: </b>" + diff + " Minutes</p>" ;
        }

        content += HTML_Templates.htmlConnection(contextPath, onList, conInfo, timeline, "");

        if(conInfo.getType() == Connection.BITTORRENT_TRAFFIC) {
            content += HTML_Templates.htmlFooter("<b>Total of Packets Found: </b>" + conInfo.getTimeline().size());
        }

        out.println(HTML_Templates.htmlFile(HTML_Templates.htmlHeader(contextPath, "Sniff Torrent"), content));
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
    }
}
