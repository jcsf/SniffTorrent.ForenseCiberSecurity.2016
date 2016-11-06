package com;

import ist.csf.snifftorrent.RMIServer.ServerInterface;
import ist.csf.snifftorrent.classes.PacketInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.Naming;

/**
 * Servlet implementation class ShowPackets
 */
@WebServlet(description = "PacketInfoDetail", urlPatterns = { "/PacketInfoDetail" }, initParams = {@WebInitParam(name="id",value="1"),@WebInitParam(name="name",value="pankaj")})
public class PacketInfoDetail extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static ServerInterface server;

    public PacketInfoDetail() {
        super();
        // TODO Auto-generated constructor stub
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String HTML_START = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n" +
                "<html>\n" +
                "  <head>\n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n" +
                "    <link href=\"" + request.getContextPath() + "/css/style.css\" rel=\"stylesheet\" type=\"text/css\">\n" +
                "    <link href=\"" + request.getContextPath() + "/css/bootstrap.min.css\" rel=\"stylesheet\" type=\"text/css\"/>\n" +
                "    <link href=\"" + request.getContextPath() + "/css/bootstrap-theme.min.css\" rel=\"stylesheet\" type=\"text/css\"/>\n" +
                "    <title>Sniff Torrent</title>\n" +
                "  </head>";
        String HTML_END="</body>\n</html>";

        PrintWriter out = response.getWriter();
        PacketInfo packet = null;

        if (server == null) {
            try {
                server = (ServerInterface) Naming.lookup("rmi://localhost:1099/server");
            } catch (Exception e) {
                System.out.println("ERRO [Server]: " + e);
                e.printStackTrace();
            }
        }

        try {
            packet = server.getPacketInfo(Integer.parseInt((String)request.getParameter("index")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        out.println(HTML_START + packet.toStringWithPacketDetails().replace("\n", "<br>") + HTML_END);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
    }
}
