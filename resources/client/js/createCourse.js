let newTag;

function pageLoad(){
    document.getElementById("CreateButton").addEventListener("click", create)
}

function create(event){
    const userID = Cookies.get("id");
    event.preventDefault();
    let formData = new FormData();
    if(document.getElementById("coursename").value != ""){
        formData.append("userId", userID);
        formData.append("coursename", document.getElementById("coursename").value);
        formData.append("tags", document.getElementById("tagAssembled").innerText);
        fetch("/courses/create", {method: 'post', body: formData}
        ).then(response => response.json()
        ).then(responseData => {
            if (responseData.hasOwnProperty('error')){
                alert(responseData.error);
            } else{
                window.location.href = '/client/index.html';
            }
        });
    } else{
        alert("Must enter a course name");
    }

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
    newTag = "";
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