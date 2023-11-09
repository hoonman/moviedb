let newStars = $("#add_stars_form");

// function handleSessionData(resultDataString) {
//     let resultDataJson = JSON.parse(resultDataString);
//     let resultData = resultDataJson["newStar"];
//     var data = {
//         starName: resultData[0],
//         birthYear: resultData[1]
//     };
//     sendPost(data);
// }

function addData(cartEvent) {
    //get the data from the form

    cartEvent.preventDefault();
    let data = newStars.serialize();
    console.log(data);
    $.ajax({
        type: "POST",
        url: "api/stars",
        data: data,
        success: function(response) {
            console.log("Data sent to starsServlet successfully:", response);
        },
        error: function(err) {
            console.error("Error while sending data to starsServlet:", err);
        }
    });

}
// function sendPost(data) {
//     $.ajax({
//         type: "POST",
//         url: "api/stars",
//         data: data,
//         success: function(response) {
//             console.log("Data sent to CartServlet successfully:", response);
//         },
//         error: function(err) {
//             console.error("Error while sending data to CartServlet:", err);
//         }
//     });
// }

// $.ajax("api/stars", {
//     method: "GET",
//     success:
// });

newStars.submit(addData)

