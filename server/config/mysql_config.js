require('dotenv').config();
const mysql = require('mysql2/promise');
const pool = mysql.createPool({
    host:process.env.DB_HOST||"localhost",
    user:process.env.DB_USER ||"root",      //replace with your username , the default username is root
    password:process.env.DB_PASS || "root",  //replace with your password , the default password is root
    database:process.env.DB_NAME ||"cascadia", //replace with your database name
})
module.exports = pool;