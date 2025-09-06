const pool = require("../config/mysql_config.js");

const MAX_CMD_LEN = 200;
const MAX_QUERY_LEN = 1000;

async function findAdmin({ id, username }) {
  try {
    if (id) {
      const [rows] = await pool.query("SELECT id, username FROM admins WHERE id = ?", [id]);
      return rows && rows.length ? rows[0] : null;
    }
    if (username) {
      const [rows] = await pool.query("SELECT id, username FROM admins WHERE username = ?", [username]);
      return rows && rows.length ? rows[0] : null;
    }
    return null;
  } catch (err) {
    console.error("DB error in findAdmin:", err);
    throw err;
  }
}

exports.addDeniedQuery = async (req, res) => {
  const rawQuery = req.body?.query;
  // Prefer username from JWT (req.user), fallback to body.username only if you really must
  const usernameFromToken = req.user?.username;
 

  if (!rawQuery || typeof rawQuery !== "string") {
    return res.status(400).json({ error: "Missing or invalid 'query' field" });
  }

  const query = rawQuery.trim();
  if (!query) return res.status(400).json({ error: "Empty query" });
  if (query.length > MAX_QUERY_LEN) return res.status(400).json({ error: "Query too long" });

  try {
    // Prefer token username; if not present and you explicitly allow fallback, use it.
    const admin = await findAdmin({ username: usernameFromToken  });

    if (!admin) return res.status(403).json({ error: "Access denied" });

    const table = "denied_queries";

    // duplicate check
    const [existing] = await pool.query(`SELECT id FROM ${table} WHERE query = ?`, [query]);
    if (existing && existing.length > 0) {
      return res.status(409).json({ error: "Query already exists in denied list" });
    }

    const [result] = await pool.query(`INSERT INTO ${table} (query, created_by) VALUES(?, ?)`, [query, admin.id]);

    if (result && result.affectedRows === 1) {
      return res.status(201).json({ message: "Query added", id: result.insertId });
    } else {
      console.warn("Insert did not affect rows:", result);
      return res.status(500).json({ error: "Failed to add query" });
    }
  } catch (err) {
    console.error("DB error in addDeniedQuery:", err);
    // handle duplicate key race if DB unique constraint exists
    if (err && err.code === 'ER_DUP_ENTRY') {
      return res.status(409).json({ error: "Query already exists (unique constraint)" });
    }
    return res.status(500).json({ error: "Internal Server Error" });
  }
};

exports.addDeniedCommand = async (req, res) => {
  const rawCmd = req.body?.command;
  const usernameFromToken = req.user?.username;
  const usernameFallback = req.body?.username;

  if (!rawCmd || typeof rawCmd !== "string") {
    return res.status(400).json({ error: "Missing or invalid 'command' field" });
  }

  const command = rawCmd.trim();
  if (!command) return res.status(400).json({ error: "Empty command" });
  if (command.length > MAX_CMD_LEN) return res.status(400).json({ error: "Command too long" });
  if (/\r|\n/.test(command)) return res.status(400).json({ error: "Invalid command format" });

  try {
    const admin = await findAdmin({ username: usernameFromToken || usernameFallback });
    if (!admin) return res.status(403).json({ error: "Access denied" });

    const table = "denied_commands";

    // check duplicate (case-insensitive)
    const [existing] = await pool.query(`SELECT id FROM ${table} WHERE LOWER(command) = LOWER(?)`, [command]);
    if (existing && existing.length > 0) {
      return res.status(409).json({ error: "Command already exists in denied list" });
    }

    const [result] = await pool.query(`INSERT INTO ${table} (command, created_by) VALUES(?, ?)`, [command, admin.id]);

    if (result && result.affectedRows === 1) {
      return res.status(201).json({ message: "Command added", id: result.insertId });
    } else {
      console.warn("Insert did not affect rows:", result);
      return res.status(500).json({ error: "Failed to add command" });
    }
  } catch (err) {
    console.error("DB error in addDeniedCommand:", err);
    if (err && err.code === 'ER_DUP_ENTRY') {
      return res.status(409).json({ error: "Command already exists (unique constraint)" });
    }
    return res.status(500).json({ error: "Internal Server Error" });
  }
};
