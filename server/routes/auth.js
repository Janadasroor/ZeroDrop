const router = require('express').Router();
const authController = require("../controllers/auth_controller");

router.post("/login", authController.login);
router.post("/register", authController.register);
router.post("/refresh", authController.refreshToken);
router.post("/logout", authController.logout);

module.exports = router;