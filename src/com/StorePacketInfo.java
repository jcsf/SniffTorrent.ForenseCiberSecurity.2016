package com;

import ist.csf.snifftorrent.RMIServer.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;

@WebServlet(description = "StorePacketInfo", urlPatterns = { "/StorePacketInfo" })
public class StorePacketInfo extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public StorePacketInfo() {
        super();
        // TODO Auto-generated constructor stub
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServerInterface server = HTML_Templates.server;

        PrintWriter out = response.getWriter();

        if (server == null) {
            server = HTML_Templates.connectToServer();
        }

        server.savePacketInfo(Integer.parseInt(request.getParameter("hash")));

        // MAKE CONTENT
        out.println(HTML_Templates.htmlFile(HTML_Templates.htmlRedirectHeader("/SniffTorrent/ShowPackets?list=" + Server.SAVED_PACKETS, "Sniff Torrent"), ""));
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
    }
}
