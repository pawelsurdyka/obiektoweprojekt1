# Evolution Simulator

Project for AGH course Object-oriented programming. A simulation of a dynamically evolving ecosystem where herbivorous animals move, eat, reproduce, and pass on their genes. Inspired by *Land of Lisp* by Conrad Barski and the evolutionary algorithms from *Genetic Algorithms in Search, Optimization, and Machine Learning* by David E. Goldberg.

## 🧠 Concept

- Animals live on a rectangular map divided into two zones: grassland and jungle.
- They move based on a genome, rotate, eat plants, and reproduce.
- Genes are passed on with crossover and mutation.
- The ecosystem evolves based on natural selection: survival and reproduction of the fittest.

## 🌍 Map Variants

- **Earth Globe**: horizontal edges wrap around, vertical edges are blocked.
- **Hell Portal**: crossing any edge teleports the animal randomly and drains energy.

## 🌱 Plant Growth Strategies

- **Equator Forest**: more plants grow near the equator (center horizontal band).
- **Toxic Corpses**: areas with fewer deaths get more plants.

## 🧬 Mutation Types

- **Random**: gene mutates to any possible value.
- **Slight Change**: gene value increases or decreases by 1.

## 🦓 Animal Behavior

- **Deterministic**: follows gene sequence exactly.
- **Slightly Chaotic**: 80% chance to follow next gene, 20% to pick a random one.

## 🔄 Simulation Loop

Each simulation "day" consists of:
1. Removal of dead animals.
2. Movement based on genome.
3. Eating plants (if present on tile).
4. Reproduction if energy allows.
5. Plant regrowth.

## 🎮 User Interface (JavaFX)

- Multiple simulation windows possible.
- Real-time visualization of animals and plants.
- Pause/resume individual simulations.
- Live statistics:
  - Total animals and plants
  - Number of free tiles
  - Most common genotypes
  - Average energy and lifespan
- **CSV Export** of daily statistics.
- Track a selected animal:
  - Genome
  - Energy
  - Age
  - Number of children
  - Food consumed
  - Day of death (if applicable)
- Highlight animals with the **dominant genotype**.

## ⚙️ Technologies

- Java 17
- JavaFX
- Gradle
- Object-oriented architecture
- MVC-inspired structure
