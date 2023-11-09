function handleResultData(resultData) {
    displayData(resultData);
}

function displayData(data) {
    let movieTableBodyElement = $("#creditcard");
    let movieTableBodyElement2 = $("#customers");
    let movieTableBodyElement3 = $("#employees");
    let movieTableBodyElement4 = $("#genres");
    let movieTableBodyElement5 = $("#genres_in_movies");
    let movieTableBodyElement6 = $("#movies");
    let movieTableBodyElement7 = $("#ratings");
    let movieTableBodyElement8 = $("#sales");
    let movieTableBodyElement9 = $("#stars");
    let movieTableBodyElement10 = $("#stars_in_movies");

    for (let i = 0; i < data.length; i++) {
        let rowHTML = "";
        rowHTML += '<tr class="table-default">';
        if (data[i]["TableName"] === 'creditcards') {
            rowHTML += "<th>" + data[i]["ColumnName"] + "</th>";
            rowHTML += "<th>" + data[i]["DataType"] + "</th>";
        }
        rowHTML += "</tr>";
        movieTableBodyElement.append(rowHTML);
    }
    for (let i = 0; i < data.length; i++) {
        let rowHTML = "";
        rowHTML += '<tr class="table-default">';
        if (data[i]["TableName"] === 'customers') {
            rowHTML += "<th>" + data[i]["ColumnName"] + "</th>";
            rowHTML += "<th>" + data[i]["DataType"] + "</th>";
        }
        rowHTML += "</tr>";
        movieTableBodyElement2.append(rowHTML);
    }
    for (let i = 0; i < data.length; i++) {
        let rowHTML = "";
        rowHTML += '<tr class="table-default">';
        if (data[i]["TableName"] === 'employees') {
            rowHTML += "<th>" + data[i]["ColumnName"] + "</th>";
            rowHTML += "<th>" + data[i]["DataType"] + "</th>";
        }
        rowHTML += "</tr>";
        movieTableBodyElement3.append(rowHTML);
    }
    for (let i = 0; i < data.length; i++) {
        let rowHTML = "";
        rowHTML += '<tr class="table-default">';
        if (data[i]["TableName"] === 'genres') {
            rowHTML += "<th>" + data[i]["ColumnName"] + "</th>";
            rowHTML += "<th>" + data[i]["DataType"] + "</th>";
        }
        rowHTML += "</tr>";
        movieTableBodyElement4.append(rowHTML);
    }
    for (let i = 0; i < data.length; i++) {
        let rowHTML = "";
        rowHTML += '<tr class="table-default">';
        if (data[i]["TableName"] === 'genres_in_movies') {
            rowHTML += "<th>" + data[i]["ColumnName"] + "</th>";
            rowHTML += "<th>" + data[i]["DataType"] + "</th>";
        }
        rowHTML += "</tr>";
        movieTableBodyElement5.append(rowHTML);
    }
    for (let i = 0; i < data.length; i++) {
        let rowHTML = "";
        rowHTML += '<tr class="table-default">';
        if (data[i]["TableName"] === 'movies') {
            rowHTML += "<th>" + data[i]["ColumnName"] + "</th>";
            rowHTML += "<th>" + data[i]["DataType"] + "</th>";
        }
        rowHTML += "</tr>";
        movieTableBodyElement6.append(rowHTML);
    }
    for (let i = 0; i < data.length; i++) {
        let rowHTML = "";
        rowHTML += '<tr class="table-default">';
        if (data[i]["TableName"] === 'ratings') {
            rowHTML += "<th>" + data[i]["ColumnName"] + "</th>";
            rowHTML += "<th>" + data[i]["DataType"] + "</th>";
        }
        rowHTML += "</tr>";
        movieTableBodyElement7.append(rowHTML);
    }
    for (let i = 0; i < data.length; i++) {
        let rowHTML = "";
        rowHTML += '<tr class="table-default">';
        if (data[i]["TableName"] === 'sales') {
            rowHTML += "<th>" + data[i]["ColumnName"] + "</th>";
            rowHTML += "<th>" + data[i]["DataType"] + "</th>";
        }
        rowHTML += "</tr>";
        movieTableBodyElement8.append(rowHTML);
    }
    for (let i = 0; i < data.length; i++) {
        let rowHTML = "";
        rowHTML += '<tr class="table-default">';
        if (data[i]["TableName"] === 'stars') {
            rowHTML += "<th>" + data[i]["ColumnName"] + "</th>";
            rowHTML += "<th>" + data[i]["DataType"] + "</th>";
        }
        rowHTML += "</tr>";
        movieTableBodyElement9.append(rowHTML);
    }
    for (let i = 0; i < data.length; i++) {
        let rowHTML = "";
        rowHTML += '<tr class="table-default">';
        if (data[i]["TableName"] === 'stars_in_movies') {
            rowHTML += "<th>" + data[i]["ColumnName"] + "</th>";
            rowHTML += "<th>" + data[i]["DataType"] + "</th>";
        }
        rowHTML += "</tr>";
        movieTableBodyElement10.append(rowHTML);
    }

}

$.ajax({
    type: "GET",
    url: "api/metadata", // URL of the servlet
    dataType: "json",
    success: handleResultData,
    error: function(error) {
        console.error("Error fetching metadata:", error);
    }
});
