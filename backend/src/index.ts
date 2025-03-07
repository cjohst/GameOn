import express from "express";
import cors from 'cors';
import dotenv from "dotenv";
import authRouter from "./routes/AuthRoutes";
import { AppDataSource } from "./data-source"
import { RedisStore } from "connect-redis"
import session from "express-session"

import userRoutes from "./routes/UserRoutes";
import preferencesRoutes from "./routes/PreferencesRoutes";
import gameRoutes from "./routes/GameRoutes";
import groupRoutes from "./routes/GroupRoutes";
import reportRoutes from "./routes/ReportRoutes";
import adminRoutes from "./routes/AdminRoutes";
import matchmakingRoutes from "./routes/MatchmakingRoutes";
import { connectToRedisClient, redisClient } from "./redis_client";
import { processMatchmakingQueue } from './services/MatchmakingService';

// Load environment variables
dotenv.config();

const app = express();

app.use(cors({
  origin: true,
  credentials: true
}));

// Use body parsing middleware
app.use(express.json());

// Middleware: Redis Sessions Setup
app.use(
  session({
    store: new RedisStore({ client: redisClient }),
    secret: process.env.SESSION_SECRET || 'your-secret-key', 
    name: 'discord_app.sid',                                 
    resave: false,                                            
    saveUninitialized: false,                                 
    cookie: {
      secure: false,            // Should be set to true to prevent session hijacking attacks.
      httpOnly: true,           // JS cannot access cookie
      sameSite: 'lax',       // CSRF protection
      maxAge: 1000 * 60 * 60 * 24, // 1 day expiration
    },
  })
);

//SessionData User Type Declaration
declare module 'express-session' {
  interface SessionData {
    user?: {
      discord_id: string;
      discord_access_token: string;
      discord_refresh_token: string;
      discord_username?: string;
      discord_email?: string;
      discord_avatar?: string,
      temp_session: boolean;
    };
  }
}

const PORT = process.env.PORT || 3000;

// Initialize the data source and start the server
AppDataSource.initialize()
  .then(async () => {
    console.log("Data Source has been initialized!");

    await connectToRedisClient();

    // Start the server
    app.listen(PORT, () => {
      console.log(`Server is running on http://localhost:${PORT}`);
    });

    // Periodically process the matchmaking queue
    setInterval(async () => {
      console.log('Processing matchmaking queue...');
      await processMatchmakingQueue();
    }, 10000); // Adjust the interval as needed (e.g., every 10 seconds)
  })
  .catch((error) => {
    console.error("Error during Data Source initialization:", error);
  });

// Uncomment to see which requests are sent
// app.use('/', (req, res, next) => {
//   console.log(req.method, req.url)
//   next()
// })

// Define routes
app.use('/auth', authRouter);
app.use("/users", userRoutes);
app.use("/preferences", preferencesRoutes);
app.use("/games", gameRoutes);
app.use("/groups", groupRoutes);
app.use("/reports", reportRoutes);
app.use("/admins", adminRoutes);
app.use("/matchmaking", matchmakingRoutes);