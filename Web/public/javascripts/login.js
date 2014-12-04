$(document).ready(function() {
    //http://bootsnipp.com/snippets/featured/parallax-login-forms
    if (typeof $.cookie('token') !== 'undefined') {
        location.href = "/events";
    }

    $(document).mousemove(function(event) {
        TweenLite.to($('body'),
            .5, {
                css: {
                    backgroundPosition: "" + parseInt(event.pageX / 8) + "px " + parseInt(event.pageY / '12') + "px, " + parseInt(event.pageX / '15') + "px " + parseInt(event.pageY / '15') + "px, " + parseInt(event.pageX / '30') + "px " + parseInt(event.pageY / '30') + "px"
                }
            });
    });
    $('#btn-login-user').on('click', authUser);
    $('#btn-signup-user').on('click', signupUser);


    $('#input-signup-password').focus(function() {
        $('#input-signup-confirm-password').css('display', 'block');
    });

    $('#input-signup-password').focusout(function() {
        if ($('#input-signup-password').val() === '')
            $('#input-signup-confirm-password').css('display', 'none');
    });

});

authUser = function() {
    var errorCount = 0;
    $('.login-form').each(function(index, val) {
        if ($(this).val() === '') {
            errorCount++;
        }
    });

    if (errorCount === 0) {

        var username = $('#input-login-username').val();
        var password = $('#input-login-password').val();

        var userinfo = {
            "username": username,
            "userPassword": password
        };

        console.log(userinfo);

        $.ajax({
            type: 'POST',
            data: userinfo,
            url: '/users/auth',
            dataType: 'JSON'
        }).done(function(response) {
            console.log(response);
            if (response.hasOwnProperty('token')) {

                location.href = "/events";
                $.cookie("username", response["username"]);
                $.cookie("token", response["token"]);
            }
        });
    } else {
        alert('please enter all the required fields');
    }


};

signupUser = function() {

    var errorCount = 0;
    $('.signup-form').each(function(index, val) {
        if ($(this).val() === '') {
            errorCount++;
        }
    });

    if (errorCount === 0) {

        var username = $('#input-signup-username').val();
        var useremail = $('#input-signup-email').val();
        var password = $('#input-signup-password').val();
        var confirmpassword = $('#input-signup-confirm-password').val();

        if (password !== confirmpassword) {
            alert('Passwords do not match!');
            return;
        }

        var userinfo = {
            "username": username,
            "userEmail": useremail,
            "userPassword": password
        };

        console.log(userinfo);

        $.ajax({
            type: 'POST',
            data: userinfo,
            url: '/users/adduser',
        }).done(function(response) {
            alert("Succesfully Registered");
        });


    } else {
        alert('please enter all the required fields');
    }




}