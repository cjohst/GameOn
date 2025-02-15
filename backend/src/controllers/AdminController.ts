import { Request, Response } from "express";
import { AppDataSource } from "../data-source";
import { Admin } from "../entity/Admin";
import { User } from "../entity/User";

export const getAdmins = async (req: Request, res: Response): Promise<void> => {
  const adminRepository = AppDataSource.getRepository(Admin);
  const admins = await adminRepository.find({ relations: ["user"] });
  res.json(admins);
};

export const getAdminById = async (req: Request, res: Response): Promise<void> => {
  const adminRepository = AppDataSource.getRepository(Admin);
  const admin = await adminRepository.findOne({
    where: { admin_id: parseInt(req.params.id) },
    relations: ["user"],
  });
  if (admin) {
    res.json(admin);
  } else {
    res.status(404).json({ message: "Admin not found" });
  }
};

export const createAdmin = async (req: Request, res: Response): Promise<void> => {
  const { user_id, permissions } = req.body;

  if (!user_id || !permissions) {
    res.status(400).json({ message: "user_id and permissions are required" });
    return;
  }

  const userRepository = AppDataSource.getRepository(User);
  const adminRepository = AppDataSource.getRepository(Admin);

  try {
    const user = await userRepository.findOne({
      where: { user_id: parseInt(user_id) },
    });

    if (!user) {
      res.status(404).json({ message: "User not found" });
      return;
    }

    const admin = adminRepository.create({
      permissions,
      user,
    });

    await adminRepository.save(admin);
    res.status(201).json(admin);
  } catch (error) {
    console.error("Error creating admin:", error);
    res.status(500).json({ message: "Internal server error" });
  }
};

export const updateAdmin = async (req: Request, res: Response): Promise<void> => {
  const adminRepository = AppDataSource.getRepository(Admin);
  const admin = await adminRepository.findOne({
    where: { admin_id: parseInt(req.params.id) },
  });
  if (admin) {
    adminRepository.merge(admin, req.body);
    await adminRepository.save(admin);
    res.json(admin);
  } else {
    res.status(404).json({ message: "Admin not found" });
  }
};

export const deleteAdmin = async (req: Request, res: Response): Promise<void> => {
  const adminRepository = AppDataSource.getRepository(Admin);
  const admin = await adminRepository.findOne({
    where: { admin_id: parseInt(req.params.id) },
  });
  if (admin) {
    await adminRepository.remove(admin);
    res.status(204).send();
  } else {
    res.status(404).json({ message: "Admin not found" });
  }
};