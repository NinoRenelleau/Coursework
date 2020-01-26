let newTag;

function pageLoad(){
    document.getElementById('content').innerHTML = '<h1>Logging out, please wait...</h1>';
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

function create(event) {
    event.preventDefault();
    let formData = new FormData();
    formData.append("username", document.getElementById("username").value);
    formData.append("password", document.getElementById("password").value);
    formData.append("userType", document.getElementById("userType").value);
    formData.append("tags", document.getElementById("tagAssembled").innerText);
    fetch("/users/create", {method: 'post', body: formData}
    ).then(response => response.json()
    ).then(responseData => {
        if (responseData.hasOwnProperty('error')){
            if(!(responseData.username === undefined)){
                alert(responseData.username);
            }
            if(!(responseData.password === undefined)){
                alert(responseData.password);
            }
            if(!(responseData.userType === undefined)){
                alert(responseData.userType);
            }
        } else{
            window.location.href = '/client/login.html?login';
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

function addTag(){
    let Tags = document.getElementById("listOfTags");
    let text = document.getElementById("tagAssembled").innerText;
    let textParts = text.split(";");
    let exists = false;
    newTag = undefined;
    for (let tag of Tags){
        if(tag.selected){
            for (let part of textParts){
                if (tag.value == part){
                    exists = true;
                }
            }
            if(!exists){
                newTag = tag.value;
            }
        }
    }
    if(!(newTag === undefined)){
        document.getElementById("tagAssembled").innerText += newTag + ";";
    }
    newTag = "";
}

function removeTag(){
    if (!(newTag === undefined)){
        let text = document.getElementById("tagAssembled").innerText;
        let textParts = text.split(";");
        let newText = "";
        console.log(textParts);
        if (textParts.length == 2){
            newText = "";
        }else{
            for(let x = 0; x < textParts.length - 2; x++){
                if(x != textParts.length - 2){
                    newText += textParts[x] + ";";
                }
            }
        }
        document.getElementById("tagAssembled").innerText = newText;
    }
}

function goToCreate(event) {
    event.preventDefault();
    window.location.href = '/client/login.html?create';
}
function goToLogin(event) {
    event.preventDefault();
    window.location.href = '/client/login.html?login';
}