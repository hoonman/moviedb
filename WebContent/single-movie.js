/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs three steps:
 *      1. Get parameter from request URL so it know which id to look for
 *      2. Use jQuery to talk to backend API to get the json data.
 *      3. Populate the data to correct html elements.
 */


/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {
    let movieInfoElement = jQuery("#movie_info");
    movieInfoElement.append("<h1> " + resultData[0]["movie_Title"] + "</h1>");
    let movieTableBodyElement = jQuery("#single_movie_table_body");
    let rowHTML = "";
    rowHTML += "<tr>";
    rowHTML += "<th>" + resultData[0]["movie_Year"] + "</th>";
    rowHTML += "<th>" + resultData[0]["director"] + "</th>";
    rowHTML += "<th>";
    for(let j = 0; j< resultData[0]["genres"].length; j++){
        rowHTML += '<a href ="browse_page.html?genreID=' + resultData[0]["genres"][j]["id"] +"&page_number=1&page_size=25"+ '">' +  resultData[0]["genres"][j]["name"] +" "+"</a>";
    }

    rowHTML += "</th>";
    rowHTML += "<th>";
    for(let j = 0; j< resultData[0]["stars"].length; j++){
        if (j == resultData[0]["stars"].length - 1) {
            // don't add the comma when hyou add it
            rowHTML +=  '<a href ="single-star.html?id=' + resultData[0]["stars"][j]["id"] + '">' +  resultData[0]["stars"][j]["name"] +" "+"</a>" ;
        } else {
            rowHTML +=  '<a href ="single-star.html?id=' + resultData[0]["stars"][j]["id"] + '">' +  resultData[0]["stars"][j]["name"] +", "+"</a>" ;
        }

    }
    rowHTML += "<th>" + resultData[0]["rating"] + "</th>";
    rowHTML += "<th><button class='cart-button' data-movie-title='" + resultData[0]["movie_Title"] + "'>Add to Cart</button>"
    rowHTML += "</th>";
    rowHTML += "</tr>";
    movieTableBodyElement.append(rowHTML);
    $(".cart-button").click(function() {
        // Access the "data-movie-title" attribute of the clicked button
        var movieTitle = $(this).data("movie-title");

        // Do something with the movieTitle, e.g., add it to the cart
        console.log("Movie Title: " + movieTitle);
        // make a ajax request to send the data
        var data = {
            movieName: movieTitle,
            quantity: 1,
            cost: 30,
            remove: "No",
        };

        console.log("data looks like: ", data);

        $.ajax({
            type: "POST",
            url: "api/cart",
            data: data,
            success: function(response) {
                console.log("Data sent to CartServlet successfully:", response);
                alert("Item successfully added!");
            },
            error: function(err) {
                console.error("Error while sending data to CartServlet:", err);
                alert("Could not add to shopping cart.");
            }
        });
    });
}

let url = null;
function handleJumpData(resultData) {
    let resultDataJson = JSON.parse(resultData);
    let resultDataJsonURL = resultDataJson["currURL"];
    // find the last index
    url = resultDataJsonURL[resultDataJsonURL.length - 1].URL;
    console.log("url is: ", url);

    if (url == null) {
        window.location.href = '/cs122b_project1_api_example_war/browse.html';
    } else{
        window.location.href = url;
    }

}

$("#jump_button").click(function() {
    $.ajax("api/jump", {
        method: "GET",
        success: handleJumpData
    });
})


/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let starId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + starId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});