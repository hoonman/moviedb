/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */

let order_string = "RATING_DESC_TABLES_DESC";
let page_size_amt = 25;
let page_num = 1;

function getParameterByName(target) {
  // Get request URL
  let url = window.location.href;
  // Encode target parameter name to url encoding
  target = target.replace(/[\[\]]/g, "\\$&");

  // Ues regular expression to find matched parameter value
  let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
    results = regex.exec(url);
  if (!results) return null;
  if (!results[2]) return "";

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
  movieTableBodyElement.empty();

  // Concatenate the html tags with resultData jsonObject to create table rows
  for (let i = 0; i < resultData.length; i++) {
    let rowHTML = "";
    rowHTML += '<tr class="table-default">';
    rowHTML +=
      "<th>" +
      '<a href="single-movie.html?id=' +
      resultData[i]["movie_Id"] +
      '">' +
      resultData[i]["movie_Title"] +
      "</a>" +
      "</th>";
    rowHTML += "<th>" + resultData[i]["movie_Year"] + "</th>";
    rowHTML += "<th>" + resultData[i]["director"] + "</th>";
    rowHTML += "<th>";

    for (let j = 0; j < resultData[i]["genres"].length; j++) {
      rowHTML +=
        '<a href ="browse_page.html?genreID=' +
        resultData[i]["genres"][j]["id"] +
        "&page_number=1&page_size=25" +
        '">' +
        resultData[i]["genres"][j]["name"] +
        " " +
        "</a>";
    }
    rowHTML += "</th>";

    rowHTML += "<th>";

    for (let j = 0; j < resultData[i]["stars"].length; j++) {
      rowHTML +=
        '<a href ="single-star.html?id=' +
        resultData[i]["stars"][j]["id"] +
        '">' +
        resultData[i]["stars"][j]["name"] +
        " " +
        "</a>";
    }
    rowHTML += "</th>";
    rowHTML += "<th>" + resultData[i]["rating"] + "</th>";
    rowHTML += "</tr>";

    // Append the row created to the table body, which will refresh the page
    movieTableBodyElement.append(rowHTML);
  }
}
$(".dropdown-item").click(function (event) {
  event.preventDefault();

  var selectedOption = $(this).data("value");
  $("#dropdown").html($(this).text());
  order_string = selectedOption;
  // Make an API call based on the selected option
  populate_table(selectedOption, page_size_amt);
});
$(".display-amt-item").click(function (event) {
  event.preventDefault();

  var selectedNumber = $(this).data("value");
  $("#dropdown_display").html($(this).text());
  page_size_amt = selectedNumber;
  // window.location.href = window.location.href.replace(regex, replacement);
  // Make an API call based on the selected option
  populate_table(order_string, selectedNumber);
});

function populate_table(order, page_size_amt = null, page_num = null) {
  // Get id from URL
  let genreID = getParameterByName("genreID");
  let page_size =
    page_size_amt != null
      ? page_size_amt
      : getParameterByName("page_size")
      ? getParameterByName("page_size")
      : 25;
  let page_number = page_num
    ? page_num
    : getParameterByName("page_number")
    ? getParameterByName("page_number")
    : 1;
  let nameStartsWith = getParameterByName("nameStartsWith");
  let title = getParameterByName("title");
  let year = getParameterByName("year");
  let director = getParameterByName("director");
  let starName = getParameterByName("star_name");
  let search = getParameterByName("search");

  // Check if necessary parameters are present and make AJAX requests conditionally
  if (genreID !== null && page_size !== null && page_number !== null) {
    console.log(genreID, page_size, page_number);
    jQuery.ajax({
      dataType: "json", // Setting return data type
      method: "GET", // Setting request method
      url:
        "api/genre_selection_list?genreID=" +
        genreID +
        "&page_number=" +
        page_number +
        "&page_size=" +
        page_size +
        "&order=" +
        order,
      success: (resultData) => handleResult(resultData),
    });
  }
  if (nameStartsWith !== null && page_size !== null && page_number !== null) {
    console.log(nameStartsWith, page_size, page_number);

    jQuery.ajax({
      dataType: "json", // Setting return data type
      method: "GET", // Setting request method
      url:
        "api/first-character?nameStartsWith=" +
        nameStartsWith +
        "&page_number=" +
        page_number +
        "&page_size=" +
        page_size +
        "&order=" +
        order,
      success: (resultData) => handleResult(resultData),
    });
  }
  if (search) {
    console.log(title, year, director, starName, page_size, page_number);
    if (page_number === null) {
      page_number = 1;
    }
    if (page_size === null) {
      page_size = 25;
    }
    jQuery.ajax({
      dataType: "json", // Setting return data type
      method: "GET", // Setting request method
      url:
        "api/search-movies?title=" +
        title +
        "&year=" +
        year +
        "&director=" +
        director +
        "&star_name=" +
        starName +
        "&page_number=" +
        page_number +
        "&page_size=" +
        page_size +
        "&order=" +
        order,
      success: (resultData) => handleResult(resultData),
    });
  }
  if (!search && !nameStartsWith && !genreID) {
    jQuery.ajax({
      dataType: "json", // Setting return data type
      method: "GET", // Setting request method
      url:
        "api/movie-list?" +
        "&page_number=" +
        page_number +
        "&page_size=" +
        page_size +
        "&order=" +
        order,
      success: (resultData) => handleResult(resultData),
    });
  }
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */
$(document).ready(function () {
  populate_table(order_string);
});
