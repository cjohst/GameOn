import { Request, Response } from "express";
import { AppDataSource } from "../data-source";
import { Game } from "../entity/Game";

export const getGames = async (req: Request, res: Response) => {
  const gameRepository = AppDataSource.getRepository(Game);
  const games = await gameRepository.find();
  res.json(games);
};

export const getGameById = async (req: Request, res: Response) => {
  const gameRepository = AppDataSource.getRepository(Game);
  const game = await gameRepository.findOne({
    where: { game_id: parseInt(req.params.id) },
  });
  if (game) {
    res.json(game);
  } else {
    res.status(404).json({ message: "Game not found" });
  }
};

export const createGame = async (req: Request, res: Response) => {
  const gameRepository = AppDataSource.getRepository(Game);
  const game = gameRepository.create(req.body);
  await gameRepository.save(game);
  res.status(201).json(game);
};

export const updateGame = async (req: Request, res: Response) => {
  const gameRepository = AppDataSource.getRepository(Game);
  const game = await gameRepository.findOne({
    where: { game_id: parseInt(req.params.id) },
  });
  if (game) {
    gameRepository.merge(game, req.body);
    await gameRepository.save(game);
    res.json(game);
  } else {
    res.status(404).json({ message: "Game not found" });
  }
};

export const deleteGame = async (req: Request, res: Response) => {
  const gameRepository = AppDataSource.getRepository(Game);
  const game = await gameRepository.findOne({
    where: { game_id: parseInt(req.params.id) },
  });
  if (game) {
    await gameRepository.remove(game);
    res.status(204).send();
  } else {
    res.status(404).json({ message: "Game not found" });
  }
};