import java.sql.ResultSet;
import com.google.gson.JsonObject;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import moviePackage.AddMovie;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.SQLException;

@WebServlet(name="AddMovieServlet", urlPatterns = "/_dashboard/api/movies")
public class AddMovieServlet extends HttpServlet {
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

        AddMovie addMovie = (AddMovie) session.getAttribute("newMovie");
        System.out.println("star: " + addMovie.getStarName());
//        System.out.println("star date: " + addMovie.getDate());
        String status = (String) session.getAttribute("status");
        String movieId = (String) session.getAttribute("movieId");
        String genreId = (String) session.getAttribute("genreId");
        String starId = (String) session.getAttribute("starId");
        System.out.println("status is: " + status);
        if (addMovie == null) {
            addMovie = new AddMovie();
//            Star testStar = new Star("something", 2002);
            AddMovie testMovie = new AddMovie("test", 0, "test", "test", "test");
            addMovie.setMovieDirector(testMovie.getMovieDirector());
            addMovie.setMovieYear(testMovie.getMovieYear());
            addMovie.setMovieDirector(testMovie.getMovieDirector());
            addMovie.setStarName(testMovie.getStarName());
            addMovie.setGenreName(testMovie.getGenreName());
        }

        if (status == null) {
            status = "fail";
        } else if (status.equals("success")) {
            status = "success";
        }

        session.setAttribute("newMovie", addMovie);
        JsonObject movieObject = new JsonObject();
        movieObject.addProperty("movieTitle", addMovie.getMovieTitle());
        movieObject.addProperty("movieYear", addMovie.getMovieYear());
        movieObject.addProperty("movieDirector", addMovie.getMovieDirector());
        movieObject.addProperty("starName", addMovie.getStarName());
        movieObject.addProperty("genreName", addMovie.getGenreName());
        responseJsonObject.add("newMovie", movieObject);
        responseJsonObject.addProperty("status", status);
        responseJsonObject.addProperty("movieId", movieId);
        responseJsonObject.addProperty("genreId", genreId);
        responseJsonObject.addProperty("starId", starId);
        response.getWriter().write(responseJsonObject.toString());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String movieTitle = request.getParameter("movieTitle");
        String movieYear = request.getParameter("movieYear");
        String movieDirector = request.getParameter("movieDirector");
        String starName = request.getParameter("starName");
        String genreName = request.getParameter("genreName");
        String movieId = "";
        String genreId = "";
        String starId = "";
        AddMovie newAddMovie = new AddMovie(movieTitle, Integer.parseInt(movieYear), movieDirector, starName, genreName);
        try (Connection conn = dataSource.getConnection()) {
//            String insertQuery = "INSERT INTO stars (name, birthYear, id) VALUES (?, ?, ?)";
//            String callProcedure = "{call insert_star(?, ?)}";
            String checkDup = "SELECT id FROM movies WHERE title = ? AND `year` = ? AND director = ?;";
            try (PreparedStatement preparedStatement = conn.prepareStatement(checkDup)) {
                preparedStatement.setString(1, movieTitle);
                preparedStatement.setInt(2, Integer.parseInt(movieYear));
                preparedStatement.setString(3, movieDirector);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    System.out.println("movie already exists... Aborting.");
                    JsonObject responseJsonObject = new JsonObject();
                    responseJsonObject.addProperty("status", "fail");
//                    responseJsonObject.addProperty("message", "Movie already ex/ists");
                    response.getWriter().write(responseJsonObject.toString());
                    return;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            String callProcedure = "{call add_movie(?, ?, ?, ?, ?)}";
            try (PreparedStatement preparedStatement = conn.prepareStatement(callProcedure)) {

                preparedStatement.setString(1, newAddMovie.getMovieTitle());
                preparedStatement.setInt(2, newAddMovie.getMovieYear());
                preparedStatement.setString(3, newAddMovie.getMovieDirector());
                preparedStatement.setString(4, newAddMovie.getStarName());
                preparedStatement.setString(5, newAddMovie.getGenreName());

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    // Data successfully inserted into the database
                    HttpSession session = request.getSession();
                    session.setAttribute("newMovie", newAddMovie);

                    //need to get movieId, genreId, starId
                    String movieIdQuery = "select id from movies where title = ? AND year = ? AND director = ?";
                    try (PreparedStatement preparedStatement1 = conn.prepareStatement(movieIdQuery)) {
                        preparedStatement1.setString(1, movieTitle);
                        preparedStatement1.setInt(2, Integer.parseInt(movieYear));
                        preparedStatement1.setString(3, movieDirector);
                        try (ResultSet resultSet = preparedStatement1.executeQuery()) {
                            if (resultSet.next()) {
                                movieId = resultSet.getString("id");
                                System.out.println("movie id: " + movieId);
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    String genreIdQuery = "select id from genres where name = ?";
                    try (PreparedStatement preparedStatement1 = conn.prepareStatement(genreIdQuery)) {
                        preparedStatement1.setString(1, genreName);
                        try (ResultSet resultSet = preparedStatement1.executeQuery()) {
                            if (resultSet.next()) {
                                genreId = resultSet.getString("id");
                                System.out.println("genre id: " + genreId);
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    String idRetriever = "select id from stars where name=?";
                    try (PreparedStatement preparedStatement1 = conn.prepareStatement(idRetriever)) {
                        preparedStatement1.setString(1, starName);
                        try (ResultSet resultSet = preparedStatement1.executeQuery()) {
                            if (resultSet.next()) {
                                starId = resultSet.getString("id");
                                System.out.println("star id: " + starId);
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    JsonObject responseJsonObject = new JsonObject();
                    JsonObject movieObject = new JsonObject();
                    movieObject.addProperty("movieTitle", newAddMovie.getMovieTitle());
                    movieObject.addProperty("movieYear", newAddMovie.getMovieYear());
                    movieObject.addProperty("movieDirector", newAddMovie.getMovieDirector());
                    movieObject.addProperty("starName", newAddMovie.getStarName());
                    movieObject.addProperty("genreName", newAddMovie.getGenreName());
                    responseJsonObject.add("newMovie", movieObject);
                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("movieId", movieId);
                    responseJsonObject.addProperty("genreId", genreId);
                    responseJsonObject.addProperty("starId", starId);
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