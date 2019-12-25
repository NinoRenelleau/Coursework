function pageLoad() {
    let quizID = Cookies.get("quizID");
    fetch('/quizzes/searchByID/' + quizID, {method: 'get'}
    ).then(response => response.json()
    ).then(quiz => {
        console.log(quiz);
        document.getElementById("MainHeading").innerText = quiz.quizname;
    });
}