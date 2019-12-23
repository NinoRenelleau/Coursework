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
    fetch('users/check', {method: 'get', body: Cookies.get("token")}).then(response => response.json()).then(user => {
         id = user.ID;
    });

    fetch('/courses/list', {method: 'get'}).then(response => response.json()).then(courses => {

        for (let course of courses){
            let starNum = Math.round(course.rating);
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
            let formData = new FormData();
            formData.append('courseID', course.courseID);
            formData.append('UserID', id);
            fetch('/history/courseScore', {method: 'get', body: formData}).then(response => response.json()).then(responseData => {
                progress = responseData.Score;
            });

            coursesHTML += '<tr>' +
                '<td>${course.courseID}</td>' +
                '<td>${course.username}</td>' +
                '<td>${course.coursename}</td>' +
                '<td>${course.Id}</td>' +
                '<td>'+starText+'</td>' +
                '<td>${course.tags}</td>' +
        }
    });

}
