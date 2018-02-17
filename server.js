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
    password: 'ApriL-0696',
    database: 'vobot'
});

con.connect(function(err) {
    if (err) throw err;
    console.log("Conected!");

});

// pushing child's name, child's word, level for word, individual simiarlity score, session number to sessions table (adding every time)
app.post('/sessions', function(req,res) {
    let phone_number = req.body.phone_number;
    let session = req.body.session;
    let childs_word = req.body.childs_word;
    let childs_name = req.body.childs_name;
    let indiv_score = req.body.indiv_score;
    let level = req.body.level;
 
    if (!phone_number || !session || !childs_word || !childs_name || !indiv_score || !level) {
        return res.status(400).send({ error:true, message: 'Please provide task' });
    }

    var info = {phone_number: phone_number,  session: session, childs_word: childs_word, childs_name: childs_name, indiv_score: indiv_score, level: level};
    
    con.query('INSERT INTO sessions SET ? ', info, function(error, results, fields) {
        if (error) throw error;
        return res.send({ error: false, data: results, message: 'New task has been created successfully.' });
    });

});

// get information for feedback
// selecting the specfic session and word from the sessions table for specific word
app.get('/sessions/graph/:phone&:word', function(req, res) {
    let phone_number = req.params.phone;
    let childs_word = req.params.word;

    if (!phone_number || !childs_word) {
        return res.status(400).send({error:true, message: 'Please enter valid parameters.'})
    }

    con.query('SELECT session, indiv_score FROM sessions WHERE phone_number=? AND childs_word=?', [phone_number, childs_word], function(error, results, fields) {
        if (error) throw error;
        return res.send({error: false, data: results, message: 'Information Taken'})
    });
});

// phone_number accounts table (adding a new entry)
app.post('/accounts', function (req, res) {
 
    let phone_number = req.body.phone_number;
 
    if (!phone_number) {
        return res.status(400).send({ error:true, message: 'Please provide task' });
    }
 
    con.query("INSERT INTO accounts SET ? ", { phone_number: phone_number }, function (error, results, fields) {
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