const router =require('express').Router();
const {addDeniedQuery,addDeniedCommand} = require('../controllers/admin_controller');
router.post("/addDeniedQuery",addDeniedQuery);
router.post("/addDeniedCommand",addDeniedCommand);
module.exports = router;