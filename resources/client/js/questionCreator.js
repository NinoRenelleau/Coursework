 let mouseClicked = false;
const mousePosition = {x: 0, y: 0, snappedT: 0};
const pressedKeys = {};
let input;
let timer = 0;
let keyDown = false;
let write = false;
let entered = false;
let validate = false;
let w = 0, h = 0;
let lastTimestamp = 0;
let quizID;
let existing = false;
let questionID;
let minimise = false;
let maximise = false;
let deleteObj = false;
let points = 0;
let currentPoint;
let brightness = 1;
let select = false;
let follow = false;
let buttons = [];
let headers = [];
let objectSelect = 0;
let objects = [{type:"select", object: false, help:"Select tool, click on objects to select and move them, pageUp and pageDown allows to decrease and increase the font size"},
    {name:"correctButtons", type:"buttons", code:
        "        context.strokeStyle = \"black\";\n" +
        "        context.strokeRect(mousePosition.snappedX-10, mousePosition.snappedY-20, 20, 45);\n" +
        "        context.globalCompositeOperation = \"destination-over\";\n" +
        "        context.fillStyle = \"green\";\n" +
        "        context.fillRect(mousePosition.snappedX-10, mousePosition.snappedY-20, 20, 45);", object:true, x:"mousePosition.snappedX-10", y:"mousePosition.snappedY-20", height:40, help:"Button (correct), click to place a button for the correct answer"},
    {name:"wrongButtons", type:"buttons", code:
            "        context.strokeStyle = \"black\";\n" +
            "        context.strokeRect(mousePosition.snappedX-10, mousePosition.snappedY-20, 20, 45);\n" +
            "        context.globalCompositeOperation = \"destination-over\";\n" +
            "        context.fillStyle = \"red\";\n" +
            "        context.fillRect(mousePosition.snappedX-10, mousePosition.snappedY-20, 20, 45);", object:true, x:"mousePosition.snappedX-10", y:"mousePosition.snappedY-20", height:40, help:"Button (incorrect), click to place a button for the incorrect answer"},
    {name: "headers", type:"headers", code:
            "        context.strokeStyle = \"black\";\n" +
            "        context.beginPath();\n" +
            "        context.moveTo(mousePosition.snappedX-10, mousePosition.snappedY+25);\n" +
            "        context.lineTo(mousePosition.snappedX+10, mousePosition.snappedY+25);\n" +
            "        context.stroke();", object:true, x:"mousePosition.snappedX-10", y:"mousePosition.snappedY+25", height:45, help:"Header, click to place a header"}];
let totalPoints = 0;
let endQuestion = false;
let selected = false;
let waitTime = 0;

function fixSize(){
    w = window.innerWidth;
    h = window.innerHeight;
    const canvas2 = document.getElementById('inputBox');
    const canvas = document.getElementById('question');
    const canvas3 = document.getElementById('help');
    canvas.width = w;
    canvas.height = h;
    canvas2.height = h;
    canvas2.width = w;
    canvas3.height = h;
    canvas3.width = w;
}

function pageLoad(){
    totalPoints = 0;
    quizID = Cookies.get("quizID");
    input = new CanvasInput({
        canvas: document.getElementById('inputBox'),
    });

    window.addEventListener("resize", fixSize);
    fixSize();

    window.addEventListener("keydown", event => pressedKeys[event.key] = true);
    window.addEventListener("keyup", event => pressedKeys[event.key] = false);

    const canvas = document.getElementById('question');
    const context = canvas.getContext('2d');
    canvas.addEventListener('mousemove', event => {
        mousePosition.x = event.clientX;
        mousePosition.y = event.clientY;
        mousePosition.snappedY = Math.floor(event.clientY/10)*10;
        mousePosition.snappedX = Math.floor(event.clientX/10)*10
    }, false);

    canvas.addEventListener('click', event => {
        mouseClicked = true;
    }, false);

    let url = window.location.search.substring(1);
    let param = url.split("=");

    if(param[0] == "Existing"){
        existing = true;
        questionID = param[1];
        fetch('/objects/list/'+param[1], {method:'get'}
        ).then(response => response.json()
        ).then(Objects=>{
            if (Objects.hasOwnProperty('error')){
                alert(Objects.error);
            } else{
                console.log(Objects);
                for(let object of Objects){
                    let coordinates = object.coordinates.split("s");
                    let x = Number(coordinates[0]);
                    let y = Number(coordinates[1]);
                    let width = context.measureText(object.content).width;
                    if(object.Type == "correctButtons" || object.Type == "wrongButtons"){
                        buttons.push({x:x, y:y, height:object.font + 5, content:object.content, width:width, type:object.Type, picked:false, font:object.font, exists: true, Id:object.objectID});
                    } else if (object.Type == "headers"){
                        headers.push({x:x, y:y, height:object.font + 5, content:object.content, width:width, type:object.Type, picked:false, font:object.font, exists: true, Id:object.objectID});
                    }
                }
                window.requestAnimationFrame(gameFrame);
            }
        });
    } else{
        window.requestAnimationFrame(gameFrame);
    }
}

