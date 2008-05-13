<%@ page contentType="text/html" %>
<jsp:useBean id="bcrequest" class="org.krysalis.barcode4j.webapp.BarcodeRequestBean" scope="request"/>
<jsp:setProperty name="bcrequest" property="*"/>
<%@page import="java.util.Collection"%>
<%@page import="java.util.Iterator"%>
<%@page import="org.krysalis.barcode4j.BarcodeUtil"%>
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <title>Barcode4J Servlet</title>
  </head>
  <body>
    <h1>Barcode4J Servlet</h1>
    <p>This page demostrates the usage of the BarcodeServlet.</p>
    <%
    final String genbc = bcrequest.toURL();
    if (bcrequest.isSVG()) {
    %>
    <p>The generated barcode in SVG format (only displayed if SVG is supported in your browser):</p>
    <%
        if (bcrequest.isSvgEmbed()) {
    %>
    <p>
      <embed src="<%=genbc%>&ext=.svg" pluginspage="http://www.adobe.com/svg/viewer/install/" width="100%" height="100"/>
    </p>
    <%
        } else {
    %>
    <p>
      <object type="image/svg+xml" data="<%=genbc%>&ext=.svg" name="DynamicBarcode" width="100%" height="100"/>
    </p>
    <%
        }
    } else if (bcrequest.isBitmap()) {
    %>
    <p>The generated barcode in <%=bcrequest.getFormat()%> format (only displayed if <%=bcrequest.getFormat()%> is supported on the server and in your browser):</p>
    <p>
      <img src="<%=genbc%>"/>
    </p>
    <%
    } else {
        %>
        <p><i>The generated barcode cannot be previewed. Format is <%=bcrequest.getFormat()%>.</i></p>
        <%
    }
    %>
    <p>The following is the URL that was used to create the above barcode:</p>
    <table width="100%" border="1" rules="none" cellpadding="5">
      <tr>
        <td width="100%">
          <p>
            <a href="<%=genbc%>"><%=genbc%></a>
          </p>
        </td>
      </tr>
    </table>
    <p>Change the parameters:</p>
    <form method="post" action="index.jsp">
      <table border="0">
        <tr>
          <td>
            <p>Output format (required):</p>
          </td>
          <td>
            <p>
              <select name="format">
                <%
                final String[] FORMATS = new String[] {"svg", "eps", "jpeg", "tiff", "png", "gif"};
                for (int i = 0; i < FORMATS.length; i++) {
                  out.print("<option");
                  if (FORMATS[i].equals(bcrequest.getFormat())) {
                    out.print(" selected=\"selected\"");
                  }
                  out.print(">");
                  out.print(FORMATS[i]);
                  out.println("</option>");
                }
                %>
              </select>
            </p>
          </td>
          <td>
            <p>Some bitmap formats won't work if there's no image encoder available for this format.</p>
          </td>
        </tr>
        <tr>
          <td>
            <p>SVG display style:</p>
          </td>
          <td>
            <p>
              <input type="checkbox" name="svgEmbed" <%= (bcrequest.isSvgEmbed() ? " checked=\"checked\"" : "") %>/>
            </p>
          </td>
          <td>Checked uses &lt;EMBED&gt; (better for Internet Explorer), unchecked uses &lt;OBJECT&gt; (better for Firefox).</td>
        </tr>
        <tr>
          <td>
            <p>Grayscale:</p>
          </td>
          <td>
            <p>
              <input type="checkbox" name="gray" <%= (bcrequest.isGray() ? " checked=\"checked\"" : "") %>/>
            </p>
          </td>
          <td>Applies to bitmap formats only (JPEG, PNG etc.)</td>
        </tr>
        <tr>
          <td>
            <p>Bitmap resolution (in dpi):</p>
          </td>
          <td>
            <p>
              <input type="text" name="resolution" value="<%= (bcrequest.getResolution() != null ? bcrequest.getResolution() : "") %>"/>
            </p>
          </td>
          <td>
            <p>Applies to bitmap formats only. Example: 300</p>
          </td>
        </tr>
        <tr>
          <td>
            <p>Barcode type (required):</p>
          </td>
          <td>
            <p>
              <select name="type">
                <%
                Collection names = BarcodeUtil.getInstance().getClassResolver().getBarcodeNames();
                Iterator iter = names.iterator();
                while (iter.hasNext()) {
                    String name = (String)iter.next();
                    out.print("<option");
                    if (name.equals(bcrequest.getType())) {
                        out.print(" selected=\"selected\"");
                    }
                    out.print(">");
                    out.print(name);
                    out.println("</option>");
                }
                %>
              </select>
            </p>
          </td>
        </tr>
        <tr>
          <td>
            <p>Message (required):</p>
          </td>
          <td>
            <p>
              <input type="text" name="msg" value="<%= (bcrequest.getMsg() != null ? bcrequest.getMsg() : "") %>"/>
            </p>
          </td>
        </tr>
        <tr>
          <td>
            <p>Height:</p>
          </td>
          <td>
            <p>
              <input type="text" name="height" value="<%= (bcrequest.getHeight() != null ? bcrequest.getHeight() : "") %>"/>
            </p>
          </td>
          <td>
            <p>Example: 2.5cm</p>
          </td>
        </tr>
        <tr>
          <td>
            <p>Module Width:</p>
          </td>
          <td>
            <p>
              <input type="text" name="moduleWidth" value="<%= (bcrequest.getModuleWidth() != null ? bcrequest.getModuleWidth() : "") %>"/>
            </p>
          </td>
          <td>
            <p>Example: 0.3mm</p>
          </td>
        </tr>
        <tr>
          <td>
            <p>Wide Factor:</p>
          </td>
          <td>
            <p>
              <input type="text" name="wideFactor" value="<%= (bcrequest.getWideFactor() != null ? bcrequest.getWideFactor() : "") %>"/>
            </p>
          </td>
          <td>
            <p>Example: 2 or 3</p>
          </td>
        </tr>
        <tr>
          <td>
            <p>Enable Quiet Zone:</p>
          </td>
          <td>
            <p>
              <input type="text" name="quietZone" value="<%= (bcrequest.getQuietZone() != null ? bcrequest.getQuietZone() : "") %>"/>
            </p>
          </td>
          <td>
            <p>Example: 10mw or 1cm. Use "disable" to disable the quiet zone.</p>
          </td>
        </tr>
        <tr>
          <td>
            <p>Placement of human-readable part:</p>
          </td>
          <td>
            <p>
              <select name="humanReadable">
                <%
                final String[] HRPOSITIONS = new String[] {"[default]", "top", "bottom", "none"};
                String hrpos = bcrequest.getHumanReadable();
                if (hrpos == null) {
                    hrpos = HRPOSITIONS[0];
                }
                for (int i = 0; i < HRPOSITIONS.length; i++) {
                    out.print("<option");
                    if (HRPOSITIONS[i].equals(hrpos)) {
                        out.print(" selected=\"selected\"");
                    }
                    out.print(">");
                    out.print(HRPOSITIONS[i]);
                    out.println("</option>");
                }
                %>
              </select>
            </p>
          </td>
        </tr>
        <tr>
          <td>
            <p>Human Readable Size</p>
          </td>
          <td>
            <p>
              <input type="text" name="humanReadableSize" value="<%= (bcrequest.getHumanReadableSize() != null ? bcrequest.getHumanReadableSize() : "") %>"/>
            </p>
          </td>
          <td>
            <p>Example: 8pt</p>
          </td>
        </tr>
        <tr>
          <td>
            <p>Human Readable Font</p>
          </td>
          <td>
            <p>
              <input type="text" name="humanReadableFont" value="<%= (bcrequest.getHumanReadableFont() != null ? bcrequest.getHumanReadableFont() : "") %>"/>
            </p>
          </td>
          <td>
            <p>Example: "Helvetica"</p>
          </td>
        </tr>
        <tr>
          <td>
            <p>Human Readable Pattern</p>
          </td>
          <td>
            <p>
              <input type="text" name="humanReadablePattern" value="<%= (bcrequest.getHumanReadablePattern() != null ? bcrequest.getHumanReadablePattern() : "") %>"/>
            </p>
          </td>
          <td>
            <p>Example: "\_patterned\_:__/__/____" (Any '_' is placeholder for the next message symbol, all other pattern symbols will be inserted between. The '\' is escape char. If the patterned message is too long you can increase the quite zone lenght to make it visible)</p>
          </td>
        </tr>
        <tr>
          <td/>
          <td>
            <p>
              <input type="submit" value="Generate!"/>
            </p>
          </td>
        </tr>
      </table>
    </form>
    <p>
      For the documention on <b>Barcode4J</b>, please visit <a href="http://barcode4j.sourceforge.net" target="_blank">http://barcode4j.sourceforge.net</a>.
    </p>
  </body>
</html>
