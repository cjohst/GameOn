import express from "express";
import {
  getPreferences,
  getPreferencesById,
  createPreferences,
  updatePreferences,
  deletePreferences,
} from "../controllers/PreferencesController";

const router = express.Router();

router.get("/", getPreferences);
router.get("/:id", getPreferencesById);
router.post("/", createPreferences);
router.put("/:id", updatePreferences);
router.delete("/:id", deletePreferences);

export default router;