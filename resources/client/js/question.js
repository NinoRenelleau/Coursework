let mouseClicked = false;
const mousePosition = {x: 0, y: 0, snappedT: 0};
let w = 0, h = 0;
let lastTimestamp = 0;
let quizID;
let questionID;
let templateID;
let instruction;
let questionData;
let questNum = 0;
let points = 0;
let currentPoint;
let Questions = [];
let buttons = [];
let headers = [];
let endQuiz = false;
let totalPoints = 0;
let endQuestion = true;
let stars = [];
let rating = 0;
let selected = false;
let waitTime = 0;

function fixSize(){
    w = window.innerWidth;
    h = window.innerHeight;
    const canvas = document.getElementById('question');
    canvas.width = w;
    canvas.height = h;
}

function pageLoad(){
    totalPoints = 0;
    quizID = Cookies.get("quizID");

    window.addEventListener("resize", fixSize);
    fixSize();

    const canvas = document.getElementById('question');
    canvas.addEventListener('mousemove', event => {
        mousePosition.x = event.clientX;
        mousePosition.y = event.clientY;
        mousePosition.snappedT = h - Math.floor(event.clientY/10)*10
    }, false);

    canvas.addEventListener('click', event => {
        mouseClicked = true;
    }, false);

    window.requestAnimationFrame(gameFrame);
}

function gameFrame(timestamp) {
    if (lastTimestamp === 0) lastTimestamp = timestamp;
    const frameLength = (timestamp - lastTimestamp) / 1000;
    lastTimestamp = timestamp;
    //console.log(totalPoints);
    if(endQuestion){
        initialise();
    }
    if(endQuiz){
        if(stars.length == 0){
            endScreen();
        }
        ratingProcess();

    } else{
        inputs();
        processes();
        //outputs();
    }
    window.requestAnimationFrame(gameFrame);
}

function inputs(){
    const canvas = document.getElementById('question');
    const context = canvas.getContext('2d');

    for (let button of buttons){
        if(isInside(button)){
            button.on = true;
        } else{
            button.on = false;
        }
    }
}

function processes(){
    const canvas = document.getElementById('question');
    const context = canvas.getContext('2d');
    context.clearRect(0, 0, canvas.width, canvas.height);
    context.fillStyle = "blue";
    context.font = "20px Arial";
    context.fillText(points, 50, 68);
    for (let button of buttons){
        if(button.on){
            console.log(button.correct);
            context.fillStyle = "black";
            context.font = "30px Arial";
            context.fillText(button.content, button.x, button.y);
            context.globalCompositeOperation = "destination-over";
            context.strokeStyle = "black";
            context.strokeRect(button.X, button.Y, button.width, button.height);
            context.globalCompositeOperation = "destination-over";
            context.fillStyle = "green";
            context.fillRect(button.X, button.Y, button.width, button.height);
            if(mouseClicked && button.correct){
                points += Number(Cookies.get("currentPoint"));
                clearAll();
                mouseClicked = false;
                endQuestion = true;
            } else if (mouseClicked && !button.correct){
                clearAll();
                mouseClicked = false;
                endQuestion = true;
            }
        } else{
            context.fillStyle = "black";
            context.font = "30px Arial";
            context.fillText(button.content, button.x, button.y);
            context.globalCompositeOperation = "destination-over";
            context.strokeStyle = "black";
            context.strokeRect(button.X, button.Y, button.width, button.height);
            context.globalCompositeOperation = "destination-over";
        }
    }
    for (let header of headers){
        context.fillStyle = "black";
        context.font = "50px Arial";
        context.fillText(header.content, header.x, header.y);
        context.strokeStyle = "black";
        context.beginPath();
        context.moveTo(header.boxX, header.boxY);
        context.lineTo(header.width + header.x, header.boxY);
        context.stroke();
    }
    mouseClicked = false;
}

