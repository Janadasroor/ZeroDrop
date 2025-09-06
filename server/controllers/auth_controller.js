// controllers/auth_controller.js
const pool = require("../config/mysql_config");
const bcrypt = require("bcrypt");
const jwt = require("jsonwebtoken");
const JWT_SECRET = process.env.JWT_SECRET || "secret";

exports.login = async (req, res) => {
  try {
    // defensive: req.body might be undefined if client didn't send JSON
    const { username, password } = req.body || {};
    console.log("login....");
    if (!username || !password) {
      return res.status(400).json({ error: "Missing username or password" });
    }

    // pool.query returns [rows, fields]
    const [rows] = await pool.query(
      "SELECT id, username, password FROM admins WHERE username = ?",
      [username]
    );

    if (!rows || rows.length === 0) {
      return res.status(401).json({ error: "Invalid credentials" });
    }

    const user = rows[0];

    const isMatch = await bcrypt.compare(password, user.password);
    if (!isMatch) {
      return res.status(401).json({ error: "Invalid credentials" });
    }

    // Sign token with id and username so client and other endpoints can rely on req.user.username
    const token = jwt.sign({ id: user.id, username: user.username }, JWT_SECRET, { expiresIn: "4h" });

    const safeUser = { id: String(user.id), username: user.username };

    return res.json({ token, user: safeUser });
  } catch (err) {
    console.error("Error in login:", err);
    return res.status(500).json({ error: "Internal Server Error" });
  }
};

exports.register = async (req, res) => {
  try {
    const { username, password } = req.body || {};
    if (!username || !password) return res.status(400).json({ error: "Missing username or password" });

    // basic normalize
    const cleanUsername = String(username).trim();
    if (!cleanUsername) return res.status(400).json({ error: "Invalid username" });

    const salt = await bcrypt.genSalt(10);
    const hashedPassword = await bcrypt.hash(password, salt);

    // insert user
    const [result] = await pool.query(
      "INSERT INTO admins (username, password) VALUES (?, ?)",
      [cleanUsername, hashedPassword]
    );

    // create token for the new user
    const userId = result.insertId;
    const token = jwt.sign({ id: userId, username: cleanUsername }, JWT_SECRET, { expiresIn: "4h" });

    // return safe user info + token 
    const safeUser = { id: String(userId), username: cleanUsername };

    return res.status(201).json({  id: userId, token, user: safeUser });
  } catch (err) {
    console.error("Error in register:", err);
    if (err && err.code === "ER_DUP_ENTRY") {
      return res.status(409).json({ error: "Username already exists" });
    }
    return res.status(500).json({ error: "Internal Server Error" });
  }
};
