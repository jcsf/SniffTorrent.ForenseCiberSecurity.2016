package com;

import ist.csf.snifftorrent.RMIServer.*;
import ist.csf.snifftorrent.classes.*;

import java.rmi.Naming;
import java.util.ArrayList;

public class HTML_Templates {

    public static ServerInterface server = null;

    public static ServerInterface connectToServer() {
        try {
            server = (ServerInterface) Naming.lookup("rmi://localhost:1099/server");
        } catch (Exception e) {
            System.out.println("ERRO [Server]: " + e);
            e.printStackTrace();
        }

        return server;
    }

    public static String htmlFile(String header, String content) {
        return  "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n" +
                "<html>" +
                header +
                "<body>" +
                content +
                "</body>\n" +
                "</html>";
    }

    public static String htmlHeader (String requestPath, String title) {
        return  "  <head>\n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n" +
                "    <link href=\"" + requestPath + "/css/bootstrap.min.css\" rel=\"stylesheet\" type=\"text/css\"/>\n" +
                "    <link href=\"" + requestPath + "/css/bootstrap-theme.min.css\" rel=\"stylesheet\" type=\"text/css\"/>\n" +
                "    <link href=\"" + requestPath + "/css/style.css\" rel=\"stylesheet\" type=\"text/css\">\n" +
                "    <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js\"></script>\n" +
                "    <script src=\"" + requestPath + "/js/bootstrap.min.js\"></script>\n" +
                "    <script src=\"" + requestPath + "/js/scripts.js\"></script>\n" +
                "    <title>" + title + "</title>\n" +
                "  </head>";
    }

    public static String htmlRedirectHeader (String currentURL, String title) {
        return  "  <head>\n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n" +
                "    <meta http-equiv=\"refresh\" content=\"0; url=" + currentURL + "\" />\n" +
                "    <title>" + title + "</title>\n" +
                "  </head>";
    }

    public static String htmlNavBar (String requestPath, int list, Connection connection) {
        String nav_tab;

        if (list == Server.LIVE) {

            nav_tab = "<li role=\"presentation\" class=\"active\"><a href=\"/SniffTorrent/ListConnections?list=0\" style=\"color:#ad415b\"><span class=\"glyphicon glyphicon-facetime-video\" aria-hidden=\"true\"></span> <b>LIVE</b></a></li>\n" +
                        "<li role=\"presentation\"><a href=\"/SniffTorrent/ListConnections?list=1\" style=\"color:#ffffff\"><span class=\"glyphicon glyphicon-inbox\" aria-hidden=\"true\"></span> <b>TRACKING</b></a></li>\n";
        } else {
            nav_tab = "<li role=\"presentation\"><a href=\"/SniffTorrent/ListConnections?list=0\" style=\"color:#ffffff\"><span class=\"glyphicon glyphicon-facetime-video\" aria-hidden=\"true\"></span> <b>LIVE</b></a></li>\n" +
                    "<li role=\"presentation\" class=\"active\"><a href=\"/SniffTorrent/ListConnections?list=1\" style=\"color:#ad415b\"><span class=\"glyphicon glyphicon-inbox\" aria-hidden=\"true\"></span> <b>TRACKING</b></a></li>\n";
        }

        return "<nav class=\"navbar navbar-default navbar-fixed-top\">\n" +
                    "<div class=\"container-fluid\">\n" +
                        "<div class=\"navbar-header\">\n" +
                            "<a class=\"navbar-brand\" href=\"ListConnections?list=" + list + "\">\n" +
                                "<img alt=\"Brand\" src=\"" + requestPath + "/images/Sniff_Torrent_NavBar.png\" height=50px style=\"margin-top:-15px;\">\n" +
                            "</a>" +
                        "</div>" +
                        "<div class=\"col-md-4\" style=\"padding-top: 8px;\">\n" +
                            "<ul class=\"nav nav-tabs nav-justified\">\n" +
                                nav_tab +
                            "</ul>\n" +
                        "</div>" +
                        htmlSearch(list, connection) +
                    "</div>\n" +
                "</nav>";
    }

