let quizID;

function pageLoad(){
    quizID = Cookies.get("quizID");
    fetch('/quizzes/searchByID/' + quizID, {method: 'get'}
    ).then(response => response.json()
    ).then(quiz => {
        document.getElementById("MainHeading").innerText = quiz.quizname;
    });
    fetch("/questions/list/"+quizID, {method: 'get'}
    ).then(response => response.json()
    ).then(questions =>{
        let number = 0;
        let url;
        let questionHTML = '<table>' +
            '<tr>' +
            '<th>No#</th>' +
            '<th>Question</th>' +
            '<th>Edit</th>' +
            '<th>Points</th>' +
            '</tr>';

        for(let question of questions){
            url = "displayQuestion.html?" + number;
            questionHTML += `<tr>` +
                `<td style="width: *1">${number+=1}</td>` +
                `<td style="width: *5"><iframe src="${url}" height="250" width="468" style="border:2px solid black; overflow: auto;"></iframe></td>` +
                `<td style="width: *5"><button class='editQuestionButton' data-id='${question.questionID}'>Edit</button><button class='deleteQuestionButton' data-id='${question.questionID}'>Delete</button></td>` +
                `<td style="align-content: right; width: *3;">${question.Points}</td>` +
                `</tr>`;
        }
        questionHTML += '</table>';
        document.getElementById("listDiv").innerHTML = questionHTML;
        let editbuttons = document.getElementsByClassName("editQuestionButton");
        for (let button of editbuttons){
            button.addEventListener("click", goToEdit);
        }
        let deletebuttons = document.getElementsByClassName("deleteQuestionButton");
        for (let button of deletebuttons){
            button.addEventListener("click", deleteQuestion);
        }
    });

}

function goToQuestion(){
    window.location.href = '/client/playQuiz.html?editor';
}

function goBack(){
    window.location.href = '/client/displayQuiz.html';
}

function goToEdit(event){
    event.preventDefault();
    const questionID = event.target.getAttribute("data-id");
    window.location.href = '/client/playQuiz.html?editorExisting='+questionID;
}

function deleteQuestion(event){
    event.preventDefault();
    const questionID = event.target.getAttribute("data-id");
    const sessionToken = Cookies.get("token");
    let formData = new FormData();
    formData.append("QuestionId", questionID);
    formData.append("token", sessionToken);
    fetch('/questions/delete', {method: 'post', body: formData}
    ).then(response => response.json()
    ).then(responseData =>{
        if (responseData.hasOwnProperty('error')) {
            alert(responseData.error);
        }
        window.location.href = '/client/listQuestions.html';
    });
}