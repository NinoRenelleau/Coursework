function pageLoad() {

    let now = new Date();

    let myHTML = '<div style="text-align:center;">'
        + '<h1>Welcome to my API powered website!</h1>'
        + '<div style="font-style: italic;">'
        + 'Generated at ' + now.toLocaleTimeString()
        + '</div>'
        + '</div>';

    document.getElementById("testDiv").innerHTML = myHTML;

}
