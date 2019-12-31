let mouseClicked = false;
const mousePosition = {x: 0, y: 0, snappedT: 0};
const pressedKeys = {};
let input;
let keyDown = false;
let write = false;
let entered = false;
let validate = false;
let w = 0, h = 0;
let lastTimestamp = 0;
let quizID;
let questionID;
let templateID;
let instruction;
let questionData;
let minimise = false;
let maximise = false;
let deleteObj = false;
let points = 0;
let currentPoint;
let brightness = 1;
let select = false;
let follow = false;
let buttons = [];
let correctButtons = [];
let wrongButtons = [];
let headers = [];
let objectSelect = 0;
let objects = [{type:"select", object: false},
    {name:"correctButtons", type:"buttons", code:
        "        context.strokeStyle = \"black\";\n" +
        "        context.strokeRect(mousePosition.snappedX-10, mousePosition.snappedY-20, 20, 45);\n" +
        "        context.globalCompositeOperation = \"destination-over\";\n" +
        "        context.fillStyle = \"green\";\n" +
        "        context.fillRect(mousePosition.snappedX-10, mousePosition.snappedY-20, 20, 45);", object:true, x:"mousePosition.snappedX-10", y:"mousePosition.snappedY-20", height:40},
    {name:"wrongButtons", type:"buttons", code:
            "        context.strokeStyle = \"black\";\n" +
            "        context.strokeRect(mousePosition.snappedX-10, mousePosition.snappedY-20, 20, 45);\n" +
            "        context.globalCompositeOperation = \"destination-over\";\n" +
            "        context.fillStyle = \"red\";\n" +
            "        context.fillRect(mousePosition.snappedX-10, mousePosition.snappedY-20, 20, 45);", object:true, x:"mousePosition.snappedX-10", y:"mousePosition.snappedY-20", height:40},
    {name: "headers", type:"headers", code:
            "        context.strokeStyle = \"black\";\n" +
            "        context.beginPath();\n" +
            "        context.moveTo(mousePosition.snappedX-10, mousePosition.snappedY+25);\n" +
            "        context.lineTo(mousePosition.snappedX+10, mousePosition.snappedY+25);\n" +
            "        context.stroke();", object:true, x:"mousePosition.snappedX-10", y:"mousePosition.snappedY+25", height:45}];
let totalPoints = 0;
let endQuestion = true;
let stars = [];
let rating = 0;
let selected = false;
let waitTime = 0;

function fixSize(){
    w = window.innerWidth;
    h = window.innerHeight;
    const canvas2 = document.getElementById('inputBox');
    const canvas = document.getElementById('question');
    canvas.width = w;
    canvas.height = h;
    canvas2.height = h;
    canvas2.width = w;
}

