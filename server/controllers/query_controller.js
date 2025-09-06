const pool = require('../config/mysql_config.js');
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
const JWT_SECRET = 'secret';
const {filterQuery} = require('../middleware/filter.js');



exports.runQuery =async(req,res)=>{
    
    const query = req.body.query;
    const allowed = await filterQuery(query,pool);
       if (!allowed) {
      res.status(400).send({ error: 'Access Denied' });
      return;
    }
   const [rows] = await pool.query(query);
   console.log(rows);
    res.send(rows);
}

exports.runQueryGet =async(req,res)=>{
     const query = req.query.query; 
    if (!filterQuery(query)) {
        return res.status(400).send({ error: 'Access Denied' });
    }

    const [rows] = await pool.query(query);
    res.send(rows);

}