function initialise(){
    console.log(questNum);
    endQuestion = false;
    const canvas = document.getElementById('question');
    const context = canvas.getContext('2d');
    fetch('/questions/list/'+quizID, {method: 'get'}
    ).then(response => response.json()
    ).then(questions => {
        for(let question of questions){
            Questions.push(question);
        }
        //console.log(questions);
        if (questions[questNum] === undefined){
            endQuiz = true;
            console.log(endQuiz);
        } else{
            Cookies.set('questionID', questions[questNum].questionID);
            Cookies.set('templateID', questions[questNum].templateID);
            Cookies.set('instruction', questions[questNum].instruction);
            Cookies.set('questionData', questions[questNum].questionData);
            Cookies.set('currentPoint', questions[questNum].Points);
            //console.log(questions[questNum]);
            totalPoints += Number(Cookies.get("currentPoint"));
        }
        questNum += 1;

        if (!endQuiz) {
            templateID = Cookies.get('templateID');
            //console.log(templateID);
            fetch('/objects/list/' + templateID, {method: 'get'}
            ).then(response => response.json()
            ).then(objects => {
                console.log(objects);
                for (let object of objects) {
                    let coordinates = object.coordinates.split("s");
                    let x = coordinates[0];
                    let y = coordinates[1];
                    if (object.Type == "button") {
                        let buttonContent = Cookies.get("questionData").split(":::");
                        let buttonWriting;
                        let correct = false;
                        for (let contents of buttonContent) {
                            if (contents[0] == (object.objectID).toString()) {
                                context.fillStyle = "black";
                                context.font = "30px Arial";
                                buttonWriting = contents.substring(1, (contents.length - 1));
                                if (contents.substring(contents.length - 1) == "r") {
                                    correct = true;
                                }
                            }
                        }
                        let widthOfWriting = context.measureText(buttonWriting).width;
                        context.strokeStyle = "black";
                        let boxX = Number(x) - 5;
                        let boxY = Number(y) - 30;
                        buttons.push({
                            X: boxX,
                            Y: boxY,
                            x: Number(x),
                            y: Number(y),
                            width: widthOfWriting + 10,
                            height: 40,
                            on: false,
                            content: buttonWriting,
                            correct: correct
                        });
                    } else if (object.Type == "header") {
                        let headerContent = Cookies.get("questionData").split(":::");
                        let headerWriting;
                        for (let contents of headerContent) {
                            if (contents[0] == (object.objectID).toString()) {
                                context.fillStyle = "black";
                                context.font = "50px Arial";
                                headerWriting = contents.substring(1, (contents.length - 1));
                                //console.log(headerWriting);
                            }
                        }
                        let widthOfWriting = context.measureText(headerWriting).width;
                        context.strokeStyle = "black";
                        let boxX = Number(x) - 5;
                        let boxY = Number(y) + 5;
                        headers.push({
                            boxX: boxX,
                            boxY: boxY,
                            x: Number(x),
                            y: Number(y),
                            content: headerWriting,
                            width: widthOfWriting
                        });
                    }

                }
            });
        }
    });

}

function isInside(rect){
    return mousePosition.x > rect.X && mousePosition.x < rect.X+rect.width && mousePosition.y < rect.Y+rect.height && mousePosition.y > rect.Y
}

function isInsideStar(star){
    let y = (2*h)/3;
    let distance = Math.floor(Math.sqrt(((Number(mousePosition.x) - Number(star.x))*(Number(mousePosition.x) - Number(star.x))) + ((Number(mousePosition.y) - Number(y))*(Number(mousePosition.y) - Number(y)))));
    return distance <= 20;
}

function endScreen(){
    //console.log("this is the end");
    for (let x = 0; x < 5; x++){
        stars.push({num:x, selected:false, x:(w/6)*(x+1)});
    }
}

