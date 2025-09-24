const pool = require('../config/mysql_config');
const { filterCMD } = require('../middleware/filter.js');
const { exec } = require('child_process');

exports.executeCmd = async (req, res) => {
  try {
    const cmd = req.body.cmd;

    if (!cmd) {
      return res.status(400).json({ error: 'No command provided' });
    }

    // Check command against denied list
    const allowed = await filterCMD(cmd, pool);
    if (!allowed) {
      return res.status(403).json({ error: 'Command is blocked' });
    }

    // Run the command with a timeout
    exec(cmd, { timeout: 5000, shell: true }, (error, stdout, stderr) => {
      if (error) {
        // Command failed
        console.error('Command failed:', error);
        return res.status(500).json({
          error: stderr?.toString().trim() || error.message,
        });
      }

      const output = stdout?.toString().trim();
      res.status(200).json({
        output: output.length > 0 ? output : 'Command executed successfully (no output)',
      });
    });
  } catch (err) {
    console.error(err);
    return res.status(500).json({ error: 'Internal server error' });
  }
};