function gameFrame(timestamp) {
    if (lastTimestamp === 0) lastTimestamp = timestamp;
    const frameLength = (timestamp - lastTimestamp) / 1000;
    lastTimestamp = timestamp;

    inputs();
    outputs(frameLength);
    if(!(endQuestion && entered)){
        window.requestAnimationFrame(gameFrame);
    } else{
        if(existing){
            fetch('/objects/list/'+questionID, {method:'get'}
            ).then(response => response.json()
            ).then(Objects=> {
                if (Objects.hasOwnProperty('error')) {
                    alert(Objects.error);
                }else{
                    for(let object of Objects){
                        let formData = new FormData();
                        formData.append("objectId", object.objectID);
                        fetch('/objects/delete', {method:'post', body:formData}
                        ).then(response => response.json()
                        ).then(responseDataDelete => {
                            if (responseDataDelete.hasOwnProperty('error')) {
                                alert(responseDataDelete.error);
                            }
                        });
                    }
                    let formData = new FormData();
                    formData.append("questionID", questionID);
                    formData.append("points", points);
                    fetch('/questions/updatePoints', {method:'post', body:formData}
                    ).then(response => response.json()
                    ).then(responseData =>{
                        if (responseData.hasOwnProperty('error')){
                            alert(responseData.error);
                        } else{
                            for(let button of buttons){
                                console.log(button);
                                let formData2 = new FormData();
                                formData2.append("type", button.type);
                                formData2.append("coordinates", button.x + "s" + button.y);
                                formData2.append("font", button.font);
                                formData2.append("content", button.content);
                                formData2.append("QuestionId", questionID);
                                fetch('/objects/create', {method:'post', body:formData2}
                                ).then(response => response.json()
                                ).then(responseData2 =>{
                                    if (responseData2.hasOwnProperty('error')){
                                        alert(responseData2.error);
                                    }
                                });
                            }
                            for(let header of headers){
                                console.log(headers);
                                let formData2 = new FormData();
                                formData2.append("type", header.type);
                                formData2.append("coordinates", header.x + "s" + header.y);
                                formData2.append("font", header.font);
                                formData2.append("content", header.content);
                                formData2.append("QuestionId", questionID);
                                fetch('/objects/create', {method:'post', body:formData2}
                                ).then(response => response.json()
                                ).then(responseData2 =>{
                                    if (responseData2.hasOwnProperty('error')){
                                        alert(responseData2.error);
                                    }
                                });
                            }
                            parent.window.location.href = '/client/listQuestions.html';
                        }
                    });
                }
            });

        }else{
            let formData = new FormData();
            formData.append("QuizID", quizID);
            formData.append("Points", points);
            fetch('/questions/create', {method:'post', body:formData}
            ).then(response => response.json()
            ).then(responseData =>{
                if (responseData.hasOwnProperty('error')){
                    alert(responseData.error);
                } else{
                    for(let button of buttons){
                        let formData2 = new FormData();
                        formData2.append("QuestionId", responseData.questionID);
                        formData2.append("type", button.type);
                        formData2.append("coordinates", button.x + "s" + button.y);
                        formData2.append("font", button.font);
                        formData2.append("content", button.content);
                        fetch('/objects/create', {method:'post', body:formData2}
                        ).then(response => response.json()
                        ).then(responseData2 =>{
                            if (responseData2.hasOwnProperty('error')){
                                alert(responseData2.error);
                            }
                        });
                    }
                    for(let header of headers){
                        let formData2 = new FormData();
                        formData2.append("QuestionId", responseData.questionID);
                        formData2.append("type", header.type);
                        formData2.append("coordinates", header.x + "s" + header.y);
                        formData2.append("font", header.font);
                        formData2.append("content", header.content);
                        fetch('/objects/create', {method:'post', body:formData2}
                        ).then(response => response.json()
                        ).then(responseData2 =>{
                            if (responseData2.hasOwnProperty('error')){
                                alert(responseData2.error);
                            }
                        });
                    }
                    parent.window.location.href = '/client/listQuestions.html';
                }
            });
        }

    }

}

