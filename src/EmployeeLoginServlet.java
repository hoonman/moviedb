//import com.google.gson.JsonArray;
//import com.google.gson.JsonObject;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//import java.io.IOException;
//
//
//@WebServlet("/_dashboard/login.html")
//public class EmployeeLoginServlet extends HttpServlet {
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        // Your dashboard logic here
//        response.setContentType("text/html");
//        response.getWriter().println("<html><body><h1>Welcome to the dashboard!</h1></body></html>");
//    }
//}
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.Statement;

@WebServlet(name = "EmployeeLoginServlet", urlPatterns = "/_dashboard/api/login")
public class EmployeeLoginServlet extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
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

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // recaptcha code begins here
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        /* This example only allows username/password to be test/test
        /  in the real project, you should talk to the database to verify username/password
        */
        JsonObject responseJsonObject = new JsonObject();
        PrintWriter out = response.getWriter();
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
//        System.out.println("####loginservlet: recaptcharesponse: " + gRecaptchaResponse);



        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            // Construct a query with parameter represented by "?"
            String query = "SELECT password \n" +
                    "FROM employees \n" +
                    "WHERE email = ?\n"+
                    "LIMIT 1;";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, email);
            // Perform the query
            ResultSet rs = statement.executeQuery();

            boolean valid_email = false;
            boolean correct_password = false;
            // Iterate through each row of rs
            while (rs.next()) {
                valid_email = true;
                String sql_password = rs.getString("password");
                correct_password = password.equals(sql_password);

            }
            rs.close();
            statement.close();

            boolean captchaSuccess = false;

            try {
                RecaptchaVerifyUtils.verify(gRecaptchaResponse);
                // set the status as success
                captchaSuccess = true;
                responseJsonObject.addProperty("reCaptchaStatus", "success");
                responseJsonObject.addProperty("reCaptchaMessage", "success");

            } catch (Exception e) {
                // fill the data in as status failed
                captchaSuccess = false;
                responseJsonObject.addProperty("reCaptchaStatus", "fail");
                responseJsonObject.addProperty("reCaptchaMessage", "reCaptcha has failed");

            }
            if (valid_email && correct_password && captchaSuccess) {
                // Login success:

                // set this user into the session
                request.getSession().setAttribute("user", new User(email));

                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");

            }else{
                // Login fail
                responseJsonObject.addProperty("status", "fail");
                // Log to localhost log
                request.getServletContext().log("Login failed");
                // sample error messages. in practice, it is not a good idea to tell user which one is incorrect/not exist.
                if (!valid_email) {
                    responseJsonObject.addProperty("message", "user " + email + " doesn't exist");
                } else {
                    responseJsonObject.addProperty("message", "incorrect password");
                }
            }

            // Write JSON string to output
            out.write(responseJsonObject.toString());

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



    }
}
