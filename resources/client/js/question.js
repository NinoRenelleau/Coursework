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

function fixSize(){
    w = window.innerWidth;
    h = window.innerHeight;
    const canvas = document.getElementById('question');
    canvas.width = w;
    canvas.height = h;
}

function pageLoad(){
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

    initialise();
    //inputs();
    //processes(frameLength);
    //outputs();

    window.requestAnimationFrame(gameFrame);

}

function initialise(){
    const canvas = document.getElementById('question');
    const context = canvas.getContext('2d');
    fetch('/questions/list/'+quizID, {method: 'get'}
    ).then(response => response.json()
    ).then(questions => {
        Cookies.set('questionID', questions[questNum].questionID);
        Cookies.set('templateID', questions[questNum].templateID);
        Cookies.set('instruction', questions[questNum].instruction);
        Cookies.set('questionData', questions[questNum].questionData);
        Cookies.set('currentPoint', questions[questNum].Points);

    });
    /*console.log(questionID);
    console.log(templateID);
    console.log(instruction);
    console.log(questionData);
    console.log(currentPoint);*/
    templateID = Cookies.get('templateID');
    console.log(templateID);
    fetch('/objects/list/'+templateID, {method: 'get'}
    ).then(response => response.json()
    ).then(objects => {
        console.log(objects);
        for (let object of objects){
            let coordinates = object.coordinates.split("s");
            let x = coordinates[0];
            let y = coordinates[1];
            if(object.Type == "button"){
                let buttonContent = Cookies.get("questionData").split(":::");
                console.log(buttonContent);
                context.strokeStyle = "black";
                context.strokeRect(x, y, 100, 50);
                for(let contents of buttonContent){
                    if(contents[0] == (object.objectID).toString()){
                        context.fillStyle = "black";
                        context.font = "30px Arial";
                        let buttonWriting = contents.substring(1, (contents.length-1));
                        console.log(buttonWriting);
                        context.fillText(buttonWriting, (x+5), (y-35));
                    }
                }

            } else if (object.Type == "header"){
                context.fillStyle = "red";
                context.fillRect(x, y, 100, 20);
            }

        }
    });
}

function isInside(rect){
    return mousePosition.x > rect.x && mousePosition.x < rect.x+rect.width && mousePosition.y < rect.y+rect.height && mousePosition.y > rect.y
}