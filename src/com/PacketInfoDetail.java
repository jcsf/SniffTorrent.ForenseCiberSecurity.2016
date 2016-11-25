package com;

import ist.csf.snifftorrent.RMIServer.*;
import ist.csf.snifftorrent.classes.PacketInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(description = "PacketInfoDetail", urlPatterns = { "/ShowConnection/PacketInfoDetail" })
public class PacketInfoDetail extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public PacketInfoDetail() {
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

        PacketInfo packet = null;

        try {
            packet = server.getPacketInfo(onList,  Integer.parseInt(request.getParameter("connection")), Integer.parseInt(request.getParameter("hash")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        content += HTML_Templates.htmlPacket(contextPath, packet);

        out.println(HTML_Templates.htmlFile(HTML_Templates.htmlHeader(contextPath, "Sniff Torrent"), content));
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
    }
}
