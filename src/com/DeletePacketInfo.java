package com;

import ist.csf.snifftorrent.RMIServer.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(description = "DeletePacketInfo", urlPatterns = { "/DeletePacketInfo" })
public class DeletePacketInfo extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public DeletePacketInfo() {
        super();
        // TODO Auto-generated constructor stub
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServerInterface server = HTML_Templates.server;

        PrintWriter out = response.getWriter();

        if (server == null) {
            server = HTML_Templates.connectToServer();
        }

        // DO ACTION
        if(Integer.parseInt(request.getParameter("list")) == Server.LIVE_PACKETS) {
            server.deletePacketInfo(Integer.parseInt(request.getParameter("hash")));
        } else {
            server.unSavePacketInfo(Integer.parseInt(request.getParameter("hash")));
        }

        System.out.println(request.getParameter("currentURL"));

        // MAKE CONTENT
        out.println(HTML_Templates.htmlFile(HTML_Templates.htmlRedirectHeader(request.getParameter("currentURL"), "Sniff Torrent"), ""));
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
    }
}
