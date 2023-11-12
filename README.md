# CS 122B Project 2 Team Ja # 

Demo video URL of Project 1: https://www.youtube.com/watch?v=Tv-3f4Oi3nE 
Demo video URL of Project 2: https://youtu.be/yCj33M3dh3I 

**Substring Matching Design**
We used the Like/ILike predicate in the MovieListServlet. We used it for constructing SQL queries for searching movies based on various criteria.
For example, we used LIKE to perform substring matching on the "title" and "director" columns.

**Inconsistencies Report**
Duplicate Handling
  We've taking into consideration for multiple datatypes
* Movies - Check if movie with the same director, year, and title already exist in the database.
* Genres - Check if genre values is already created, if not create a new one.
* Stars - Compare current database to see if an actor with the same name and birthyear already exist
* Stars in Movies - Check if there's already a entry with the same linking for stars and movieID name
Inconsistencies in XML
* Stars with missing birthyears were assigned NULL values
* Stars with missing first and last names were assigned them deriving from their stage name
* Genres that didn't match key table found in doucmentation for code were set to Other
* Movies with missing director, title, year, or genres were not added
* Movies without stars are excluded
* Stars that didn't appear in any movies were excluded.
* Stars with missing stagenames (Key identifier) were excluded
* Movies with missing IDs were excluded.

**Performance Tuning**
1. Pregenerating UUIDs on Java-side
   * By reducing the amount of connections to the mysql server, we're able to reduce the runtime of the parser.
   * Instead of making a new sql connection, for every movie, star, and genre, id creation, they will already be created.
   * Possible caveat would be collisions.
     * Using the birthday problem forumla to detect the probability of collisions
       ```math
        p(n) \approx 1 - e^{-\frac{n^2}{2 \times M}}
       ```
     * With M = \$2^{40}\$ (the total number of possible UUIDs). -  40 bits due to varchar(10) characters.
     * N = 12,000 (the number of generated UUIDs) - seen in amount of films in main.xml file
     * The probality of crashing is 0.0055%
2. Multi-threading
   * By multithreading each mysql connection for all of the movies, stars, and stars_in_movies, there will faster runtime insertion, as each worker will have their own unique connection running in parallel.

**Contributions**

Jason:
* CSS Styling
* Movielist/Index Servlet & html page
* Login, Search and Browse, Extend movie list, sorting, previous/next, extend single page.

Jaehoon:
* Single Movie Servlet & html
* Single Star html
* CSS Styling, jump functionality, shopping cart, payment, and confirmation.
