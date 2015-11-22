var express = require('express');
var router = express.Router();
var bcrypt = require('bcrypt');
var uuid = require('node-uuid');
var mongo = require('mongoskin');
/*
 * GET userlist
 */
router.get('/userlist', function(req, res) {
	var db = req.db;
	db.collection('userlist').find().toArray(function (err, items) {
		res.json(items);
	});
});

/*
 * POST to adduser
 */
router.post('/adduser', function(req, res) {
	var db = req.db;
	req.body['userPassword'] = encrypt(req.body['userPassword']);	

	var random = uuid.v4();
	req.body["_id"] = mongo.helper.toObjectID(random);
	
	db.collection('userlist').insert(req.body, function(err, result) {
		if(err != null) {
			res.status(500);
			res.send();	
		}
		else {
			res.status(200);
			res.send();
		}
	});
});

//takes in username, which is facebook:username, but passed in client side. 
router.post('/facebook', function(req, res) {
	var db = req.db;
	
	var userlist = db.collection('userlist');
				userlist.find(
								{username: req.body["username"], 
								}
							  ).toArray(function(err, result) {
							  		if(err) {
										res.status(500);	
							  			res.send({msg: 'Internal server error'});
							  		}	
							  		else {
							  			if(result.length === 0) {
							  				var random = uuid.v4();
							  				req.body["_id"] = mongo.helper.toObjectID(random);
							  				db.collection('userlist').insert(req.body, function(err, result) {
							  					
							  					if(err !== null) {
							  						res.status(500);
							  						res.send({msg: 'Internal server error'});
							  					}
							  					else {
							  						res.status(200);
							  						res.send({
														username: req.body["username"],
							  							token: insertToken(userlist, req.body["username"]) });
							  					}

							  				});
							  			}
							  			else {
							  				if(result[0].hasOwnProperty('token')) {
							  					res.status(200);
							  					res.send({
							  						username: result[0].username,
													token: result[0].token});
							  				}
							  				else {
							  					res.status(200);
							  					res.send({
							  						username: req.body["username"],
							  						token: insertToken(userlist, req.body["username"])});
							  				}
							  			}
							  		}
							  	}	
							  );	
});

/*
 * DELETE to deleteuser
 */
router.delete('/deleteuser/:id', function(req, res) {
	var db = req.db;
	var userToDelete = req.params.id;
	db.collection('userlist').removeById(userToDelete, function(err, result) {
		res.send((result === 1) ? { msg: '' } : { msg:'error' + err });
	});
});

router.get('/hello/:name', function(req, res) {
	var name = req.params.name;
	res.status(200);
	res.send('Hello ' + name);
});

router.post('/auth', function(req, res) {
	var db = req.db;
	var userlist = db.collection('userlist');
				userlist.find(
								{username: req.body["username"], 
								}
							  ).toArray(function(err, result) {
							  		if(err) {
							  			//var random = Math.floor((Math.random() * 10000) + 1);
							  			//userlist.update({username: req.body["userName"], password: req.body["userPassword"]}, {$set:{token: random}}, 
							  			//				function (err, result) {
							  			//					if(!err) res.send(random);
							  			//				});
										res.status(500);	
							  			res.send({msg: 'Internal server error'});
							  		}	
							  		else {
							  			//res.send(result[0]);
							  			if(result.length === 0) {
							  				res.send({msg: 'invalid user found, some error code'});
							  			}
							  			else {
							  				var userPassword = result[0].userPassword;
							  				if(!verifyPassword(req.body["userPassword"], userPassword)) {
							  					res.status(400);
												res.send({msg: 'User not authorized'});
							  					return false;
							  				}


							  				if(result[0].hasOwnProperty('token')) {
							  					res.status(200);
							  					res.send({
													username: result[0].username,
													token: result[0].token});
							  				}
							  				else {
							  				var v5uuid = uuid.v4();
							  				userlist.update({username: req.body["username"]}, {$set:{token: v5uuid}}, 
							  								function(err, updateresult) {
							  									if(!err){
																	res.status(200);
																	res.send({token: v5uuid});
																}
							  								});
							  				}
							  			}
							  		}	
							  });
});

module.exports = router;

encrypt = function(password) {
	var salt = bcrypt.genSaltSync(10);
	var hash = bcrypt.hashSync(password, salt);
	return hash;
}

verifyPassword = function(password, passwordHash) {
	var verified = bcrypt.compareSync(password, passwordHash);
	return verified;
}

insertToken = function(userlist, _username) {
		
	
		var v5uuid = uuid.v4();
		userlist.update({username: _username}, {$set:{token: v5uuid}}, 
						function(err, updateresult) {

						});

		return v5uuid;
}