function pageLoad() {
    let url = window.location.search.substring(1);
    if(url == "editor"){
        let src = '<iframe src="questionCreator.html" height="500" width="936" style="border:2px solid black; overflow: auto;"></iframe>';
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