function pageLoad() {
    let url = window.location.search.substring(1);
    let param = url.split('=');
    if(url == "editor"){
        let src = '<iframe src="questionCreator.html" height="500" width="936" style="border:2px solid black; overflow: auto;"></iframe>';
        document.getElementById("display").innerHTML = src;
        document.getElementById("MainHeading").innerText = "Question Editor";
    }else if(param[0] == "editorExisting"){
        let nextUrl = "questionCreator.html?Existing="+param[1];
        let src = `<iframe src="${nextUrl}" height="500" width="936" style="border:2px solid black; overflow: auto;"></iframe>`;
        document.getElementById("display").innerHTML = src;
        document.getElementById("MainHeading").innerText = "Question Editor";
    }else{
        let src = '<iframe src="question.html" height="500" width="936" style="border:2px solid black;"></iframe>';
        document.getElementById("display").innerHTML = src;
        let quizID = Cookies.get("quizID");
        fetch('/quizzes/searchByID/' + quizID, {method: 'get'}
        ).then(response => response.json()
        ).then(quiz => {
            console.log(quiz);
            document.getElementById("MainHeading").innerText = quiz.quizname;
        });
    }
}

function goBack() {
    let url = window.location.search.substring(1);
    let param = url.split("=");
    if(url == "editor"){
        window.location.href = '/client/listQuestions.html';
    }else if(param[0] == "editorExisting"){
        window.location.href = '/client/listQuestions.html';
    }else{
        window.location.href = '/client/displayQuiz.html';
    }

}