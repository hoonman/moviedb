/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */

let order_string = "RATING_DESC_TABLES_DESC";
let last_page = false;
let page_size_amt = 25;
let page_number_val = 1;
const prevButton = $("#prevBtn");
const nextButton = $("#nextBtn");
const pageCounter = $("#pageCounter");

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
  if (resultData.length < page_size_amt) {
    last_page = true;
  } else {
    last_page = false;
  }
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
    rowHTML +=
      "<th><button class='cart-button' data-movie-title='" +
      resultData[i]["movie_Title"] +
      "'>Add to Cart</button>";
    rowHTML += "</th>";
    rowHTML += "</tr>";
    movieTableBodyElement.append(rowHTML);

    // Append the row created to the table body, which will refresh the page
  }
  $(".cart-button").click(function () {
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
      success: function (response) {
        console.log("Data sent to CartServlet successfully:", response);
        alert("Item successfully added!");
      },
      error: function (err) {
        console.error("Error while sending data to CartServlet:", err);
        alert("Could not add to the shopping cart.");
      },
    });
  });
}

function checkURLChange() {
  var currentPath = window.location.pathname;
  var currentSearchParams = new URLSearchParams(window.location.search);

  var fullRelativeURL = currentPath + "?" + currentSearchParams.toString();
  console.log("full relative url: ", fullRelativeURL);

  var data = {
    URL: fullRelativeURL,
  };
  console.log("data: ", JSON.stringify(data));
  // use post to upsert data int api/jump
  // $.ajax("api/jump", {
  //     method: "POST",
  //     data: data,
  //     success: resultDataString => {
  //         console.log("success loading url into json");
  //     },
  //     error: function(xhr, status, error) {
  //         console.error("error upserting data", error);
  //     }
  //
  // })
  $.ajax({
    type: "POST",
    url: "api/jump",
    data: data,
    success: function (response) {
      console.log("Data sent to CartServlet successfully:", response);
    },
    error: function (err) {
      console.error("Error while sending data to jumpservelt:", err);
    },
  });
}

window.addEventListener("load", checkURLChange);
window.addEventListener("popstate", checkURLChange);

$(".dropdown-item").click(function (event) {
  event.preventDefault();

  var selectedOption = $(this).data("value");
  $("#dropdown").html($(this).text());
  order_string = selectedOption;

  const replacementText = `order=${order_string}`;
  let searchText =
    "order=(TABLES_DESC|RATING_DESC|TABLES_ASC|RATING_ASC)_(TABLES_DESC|RATING_DESC|TABLES_ASC|RATING_ASC)";
  const regex = new RegExp(searchText, "g");
  // let c = window.location.href.replace(regex, replacementText);
  const matches = window.location.href.match(regex);
  if (matches) {
    window.location.href = window.location.href.replace(regex, replacementText);
  } else {
    window.location.href += `&order=${order_string}`;
  }

  // Make an API call based on the selected option
  populate_table(selectedOption, page_size_amt);
});
$(".display-amt-item").click(function (event) {
  event.preventDefault();

  var selectedNumber = $(this).data("value");
  $("#dropdown_display").html($(this).text());
  const replacementText = `page_size=${selectedNumber}`;
  let searchText = "page_size=\\d+";
  const regex = new RegExp(searchText, "g");
  page_size_amt = selectedNumber;
  // let c = window.location.href.replace(regex, replacementText);
  window.location.href = window.location.href.replace(regex, replacementText);
  // Make an API call based on the selected option
  populate_table(order_string, selectedNumber);
});
function updatePageCounter() {
  pageCounter.text(`Page ${page_number_val}`);
}

prevButton.click(() => {
  console.log("pressed prev button");
  if (page_number_val > 1) {
    page_number_val--;
    updatePageCounter();

    const replacementText = `page_number=${page_number_val}`;
    let searchText = "page_number=\\d+";
    const regex = new RegExp(searchText, "g");
    window.location.href = window.location.href.replace(regex, replacementText);
    populate_table(order_string, page_size_amt, page_number_val);
  }
});

nextButton.click(() => {
  console.log("pressed next button");

  if (!last_page) {
    page_number_val++;
    updatePageCounter();
    const replacementText = `page_number=${page_number_val}`;
    let searchText = "page_number=\\d+";
    const regex = new RegExp(searchText, "g");
    console.log("replacementText," + replacementText);
    console.log(window.location.href.replace(regex, replacementText));
    window.location.href = window.location.href.replace(regex, replacementText);
    populate_table(order_string, page_size_amt, page_number_val);
  }
});
function populate_table(
  order_val = null,
  page_size_amt = null,
  page_num = null,
) {
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
  console.log("page_number: " + page_number);
  let nameStartsWith = getParameterByName("nameStartsWith");
  let title = getParameterByName("title");
  let year = getParameterByName("year");
  let director = getParameterByName("director");
  let starName = getParameterByName("star_name");
  let search = getParameterByName("search");
  let fullTextSearch = getParameterByName("full_text_search");
  let query = getParameterByName("query");
  let order = order_val
    ? order_val
    : getParameterByName("order")
    ? getParameterByName("order")
    : "RATING_DESC_TABLES_DESC";
  page_number_val = page_number;
  // Check if necessary parameters are present and make AJAX requests conditionally
  page_number_val = page_number;

  if (fullTextSearch) {
    jQuery.ajax({
      dataType: "json", // Setting return data type
      method: "GET", // Setting request method
      url:
        "api/full-text-search?query=" +
        query +
        "&page_number=" +
        page_number +
        "&page_size=" +
        page_size +
        "&order=" +
        order,
      success: (resultData) => handleResult(resultData),
    });
  } else if (genreID !== null && page_size !== null && page_number !== null) {
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
  } else if (
    nameStartsWith !== null &&
    page_size !== null &&
    page_number !== null
  ) {
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
  } else if (search) {
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
  } else if (!search && !nameStartsWith && !genreID) {
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
  populate_table();
  updatePageCounter();
});
