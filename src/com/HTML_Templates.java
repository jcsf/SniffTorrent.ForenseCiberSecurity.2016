package com;

import ist.csf.snifftorrent.RMIServer.ServerInterface;
import ist.csf.snifftorrent.classes.PacketInfo;

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
                "    <title>" + title + "</title>\n" +
                "  </head>";
    }

    public static String htmlNavBar (String requestPath) {
        return "<nav class=\"navbar navbar-default navbar-fixed-top\"\">\n" +
                    "<div class=\"container-fluid\">\n" +
                        "<div class=\"navbar-header\">\n" +
                            "<a class=\"navbar-brand\" href=\"ShowPackets\">\n" +
                                "<img alt=\"Brand\" src=\"" + requestPath + "/images/Sniff_Torrent_NavBar.png\" height=50px style=\"margin-top:-15px;\">\n" +
                            "</a>" +
                        "</div>" +
                        "<form class=\"navbar-form navbar-right\" role=\"search\" action=\"FilterPackets\" method=\"get\">\n" +
                            "<div class=\"form-group\">\n" +
                                "<select class=\"form-control\" name=\"filter\">\n" +
                                    "<option value=\"type\">Infraction Type</option>\n" +
                                    "<option value=\"ip\">Infractor IP</option>\n" +
                                    "<option value=\"mac\">Infractor MAC</option>\n" +
                                "</select>\n" +
                                "<input type=\"text\" class=\"form-control\" placeholder=\"Filter\" name=\"search\">\n" +
                            "</div>\n" +
                            "<button type=\"submit\" class=\"btn btn-default\">Search</button>\n" +
                        "</form>" +
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

    public static String htmlPacketInfoListtoHtmlList(String requestPath, ArrayList<PacketInfo> packets) {
        String list = "<div class=\"list-group\">\n";

        for (int i = 0; i < packets.size(); i++) {
            PacketInfo info = packets.get(i);
            list += "<a href=\"/SniffTorrent/PacketInfoDetail?hash=" + info.getHash() + "\" class=\"list-group-item\">\n" +
                        "<div class=\"row\">\n" +
                            "<div class=\"pull-left\">\n" +
                                "<img src=\"" + requestPath + "/images/" + getTypeImage(info.getInfractionType()) + ".png\" height=50px style=\"margin-top: 12px; margin-left: 12px;\">\n" +
                            "</div>\n" +
                            "<div class=\"col-md-11\">\n" +
                                "<h4 class=\"list-group-item-heading\">" + info.getInfractionTypeDescription() + "</h4>\n" +
                                "<p class=\"list-group-item-text\"><b>Infractor IP: </b>" + info.getInfractor_IP() + "</p>\n" +
                                "<p class=\"list-group-item-text\"><b>Infractor MAC: </b>" + info.getInfractor_MAC() + "</p>\n" +
                                "<p class=\"list-group-item-text\"><b>TimeStamp: </b>" + info.getTimeStamp() + "</p>\n" +
                            "</div>\n" +
                        "</div>\n" +
                    "</a>\n";

        }

        return list + "</div>";
    }

    public static String htmlPacket(String requestPath, PacketInfo packet) {
        return "<div class=\"container\">\n" +
                    "<div class=\"jumbotron\">\n" +
                        "<div class=\"row\">\n" +
                        "<div class=\"col-md-1\">\n" +
                        "<img src=\"" + requestPath + "/images/" + getTypeImage(packet.getInfractionType()) + ".png\" height=70px style=\"margin-top: 15px;\">\n" +
                        "</div>\n" +
                        "<div class=\"col-md-11\">\n" +
                        "<h1 class=\"display-3\">" + packet.getInfractionTypeDescription() + "</h1>\n" +
                        "</div>\n" +
                        "</div>\n" +
                        "<p class=\"lead\"><b>Infractor IP: </b>" + packet.getInfractor_IP() + "</p>\n" +
                        "<p class=\"lead\"><b>Infractor MAC: </b>" + packet.getInfractor_MAC() + "</p>\n" +
                        "<p class=\"lead\"><b>TimeStamp: </b>" + packet.getTimeStamp() + "</p>\n" +
                        "<hr class=\"my-2\">\n" +
                        "<p class=\"lead\">\n" +
                        "<button type=\"button\" class=\"btn btn-primary\" data-toggle=\"modal\" data-target=\"#myModal\">\n" +
                            "<span class=\"glyphicon glyphicon-plus\" aria-hidden=\"true\"></span> Show Packet Raw Info" +
                        "</button>\n" +
                        "</p>\n" +
                        "<div class=\"modal fade\" id=\"myModal\" tabindex=\"-1\" role=\"dialog\" aria-labelledby=\"myModalLabel\" aria-hidden=\"true\">\n" +
                            "<div class=\"modal-dialog\" role=\"document\">\n" +
                                "<div class=\"modal-content\">\n" +
                                    "<div class=\"modal-header\">\n" +
                                        "<button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-label=\"Close\">\n" +
                                            "<span aria-hidden=\"true\">&times;</span>\n" +
                                        "</button>\n" +
                                        "<h4 class=\"modal-title\" id=\"myModalLabel\">Raw Packet</h4>\n" +
                                    "</div>\n" +
                                    "<div class=\"modal-body\">\n" +
                                        packet.getPacket().toString().substring(1).replace("\n", "<br>") +
                                    "</div>\n" +
                                    "<div class=\"modal-footer\">\n" +
                                        //"<button type=\"button\" class=\"btn btn-primary\">Save Info</button>\n" +
                                        "<button type=\"button\" class=\"btn btn-secondary\" data-dismiss=\"modal\">Close</button>\n" +
                                    "</div>\n" +
                                "</div>\n" +
                            "</div>\n" +
                        "</div>\n" +
                    "</div>\n" +
                "</div>";
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
