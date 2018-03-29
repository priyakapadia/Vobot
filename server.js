const express = require('express');
const app = express();
const bodyParser = require('body-parser');
const mysql = require('mysql');

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({
    extended: true
}));
 
// connection configurations
const con = mysql.createConnection({
    host: 'vobot.cbd0j537sw1a.us-east-1.rds.amazonaws.com',
    port: '3306',
    user: 'scbhatia',
    password: '12345678',
    database: 'vobot'
});

con.connect(function(err) {
    if (err) throw err;
    console.log("Conected!");
});

// con.query("CREATE TABLE IF NOT EXISTS accounts (phone_number VARCHAR(10)" function(err, result) {
//     if (err) throw err;
//     console.log("Accounts Table Created");
// });

// con.query("CREATE TABLE IF NOT EXISTS sessions (phone_number VARCHAR(10)" function(err, result) {
//     if (err) throw err;
//     console.log("Accounts Table Created");
// });

// con.query("CREATE TABLE IF NOT EXISTS progress (phone_number VARCHAR(10)" function(err, result) {
//     if (err) throw err;
//     console.log("Accounts Table Created");
// });

// Calculates level, overall score, session 
app.post('/calc_val', function(req,res) {
    let phone_number = req.body.phone_number;
    let childs_word = req.body.childs_word;
    let childs_name = req.body.childs_name;
    let indiv_score = req.body.indiv_score;
    var session;
    var level;
    var score;
    var levelbool = false;
    var counter = 0;

    if (!phone_number || !childs_word || !childs_name || !indiv_score) {
        return res.status(400).send({ error:true, message: 'Please provide task' });
    }

    indiv_score = parseInt(indiv_score);

    con.query('SELECT level, overall_score, overall_sessions, counter FROM progress WHERE phone_number=? AND childs_word=?', [phone_number, childs_word], function(err,results, fields) {
        if (err) {
            console.log(err)
        }
        else {
            if (results.length > 0) {
                if (results) {
                    level = results[0].level;
                    score = results[0].overall_score;
        		    session = results[0].overall_sessions;
                    session = session + 1;
        		    counter = results[0].counter;
                    score = (score + indiv_score) / 2;
        		    if (counter >= 3) {
                        switch(level) {
                            case 2: // 10 - 20
                            // goes down
                                if (indiv_score < 10) {
                                    level = 1;
                                }
                                // goes up
                                else if (indiv_score >= 20) {
                                    level = 3;
                                    levelbool = true;
                                }
                                // same
                                else {
                                    level = 2;
                                }
                                break;
            
                            case 3: // 20-30
                                // goes down
                                if (indiv_score < 20) {
                                    level = 2;
                                }
                                // goes up
                                else if (indiv_score >= 30) {
                                    level = 4;
                                    levelbool = true;
                                }
                                // same
                                else {
                                    level = 3;
                                }
                                break;
            
                            case 4: // 30-40
                                // down
                                if (indiv_score < 30) {
                                    level = 3;
                                }
                                // up
                                else if (indiv_score >= 40) {
                                    level = 5;
                                    levelbool = true;
                                }
                                // same
                                else {
                                    level = 4;
                                }
                                break;
            
                            case 5: // 40-50
                                // down
                                if (indiv_score < 40) {
                                    level = 4;
                                }
                                // up
                                else if (indiv_score >= 50) {
                                    level = 6;
                                    levelbool = true;
                                }
                                // same
                                else {
                                    level = 5;
                                }
                                break;
            
                            case 6: // 50-60
                                // down
                                if (indiv_score < 50) {
                                    level = 5;
                                }
                                // up
                                else if (indiv_score >= 60) {
                                    level = 7;
                                    levelbool = true;
                                }
                                // same
                                else {
                                    level = 6;
                                }
                                break;
            
                            case 7: // 60-70
                                // down
                                if (indiv_score < 60) {
                                    level = 6;
                                }
                                // up
                                else if (indiv_score >= 70) {
                                    level = 8;
                                    levelbool = true;
                                }
                                // same
                                else {
                                    level = 7;
                                }
                                break;
            
                            case 8: //70-80
                                // goes down
                                if (indiv_score < 70) {
                                    level = 7;
                                }
                                // goes up
                                else if (indiv_score >= 80) {
                                    level = 9;
                                    levelbool = true;
                                }
                                // stays 
                                else {
                                    level = 8;
                                }
                                break;
            
                            case 9: //80-90
                                // goes down
                                if (indiv_score < 80) {
                                    level = 8;
                                }
                                // goes up
                                else if (indiv_score >= 90) {
                                    level = 10;
                                    levelbool = true;
                                }
                                // stays
                                else {
                                    level = 9;
                                }
                                break;
            
                            case 10: //90-100
                                // goes down
                                if (indiv_score < 90) {
                                    level = 9;
                                }
                                // stays
                                else {
                                    level = 10;
                                }
                                break;
            
                            default:
                                level = level;
                                break;
                        }
    		        counter = 0;
    		    }
    		    else {
    			    counter = counter + 1;
    		    }
                    return res.send({error:false, levelbool: levelbool, level: level, session: session, overall_score: score, counter: counter})
            }
        }
            else {
                level = 1;
                score = indiv_score;
                session = 1;
                counter = 0;
                // // goes up
                // if (indiv_score >= 10) {
                //     level = 2;
                //     levelbool = true;
                // }
                // // same
                // else {
                //     level = 1;
                // }
                return res.send({error:false, levelbool: levelbool, level: level, session: session, overall_score: score, counter: counter})
            }
        }

    });
});

