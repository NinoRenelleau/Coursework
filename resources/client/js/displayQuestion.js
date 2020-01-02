let h=0, w=0;
let questNum;
let buttons = [];
let headers = [];
let lastTimestamp;

function fixSize(){
    w = window.innerWidth;
    h = window.innerHeight;
    const canvas = document.getElementById('questionDisplayed');
    canvas.width = w;
    canvas.height = h;
}

function pageLoad() {
    let url = window.location.search.substring(1);
    console.log(url);
    questNum = url;

    totalPoints = 0;
    quizID = Cookies.get("quizID");

    window.addEventListener("resize", fixSize);
    fixSize();

    const canvas = document.getElementById('question');

    initialise();
    /*console.log(buttons);
    console.log(headers);*/
    window.requestAnimationFrame(gameFrame);
}

function gameFrame(timestamp) {
    if (lastTimestamp === 0) lastTimestamp = timestamp;
    const frameLength = (timestamp - lastTimestamp) / 1000;
    lastTimestamp = timestamp;
    //console.log(totalPoints);
    processes(frameLength);

    window.requestAnimationFrame(gameFrame);
}

function initialise() {
    console.log(questNum);
    const canvas = document.getElementById('questionDisplayed');
    const context = canvas.getContext('2d');
    context.scale(0.5,0.5);
    fetch('/questions/list/' + quizID, {method: 'get'}
    ).then(response => response.json()
    ).then(questions => {
        /*Cookies.set('questionID', questions[questNum].questionID);
        Cookies.set('templateID', questions[questNum].templateID);
        Cookies.set('instruction', questions[questNum].instruction);
        Cookies.set('questionData', questions[questNum].questionData);*/
        let questionID = questions[questNum].questionID;
        console.log(questNum);
        console.log(questions[questNum]);
        //templateID = Cookies.get('templateID');
        fetch('/objects/list/' + questionID, {method: 'get'}
        ).then(response => response.json()
        ).then(objects => {
            for (let object of objects) {
                let coordinates = object.coordinates.split("s");
                let x = coordinates[0];
                let y = coordinates[1];
                if (object.Type == "correctButtons" || object.Type == "wrongButtons") {
                    context.fillStyle = "black";
                    context.font = object.font+"px Arial";
                    let buttonWriting = object.content;
                    let correct = false;
                    if (object.Type == "correctButtons") correct = true;
                    let widthOfWriting = context.measureText(buttonWriting).width;
                    context.strokeStyle = "black";
                    let boxX = Number(x) - 5;
                    buttons.push({
                        X: boxX,
                        Y: Number(y),
                        x: Number(x),
                        y: Number(y)+object.font - 5,
                        width: widthOfWriting + 10,
                        font: object.font,
                        height: object.font+10,
                        on: false,
                        content: buttonWriting,
                        correct: correct
                    });
                } else if (object.Type == "headers") {
                    let headerWriting = object.content;
                    context.fillStyle = "black";
                    context.font = object.font+"px Arial";
                    let widthOfWriting = context.measureText(headerWriting).width;
                    context.strokeStyle = "black";
                    let boxX = Number(x) - 5;
                    let boxY = Number(y) + 5;
                    headers.push({
                        boxX: boxX,
                        boxY: boxY,
                        x: Number(x),
                        y: Number(y),
                        font: object.font,
                        content: headerWriting,
                        width: widthOfWriting
                    });
                }

            }
        });

    });
}

function processes(){
    const canvas = document.getElementById('questionDisplayed');
    const context = canvas.getContext('2d');
    context.clearRect(0, 0, canvas.width, canvas.height);
    for (let button of buttons){
        console.log(button);
        if (button.correct){
            console.log(button.correct);
            context.fillStyle = "black";
            context.font = button.font+"px Arial";
            context.fillText(button.content, button.x, button.y);
            context.globalCompositeOperation = "destination-over";
            context.strokeStyle = "black";
            context.strokeRect(button.X, button.Y, button.width, button.height);
            context.globalCompositeOperation = "destination-over";
            context.fillStyle = "green";
            context.fillRect(button.X, button.Y, button.width, button.height);
        }else{
            context.fillStyle = "black";
            context.font = button.font+"px Arial";
            context.fillText(button.content, button.x, button.y);
            context.globalCompositeOperation = "destination-over";
            context.strokeStyle = "black";
            context.strokeRect(button.X, button.Y, button.width, button.height);
            context.globalCompositeOperation = "destination-over";
        }


    }
    for (let header of headers){
        context.fillStyle = "black";
        context.font = header.font+"px Arial";
        context.fillText(header.content, header.x, header.y);
        context.strokeStyle = "black";
        context.beginPath();
        context.moveTo(header.boxX, header.boxY);
        context.lineTo(header.width + header.x, header.boxY);
        context.stroke();
    }

}