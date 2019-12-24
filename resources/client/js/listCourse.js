function pageLoad() {

    let coursesHTML = '<table' +
        '<tr>' +
        '<th>Id</th>' +
        '<th>Creator</th>' +
        '<th>Name</th>' +
        '<th class="stats">Progress</th>' +
        '<th class="rating">Rating</th>' +
        '<th>Tags</th>' +
        '</tr>tr>';

    let id;
    id = Cookies.get("id");

    fetch('/courses/list', {method: 'get'}).then(response => response.json()).then(courses => {

        for (let course of courses){
            /*let starNum = Math.round(course.rating);
            let starText = '';
            let courseID = course.courseID;
            for (i = 0; i < 5; i++){
                if (starNum == 0){
                    starText += '<span class="fa fa-star"></span>'
                } else{
                    starText += '<span class="fa fa-star checked"></span>';
                    starNum -= 1;
                }
            }

            let progress = 0;
            let total = 0;

            fetch('/courses/getTotal/'+ courseID, {method: 'get'}).then(response => response.json()).then(responseData => {
               total = responseData.TotalCourseScore;
            });

            let inputData = courseID + "s" + id;
            fetch('/history/courseScore/'+ inputData, {method: 'get'}).then(response => response.json()).then(responseData => {
                progress = responseData.Score;
            });

            let percentage = (progress/total)*100;

            let progressBar = '<div class="container">\n' +
                '    <h2>Basic Progress Bar</h2>\n' +
                '<div class="progress">\n' +
                '    <div class="progress-bar" role="progressbar" aria-valuenow="'+progress+'" aria-valuemin="0" aria-valuemax="'+total+'" style="width:'+percentage+'%">\n' +
                '    <span class="sr-only">'+percentage+'% Complete</span>\n' +
                '    </div>\n' +
                '    </div>\n' +
                '    </div>';*/

            coursesHTML += '<tr>' +
                '<td>${course.courseID}</td>' +
                '<td>${course.username}</td>' +
                '<td>${course.coursename}</td>' +
                /*'<td>${progressBar}</td>' +
                '<td>${starText}</td>' +*/
                '<td>${course.tags}</td>' +
                '</tr>';
        }
        coursesHTML += '</table>';
        document.getElementById("listDiv").innerHTML = coursesHTML;

    });

}

