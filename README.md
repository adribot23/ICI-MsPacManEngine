# ICI - Intelligent Behaviors Engineering: Ms. Pac-Man

Academic project for the UCM course **ICI (Ingeniería de Comportamientos Inteligentes - Intelligent Behaviors Engineering)**.

This repository contains multiple **intelligent agent programming** practices to control Pac-Man and ghosts in the Ms. Pac-Man game, using advanced AI techniques such as **FSM (Finite State Machines)**, **CBR (Case-Based Reasoning)**, **Fuzzy Logic** and **Rule Engines**.

## Overview

The project implements progressive intelligent behaviors for Pac-Man and ghosts:

- **Practices 0-1**: Basic behaviors and simple state machines
- **Practices 2-3**: Complex FSM with transitions and strategic actions
- **Practices 4-5**: CBR with case memory, Fuzzy Logic and Rule Engines

All practices run on the **MsPacManEngine**, a robust Maven-based game engine.

## Technologies & Techniques

### Project Core

- **Java 8+**
- **Maven** for build and dependencies
- **MsPacManEngine**: Base engine with Swing visualization

### Implemented AI Techniques

| Technique | Description |
|-----------|-------------|
| **FSM (Finite State Machine)** | State machines with conditional transitions to control behaviors |
| **CBR (Case-Based Reasoning)** | Case-based reasoning using jCOLIBRI to reuse previous solutions |
| **Fuzzy Logic** | Fuzzy logic for decision-making in uncertain environments |
| **Rules Engine** | Rule engine with Jess for fact and rule-based logic execution |

### Main Dependencies

- `jCOLIBRI 3.2`: CBR Engine
- `GraphStream 2.0`: Graph visualization (FSM, dependencies)
- `SLF4J`: Logging
- `Jess`: Rule engine

## Project Structure

```text
ICI-MsPacManEngine/
├── MsPacManEngine/
│   ├── src/main/java/
│   │   ├── pacman/
│   │   │   ├── game/         # Game logic
│   │   │   ├── Executor.java # Main executor
│   │   │   └── controllers/  # Base controllers
│   │   └── es/ucm/fdi/ici/
│   │       ├── fsm/          # State Machines
│   │       ├── cbr/          # Case-Based Reasoning
│   │       ├── fuzzy/        # Fuzzy Logic
│   │       ├── rules/        # Rule Engine
│   │       ├── Action.java
│   │       └── Input.java
│   └── pom.xml
├── Practica0/  # Basic behaviors
├── Practica1/  # Simple states
├── Practica2/  # FSM with transitions
├── Practica3/  # Advanced FSM with Rete
├── Practica4/  # CBR - Case-Based Reasoning
├── Practica5/  # Fuzzy Logic
├── ICI-Evaluation/  # Practice evaluator
└── PacMan.pdf       # Game specification
```

## Running the Project

### Prerequisites

- **JDK 8 or higher**
- **Maven 3.6+**
- Eclipse IDE or equivalent (optional)

### Build the Engine

```bash
cd MsPacManEngine
mvn clean install
```

### Run a Practice

Each practice contains an `ExecutorTest.java` that allows you to run the controller against the engine.

```bash
cd Practica2
javac -cp ../MsPacManEngine/target/MsPacManEngine-2.0.0.jar src/**/*.java
java -cp ../MsPacManEngine/target/MsPacManEngine-2.0.0.jar:. ExecutorTest
```

Alternatively, open the project in Eclipse:

1. Import > Existing Projects into Workspace
2. Select the root folder
3. Right-click on `ExecutorTest.java` > Run As > Java Application

### Automatic Evaluator

The `ICI-Evaluation` folder contains tools to automatically evaluate all practices:

```bash
cd ICI-Evaluation
java -cp ../MsPacManEngine/target/MsPacManEngine-2.0.0.jar:. Evaluate
```

## Practice Details

### Practice 0: Basic Behaviors

Implements simple behaviors (random, flee, chase) for Pac-Man and ghosts.

