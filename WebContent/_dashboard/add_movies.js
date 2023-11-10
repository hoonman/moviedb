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
            $("#add_movie_error_message").text(status);
        },
        error: function(err) {
            console.log("error while posting data to addmoviesServlet: ", err);
        }

    });
}


addMovies.submit(addData);
