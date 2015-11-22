post = function(jsondata, url) {
	$.ajax({
	            type: 'POST',
	            data: userinfo,
	            url: '/users/auth',
	            dataType: 'JSON'
	        }).done(function(response)
	                {
	                    if(response.msg === '') {
	                        alert('504 internal server error');
	                    }
	                    else {
	                        sessionID = response.msg;
	                        alert(JSON.stringify(response.msg));
	                        console.log(sessionID);
	                    }
	                });
	    }
}

authenticate = function(username, password) {
	var userinfo = {
            'userName' : $('#loginUser fieldset input#inputUserName').val(),
            'userPassword': $('#loginUser fieldset input#inputUserPassword').val()
        }

    
}




