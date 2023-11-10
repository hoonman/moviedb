import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import Star.Star;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.sql.SQLException;

@WebServlet(name="AddStarsServlet", urlPatterns = "/_dashboard/api/stars")
public class AddStarsServlet extends HttpServlet {
    private DataSource dataSource;
    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        JsonObject responseJsonObject = new JsonObject();

        Star star = (Star) session.getAttribute("newStar");
        System.out.println("star: " + star.getStarName());
        System.out.println("star date: " + star.getDate());
        String status = (String) session.getAttribute("status");
        System.out.println("status is: " + status);
        if (star == null) {
            star = new Star();
            Star testStar = new Star("something", 2002);
            star.setStarName(testStar.getStarName());
            star.setBirthYear(testStar.getDate());
        }

        if (status == null) {
            status = "fail";
        } else if (status.equals("success")) {
            status = "success";
        }

        session.setAttribute("newStar", star);
        JsonObject starObject = new JsonObject();
        starObject.addProperty("starName", star.getStarName());
        starObject.addProperty("birthYear", star.getDate());

        responseJsonObject.add("newStar", starObject);
        responseJsonObject.addProperty("status", status);
        response.getWriter().write(responseJsonObject.toString());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String starName = request.getParameter("starName");
        String birthYear = request.getParameter("birthYear");
        Star newStar = new Star(starName, Integer.parseInt(birthYear));
        boolean success = false;
        try (Connection conn = dataSource.getConnection()) {
//            String insertQuery = "INSERT INTO stars (name, birthYear, id) VALUES (?, ?, ?)";
            String callProcedure = "{call insert_star(?, ?)}";
            try (PreparedStatement preparedStatement = conn.prepareStatement(callProcedure)) {
                preparedStatement.setString(1, newStar.getStarName());
                preparedStatement.setInt(2, newStar.getDate());
//                preparedStatement.setString(3, "000001");

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    // Data successfully inserted into the database
                    success = true;
                    HttpSession session = request.getSession();
                    session.setAttribute("newStar", newStar);
//                    session.setAttribute("status", true);

                    JsonObject responseJsonObject = new JsonObject();
                    JsonObject starObject = new JsonObject();
                    starObject.addProperty("starName", newStar.getStarName());
                    starObject.addProperty("birthYear", newStar.getDate());
                    responseJsonObject.add("newStar", starObject);
                    responseJsonObject.addProperty("status", "success");

                    response.getWriter().write(responseJsonObject.toString());
                } else {
                    response.getWriter().write("Failed to insert new star data into the database.");
                }
            } catch (SQLException e) {
                System.out.println("conecction didn't work (outer ) ");
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("conecction didn't work (outer outer) ");
            e.printStackTrace();
        }

    }

}