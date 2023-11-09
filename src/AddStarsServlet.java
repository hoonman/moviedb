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
        if (star == null) {
            // get testStar
            star = new Star();
            Star testStar = new Star("something", 2002);
            star.setStarName(testStar.getStarName());
            star.setBirthYear(testStar.getDate());
        }
        session.setAttribute("newStar", star);
        JsonObject starObject = new JsonObject();
        starObject.addProperty("starName", star.getStarName());
        starObject.addProperty("birthYear", star.getDate());

        responseJsonObject.add("newStar", starObject);
        response.getWriter().write(responseJsonObject.toString());


    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String starName = request.getParameter("starName");
        String birthYear = request.getParameter("birthYear");
        Star newStar = new Star(starName, Integer.parseInt(birthYear));
//        JsonObject responseJsonObject = new JsonObject();

        //add the new star data to the database

//        HttpSession session = request.getSession();
//        session.setAttribute("newStar", newStar);
//
//        JsonObject starObject = new JsonObject();
//        starObject.addProperty("starName", newStar.getStarName());
//        starObject.addProperty("birthYear", newStar.getDate());
//
//        responseJsonObject.add("newStar", starObject);
//        response.getWriter().write(responseJsonObject.toString());
//        String jdbcUrl = "jdbc:mysql://your_database_host:your_database_port/moviedb";
        try (Connection conn = dataSource.getConnection()) {
            String insertQuery = "INSERT INTO stars (name, birthYear, id) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = conn.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, newStar.getStarName());
                preparedStatement.setInt(2, newStar.getDate());
                preparedStatement.setString(3, "000001");

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    // Data successfully inserted into the database
                    HttpSession session = request.getSession();
                    session.setAttribute("newStar", newStar);

                    JsonObject responseJsonObject = new JsonObject();
                    JsonObject starObject = new JsonObject();
                    starObject.addProperty("starName", newStar.getStarName());
                    starObject.addProperty("birthYear", newStar.getDate());
                    responseJsonObject.add("newStar", starObject);
                    response.getWriter().write(responseJsonObject.toString());
                } else {
                    // Handle the case where no rows were affected (insertion failed)
                    response.getWriter().write("Failed to insert new star data into the database.");
                }
            } catch (SQLException e) {
                System.out.println("conecction didn't work (outer ) ");
                e.printStackTrace();
                // Handle SQL exception
            }
        } catch (Exception e) {
            System.out.println("conecction didn't work (outer outer) ");
            e.printStackTrace();
        }

    }

}