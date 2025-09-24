require('dotenv').config();
const express  =require('express');
const fs = require('fs');
const pool = require('./config/mysql_config');
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
const path =require("path");
const app = express();
//change the port if server is already running on the same port
const port = 3007;


const JWT_SECRET = process.env.ACCESS_TOKEN_SECRET;

app.use(express.static(path.join(__dirname, 'public')));

app.use(express.json());
pool.getConnection((err,connection)=>{
    if(err) throw err;
    console.log('connected as id '+connection.threadId);
})

//this is a middleware function to check if the user is authenticated else will block the request

function authenticateToken(req, res, next) {
    const authHeader = req.headers['authorization'];
    const token = authHeader && authHeader.split(' ')[1];
        console.log(token);

    if (!token){
        console.log("no token");
         return res.sendStatus(401);}

    jwt.verify(token, JWT_SECRET, (err, user) => {
        if (err) {
            console.log(err);
            return res.sendStatus(403);}
        req.user = user;
        next();
    });
}

app.use("/auth",  require("./routes/auth"));
app.use("/run",authenticateToken, require("./routes/query"));
app.use("/run",authenticateToken, require("./routes/cmd"));
app.use("/admin",authenticateToken, require("./routes/admin"));
app.get("/", (req, res) => res.send("Hello World"));
app.post("/test/msg",authenticateToken ,(req, res) => 
 {
    const msg = req.body;
    console.log(msg);
    

 }
);


app.listen(port,()=>console.log(`server started at ${port}`));