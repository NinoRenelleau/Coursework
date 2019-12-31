let creator = false;

function pageLoad() {
    const courseID = Cookies.get("courseID");
    let heading = "";
    fetch('/courses/searchByID/'+courseID, {method: 'get'}
    ).then(response => response.json()
    ).then(course =>{
        heading = course.coursename;
        document.getElementById("MainHeading").innerText = heading;
        if (course.id == Cookies.get("id")){
            let buttonHTML = '<button id="addNew">Add a quiz</button>';
            document.getElementById("creatorButton").innerHTML = buttonHTML;
            creator = true;
            document.getElementById("addNew").addEventListener("click", createAquiz);
        }



    const userID = Cookies.get("id");

    const pathParam = courseID +"s"+userID;
    let editColumn = '';
    if(creator){
        editColumn = '<th>Edit</th>';
        console.log(editColumn);
    }
    let quizzesHTML = '<table>' +
        '<tr>' +
        '<th>Id</th>' +
        '<th>Quiz</th>' +
        '<th class="rating">Rating</th>' +
        editColumn +
        '<th>Progress</th>' +
        '</tr>';
    console.log(quizzesHTML);
    fetch('/quizzes/list/'+pathParam, {method: 'get'}
    ).then(response => response.json()
    ).then(quizzes =>{
        console.log(quizzes);
        for(let quiz of quizzes){
            console.log(quiz);
            let starNum = Math.round(quiz.rating);
            let starText = '';
            let quizID = quiz.quizID;
            for (i = 0; i < 5; i++){
                if (starNum == 0){
                    starText += '<span class="fa fa-star"></span>'
                } else{
                    starText += '<span class="fa fa-star checked"></span>';
                    starNum -= 1;
                }
            }

            let progress = quiz.score;
            let total = quiz.Total;
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

            console.log(progressBar);

            let quizName = quiz.quizname;
            if(quizName.length > 50){
                quizName = quizName.slice(0, 50) + "...";
            }

            let editButton = ``;

            if (creator){
                editButton = `<td style="width: *5"><button class="editQuizButton" data-id='${quiz.quizID}'>Edit</button></td>`
            }

            quizzesHTML += `<tr>` +
                `<td style="width: *1">${quiz.quizID}</td>` +
                `<td style="width: *5"><button class='playButton' data-id='${quiz.quizID}'>${quizName}</button></td>` +
                `<td style="width: *5">${starText}</td>` +
                editButton +
                `<td style="align-content: right; width: *3;">${progressBar}</td>` +
                `</tr>`;

        }
        quizzesHTML += '</table>';
        document.getElementById("listDiv").innerHTML = quizzesHTML;
        let playbuttons = document.getElementsByClassName("playButton");
        for (let button of playbuttons){
            button.addEventListener("click", goTo);
        }

        let editbuttons = document.getElementsByClassName("editQuizButton");
        for (let button of editbuttons){
            button.addEventListener("click", goToEdit);
        }
    });
    });
}

function goTo(event){
    event.preventDefault();
    const quizID = event.target.getAttribute("data-id");
    Cookies.set("quizID", quizID);
    window.location.href = '/client/playQuiz.html';
}

function goToEdit(event){
    event.preventDefault();
    const quizID = event.target.getAttribute("data-id");
    Cookies.set("quizID", quizID);
    window.location.href = '/client/listQuestions.html';
}

function createAquiz(event) {
    event.preventDefault();
    window.location.href = '/client/createQuiz.html';
}