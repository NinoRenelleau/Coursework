function pageLoad(){
    if(window.location.search === '?logout'){
        document.getElementById('content').innerHTML = '<h1>Logging out, please wait...</h1>';
        logout();
    } else{
        document.getElementById("loginButton").addEventListener("click", login);
    }
}
function login(event) {
    event.preventDefault();
    const form = document.getElementById("loginForm");
    const formData = new FormData(form);

    fetch("/users/login", {method: 'post', body: formData}
    ).then(response => response.json()
    ).then(responseData => {
        if (responseData.hasOwnProperty('error')){
            alert(responseData.error);
        } else{
            Cookies.set("username", responseData.username);
            Cookies.set("token", responseData.token);
            Cookies.set("id", responseData.userID);

            window.location.href = '/client/index.html';
        }
    });
}
function logout(){
    fetch("/users/logout", {method: 'post'}
    ).then(response => response.json()
    ).then(responseData =>{
        if(responseData.hasOwnProperty('error')){
            alert(responseData.error);
        } else {
            Cookies.remove("username");
            Cookies.remove("token");
            Cookies.remove("id");

            window.location.href = '/client/index.html';
        }
    });
}