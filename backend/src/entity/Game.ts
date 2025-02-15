import { Entity, PrimaryGeneratedColumn, Column, OneToMany } from "typeorm";
import { Group } from "./Group";

@Entity()
export class Game {
  @PrimaryGeneratedColumn()
  game_id!: number;

  @Column({ unique: true })
  game_name!: string;

  @Column({ nullable: true })
  description!: string;

  @OneToMany(() => Group, (group) => group.game)
  groups!: Group[];
}