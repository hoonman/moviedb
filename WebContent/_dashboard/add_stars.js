let newStars = $("#add_stars_form");
function handleSubmitResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);
    console.log("add stars status: ", resultDataJson["status"]);
    //if the status is success, we display a success message, if not we display a fail message

    // if (resultDataJson["status"] === "success") {
    $("#add_star_error_message").text(resultDataJson["status"]);
}
function addData(cartEvent) {
    //get the data from the form
    cartEvent.preventDefault();
    let data = newStars.serialize();
    console.log(data); //success is already in here
    $.ajax({
        type: "POST",
        url: "api/stars",
        data: data,
        success: function(response) {
            console.log("Data sent to starsServlet successfully:", response);
            let jsonResponse = JSON.parse(response);
            let status = jsonResponse.status;
            console.log("the status is: ", status);
            $("#add_star_error_message").text(status);
        },
        error: function(err) {
            console.error("Error while sending data to starsServlet:", err);
        }
    });

    // $.ajax({
    //     type: "GET",
    //     url: "api/stars",
    //     success: handleSubmitResult
    // });

}


newStars.submit(addData)

