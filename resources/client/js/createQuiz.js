function pageLoad(){
    document.getElementById("CreateButton").addEventListener("click", create);
}

function create(event){
    const courseID = Cookies.get("courseID");
    event.preventDefault();
    let formData = new FormData();
    if(document.getElementById("quizname").value != "") {
        formData.append("courseID", courseID);
        formData.append("name", document.getElementById("quizname").value);
        fetch("/quizzes/create", {method: 'post', body: formData}
        ).then(response => response.json()
        ).then(responseData => {
            if (responseData.hasOwnProperty('error')) {
                alert(responseData.error);
            }else{
                Cookies.set("quizID", responseData.quizID);
                window.location.href = '/client/listQuestions.html';
            }
        });
    }else{
        alert("Must enter a quiz name");
    }
}

function goBack(){
    window.location.href = '/client/displayQuiz.html';
}