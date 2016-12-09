package com;

import ist.csf.snifftorrent.RMIServer.ServerInterface;
import ist.csf.snifftorrent.classes.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Servlet implementation class FirstServlet
 */
@WebServlet(description = "Settings", urlPatterns = { "/Settings" })
public class Settings extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static boolean saved = false;

    public Settings() {
        super();
        // TODO Auto-generated constructor stub
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServerInterface server = HTML_Templates.server;

        String contextPath = request.getContextPath();
        String content = HTML_Templates.htmlNavBar(contextPath, -1, null);

        PrintWriter out = response.getWriter();

        if (server == null) {
            server = HTML_Templates.connectToServer();
        }

        // MAKE CONTENT

        ServerProperties prop = null;
        try {
            prop  = server.getServerProperties();
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<b> ERRO 404 </b>");
            return;
        }

        content += HTML_Templates.htmlSettings(contextPath, prop, this.saved);

        content += HTML_Templates.htmlFooter("");

        out.println(HTML_Templates.htmlFile(HTML_Templates.htmlHeader(contextPath, "Sniff Torrent"), content));

        saved = false;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServerInterface server = HTML_Templates.server;

        if (server == null) {
            server = HTML_Templates.connectToServer();
        }

        try {
            server.changeServerProperties(Long.parseLong(request.getParameter("time")), Integer.parseInt(request.getParameter("nPackets")));
            this.saved = true;
        } catch (Exception e) {
            this.saved = false;
        }

        doGet(request, response);
    }
}
