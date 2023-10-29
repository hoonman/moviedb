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
import moviePackage.Movie;

/**
 * This IndexServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/index.
 */
@WebServlet(name = "IndexServlet", urlPatterns = "/api/cart")
public class CartServlet extends HttpServlet {
    /**
     * handles GET requests to store session information
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        long lastAccessTime = session.getLastAccessedTime();

        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("sessionID", sessionId);
        responseJsonObject.addProperty("lastAccessTime", new Date(lastAccessTime).toString());

        ArrayList<Movie> previousItems = (ArrayList<Movie>) session.getAttribute("previousItems");
        if (previousItems == null) {
            previousItems = new ArrayList<Movie>();
        }

//        String remove = request.getParameter("remove");
//        if (remove.equals("No")) {
//            System.out.println("something");
//
//        }

        JsonArray previousItemsJsonArray = new JsonArray();
        // replaced the lambda add with this iterative approach
        for (Movie movie : previousItems) {
            JsonObject movieJson = new JsonObject();
            movieJson.addProperty("movieName", movie.getName());
            movieJson.addProperty("quantity", movie.getQuantity());
            movieJson.addProperty("cost", movie.getCost());
            movieJson.addProperty("remove", movie.getRemove());
            previousItemsJsonArray.add(movieJson);
        }

        responseJsonObject.add("previousItems", previousItemsJsonArray);

        response.getWriter().write(responseJsonObject.toString());
    }

    /**
     * handles POST requests to add and show the item list information
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String item = request.getParameter("movieName");
        String quantity = request.getParameter("quantity");
        String cost = request.getParameter("cost");
        String remove = request.getParameter("remove");

        HttpSession session = request.getSession();
        boolean movieExists = false;

        ArrayList<Movie> previousItems = (ArrayList<Movie>) session.getAttribute("previousItems");
        if (previousItems == null) {
            previousItems = new ArrayList<Movie>();
            Movie singleMovie = new Movie(item, Integer.parseInt(quantity), Integer.parseInt(cost), remove);
            previousItems.add(singleMovie);
            session.setAttribute("previousItems", previousItems);
        } else {
            synchronized (previousItems) {
                for (int i = 0; i < previousItems.size(); i++) {
                    if (previousItems.get(i).getName().equals(item) && remove.equals("No")) {
                        movieExists = true;
                        previousItems.get(i).setQuantity(previousItems.get(i).getQuantity() + 1);
                        previousItems.get(i).setCost(previousItems.get(i).getCost() + Integer.parseInt(cost));
                    } else if (previousItems.get(i).getName().equals(item) && remove.equals("Yes")) {
                        //remove previousItems from the list and break
                        previousItems.get(i).setQuantity(previousItems.get(i).getQuantity() - 1);
                        previousItems.get(i).setCost(previousItems.get(i).getCost());
//                        previousItems.remove(i);
                        break;
                    } else if (remove.equals("Delete")) {
                        previousItems.remove(i);
                        break;
                    }
                }
                if (!movieExists) {
                    Movie newMovie = new Movie(item, Integer.parseInt(quantity), Integer.parseInt(cost), remove);
                    previousItems.add(newMovie);
                }
                if (!remove.equals("Delete") && !remove.equals("Yes")) {
                    session.setAttribute("previousItems", previousItems);

                }
            }
        }
        if (!remove.equals("Delete") && !remove.equals("Yes")) {
            JsonObject responseJsonObject = new JsonObject();

            JsonArray previousItemsJsonArray = new JsonArray();
            for (Movie movie : previousItems) {
                JsonObject movieJson = new JsonObject();
                movieJson.addProperty("movieName", movie.getName());
                movieJson.addProperty("quantity", movie.getQuantity());
                movieJson.addProperty("cost", movie.getCost());
                movieJson.addProperty("remove", movie.getRemove());
                previousItemsJsonArray.add(movieJson);
            }
            responseJsonObject.add("previousItems", previousItemsJsonArray);


            response.getWriter().write(responseJsonObject.toString());

        }

    }
}
