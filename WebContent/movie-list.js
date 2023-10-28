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

    console.log("handleResult: populating star info from resultData");

    // populate the star info h3
    // find the empty h3 body by id "star_info"
    let starInfoElement = jQuery("#star_info");

    // append two html <p> created to the h3 body, which will refresh the page
    starInfoElement.append("<p>Star Name: " + resultData[0]["star_name"] + "</p>" +
        "<p>Date Of Birth: " + resultData[0]["star_dob"] + "</p>");

    console.log("handleResult: populating movie table from resultData");
    console.log(resultData);

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");
    console.log(resultData)

    console.log(resultData[0]["genres"])
    console.log(resultData[1]["genres"].length)
    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < Math.min(20, resultData.length); i++) {
        let rowHTML = "";
        rowHTML += "<tr class=\"table-default\">";
        rowHTML += "<th>" +'<a href="single-movie.html?id=' + resultData[i]['movie_Id'] + '">' +  resultData[i]["movie_Title"] +"</a>"+ "</th>";
        rowHTML += "<th>" + resultData[i]["movie_Year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["director"] + "</th>";
        rowHTML += "<th>";
        for(let j = 0; j< resultData[i]["genres"].length; j++){
            rowHTML += '<a href ="browse_page.html?genreID=' + resultData[i]["genres"][j]["id"] +"&page_number=1&page_size=25"+ '">' +  resultData[i]["genres"][j]["name"] +" "+"</a>";
        }
        rowHTML += "</th>";

        rowHTML += "<th>";

        for(let j = 0; j< resultData[i]["stars"].length; j++){
            rowHTML += '<a href ="single-star.html?id=' + resultData[i]["stars"][j]["id"] + '">' +  resultData[i]["stars"][j]["name"] +" "+"</a>";
        }
        rowHTML += "</th>";
        rowHTML += "<th>" + resultData[i]["rating"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */


// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/movie-list",
    success: (resultData) => handleResult(resultData)
});