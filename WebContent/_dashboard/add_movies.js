let addMovies = $("#add_movies_form");


function addData(cartEvent) {
    cartEvent.preventDefault();
    let data = addMovies.serialize();
    $.ajax({
        type: "POST",
        url: "api/movies",
        data: data,
        success: function(response) {
            console.log("data sent to the addMovieServlet successfully: ", response);
            let jsonResponse = JSON.parse(response);
            let status = jsonResponse.status;
            let movieId = jsonResponse.movieId;
            let genreId = jsonResponse.genreId;
            let starId = jsonResponse.starId;
            $("#add_movie_error_message").text(status + " " + movieId + " " + genreId + " " + starId);
        },
        error: function(err) {
            console.log("error while posting data to addmoviesServlet: ", err);
        }

    });
}


addMovies.submit(addData);
