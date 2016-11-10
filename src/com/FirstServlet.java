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
@WebServlet(description = "ShowPackets", urlPatterns = { "/ShowPackets" })
public class FirstServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public FirstServlet() {
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
            packetsInfoList = server.getPacketInfoList(onList);
        } catch (Exception e) {
            e.printStackTrace();
            packetsInfoList = new ArrayList<>();
        }

        content += HTML_Templates.htmlPacketInfoListtoHtmlList(contextPath, onList, request.getRequestURI()+ "?" + request.getQueryString(), packetsInfoList);

        content += HTML_Templates.htmlFooter("<b>Total of Packets Found: </b>" + packetsInfoList.size());

        out.println(HTML_Templates.htmlFile(HTML_Templates.htmlHeader(contextPath, "Sniff Torrent"), content));
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
    }
}
