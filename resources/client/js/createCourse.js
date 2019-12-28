let newTag;

function pageLoad(){

}


function addTag(){
    let Tags = document.getElementById("listOfTags");
    let text = document.getElementById("tagAssembled").innerText;
    let textParts = text.split(";");
    let exists = false;
    newTag = undefined;
    for (let tag of Tags){
        if(tag.selected){
            for (let part of textParts){
                if (tag.value == part){
                    exists = true;
                }
            }
            if(!exists){
                newTag = tag.value;
            }
        }
    }
    if(!(newTag === undefined)){
        document.getElementById("tagAssembled").innerText += newTag + ";";
    }
}

function removeTag(){
    if (!(newTag === undefined)){
        let text = document.getElementById("tagAssembled").innerText;
        let textParts = text.split(";");
        let newText = "";
        console.log(textParts);
        if (textParts.length == 2){
            newText = "";
        }else{
            for(let x = 0; x < textParts.length - 2; x++){
                if(x != textParts.length - 2){
                    newText += textParts[x] + ";";
                }
            }
        }
        document.getElementById("tagAssembled").innerText = newText;
    }
}