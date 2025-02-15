import { Entity, PrimaryGeneratedColumn, Column, ManyToOne, OneToMany, JoinColumn } from "typeorm";
import { Game } from "./Game";
import { GroupMember } from "./GroupMember";

@Entity()
export class Group {
  @PrimaryGeneratedColumn()
  group_id!: number;

  @Column()
  group_name!: string;

  @Column({ type: "timestamp", default: () => "CURRENT_TIMESTAMP" })
  created_at!: Date;

  @Column()
  max_players!: number;

  @ManyToOne(() => Game, (game) => game.groups)
  @JoinColumn({ name: "game_id" })
  game!: Game;

  @OneToMany(() => GroupMember, (groupMember) => groupMember.group)
  members!: GroupMember[];
}