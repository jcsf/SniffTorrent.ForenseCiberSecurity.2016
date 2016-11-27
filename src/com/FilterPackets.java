package com;

import ist.csf.snifftorrent.RMIServer.*;
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

@WebServlet(description = "FilterPackets", urlPatterns = { "/ShowConnection/FilterPackets" })
public class FilterPackets extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public FilterPackets() {
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

        PrintWriter out = response.getWriter();

        if (server == null) {
            server = HTML_Templates.connectToServer();
        }

        // MAKE CONTENT
        int con = Integer.parseInt(request.getParameter("connection"));
        Connection conInfo;
        ArrayList<PacketInfo> packetsInfoList;

        try {
            conInfo = server.getConnection(onList, con);
            switch (request.getParameter("filter")) {
                case "type":
                    packetsInfoList = server.getPacketsFilteringType(onList, Integer.parseInt(request.getParameter("connection")), request.getParameter("search"));
                    break;
                case "packetType":
                    packetsInfoList = server.getPacketsFilteringTCPUDP(onList, Integer.parseInt(request.getParameter("connection")), request.getParameter("search"));
                    break;
                default:
                    packetsInfoList = new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
            conInfo = null;
            packetsInfoList = new ArrayList<>();
        }

        String content = HTML_Templates.htmlNavBar(contextPath, onList, conInfo);

        String timeline = HTML_Templates.htmlConnectionTimeLineToHtmlTimeLine(contextPath, onList, con, request.getRequestURI()+ "?" + request.getQueryString(), conInfo, packetsInfoList);

        String filter = "<div class=\"alert alert-danger alert-dismissible\" role=\"alert\"><a href=\"/SniffTorrent/ShowConnection?list=" + onList + "&connection=" + con + "\" class=\"close\" data-dismiss=\"alert\" aria-label=\"close\">x</a><b><span class=\"glyphicon glyphicon-search\" aria-hidden=\"true\"></span> Filtering Packages by:</b> " + request.getParameter("filter").toUpperCase() + " = " + request.getParameter("search").toUpperCase() + "</div>\n";

        content += HTML_Templates.htmlConnection(contextPath, conInfo, timeline, filter);

        content += HTML_Templates.htmlFooter("<b>Total of Packets Found: </b>" + packetsInfoList.size());

        out.println(HTML_Templates.htmlFile(HTML_Templates.htmlHeader(contextPath, "Sniff Torrent"), content));
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
    }
}
