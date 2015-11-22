var express = require('express');
var router = express.Router();
var uuid = require('node-uuid');
var mongo = require('mongoskin');
/*
 * GET eventlist
 */
router.get('/eventlist', function(req, res) {
	var db = req.db;
	db.collection('events').find().toArray(function (err, items) {
		res.json(items);
	});
});

router.get('/eventlist/city/:city', function(req, res) {
	var db = req.db;
	var _city = req.params.city;
	db.collection('events').find({city: _city}).toArray(function(err, items) {
		res.json(items);
	});
});

router.post('/eventlist/:userid', function(req, res) {

	var db = req.db;
	var userID = req.params.userid;
	var id = req.body['userToken'];

	var userlist = db.collection('userlist');
		userlist.find(
						{token: id}
					  ).toArray(function(err, result) {
					  		if(err) {
							  			res.send({msg: '504: internal server error'});
							  			return false;
							  		}	
							 else {
							 	if(result.length === 0) {
							 		res.send({msg: '400: not authorized'});
							 		return false;
							 	}
							 	else {
									var userlist = db.collection('events').find({
											username: userID}
										).toArray(function (err, result) {
											if(err) {
												res.send({msg: '504: internal server error'});
												return false;
A
											}
											else {
												res.json(result);
												return true;
											}
										});
								


								}
							}
});
});

/*
 * POST to addevent
 */
router.post('/addevent', function(req, res) {
	var db = req.db;
	var userToken = req.body["userToken"];
	var _userid = '';
	delete req.body["userToken"];

	var userlist = db.collection('userlist');
		userlist.find(
						{token: userToken}
					  ).toArray(function(err, result) {
					  		if(err) {
							  			res.send({msg: '504: internal server error'});
							  			return false;
							  		}	
							 else {
							 	if(result.length === 0) {
							 		res.send({msg: '400: not authorized'});
							 		return false;
							 	}
							 	else {
							 		req.body['userid'] = result[0]._id;
									req.body['username'] = result[0].username; //oh this is SUCH a bad way of doing this, but at this point i don't think anyone really cares, and lets see if anyone ever stumbles onto this comment again. 
							 		var random = uuid.v4();
    								req.body["_id"] = mongo.helper.toObjectID(random.toString());
    								userlist.update({_id: req.body["userid"]}, {$push:{events: req.body["_id"]}}, 
							  								function(err, updateresult) {
																if (err) return false;
							  								});
									db.collection('events').insert(req.body, function(err, result) {
													res.send(
														(err === null) ? { msg: req.body["_id"] } : { msg: err }
													);
									});
							 		return true;
							 	}
							 }
					});

	
});

router.post('/acceptevent/:eventid', function(req, res) {
	var db = req.db;
	var userToken = req.body["userToken"];
	var eventid = req.params.eventid;
	var _userid = '';
	delete req.body["userToken"];

	var userlist = db.collection('userlist');
	var events = db.collection('events');
		userlist.find(
						{token: userToken}
					  ).toArray(function(err, result) {
					  		if(err) {
							  			res.send({msg: '504: internal server error'});
							  			return false;
							  		}	
							 else {
							 	if(result.length === 0) {
							 		res.send({msg: '400: not authorized'});
							 		return false;
							 	}
							 	else {
    								
    								userlist.update({_id: req.body["userid"]}, {$push:{events: req.params.eventid}}, 
							  								function(err, updateresult) {
																if (err) return false;
							  								});

    								
							 		return true;
							 	}
							 }
					});

	
});



/*
 * DELETE to deleteevent
 */
router.delete('/deleteevent/:id', function(req, res) {
	var db = req.db;
	var eventToDelete = req.params.id;
	db.collection('events').removeById(eventToDelete, function(err, result) {
		res.send((result === 1) ? { msg: '' } : { msg:'error' + err });
	});
});

module.exports = router;
