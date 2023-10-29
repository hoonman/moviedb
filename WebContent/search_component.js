jQuery(document).ready(function () {
  let navHTML = '<nav class="navbar navbar-expand-lg navbar-dark bg-dark">';
  navHTML +=
    '<a class="navbar-brand" href="/cs122b_project1_api_example_war/">Fabflix</a>';
  navHTML +=
    '<button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">';
  navHTML += '<span class="navbar-toggler-icon"></span>';
  navHTML += "</button>";

  navHTML += '<ul class="navbar-nav me-auto mb-2 mb-lg-0">';
  navHTML += '<li class="nav-item">';
  navHTML +=
    '<a class="nav-link" aria-current="page" href="movie-list.html">Movie List</a>';
  navHTML += "</li>";
  navHTML += "</ul>";

  navHTML += '<form id="search-form" class="form-inline ml-auto">';
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
  navHTML += "</nav>";

  // Append the HTML code to the container
  jQuery("#navbar-container").html(navHTML);

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
      "browse_page.html?title=" +
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