function pageLoad(){
    totalPoints = 0;
    quizID = Cookies.get("quizID");

    input = new CanvasInput({
        canvas: document.getElementById('inputBox'),
        /*fontSize: 18,
        fontFamily: 'Arial',
        fontColor: '#212121',
        fontWeight: 'bold',
        width: 300,
        padding: 8,
        borderWidth: 1,
        borderColor: '#000',
        borderRadius: 3,
        //placeHolder: 'Enter value here...',
        value:''*/
    });

    window.addEventListener("resize", fixSize);
    fixSize();

    window.addEventListener("keydown", event => pressedKeys[event.key] = true);
    window.addEventListener("keyup", event => pressedKeys[event.key] = false);

    const canvas = document.getElementById('question');
    canvas.addEventListener('mousemove', event => {
        mousePosition.x = event.clientX;
        mousePosition.y = event.clientY;
        mousePosition.snappedY = Math.floor(event.clientY/10)*10;
        mousePosition.snappedX = Math.floor(event.clientX/10)*10
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
    //input.render();

    inputs();
    //processes(frameLength);
    outputs();

    window.requestAnimationFrame(gameFrame);
}

function outputs(){
    const canvas = document.getElementById('question');
    const canvas2 = document.getElementById('inputBox');
    const context2 = canvas2.getContext('2d');
    const context = canvas.getContext('2d');
    context.clearRect(0, 0, canvas.width, canvas.height);

    if(!write){
        if((objects[objectSelect].type == "write")){

        }else if((objects[objectSelect].type == "select")) {

        }
        else{
            context.globalAlpha = 0.5;
            eval(objects[objectSelect].code);
            context.globalAlpha = 1;
        }
    }

    let x = 0;
    if(write){
        brightness = 0.5;
        context2.clearRect(0, 0, canvas.width, input.y());
        context2.clearRect(0, input.y()+input.height()+20, canvas.width, canvas.height);
    } else{
        brightness = 1;
        context2.clearRect(0, 0, canvas.width, canvas.height);
    }
    for(let button of buttons){
        let fontSize = button.font;
        context.globalAlpha = brightness;
        let colour;
        if(button.type == "correctButtons")colour = "green";
        else if (button.type == "wrongButtons") colour = "red";
        if(button.picked){
            if (follow){
                button.x = mousePosition.x - button.width/2;
                button.y = mousePosition.y - button.height/2;
                if (select){
                    follow = false;
                    select = false;
                }
                if (maximise){
                    button.font += 5;
                    maximise = false;
                }
                if (minimise){
                    button.font -= 5;
                    minimise = false;
                }
            }else if (write){
                context.globalAlpha = 1;
                button.content=input.value();
                if(validate){
                    input.blur();
                    button.picked = false;
                    write = false;
                    canvas.style.zIndex = "2";
                    canvas2.style.zIndex = "1";
                    validate = false;
                }
            } else{
                button.picked = false;
            }

        }
        context.font = fontSize+'px Arial';
        context.fillStyle = "black";
        let widthOfWriting = context.measureText(button.content).width;
        if(button.content == "")widthOfWriting=20;
        button.width = widthOfWriting;
        context.fillText(button.content, button.x, button.y+fontSize-5);
        context.strokeStyle = "black";
        context.strokeRect(button.x, button.y, widthOfWriting, fontSize+5);
        context.globalCompositeOperation = "destination-over";
        context.fillStyle = colour;
        context.fillRect(button.x, button.y, widthOfWriting, fontSize+5);
        if (isInside(button)){
            context.globalAlpha = 0.5;
            context.strokeStyle = "black";
            context.strokeRect(button.x-3, button.y-3, widthOfWriting+6, button.font+11);
            context.globalAlpha = 1;
            if(deleteObj){
                buttons.splice(x, 1);
                deleteObj = false;
            }else if(entered){
                entered = false;
                write = true;
                button.picked = true;
                context2.clearRect(0, 0, canvas.width, canvas.height);
                input.x(button.x);
                input.y(button.y + 50);
                input.value(button.content);
                input.focus();
                context2.clearRect(0, 0, canvas.width, input.y());
                context2.clearRect(0, input.y()+input.height()+20, canvas.width, canvas.height);
                canvas.style.zIndex = "1";
                canvas2.style.zIndex = "2";
            } else if (select && !follow){
                follow = true;
                button.picked = true;
                select = false;
            }
        }
        x++;
    }
    x = 0;
    for(let header of headers){
        let fontSize = header.font;
        context.globalAlpha = brightness;
        if(header.picked){
            if (follow){
                header.x = mousePosition.x - header.width/2;
                header.y = mousePosition.y + header.height/2;
                if (select){
                    console.log("false");
                    follow = false;
                    select = false;
                }
                if (maximise){
                    header.font += 5;
                    maximise = false;
                }
                if (minimise){
                    header.font -= 5;
                    minimise = false;
                }
            } else if (write){
                context.globalAlpha = 1;
                header.content=input.value();
                if(validate){
                    input.blur();
                    header.picked = false;
                    write = false;
                    canvas.style.zIndex = "2";
                    canvas2.style.zIndex = "1";
                    validate = false;
                }
            }else{
                header.picked = false;
            }
        }

        context.font = fontSize+'px Arial';
        context.fillStyle = "black";
        context.fillText(header.content, header.x, header.y-5);
        let widthOfWriting = context.measureText(header.content).width;
        if(header.content == "")widthOfWriting=20;
        header.width = widthOfWriting;
        context.strokeStyle = "black";
        context.beginPath();
        context.moveTo(header.x, header.y);
        context.lineTo(header.x+widthOfWriting, header.y);
        context.stroke();
        if (isInside(header)){
            context.globalAlpha = 0.5;
            context.strokeStyle = "black";
            context.strokeRect(header.x, header.y-header.font, widthOfWriting, header.font);
            context.globalAlpha = 1;
            if(deleteObj){
                headers.splice(x, 1);
                deleteObj = false;
            }else if(entered){
                entered = false;
                write = true;
                header.picked = true;
                context2.clearRect(0, 0, canvas.width, canvas.height);
                input.x(header.x);
                input.y(header.y);
                input.value(header.content);
                input.focus();
                context2.clearRect(0, 0, canvas.width, input.y());
                context2.clearRect(0, input.y()+input.height()+20, canvas.width, canvas.height);
                canvas.style.zIndex = "1";
                canvas2.style.zIndex = "2";
            } else if (select && !follow){
                follow = true;
                header.picked = true;
                select = false;
            }
        }
        x ++;
    }
    select = false;
}
function inputs(){
    const canvas = document.getElementById('question');
    const context = canvas.getContext('2d');

    if(mouseClicked && !write){
        //console.log("pushed");
        let object = objects[objectSelect]
        if(object.object){
            eval(object.type).push({x:eval(object.x), y:eval(object.y), width:20, height:object.height, content:"", type:object.name, picked:false, font:40});
        }else{
            select = true;
            console.log("true");
        }
        mouseClicked = false;
    }


    if(pressedKeys["PageUp"]) {
        //console.log("next");
        if (!write && !follow){
            if (!keyDown) {
                objectSelect++;
                if (objectSelect > 3) objectSelect = 0;
                keyDown = true;
            }
        } else if (follow){
            if(!keyDown){
                maximise = true;
                keyDown = true;
            }
        }
    } else if (pressedKeys["PageDown"]){
        if(!write && !follow){
            if(!keyDown){
                objectSelect --;
                if(objectSelect < 0)objectSelect = 3;
                keyDown = true;
            }
        } else if (follow){
            if(!keyDown){
                minimise = true;
                keyDown = true;
            }
        }
    }else if(pressedKeys["Enter"] && !follow){
        if(!keyDown){
            if(!write){
                entered = true;
            }else{
                validate = true;
            }
            keyDown = true
        }
    }
    else if(pressedKeys["Delete"] && !write && !follow){
        if(!keyDown){
            deleteObj = true;
            keyDown = true
        }
    }else{
        entered = false;
        keyDown = false;
        deleteObj = false;
    }


    /*for (let button of buttons){
        if(isInside(button)){
            button.on = true;
        } else{
            button.on = false;
        }
    }*/
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

function isInside(rect){
    const canvas = document.getElementById('question');
    const context = canvas.getContext('2d');
    let widthOfWriting = context.measureText(rect.content).width;
    if(rect.content == "")widthOfWriting=20;
    if(rect.type != "headers"){
        return mousePosition.x > rect.x && mousePosition.x < rect.x+widthOfWriting && mousePosition.y < rect.y+rect.font && mousePosition.y > rect.y
    }else{
        return mousePosition.x > rect.x && mousePosition.x < rect.x+widthOfWriting && mousePosition.y < rect.y && mousePosition.y > rect.y-(rect.font + 5)
    }
}

function isInsideStar(star){
    let y = (2*h)/3;
    let distance = Math.floor(Math.sqrt(((Number(mousePosition.x) - Number(star.x))*(Number(mousePosition.x) - Number(star.x))) + ((Number(mousePosition.y) - Number(y))*(Number(mousePosition.y) - Number(y)))));
    return distance <= 20;
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

