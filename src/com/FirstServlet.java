package com;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;

import ist.csf.snifftorrent.RMIServer.ServerInterface;
import ist.csf.snifftorrent.classes.*;

/**
 * Servlet implementation class FirstServlet
 */
@WebServlet(description = "ShowPackets", urlPatterns = { "/ShowPackets" }, initParams = {@WebInitParam(name="id",value="1"),@WebInitParam(name="name",value="pankaj")})
public class FirstServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static ServerInterface server;

    public FirstServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String HTML_START = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n" +
                "<html>\n" +
                "  <head>\n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n" +
                "    <link href=\"" + request.getContextPath() + "/css/bootstrap.min.css\" rel=\"stylesheet\" type=\"text/css\"/>\n" +
                "    <link href=\"" + request.getContextPath() + "/css/bootstrap-theme.min.css\" rel=\"stylesheet\" type=\"text/css\"/>\n" +
                "    <link href=\"" + request.getContextPath() + "/css/style.css\" rel=\"stylesheet\" type=\"text/css\">\n" +
                "    <title>Sniff Torrent</title>\n" +
                "  </head>" +
                "<nav class=\"navbar navbar-default navbar-fixed-top\"\">\n" +
                "  <div class=\"container-fluid\">\n" +
                "   <div class=\"navbar-header\">\n" +
                "       <a class=\"navbar-brand\" href=\"#\">\n" +
                "        <img alt=\"Brand\" src=\"" + request.getContextPath() + "/images/Sniff_Torrent_NavBar.png\" height=50px style=\"margin-top:-15px;\">\n" +
                "      </a>" +
                "   </div>" +
                "<form class=\"navbar-form navbar-right\" role=\"filter\">\n" +
                "  <div class=\"form-group\">\n" +
                "       <select class=\"form-control\">\n" +
                "           <option>Infraction Type</option>\n" +
                "           <option>Infractor IP</option>\n" +
                "           <option>Infractor MAC</option>\n" +
                "       </select>" +
                "    <input type=\"text\" class=\"form-control\" placeholder=\"Filter\">\n" +
                "  </div>\n" +
                "  <button type=\"submit\" class=\"btn btn-default\">Submit</button>\n" +
                "</form>" +
                "  </div>\n" +
                "</nav>";
        String HTML_END="</body>\n</html>";

        PrintWriter out = response.getWriter();
        ArrayList<PacketInfo> warnings = null;

        if (server == null) {
            try {
                server = (ServerInterface) Naming.lookup("rmi://localhost:1099/server");
            } catch (Exception e) {
                System.out.println("ERRO [Server]: " + e);
                e.printStackTrace();
            }
        }

        try {
            warnings = server.getPacketInfoList();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String table = "";
        if (warnings != null) {
            table =  "<div class=\"list-group\">\n";
            for (int i = 0; i < warnings.size(); i++) {
                PacketInfo info = warnings.get(i);
                table +="   <a href=\"/SniffTorrent/PacketInfoDetail?index=" + i + "\" class=\"list-group-item\">\n" +
                        "   <h4 class=\"list-group-item-heading\">" + info.getInfractionTypeDescription() + "</h4>\n" +
                        "   <p class=\"list-group-item-text\"><b>Infractor IP: </b>" + info.getInfractor_IP() + "</p>\n" +
                        "   <p class=\"list-group-item-text\"><b>Infractor MAC: </b>" + info.getInfractor_MAC() + "</p>\n" +
                        "   <p class=\"list-group-item-text\"><b>TimeStamp: </b>" + info.getTimeStamp() + "</p>\n" +
                        "   </a>\n";

            }
            table +=  "</div>";

        }
        out.println(HTML_START + table + HTML_END);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
    }
}
