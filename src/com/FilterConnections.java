package com;

import ist.csf.snifftorrent.RMIServer.Server;
import ist.csf.snifftorrent.RMIServer.ServerInterface;
import ist.csf.snifftorrent.classes.Connection;
import ist.csf.snifftorrent.classes.PacketInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

@WebServlet(description = "FilterConnections", urlPatterns = { "/FilterConnections" })
public class FilterConnections extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public FilterConnections() {
        super();
        // TODO Auto-generated constructor stub
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int onList;
        ServerInterface server = HTML_Templates.server;

        if(Integer.parseInt(request.getParameter("list")) == Server.LIVE) {
            onList = Server.LIVE;
        } else {
            onList = Server.SAVED;
        }

        String contextPath = request.getContextPath();
        String content = HTML_Templates.htmlNavBar(contextPath, onList);

        PrintWriter out = response.getWriter();

        if (server == null) {
            server = HTML_Templates.connectToServer();
        }

        // MAKE CONTENT

        ArrayList<Connection> connectionsList;

        try {
            switch (request.getParameter("filter")) {
                case "ip":
                    connectionsList = server.getPacketsFilteringInfIP(onList, request.getParameter("search"));
                    break;
                case "mac":
                    connectionsList = server.getPacketsFilteringInfMAC(onList, request.getParameter("search"));
                    break;
                default:
                    connectionsList = new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
            connectionsList = new ArrayList<>();
        }

        content += "<div class=\"alert alert-danger\" role=\"alert\"><b><span class=\"glyphicon glyphicon-search\" aria-hidden=\"true\"></span> Filtering Connections by:</b> " + request.getParameter("filter").toUpperCase() + " = " + request.getParameter("search").toUpperCase() + "</div>\n";

        content += HTML_Templates.htmlConnectionstoHtmlList(contextPath, onList, request.getRequestURI() + "?" + request.getQueryString(), connectionsList);

        content += HTML_Templates.htmlFooter("<b>Total of Connections Found: </b>" + connectionsList.size());

        out.println(HTML_Templates.htmlFile(HTML_Templates.htmlHeader(contextPath, "Sniff Torrent"), content));
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
    }
}