    public static String htmlFooter (String text) {
        return "<nav class=\"navbar navbar-default navbar-fixed-bottom\"\">\n" +
                    "<div class=\"container\">\n" +
                        "<div class=\"row\">\n" +
                            "<div class=\"col-md-4\"></div>\n" +
                            "<div class=\"col-md-4\">\n" +
                                "<p class=\"navbar-text\">" + text + "</p>\n" +
                            "</div>\n" +
                            "<div class=\"col-md-4\"></div>\n" +
                        "</div>\n" +
                    "</div>\n" +
                "</nav>";
    }


    public static String htmlConnectionstoHtmlList(String requestPath, int list, String currentURL, ArrayList<Connection> connections) {
        String listHTML = "<div class=\"list-group\">\n";

        for (int i = 0; i < connections.size(); i++) {
            Connection con = connections.get(i);
            listHTML += "<a href=\"/SniffTorrent/ShowConnection?list=" + list + "&connection=" + con.getHash() + "\" class=\"list-group-item\">\n" +
                        "<div class=\"row\">\n" +
                            "<div class=\"pull-left\">\n" +
                                "<img src=\"" + requestPath + "/images/connection.png\" height=50px style=\"margin-top: 6px; margin-left: 12px;\">\n" +
                            "</div>\n" +
                            "<div class=\"col-md-10\">\n" +
                                "<h4 class=\"list-group-item-heading\"> Bittorrent Connection </h4>\n" +
                                "<p class=\"list-group-item-text\"><b>Infractor IP: </b>" + con.getInfractorIP() + "</p>\n" +
                                "<p class=\"list-group-item-text\"><b>Outside IP: </b>" + con.getOutsideIP() + "</p>\n" +
                            "</div>\n" +
                            "<div class=\"col-md-1\" style=\"padding-top: 16px;\">\n" +
                                htmlSaveButton(list, con.getHash()) +
                                htmlDeleteButton(list, currentURL, "/DeleteConnection", con.getHash(), -1) +
                            "</div>\n" +
                        "</div>\n" +
                    "</a>\n";
        }

        return listHTML + "</div>";
    }

    public static String htmlConnection(String requestPath, int list, Connection connection, String timeline, String filter) {
        String saveButton = "";

        if(list == Server.LIVE) {
            saveButton = "<form action=\"/SniffTorrent/TrackConnection\" method=\"get\" style=\"display: inline-block; margin-right: 8px;\">\n" +
                            "<input type=\"hidden\" name=\"connection\" value=\""+ connection.getHash() +"\">\n" +
                            "<button type=\"submit\" class=\"btn btn-default\"><span class=\"glyphicon glyphicon-floppy-disk\" aria-hidden=\"true\"></span> Save Connection</button>\n"  +
                        "</form>";
        }

        String deleteButton = "<form action=\"/SniffTorrent/DeleteConnection\" method=\"get\" style=\"display: inline-block; margin-right: 8px;\">\n" +
                                    "<input type=\"hidden\" name=\"list\" value=\""+ list +"\">\n" +
                                    "<input type=\"hidden\" name=\"connection\" value=\""+ connection.getHash() +"\">\n" +
                                    "<input type=\"hidden\" name=\"currentURL\" value=\"/SniffTorrent/ListConnections?list=" + list + "\">\n" +
                                    "<button type=\"submit\" class=\"btn btn-danger\"><span class=\"glyphicon glyphicon-trash\" aria-hidden=\"true\"></span> Delete Connection</button>\n"  +
                                "</form>";

        return "<div class=\"container\">\n" +
                    "<div class=\"jumbotron\">\n" +
                        "<div class=\"row\">\n" +
                            "<div class=\"col-md-1\">\n" +
                                "<img src=\"" + requestPath + "/images/connection.png\" height=70px style=\"margin-top: 15px;\">\n" +
                            "</div>\n" +
                            "<div class=\"col-md-11\">\n" +
                                "<h1 class=\"display-3\">BITTORRENT CONNECTION</h1>\n" +
                            "</div>\n" +
                        "</div>\n" +
                        "<br>\n" +
                        "<p class=\"lead\"><b>Infractor IP: </b>" + connection.getInfractorIP() + "</p>\n" +
                        "<p class=\"lead\"><b>Infractor MAC: </b>" + connection.getInfractorMAC() + "</p>\n" +
                        "<p class=\"lead\"><b>Outside IP: </b>" + connection.getOutsideIP() + "</p>\n" +
                        "<p class=\"lead\"><b>Outside MAC: </b>" + connection.getOutsideMAC() + "</p>\n" +
                        "<div class=\"row\"><div class=\"pull-right\">" +
                        saveButton +
                        deleteButton +
                        "</div></div>" +
                        "<hr class=\"my-2\">\n" +
                        filter +
                        timeline +
                    "</div>\n" +
                "</div>";
    }

