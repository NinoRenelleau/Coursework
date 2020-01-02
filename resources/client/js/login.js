let newTag;

function pageLoad(){
    if(window.location.search === '?logout'){
        document.getElementById('content').innerHTML = '<h1>Logging out, please wait...</h1>';
        logout();
    } else if (window.location.search === '?login'){
        let loginHTML = '<div class="loginDiv">\n' +
            '            <label for="username">Username: </label>\n' +
            '            <input type="text" name="username" id="username">\n' +
            '        </div>\n' +
            '        <div class="loginDiv">\n' +
            '            <label for="password">Password: </label>\n' +
            '            <input type="password" name="password" id="password">\n' +
            '        </div>\n' +
            '        <div class="loginDiv">\n' +
            '            <input type="submit" value="Login" id="loginButton">\n' +
            '        </div>';
        document.getElementById("loginForm").innerHTML = loginHTML;
        document.getElementById("loginButton").addEventListener("click", login);
        document.getElementById("sign").innerText = "Create a new account";
        document.getElementById("sign").addEventListener("click", goToCreate)
    }else if (window.location.search === '?create'){
        let createHTML = '<div class="loginDiv">\n' +
            '            <label for="username">Username: </label>\n' +
            '            <input type="text" name="username" id="username">\n' +
            '        </div>\n' +
            '        <div class="loginDiv">\n' +
            '            <label for="password">Password: </label>\n' +
            '            <input type="password" name="password" id="password">\n' +
            '        </div>\n' +
            '        <div class="loginDiv">\n' +
            '            <p>Add tags to describe your account: <p/>\n' +
            '            <select id="userType" style="vertical-align: middle; display: inline;" name="tags">\n' +
            '                <option value="student">Student</option>\n' +
            '                <option value="teacher">Teacher</option>\n' +
            '            </select>\n' +
            '        </div>\n' +
            '        <div class="loginDiv">\n' +
            '            <p>Add tags to describe your account: <p/>\n' +
            '            <select id="listOfTags" style="vertical-align: middle; display: inline;" name="tags">\n' +
            '                <option value="mathematics">Maths</option>\n' +
            '                <option value="physics">Physics</option>\n' +
            '                <option value="mechanics">Mechanics</option>\n' +
            '                <option value="statistics">Statistics</option>\n' +
            '                <option value="computerScience">Computer Science</option>\n' +
            '                <option value="english">English</option>\n' +
            '                <option value="geography">Geography</option>\n' +
            '                <option value="aLevel">A level</option>\n' +
            '                <option value="gcse">GCSE</option>\n' +
            '            </select>\n' +
            '            <a onclick="addTag()" class="tag" style="vertical-align: middle; display: inline;" >Add tag</a>\n' +
            '            <a onclick="removeTag()" class="tag" style="vertical-align: middle; display: inline;" >Remove tag</a>\n' +
            '            <div id="tagAssembled"></div>\n' +
            '        </div>\n' +
            '        <div class="loginDiv">\n' +
            '            <input type="submit" value="Create" id="loginButton">\n' +
            '        </div>';
        document.getElementById("loginForm").innerHTML = createHTML;
        document.getElementById("loginButton").addEventListener("click", create);
        document.getElementById("sign").innerText = "Login";
        document.getElementById("sign").addEventListener("click", goToLogin)
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

function create(event) {
    event.preventDefault();
    let formData = new FormData();
    formData.append("username", document.getElementById("username").value);
    formData.append("password", document.getElementById("password").value);
    formData.append("userType", document.getElementById("userType").value);
    formData.append("tags", document.getElementById("tagAssembled").innerText)
    fetch("/users/create", {method: 'post', body: formData}
    ).then(response => response.json()
    ).then(responseData => {
        if (responseData.hasOwnProperty('error')){
            alert(responseData.error);
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