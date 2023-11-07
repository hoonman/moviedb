//package ReadInDatabase;
//
//import javax.naming.InitialContext;
//import javax.naming.NamingException;
//import javax.sql.DataSource;
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
//import java.util.List;
//import java.sql.Connection;
//import java.util.AbstractMap.SimpleEntry;
//
//public class UpdateDatabase {
//    private DataSource dataSource;
//
//    public void init() {
//        try {
//            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedbtest");
//        } catch (NamingException e) {
//            e.printStackTrace();
//        }
//    }
//    private void insertMovies(List<MovieData> movies) {
//        String insertMovieSql = "INSERT INTO movies (id, title, year, director) VALUES (?, ?, ?, ?)";
//
//        try (Connection conn = dataSource.getConnection();
//             PreparedStatement movieStmt = conn.prepareStatement(insertMovieSql)) {
//
//            for (MovieData movie : movies) {
//                movieStmt.setString(1, movie.getId());
//                movieStmt.setString(2, movie.getTitle());
//                movieStmt.setInt(3, movie.getYear());
//                movieStmt.setString(4, movie.getDirector());
//                movieStmt.addBatch();
//            }
//
//            movieStmt.executeBatch();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void insertStars(List<StarData> stars) {
//        String insertStarSql = "INSERT INTO stars (id, name, birthYear) VALUES (?, ?, ?)";
//
//        try (Connection conn = dataSource.getConnection();
//             PreparedStatement starStmt = conn.prepareStatement(insertStarSql)) {
//
//            for (StarData star : stars) {
//                starStmt.setString(1, star.getId());
//                starStmt.setString(2, star.getName());
//                if (star.getBirthYear() != null) {
//                    starStmt.setInt(3, star.getBirthYear());
//                } else {
//                    starStmt.setNull(3, java.sql.Types.INTEGER);
//                }
//                starStmt.addBatch();
//            }
//
//            starStmt.executeBatch();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void insertCasts(List<SimpleEntry<String, String>> casts) {
//        String insertCastSql = "INSERT INTO stars_in_movies (starId, movieId) VALUES (?, ?)";
//
//        try (Connection conn = dataSource.getConnection();
//             PreparedStatement castStmt = conn.prepareStatement(insertCastSql)) {
//
//            for (SimpleEntry<String, String> cast : casts) {
//                castStmt.setString(1, cast.getKey());
//                castStmt.setString(2, cast.getValue());
//                castStmt.addBatch();
//            }
//
//            castStmt.executeBatch();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//    public static void main(String[] args) {
//        UpdateDatabase updater = new UpdateDatabase();
//
//        // Parse movies and insert into database
//        MovieHandler movieHandler = new MovieHandler();
//        movieHandler.parseDocument();
//        updater.insertMovies(movieHandler.getMovieList());
//
//        // Parse stars and insert into database
//        StarHandler starHandler = new StarHandler();
//        starHandler.parseDocument();
//        updater.insertStars(starHandler.getStarDataList());
//
//        // Parse casts and insert into database
//        CastHandler castHandler = new CastHandler();
//        castHandler.parseDocument();
//        updater.insertCasts(castHandler.getListOfEntries());
//    }
//
//}
