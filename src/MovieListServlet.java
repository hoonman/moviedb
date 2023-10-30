import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@WebServlet(name = "MovieListServlet", urlPatterns = {"/api/movie-list", "/api/genre_selection_list", "/api/first-character",  "/api/search-movies"})
public class MovieListServlet extends HttpServlet {
    private static final long serialVersionUID = 3L;

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    private String constructSortQuery(String sortField) {
        StringBuilder sortQuery = new StringBuilder("ORDER BY ");
        String[] sortingOrder = sortField.split("_");
        for(int i = 0; i< sortingOrder.length; i++){
            if(i == 2){
                sortQuery.append(", ");
            }
            if(i%2 == 0){
                if(sortingOrder[i].equalsIgnoreCase("rating")){
                    sortQuery.append("r.rating ");
                }else{
                    sortQuery.append("sub.MovieTitle ");
                }
            }else{
                if(sortingOrder[i].equalsIgnoreCase("ASC")){
                    sortQuery.append("ASC ");
                }else{
                    sortQuery.append("DESC ");
                }
            }
        }
        return sortQuery.toString();
    }


    private boolean isNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
    protected void sendResponse(HttpServletRequest request, HttpServletResponse response, String query, Object[] params) throws IOException {
        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource


            PreparedStatement statement = conn.prepareStatement(query);
            // Set parameter values based on their types
            int index = 1;
            for (Object param : params) {
                if (param instanceof String) {
                    statement.setString(index, (String) param);
                } else if (param instanceof Integer) {
                    statement.setInt(index, (Integer) param);
                }
                index++;
            }
            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
//            statement.setString(1, id);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {

                String movieId = rs.getString("MovieId");
                String movieTitle = rs.getString("MovieTitle");
                String movieYear = rs.getString("MovieYear");

                String director = rs.getString("Director");
                String genres = rs.getString("Genres");
                String stars = rs.getString("Stars");


                String rating = rs.getString("Rating");

                // Create a JsonObject based on the data we retrieve from rs

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_Id", movieId);
                jsonObject.addProperty("movie_Title", movieTitle);
                jsonObject.addProperty("movie_Year", movieYear);
                jsonObject.addProperty("director", director);

                JsonArray genreJsonArray = new JsonArray();
                String[] genres_split = genres.split(",");
                // Iterate through the list and split each string
                for (String genre : genres_split) {
                    JsonObject actorObject = new JsonObject();
                    String[] parts = genre.split("\\|");
                    String name = parts[0];
                    String id = parts[1];
                    actorObject.addProperty("name", name);
                    actorObject.addProperty("id", id);
                    genreJsonArray.add(actorObject);
                }
                jsonObject.add("genres", genreJsonArray);

                JsonArray starsJsonArray = new JsonArray();

                String[] actors = stars.split(",");
                // Iterate through the list and split each string
                for (String actor : actors) {
                    JsonObject actorObject = new JsonObject();
                    String[] parts = actor.split("\\|");
                    String name = parts[0];
                    String id = parts[1];
                    actorObject.addProperty("name", name);
                    actorObject.addProperty("id", id);
                    starsJsonArray.add(actorObject);
                }
                jsonObject.add("stars", starsJsonArray);

                jsonObject.addProperty("rating", rating);

                jsonArray.add(jsonObject);
            }
            rs.close();
            statement.close();

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }
        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }

    protected void handleCharacterRequest(HttpServletRequest request, HttpServletResponse response) throws IOException{
        String order = request.getParameter("order");
        if(order == null){
            order = "RATING_DESC_TABLES_DESC";
        }
        // Retrieve parameter id from url request.
        String character = request.getParameter("nameStartsWith");
        String page_number = request.getParameter("page_number");
        String page_size = request.getParameter("page_size");
        page_number = isNullOrEmpty(page_number)? "1" :  page_number;
        page_size = isNullOrEmpty(page_size)? "25" :  page_size;

        Object[] params = { character, (Integer.parseInt(page_number)-1) * Integer.parseInt(page_size), Integer.parseInt(page_size)};
        // The log message can be found in localhost log
        request.getServletContext().log("getting character: " + character);
        String query;
        if(character.equals("*")){
            query = "SELECT sub.MovieId, sub.MovieTitle, sub.MovieYear, sub.Director, sub.Genres, sub.Stars, r.rating AS Rating " +
                    "FROM (" +
                    "    SELECT m.id AS MovieId, m.title AS MovieTitle, m.year AS MovieYear, m.director AS Director, " +
                    "           (SELECT GROUP_CONCAT(CONCAT(names,'|', ids)) " +
                    "            FROM (" +
                    "                SELECT DISTINCT g.name AS names, g.id as ids " +
                    "                FROM genres_in_movies gim " +
                    "                JOIN genres g ON gim.genreId = g.id " +
                    "                WHERE gim.movieId = m.id LIMIT 3" +
                    "            ) AS SubGenres) AS Genres, " +
                    "           (SELECT GROUP_CONCAT(CONCAT(names,'|',ids)) " +
                    "            FROM (" +
                    "                SELECT DISTINCT s.name AS names, s.id as ids " +
                    "                FROM stars_in_movies sim " +
                    "                JOIN stars s ON sim.starId = s.id " +
                    "                WHERE sim.movieId = m.id LIMIT 3" +
                    "            ) AS SubStars) AS Stars " +
                    "    FROM movies m " +
                    "    WHERE m.id IN (" +
                    "        SELECT gim.movieId " +
                    "        FROM genres_in_movies gim " +
                    "        JOIN genres g ON gim.genreId = g.id" +
                    "    ) " +
                    "    AND SUBSTRING(m.title, 1, 1) NOT REGEXP '^[A-Za-z0-9]' " +
                    ") AS sub " +
                    "JOIN ratings r ON sub.MovieId = r.movieId " +
                    "ORDER BY r.rating DESC " +
                    "LIMIT ?, ?;";
        }else{
            query = "SELECT sub.MovieId, sub.MovieTitle, sub.MovieYear, sub.Director, sub.Genres, sub.Stars, r.rating AS Rating " +
                    "FROM (" +
                    "    SELECT m.id AS MovieId, m.title AS MovieTitle, m.year AS MovieYear, m.director AS Director, " +
                    "           (SELECT GROUP_CONCAT(CONCAT(names,'|', ids)) " +
                    "            FROM (" +
                    "                SELECT DISTINCT g.name AS names, g.id as ids " +
                    "                FROM genres_in_movies gim " +
                    "                JOIN genres g ON gim.genreId = g.id " +
                    "                WHERE gim.movieId = m.id LIMIT 3" +
                    "            ) AS SubGenres) AS Genres, " +
                    "           (SELECT GROUP_CONCAT(CONCAT(names,'|',ids)) " +
                    "            FROM (" +
                    "                SELECT DISTINCT s.name AS names, s.id as ids " +
                    "                FROM stars_in_movies sim " +
                    "                JOIN stars s ON sim.starId = s.id " +
                    "                WHERE sim.movieId = m.id LIMIT 3" +
                    "            ) AS SubStars) AS Stars " +
                    "    FROM movies m " +
                    "    WHERE m.id IN (" +
                    "        SELECT gim.movieId " +
                    "        FROM genres_in_movies gim " +
                    "        JOIN genres g ON gim.genreId = g.id" +
                    "    ) " +
                    "    AND SUBSTRING(m.title, 1, 1) = ?" +
                    ") AS sub " +
                    "JOIN ratings r ON sub.MovieId = r.movieId " ;
            query += constructSortQuery(order);
//                "ORDER BY r.rating DESC, sub.MovieTitle ASC\n";

            query += "LIMIT ?,?;\n";
        }



        sendResponse(request, response ,query,params);

    }
    protected void handleSearchRequest(HttpServletRequest request, HttpServletResponse response) throws IOException{
        String order = request.getParameter("order");
        if(order == null){
            order = "RATING_DESC_TABLES_DESC";
        }
        // Retrieve parameter id from url request.
        String title = request.getParameter("title");
        String str_year = request.getParameter("year");
        String q_director = request.getParameter("director");
        String starName = request.getParameter("star_name");

        title = isNullOrEmpty(title) ? "!" : title;
        int year = isNullOrEmpty(str_year) ? -1 : Integer.parseInt(str_year);
        q_director = isNullOrEmpty(q_director) ? "!" : q_director;
        starName = isNullOrEmpty(starName) ? "!" : starName;
        String page_number = request.getParameter("page_number");
        String page_size = request.getParameter("page_size");
        page_number = isNullOrEmpty(page_number)? "1" :  page_number;
        page_size = isNullOrEmpty(page_size)? "25" :  page_size;


        Object[] params = { q_director, "%"+q_director+"%", year, year, title, "%"+title+ "%","%" +starName+"%",starName,(Integer.parseInt(page_number)-1) * Integer.parseInt(page_size), Integer.parseInt(page_size)};
        // The log message can be found in localhost log
        String query = "SELECT\n" +
                "    sub.MovieId,\n" +
                "    sub.MovieTitle,\n" +
                "    sub.MovieYear,\n" +
                "    sub.Director,\n" +
                "    sub.Genres,\n" +
                "    sub.Stars,\n" +
                "    r.rating AS Rating\n" +
                "FROM\n" +
                "    (\n" +
                "        SELECT\n" +
                "            m.id AS MovieId,\n" +
                "            m.title AS MovieTitle,\n" +
                "            m.year AS MovieYear,\n" +
                "            m.director AS Director,\n" +
                "            (\n" +
                "                SELECT GROUP_CONCAT(CONCAT(names, \"|\", ids))\n" +
                "                FROM\n" +
                "                    (\n" +
                "                        SELECT DISTINCT g.name AS names, g.id AS ids\n" +
                "                        FROM genres_in_movies gim\n" +
                "                        JOIN genres g ON gim.genreId = g.id\n" +
                "                        WHERE gim.movieId = m.id\n" +
                "                        LIMIT 3\n" +
                "                    ) AS SubGenres\n" +
                "            ) AS Genres,\n" +
                "            (\n" +
                "                SELECT GROUP_CONCAT(CONCAT(names, \"|\", ids))\n" +
                "                FROM\n" +
                "                    (\n" +
                "                        SELECT DISTINCT s.name AS names, s.id AS ids\n" +
                "                        FROM stars_in_movies sim\n" +
                "                        JOIN stars s ON sim.starId = s.id\n" +
                "                        WHERE sim.movieId = m.id\n" +
//                    "                        AND\n" +
//                    "                        (? = \'!\' OR s.name LIKE ?)\n" +
                "                        LIMIT 3\n" +
                "                    ) AS SubStars\n" +
                "            ) AS Stars\n" +
                "        FROM\n" +
                "            movies m\n" +
                "        WHERE " +
                "           (? = \'!\' OR m.director LIKE ?)\n" +
                "           AND (? = -1 OR m.year = ?)\n" +
                "           AND (? = \'!\'  OR m.title LIKE ?)\n" +
                "    ) AS sub\n" +
                "JOIN ratings r ON sub.MovieId = r.movieId\n" +
                "WHERE\n" +
                "    sub.MovieId IN (\n" +
                "        SELECT movieId\n" +
                "        FROM stars_in_movies\n" +
                "        WHERE starId IN (\n" +
                "            SELECT id\n" +
                "            FROM stars\n" +
                "            WHERE (name LIKE ? OR ? = '!')\n" +
                "        )\n" +
                "    )\n";
        query += constructSortQuery(order);
//                "ORDER BY r.rating DESC, sub.MovieTitle ASC\n";

        query += "LIMIT ?,?;\n";

        sendResponse(request, response ,query,params);

    }

    protected void handleGenreRequest(HttpServletRequest request, HttpServletResponse response) throws IOException{
        String order = request.getParameter("order");
        if(order == null){
            order = "RATING_DESC_TABLES_DESC";
        }
        // Retrieve parameter id from url request.
        String genreID = request.getParameter("genreID");
        String page_number = request.getParameter("page_number");
        String page_size = request.getParameter("page_size");
        page_number = isNullOrEmpty(page_number)? "1" :  page_number;
        page_size = isNullOrEmpty(page_size)? "25" :  page_size;

        Object[] params = { genreID, (Integer.parseInt(page_number)-1) * Integer.parseInt(page_size), Integer.parseInt(page_size)};
        // The log message can be found in localhost log
        request.getServletContext().log("getting genreID: " + genreID);
        String query = "SELECT\n" +
                "    sub.MovieId,\n" +
                "    sub.MovieTitle,\n" +
                "    sub.MovieYear,\n" +
                "    sub.Director,\n" +
                "    sub.Genres,\n" +
                "    sub.Stars,\n" +
                "    r.rating AS Rating\n" +
                "FROM\n" +
                "    (\n" +
                "        SELECT\n" +
                "            m.id AS MovieId,\n" +
                "            m.title AS MovieTitle,\n" +
                "            m.year AS MovieYear,\n" +
                "            m.director AS Director,\n" +
                "            (\n" +
                "                SELECT GROUP_CONCAT(CONCAT(names, '|', ids))\n" +
                "                FROM\n" +
                "                    (\n" +
                "                        SELECT DISTINCT g.name AS names, g.id AS ids\n" +
                "                        FROM genres_in_movies gim\n" +
                "                        JOIN genres g ON gim.genreId = g.id\n" +
                "                        WHERE gim.movieId = m.id\n" +
                "                        LIMIT 3\n" +
                "                    ) AS SubGenres\n" +
                "            ) AS Genres,\n" +
                "            (\n" +
                "                SELECT GROUP_CONCAT(CONCAT(names, '|', ids))\n" +
                "                FROM\n" +
                "                    (\n" +
                "                        SELECT DISTINCT s.name AS names, s.id AS ids\n" +
                "                        FROM stars_in_movies sim\n" +
                "                        JOIN stars s ON sim.starId = s.id\n" +
                "                        WHERE sim.movieId = m.id\n" +
                "                        LIMIT 3\n" +
                "                    ) AS SubStars\n" +
                "            ) AS Stars\n" +
                "        FROM movies m\n" +
                "        WHERE m.id IN\n" +
                "            (\n" +
                "                SELECT gim.movieId\n" +
                "                FROM genres_in_movies gim\n" +
                "                JOIN genres g ON gim.genreId = g.id\n" +
                "                WHERE g.id = ?\n" +
                "            )\n" +
                "    ) AS sub\n" +
                "JOIN ratings r ON sub.MovieId = r.movieId\n";
        query += constructSortQuery(order);
//                "ORDER BY r.rating DESC, sub.MovieTitle ASC\n";

        query += "LIMIT ?,?;\n";


        sendResponse(request, response ,query,params);

    }
    protected void handleMovieList(HttpServletRequest request, HttpServletResponse response) throws IOException{
        String order = request.getParameter("order");
        if(order == null){
            order = "RATING_DESC_TABLES_DESC";
        }
        String query = "SELECT\n" +
                "    sub.MovieId,\n" +
                "    sub.MovieTitle,\n" +
                "    sub.MovieYear,\n" +
                "    sub.Director,\n" +
                "    sub.Genres,\n" +
                "    sub.Stars,\n" +
                "    r.rating AS Rating\n" +
                "FROM\n" +
                "    (\n" +
                "        SELECT\n" +
                "            m.id AS MovieId,\n" +
                "            m.title AS MovieTitle,\n" +
                "            m.year AS MovieYear,\n" +
                "            m.director AS Director,\n" +
                "            (\n" +
                "                SELECT GROUP_CONCAT(CONCAT(names, \"|\", ids))\n" +
                "                FROM\n" +
                "                    (\n" +
                "                        SELECT DISTINCT g.name AS names, g.id AS ids\n" +
                "                        FROM genres_in_movies gim\n" +
                "                        JOIN genres g ON gim.genreId = g.id\n" +
                "                        WHERE gim.movieId = m.id\n" +
                "                        LIMIT 3\n" +
                "                    ) AS SubGenres\n" +
                "            ) AS Genres,\n" +
                "            (\n" +
                "                SELECT GROUP_CONCAT(CONCAT(names, \"|\", ids))\n" +
                "                FROM\n" +
                "                    (\n" +
                "                        SELECT DISTINCT s.name AS names, s.id AS ids\n" +
                "                        FROM stars_in_movies sim\n" +
                "                        JOIN stars s ON sim.starId = s.id\n" +
                "                        WHERE sim.movieId = m.id\n" +
                "                        LIMIT 3\n" +
                "                    ) AS SubStars\n" +
                "            ) AS Stars\n" +
                "        FROM\n" +
                "            movies m\n" +
                "    ) AS sub\n" +
                "JOIN ratings r ON sub.MovieId = r.movieId\n";
                query += constructSortQuery(order);
//                "ORDER BY r.rating DESC, sub.MovieTitle ASC\n";

                query += "LIMIT ?,?;\n";
        String page_number = request.getParameter("page_number");
        String page_size = request.getParameter("page_size");

        page_number = isNullOrEmpty(page_number)? "1" :  page_number;
        page_size = isNullOrEmpty(page_size)? "25" :  page_size;

        Object[] params = {  (Integer.parseInt(page_number)-1) * Integer.parseInt(page_size), Integer.parseInt(page_size)};

        sendResponse(request,response,query,params);
    }
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getServletPath();
        response.setContentType("application/json"); // Response mime type
                  // Construct a query with parameter represented by "?"


        switch (pathInfo) {
            case "/api/genre_selection_list":
                handleGenreRequest(request, response);
                break;
            case "/api/first-character":
                handleCharacterRequest(request, response);
                break;
            case "/api/movie-list":
                handleMovieList(request, response);
                break;
            case "/api/search-movies":
                handleSearchRequest(request,response);
                break;
            default:
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                break;
        }

    }

}
