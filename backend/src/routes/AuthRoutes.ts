import { Router } from "express";
import dotenv from "dotenv";
import { handleLoginOrRedirect, handleDiscordCallback, handleRegister, handleLogout, protectEndpoint } from "../controllers/AuthController";

dotenv.config();

const authRouter: Router = Router();

authRouter.get("/login", handleLoginOrRedirect);
authRouter.get("/callback_discord", handleDiscordCallback);
authRouter.post("/register", protectEndpoint, handleRegister)
authRouter.post("/logout", protectEndpoint, handleLogout)

authRouter.get("/redirect", (req, res) => {
    if (req.query.error) {
        res.redirect(`intent://startup#Intent;scheme=gameoncpen;package=com.example.gameon;end;`)
    } else 
        res.redirect(`intent://redirect?code=${req.query.code}#Intent;scheme=gameoncpen;package=com.example.gameon;end;`)
})

export default authRouter;