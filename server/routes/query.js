const router =require('express').Router();
const {runQuery} = require('../controllers/query_controller.js');
router.post("/query",runQuery);
module.exports = router;