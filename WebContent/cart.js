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
    let total_cost = 0;
    let rowHTML = "<tr>";

    for (let i = 0; i < previousItemArr.length; i++) {
        let movieName = previousItemArr[i].movieName;
        let quantity = previousItemArr[i].quantity;
        let totalCost = previousItemArr[i].cost;
        let cost = previousItemArr[i].cost / quantity;
        rowHTML += "<th>" + movieName + "</th>";
        rowHTML += "<th><button class='plus' data-movie-title='" + movieName + "'>+</button>";
        rowHTML += "<span class='quantity'>" + quantity + "</span>";
        rowHTML += "<button class='minus' data-movie-title='" + movieName + "'>-</button></th>";
        rowHTML += "<th><button class='delete' data-movie-title='" + movieName + "'> Delete </button></th>";
        rowHTML += "<th class='cost'>" + cost + "</th>";
        rowHTML += "<th class='totalCost'>" + totalCost + "</th>";
        total_cost += totalCost;
        rowHTML += "</tr>";

    }
    cartTable.append(rowHTML);

    let total = $("#payment_list");
    let rowHTML2 = "";
    rowHTML2 += "<p class='total'> Total Cost is " + total_cost + " </p>"
    total.append(rowHTML2);

    $(".minus").click(function() {
        var movieTitle = $(this).data("movie-title");
        var quantity = $(this).closest("tr").find(".quantity");
        var cost= $(this).closest("tr").find(".totalCost");
        var currentCost= parseInt(cost.text());
        var currentQuantity = parseInt(quantity.text());
        let rowToDelete = $("#cart_body tr").filter(function () {
            return $(this).find("th:first").text() === movieTitle;
        });
        var total = $(this).closest("p").find(".total");
        // var grandTotal = total.text();

        var data = {
            movieName: movieTitle,
            quantity: 1,
            cost: 30,
            remove: "Yes"
        };
        if (currentQuantity <= 1) {
            console.log("quantity is zero we must remove");
            //delete the movie
            rowToDelete.remove();
            data.remove = "Delete";

        } else {
            data.quantity = data.quantity - 1;
            data.remove = "Yes";
            quantity.text(currentQuantity - 1);
            cost.text(currentCost - data.cost);
        }

        sendPost(data);

    });
    $(".plus").click(function() {
        var movieTitle = $(this).data("movie-title");
        var quantity = $(this).closest("tr").find(".quantity");
        var cost= $(this).closest("tr").find(".totalCost");
        var currentQuantity = parseInt(quantity.text());
        var currentCost = parseInt(cost.text());

        let globalResultData2 = globalResultData["previousItems"];

        var data = {
            movieName: movieTitle,
            quantity: 1,
            cost: 30,
            remove: "No"
        };
        data.quantity = data.quantity + 1;
        quantity.text(currentQuantity + 1);
        cost.text(currentCost + data.cost);
        sendPost(data);

    });

    $(".delete").click(function() {
        let movieTitle = $(this).data("movie-title");
        let rowToDelete = $("#cart_body tr").filter(function () {
            return $(this).find("th:first").text() === movieTitle;
        });
        var data = {
            movieName: movieTitle,
            quantity: 1,
            cost: 30,
            remove: "Delete"
        };
        globalResultData2 = globalResultData["previousItems"];

        rowToDelete.remove();
        sendPost(data);
    });
}

$(".payment").click(function() {
    //redirect the user to the payment page
    window.location.href = '/cs122b-project1-api-example/payment.html';
    sendPost();
});



function sendPost(data) {
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
}



$.ajax("api/cart", {
    method: "GET",
    success: handleSessionData
})