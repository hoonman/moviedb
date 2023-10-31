// get the data using GET from the /api/cart
// get the data from payment and upload it as POST

let payment = $("#payment");
let saleId = "";

function handleSessionData(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    let resultArray = resultDataJson["payUser"];
    //depending on whether or not the authorized is true or false, we will alert the user
    let resultArray_i = resultArray[resultArray.length - 1]["authorized"];
    if (resultArray != null) {
        saleId = resultArray[resultArray.length-1].saleId;
        console.log("sale id: ", saleId);
    }


    console.log("resultarray_i's authorization: ", resultArray_i["authorized"]);
    if (resultArray_i === false) {
        alert("Invalid data. Please try again.");
        // clear the form
        payment[0].reset();

    } else {
        alert("payment processed");
        // redirect to confirmation.html. store
        window.location.href = "/cs122b_project1_api_example_war/confirmation.html";

    }
}

let movieName = "";

function handleCartData(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);
    let cost = resultDataJson["previousItems"];
    movieName = "";
    saleId = "";

    let total_cost = 0;
    if (cost != null) {
        movieName = cost[0].movieName;
        console.log("movie name here: ", movieName);
        saleId = cost[0].saleId;
    }

    for (let i = 0; i < cost.length; i++) {
        // iterate through the list and add up the costs
        total_cost += cost[i].cost;
    }
    console.log("total cost is: ", total_cost);
    //need to display the cost somewhere.
    let totalCost = $("#total_cost");
    let res = "";
    res += "<p> Total Cost: " + total_cost + "</p>";
    totalCost.html("");
    totalCost.append(res);
    // onSubmitFunction();
}

function onSubmitFunction(cartEvent) {
    cartEvent.preventDefault();

    let serialized = payment.serialize();
    let movieName2 = "";
    serialized += "&movieId=" + encodeURIComponent(movieName);
    serialized += "&saleId=" + encodeURIComponent(saleId);
    console.log("payment is: ", serialized);
    // make a post request to api/payment
    $.ajax("api/payment", {
        method: "POST",
        data: serialized,
        success: resultDataString => {
            console.log("success");
            $.ajax("api/payment", {
                method: "GET",
                success: handleSessionData
            });
        },
        error: function (err) {
            console.error("Error while sending data to payment servlet", err);
        }
    });
    // $.ajax("api/payment", {
    //     method: "GET",
    //     success: handleSessionData
    // });

    //get the authorization result?

}


//


$.ajax("api/cart", {
    method: "GET",
    success: handleCartData
})

//
// $.ajax("api/payment", {
//     method: "GET",
//     success: handleSessionData
// });
//



payment.submit(onSubmitFunction);

