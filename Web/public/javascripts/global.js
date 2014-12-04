var eventListData = [];
var userListData = [];
var sessionID = '';

$(document).ready(function() {
    populateTable();

    $('#userList table tbody').on('click', 'td a.linkshowuser', showUserInfo);
    $('#btnAddUser').on('click', addUser);
    $('#userList table tbody').on('click', 'td a.linkdeleteuser', deleteuser);

    $('#btnLoginUser').on('click', authUser);
    $('#eventList table tbody').on('click', 'td a.linkshowevent', showeventInfo);


    $('#btnAddevent').on('click', addevent);

    $('#eventList table tbody').on('click', 'td a.linkdeleteevent', deleteevent);

});

function populateTable() {
    populateEventsTable();
    populateUsersTable();
}

function populateUsersTable() {

    var tableContent = '';
    $.getJSON("/users/userlist", function(data) {
        console.log('huh', data);
        userListData = data;
        $.each(data, function() {
            tableContent += '<tr>';
            tableContent += '<td><a href="#" class="linkshowuser" rel="' + this.username + '" title="Show Details">' + this.username + '</a></td>';
            tableContent += '<td>' + this.userEmail + '</td>';
            tableContent += '<td>' + this.events + '</td>';
            tableContent += '<td><a href="#" class="linkdeleteuser" rel="' + this._id + '">delete</a></td>';
            tableContent += '</tr>';
        });

        $('#userList table tbody').html(tableContent);

    });
};

function populateEventsTable() {


    var tableContent = '';


    $.getJSON("/events/eventlist", function(data) {


        eventListData = data;


        $.each(data, function() {
            tableContent += '<tr>';
            tableContent += '<td><a href="#" class"linkshowevent" rel="' + this.eventname + '" title="Show Details">' + this.eventname + '</a></td>';
            tableContent += '<td>' + this.userid + '</td>';
            tableContent += '<td>' + this.location + '</td>';
            tableContent += '<td>' + this.date + '</td>';
            tableContent += '<td>' + this.time + '</td>';
            tableContent += '<td><a href="#" class="linkdeleteevent" rel="' + this._id + '">delete</a></td>';
            tableContent += '</tr>';
        });


        $('#eventList table tbody').html(tableContent);
    });
};

function showUserInfo(user) {
    user.preventDefault();
    var thisUserName = $(this).attr('rel');
    var arrayPosition = userListData.map(function(arrayItem) {
        return arrayItem.username;
    }).indexOf(thisUserName);

    var thisUserObject = userListData[arrayPosition];

    $('#userName').text(thisUserObject.username);
    $('#userEmail').text(thisUserObject.userEmail);
    $('#userEvents').text(thisUserObject.userEvents);
};


function showeventInfo(event) {

    event.preventDefault();

    var thiseventName = $(this).attr('rel');

    var arrayPosition = eventListData.map(function(arrayItem) {
        return arrayItem.eventname;
    }).indexOf(thiseventName);
    var thiseventObject = eventListData[arrayPosition];
    $('#eventName').text(thiseventObject.eventname);
    $('#eventDate').text(thiseventObject.date);
    $('#eventTime').text(thiseventObject.time);
    $('#eventLocation').text(thiseventObject.location);

};

function addUser(user) {
    user.preventDefault();

    var errorCount = 0;
    $('#adduser input').each(function(index, val) {
        if ($(this).val() === '') {
            errorCount++;
        }
    });

    if (errorCount === 0) {
        var newUser = {
            'username': $('#adduser fieldset input#inputUserName').val(),
            'userEmail': $('#adduser fieldset input#inputUserEmail').val(),
            'userPassword': $('#adduser fieldset input#inputUserPassword').val()
        }

        $.ajax({
            type: 'POST',
            data: newUser,
            url: '/users/adduser',
            dataType: 'JSON'
        }).done(function(response) {
            if (response.msg === '') {
                $('#adduser fieldset input').val('');
                populateUsersTable();
            } else {
                alert(JSON.stringify(response.msg));
            }
        });
    } else {
        alert('yo some fields re missing');
        return false;
    }
}

function authUser(user) {
    user.preventDefault();

    var errorCount = 0;
    $('#loginuser input').each(function(index, val) {
        if ($(this).val() === '') {
            errorCount++;
        }
    });

    if (errorCount === 0) {
        var userinfo = {
            'userName': $('#loginUser fieldset input#inputUserName').val(),
            'userPassword': $('#loginUser fieldset input#inputUserPassword').val()
        }

        console.log(userinfo);

        $.ajax({
            type: 'POST',
            data: userinfo,
            url: '/users/auth',
            dataType: 'JSON'
        }).done(function(response) {
            if (response.msg === '') {
                alert('504 internal server error');
            } else {
                sessionID = response.msg;
                alert(JSON.stringify(response.msg));
                console.log(sessionID);
            }
        });
    } else {
        alert('please enter all the required fields');
    }

}

function addevent(event) {
    event.preventDefault();

    var errorCount = 0;
    $('#addevent input').each(function(index, val) {
        if ($(this).val() === '') {
            errorCount++;
        }
    });

    if (errorCount === 0) {

        var userToken = $('#addevent fieldset input#inputUserToken').val();
        var newevent = {
            'userToken': userToken,
            'eventname': $('#addevent fieldset input#inputeventName').val(),
            'description': $('#addevent fieldset input#inputeventdescription').val(),
            'time': $('#addevent fieldset input#inputeventtime').val(),
            'location': $('#addevent fieldset input#inputeventLocation').val(),
            'date': $('#addevent fieldset input#inputeventdate').val()
        }

        $.ajax({
            type: 'POST',
            data: newevent,
            url: '/events/addevent',
            dataType: 'JSON'
        }).done(function(response) {

            if (response.msg === '') {

                $('#addevent fieldset input').val('');

                alert(response.msg);
                // Update the table
                populateEventsTable();

            } else {
                alert('Error: ' + response.msg);

            }
        });
    } else {
        alert('Please fill in all fields');
        return false;
    }
};

function deleteuser(user) {
    user.preventDefault();
    var confirmation = confirm('are you sure you want to delete ths event');

    if (confirmation === true) {
        $.ajax({
            type: 'DELETE',
            url: 'users/deleteuser/' + $(this).attr('rel')
        }).done(function(response) {
            if (response.msg === '') {

            } else {
                alert('error: ' + response.msg);
            }
            populateUsersTable();
        });
    } else {
        return false;
    }
}

function deleteevent(event) {
    event.preventDefault();


    var confirmation = confirm('Are you sure you want to delete this event?');


    if (confirmation === true) {

        $.ajax({
            type: 'DELETE',
            url: '/events/deleteevent/' + $(this).attr('rel')
        }).done(function(response) {

            if (response.msg === '') {} else {
                alert('Error: ' + response.msg);
            }

            populateEventsTable();

        });

    } else {
        return false;
    }

};