    public static String htmlConnectionTimeLineToHtmlTimeLine(String requestPath, int list, int con, String currentURL, Connection connection, ArrayList<PacketInfo> packets) {
        String listHTML = "<div class=\"row\"><div class=\"col-md-6\"><p class=\"text-center\"><b>From Infractor</b></p></div><div class=\"col-md-6\"><p class=\"text-center\"><b>From Outside</b></p></div></div>";

        listHTML += "<ul class=\"timeline\">\n";

        for (int i = 0; i < packets.size(); i++) {
            PacketInfo info = packets.get(i);
            if (info.getSourceIP().equals(connection.getInfractorIP())) {
                listHTML += "<li>\n";
            } else {
                listHTML += "<li class=\"timeline-inverted\">\n";
            }
            listHTML +=   "<div class=\"timeline-badge\"><img src=\"" + requestPath + "/images/"+ getTypeImage(info.getInfractionType()) + ".png\" height=\"50px\"></div>\n" +
                            "<div class=\"timeline-panel\">\n" +
                                "<div class=\"timeline-heading\">\n" +
                                    "<h4 class=\"timeline-title\"><b>" + info.getInfractionTypeDescription() + "</b></h4>\n" +
                                    "<p class=\"text-muted\" style=\"font-size: 14px;\"><i><span class=\"glyphicon glyphicon-time\"></span> " + info.getTimeStamp() + "</i></p>\n" +
                                "</div>\n" +
                                "<div class=\"timeline-body\">\n" +
                                    "<p><b>Packet Type: </b>" + info.getPacketType() + "</p>\n" +
                                    "<p><b>Source IP: </b>" + info.getSourceIP() + ":" + info.getSourcePort() + "</p>\n" +
                                    "<p><b>Destination IP: </b>" + info.getDestinationIP() + ":" + info.getDestinationPort() +  "</p>\n" +
                                    "<div id=\"packet_" + info.getHash() + "\" class=\"collapse\">\n" +
                                    "<hr class=\"my-2\">\n" +
                                    info.getHTMLTypeLayout() +
                                    "</div>" +
                                    "<button type=\"button\" onclick=\"showHideButton(this); showHideRawButton('packetButton_" + info.getHash() + "');\" class=\"btn btn-primary btn-sm pull-right\" data-toggle=\"collapse\" data-target=\"#packet_" + info.getHash() + "\"><span class=\"glyphicon glyphicon-chevron-down\" aria-hidden=\"true\"></span> Show More</button>\n" +
                                    "<div class=\"pull-right\">" + htmlDeleteButton(list, currentURL, "/ShowConnection/DeletePacketInfo", con, info.getHash()) + "</div>" +
                                    "<button type=\"button\" id=\"packetButton_" + info.getHash() + "\" class=\"btn btn-default btn-sm pull-right\" data-toggle=\"modal\" data-target=\"#packetModal_" + info.getHash() + "\" style=\"margin-right: 8px; display: none;\"><span class=\"glyphicon glyphicon-compressed\" aria-hidden=\"true\"></span> Show Packet Raw Info</button>\n" +
                                    "<div class=\"modal fade\" id=\"packetModal_" + info.getHash() + "\" tabindex=\"-1\" role=\"dialog\" aria-hidden=\"true\">\n" +
                                        "<div class=\"modal-dialog\" role=\"document\">\n" +
                                            "<div class=\"modal-content\">\n" +
                                                "<div class=\"modal-header\">\n" +
                                                    "<button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-label=\"Close\"><span aria-hidden=\"true\">&times;</span></button>\n" +
                                                    "<h4 class=\"modal-title\" id=\"myModalLabel\">Raw Packet</h4>\n" +
                                                "</div>\n" +
                                                "<div class=\"modal-body\">\n" +
                                                    info.getPacket().toString().substring(1).replace("\n", "<br>") +
                                                "</div>\n" +
                                                "<div class=\"modal-footer\">\n" +
                                                    "<button type=\"button\" class=\"btn btn-default\" data-dismiss=\"modal\">Close</button>\n" +
                                                "</div>\n" +
                                            "</div>\n" +
                                        "</div>\n" +
                                    "</div>\n" +
                                "</div>\n" +
                            "</div>\n" +
                    "</li>\n";
        }

        return listHTML + "</ul>";
    }

