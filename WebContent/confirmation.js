//get request to get data from api/cart

function handleSessionData(resultData) {
    let resultDataJson = JSON.parse(resultData);
    let previousItems = resultDataJson["previousItems"];
    console.log("previousItems: ", previousItems);
    //now we need to get all the movies and display it on html
    handleCartArray(previousItems);
}

function handleCartArray(resultArray) {
    let item_list = $("#item_list");
    let res = "<ul>";
    let total_total = 0;
    for (let i = 0; i < resultArray.length; i++) {
        res += "<li>" + "Movie: " + resultArray[i].movieName +"   quantity: " + resultArray[i].quantity + "   Total: " + resultArray[i].cost + "</li>";
        total_total += resultArray[i].cost;
    }
    res += "<p> Total Cost is " + total_total + " </p>"
    res += "</ul>";
    item_list.html("");
    item_list.append(res);

}



$.ajax("api/cart", {
    //calls handlesessiondata function
    method: "GET",
    success: handleSessionData
});