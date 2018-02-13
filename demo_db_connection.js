var mysql = require('mysql');

var con = mysql.createConnection( {
    host: 'vobot.cbd0j537sw1a.us-east-1.rds.amazonaws.com',
    port: '3306',
    user: 'scbhatia',
    password: 'ApriL-0696',
    database: 'vobot'
});

con.connect(function(err) {
    if (err) throw err;
    console.log("Conected!");

    con.query("CREATE DATABASE mydb", function(err,result) {
        if (err) throw err;
        console.log("Database created");
    });
});