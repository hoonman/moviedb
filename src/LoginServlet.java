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

import jakarta.servlet.http.HttpSession;
import org.jasypt.util.password.PasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.jasypt.util.text.AES256TextEncryptor;

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
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
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        JsonObject responseJsonObject = new JsonObject();
        PrintWriter out = response.getWriter();
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT password \n" +
                    "FROM customers\n" +
                    "WHERE email = ?\n"+
                    "LIMIT 1;";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, email);
            ResultSet rs = statement.executeQuery();

            boolean valid_email = false;
            boolean correct_password = false;
            while (rs.next()) {
                valid_email = true;
                String sql_password = rs.getString("password");
                PasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
                correct_password = passwordEncryptor.checkPassword(password, sql_password);
            }
            rs.close();
            statement.close();
            try {
                RecaptchaVerifyUtils.verify(gRecaptchaResponse);
                responseJsonObject.addProperty("reCaptchaStatus", "success");
                responseJsonObject.addProperty("reCaptchaMessage", "success");

            } catch (Exception e) {
                // fill the data in as status failed
                responseJsonObject.addProperty("reCaptchaStatus", "fail");
                responseJsonObject.addProperty("reCaptchaMessage", "reCaptcha has failed");

            }
            if (valid_email && correct_password) {
                request.getSession().setAttribute("user", new User(email));
                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");

            }else{
                responseJsonObject.addProperty("status", "fail");
                request.getServletContext().log("Login failed");
                if (!valid_email) {
                    responseJsonObject.addProperty("message", "user " + email + " doesn't exist");
                } else {
                    responseJsonObject.addProperty("message", "incorrect password");
                }
            }
            out.write(responseJsonObject.toString());
            response.setStatus(200);

        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
            request.getServletContext().log("Error:", e);
            response.setStatus(500);
        } finally {
            out.close();
        }
    }
}