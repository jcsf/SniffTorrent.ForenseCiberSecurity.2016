package com;

import ist.csf.snifftorrent.RMIServer.Server;
import ist.csf.snifftorrent.RMIServer.ServerInterface;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(description = "SaveConnection", urlPatterns = { "/TrackConnection" })
public class SaveConnection extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public SaveConnection() {
        super();
        // TODO Auto-generated constructor stub
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServerInterface server = HTML_Templates.server;

        PrintWriter out = response.getWriter();

        if (server == null) {
            server = HTML_Templates.connectToServer();
        }

        server.saveConnection(Integer.parseInt(request.getParameter("connection")));

        // MAKE CONTENT
        out.println(HTML_Templates.htmlFile(HTML_Templates.htmlRedirectHeader("/SniffTorrent/ShowConnections?list=" + Server.SAVED, "Sniff Torrent"), ""));
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
    }
}
