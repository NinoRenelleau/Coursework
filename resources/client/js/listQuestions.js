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
                `<td style="width: *5"><button class='editQuestionButton' data-id='${question.questionID}'>Edit</button></td>` +
                `<td style="align-content: right; width: *3;">${question.Points}</td>` +
                `</tr>`;
        }
        questionHTML += '</table>';
        document.getElementById("listDiv").innerHTML = questionHTML;

    });

}

function goToQuestion(){
    window.location.href = '/client/playQuiz.html?editor';
}