function outputs(frameLength){
    const canvas = document.getElementById('question');
    const canvas2 = document.getElementById('inputBox');
    const context2 = canvas2.getContext('2d');
    const context = canvas.getContext('2d');
    const canvas3 = document.getElementById('help');
    const context3 = canvas3.getContext('2d');
    context.clearRect(0, 0, canvas.width, canvas.height);
    context3.clearRect(0,0, w, h);

    if (endQuestion){
        context3.font = '15px Arial';
        context3.fillStyle = "blue";
        context3.fillText("Press 'Enter' to save your question and exit, or press 'Escape' to go back.", 20, h/2 - 20);
        context3.fillText("Press pageUp or PageDown to increase or decrease the amount of points the question is worth.", 20, h/2 );
        context3.font = '20px Arial';
        context3.fillStyle = "black";
        context3.fillText("Points:" + points, 20, h/2 + 20);


    } else{
        if(!write){
            if((objects[objectSelect].type != "select")) {
                context.globalAlpha = 0.5;
                eval(objects[objectSelect].code);
                context.globalAlpha = 1;
            }
            if(timer <= 250){
                timer += frameLength*30;
                context3.font = '15px Arial';
                context3.fillStyle = "blue";
                context3.fillText(objects[objectSelect].help, 20, 25);
                context3.fillText("Press pageUp and pageDown to change tool. Press 'Enter' when hovering over an object to enter text.", 20, h-25);
                context3.fillText("Press 'Delete' when hovering over an object to delete it. Press 'Escape' to save the question.", 20, h-5);
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
                    button.x = mousePosition.snappedX - button.width/2;
                    button.y = mousePosition.snappedY - button.height/2;
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
            button.height = fontSize+5;
            context.fillText(button.content, button.x, button.y+fontSize-5);
            context.globalCompositeOperation = "destination-over";
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
                    header.x = mousePosition.snappedX - header.width/2;
                    header.y = mousePosition.snappedY + header.height/2;
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

}
function inputs(){
    const canvas = document.getElementById('question');
    const context = canvas.getContext('2d');

    if(mouseClicked && !write){
        //console.log("pushed");
        let object = objects[objectSelect]
        if(object.object){
            eval(object.type).push({x:eval(object.x), y:eval(object.y), height:object.height, content:"", type:object.name, picked:false, font:40});
        }else{
            select = true;
            console.log("true");
        }
        mouseClicked = false;
    }


    if(pressedKeys["PageUp"]) {
        //console.log("next");
        if (!write && !follow && !endQuestion){
            if (!keyDown) {
                objectSelect++;
                if (objectSelect > 3) objectSelect = 0;
                timer = 0;
                keyDown = true;
            }
        } else if (follow){
            if(!keyDown){
                maximise = true;
                keyDown = true;
            }
        }else if (endQuestion){
            if(!keyDown){
                points ++;
                keyDown = true;
            }
        }
    } else if (pressedKeys["PageDown"]){
        if(!write && !follow && !endQuestion){
            if(!keyDown){
                objectSelect --;
                if(objectSelect < 0)objectSelect = 3;
                timer = 0;
                keyDown = true;
            }
        } else if (follow){
            if(!keyDown){
                minimise = true;
                keyDown = true;
            }
        }else if (endQuestion){
            if(!keyDown){
                if(points!= 0){
                    points--;
                }
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
    else if(pressedKeys["Delete"] && !write && !follow && !endQuestion){
        if(!keyDown){
            deleteObj = true;
            keyDown = true
        }
    }else if (pressedKeys["Escape"]){
        if(!keyDown){
            if (endQuestion){
                endQuestion = false;
            } else{
                endQuestion = true;
            }
            keyDown = true;
        }
    }else{
        entered = false;
        keyDown = false;
        deleteObj = false;
        escaped = false;
    }

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

function clearAll(){
    const canvas = document.getElementById('question');
    const context = canvas.getContext('2d');
    headers = [];
    buttons = [];
    context.clearRect(0, 0, canvas.width, canvas.height);
}
