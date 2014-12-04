var express = require('express');
var app = express();
var router = express.Router();
	
/* GET home page. */
router.get('/', function(req, res) {
	res.sendFile(__dirname + "/views/login.html");
});

router.get('/users', function(req, res) {
	res.sendFile(__dirname + "/views/users.html");
});

router.get('/events', function(req, res) {
	res.sendFile(__dirname + "/views/events.html");
});

module.exports = router;
