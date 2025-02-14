import express from "express";
import dotenv from "dotenv";
import { authRouter } from "./auth";

//load env variables
dotenv.config();

const app = express();

const PORT = process.env.PORT || 3000;
//Auth Route
app.use('/auth', authRouter)

app.listen(PORT, () => {
  console.log(`Server is running on http://localhost:${PORT}`);
});