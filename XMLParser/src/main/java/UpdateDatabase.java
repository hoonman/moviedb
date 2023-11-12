
import javax.sql.DataSource;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class UpdateDatabase {
    private DataSource dataSource;
    String url="jdbc:mysql://localhost:3306/moviedb";
    String user = "mytestuser"; // Replace with your username
    String password = "My6$Password"; // Replace with your password

    private static final String NAMESPACE = "fabflix.com";
    private int genres_inserted = 0;
    private int movies_inserted = 0;
    private int stars_inserted = 0;
    private int genres_in_movies_inserted = 0;
    private int stars_in_movies_inserted = 0;

    private String generateUUID(String name){
        try {
            MessageDigest sha256Digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = sha256Digest.digest((NAMESPACE + name).getBytes(StandardCharsets.UTF_8));
            String hexHash = String.format("%064x", new BigInteger(1, hash));
            return hexHash.substring(0, 10);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Could not create hash", e);
        }
    }

    private void insertMovies(List<MovieData> movies) {
        final int CHUNK_SIZE = 200; // Adjust based on your requirements
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (int i = 0; i < movies.size(); i += CHUNK_SIZE) {
            final List<MovieData> chunk = new ArrayList<>(movies.subList(i, Math.min(i + CHUNK_SIZE, movies.size())));
            Runnable task = () -> {
                Connection conn = null;
                try{
                    conn = DriverManager.getConnection(url, user, password);
                    conn.setAutoCommit(false);
                    processMovieChunk(conn, chunk);
                    conn.commit();
                } catch (SQLException e) {
                    try {
                        if (conn != null) conn.rollback(); // Rollback in case of error
                    } catch (SQLException ex) {
                    }

                } finally {
                    try {
                        if (conn != null) conn.close();
                    } catch (SQLException ex) {
                        // Log or handle the exception thrown on close
                    }
                }
            };
            executor.submit(task);
        }

        executor.shutdown(); // Disallow new tasks
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) { // Wait for tasks to complete
                executor.shutdownNow(); // Force shutdown if tasks exceed time limit
            }
        } catch (InterruptedException ie) {
            executor.shutdownNow(); // Cancel if current thread interrupted
            Thread.currentThread().interrupt(); // Preserve interrupt status
            // Log and handle the InterruptedException
        }
    }
    private void processMovieChunk(Connection conn, List<MovieData> chunk ) throws SQLException {
        String insertMovieSql = "INSERT INTO movies (id, title, year, director) VALUES (?, ?, ?, ?)";
        String checkMovieSql = "SELECT COUNT(*) FROM movies WHERE title = ? AND year = ? AND director = ?";
        String checkMovieIDSql = "SELECT COUNT(*) FROM movies WHERE id = ?";
        String insertRating = "INSERT INTO ratings (movieId, rating, numVotes) VALUES (?,?,?)";

        try (PreparedStatement checkStmt = conn.prepareStatement(checkMovieSql);
             PreparedStatement movieStmt = conn.prepareStatement(insertMovieSql);
             PreparedStatement movieIDStmt = conn.prepareStatement(checkMovieIDSql);
             PreparedStatement ratingsStmt = conn.prepareStatement(insertRating);
        ) {

            for (MovieData movie : chunk) {
                // Check for existing movie
                checkStmt.setString(1, movie.getTitle());
                checkStmt.setInt(2, movie.getYear());
                checkStmt.setString(3, movie.getDirector());
                String movie_UUID = generateUUID(movie.getId());
                movieIDStmt.setString(1, movie_UUID);
                ResultSet resultSet = checkStmt.executeQuery();
                ResultSet resultMovieIDSet = movieIDStmt.executeQuery();
                ratingsStmt.setString(1, movie_UUID);
                ratingsStmt.setFloat(2, -1);
                ratingsStmt.setInt(3, -1);

                ratingsStmt.addBatch();

                if (resultSet.next()
                        && resultSet.getInt(1) == 0
                        && resultMovieIDSet.next()
                        && resultMovieIDSet.getInt(1) == 0) {
                    // Insert new movie
                    movieStmt.setString(1, movie_UUID);
                    movieStmt.setString(2, movie.getTitle());
                    movieStmt.setInt(3, movie.getYear());
                    movieStmt.setString(4, movie.getDirector());
                    movieStmt.addBatch();
                }
                // Reset for next iteration
                checkStmt.clearParameters();
                movieIDStmt.clearParameters();
            }
            int[] movieInsertResults = movieStmt.executeBatch();
            for (int result : movieInsertResults) {
                if (result != Statement.EXECUTE_FAILED) {
                    movies_inserted++;
                }
            }
            ratingsStmt.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            conn.rollback(); // Rollback in case of error
            throw e;
        }
    }

    private void insertGenres(List<MovieData> movies) {
        String getAllGenresSql = "SELECT name FROM genres";
        String insertGenreSql = "INSERT INTO genres (id, name) VALUES (?, ?)";
        String getGenreIdSql = "SELECT id FROM genres WHERE name = ?";
        String insertGenreMovieSql = "INSERT INTO genres_in_movies (genreId, movieId) VALUES (?, ?)";
        String checkGenreMovieExist = "SELECT COUNT(*) FROM genres_in_movies WHERE genreId = ? AND movieId = ?";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            conn.setAutoCommit(false);

            // Fetch existing genres and populate the set
            Set<String> existingGenres = new HashSet<>();
            int genreCount = 0;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(getAllGenresSql)) {
                while (rs.next()) {
                    existingGenres.add(rs.getString("name"));
                    genreCount++;
                }
            }

            try (PreparedStatement insertGenreStmt = conn.prepareStatement(insertGenreSql);
                 PreparedStatement getGenreIdStmt = conn.prepareStatement(getGenreIdSql);
                 PreparedStatement insertGenreMovieStmt = conn.prepareStatement(insertGenreMovieSql);
                 PreparedStatement checkGenreMovieExistStmt = conn.prepareStatement(checkGenreMovieExist)) {

                for (MovieData movie : movies) {
                    String movie_UUID = generateUUID(movie.getId());

                    for (GenreData genre : movie.getGenres()) {
                        if (!existingGenres.contains(genre.name)) {
                            // Insert new genre with custom ID and add to the set
                            genreCount++;
                            insertGenreStmt.setInt(1, genreCount);
                            insertGenreStmt.setString(2, genre.name);
                            int genreInsertResult = insertGenreStmt.executeUpdate();
                            if (genreInsertResult != 0) {
                                genres_inserted++;
                                existingGenres.add(genre.name);
                            }
                        }

                        // Retrieve genreId
                        getGenreIdStmt.setString(1, genre.name);
                        ResultSet genreIdResultSet = getGenreIdStmt.executeQuery();
                        genreIdResultSet.next();
                        int genre_id = genreIdResultSet.getInt(1);

                        // Check if genre-movie pair exists
                        checkGenreMovieExistStmt.setInt(1, genre_id);
                        checkGenreMovieExistStmt.setString(2, movie_UUID);
                        ResultSet checkGenreMovie = checkGenreMovieExistStmt.executeQuery();

                        if (checkGenreMovie.next() && checkGenreMovie.getInt(1) == 0) {
                            // Insert into genres_in_movies
                            insertGenreMovieStmt.setInt(1, genre_id);
                            insertGenreMovieStmt.setString(2, movie_UUID);
                            int genreMovieInsertResult = insertGenreMovieStmt.executeUpdate();
                            if (genreMovieInsertResult != 0) {
                                genres_in_movies_inserted++;
                            }
                        }
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback(); // Rollback in case of error
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertStars(List<StarData> stars){
        final int CHUNK_SIZE = 200; // Adjust based on your requirements
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (int i = 0; i < stars.size(); i += CHUNK_SIZE) {
            final List<StarData> chunk = new ArrayList<>(stars.subList(i, Math.min(i + CHUNK_SIZE, stars.size())));
            Runnable task = () -> {
                Connection conn = null;
                try {
                    conn = DriverManager.getConnection(url, user, password);
                    conn.setAutoCommit(false);
                    processStarChunk(conn, chunk);
                    conn.commit();
                } catch (SQLException e) {
                    try {
                        if (conn != null) conn.rollback();
                    } catch (SQLException ex) {
                        // Log or handle the rollback exception
                    }
                    // Log or handle the original SQLException
                } finally {
                    try {
                        if (conn != null) conn.close();
                    } catch (SQLException ex) {
                        // Log or handle the exception thrown on close
                    }
                }
            };
            executor.submit(task);
        }
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException ie) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
            // Log and handle the InterruptedException
        }
    }
    private void processStarChunk(Connection conn, List<StarData> stars) throws SQLException {
        String insertStarSql = "INSERT INTO stars (id, name, birthYear) VALUES (?, ?, ?)";
        String checkStarSql = "SELECT COUNT(*) FROM stars WHERE name = ? AND birthYear = ?";
        String checkStarIDSql = "SELECT COUNT(*) FROM stars WHERE id = ?";

        try(PreparedStatement starStmt = conn.prepareStatement(insertStarSql);
            PreparedStatement checkStarStmt = conn.prepareStatement(checkStarSql);
            PreparedStatement checkStarIDStmt = conn.prepareStatement(checkStarIDSql);
        ) {

            for (StarData star : stars) {
                String starID = generateUUID(star.getStage_name());
                checkStarStmt.setString(1, starID);
                checkStarStmt.setString(2, star.getFirst_name() + star.getLast_name());
                checkStarIDStmt.setString(1, starID);
                ResultSet resultSet = checkStarStmt.executeQuery();
                ResultSet resultSetID = checkStarIDStmt.executeQuery();

                if(resultSet.next() && resultSet.getInt(1) ==0 &&resultSetID.next() && resultSetID.getInt(1) == 0){
                    starStmt.setString(1, starID);
                    starStmt.setString(2, star.getFirst_name() +" "+ star.getLast_name());
                    if (star.getYear() != -1) {
                        starStmt.setInt(3, star.getYear());
                    } else {
                        starStmt.setNull(3, java.sql.Types.INTEGER);
                    }
                    starStmt.addBatch();

                }
                checkStarStmt.clearParameters();
                checkStarIDStmt.clearParameters();
            }


            int[] starsInsertResults = starStmt.executeBatch();
            for (int result : starsInsertResults) {
                if (result != Statement.EXECUTE_FAILED) {
                    stars_inserted++;
                }
            }

            conn.commit();
        } catch (SQLException e) {
            conn.rollback(); // Rollback in case of error
            throw e;
        }
    }
    private void insertCasts(List<SimpleEntry<String, String>> casts) {
        final int CHUNK_SIZE = 200; // Adjust based on your requirements
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (int i = 0; i < casts.size(); i += CHUNK_SIZE) {
            final List<SimpleEntry<String, String>> chunk = new ArrayList<>(casts.subList(i, Math.min(i + CHUNK_SIZE, casts.size())));
            Runnable task = () -> {
                Connection conn = null;
                try {
                    conn = DriverManager.getConnection(url, user, password);
                    conn.setAutoCommit(false);
                    processCastChunk(conn, chunk);
                    conn.commit();
                } catch (SQLException e) {
                    try {
                        if (conn != null) conn.rollback();
                    } catch (SQLException ex) {
                        // Log or handle the rollback exception
                    }
                    // Log or handle the original SQLException
                } finally {
                    try {
                        if (conn != null) conn.close();
                    } catch (SQLException ex) {
                        // Log or handle the exception thrown on close
                    }
                }
            };
            executor.submit(task);
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException ie) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
            // Log and handle the InterruptedException
        }
    }

    private void processCastChunk(Connection conn, List<SimpleEntry<String, String>> casts) {
        String insertCastSql = "INSERT INTO stars_in_movies (starId, movieId) VALUES (?, ?)";
        String checkStarSql = "SELECT COUNT(*) FROM stars WHERE id = ?";
        String checkMovieSql = "SELECT COUNT(*) FROM movies WHERE id = ?";
        String checkStarInMovies = "SELECT COUNT(*) FROM stars_in_movies WHERE starId = ? AND movieId = ?";

        try(
                PreparedStatement castStmt = conn.prepareStatement(insertCastSql);
                PreparedStatement checkStarStmt = conn.prepareStatement(checkStarSql);
                PreparedStatement checkMovieStmt = conn.prepareStatement(checkMovieSql);
                PreparedStatement checkStarInMoviesStmt = conn.prepareStatement(checkStarInMovies);
        ) {

            for (SimpleEntry<String, String> cast : casts) {
                String starID = generateUUID(cast.getValue());
                String movieID = generateUUID(cast.getKey());

                checkStarStmt.setString(1, starID);
                checkMovieStmt.setString(1, movieID);
                checkStarInMoviesStmt.setString(1,starID);
                checkStarInMoviesStmt.setString(2,movieID);

                ResultSet checkStarResult = checkStarStmt.executeQuery();
                ResultSet checkMovie = checkMovieStmt.executeQuery();
                ResultSet checkStarInMoviesResult = checkStarInMoviesStmt.executeQuery();

                if(checkStarResult.next()
                        && checkMovie.next()
                        && checkStarInMoviesResult.next()
                        && checkStarResult.getInt(1) == 1
                        && checkMovie.getInt(1) == 1
                        && checkStarInMoviesResult.getInt(1) == 0
                ){
                    castStmt.setString(1, starID);
                    castStmt.setString(2, movieID);
                    castStmt.addBatch();
                }
                checkStarStmt.clearParameters();
                checkMovieStmt.clearParameters();
            }


            int[] castInsertResults = castStmt.executeBatch();
            for (int result : castInsertResults) {
                if (result != Statement.EXECUTE_FAILED) {
                    stars_in_movies_inserted++;
                }
            }
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<MovieData> filterMovies(Set<String> foundMovies, List<MovieData> movieDataList){
        return movieDataList.stream()
                .filter(movieData -> foundMovies.contains(movieData.getId()))
                .collect(Collectors.toList());
    }
    public List<StarData> filterStars(Set<String> foundStars, List<StarData> starDataList){
        return starDataList.stream()
                .filter(starData -> foundStars.contains(starData.getStage_name()))
                .collect(Collectors.toList());
    }

    private void printInsertionStats() {
        System.out.println("Insertion Statistics:");
        System.out.println("\tGenres Inserted: " + genres_inserted);
        System.out.println("\tMovies Inserted: " + movies_inserted);
        System.out.println("\tStars Inserted: " + stars_inserted);
        System.out.println("\tGenres-In-Movies Inserted: " + genres_in_movies_inserted);
        System.out.println("\tStars-In-Movies Inserted: " + stars_in_movies_inserted);
    }

    public static void main(String[] args) {
        long startTime = System.nanoTime();

        UpdateDatabase updater = new UpdateDatabase();
        MovieHandler movieHandler = new MovieHandler();
        StarHandler starHandler = new StarHandler();

        movieHandler.parseDocument();
        starHandler.parseDocument();
        CastHandler castHandler = new CastHandler(movieHandler.getMovieHashMap().keySet(), starHandler.getStarHashMap().keySet());
        castHandler.parseDocument();

        List<MovieData> movieList =  updater.filterMovies(castHandler.getMovieFoundSet(), movieHandler.getMovieList() );
        List<StarData> starList =  updater.filterStars(castHandler.getStarFoundSet(), starHandler.getStarDataList() );

        updater.insertMovies(movieList);
        updater.insertGenres(movieList);
        updater.insertStars(starList);
        updater.insertCasts(castHandler.getListOfEntries());

        // Stop measuring execution time
        long endTime = System.nanoTime();

        // Calculate the execution time in milliseconds
        long executionTime
                = (endTime - startTime) / 1_000_000_000;

        System.out.println("Parsing and inserting into database takes "
                + executionTime + " seconds");
        updater.printInsertionStats();
        movieHandler.printData();
        starHandler.printData();
        castHandler.printData();
        System.out.println("\tMovies with no stars: "+ (movieHandler.getMovieList().size() - movieList.size() ));
        System.out.println("\tStars with no movies: "+ (starHandler.getStarDataList().size() - starList.size()));

    }

}
