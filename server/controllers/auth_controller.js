const pool = require("../config/mysql_config");
const bcrypt = require("bcrypt");
const jwt = require("jsonwebtoken");
require('dotenv').config();

const ACCESS_TOKEN_SECRET = process.env.ACCESS_TOKEN_SECRET;
const REFRESH_TOKEN_SECRET = process.env.REFRESH_TOKEN_SECRET ;

// generate access token
function generateAccessToken(user) {
  return jwt.sign(user, ACCESS_TOKEN_SECRET, { expiresIn: "5m" }); // short-lived
}

// generate refresh token
function generateRefreshToken(user) {
  return jwt.sign(user, REFRESH_TOKEN_SECRET, { expiresIn: "7d" }); // long-lived
}

// 🔹 LOGIN
exports.login = async (req, res) => {
  try {
    const { email, password } = req.body || {};
    if (!email || !password) {
      return res.status(400).json({ error: "Missing email or password" });
    }

    const [rows] = await pool.query(
      "SELECT id, email, password FROM admins WHERE email = ?",
      [email]
    );

    if (!rows || rows.length === 0) {
      return res.status(401).json({ error: "Invalid credentials" });
    }

    const user = rows[0];

    const isMatch = await bcrypt.compare(password, user.password);
    if (!isMatch) {
      return res.status(401).json({ error: "Invalid credentials" });
    }

    const safeUser = { id:toString(user.id), email: user.email };

    const accessToken = generateAccessToken(safeUser);
    const refreshToken = generateRefreshToken(safeUser);

    // store refresh token in DB
    const expiresAt = new Date(Date.now() + 7 * 24 * 60 * 60 * 1000);
    await pool.query(
      "INSERT INTO refresh_tokens (user_id, token, expires_at) VALUES (?, ?, ?)",
      [user.id, refreshToken, expiresAt]
    );

    return res.json({ accessToken, refreshToken, user: safeUser });
  } catch (err) {
    console.error("Error in login:", err);
    return res.status(500).json({ error: "Internal Server Error" });
  }
};

// 🔹 REGISTER
exports.register = async (req, res) => {
  try {
    const { email, password } = req.body || {};
    if (!email || !password)
      return res.status(400).json({ error: "Missing email or password" });

    const cleanEmail = String(email).trim();

    const salt = await bcrypt.genSalt(10);
    const hashedPassword = await bcrypt.hash(password, salt);

    const [result] = await pool.query(
      "INSERT INTO admins (email, password) VALUES (?, ?)",
      [cleanEmail, hashedPassword]
    );

    const userId = result.insertId;
    const safeUser = { id: toString(userId), email: cleanEmail };

    const accessToken = generateAccessToken(safeUser);
    const refreshToken = generateRefreshToken(safeUser);

    const expiresAt = new Date(Date.now() + 7 * 24 * 60 * 60 * 1000);
    await pool.query(
      "INSERT INTO refresh_tokens (user_id, token, expires_at) VALUES (?, ?, ?)",
      [userId, refreshToken, expiresAt]
    );

    return res.status(201).json({ accessToken, refreshToken, user: safeUser });
  } catch (err) {
    console.error("Error in register:", err);
    if (err && err.code === "ER_DUP_ENTRY") {
      return res.status(409).json({ error: "Email already exists" });
    }
    return res.status(500).json({ error: "Internal Server Error" });
  }
};

// 🔹 REFRESH TOKEN ENDPOINT
exports.refreshToken = async (req, res) => {
  const { refreshToken } = req.body;
  console.log({refreshToken: refreshToken  });
  if (!refreshToken) return res.status(401).json({ error: "Missing refresh token" });

  try {
    // check refresh token in DB
    const [rows] = await pool.query(
      "SELECT user_id FROM refresh_tokens WHERE token = ? AND expires_at > NOW()",
      [refreshToken]
    );

    if (!rows || rows.length === 0) {

      return res.status(403).json({ error: "Invalid or expired refresh token" });
    }

    // verify refresh token signature
    jwt.verify(refreshToken, REFRESH_TOKEN_SECRET, (err, user) => {
      if (err) return res.status(403).json({ error: "Invalid token" });

      const newAccessToken = generateAccessToken({ id: user.id, email: user.email });
      console.log({newAccessToken:newAccessToken});
      return res.status(200).json({ accessToken: newAccessToken });

    });
  } catch (err) {
    console.error("Error in refreshToken:", err);
    return res.status(500).json({ error: "Internal Server Error" });
  }
};

// 🔹 LOGOUT
exports.logout = async (req, res) => {
  const { refreshToken } = req.body;
  if (!refreshToken) return res.status(400).json({ error: "Missing refresh token" });

  await pool.query("DELETE FROM refresh_tokens WHERE token = ?", [refreshToken]);
  return res.status(200).json({ message: "Logged out" });
};
