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
    '<a class="nav-link" aria-current="page" href="browse_page.html">Movie List</a>';
  navHTML += "</li>";
  navHTML += '<li class="nav-item">';
  navHTML +=
    '<a class="nav-link" aria-current="page" href="cart.html">Checkout</a>';
  navHTML += "</li>";
  navHTML += "</ul>";

  navHTML += '<form id="full-search-form" class="form-inline ml-auto">';
  navHTML +=
    '<input type="text" name="hero" id="autocomplete" \n class="form-control-lg autocomplete-searchbox form-control" placeholder="Full Text Search"/>';

  navHTML +=
    '<button class="btn btn-outline-light my-2 my-sm-0" type="submit">Search</button>';
  navHTML += "</form>";
  navHTML += "</nav>";

  // Append the HTML code to the container
  jQuery("#navbar-container").html(navHTML);

  jQuery("#full-search-form").submit(function (event) {
    event.preventDefault(); // Prevent the default form submission

    // Get the values of the search inputs
    var title = $("#full-title-input").val();

    // Construct the search URL based on the input values
    // Redirect to the search results page
    console.log("nav bar search button clicked" + title);
    console.log("autocomplete value" + $("#autocomplete").val());
    handleNormalSearch($("#autocomplete").val());
  });

  /*
   * This function is called by the library when it needs to lookup a query.
   *
   * The parameter query is the query string.
   * The doneCallback is a callback function provided by the library, after you get the
   *   suggestion list from AJAX, you need to call this function to let the library know.
   */
  function handleLookup(query, doneCallback) {
    console.log("autocomplete initiated");

    // TODO: if you want to check past query results first, you can do it here
    // Construct a unique key for localStorage based on the query
    const localStorageKey = `search-${query}`;
    // Check if the query result is already cached in localStorage
    const cachedResults = localStorage.getItem(localStorageKey);
    if (cachedResults) {
      console.log("Using Cached results");
      handleLookupAjaxSuccess(cachedResults, query, doneCallback);
    } else {
      console.log("sending AJAX request to backend Java Servlet");
      // sending the HTTP GET request to the Java Servlet endpoint hero-suggestion
      // with the query data
      jQuery.ajax({
        method: "GET",
        // generate the request url from the query.
        // escape the query string to avoid errors caused by special characters
        url: "api/movie-suggestion?query=" + escape(query),
        success: function (data) {
          // pass the data, query, and doneCallback function into the success handler
          handleLookupAjaxSuccess(data, query, doneCallback);
        },
        error: function (errorData) {
          console.log("lookup ajax error");
          console.log(errorData);
        },
      });
    }
  }
  function handleLookupAjaxSuccess(data, query, doneCallback) {
    console.log("lookup ajax successful");

    // parse the string into JSON
    var jsonData = JSON.parse(data);
    console.log(jsonData);

    // TODO: if you want to cache the result into a global variable you can do it here
    const localStorageKey = `search-${query}`;
    localStorage.setItem(localStorageKey, data);

    // call the callback function provided by the autocomplete library
    // add "{suggestions: jsonData}" to satisfy the library response format according to
    //   the "Response Format" section in documentation
    doneCallback({ suggestions: jsonData });
  }

  /*
   * This function is the select suggestion handler function.
   * When a suggestion is selected, this function is called by the library.
   *
   * You can redirect to the page you want using the suggestion data.
   */
  function handleSelectSuggestion(suggestion) {
    window.location.href = "single-movie.html?id=" + suggestion["data"];
  }

  /*
   * This statement binds the autocomplete library with the input box element and
   *   sets necessary parameters of the library.
   *
   * The library documentation can be find here:
   *   https://github.com/devbridge/jQuery-Autocomplete
   *   https://www.devbridge.com/sourcery/components/jquery-autocomplete/
   *
   */
  // $('#autocomplete') is to find element by the ID "autocomplete"
  $("#autocomplete").autocomplete({
    // documentation of the lookup function can be found under the "Custom lookup function" section
    lookup: function (query, doneCallback) {
      if (query.length >= 3) handleLookup(query, doneCallback);
    },
    onSelect: function (suggestion) {
      handleSelectSuggestion(suggestion);
    },
    // set delay time
    deferRequestBy: 300,
    // there are some other parameters that you might want to use to satisfy all the requirements
    // TODO: add other parameters, such as minimum characters
  });

  /*
   * do normal full text search if no suggestion is selected
   */
  function handleNormalSearch(query) {
    console.log("doing normal search with query: " + query);
    // TODO: you should do normal search here
    // Construct the search URL based on the input values
    // Redirect to the search results page

    window.location.href =
      "browse_page.html?full_text_search=true&query=" + query;
  }

  // bind pressing enter key to a handler function
  $("#autocomplete").keypress(function (event) {
    // keyCode 13 is the enter key
    if (event.keyCode == 13) {
      // pass the value of the input box to the handler function
      handleNormalSearch($("#autocomplete").val());
    }
  });
});
