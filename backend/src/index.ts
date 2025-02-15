import express from "express";
import dotenv from "dotenv";
import { authRouter } from "./routes/auth";
import { AppDataSource } from "./data-source";

import userRoutes from "./routes/UserRoutes";
import preferencesRoutes from "./routes/PreferencesRoutes";
import gameRoutes from "./routes/GameRoutes";
import groupRoutes from "./routes/GroupRoutes";
import reportRoutes from "./routes/ReportRoutes";
import adminRoutes from "./routes/AdminRoutes";

// Load environment variables
dotenv.config();

const app = express();

// Use body parsing middleware
app.use(express.json());

const PORT = process.env.PORT || 3000;

// Initialize the data source and start the server
AppDataSource.initialize()
  .then(() => {
    console.log("Data Source has been initialized!");

    // Start the server
    app.listen(PORT, () => {
      console.log(`Server is running on http://localhost:${PORT}`);
    });
  })
  .catch((error) => {
    console.error("Error during Data Source initialization:", error);
  });

// Define routes
app.use('/auth', authRouter);
app.use("/users", userRoutes);
app.use("/preferences", preferencesRoutes);
app.use("/games", gameRoutes);
app.use("/groups", groupRoutes);
app.use("/reports", reportRoutes);
app.use("/admins", adminRoutes);