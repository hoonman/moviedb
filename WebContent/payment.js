// get the data using GET from the /api/cart
// get the data from payment and upload it as POST

let payment = $("#payment");

function handleSessionData(resultDataString) {
    console.log("payment is: ",payment.serialize());
    let resultDataJson = JSON.parse(resultDataString);

    let resultArray = resultDataJson["payUser"];
    //depending on whether or not the authorized is true or false, we will alert the user
    let resultArray_i = resultArray[resultArray.length - 1]["authorized"];

    console.log("resultarray_i's authorization: ", resultArray_i["authorized"]);
    if (resultArray_i === false) {
        alert("invalid data!");
    } else {
        alert("payment processed");
    }
}

function handleCartData(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);
    let cost = resultDataJson["previousItems"];

    let total_cost = 0;
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
}

function onSubmitFunction() {
    console.log("payment is: ", payment.serialize());
    // make a post request to api/payment
    $.ajax("api/payment", {
        method: "POST",
        data: payment.serialize(),
        // success:
        success: function(response) {
            console.log("Data sent to CartServlet successfully:", response);
        },
        error: function(err) {
            console.error("Error while sending data to CartServlet:", err);
        }
    });
    //get the authorization result?

}

//

payment.submit(onSubmitFunction);

$.ajax("api/payment", {
    method: "GET",
    success: handleSessionData
});

$.ajax("api/cart", {
    method: "GET",
    success: handleCartData
})




