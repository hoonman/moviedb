/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {
  // populate the star info h3
  // find the empty h3 body by id "star_info"
  let genreList = jQuery("#genre-list");
  console.log("Selected element:", genreList);

  console.log("handleResult: populating genre list from resultData");
  console.log(genreList);
  console.log(resultData);
  console.log(typeof resultData !== "undefined");
  console.log(resultData.hasOwnProperty("length"));

  if (
    typeof resultData !== "undefined" &&
    resultData.hasOwnProperty("length")
  ) {
    for (let i = 0; i < resultData.length; i++) {
      let rowHTML = "";
      rowHTML =
        '<li><a href="browse_page.html?genreID=' +
        resultData[i]["genreID"] +
        "&page_number=1&page_size=25" +
        '">' +
        resultData[i]["genreName"] +
        "</a></li>";
      // Append the row created to the table body, which will refresh the page
      // console.log(rowHTML);
      genreList.append(rowHTML);
    }
  }
}
function generateLinks() {
  let characterList = jQuery("#name-list");
  const characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ*";
  for (let i = 0; i < characters.length; i++) {
    rowHTML =
      '<li><a href="browse_page.html?nameStartsWith=' +
      characters.charAt(i) +
      "&page_number=1&page_size=25" +
      '">' +
      characters.charAt(i) +
      "</a></li>";
    characterList.append(rowHTML);
  }
}
// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
  dataType: "json", // Setting return data type
  method: "GET", // Setting request method
  url: "api/genre-list",
  success: (resultData) => handleResult(resultData),
});

generateLinks();

jQuery(document).ready(function () {
  let navHTML = '<form id="search-form" class="form-inline ml-auto">';
  navHTML +=
    '<input id="title-input" class="form-control mr-sm-2" type="text" placeholder="Search by Title" aria-label="Search">';
  navHTML +=
    '<input id="year-input" class="form-control mr-sm-2" type="number" placeholder="Search by Year" aria-label="Search">';
  navHTML +=
    '<input id="director-input" class="form-control mr-sm-2" type="text" placeholder="Search by Director" aria-label="Search">';
  navHTML +=
    '<input id="star-name-input" class="form-control mr-sm-2" type="text" placeholder="Search by Star\'s Name" aria-label="Search">';
  navHTML +=
    '<button class="btn btn-outline-light my-2 my-sm-0" type="submit">Search</button>';
  navHTML += "</form>";

  // Append the HTML code to the container
  jQuery("#advance_search").html(navHTML);

  jQuery("#search-form").submit(function (event) {
    event.preventDefault(); // Prevent the default form submission

    // Get the values of the search inputs
    var title = $("#title-input").val();
    var year = $("#year-input").val();
    var director = $("#director-input").val();
    var starName = $("#star-name-input").val();

    // Construct the search URL based on the input values
    // Redirect to the search results page

    window.location.href =
      "browse_page.html?search=true&title=" +
      title +
      "&year=" +
      year +
      "&director=" +
      director +
      "&star_name=" +
      starName +
      "&page_number=1&page_size=25";
  });
});
