import { Request, Response } from "express";
import { AppDataSource } from "../data-source";
import { Report } from "../entity/Report";
import { User } from "../entity/User";
import { Group } from "../entity/Group";

export const getReports = async (req: Request, res: Response) => {
  const reportRepository = AppDataSource.getRepository(Report);
  const reports = await reportRepository.find({ relations: ["reporter", "reported_user", "group"] });
  res.json(reports);
};

export const getReportById = async (req: Request, res: Response) => {
  const reportRepository = AppDataSource.getRepository(Report);
  const report = await reportRepository.findOne({
    where: { report_id: parseInt(req.params.id) },
    relations: ["reporter", "reported_user", "group"],
  });
  if (report) {
    res.json(report);
  } else {
    res.status(404).json({ message: "Report not found" });
  }
};

export const createReport = async (req: Request, res: Response) => {
  const reportRepository = AppDataSource.getRepository(Report);
  const report = reportRepository.create(req.body);
  await reportRepository.save(report);
  res.status(201).json(report);
};

export const resolveReport = async (req: Request, res: Response) => {
  const reportRepository = AppDataSource.getRepository(Report);
  const report = await reportRepository.findOne({
    where: { report_id: parseInt(req.params.id) },
  });
  if (report) {
    report.resolved = true;
    await reportRepository.save(report);
    res.json(report);
  } else {
    res.status(404).json({ message: "Report not found" });
  }
};

export const deleteReport = async (req: Request, res: Response) => {
  const reportRepository = AppDataSource.getRepository(Report);
  const report = await reportRepository.findOne({
    where: { report_id: parseInt(req.params.id) },
  });
  if (report) {
    await reportRepository.remove(report);
    res.status(204).send();
  } else {
    res.status(404).json({ message: "Report not found" });
  }
};