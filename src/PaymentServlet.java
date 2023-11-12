import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.sql.Connection;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.text.SimpleDateFormat;

import PayUser.PayUser;

/**
 * This IndexServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/index.
 */
@WebServlet(name = "PaymentServlet", urlPatterns = "/api/payment")
public class PaymentServlet extends HttpServlet {
    /**
     * handles GET requests to store session information
     */
    private static final long serialVersionUID = 3L;
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
        String sessionId = session.getId(); //get unique id
        long lastAccessTime = session.getLastAccessedTime(); //get last access time
//
        //write into a jsonObject
        JsonObject responseJsonObject = new JsonObject();
        // set the properties
        responseJsonObject.addProperty("sessionID", sessionId);
        responseJsonObject.addProperty("lastAccessTime", new Date(lastAccessTime).toString());

//        String firstName = request.getParameter("firstName");
//        String lastName = request.getParameter("lastName");
//        String creditCardNum = request.getParameter("creditCardNum");
//        String expDate = request.getParameter("expDate");
//        String saleId = request.getParameter("saleId");

        //list of items from the session (shopping cart)
        // cast it to an array of strings
        ArrayList<PayUser> previousItems = (ArrayList<PayUser>) session.getAttribute("payUser");
        if (previousItems == null) {
            previousItems = new ArrayList<PayUser>();
        }
//        // Log to localhost log
        JsonArray previousItemsJsonArray = new JsonArray();
//        previousItems.forEach(previousItemsJsonArray::add); //:: -> lambdas
        for (PayUser item : previousItems) {
            JsonObject payJson = new JsonObject();
            payJson.addProperty("firstName", item.getFirstName());
            payJson.addProperty("lastName", item.getLastName());
            payJson.addProperty("creditCardNum", item.getCardNumber());
            payJson.addProperty("expDate", item.getExpDate());
            payJson.addProperty("authorized", item.getAuthorized());
            payJson.addProperty("movieId", item.getMovieId());
            payJson.addProperty("saleId", item.getSaleId());

            previousItemsJsonArray.add(payJson);

        }
        responseJsonObject.add("payUser", previousItemsJsonArray);

        // write all the data into the jsonObject
        response.getWriter().write(responseJsonObject.toString());
    }

    /**
     * handles POST requests to add and show the item list information
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //shopping cart functionality
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String creditCardNum = request.getParameter("creditCardNum");
        String expDate = request.getParameter("expDate");
        String movieId = request.getParameter("movieId");
        String saleId = request.getParameter("saleId");
        boolean authorizer = false;
        int lastId = 0;
        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT * from creditcards where id = ? AND firstName = ? and lastName = ? and expiration = ?";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, creditCardNum);
                statement.setString(2, firstName);
                statement.setString(3, lastName);
                statement.setString(4, expDate);

                ResultSet rs = statement.executeQuery();
                JsonArray jsonArray = new JsonArray();

                while (rs.next()) {
                    authorizer = true;
                    System.out.println("does it go into rs.next()");
                    String card = rs.getString("id");
                    String first = rs.getString("firstName");
                    String last = rs.getString("lastName");
                    String exp = rs.getString("expiration");

                    //since we have succeeded, we can now populate the data here
                    String sql = "SELECT MAX(id) FROM sales;";
                    ResultSet result = statement.executeQuery(sql);
                    if (result.next()) {
                        lastId = result.getInt(1);
                    }

                    String sql2 = "SELECT id AS movieId\n" +
                            "FROM movies\n" +
                            "WHERE title = ?";
                    PreparedStatement statement2 =  conn.prepareStatement(sql2);
                    statement2.setString(1, movieId);
                    ResultSet resultSet2 = statement2.executeQuery();
                    String finalMovieId = "";
                    while (resultSet2.next()) {
                        finalMovieId = resultSet2.getString("movieId");
                    }
                    if (authorizer == true) {
                        int newId = lastId += 1;
                        saleId = Integer.toString(newId);
                        System.out.println("newid: " + newId);
//                        java.time.LocalDate localDate = java.time.LocalDate.parse(exp);
//                        java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);
                        java.time.LocalDate localDate = java.time.LocalDate.now();
                        java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);

                        String insertQuery = "Insert into sales (id, customerId, movieId, saleDate) Values (?, ?, ?, ?)";
                        PreparedStatement preparedStatement = conn.prepareStatement(insertQuery);
                        preparedStatement.setInt(1, newId);
                        System.out.println("integer parse int: " + Integer.parseInt(card));
                        preparedStatement.setInt(2, Integer.parseInt(card));
//                        preparedStatement.setString(3, "tt0395642");
                        preparedStatement.setString(3, finalMovieId);
                        System.out.println("final movie id is: " + finalMovieId);
                        preparedStatement.setDate(4, sqlDate);
                        int rowsAffected = preparedStatement.executeUpdate();
                        if (rowsAffected > 0) {
                            System.out.println("database insertion succ");
                        } else {
                            System.out.println("insertion failed");
                        }

                    }
                }
                }

        } catch (Exception e ) {
            System.out.println(e);

        }
        // set some parameters in your form (key, value)
        HttpSession session = request.getSession();

        //get the database

        ArrayList<PayUser> previousItems = (ArrayList<PayUser>) session.getAttribute("payUser");
        if (previousItems == null) {
            previousItems = new ArrayList<PayUser>();

            PayUser currUser = new PayUser(firstName, lastName, creditCardNum, expDate, authorizer, movieId, saleId);
//            previousItems.add(item);
            previousItems.add(currUser);
            session.setAttribute("payUser", previousItems);
        } else {
            // prevent corrupted states through sharing under multi-threads
            // will only be executed by one thread at a time
            synchronized (previousItems) {
//                previousItems.add();
//                previousItems.clear();
                PayUser currUser = new PayUser(firstName, lastName, creditCardNum, expDate, authorizer, movieId, saleId);
//            previousItems.add(item);
                previousItems.add(currUser);
                session.setAttribute("payUser", previousItems);
            }
        }
//
        JsonObject responseJsonObject = new JsonObject();
//
        JsonArray previousItemsJsonArray = new JsonArray();
//        previousItems.forEach(previousItemsJsonArray::add);

        responseJsonObject.add("payUser", previousItemsJsonArray);
        for (PayUser item : previousItems) {
            JsonObject payJson = new JsonObject();
            payJson.addProperty("firstName", item.getFirstName());
            payJson.addProperty("lastName", item.getLastName());
            payJson.addProperty("creditCardNum", item.getCardNumber());
            payJson.addProperty("expDate", item.getExpDate());
            payJson.addProperty("authorized", item.getAuthorized());
            payJson.addProperty("movieId", item.getMovieId());
            item.setSaleId(saleId);
            payJson.addProperty("saleId", item.getSaleId());
        }

        response.getWriter().write(responseJsonObject.toString());
    }
}
