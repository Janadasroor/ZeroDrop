const router = require('express').Router();
const {executeCmd} = require('../controllers/cmd_controller.js');

router.post("/cmd",executeCmd);
module.exports = router;