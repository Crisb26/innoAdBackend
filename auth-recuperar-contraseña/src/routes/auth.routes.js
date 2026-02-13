const express = require("express");
const router = express.Router();
const { register, login } = require("../controllers/auth.controller");
const { forgotPassword, resetPassword } = require("../controllers/auth.controller");

router.post("/forgot-password", forgotPassword);
router.post("/reset-password/:token", resetPassword);


router.post("/register", register);
router.post("/login", login);

module.exports = router;
