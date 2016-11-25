package com;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ist.csf.snifftorrent.RMIServer.Server;
import ist.csf.snifftorrent.RMIServer.ServerInterface;
import ist.csf.snifftorrent.classes.*;

/**
 * Servlet implementation class FirstServlet
 */
@WebServlet(description = "ListConnections", urlPatterns = { "/ListConnections" })
public class FirstServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public FirstServlet() {
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

        ArrayList<Connection> connectionList;

        try {
            connectionList = server.getConnectionList(onList);
        } catch (Exception e) {
            e.printStackTrace();
            connectionList = new ArrayList<>();
        }

        content += HTML_Templates.htmlConnectionstoHtmlList(contextPath, onList, request.getRequestURI()+ "?" + request.getQueryString(), connectionList);

        content += HTML_Templates.htmlFooter("<b>Total of Connections Found: </b>" + connectionList.size());

        out.println(HTML_Templates.htmlFile(HTML_Templates.htmlHeader(contextPath, "Sniff Torrent"), content));
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
    }
}