//POSTS TO SESSIONS
app.post('/sessions', function(req,res) {
    let phone_number = req.body.phone_number;
    let childs_word = req.body.childs_word;
    let childs_name = req.body.childs_name;
    let indiv_score = req.body.indiv_score;
    let level = req.body.level;
    let session = req.body.session;

    indiv_score = Math.round(indiv_score);

    var data = {phone_number: phone_number, session: session, childs_word: childs_word, childs_name: childs_name, indiv_score: indiv_score, level: level};
    con.query('INSERT INTO sessions SET ?', {phone_number: phone_number, session: session, childs_word: childs_word, childs_name: childs_name, indiv_score: indiv_score, level: level} , function(error, results, fields) {
        if (error) throw error;
        return res.send({ error: false, data: results, message: 'Sessions table updated.' });
    });

});

app.post('/progress', function(req, res) {
    let phone_number = req.body.phone_number;
    let childs_word = req.body.childs_word;
    let childs_name = req.body.childs_name;
    let overall_score = req.body.overall_score;
    let level = req.body.level;
    let counter = req.body.counter;
    let overall_sessions = req.body.overall_sessions;   
    
    overall_score = Math.round(overall_score);    
    var sql = 'INSERT INTO progress (phone_number, childs_word, childs_name, overall_score, level, overall_sessions, counter) VALUES ? ON DUPLICATE KEY UPDATE overall_score=VALUES(overall_score), level=VALUES(level), overall_sessions=VALUES(overall_sessions), counter=VALUES(counter)';
    var data = [[phone_number, childs_word, childs_name, overall_score, level, overall_sessions, counter]];
    con.query(sql, [data], function(error, results) {
        if (error) throw error;
        return res.send({ error: false, data: results, message: 'Progress table updated.' });
    })
});

app.get('/calc_level/:phone&:word&:name', function(req, res) {
    let phone_number = req.params.phone;
    let childs_word = req.params.word;
    let childs_name = req.params.name;

    if (!phone_number || !childs_word || !childs_name) {
        return res.status(400).send({error:true, message: 'Please enter a phone number and word'})
    }

    con.query('SELECT level FROM progress WHERE phone_number=? AND childs_word=?', [phone_number, childs_word], function(error, results, fields) {
        if (error) throw error;
	if (results.length > 0) {
        	return res.send({error:false, data: results, message: 'Level found'})
    	}
	else {
		return res.send({error: false, level: 1});
	}
    });
});

// get information for feedback
// selecting the specfic session and word from the sessions table for specific word
app.get('/sessions/graph/:phone&:word', function(req, res) {
    let phone_number = req.params.phone;
    let childs_word = req.params.word;

    if (!phone_number || !childs_word) {
        return res.status(400).send({error:true, message: 'Please enter a phone number and word'})
    }

    con.query('SELECT session, indiv_score FROM sessions WHERE phone_number=? AND childs_word=?', [phone_number, childs_word], function(error, results, fields) {
        if (error) throw error;
        return res.send({error: false, data: results, message: 'Graph Information Taken'})
    });
});

// phone_number accounts table (adding a new entry)
app.post('/accounts', function (req, res) {
 
    let phone_number = req.body.phone_number;
 
    if (!phone_number) {
        return res.status(400).send({ error:true, message: 'Please enter a phone number' });
    }
 
    con.query("INSERT IGNORE INTO accounts SET ? ", { phone_number: phone_number }, function (error, results, fields) {
        if (error) throw error;
        return res.send({ error: false, data: results, message: 'New task has been created successfully.' });
    });
});

// checking contents of accounts table
app.get('/accounts', function(req,res) {
    con.query('SELECT * FROM accounts', function(error, results, fields) {
        if (error) throw error;
        return res.send({error: false, data: results, message: 'Accounts Table'});
    });
});

// checking contents of users table
app.get('/sessions', function(req,res) {
    con.query('SELECT * FROM sessions', function(error, results, fields) {
        if (error) throw error;
        return res.send({error: false, data: results, message: 'Sessions Table'});
    });
});

// checking contents of words table
app.get('/progress', function(req,res) {
    con.query('SELECT * FROM progress', function(error, results, fields) {
        if (error) throw error;
        return res.send({error: false, data: results, message: 'Progress Table'});
    });
});


// port must be set to 8080 because incoming http requests are routed from port 80 to port 8080
app.listen(8080, function () {
    console.log('Node app is running on port 8080');
});