    public static String htmlSaveButton (int list, int con) { // SAVE BUTTON
        if (list == Server.LIVE) {
            return  "<form action=\"/SniffTorrent/TrackConnection\" method=\"get\" style=\"display: inline-block; margin-right: 8px;\">\n" +
                        "<input type=\"hidden\" name=\"connection\" value=\""+ con +"\">\n" +
                        "<button type=\"submit\" class=\"btn btn-default\">\n" +
                            "<span class=\"glyphicon glyphicon-floppy-disk\" aria-hidden=\"true\"></span>\n" +
                        "</button>\n"  +
                    "</form>";
        }

        return "";
    }

    public static String htmlDeleteButton (int list, String currentURL, String action, int con, int hash) { // DELETE BUTTON
        return  "<form action=\"/SniffTorrent" + action + "\" method=\"get\" style=\"display: inline-block; margin-right: 8px;\">\n" +
                    "<input type=\"hidden\" name=\"list\" value=\""+ list +"\">\n" +
                    "<input type=\"hidden\" name=\"connection\" value=\""+ con +"\">\n" +
                    "<input type=\"hidden\" name=\"hash\" value=\""+ hash +"\">\n" +
                    "<input type=\"hidden\" name=\"currentURL\" value=\""+ currentURL +"\">\n" +
                    "<button type=\"submit\" class=\"btn btn-danger\">\n" +
                        "<span class=\"glyphicon glyphicon-trash\" aria-hidden=\"true\"></span>\n" +
                    "</button>\n"  +
                "</form>";
    }

    public static String htmlSearch (int list, Connection con) {
        if (con == null) {
            return "<form class=\"navbar-form navbar-right\" role=\"search\" action=\"FilterConnections\" method=\"get\">\n" +
                        "<div class=\"form-group\">\n" +
                            "<input type=\"hidden\" name=\"list\" value=\""+ list +"\">\n" +
                            "<select class=\"form-control\" name=\"filter\">\n" +
                                "<option value=\"ip\">Infractor IP</option>\n" +
                                "<option value=\"mac\">Infractor MAC</option>\n" +
                            "</select>\n" +
                            "<input type=\"text\" class=\"form-control\" placeholder=\"Filter Connections\" name=\"search\">\n" +
                        "</div>\n" +
                        "<button type=\"submit\" class=\"btn btn-default\">Search</button>\n" +
                    "</form>";
        } else {
            return "<form class=\"navbar-form navbar-right\" role=\"search\" action=\"ShowConnection/FilterPackets\" method=\"get\">\n" +
                        "<div class=\"form-group\">\n" +
                            "<input type=\"hidden\" name=\"list\" value=\""+ list +"\">\n" +
                            "<input type=\"hidden\" name=\"connection\" value=\""+ con.getHash() +"\">\n" +
                            "<select class=\"form-control\" name=\"filter\">\n" +
                                "<option value=\"type\">Infraction Type</option>\n" +
                                "<option value=\"packetType\">UDP/TCP</option>\n" +
                            "</select>\n" +
                            "<input type=\"text\" class=\"form-control\" placeholder=\"Filter Packets\" name=\"search\">\n" +
                        "</div>\n" +
                        "<button type=\"submit\" class=\"btn btn-default\">Search</button>\n" +
                    "</form>";
        }
    }

    private static String getTypeImage(int type) {
        switch(type) {
            case PacketInfo.BITTORRENT_HANDSHAKE:
                return "handshake";
            case PacketInfo.BITTORRENT_PROTOCOL:
                return "bittorrent";
            case PacketInfo.UTORRENT_PACKAGE:
                return "utorrent";
        }

        return "";
    }
}
