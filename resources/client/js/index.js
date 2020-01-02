function pageLoad() {

    let coursesHTML = '<table>' +
        '<tr>' +
        '<th>Id</th>' +
        '<th>Course</th>' +
        '<th>Creator</th>' +
        '<th class="rating">Rating</th>' +
        '<th>Tags</th>' +
        '</tr>';

    let id;
    let nullID = false;
    id = Cookies.get("id");
    console.log(id);
    if (id === undefined){
        id = "1";
        nullID = true;
    }

    document.getElementById("create").addEventListener("click", goToCreate);
    console.log(id);
    fetch('/courses/list/'+id, {method: 'get'}).then(response => response.json()).then(courses => {

        for (let course of courses){
            let starNum = Math.round(course.rating);
            let starText = '';
            let courseID = course.courseID;
            for (i = 0; i < 5; i++){
                if (starNum == 0){
                    starText += '<span class="fa fa-star"></span>'
                } else{
                    starText += '<span class="fa fa-star checked"></span>';
                    starNum -= 1;
                }
            }

            let progress = course.score;
            let total = course.Total;
            if (nullID){
                total = 0;
                progress = 0;
            }
            let percentage = 0;
            if (total != 0){
                percentage = Math.floor((progress/total)*100);
            }

            let progressBar = '<div class="container">' +
                '<div class="progress" style="width: 300px;">' +
                '    <div class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="'+progress+'" aria-valuemin="0" aria-valuemax="'+total+'" style="width:'+percentage+'%">' +
                '    <span class="sr-only">'+percentage+'% Complete</span>' +
                 percentage+'% Complete'+
                '    </div>' +
                '    </div>' +
                '    </div>';

            let courseName = course.coursename;
            if(course.coursename.length > 50){
                courseName = courseName.slice(0, 50) + "...";
            }

            let Tags = (course.tags).split(";");
            let tagButtons = "";
            for(let tag of Tags){
                if(tag != ""){
                    tagButtons += `<button class='searchButton' data-id='${tag}'>` +
                        `${tag}</button>`;
                }
            }

            let userNameButton = `<button class='searchButton' data-id='${course.username}'>` +
                `${course.username}</button>`;

            coursesHTML += `<tr>` +
                `<td style="width: 10px">${course.courseID}</td>` +
                `<td style="width: 350px"><button class='playButton' data-id='${course.courseID}'>${courseName}</button></td>` +
                `<td><small>${userNameButton}</small></td>` +
                `<td>${starText}</td>` +
                `<td>${tagButtons}</td>` +
                `</tr>` +
                `<tr>` +
                `<td colspan="5">${progressBar}</td>` +
                `</tr>`;
        }
        coursesHTML += '</table>';
        document.getElementById("listDiv").innerHTML = coursesHTML;
        checkLogin();
        let playbuttons = document.getElementsByClassName("playButton");
        for (let button of playbuttons){
            button.addEventListener("click", goTo);
        }
    });

}

function goTo(event) {
    event.preventDefault();
    const courseID = event.target.getAttribute("data-id");
    Cookies.set("courseID", courseID);
    window.location.href = '/client/displayQuiz.html';
}

function goToCreate(event){
    event.preventDefault();
    window.location.href = '/client/createCourse.html';
}

function checkLogin() {
    let username = Cookies.get("username");
    console.log(username);
    let logInHTML = "";
    if (username === undefined){
        let playButtons = document.getElementsByClassName("playButton");
        for (let button of playButtons){
            button.disabled = true;
        }

        logInHTML = "Not logged in. <a href='/client/login.html?login'>Log in</a>";

    } else{
        let playButtons = document.getElementsByClassName("playButton");
        for (let button of playButtons){
            button.disabled = false;
        }

        logInHTML = "Logged in as " + username + ".<a href='/client/login.html?logout'>Log out</a>";
    }
    console.log(logInHTML);
    document.getElementById("loggedInDetails").innerHTML = logInHTML;
}