- **Key files**: `MsPacManRandom.java`, `GhostsRandom.java`, `MsPacManRunAway.java`, `GhostsAggresive.java`
- **Concept**: Introduction to basic controllers

### Practice 1: Simple States

Introduces simple state machines with states for different behaviors.

- **Key files**: `GhostState.java`, `Blinky.java`, `Pinky.java`, `Inky.java`, `Sue.java`
- **Concept**: Classic Ms. Pac-Man game patterns

### Practice 2: Complex FSM

State machine with multiple conditional transitions:

- **Pac-Man States**: Eat pellets, eat power pellets, chase ghosts, flee
- **Ghost States**: Chase, scatter, frightened, return
- **Transitions**: Based on distances, power state, etc.

- **Key files**: `mspacman/transitions/*.java`, `mspacman/actions/*.java`, `MsPacMan.java`, `Ghosts.java`
- **Concept**: State machines with advanced conditional logic

### Practice 3: FSM with Rete and Rules

Extension of Practice 2 with Rete (inference network) to optimize transitions.

- **Key files**: `Retetest.java`, similar to Practice 2
- **Concept**: State machine optimization with inference networks

### Practice 4: CBR (Case-Based Reasoning)

Case-based reasoning using jCOLIBRI:

- **Components**:
  - `MsPacManCBRengine.java`: Main CBR engine
  - `MsPacManDescription.java`: Case description
  - `MsPacManInput.java`: Input variables
  - `MsPacManResult.java`: Case result
  - Custom similarity functions

- **Flow**: Retrieve similar cases → Adapt → Learn
- **Concept**: Reuse previous experiences to make decisions

### Practice 5: Fuzzy Logic and Memory

Fuzzy logic for reasoning in uncertain environments:

- **Components**:
  - `MsPacManFuzzyMemory.java`: State memory
  - `MaxActionSelector.java`: Action selector
  - Fuzzy rules for decisions

- **Concept**: Combine Fuzzy Logic with case memory

## Key Interfaces and Classes

### Controllers

- `PacmanController`: Interface to control Pac-Man
- `GhostController` / `IndividualGhostController`: Interface to control ghosts
- `MASController`: Multi-agent controller

### Input and Action

- `Input`: Sensory information abstraction
- `Action`: Executable actions abstraction

### FSM

- `FSM`: Finite State Machine
- `State`: Individual states
- `Transition`: Conditional transitions
- `FSMObserver`: State change observer

### CBR

- `PacManCBRController`: CBR-based controller

### Fuzzy Logic

- `FuzzyEngine`: Fuzzy logic engine
- `FuzzyInput`: Fuzzy variables
- `ActionSelector`: Fuzzy action selector

### Rule Engine

- `RuleEngine`: Rule engine with Jess
- `RulesInput`: Engine input
- `RulesAction`: Actions fired by rules

## Observers and Visualization

Each technique includes observers to visualize its behavior:

- `ConsoleFSMObserver`: Prints FSM transitions
- `GraphFSMObserver`: Visualizes FSM as graph
- `ConsoleFuzzyEngineObserver`: Prints fuzzy decisions
- `ConsoleRuleEngineObserver`: Prints fired rules

## Evaluation

The evaluator measures:

- **Score**: Game score
- **Game Time**: Total duration
- **Pellets Eaten**: Number of pellets consumed
- **Ghosts Captured**: Ghosts eaten with power pellets
- **Deaths**: Times Pac-Man is captured

## Important Notes

1. MsPacManEngine is licensed under **GNU GPL V3+**
2. Each practice is an independent Java project that depends on the engine
3. Controllers must implement `PacmanController` or `GhostController`
4. The executor runs the game in real-time; speed can be controlled
5. Cases and rules are customized per practice

## Additional Resources

- `MsPacManEngine/src/main/resources/data/mazes/`: Game maps (a.txt, b.txt, c.txt, d.txt)
- `PacMan.pdf`: Complete game specification
- Javadoc available at `MsPacManEngine/target/site/apidocs/`

## Academic Note

Repository developed for educational purposes for the **ICI (Intelligent Behaviors Engineering)** course at UCM, under the supervision of the GAIA group.
