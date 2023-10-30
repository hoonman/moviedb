import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

/**
 * This IndexServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/index.
 */
@WebServlet(name = "JumpServlet", urlPatterns = "/api/jump")
public class JumpServlet extends HttpServlet {
    /**
     * handles GET requests to store session information
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String sessionId = session.getId(); //get unique id
        long lastAccessTime = session.getLastAccessedTime(); //get last access time

        //write into a jsonObject
        JsonObject responseJsonObject = new JsonObject();
        // set the properties
        responseJsonObject.addProperty("sessionID", sessionId);
        responseJsonObject.addProperty("lastAccessTime", new Date(lastAccessTime).toString());

        //list of items from the session (shopping cart)
        // cast it to an array of strings
        ArrayList<String> currURL = (ArrayList<String>) session.getAttribute("currURL");
        if (currURL == null) {
            currURL = new ArrayList<String>();
        }
        JsonArray previousItemsJsonArray = new JsonArray();
//        currURL.forEach(previousItemsJsonArray::add);
        for (String i : currURL) {
            JsonObject jumpJson = new JsonObject();
            jumpJson.addProperty("URL", i);
            previousItemsJsonArray.add(jumpJson);
        }
        responseJsonObject.add("currURL", previousItemsJsonArray);
        // write all the data into the jsonObject
        response.getWriter().write(responseJsonObject.toString());
    }

    /**
     * handles POST requests to add and show the item list information
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //shopping cart functionality
//        String currentURL = request.getRequestURL().toString();
        String currentURL = request.getParameter("URL");
        // need to cut out the http://local host part

        System.out.println("currentURL: " + currentURL);
        HttpSession session = request.getSession();

        // get the previous items in a ArrayList
        ArrayList<String> currURL = (ArrayList<String>) session.getAttribute("currURL");
//        String currURL = (String) session.getAttribute("currURL");
        if (currURL == null) {
//            currURL = currentURL;
//            currURL.set(0, currentURL);
            currURL = new ArrayList<String>();
            currURL.add(currentURL);
            session.setAttribute("currURL", currURL);
        } else {
            synchronized (currURL) {
//                currURL = currentURL;
                currURL.add(currentURL);

            }
            session.setAttribute("currURL", currURL);
        }
        JsonObject responseJsonObject = new JsonObject();
        JsonArray previousItemsJsonArray = new JsonArray();
//        previousItemsJsonArray.add(currURL);
//        currURL.forEach(previousItemsJsonArray::add);
        for (String i : currURL) {
            JsonObject jumpJson = new JsonObject();
            jumpJson.addProperty("URL", i);
            previousItemsJsonArray.add(jumpJson);
        }
        responseJsonObject.add("currURL", previousItemsJsonArray);

        response.getWriter().write(responseJsonObject.toString());
    }
}
