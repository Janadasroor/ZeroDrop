const mysql = require('mysql2/promise');

const pool = mysql.createPool({
    host:'localhost',
    user:'root',      //replace with your username , the default username is root
    password:'root',  //replace with your password , the default password is root
    database:'database_name', //replace with your database name
})
module.exports = pool;