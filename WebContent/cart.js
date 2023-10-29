// get the data from api/json

// parse the data

// include it into our page.

let globalResultData = null;
function handleSessionData(resultData) {
    let resultDataJson = JSON.parse(resultData);
    console.log("resultdatajson is: ",resultDataJson);

    //display the data now
    // globalResultdata = resultDataJson["previousItems"];
    globalResultData = resultDataJson;

    displayData(resultDataJson["previousItems"]);

}

function displayData(previousItemArr) {
    let cartTable = $("#cart_body");
    cartTable.html("");

    for (let i = 0; i < previousItemArr.length; i++) {
        let movieName = previousItemArr[i].movieName;
        let quantity = previousItemArr[i].quantity;
        let cost = previousItemArr[i].cost;
        let rowHTML = "<tr>";
        rowHTML += "<th>" + movieName + "</th>";
        rowHTML += "<th><button class='plus' data-movie-title='" + movieName + "'>+</button>";
        rowHTML += quantity
        rowHTML += "<button class='minus' data-movie-title='" + movieName + "'>-</button></th>";
        rowHTML += "<th><button class='delete' data-movie-title='" + movieName + "'> Delete </button></th>";
        rowHTML += "<th>" + cost + "</th>";
        rowHTML += "<th>" + cost + "</th>";
        rowHTML += "</tr>";
        rowHTML += "</tr>";
        cartTable.append(rowHTML);

    }

    $(".minus").click(function() {
        // Access the "data-movie-title" attribute of the clicked button
        var movieTitle = $(this).data("movie-title");

        // Do something with the movieTitle, e.g., add it to the cart
        console.log("Movie Title: " + movieTitle);
        // make a ajax request to send the data

        let globalResultData2 = globalResultData["previousItems"];

        var data = {
            movieName: movieTitle,
            quantity: 1,
            cost: 30,
            remove: "No"
        };
        for (let i = 0; i < globalResultData2.length; i++) {
            if (globalResultData2[i].movieName === movieTitle) {
                // increment the quantity of globalResultData[i]
                let finalQuantity = globalResultData2[i].quantity - 1;
                console.log(finalQuantity)
                if (finalQuantity === 0) {
                    //remove it from the list
                    data.remove = "delete";
                } else {
                    data.remove = "Yes";
                    data.quantity = finalQuantity;
                }

            }
        }

        $.ajax({
            type: "POST",
            url: "api/cart",
            data: data,
            success: function(response) {
                console.log("Data sent to CartServlet successfully:", response);
            },
            error: function(err) {
                console.error("Error while sending data to CartServlet:", err);
            }
        });

        // $.ajax({
        //     type: "POST",
        //     url: "api/cart",
        //     data: data,
        //     success: function(response) {
        //         console.log("Data sent to CartServlet successfully:", response);
        //     },
        //     error: function(err) {
        //         console.error("Error while sending data to CartServlet:", err);
        //     }
        // });
    });

}

// $(document).on("click", ".plus", function() {
//     // we meed to modify the result data and send it back
//     let movieName = $(this).data("movieId");
//     console.log("plus button clicked: ", movieName);
//
//     // Find the item in the globalResultData and modify it
//     // let itemToModify = globalResultData.previousItems.find(item => item.movieName === movieName);
//     let itemToModify = null;
//     for (let i = 0; i < globalResultdata.length; i++) {
//         if (globalResultdata[i] === movieName) {
//             // increment the quantity of globalResultData[i]
//             globalResultdata[i].quantity += 1;
//         }
//     }
//
//     // Send the modified data back to the server using a POST request
//     $.ajax("api/cart", {
//         method: "POST",
//         data: JSON.stringify(globalResultData), // Send the modified data
//         contentType: "application/json",
//         success: function (response) {
//             // Handle the response if needed
//         }
//     });
// });
//
$.ajax("api/cart", {
    method: "GET",
    success: handleSessionData
})