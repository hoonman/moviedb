
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
    console.log(typeof resultData!== 'undefined');
    console.log(resultData.hasOwnProperty('length'));
    // resultData.forEach(function (item) {
    //     console.log(item.name, item.value);
    // });
    // Concatenate the html tags with resultData jsonObject to create table rows
    if(typeof resultData !== 'undefined' && resultData.hasOwnProperty('length')) {
        for (let i = 0; i < resultData.length; i++) {
            let rowHTML = "";
            rowHTML = '<li><a href="browse_page.html?genre=' + resultData[i]['genreID'] + '">' + resultData[i]["genreName"] + '</a></li>';
            // Append the row created to the table body, which will refresh the page
            console.log(rowHTML)
            genreList.append(rowHTML);
        }
    }
}
function generateLinks() {
    let characterList = jQuery("#name-list");
    const characters = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ*';
    for(let i =0; i< characters.length; i++){
        rowHTML = '<li><a href="browse_page.html?nameStartsWith=' + characters.charAt(i) + '">' + characters.charAt(i)+ '</a></li>';
        characterList.append(rowHTML)

    }
}
// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/genre-list", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});

generateLinks();