function ratingProcess(){
    const canvas = document.getElementById('question');
    const context = canvas.getContext('2d');
    context.clearRect(0, 0, canvas.width, canvas.height);
    let percentage = Math.floor((points/totalPoints)*100);
    context.fillStyle = "white";
    context.font = "30px Arial";
    context.fillText((percentage+"%"), (w/2)-295, (h/2)+10);
    context.globalCompositeOperation = "destination-over";
    context.strokeStyle = "black";
    context.strokeRect((w/2)-300, (h/2)-20, 600, 40);
    context.globalCompositeOperation = "destination-over";
    context.fillStyle = "blue";
    context.fillRect((w/2)-300, (h/2)-20, percentage*6, 40);

    for (let star of stars){
        if(isInsideStar(star)){
            star.selected = true;
            selected = true;
        } else{
            star.selected = false;
        }

        if(star.selected){
            rating = star.num + 1;
        }
    }
    console.log(rating);
    if (!(mouseClicked && selected)) {
        for (let star of stars) {
            if (rating - 1 >= star.num) {
                drawStar(Number(star.x), (2 * h) / 3, 5, 30, 15, "yellow");
            } else {
                drawStar(Number(star.x), (2 * h) / 3, 5, 30, 15, "black");
            }
        }
        mouseClicked = false;
        rating = 0;
    } else{
        context.fillStyle = "black";
        context.font = "30px Arial";
        context.fillText("Quiz Completed, Saving...", (w/6), ((2 * h) / 3)-30);
        let formData = new FormData();
        formData.append("QuizID", Cookies.get("quizID"));
        formData.append("Score", points);
        formData.append("Review", rating);
        waitTime += 1;
        if (waitTime >= 150){
            let formData2 = new FormData();
            formData2.append("ID", 0);
            fetch('/history/update', {method: 'post', body: formData}
            ).then(response => response.json()
            ).then(responseData =>{
                if (responseData.hasOwnProperty('error')) {
                    alert(responseData.error);
                }
                fetch('/history/updateRatings', {method: 'post', body: formData2}
                ).then(response => response.json()
                ).then(dataResponse => {
                    if (dataResponse.hasOwnProperty('error')) {
                        alert(dataResponse.error);
                    }
                    fetch('/history/updateCourseRatings', {method: 'post', body: formData2}
                    ).then(response => response.json()
                    ).then(Response => {
                        if (Response.hasOwnProperty('error')) {
                            alert(Response.error);
                        }

                    });
                });
                parent.window.location.href = '/client/index.html';

            });

        }
    }

}

function clearAll(){
    const canvas = document.getElementById('question');
    const context = canvas.getContext('2d');
    /*for(let button in buttons){
        console.log(buttons[button]);
        delete buttons[button];
        buttons.pop()
    }
    console.log(buttons);
    for(let header in headers) {
        delete headers[header];
        headers.pop();
    }*/
    headers = [];
    buttons = [];
    context.clearRect(0, 0, canvas.width, canvas.height);
}

function drawStar(cx, cy, spikes, outerRadius, innerRadius, colour) {
    const canvas = document.getElementById('question');
    const context = canvas.getContext('2d');

    let rot = Math.PI / 2 * 3;
    let x = cx;
    let y = cy;
    let step = Math.PI / spikes;

    context.strokeSyle = "#000";
    context.beginPath();
    context.moveTo(cx, cy - outerRadius)
    for (let i = 0; i < spikes ; i++) {
        x = cx + Math.cos(rot) * outerRadius;
        y = cy + Math.sin(rot) * outerRadius;
        context.lineTo(x, y)
        rot += step

        x = cx + Math.cos(rot) * innerRadius;
        y = cy + Math.sin(rot) * innerRadius;
        context.lineTo(x, y)
        rot += step
    }
    context.lineTo(cx, cy - outerRadius)
    context.closePath();
    context.lineWidth=5;
    context.strokeStyle='grey';
    context.stroke();
    context.fillStyle=colour;
    context.fill();
}