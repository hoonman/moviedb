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

    console.log(resultData);

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    // console.log(resultData[0]["genres"])
    // console.log(resultData[1]["genres"].length)
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

// Get id from URL
let genreID = getParameterByName('genreID');
let page_size = getParameterByName('page_size');
let page_number = getParameterByName('page_number');
let nameStartsWith = getParameterByName('nameStartsWith');


// Check if necessary parameters are present and make AJAX requests conditionally
if (genreID !== null && page_size !== null && page_number !== null) {
    console.log(genreID,page_size,page_number);
    jQuery.ajax({
        dataType: "json",  // Setting return data type
        method: "GET",      // Setting request method
        url: "api/genre_selection_list?genreID=" + genreID + "&page_number=" + page_number + "&page_size=" + page_size,
        success: (resultData) => handleResult(resultData)
    });
}

if (nameStartsWith !== null && page_size !== null && page_number !== null) {
    console.log(nameStartsWith,page_size,page_number);

    jQuery.ajax({
        dataType: "json",  // Setting return data type
        method: "GET",      // Setting request method
        url: "api/first-character?nameStartsWith=" + nameStartsWith + "&page_number=" + page_number + "&page_size=" + page_size,
        success: (resultData) => handleResult(resultData)
    });
}
