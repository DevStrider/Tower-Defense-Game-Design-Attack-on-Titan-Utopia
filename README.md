# Attack on Titan: Utopia

A Java + JavaFX tower-defense game inspired by *Attack on Titan*. Players defend the wall of a lane-based map against waves of approaching titans by purchasing and placing weapons, managing resources, and surviving as many turns as possible.

This project was developed across three milestones for an Object-Oriented Programming course (see `Milestone 1.pdf`, `Milestone 2.pdf`, and `MileStone 3.pdf`).

---

## Gameplay Overview

- **Goal:** Protect the wall(s) from advancing titans for as long as possible and rack up the highest score.
- **Resources** are earned each turn and by defeating titans. They are spent on weapons.
- **Lanes** each contain a wall with health. When a wall's health drops to 0, the lane is lost. The game ends when every lane is destroyed.
- **Battle phases** (Early, Intense, Grumbling) progressively spawn tougher titans and more of them per turn.

### Titan Types
Loaded from `titans.csv`:
1. **Pure Titan** — balanced baseline enemy.
2. **Abnormal Titan** — fast, dangerous in numbers.
3. **Armored Titan** — high health, tough to break.
4. **Colossal Titan** — massive HP, heavy hitter.

### Weapon Types
Loaded from `weapons.csv` and built through a factory:
1. **Anti-Titan Shell (Piercing Cannon)** — single-target.
2. **Long-Range Spear (Sniper Cannon)** — targets the farthest titan.
3. **Wall-Spread Cannon (Volley Spread)** — area damage.
4. **Proximity Trap (Wall Trap)** — passive trap that activates on contact.

---

## Project Structure

```
.
├── src/game/
│   ├── engine/               # Core game logic
│   │   ├── Battle.java       # Main battle state machine
│   │   ├── BattlePhase.java
│   │   ├── base/Wall.java
│   │   ├── dataloader/       # CSV loading
│   │   ├── exceptions/       # Custom checked exceptions
│   │   ├── interfaces/       # Attacker / Attackee / Mobil contracts
│   │   ├── lanes/Lane.java
│   │   ├── titans/           # Titan hierarchy + registry
│   │   └── weapons/          # Weapon hierarchy + factory
│   ├── gui/                  # JavaFX UI layer (MVC)
│   │   ├── GameApp.java      # Application entry point
│   │   ├── GameConstants.java
│   │   ├── GameSettings.java
│   │   ├── controllers/      # Start / Battle / Settings controllers
│   │   └── views/            # Start / Battle / Lane / Titan / Settings / GameOver
│   └── tests/                # Milestone 1 & 2 JUnit tests
├── bin/                      # Compiled output (mirrors src)
├── titans.csv                # Titan stats
├── weapons.csv               # Weapon stats
├── Milestone 1.pdf           # Engine spec
├── Milestone 2.pdf           # Extended engine spec
└── MileStone 3.pdf           # GUI spec
```

---

## Architecture Notes

- **MVC-style GUI:** Each screen has a paired `View` (JavaFX node tree) and `Controller` (event wiring + game-state hookup). `GameApp` swaps scenes on the primary stage.
- **Factory pattern** for weapon construction (`WeaponFactory` + `FactoryResponse`).
- **Registry pattern** for titan and weapon templates loaded once from CSV (`TitanRegistry`, `WeaponRegistry`).
- **Priority queue of lanes** in `Battle` so the most-threatened lane is always processed first.
- **Phased spawning** controlled by `PHASES_APPROACHING_TITANS` in `Battle.java`.

---

## Running the Game

See [`HOW_TO_RUN.md`](./HOW_TO_RUN.md) for step-by-step build & run instructions (JavaFX setup, IDE configuration, and command-line execution).

---

## Course Info

- **Course:** CSEN 202 / OOP — Tower Defense Game Project
- **Milestones:** Engine (M1), Extended engine + tests (M2), GUI (M3)
- **Authors:** Abdelrahman Walid Mansour, Mostafa Ahmed Rashwan
