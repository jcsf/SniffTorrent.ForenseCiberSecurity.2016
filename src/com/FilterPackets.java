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
import java.util.ArrayList;

@WebServlet(description = "FilterPackets", urlPatterns = { "/FilterPackets" })
public class FilterPackets extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public FilterPackets() {
        super();
        // TODO Auto-generated constructor stub
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int onList;
        ServerInterface server = HTML_Templates.server;

        if(Integer.parseInt(request.getParameter("list")) == Server.LIVE_PACKETS) {
            onList = Server.LIVE_PACKETS;
        } else {
            onList = Server.SAVED_PACKETS;
        }

        String contextPath = request.getContextPath();
        String content = HTML_Templates.htmlNavBar(contextPath, onList);

        PrintWriter out = response.getWriter();

        if (server == null) {
            server = HTML_Templates.connectToServer();
        }

        // MAKE CONTENT

        ArrayList<PacketInfo> packetsInfoList;

        try {
            switch (request.getParameter("filter")) {
                case "type":
                    packetsInfoList = server.getPacketsFilteringType(onList, request.getParameter("search"));
                    break;
                case "ip":
                    packetsInfoList = server.getPacketsFilteringInfIP(onList, request.getParameter("search"));
                    break;
                case "mac":
                    packetsInfoList = server.getPacketsFilteringInfMAC(onList, request.getParameter("search"));
                    break;
                case "packetType":
                    packetsInfoList = server.getPacketsFilteringTCPUDP(onList, request.getParameter("search"));
                    break;
                default:
                    packetsInfoList = new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
            packetsInfoList = new ArrayList<>();
        }

        content += "<div class=\"alert alert-danger\" role=\"alert\"><b><span class=\"glyphicon glyphicon-search\" aria-hidden=\"true\"></span> Filtering Packages by:</b> " + request.getParameter("filter").toUpperCase() + " = " + request.getParameter("search").toUpperCase() + "</div>\n";

        content += HTML_Templates.htmlPacketInfoListtoHtmlList(contextPath, onList, request.getRequestURI() + "?" + request.getQueryString(), packetsInfoList);

        content += HTML_Templates.htmlFooter("<b>Total of Packets Found: </b>" + packetsInfoList.size());

        out.println(HTML_Templates.htmlFile(HTML_Templates.htmlHeader(contextPath, "Sniff Torrent"), content));
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
    }
}
