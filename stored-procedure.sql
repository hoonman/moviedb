

DELIMITER //
CREATE PROCEDURE insert_star(IN star_name VARCHAR(255), IN birth_year INT)
BEGIN
    DECLARE max_id VARCHAR(255);
    DECLARE new_star_id VARCHAR(255);

SELECT substring(max(id), 3) INTO max_id from stars;
SET max_id = cast(max_id as unsigned);
    -- SELECT MAX(CAST(SUBSTRING(id, 3) AS UNSIGNED)) INTO max_id FROM stars;

    -- If max_id is null, set new_star_id to "nm0000001"; otherwise, increment max_id and set it to new_star_id
    IF max_id IS NULL THEN
        SET new_star_id = 'nm0000001';
ELSE
        SET new_star_id = CONCAT('nm', LPAD(CAST(max_id + 1 AS CHAR), 7, '0'));
END IF;

    -- Insert the new star with the generated ID
INSERT INTO stars(id, name, birthYear) VALUES(new_star_id, star_name, birth_year);
END //
DELIMITER ;


--Stored procedure for add_movie
DELIMITER //

CREATE PROCEDURE add_movie(
    IN movie_title VARCHAR(100),
    IN movie_year INT,
    IN movie_director VARCHAR(100),
    IN movie_star_name VARCHAR(50),
    IN movie_genre_name VARCHAR(50)
)
BEGIN
    DECLARE genre_id VARCHAR(10);
    DECLARE star_id VARCHAR(10);
    DECLARE movie_id VARCHAR(10);
    DECLARE max_id INT;
    DECLARE max_genre_id INT;
    DECLARE max_star_id VARCHAR(100);
    DECLARE genre_toggle INT;
    DECLARE star_toggle INT;
    SET genre_toggle = 0;
    SET star_toggle = 0;
SELECT SUBSTRING(MAX(id), 3) INTO max_star_id FROM stars;
SET max_star_id = cast(max_star_id as unsigned);
SELECT MAX(CONVERT(id, UNSIGNED)) INTO max_genre_id FROM genres;
-- SELECT MAX(CAST(SUBSTRING(id, 3) AS UNSIGNED)) INTO max_star_id FROM stars;
SELECT MAX(CAST(SUBSTRING(id, 3) AS UNSIGNED)) INTO max_id FROM movies;

SELECT id INTO genre_id from genres where name = movie_genre_name LIMIT 1;
IF genre_id IS NULL THEN
        SET genre_toggle = 1;
        IF max_genre_id IS NULL THEN
            SET genre_id = 1;
ELSE
            SET genre_id = max_genre_id + 1;
END IF;
END IF;

SELECT id INTO star_id from stars where name = movie_star_name LIMIT 1;
IF star_id IS NULL THEN
        SET star_toggle = 1;
        IF max_star_id IS NULL THEN
            SET star_id = 'nm0000001';
ELSE
            SET star_id = CONCAT('nm', LPAD(CAST(max_star_id + 1 AS CHAR), 7, '0'));
END IF;
END IF;
    -- if the stars and genres already exist, we need to set their id to those respectively
    -- Check if the movie already exists
SELECT id INTO movie_id FROM movies WHERE title = movie_title AND `year` = movie_year AND director = movie_director;
IF movie_id IS NULL THEN
        IF max_id IS NULL THEN
            SET movie_id = 'tt0000001';
ELSE
            SET movie_id = CONCAT('tt', CAST(max_id + 1 AS CHAR));
END IF;
        -- insert star and genre
        IF (star_toggle = 1) THEN
            INSERT INTO stars (id, name)
            VALUES (star_id, movie_star_name);
END IF;
        IF (genre_toggle = 1) THEN
            INSERT INTO genres (id, name)
            VALUES (genre_id, movie_genre_name);
END IF;

INSERT INTO movies (id, title, `year`, director)
VALUES (movie_id, movie_title, movie_year, movie_director);

-- add the new genre
INSERT INTO stars_in_movies (starId, movieId)
VALUES (star_id, movie_id);

INSERT INTO genres_in_movies (genreId, movieId)
VALUES (genre_id, movie_id);

END IF;

END //

DELIMITER ;
