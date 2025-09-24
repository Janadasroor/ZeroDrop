# ZeroDrop - Full Stack Command Execution App

A full-stack application that allows Android clients to send command line instructions and MySQL queries to a Node.js server for remote execution. Built with Express.js backend and Jetpack Compose frontend.

## 🏗️ Architecture

```
┌─────────────────┐    HTTP/HTTPS    ┌─────────────────┐
│   Android App   │ ────────────────► │   Node.js API   │
│ (Jetpack Compose) │                  │   (Express.js)  │
└─────────────────┘                  └─────────────────┘
                                               │
                                               ▼
                                      ┌─────────────────┐
                                      │   MySQL DB      │
                                      │                 │
                                      └─────────────────┘
```

## 🚀 Features

- **Remote Command Execution**: Execute system commands on the server from Android app
- **MySQL Query Interface**: Run database queries remotely with results returned to mobile client
- **JWT Authentication**: Secure login/register system with token-based authentication
- **Modern UI**: Clean Android interface built with Jetpack Compose
- **Network Communication**: RESTful API communication between client and server
- **Port Forwarding Support**: Easy development setup with VS Code port forwarding

## 📋 Prerequisites

### Server Requirements
- Node.js (v14 or higher)
- MySQL Server
- VS Code (for development with port forwarding)

### Android Requirements
- Android Studio
- Android SDK (API level 25+)
- Kotlin support

## 🛠️ Installation & Setup

### 1. Server Setup (Node.js/Express)

```bash
# Clone the repository
git clone https://github.com/Janadasroor/ZeroDrop.git
cd zerodrop/server

# Install dependencies
npm install

# Create environment variables file (Optional)
cp .env.example .env

# Edit .env with your configuration
# DB_HOST=localhost
# DB_USER=your_mysql_user
# DB_PASSWORD=your_mysql_password
# DB_NAME=zerodrop
# JWT_SECRET=your_jwt_secret
# PORT=3000

# Start the server
npm start
```

### 2. Android App Setup
- Navigate to Android project
- Open `client/ZeroDrop_Demo` in Android Studio
 

### 3. VS Code Port Forwarding Configuration

1. **Configure Port Forwarding**:
    - It should be found near the terminal tab

2. **Make Port Public**:
   - In the Ports panel, right-click on your forwarded port
   - Select "Port Visibility" → "Public"
   - Copy the generated public URL (e.g., `https://abc123-3000.preview.app.github.dev`)

### 4. Configure Android Network Module
  
- Edit the file `app/src/main/java/com/janad/zerodrop/data/api/NetworkModule.kt`:
- 
```kotlin
    // Replace with your VS Code port forwarding public URL
    private const val BASE_URL = "https://your-forwarded-url.preview.app.github.dev/"

```

## 📡 API Endpoints

### Authentication
- `POST /auth/register` - User registration
- `POST /auth/login` - User login

### Commands & Queries
- `POST /run/command` - Execute system command
- `POST /run/query` - Execute MySQL query

### Example API Usage

```json
// Login Request
POST /auth/login
{
  "username": "username",
  "password": "password123"
}

// Command Execution
POST /run/command
Headers: { "Authorization": "Bearer <token>" }
{
  "command": "ls -la"
}

// MySQL Query
POST /run/query
Headers: { "Authorization": "Bearer <token>" }
{
  "query": "SELECT * FROM admins LIMIT 5"
}
```

## 📱 Android App Structure

```
app/src/main/java/com/janad/zerodrop/
├── data/
│   ├── api/
│   │   ├── ApiService.kt
│   │   └── NetworkModule.kt          # ← Configure server URL here
└── MainActivity.kt
```

## 🔧 Development Workflow

### For Server Development:
1. Make code changes
2. Server automatically updates 
3. Port forwarding automatically updates

### For Android Development:
1. Update `NetworkModule.kt` with new forwarded URL if needed
2. Build and run Android app
3. Test API communication

### Switching Between Environments:
```kotlin
// Development (VS Code Port Forwarding)
private const val BASE_URL = "https://abc123-3000.preview.app.github.dev/"

// Production
private const val BASE_URL = "https://your-production-server.com/"

// Local Development if you don't like to forward the port 
private const val BASE_URL = "http://10.0.2.2:3000/" // Android Emulator
// or
private const val BASE_URL = "http://192.168.1.100:3000/" // Physical Device
//This is your devlopment machine ip on the same local network http://192.168.1.100
```

## 🔒 Security Considerations

- JWT tokens will never expire 
- All API endpoints (except auth) require valid authentication
- Command execution is logged and monitored
- Database queries are parameterized to prevent SQL injection
- HTTPS required for production deployment

## 🐛 Troubleshooting

### Common Issues:

1. **Network Connection Failed**
   - Verify VS Code port forwarding is active and public
   - Check `NetworkModule.kt` has correct URL
   - Ensure Android device/emulator has internet access

2. **Authentication Failed**
   - Check JWT token validity
   - Verify server is running and accessible
   - Confirm user credentials are correct

3. **Command Execution Failed**
   - Check server permissions for command execution
   - Verify MySQL connection settings
   - Review server logs for detailed error messages


## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.


[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)


[![Node.js](https://img.shields.io/badge/Node.js-v18+-green)](https://nodejs.org/)


## 👨‍💻 Author

Janada Sroor


